package com.background;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import com.customexception.AppException;

public class RobotProperty {
	private final File PROPERTYFILE = new File("./robot.properties");

	private static Properties configuration = null;
	private static RobotProperty property = null;

	private RobotProperty() {
		configurationInit();
	}

	private void configurationInit() {
		FileOutputStream fOutputStream = null;
		FileInputStream fInputStream = null;
		BufferedInputStream bufInputStream = null;

		configuration = new Properties();
		try {

			// 当robot.properties文件不存在时，该默认配置默认使用sqlserver数据库配置
			if (!PROPERTYFILE.exists()) {
				fOutputStream = new FileOutputStream(PROPERTYFILE);
				configuration.setProperty("DBDriver",
						"com.microsoft.sqlserver.jdbc.SQLServerDriver");
				configuration.setProperty("DBUrl",
						"jdbc:sqlserver://localhost:1433;databaseName=DBname");
				configuration.setProperty("DBUsername", "");
				configuration.setProperty("DBPassword", "");

				// 定义 程序运行后多长时间开始启动增量索引工作
				configuration.setProperty("delay", "1000");

				// 定义 增量索引的周期
				configuration.setProperty("period", "5000");
				configuration
						.store(fOutputStream,
								"set your DataBase infomation\ndelay:The begin time of making delta index.\nperiod:the period time of making delta index.");
			}
			fInputStream = new FileInputStream(PROPERTYFILE);
			bufInputStream = new BufferedInputStream(fInputStream);
			configuration.load(bufInputStream);
		} catch (FileNotFoundException e) {
			throw new AppException("[ExceptionInfo]没有找到" + PROPERTYFILE
					+ "这个文件。", e);
		} catch (IOException e) {
			throw new AppException("[ExceptionInfo]在加载配置文件的时候出现IO错误。", e);
		} finally {
			if (bufInputStream != null) {
				try {
					bufInputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (fInputStream != null) {
				try {
					fInputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (fOutputStream != null) {
				try {
					fOutputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public static Properties getConfiguration() {
		if (property == null) {
			property = new RobotProperty();
		}
		return configuration;
	}
}
