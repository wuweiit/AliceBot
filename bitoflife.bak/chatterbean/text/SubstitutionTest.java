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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import junit.framework.TestCase;

public class SubstitutionTest extends TestCase
{
  /*
  Attribute Section
  */

  private Substitution substitution;

  /*
  Event Section
  */

  protected void setUp()
  {
  }

  protected void tearDown()
  {
  }

  /*
  Test Section
  */

  public void testMultipleSubstitution() throws Exception
  {
    substitution = new Substitution("un do", "undo", TokenizerMother.newInstance());

    List<String> expected = Arrays.asList(new String[] {"undo", "changes", "."});

    List<String> actual =
      new ArrayList<String>(
        Arrays.asList(
          new String[] {"un", "do", "changes", "."}));

    substitution.substitute(actual);

    assertEquals(expected, actual);
  }

  public void testOffsetSubstitution() throws Exception
  {
    substitution = new Substitution("for you", "for me", TokenizerMother.newInstance());

    List<String> expected = Arrays.asList(new String[] {"It's", "for", "me", "not", "for", "you", "."});
    List<String> original = Arrays.asList(new String[] {"It's", "for", "you", "not", "for", "you", "."});
    List<String> actual = new ArrayList<String>(original);

    int offset = substitution.substitute(0, actual);
    assertEquals(0, offset);
    assertEquals(original, actual);
          
    offset = substitution.substitute(1, actual);
    assertEquals(3, offset);
    assertEquals(expected, actual);
  }
  
  public void testSinglePrefixSubstitution() throws Exception
  {
    substitution = new Substitution(" waht", " what", TokenizerMother.newInstance());

    List<String> expected = Arrays.asList(new String[] {"whatever", "you", "say", "."});

    List<String> actual =
      new ArrayList<String>(
        Arrays.asList(
          new String[] {"Wahtever", "you", "say", "."}));

    substitution.substitute(actual);

    assertEquals(expected, actual);
  }
  
  public void testSingleSufixSubstitution() throws Exception
  {
    substitution = new Substitution("'ll ", " will", TokenizerMother.newInstance());

    List<String> expected = Arrays.asList(new String[] {"I", "will", "do", "the", "test", "."});

    List<String> actual =
      new ArrayList<String>(
        Arrays.asList(
          new String[] {"I'll", "do", "the", "test", "."}));
        
    substitution.substitute(actual);
    
    assertEquals(expected, actual);
  }

  public void testSingleWordSubstitution() throws Exception
  {
    substitution = new Substitution(" whatthe ", "what the", TokenizerMother.newInstance());

    List<String> expected = Arrays.asList(new String[] {"Tell", "me", "what", "the", "problem", "is", "."});

    List<String> actual =
      new ArrayList<String>(
        Arrays.asList(
          new String[] {"Tell", "me", "whatthe", "problem", "is", "."}));

    substitution.substitute(actual);

    assertEquals(expected, actual);
  }

  public void testSuccessiveSubstitutions() throws Exception
  {
    List<String> original = Arrays.asList(new String[] {"I", "will", "do", "the", "test", "for", "him"});
    List<String> expected = Arrays.asList(new String[] {"he", "or", "she", "will", "do", "the", "test", "for", "me"});
    List<String> actual = new ArrayList<String>(original);

    Tokenizer tokenizer = TokenizerMother.newInstance();
    List<Substitution> substitutions =
      Arrays.asList(
        new Substitution[] {new Substitution(" I was ",  "he or she was", tokenizer),
                            new Substitution(" he was ",  "I was", tokenizer),
                            new Substitution(" she was ",  "I was", tokenizer),
                            new Substitution(" I am ",  "he or she is", tokenizer),
                            new Substitution(" I ",  "he or she", tokenizer),
                            new Substitution(" he ",  "I", tokenizer),
                            new Substitution(" she ",  "I", tokenizer),
                            new Substitution(" me ",  "him or her", tokenizer),
                            new Substitution(" him ",  "me", tokenizer),
                            new Substitution(" her ",  "me", tokenizer),
                            new Substitution(" my ",  "his or her", tokenizer),
                            new Substitution(" myself ",  "him or herself", tokenizer),
                            new Substitution(" mine ",  "his or hers", tokenizer)});

    int i = 0, j = 0;
    int[] offsets = {3, 9};
    outer: for (; i < actual.size();)
    {
      int offset = i;
      for (final Substitution substitution : substitutions)
      {
        i = substitution.substitute(offset, actual);
        if (i > offset)
        {
          assertEquals(offsets[j++], i);
          continue outer;
        }
        
        i++;
      }
    }

    assertEquals(9, i);
    assertEquals(2, j); // Number of substitutions.
    assertEquals(expected, actual);
  }
}
