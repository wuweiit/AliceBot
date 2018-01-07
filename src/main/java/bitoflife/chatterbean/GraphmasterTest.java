/*
Copyleft (C) 2005 Hélio Perroni Filho
xperroni@yahoo.com
ICQ: 2490863

This file is part of ChatterBean.

ChatterBean is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

ChatterBean is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with ChatterBean (look at the Documents/ directory); if not, either write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA, or visit (http://www.gnu.org/licenses/gpl.txt).
*/

package bitoflife.chatterbean;

import bitoflife.chatterbean.aiml.Category;
import bitoflife.chatterbean.aiml.Pattern;
import bitoflife.chatterbean.aiml.Template;
import bitoflife.chatterbean.aiml.That;
import bitoflife.chatterbean.text.Sentence;
import junit.framework.TestCase;

import static bitoflife.chatterbean.text.Sentence.ASTERISK;

public class GraphmasterTest extends TestCase
{
  /*
  Attributes
  */

  private final GraphmasterMother mother  = new GraphmasterMother();

  private Graphmaster root;
  
  /*
  Events
  */

  protected void setUp()
  {
    root = mother.newInstance();
  }

  protected void tearDown()
  {
    root = null;
  }
  
  /*
  Methods
  */

  public void testMatch()
  {
    Category category;
    Match match;
    
    match = new Match(new Sentence(" Say goodbye again. ", new Integer[] {0, 4, 12, 19}, " SAY GOODBYE AGAIN "));
    category = root.match(match);
    assertNotNull(category);
    assertEquals("What, again? \"goodbye\".", category.process(match));

    match = new Match(new Sentence(" Say it now. ", new Integer[] {0, 4, 7, 12}, " SAY IT NOW "));
    category = root.match(match);
    assertNotNull(category);
    assertEquals("Whatever you want...", category.process(match));

    match = new Match(new Sentence(" Say goodbye. ", new Integer[] {0, 4, 13}, " SAY GOODBYE "));
    category = root.match(match);
    assertNotNull(category);
    assertEquals("goodbye!", category.process(match));
    
    match = new Match(
              new Sentence(" Do you see the fire in my eyes? ",
                           new Integer[] {0, 3, 7, 11, 15, 20, 23, 26, 32},
                           " DO YOU SEE THE FIRE IN MY EYES "));

    category = root.match(match);
    assertNotNull(category);
    assertEquals("Yes, I see the fire in your eyes.", category.process(match));
  }
  
  public void testThatMatch()
  {
    Sentence input = new Sentence(" Do you like it? ", new Integer[] {0, 3, 12, 16}, " DO YOU LIKE IT ");
    Sentence that = new Sentence(" CHEESE ", new Integer[] {0, 7}, " CHEESE ");
    Match match = new Match(null, input, that, ASTERISK);

    Category expected = new Category(new Pattern("DO YOU LIKE IT"), new That("CHEESE"), new Template("Yes."));
    root.append(expected);
    Category actual = root.match(match);

    assertEquals(expected, actual);
  }
}
