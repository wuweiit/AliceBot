package com.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


import com.customexception.AppException;

public class Util {

	// 数据库数据索引存放位置
	public static final File INDEXFILE = new File("./Index");
	public static final String INDEXFILE_PATH = "./Index";

	// 每次索引的时间戳文件存放位置
	public static final File TIMESTAMPFILE = new File("./timestamp.txt");

	// 编码
	public static final String ENCODING = "GBK";

	// 需要被索引的数据库字段
	public static final String FIELDSNAME = "copyfield";


	/**
	 * 用来产生时间戳文件，该文件记录索引最近更新时间
	 */
	public static void setTimestamp() {
		String correntTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
				.format(new Date());

		FileWriter fileWriter = null;

		try {
			if (!TIMESTAMPFILE.exists()) {
				TIMESTAMPFILE.createNewFile();
			}
			fileWriter = new FileWriter(TIMESTAMPFILE);
			fileWriter.write(correntTime);
		} catch (IOException e) {
			throw new AppException("[ExceptionInfo]在写时间戳的时候出现IO错误。", e);
		} finally {
			try {
				if (fileWriter != null)
					fileWriter.close();
			} catch (IOException e) {
				throw new AppException("[ExceptionInfo]在写时间戳结束后关闭数据流时出现错误。", e);
			}
		}
	}

	/**
	 * 读取时间戳文件。
	 * 
	 * @return
	 */
	public static String getTimestamp() {
		BufferedReader bufReader = null;
		FileReader fileReader = null;
		String timestamp = "";
		try {
			if (!TIMESTAMPFILE.exists()) {

				TIMESTAMPFILE.createNewFile();
				FileWriter fileWriter = new FileWriter(TIMESTAMPFILE);
				fileWriter.write("1990/01/01 00:00:00");
				fileWriter.close();
			}
			fileReader = new FileReader(TIMESTAMPFILE);
			bufReader = new BufferedReader(fileReader);
			timestamp = bufReader.readLine();

		} catch (IOException e) {
			throw new AppException("[ExceptionInfo]读时间戳文件或创建时间戳文件出现IO错误。", e);
		} finally {
			if (fileReader != null) {
				try {
					fileReader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return timestamp;
	}
}
