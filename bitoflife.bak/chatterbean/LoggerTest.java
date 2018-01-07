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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import java.io.FileWriter;
import junit.framework.TestCase;
import bitoflife.chatterbean.util.Sequence;

public class LoggerTest extends TestCase
{
  /*
  Attributes
  */

  private File file;
  private Logger logger;

  /* 
  Events
  */

  protected void setUp() throws Exception
  {
    Sequence sequence = new Sequence("Logs/sequence.txt");
    file = new File("Logs/log" + sequence.getNext() + ".txt");
    logger = new Logger(new FileWriter(file));
  }

  protected void tearDown()
  {
    file = null;
    logger = null;
  }

  /*
  Methods
  */

  public void testAddEntry() throws IOException
  {
    logger.append("First request", "First response");
    logger.append("Second request", "Second response");
    logger.append("Third request", "Third response");
    
    BufferedReader reader = new BufferedReader(new FileReader(file));
    
    assertTrue(reader.readLine().matches("\\[[0-9]{2}/[0-9]{2}/[0-9]{4} [0-9]{2}:[0-9]{2}:[0-9]{2}\\]" +
                                         "\\[First request\\]\\[First response\\]"));

    assertTrue(reader.readLine().matches("\\[[0-9]{2}/[0-9]{2}/[0-9]{4} [0-9]{2}:[0-9]{2}:[0-9]{2}\\]" +
                                         "\\[Second request\\]\\[Second response\\]"));

    assertTrue(reader.readLine().matches("\\[[0-9]{2}/[0-9]{2}/[0-9]{4} [0-9]{2}:[0-9]{2}:[0-9]{2}\\]" +
                                         "\\[Third request\\]\\[Third response\\]"));
  }
}
