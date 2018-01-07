/*
Copyleft (C) 2005 Hélio Perroni Filho
xperroni@bol.com.br
ICQ: 2490863

This file is part of ChatterBean.

ChatterBean is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

ChatterBean is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with ChatterBean (look at the Documents/ directory); if not, either write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA, or visit (http://www.gnu.org/licenses/gpl.txt).
*/

package bitoflife.chatterbean.aiml;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import junit.framework.TestCase;

import bitoflife.chatterbean.Context;
import bitoflife.chatterbean.Match;
import bitoflife.chatterbean.Graphmaster;
import bitoflife.chatterbean.util.Searcher;

public class AIMLParserTest extends TestCase
{
  /*
  Inner Classes
  */

  public class GraphmasterMock extends Graphmaster
  {
    /*
    Attributes
    */

    public List<Category> categories;

    /*
    Methods
    */

    public void append(List<Category> categories)
    {
       this.categories = categories;
    }

    public Category match(Match match)
    {
      return null;
    }

    public int size()
    {
      return categories.size();
    }
  }

  /*
  Attributes
  */

  private AIMLParser loader;

  /*
  Events
  */

  protected void setUp() throws Exception
  {
    loader = new AIMLParser();
  }

  protected void tearDown()
  {
    loader = null;
  }

  /*
  Methods
  */

  public void testParseThatTopicAIML() throws Exception
  {
    AIMLParser parser = new AIMLParser();
    GraphmasterMock mock = new GraphmasterMock();
    parser.parse(mock, new FileInputStream("Bots/Alice/thattopic.aiml"));
    
    assertEquals(4, mock.categories.size());
    
    Category expected = new Category(
      new Pattern(" YES "),
      new That(" DO YOU LIKE CHEESE "),
      new Template("Good for you.", new Think(new Set("topic", "*")))
    );
    Category actual = mock.categories.get(0);
    
    assertEquals(expected, actual);
  }

  public void testParseAgainAIML() throws Exception
  {
    AIMLParser parser = new AIMLParser();
    GraphmasterMock mock = new GraphmasterMock();
    parser.parse(mock, new FileInputStream("Bots/Alice/Again.aiml"));

    Category category = null;
    Map<Pattern, Category> categories = new HashMap<Pattern, Category>();
    for(Iterator<Category> i = mock.categories.iterator(); i.hasNext();)
    {
      category = i.next();
      categories.put(category.getPattern(), category);
    }

    /* Categories from the Again.aiml file. */
    category = new Category(new Pattern(" _ AGAIN "), new Template("Once more? ", new Srai(1)));
    Category actual = categories.get(category.getPattern());
    assertNotNull(actual);
    assertEquals(category, actual);

    category = new Category(new Pattern(" _ ALICE "), new Template(new Srai(1)));
    assertEquals(category, categories.get(category.getPattern()));

    category = new Category(new Pattern(" YOU MAY * "), new Template(new Srai(1)));
    assertEquals(category, categories.get(category.getPattern()));

    category = new Category(new Pattern(" SAY * "), new Template("\"", new Star(1), "\"."));
    assertEquals(category, categories.get(category.getPattern()));
  }
}
