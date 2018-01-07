package com.aiml;

import java.util.List;

import org.apache.lucene.document.Document;

public class AskToDB implements IAskApproach {
	private IndexSearchService searchService = null;

	public AskToDB() {
		searchService = new IndexSearchService();
		searchService.indexTaskSetup();
	}

	public String response(String input) {
		// StringBuffer sb = new StringBuffer(""); // 这里每次都new，肯定有问题！！！
		List<Document> list = null;
		try {
			list = searchService.search(input);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// for (Document doc : list) {
		// sb.append("[问题]-->").append(doc.get("question")).append("||")
		// .append("[答案]-->").append(doc.get("replay"));
		// sb.append("\n");
		// }
		if (list.size() > 0)
			return list.get(0).get("replay");
		else
			return "";
	}
}
