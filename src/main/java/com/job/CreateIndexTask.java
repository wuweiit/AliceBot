package com.job;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;

import com.aiml.IndexResource;
import com.background.RobotProperty;
import com.customexception.AppException;
import com.util.Util;

public class CreateIndexTask {
	private String fullIndexSql = "";
	private String deltaIndexSql = "";
	private IndexResource indexResource = null;

	public CreateIndexTask(IndexResource resource) {
		buildQuery();
		this.indexResource = resource;
	}

	public void buildQuery() {
		// 全量索引的sql语句
		fullIndexSql = "select * from ResponseTable where isDeleted=0 and LENGTH(replay)!=0";
		// 增量索引的sql语句
		deltaIndexSql = "select * from ResponseTable where lastModifyTime >'"
				+ Util.getTimestamp()
				+ "' and isDeleted=0 and LENGTH(replay)!=0";
	}

	/**
	 * 全量索引
	 */
	public void fullIndexSetup() {
		IndexOperation(fullIndexSql, OpenMode.CREATE);
	}

	/**
	 * 增量索引
	 */
	public void deltaIndexSetup() {
		Timer timerIndexMake = new Timer();
		timerIndexMake.schedule(
				new makeIndexTask(),
				Integer.parseInt(RobotProperty.getConfiguration().getProperty(
						"delay")),
				Integer.parseInt(RobotProperty.getConfiguration().getProperty(
						"period")));
	}



	public class makeIndexTask extends TimerTask {

		@Override
		public void run() {
			IndexOperation(deltaIndexSql, OpenMode.APPEND);
		}
	}

	/**
	 * openMode会有2中模式： 1.全量索引模式 2.增量索引模式
	 * 
	 * @param sql
	 * @param openMode
	 */
	public void IndexOperation(String sql, OpenMode openMode) {
		indexResource.dbObject.linkDataBase(); // 连接数据库
		IndexWriter writer = null;
		ResultSet rs = indexResource.dbObject.getResultSet(sql);// 根据sql得到结果集
		if (!Util.INDEXFILE.exists()) {// 判断index索引文件是否存在
			Util.INDEXFILE.mkdir();
		}

		try {
			// 根据模式得到相应的写索引的对象
			writer = indexResource.getIndexWriterByMode(openMode);
			while (rs.next()) {
				Document doc = new Document();




				// 得到数据库中copyfield字段名称
				// 得到copyfield字段的值
				String columnValue = indexResource.dbObject.getColumnValue(8);
				String replayValue = indexResource.dbObject.getColumnValue(6);
				String id = indexResource.dbObject.getColumnValue(1);


				// 在document中加入该field
				doc.add(new TextField("copyfield", columnValue, Field.Store.YES));
				doc.add(new TextField("replay", replayValue, Field.Store.YES));
				doc.add(new TextField("id", id, Field.Store.YES));

				// 将该document（也就是数据库中一个数据项）写入索引中
				indexDoc(writer, doc, openMode);
			}
			// 记录当前索引行为的时间
			Util.setTimestamp();
		} catch (SQLException e) {
			throw new AppException(e);
		} finally {
			if (indexResource.dbObject != null) {
				indexResource.dbObject.close();
			}
			if (writer != null) {
				try {
					writer.close();
				} catch (CorruptIndexException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void indexDoc(IndexWriter writer, Document doc, OpenMode openMode) {


		try {
			// 更新索引，他的工作原理就是先将索引文件中该id的数据删除掉，然后在将数据库中该id的数据索引一遍
			if (openMode == OpenMode.APPEND) {
				writer.updateDocument(new Term("id", doc.get("id")), doc);

			} else if (openMode == OpenMode.CREATE) {// 创建索引
				writer.addDocument(doc);
			}
		} catch (CorruptIndexException e) {
			throw new AppException(e);
		} catch (IOException e) {
			throw new AppException(e);
		}
	}
}
