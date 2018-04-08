package com.aiml;

import com.background.DBOperation;
import com.customexception.AppException;
import com.util.Util;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.index.*;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;

import java.io.IOException;
import java.nio.file.Paths;

public class IndexResource {
	public DBOperation dbObject = null;
	public IndexWriter indexWriter = null;
	public IndexReader indexReader = null;
	public IndexSearcher indexSearcher = null;

	private static IndexResource resource = null;

	private IndexResource() {
		init();
	}

    /**
     * 初始化
     */
	private void init() {
		dbObject = DBOperation.getInstance();
		dbObject.linkDataBase();
        buildIndex();
		indexReader = buildIndexReader();
		indexSearcher = buildIndexSearcher(indexReader);
	}


    /**
     * 构建索引
     */
    public void buildIndex() {
        try {

            Directory directory = FSDirectory.open(Paths.get(Util.INDEXFILE_PATH) );


            PerFieldAnalyzerWrapper wrapper = new PerFieldAnalyzerWrapper(new SmartChineseAnalyzer());
            //开始索引
            IndexWriterConfig indexConfig = new IndexWriterConfig(wrapper);

            indexWriter = new IndexWriter(directory,
                    indexConfig);

            indexWriter.commit();
            indexWriter.close();

        } catch (CorruptIndexException e) {
            throw new AppException(e);
        } catch (LockObtainFailedException e) {
            throw new AppException(e);
        } catch (IOException e) {
            throw new AppException("[ExceptionInfo]在创建IndexWriter的时候出现了IO错误。",
                    e);
        }
    }



	public IndexWriter getIndexWriterByMode(OpenMode openMode) {
		try {

		 	Directory directory = FSDirectory.open(Paths.get(Util.INDEXFILE_PATH) );


            PerFieldAnalyzerWrapper wrapper = new PerFieldAnalyzerWrapper(new SmartChineseAnalyzer());
            //开始索引
            IndexWriterConfig indexConfig = new IndexWriterConfig(wrapper);
            indexConfig.setOpenMode(openMode);




            indexWriter = new IndexWriter(directory,
					indexConfig);
		} catch (CorruptIndexException e) {
			throw new AppException(e);
		} catch (LockObtainFailedException e) {
			throw new AppException(e);
		} catch (IOException e) {
			throw new AppException("[ExceptionInfo]在创建IndexWriter的时候出现了IO错误。",
					e);
		}
		return indexWriter;
	}



	public IndexReader buildIndexReader() {
		IndexReader reader = null;
		try {

            Directory directory = FSDirectory.open(Paths.get(Util.INDEXFILE_PATH) );



            DirectoryReader ireader = DirectoryReader.open(directory);

			reader = ireader;
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return reader;
	}

	public IndexSearcher buildIndexSearcher(IndexReader reader) {
		IndexSearcher searcher = new IndexSearcher(reader);
		return searcher;
	}

	public void clean() { // 没有关闭他们
		if (dbObject != null) {
			dbObject.close();
		}

		if (indexWriter != null) {
			try {
				indexWriter.close();
			} catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (indexReader != null) {
			try {
				indexReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static IndexResource getInstance() {
		if (resource == null)
			resource = new IndexResource();
		return resource;
	}

}
