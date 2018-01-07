package com.context;

import com.aiml.AskToAIML;
import com.aiml.AskToDB;
import com.background.RobotProperty;

public class ChartManager {
    private static final AskToAIML askToAIML = new AskToAIML();
    private static AskToDB askToDB = null;
    private static ChartContext chartContext = null;
    private static ChartManager instance = null;

    /**
     * 单例模式
     */
    private ChartManager() {

        if (Boolean.valueOf((String) RobotProperty.getConfiguration().get("DBAvailable"))) {
            askToDB = new AskToDB();
        }
        chartContext = new ChartContext(askToAIML, askToDB);
    }

    public static ChartManager getInstance() {
        if (instance == null) {
//            synchronized (ChartManager.class) { // 这个什么意思？？？
                instance = new ChartManager();
//            }
        }
        return instance;
    }

    public String response(String input) {
        return chartContext.response(input);
    }

}
