/*
Copyleft (C) 2005 Hélio Perroni Filho
xperroni@yahoo.com
ICQ: 2490863

This file is part of ChatterBean.

ChatterBean is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

ChatterBean is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with ChatterBean (look at the Documents/ directory); if not, either write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA, or visit (http://www.gnu.org/licenses/gpl.txt).
*/

package bitoflife.chatterbean.parser;

import bitoflife.chatterbean.ChatterBean;
import bitoflife.chatterbean.Context;
import bitoflife.chatterbean.Graphmaster;
import bitoflife.chatterbean.Logger;
import bitoflife.chatterbean.util.Searcher;
import bitoflife.chatterbean.util.Sequence;

import java.io.*;
import java.util.Properties;

public class ChatterBeanParser
{
  /*
  Attributes
  */
  
  private AliceBotParser botParser;
  
  private Class<? extends Logger> loggerClass = Logger.class;
  
  /*
  Constructor
  */
  
  public ChatterBeanParser() throws AliceBotParserConfigurationException
  {
    try
    {
      botParser = new AliceBotParser();
    }
    catch (AliceBotParserConfigurationException e)
    {
      throw e;
    }
    catch (Exception e)
    {
      throw new AliceBotParserConfigurationException(e);
    }
  }
  
  /*
  Methods
  */
  
  private Logger newLogger(String root, String dir) throws Exception
  {
    if (dir == null) return null;
    String path = root + dir;
    
    Sequence sequence = new Sequence(path + "sequence.txt");
    File file = new File(path + "log" + sequence.getNext() + ".txt");
    return loggerClass.getConstructor(Writer.class).newInstance(new FileWriter(file));
  }
  
  private InputStream newResourceStream(String resource, String root, String path) throws Exception
  {
    if (root == null || path == null)
      throw new IllegalArgumentException(
        "Invalid path elements for retrieving " + resource + ": root(" + root + "), path(" + path + ")");
    
    path = root + path;
    
    try
    {
      return new FileInputStream(path);
    }
    catch (Exception e)
    {
      throw new Exception("Error while retrieving " + resource + ": " + path, e);
    }
  }
  
  public void parse(ChatterBean bot, String path) throws AliceBotParserException
  {
    try
    {
      Properties properties = new Properties();
      properties.loadFromXML(new FileInputStream(path));

      String root = path.substring(0, path.lastIndexOf('/') + 1);
      String categories = root + properties.getProperty("categories");
      String logs = properties.getProperty("logs");

      InputStream context = newResourceStream("context", root, properties.getProperty("context"));
      InputStream splitters = newResourceStream("splitters", root, properties.getProperty("splitters"));
      InputStream substitutions = newResourceStream("substitutions", root, properties.getProperty("substitutions"));

      Searcher searcher = new Searcher();
      bot.setLogger(newLogger(root, logs));
      botParser.parse(bot.getAliceBot(), context, splitters, substitutions, searcher.search(categories, ".*\\.aiml"));
    }
    catch (AliceBotParserException e)
    {
      throw e;
    }
    catch (Exception e)
    {
      throw new AliceBotParserException(e);
    }
  }
  
  /*
  Accessor Section
  */
  
  public <C extends Context> void contextClass(Class<C> contextClass)
  {
    botParser.contextClass(contextClass);
  }

  public <L extends Logger> void loggerClass(Class<L> loggerClass)
  {
    this.loggerClass = loggerClass;
  }
  
  public <M extends Graphmaster> void graphmasterClass(Class<M> matcherClass)
  {
    botParser.graphmasterClass(matcherClass);
  }
}
