/*
Copyleft (C) 2005-2006 Hélio Perroni Filho
xperroni@yahoo.com
ICQ: 2490863

This file is part of ChatterBean.

ChatterBean is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

ChatterBean is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with ChatterBean (look at the Documents/ directory); if not, either write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA, or visit (http://www.gnu.org/licenses/gpl.txt).
*/

/*
<object classid="clsid:CAFEEFAC-0015-0000-0002-ABCDEFFEDCBA"
        codebase="http://java.sun.com/update/1.5.0/jinstall-1_5_0_02-windows-i586.cab#Version=5,0,20,9"
        width="350" height="200">
  <param name="code" value="bitoflife.chatterbean.util.Applet">
  <param name="type" value="application/x-java-applet;jpi-version=1.5.0_02">
  <param name="scriptable" value="false">
  <comment>
    <embed type="application/x-java-applet;jpi-version=1.5.0_02"
           code="bitoflife.chatterbean.util.Applet"
           width="350" height="200"
           scriptable="false"
           pluginspage="http://java.sun.com/products/plugin/index.html#download">
      <noembed></noembed>
    </embed>
  </comment>
</object>
*/

package bitoflife.chatterbean;

import bitoflife.chatterbean.parser.AliceBotParser;
import bitoflife.chatterbean.parser.ChatterBeanParser;
import bitoflife.chatterbean.script.Interpreter;
import bitoflife.chatterbean.script.JavascriptInterpreter;
import bitoflife.chatterbean.util.Searcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class ChatterBean extends JApplet
{
  /*
  Attribute Section
  */
  
  /** Version class identifier for the serialization engine. Matches the number of the last revision where the class was created / modified. */
  private static final long serialVersionUID = 8L;

  private static final InputStream[] INPUT_STREAM_ARRAY = {};
  
  /** Javascript interpreter used by the underlying chatterbot. */
  private final Interpreter javascript = new JavascriptInterpreter(this);

  /** The component container for this object. */  
  private final Container container = getContentPane();

  /** Input text control. */
  private final JTextField input = new JTextField(30)
  {
    /** Version class identifier for the serialization engine. */
    private static final long serialVersionUID = 7L;

    /* Object initialization */
    {
      ActionListener listener = new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          String request = getText();
          String response = aliceBot.respond(request);
  
          setText("");
          output.append("> " + request + "\n");
          output.append(response + "\n");
        }
      };
      
      addActionListener(listener);
      container.add(this);
    }
  };

  /** Output text control. */
  private final JTextArea output = new JTextArea(10, 30)
  {
    /** Version class identifier for the serialization engine. */
    private static final long serialVersionUID = 7L;

    /* Object initialization */
    {
      setEditable(false);
      setLineWrap(true);
      setWrapStyleWord(true);
      container.add(new JScrollPane(this));
    }
  };
  
  /** The underlying AliceBot used to produce responses to user queries. */
  private AliceBot aliceBot;
  
  /** Logger object used to keep track of this bot's conversations. */
  private Logger logger;
  
  /*
  Constructor Section
  */
  
  /**
  Default constructor.
  */
  public ChatterBean()
  {
    container.setLayout(new FlowLayout());
  }
  
  /**
  Creates a new ChatterBean configured with a set of properties.
  
  @param path Path of the properties file.
  */
  public ChatterBean(String path)
  {
    this();
    configure(path);
  }
  
  /*
  Event Section
  */
  
  private void beforeConfigure()
  {
    if (getAliceBot() == null)
      setAliceBot(new AliceBot());
  }
  
  private void afterConfigure()
  {
    Context context = aliceBot.getContext();
    context.property("javascript.interpreter", javascript);
  }

  public void init()
  {
    try
    {
      beforeConfigure();

      AliceBotParser parser = new AliceBotParser();
      parser.parse(getAliceBot(),
        openStream("context"),
        openStream("splitters"),
        openStream("substitutions"),
        openStreams("aiml"));

      afterConfigure();
    }
    catch (ChatterBeanException e)
    {
      throw e;
    }
    catch (Exception e)
    {
      throw new ChatterBeanException(e);
    }
  }

  /*
  Method Section
  */
  
  private InputStream openStream(String name)
  {
    String value = getParameter(name);
    if (value == null || "".equals(value.trim()))
      throw new ChatterBeanException("Invalid value for parameter \"" + name + "\": " + value);

    return openURLStream(value);
  }
  
  private InputStream openURLStream(String path)
  {
    try
    {
      URL url = new URL(getDocumentBase(), path);
      return url.openStream();
    }
    catch (Exception e)
    {
      throw new ChatterBeanException(e);
    }
  }
  
  private InputStream[] openStreams(String name)
  {
    String value = getParameter(name);
    if (value == null || "".equals(value.trim()))
      throw new ChatterBeanException("Invalid value for parameter \"" + name + "\": " + value);
    else if (value.endsWith(".aiml"))
      return new InputStream[] {openURLStream(value)};
    else if (value.endsWith(".txt"))
      return openAIMLStreams(value);
    else
      return searchAIMLStreams(value);
  }
  
  private InputStream[] openAIMLStreams(String path)
  {
    try
    {
      BufferedReader reader = new BufferedReader(new InputStreamReader(openURLStream(path)));
      List<InputStream> streams = new LinkedList<InputStream>();
      for (String fileName = ""; (fileName = reader.readLine()) != null;)
        streams.add(openURLStream(fileName));
      
      return streams.toArray(INPUT_STREAM_ARRAY);
    }
    catch (Exception e)
    {
      throw new ChatterBeanException(e);
    }
  }

  private InputStream[] searchAIMLStreams(String path)
  {
    try
    {
      Searcher searcher = new Searcher();
      return searcher.search(getDocumentBase(), path, ".+\\.aiml");
    }
    catch (Exception e)
    {
      throw new ChatterBeanException(e);
    }
  }
  
  /**
  Configures this object with a set of properties.
  
  @param path Path of the properties file.
  */
  public void configure(String path)
  {
    try
    {
      beforeConfigure();
      ChatterBeanParser parser = new ChatterBeanParser();
      parser.parse(this, path);
    }
    catch (Exception e)
    {
      throw new ChatterBeanException(e);
    }
  }
  
  public String respond(String request)
  {
    String response = "";
    if(request != null && !"".equals(request.trim())) try
    {
      response = aliceBot.respond(request);
      if (logger != null)
        logger.append(request, response);
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }

    return response;
  }
  
  /**
  Main entry point.
  */
  public static void main(String[] args) throws Exception
  {
    ChatterBean applet = new ChatterBean(args[0]);
    
    if (args.length > 1 && "gui".equals(args[1]))
    {
      JFrame frame = new JFrame("ChatterBean GUI Window");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.getContentPane().add(applet);
      frame.setSize(350, 210);
      frame.setVisible(true);
    }
    else
    {
      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      for(;;)
      {
        String input = reader.readLine();
        if (input == null || "".equals(input.trim())) break;
        System.out.println(applet.respond(input));
      }
    }
  }
  
  /*
  Property Section
  */
  
  /**
  Gets the AliceBot encapsulated by this bot.
  
  @return An AliceBot.
  */
  public AliceBot getAliceBot()
  {
    return aliceBot;
  }
  
  /**
  Sets the AliceBot encapsulated by this bot.
  
  @param aliceBot An AliceBot.
  */
  public void setAliceBot(AliceBot aliceBot)
  {
    this.aliceBot = aliceBot;
  }
  
  /**
  Gets the logger object used by this bot.
  
  @return A Logger object.
  */
  public Logger getLogger()
  {
    return logger;
  }
  
  /**
  Sets the logger object used by this bot.
  
  @param logger A Logger object.
  */
  public void setLogger(Logger logger)
  {
    this.logger = logger;
  }
}
