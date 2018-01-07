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

import junit.framework.Test;
import junit.swingui.TestRunner;

public class TestSuite extends junit.framework.TestSuite
{
  /*
  Attributes
  */
  
  /** Names of the test classes to include in the test. */
  private static String[] testNames;

  /*
  Methods
  */
  
  /**
  Adds all the known unit tests to the suite.
  
  @param suite The test suite to which the known unit tests must be added. 
  */
  private static void addAllTests(TestSuite suite)
  {
    suite.addTestSuite(bitoflife.chatterbean.AliceBotTest.class);
    suite.addTestSuite(bitoflife.chatterbean.GraphmasterTest.class);
    suite.addTestSuite(bitoflife.chatterbean.LoggerTest.class);
    suite.addTestSuite(bitoflife.chatterbean.MatchTest.class);
    suite.addTestSuite(bitoflife.chatterbean.aiml.AIMLHandlerTest.class);
    suite.addTestSuite(bitoflife.chatterbean.aiml.AIMLParserTest.class);
    suite.addTestSuite(bitoflife.chatterbean.aiml.CategoryTest.class);
    suite.addTestSuite(bitoflife.chatterbean.aiml.SystemTest.class);
    suite.addTestSuite(bitoflife.chatterbean.aiml.TemplateElementTest.class);
    suite.addTestSuite(bitoflife.chatterbean.parser.ContextParserTest.class);
    suite.addTestSuite(bitoflife.chatterbean.text.SentenceTest.class);
    suite.addTestSuite(bitoflife.chatterbean.text.SentenceSplitterTest.class);
    suite.addTestSuite(bitoflife.chatterbean.text.TransformationsTest.class);
    suite.addTestSuite(bitoflife.chatterbean.text.SubstitutionTest.class);
    suite.addTestSuite(bitoflife.chatterbean.text.TokenizerTest.class);
    suite.addTestSuite(bitoflife.chatterbean.util.SearcherTest.class);
    suite.addTestSuite(bitoflife.chatterbean.util.SequenceTest.class);
  }

  /**
  Main entry point.
  
  @param args The names of test classes that must be included in the test. If ommited, all known tests will be included. 
  */
  public static void main(String args[])
  {
    testNames = args;
    TestRunner.main(new String[] {"-noloading", "bitoflife.chatterbean.TestSuite"});
  }

  /**
  Returns a new test suite.
  
  @return A new test suite.
  */
  public static Test suite()
  {
    TestSuite suite = new TestSuite();
    
    if (testNames == null || testNames.length == 0) /* If no argument was given during the command-line call... */
      addAllTests(suite); /* Add all known tests to the suite. */
    else try // Otherwise...
    {
      // Add only the given tests to the suite.
      for (String name : testNames) 
        suite.addTestSuite(Class.forName(name));
    }
    catch (ClassNotFoundException e)
    {
      throw new RuntimeException(e);
    }

    return suite;
  }
}
