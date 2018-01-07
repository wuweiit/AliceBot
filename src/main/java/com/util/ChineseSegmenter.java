package com.util;
/**
 * Created by marker on 2018/1/6.
 */

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.io.*;

/**
 *
 * 中文分词
 *
 *
 * @author marker
 * @create 2018-01-06 下午5:08
 **/
public class ChineseSegmenter {


    public static String analysis(String str) {

        if(str.getBytes().length == str.length()) {
            //如果不包含中文，就直接返回。
            return str;
        }else {
            //由于IK分词器，不支持特殊字符，所以将 * 改为中文字符“这是星号”,中文分词以后再将“这是星号”修正为为 *
            //同理将 _改为中文字符串“这是下划线”，中文分词以后再将“这是下划线”修正为 _
//            str= str.replaceAll("\\*","这是星号").replaceAll("_","这是下划线");
        }

        StringBuffer sb =new StringBuffer();
        byte[] bt =str.getBytes();
        InputStream ip = new ByteArrayInputStream(bt);
        Reader read = new InputStreamReader(ip);


//        System.out.println(ToAnalysis.parse(str));

        ToAnalysis toAnalysis = new ToAnalysis(read);

        try{
            while(true){
                Term term = toAnalysis.next();
                if(term == null){
                    break;
                }
                // 在每个分词元之后添加空格
                sb.append(term.toString() + " ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        sb.delete(sb.length() - 1, sb.length());

        return sb.toString().replaceAll("这是星号","*").replaceAll("这是下划线","_");
    }

}
