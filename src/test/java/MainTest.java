/**
 * Created by marker on 2018/1/6.
 */

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.BaseAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.junit.Test;

import java.io.*;

/**
 * @author marker
 * @create 2018-01-06 下午6:45
 **/
public class MainTest {


    @Test
    public void test(){
        String str = "欢迎使用ansj_seg,(ansj中文分词)在这里如果你遇到什么问题都可以联系我.我一定尽我所能.帮助大家.ansj_seg更快,更准,更自由!" ;
        System.out.println(ToAnalysis.parse(str));
    }


    @Test
    public void test1() throws IOException {

        String str = "欢迎使用ansj_seg,(ansj中文分词)在这里如果你遇到什么问题都可以联系我.我一定尽我所能.帮助大家.ansj_seg更快,更准,更自由!" ;

        byte[] bt = str.getBytes();
        InputStream ip = new ByteArrayInputStream(bt);
        Reader read = new InputStreamReader(ip);

        ToAnalysis toAnalysis = new ToAnalysis(read);


        while(true){
            Term term = toAnalysis.next();
            if(term == null){
                break;
            }

            System.out.println(  term);
        }

    }

    @Test
    public void test3(){
        String value = " 张 ";
        value = value.replaceAll("^[^A-Za-z0-9\\u4e00-\\u9fa5]+|[^A-Za-z0-9\\u4e00-\\u9fa5]+$", " ");

        System.out.println(value);
    }
}
