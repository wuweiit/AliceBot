package com.context;

import com.util.Translate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;



/**
 * 聊天机器人入口
 */
public class Robot {

	private ChartManager chartManager = null;

	public Robot() {
		chartManager = ChartManager.getInstance();
	}




	public String input(String input) {
		return chartManager.response(input);
	}




	public static void main(String[] args) throws IOException {
		Robot demo = new Robot();
		Scanner scanner = new Scanner(System.in);

		System.out.println("Alice 已经启动，可以和他对话了");

		String input;
		while ((input = scanner.nextLine()) != null) {
			System.out.println("you say>" + input);
			System.out.println("Alice>" + demo.input(input));
		}
	}


}
