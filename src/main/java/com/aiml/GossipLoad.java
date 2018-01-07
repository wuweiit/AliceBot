package com.aiml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.customexception.AppException;

public class GossipLoad {
	private String gossipPath = "./gossip.txt";// 数据源文件
	private String destination = "./Corpus/Chinese/gossip.xml";// 目标文件
	private InputStream inputStream = null;
	private BufferedReader bufReader = null;
	private OutputStream outputStream = null;

	/**
	 * 构造一个xml
	 * 
	 * @throws IOException
	 */
	public void load() throws IOException {
		String line;
		init();
		StringBuffer stringBuf = new StringBuffer();
		stringBuf
				.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<aiml>\n");
		// 读取gossip.txt文件内容
		while ((line = bufReader.readLine()) != null) {
			stringBuf.append(analyzing(line) + "\n");
		}
		stringBuf.append("</aiml>");
		outputStream.write(stringBuf.toString().getBytes());
	}

	/**
	 * 构造一个category
	 * 
	 * @param line
	 * @return
	 */
	private String analyzing(String line) {
		String[] pattern = line.split(":");
		return "<category><pattern>" + pattern[0] + "</pattern><template>"
				+ pattern[1] + "</template></category>";
	}

	private void init() throws AppException {
		if (bufReader == null) {
			File gossipFile = new File(gossipPath);
			try {
				inputStream(new FileInputStream(gossipFile));
				bufferedReader(this.inputStream);
				outputStream(new FileOutputStream(destination));
			} catch (FileNotFoundException e) {
				throw new AppException("[ExceptionInfo]./gossip.txt文件不存在。", e);
			}
		}
	}

	private void outputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	private void bufferedReader(InputStream inputStream) {
		this.bufReader = new BufferedReader(new InputStreamReader(inputStream));
	}

	private void inputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public void clean() { // 这里应该对每个关闭单独使用try catch块，保证关闭资源的引发的异常不会影响其他资源的关闭。

		if (outputStream != null) {
			try {
				outputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (bufReader != null) {
			try {
				bufReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		GossipLoad gossip = new GossipLoad();
		try {
			gossip.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
