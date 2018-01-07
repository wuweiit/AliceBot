/*
Copyleft (C) 2005 Hélio Perroni Filho
xperroni@yahoo.com
ICQ: 2490863

This file is part of ChatterBean.

ChatterBean is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

ChatterBean is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with ChatterBean (look at the Documents/ directory); if not, either write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA, or visit (http://www.gnu.org/licenses/gpl.txt).
*/

package bitoflife.chatterbean.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import junit.framework.TestCase;

public class SequenceTest extends TestCase
{
  /*
  Attributes
  */

  private static final SequenceMother mother = new SequenceMother();
  
  private Sequence sequence1, sequence2;

  /* 
  Events
  */

  protected void setUp() throws Exception
  {
    mother.reset();

    sequence1 = mother.newInstance();
    sequence2 = mother.newInstance();
  }

  protected void tearDown()
  {
    sequence1 = sequence2 = null;
  }

  /*
  Methods
  */

  public void testGetNext() throws IOException
  {
    for (int i = 0; i < 100; i++)
    {
      long a = sequence1.getNext();
      long b = sequence2.getNext();
    
      assertTrue("sequence1 = " + a + ", sequence2 = " + b, a != b);
    }
    
  }
  
  public void testPersistence() throws IOException
  {
    long count = sequence1.getNext();

    // Simulates a system crash at the moment the persistent file is open.    
    PrintWriter writer = new PrintWriter(new FileWriter(mother.file, false), true);
    writer.println("");
    writer.close();
    
    sequence1 = mother.newInstance();
    
    assertEquals(count + 1, sequence1.getNext());
  }
}
