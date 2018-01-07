package com.background;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import com.customexception.AppException;
import com.util.Util;

/**
 * 全局只需要一个连接，所以把他设置成单例模式。
 * 
 * @author xiaolong
 * 
 */
public class DBOperation {
	private Connection connection = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
	private ResultSetMetaData rsmd = null;

	private String dbDriver = null;
	private String dbUrl = null;
	private String dbUsername = null;
	private String dbPassword = null;

	private static DBOperation dbOperation = null;

	/**
	 * 单例
	 */
	private DBOperation() {
		dbDriver = RobotProperty.getConfiguration().getProperty("DBDriver");
		dbUrl = RobotProperty.getConfiguration().getProperty("DBUrl");
		dbUsername = RobotProperty.getConfiguration().getProperty("DBUsername");
		dbPassword = RobotProperty.getConfiguration().getProperty("DBPassword");
	}

	/**
	 * 连接数据库
	 */
	public void linkDataBase() {
		try {
			Class.forName(dbDriver);
		} catch (ClassNotFoundException e) {
			String message = "[ExceptionInfo]没有找数据库驱动类"
					+ "。请检查配置是否正确或是否存在该驱动程序。";
			throw new AppException(message, e);
		}
		try {
			connection = DriverManager.getConnection(dbUrl, dbUsername,
					dbPassword);
		} catch (SQLException e) {
			String message = "[ExceptionInfo]无法获取该数据库的连接，请检查DBUrl, DBUsername, DBPassword配置是否正确。";

		}
		try {
			statement = connection.createStatement();
		} catch (SQLException e) {
			String message = "[ExceptionInfo]Statement创建失败。";
			throw new AppException(message, e);
		}
	}

	/**
	 * 关闭连接
	 */
	public void close() {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				String message = "[ExceptionInfo]ResultSet关闭失败。";
				throw new AppException(message, e);
			}
		}
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				String message = "[ExceptionInfo]Statement关闭失败。";
				throw new AppException(message, e);
			}
		}
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				String message = "[ExceptionInfo]数据库连接关闭失败。";
				throw new AppException(message, e);
			}
		}
	}

	/**
	 * 根据sql语句从数据库查询结果集
	 * 
	 * @param sql
	 * @return
	 * @throws AppException
	 */
	public ResultSet getResultSet(String sql) throws AppException {
		if (connection == null)
			throw new AppException("[ExceptionInfo]数据库未连接。");
		try {
			resultSet = statement.executeQuery(sql);
			setResultSetMetaData();
		} catch (SQLException e) {
			String message = "[ExceptionInfo]无法通过指定sql得到查询结果集，请检查sql语句是否书写正确。";
			throw new AppException(message, e);
		}
		return resultSet;
	}

	// /**
	// * 得到数据集的行数
	// *
	// * @return
	// */
	// public int getResultSetNum(String sql) {
	// int rowNum = 0;
	// ResultSet rs = getResultSet(sql);
	// try {
	// rs.last();
	// rowNum = rs.getRow();
	// rs.beforeFirst();
	// } catch (SQLException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return rowNum;
	// }

	private void setResultSetMetaData() {
		try {
			rsmd = resultSet.getMetaData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getColumnName(int colmnNum) {
		if (rsmd == null)
			return null;
		String columnName = "";
		try {
			columnName = new String(rsmd.getColumnName(colmnNum).getBytes(
					Util.ENCODING));
		} catch (UnsupportedEncodingException e) {
			throw new AppException("[ExceptionInfo]" + Util.ENCODING
					+ "编码方式不被支持。", e);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return columnName;
	}

	public String getColumnValue(int colmnNum) {
		String columnValue = "";
		try {
			columnValue = resultSet.getString(colmnNum);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return columnValue;
	}

	/**
	 * 获取实例
	 * 
	 * @return
	 */
	public static DBOperation getInstance() {
		if (dbOperation == null) {
			dbOperation = new DBOperation();
		}
		return dbOperation;
	}

	// public static void main(String[] args) {
	// String sql =
	// "select * from ResponseTable where lastModifyTime >'2012/09/16 15:53:58' and isDeleted=0 and datalength(replay)!=0";
	// DBOperation db = DBOperation.getInstance();
	// db.linkDataBase();
	// ResultSet rs = db.getResultSet(sql);
	// System.out.println(db.getResultSetNum(sql));
	// db.close();
	// }
}
