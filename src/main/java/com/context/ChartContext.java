package com.context;

import com.aiml.AskToAIML;
import com.aiml.AskToDB;
import com.aiml.IAskApproach;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 聊天上下文
 *
 */
public class ChartContext implements IAskApproach {

    private IAskApproach askToAIML = null;

	private IAskApproach askToDB = null;

	private final String NULLSIGN = "#"; // 这个标志是用来表示，当查询AIML的时候，匹配到了*。

    private final String USEFULSIGN = "$";// 这个标志是用来表示，当查询AIML的时候匹配到了专业问题的模式。

	private final String[] NULLREPLAY = {
        "对不起，我还不能回答您的这个问题。",
        "唔，主人还没有教会我这个问题呢。",
        "我暂时还回答不了这个问题呢？",
        "我好像不明白。",
	};

	public ChartContext(AskToAIML askToAIML, AskToDB askToDB) {
		this.askToAIML = askToAIML;
		this.askToDB = askToDB;
	}

	/**
	 * 联合AIML和数据库这2个知识库。
	 */
	public String response(String input) {
		String responseFromAIML = askToAIML.response(input);
		// 替换文本中的空格字符串
        // 判断是中文还是英文,中文需要去掉空格。
//        if(isContainChinese(responseFromAIML)){
//            responseFromAIML = responseFromAIML.replace(" ", "");
//        }
		return translate(input, responseFromAIML);
	}



    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }


	/**
	 * 从数据库中查询
     * （处理无法回复的回复）
     *
	 * @param originInput 原来输入的内容
	 * @param aimlReplay 回答的内容
	 * @return
	 */
	private String translate(String originInput, String aimlReplay) {
		String asDBInput = "";
		if (-1 != aimlReplay.indexOf(NULLSIGN)) {
			asDBInput = originInput;
		} else if (-1 != aimlReplay.indexOf(USEFULSIGN)) {
			asDBInput = aimlReplay.replaceAll(USEFULSIGN, "");
		} else
			return aimlReplay;


        aimlReplay = aimlReplay.replaceFirst("#","");


		String dbReplay = askToDB.response(aimlReplay);
		if (0 == dbReplay.length()) {
			return getRandomResponse();
		} else
			return dbReplay;
	}

    /**
     * 随机回复
     * @return
     */
	private String getRandomResponse() {
		return NULLREPLAY[getRandomNum()];
	}

	private int getRandomNum() {
		return (int) (Math.random() * NULLREPLAY.length);
	}
}
