/*
Copyleft (C) 2005 Hélio Perroni Filho
xperroni@bol.com.br
ICQ: 2490863

This file is part of ChatterBean.

ChatterBean is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

ChatterBean is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with ChatterBean (look at the Documents/ directory); if not, either write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA, or visit (http://www.gnu.org/licenses/gpl.txt).
*/

package bitoflife.chatterbean.util;

import java.net.URL;
import java.util.List;
import junit.framework.TestCase;

public class SearcherTest extends TestCase
{
  /*
  Attribute Section
  */

  private Searcher searcher;

  /* 
  Event Section
  */

  protected void setUp()
  {
    searcher = new Searcher();
  }

  protected void tearDown()
  {
    searcher = null;
  }

  /*
  Method Section
  */
  
  public void testDirFilesystem()
  {
    String[] paths = searcher.dir("Bots/Alice", ".+\\.aiml");
    
    assertEquals("Bots/Alice/Again.aiml", paths[0]);
    assertEquals("Bots/Alice/Alice.aiml", paths[1]);
    assertEquals("Bots/Alice/Astrology.aiml", paths[2]);
  }
  
  public void testDirURL() throws Exception
  {
    String[] paths = searcher.dir(new URL("file", "localhost", "./"), "Bots/Alice", ".+\\.aiml");
    
    assertEquals("Bots/Alice/Again.aiml", paths[0]);
    assertEquals("Bots/Alice/Alice.aiml", paths[1]);
    assertEquals("Bots/Alice/Astrology.aiml", paths[2]);
  }
}
