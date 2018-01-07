/*
Copyleft (C) 2005 Hélio Perroni Filho
xperroni@yahoo.com
ICQ: 2490863

This file is part of ChatterBean.

ChatterBean is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

ChatterBean is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with ChatterBean (look at the Documents/ directory); if not, either write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA, or visit (http://www.gnu.org/licenses/gpl.txt).
*/

package bitoflife.chatterbean.text;

import java.util.Arrays;
import junit.framework.TestCase;

public class SentenceTest extends TestCase
{
  /*
  Mehods
  */
  
  public void testEquals()
  {
    Sentence expected = new Sentence(" What's going on? ", new Integer[] {0, -1, 7, 13, 17}, " WHAT IS GOING ON ");
    Sentence actual = new Sentence(" What's going on? ", new Integer[0], " WHAT IS GOING ON ");
    assertFalse(expected.equals(actual));
    
    actual = new Sentence(" What's going on? ", new Integer[] {0, -1, 7, 13, 17}, " WHAT IS GOING ON ");
    assertEquals(expected, actual);
  }
  
  public void testOriginal()
  {
    Sentence sentence = new Sentence(" What's going on? ", new Integer[] {0, null, 7, 13, 17}, " WHAT IS GOING ON ");
    assertEquals(" What's ", sentence.original(0, 2));
    assertEquals(" What's ", sentence.original(0, 1));
    assertEquals(" What's ", sentence.original(1, 2));
  }
}
