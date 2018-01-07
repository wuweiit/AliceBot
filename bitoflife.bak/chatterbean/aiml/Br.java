package bitoflife.chatterbean.aiml;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import org.xml.sax.Attributes;

import bitoflife.chatterbean.AliceBot;
import bitoflife.chatterbean.Context;
import bitoflife.chatterbean.Match;
import bitoflife.chatterbean.Graphmaster;

public class Br extends TemplateElement
{
  /*
  Constructors
  */

  public Br(Attributes attributes)
  {
  }

  public Br(Object... children)
  {
    super(children);
  }

  /*
  Methods
  */

  public String process(Match match)
  {
    return "";
  }
}