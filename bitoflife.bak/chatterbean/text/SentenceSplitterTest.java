/*
Copyleft (C) 2006 Hélio Perroni Filho
xperroni@yahoo.com
ICQ: 2490863

This file is part of ChatterBean.

ChatterBean is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

ChatterBean is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with ChatterBean (look at the Documents/ directory); if not, either write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA, or visit (http://www.gnu.org/licenses/gpl.txt).
*/

package bitoflife.chatterbean.text;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;

public class SentenceSplitterTest extends TestCase
{
  /*
  Attribute Section
  */
  
  private SentenceSplitter splitter;
  
  /*
  Event Section
  */

  protected void setUp() throws Exception
  {
    splitter = newSentenceSplitter();
  }

  protected void tearDown()
  {
    splitter = null;
  }

  /*
  Method Section
  */
  
  private SentenceSplitter newSentenceSplitter()
  {
    Map<String, String> protection = new HashMap<String, String>();
    List<String> splitters = Arrays.asList(new String[] {"...", ".", "!", "?", ";", ",", ":"});
    return new SentenceSplitter(protection, splitters);
  }
  
  /*
  Test Section
  */
  
  public void testSplitString()
  {
    String[] expected = 
      {"Hello Alice.", "How are you?", "You look fine!", "Please forgive my manners;", "I am so happy today..."};

    String input = "Hello Alice. How are you? You look fine! Please forgive my manners; I am so happy today...";
    String[] actual = splitter.split(input);
    
    assertEquals(Arrays.asList(expected), Arrays.asList(actual));
  }
  
  public void testSplitStringEmpty()
  {
    String[] expected = {"thank you."};
    String input = " ...thank you. ";
    String[] actual = splitter.split(input);
    
    assertEquals(Arrays.asList(expected), Arrays.asList(actual));
  }
}
