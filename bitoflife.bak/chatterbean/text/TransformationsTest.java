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

public class TransformationsTest extends TestCase
{
  /*
  Attribute Section
  */

  private static final TransformationsMother mother = new TransformationsMother();
  private Transformations transformations;

  /*
  Event Section
  */

  protected void setUp() throws Exception
  {
    transformations = mother.newInstance();
  }

  protected void tearDown()
  {
    transformations = null;
  }

  /*
  Method Section
  */

  public static void assertRequest(Request expected, Request actual)
  {
    assertEquals(expected.getOriginal(), actual.getOriginal());

    Sentence[] expSentences = expected.getSentences();
    Sentence[] actSentences = actual.getSentences();
    assertEquals(expSentences.length, actSentences.length);

    for (int i = 0, n = actSentences.length; i < n; i++)
      assertSentence(expSentences[i], actSentences[i]);
  }

  private void assertSentence(String original, Integer[] mappings, String normalized)
  {
    Sentence expected = new Sentence(original, mappings, normalized);
    Sentence actual = new Sentence(original);
    transformations.normalization(actual);

    assertSentence(expected, actual);
  }

  public static void assertSentence(Sentence expected, Sentence actual)
  {
    assertEquals("\"" + actual.getOriginal() + "\"", expected.getOriginal(), actual.getOriginal());
    assertEquals(actual.getNormalized(), expected.getNormalized(), actual.getNormalized());
    assertTrue(Arrays.toString(actual.getMappings()), Arrays.equals(expected.getMappings(), actual.getMappings()));
  }
  
  public void testEmptySentences()
  {
    Request expected = new Request
    (
      " ...thank you. ",
      new Sentence(" thank you. ", new Integer[] {0, 6, 11}, " THANK YOU ")
    );
    
    Request actual = new Request("...thank you.");
    transformations.normalization(actual);
    assertRequest(expected, actual);
  }

  public void testNormalizeRequest()
  {
    Request expected =
      new Request(" Hello Alice. How are you? You look fine! Please forgive my manners; I am so happy today... ",
                  new Sentence(" Hello Alice. ", new Integer[] {0, 6, 13}, " HELLO ALICE "),
                  new Sentence(" How are you? ", new Integer[] {0, 4, 8, 13}, " HOW ARE YOU "),
                  new Sentence(" You look fine! ", new Integer[] {0, 4, 9, 15}, " YOU LOOK FINE "),
                  new Sentence(" Please forgive my manners; ", new Integer[] {0, 7, 15, 18, 27}, " PLEASE FORGIVE MY MANNERS "),
                  new Sentence(" I am so happy today... ", new Integer[] {0, 2, 5, 8, 14, 23}, " I AM SO HAPPY TODAY "));

    Request actual =
      new Request("Hello Alice. How are you?   You look fine! Please forgive my manners; I am so happy today...");

    transformations.normalization(actual);
    assertRequest(expected, actual);
    
    expected = new Request(" Thanks. ", new Sentence(" Thanks. ", new Integer[] {0, null, 8}, " THANK YOU "));
    actual = new Request("Thanks.");
    transformations.normalization(actual);
    assertRequest(expected, actual);
                           
    expected = new Request(" Do you see the fire in my eyes? ",
                           new Sentence(" Do you see the fire in my eyes? ",
                                        new Integer[] {0, 3, 7, 11, 15, 20, 23, 26, 32},
                                        " DO YOU SEE THE FIRE IN MY EYES "));

    actual = new Request(" Do you see the fire in my eyes? ");
    transformations.normalization(actual);
    assertRequest(expected, actual);

    expected = new Request(" I said \"Hello Unknown Person!\". ",
                 new Sentence(" I said \"Hello Unknown Person!\". ",
                              new Integer[] {0, 2, 7, 14, 22, 32},
                              " I SAID HELLO UNKNOWN PERSON "));
    actual = new Request("I said \"Hello Unknown Person!\".");
    transformations.normalization(actual);
    assertRequest(expected, actual);

    expected = new Request(" Hello Unknown Person! My name is Alice, who are you? ",
                 new Sentence(" Hello Unknown Person! ",
                              new Integer[] {0, 6, 14, 22},
                              " HELLO UNKNOWN PERSON "),
                 new Sentence(" My name is Alice, who are you? ",
                              new Integer[] {0, 3, 8, 11, 18, 22, 26, 31},
                              " MY NAME IS ALICE WHO ARE YOU "));
    actual = new Request("Hello Unknown Person! My name is Alice, who are you?");
    transformations.normalization(actual);
    assertRequest(expected, actual);

    expected = new Request(" HELLO ", new Sentence(" HELLO ", new Integer[] {0, 6}, " HELLO "));
    actual = new Request("HELLO");
    transformations.normalization(actual);
    assertRequest(expected, actual);
  }

  public void testNormalizeSentence()
  {
    Sentence expected = new Sentence(" What's going on? ", new Integer[] {0, null, 7, 13, 17}, " WHAT IS GOING ON ");
    Sentence actual = new Sentence(" What's going on? ");
    transformations.normalization(actual);

    assertEquals(expected.getOriginal(), actual.getOriginal());
    assertEquals(expected.getNormalized(), actual.getNormalized());
    assertEquals(Arrays.toString(actual.getMappings()),
                 Arrays.toString(expected.getMappings()), Arrays.toString(actual.getMappings()));

    expected = new Sentence(" What's going on, and what's this? ",
                            new Integer[] {0, null, 7, 13, 17, 21, null, 28, 34},
                            " WHAT IS GOING ON AND WHAT IS THIS ");
    actual = new Sentence(" What's going on, and what's this? ");
    transformations.normalization(actual);

    assertEquals(actual.getOriginal(), expected.getOriginal(), actual.getOriginal());
    assertEquals(expected.getNormalized(), actual.getNormalized());
    assertEquals(Arrays.toString(expected.getMappings()), Arrays.toString(actual.getMappings()));

    expected = new Sentence(" Don't you know what's going on? ",
                            new Integer[] {0, null, 6, 10, 15, null, 22, 28, 32},
                            " DO NOT YOU KNOW WHAT IS GOING ON ");
    actual = new Sentence(" Don't you know what's going on? ");
    transformations.normalization(actual);

    assertEquals(expected.getOriginal(), actual.getOriginal());
    assertEquals(expected.getNormalized(), actual.getNormalized());
    assertEquals(Arrays.toString(expected.getMappings()), Arrays.toString(actual.getMappings()));

    expected = new Sentence(" Remove mis placed space. ", new Integer[] {0, 7, 18, 25}, " REMOVE MISPLACED SPACE ");
    actual = new Sentence(" Remove mis placed space. ");
    transformations.normalization(actual);

    assertEquals(expected.getOriginal(), actual.getOriginal());
    assertEquals(expected.getNormalized(), actual.getNormalized());
    assertEquals(Arrays.toString(actual.getMappings()),
                 Arrays.toString(expected.getMappings()), Arrays.toString(actual.getMappings()));

    expected = new Sentence(" Test: word; breaking, code. ", new Integer[] {0, 6, 12, 22, 28}, " TEST WORD BREAKING CODE ");
    actual = new Sentence(" Test:word;  breaking,code. ");
    transformations.normalization(actual);

    assertEquals(expected.getOriginal(), actual.getOriginal());
    assertEquals(expected.getNormalized(), actual.getNormalized());
    assertEquals(Arrays.toString(actual.getMappings()),
                 Arrays.toString(expected.getMappings()), Arrays.toString(actual.getMappings()));

    expected = new Sentence(" Today is 2005-06-11. ", new Integer[] {0, 6, 9, null, null, 21}, " TODAY IS 2005 06 11 ");
    actual = new Sentence(" Today is 2005-06-11. ");
    transformations.normalization(actual);

    assertEquals(expected.getOriginal(), actual.getOriginal());
    assertEquals(expected.getNormalized(), actual.getNormalized());
    assertEquals(Arrays.toString(actual.getMappings()),
                 Arrays.toString(expected.getMappings()), Arrays.toString(actual.getMappings()));

    assertSentence(" Hello Alice. ", new Integer[] {0, 6, 13}, " HELLO ALICE ");
    assertSentence(" How are you? ", new Integer[] {0, 4, 8, 13}, " HOW ARE YOU ");
    assertSentence(" You look fine! ", new Integer[] {0, 4, 9, 15}, " YOU LOOK FINE ");
    assertSentence(" Please forgive my manners; ", new Integer[] {0, 7, 15, 18, 27}, " PLEASE FORGIVE MY MANNERS ");
    assertSentence(" I am so happy today... ", new Integer[] {0, 2, 5, 8, 14, 23}, " I AM SO HAPPY TODAY ");
  }
  
  public void testNormalizeString()
  {
    String expected = " HELLO MY NAME IS ALICE HOW ARE YOU ";
    String actual = transformations.normalization("Hello! My name is Alice, how're you?");
    assertEquals(expected, actual);
    
    expected = " I AM FINE THANK YOU ";
    actual = transformations.normalization("I'm fine, thanks!");
    assertEquals(expected, actual);
  }
  
  public void testPerson()
  {
    assertEquals("I will do the test for him or her", transformations.person("He will do the test for me"));
    assertEquals("I will do the test for him or her.", transformations.person("He will do the test for me."));
    assertEquals("he or she will do the test for me.", transformations.person("I will do the test for him."));
  }
  
  public void testPerson2()
  {
    assertEquals("I will do the test for you.", transformations.person2("You will do the test for me."));
  }
  
  public void testGender()
  {
    assertEquals("she will do the test for him.", transformations.gender("He will do the test for her."));
  }
}
