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

import bitoflife.chatterbean.AliceBotMother;
import junit.framework.TestCase;
import org.xml.sax.helpers.AttributesImpl;

public class AIMLHandlerTest extends TestCase
{
  /*
  Attribute Section
  */

  private AIMLHandler handler;
  private AIMLStack stack;

  private AliceBotMother aliceBot = new AliceBotMother();

  /*
  Event Section
  */

  protected void setUp() throws Exception
  {
    handler = new AIMLHandler();
    stack = handler.stack;
  }

  protected void tearDown()
  {
    handler = null;
  }

  /*
  Method Section
  */

  private char[] toCharArray(String string)
  {
    int n = string.length();
    char[] chars = new char[n];
    string.getChars(0, n, chars, 0);
    return chars;
  }
  
  public void testCharacters() throws Exception
  {
    char[] text;
    AttributesImpl attributes = new AttributesImpl();
    
    handler.startElement(null, null, "template", attributes);
      handler.characters(text = toCharArray("Hello."), 0, text.length);
    handler.endElement(null, null, "template");

    Text expected = new Text("Hello.");
    Text actual = (Text) ((Template) stack.peek()).children().get(0);
    assertEquals(expected, actual);
  }

  public void testAiml() throws Exception
  {
    char[] text;
    AttributesImpl attributes = new AttributesImpl();
    AttributesImpl aimlAtts = new AttributesImpl();
    aimlAtts.addAttribute(null, "version", null, "String", "1.0.1");
    handler.startElement(null, null, "aiml", aimlAtts);
      handler.startElement(null, null, "category", attributes);
        handler.startElement(null, null, "pattern", attributes);
          handler.characters(text = toCharArray("HELLO ALICE I AM *"), 0, text.length);
        handler.endElement(null, null, "pattern");
        handler.startElement(null, null, "template", attributes);
          handler.characters(text = toCharArray("Hello "), 0, text.length);
          handler.startElement(null, null, "star", attributes);
          handler.characters(text = toCharArray(", nice to meet you."), 0, text.length);
        handler.endElement(null, null, "template");
      handler.endElement(null, null, "category");
    handler.endElement(null, null, "aiml");

    Aiml actual = (Aiml) stack.peek();
    Aiml expected = new Aiml(new Category(new Pattern("HELLO ALICE I AM *"),
                                          new Template("Hello ",
                                                       new Star(1),
                                                       ", nice to meet you.")));
    assertEquals(expected, actual);
    assertEquals("1.0.1", actual.getVersion());
  }

  public void testBot() throws Exception
  {
    AttributesImpl attributes = new AttributesImpl();
    attributes.addAttribute(null, "name", null, "String", "bot.predicate1");
    handler.startElement(null, null, "bot", attributes);

    Bot expected = new Bot("bot.predicate1");
    Bot actual   = (Bot) stack.peek();
    assertEquals(expected, actual);

    Bot bot2 = new Bot("bot.predicate2");
    assertFalse(bot2.equals(actual));
  }

  public void testCategory() throws Exception
  {
    char[] text;
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "category", attributes);
      handler.startElement(null, null, "pattern", attributes);
        handler.characters(text = toCharArray("HELLO ALICE I AM *"), 0, text.length);
      handler.endElement(null, null, "pattern");
      handler.startElement(null, null, "that", attributes);
        handler.characters(text = toCharArray("TEST"), 0, text.length);
      handler.endElement(null, null, "that");
      handler.startElement(null, null, "template", attributes);
        handler.characters(text = toCharArray("Hello "), 0, text.length);
        handler.startElement(null, null, "star", attributes);
        handler.characters(text = toCharArray(", nice to meet you."), 0, text.length);
      handler.endElement(null, null, "template");
    handler.endElement(null, null, "category");

    Category actual = (Category) stack.peek();
    Category expected = new Category(new Pattern("HELLO ALICE I AM *"),
                                     new That("TEST"),
                                     new Template("Hello ", new Star(1), ", nice to meet you."));
    assertEquals(expected, actual);
  }
  
  public void testCondition() throws Exception
  {
    char[] text;
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "condition", attributes);
      attributes = new AttributesImpl();
      attributes.addAttribute(null, null, "name", "String", "condition1");
      attributes.addAttribute(null, null, "value", "String", "test value1");
      handler.startElement(null, null, "li", attributes);
        handler.characters(text = toCharArray("Conditioned output 1"), 0, text.length);
      handler.endElement(null, null, "li");
      attributes = new AttributesImpl();
      handler.startElement(null, null, "li", attributes);
        handler.characters(text = toCharArray("Else output"), 0, text.length);
      handler.endElement(null, null, "li");
    handler.endElement(null, null, "condition");

    Condition actual = (Condition) stack.peek();
    Condition expected = new Condition(null, null,
                           new Li("condition1", "test value1", "Conditioned output 1"),
                           new Li(null, null, "Else output"));    
    assertEquals(expected, actual);
    
    attributes = new AttributesImpl();
    attributes.addAttribute(null, null, "name", "String", "condition");
    handler.startElement(null, null, "condition", attributes);
      attributes = new AttributesImpl();
      attributes.addAttribute(null, null, "value", "String", "test value1");
      handler.startElement(null, null, "li", attributes);
        handler.characters(text = toCharArray("Conditioned output 1"), 0, text.length);
      handler.endElement(null, null, "li");
      attributes = new AttributesImpl();
      handler.startElement(null, null, "li", attributes);
        handler.characters(text = toCharArray("Else output"), 0, text.length);
      handler.endElement(null, null, "li");
    handler.endElement(null, null, "condition");

    actual = (Condition) stack.peek();
    expected = new Condition("condition1", null,
                 new Li(null, "test value1", "Conditioned output 1"),
                 new Li(null, null, "Else output"));    
    assertEquals(expected, actual);
    
    attributes = new AttributesImpl();
    attributes.addAttribute(null, null, "name", "String", "condition");
    attributes.addAttribute(null, null, "value", "String", "test value1");
    handler.startElement(null, null, "condition", attributes);
      handler.characters(text = toCharArray("Conditioned output"), 0, text.length);
    handler.endElement(null, null, "condition");

    expected = new Condition("condition1", "test value1", "Conditioned output");    
    actual = (Condition) stack.peek();
    assertEquals(expected, actual);
  }

  public void testDate() throws Exception
  {
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "date", attributes);

    Date expected = new Date();
    Date actual = (Date) stack.peek();
    assertEquals(expected, actual);
  }

  public void testFormal() throws Exception
  {
    char[] text;
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "formal", attributes);
      handler.characters(text = toCharArray("change this input case to title case"), 0, text.length);
    handler.endElement(null, null, "formal");

    Formal expected = new Formal("change this input case to title case");
    Formal actual   = (Formal) stack.peek();
    assertEquals(expected, actual);

    assertEquals("Change This Input Case To Title Case", actual.process(null));
  }

  public void testGender() throws Exception
  {
    char[] text;
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "gender", attributes);
      handler.characters(text = toCharArray("I WILL DO WHAT HE SAYS"), 0, text.length);
    handler.endElement(null, null, "gender");

    Gender expected = new Gender("I WILL DO WHAT HE SAYS");
    Gender actual   = (Gender) stack.peek();
    assertEquals(expected, actual);

    Gender differs = new Gender("I WILL DO SOMETHING ELSE");
    assertFalse(differs.equals(actual));
  }

  public void testGet() throws Exception
  {
    AttributesImpl attributes = new AttributesImpl();
    attributes.addAttribute(null, "name", null, "String", "predicate1");
    handler.startElement(null, null, "get", attributes);

    Get expected = new Get("predicate1");
    Get actual = (Get) stack.peek();
    assertEquals(expected, actual);
  }

  public void testGossip() throws Exception
  {
    char[] text;
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "gossip", attributes);
      handler.characters(text = toCharArray("write this input to alternate channel"), 0, text.length);
    handler.endElement(null, null, "gossip");

    Gossip expected = new Gossip("write this input to alternate channel");
    Gossip actual = (Gossip) stack.peek();
    assertEquals(expected, actual);
  }

  public void testId() throws Exception
  {
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "id", attributes);

    Id expected = new Id();
    Id actual = (Id) stack.peek();
    assertEquals(expected, actual);
    assertEquals("unknown", actual.process(null));
  }

  public void testInput() throws Exception
  {
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "input", attributes);
    Input expected = new Input(1, 1);
    Input actual = (Input) stack.peek();
    assertEquals(expected, actual);

    attributes = new AttributesImpl();
    attributes.addAttribute(null, "input", null, "String", "2");
    handler.startElement(null, null, "input", attributes);
    expected = new Input(2, 1);
    actual = (Input) stack.peek();
    assertEquals(expected, actual);

    attributes = new AttributesImpl();
    attributes.addAttribute(null, "input", null, "String", "2, 3");
    handler.startElement(null, null, "input", attributes);
    expected = new Input(2, 3);
    actual = (Input) stack.peek();
    assertEquals(expected, actual);
  }

  public void testJavascript() throws Exception
  {
    char[] text;
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "javascript", attributes);
      handler.characters(text = toCharArray("Anything can go here"), 0, text.length);
    handler.endElement(null, null, "javascript");

    Javascript expected = new Javascript("Anything can go here");
    Javascript actual = (Javascript) stack.peek();
    assertEquals(expected, actual);
    assertFalse(expected.equals(new Javascript("Anything else can go here")));

//    assertEquals("", actual.process(null));
  }

  public void testLearn() throws Exception
  {
    char[] text;
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "learn", attributes);
      handler.characters(text = toCharArray("http://resource1"), 0, text.length);
    handler.endElement(null, null, "learn");

    Learn expected = new Learn("http://resource1");
    Learn actual = (Learn) stack.peek();
    assertEquals(expected, actual);
  }

  public void testLi() throws Exception
  {
    char[] text;
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "li", attributes);
      handler.characters(text = toCharArray("defaultListItem type li element."), 0, text.length);
    handler.endElement(null, null, "li");

    Li expected = new Li(null, null, "defaultListItem type li element.");
    Li actual = (Li) stack.peek();
    assertEquals(expected, actual);

    attributes = new AttributesImpl();
    attributes.addAttribute(null, null, "value", "String", "test value");
    handler.startElement(null, null, "li", attributes);
      handler.characters(text = toCharArray("valueOnlyListItem type li element."), 0, text.length);
    handler.endElement(null, null, "li");

    expected = new Li(null, "test value", "valueOnlyListItem type li element.");
    actual = (Li) stack.peek();
    assertEquals(expected, actual);

    attributes = new AttributesImpl();
    attributes.addAttribute(null, null, "name", "String", "condition");
    attributes.addAttribute(null, null, "value", "String", "test value");
    handler.startElement(null, null, "li", attributes);
      handler.characters(text = toCharArray("valueOnlyListItem type li element."), 0, text.length);
    handler.endElement(null, null, "li");

    expected = new Li("condition", "test value", "valueOnlyListItem type li element.");
    actual = (Li) stack.peek();
    assertEquals(expected, actual);
  }

  public void testLowercase() throws Exception
  {
    char[] text;
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "lowercase", attributes);
      handler.characters(text = toCharArray("CONVERT THIS TO LOWERCASE"), 0, text.length);
    handler.endElement(null, null, "lowercase");

    Lowercase expected = new Lowercase("CONVERT THIS TO LOWERCASE");
    Lowercase actual = (Lowercase) stack.peek();
    assertEquals(expected, actual);

    assertEquals("convert this to lowercase", actual.process(null));
  }

  public void testPattern() throws Exception
  {
    char[] text;
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "pattern", attributes);
      handler.characters(text = toCharArray("HELLO ALICE"), 0, text.length);
    handler.endElement(null, null, "pattern");

    Pattern expected = new Pattern(" HELLO ALICE ");
    Pattern actual = (Pattern) stack.peek();

    assertEquals(expected, actual);
    assertEquals(expected.hashCode(), actual.hashCode());
  }

  public void testPerson() throws Exception
  {
    char[] text;
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "person", attributes);
      handler.characters(text = toCharArray("I WILL DO WHAT HE SAYS"), 0, text.length);
    handler.endElement(null, null, "person");

    Person expected = new Person("I WILL DO WHAT HE SAYS");
    Person actual   = (Person) stack.peek();
    assertEquals(expected, actual);

    Person differs = new Person("I WILL DO SOMETHING ELSE");
    assertFalse(differs.equals(actual));
  }

  public void testPerson2() throws Exception
  {
    char[] text;
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "person2", attributes);
      handler.characters(text = toCharArray("I WILL DO WHAT YOU SAY"), 0, text.length);
    handler.endElement(null, null, "person2");

    Person2 expected = new Person2("I WILL DO WHAT YOU SAY");
    Person2 actual   = (Person2) stack.peek();
    assertEquals(expected, actual);

    Person2 differs = new Person2("I WILL DO SOMETHING ELSE");
    assertFalse(differs.equals(actual));
  }

  public void testRandom() throws Exception
  {
    char[] text;
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "random", attributes);
      handler.startElement(null, null, "li", attributes);
        handler.characters(text = toCharArray("defaultListItem type li element 1."), 0, text.length);
      handler.endElement(null, null, "li");
      handler.startElement(null, null, "li", attributes);
        handler.characters(text = toCharArray("defaultListItem type li element 2."), 0, text.length);
      handler.endElement(null, null, "li");
      handler.startElement(null, null, "li", attributes);
        handler.characters(text = toCharArray("defaultListItem type li element 3."), 0, text.length);
      handler.endElement(null, null, "li");
    handler.endElement(null, null, "random");

    Random expected = new Random(new Li(null, null, "defaultListItem type li element 1."),
                                 new Li(null, null, "defaultListItem type li element 2."),
                                 new Li(null, null, "defaultListItem type li element 3."));
    Random actual = (Random) stack.peek();
    assertEquals(expected, actual);
  }

  public void testSentence() throws Exception
  {
    char[] text;
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "sentence", attributes);
      handler.characters(text = toCharArray("testing sentence... will this work? hope so! let's see."), 0, text.length);
    handler.endElement(null, null, "sentence");

    Sentence expected = new Sentence("testing sentence... will this work? hope so! let's see.");
    Sentence actual   = (Sentence) stack.peek();
    assertEquals(expected, actual);

    assertEquals("Testing sentence... Will this work? Hope so! Let's see.", actual.process(null));
  }

  public void testSet() throws Exception
  {
    char[] text;
    AttributesImpl attributes = new AttributesImpl();
    attributes.addAttribute(null, "name", null, "String", "predicate1");
    handler.startElement(null, null, "set", attributes);
    handler.characters(text = toCharArray("value"), 0, text.length);
    handler.endElement(null, null, "set");

    Set expected = new Set("predicate1", "value");
    Set actual = (Set) stack.peek();
    assertEquals(expected, actual);
  }

  public void testSize() throws Exception
  {
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "size", attributes);

    Size expected = new Size();
    Size actual = (Size) stack.peek();
    assertEquals(expected, actual);
  }

  public void testSr() throws Exception
  {
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "sr", attributes);
    Srai srai = (Srai) stack.peek();
    assertEquals(new Srai(1), srai);
  }

  public void testSrai() throws Exception
  {
    AttributesImpl attributes = new AttributesImpl();

    handler.startElement(null, null, "srai", attributes);
    handler.startElement(null, null, "star", attributes);
    handler.endElement(null, null, "srai");

    Srai srai = (Srai) stack.peek();
    assertEquals(new Srai(1), srai);
  }

  public void testStar() throws Exception
  {
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "star", attributes);

    assertEquals(new Star(1), stack.peek());

    attributes.addAttribute(null, "index", null, "String", "2");
    handler.startElement(null, null, "star", attributes);
    assertEquals(new Star(2), stack.peek());
  }

  public void testSystem() throws Exception
  {
    char[] text;
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "system", attributes);
    handler.characters(text = toCharArray("system = \"Hello System!\""), 0, text.length);
    handler.endElement(null, null, "system");

    System tag = (System) stack.peek();
    assertEquals(new System("system = \"Hello System!\""), tag);
  }

  public void testTemplate() throws Exception
  {
    char[] text;
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "template", attributes);
      handler.characters(text = toCharArray("Hello "), 0, text.length);
      handler.startElement(null, null, "star", attributes);
      handler.characters(text = toCharArray(",\nnice to meet you, too."), 0, text.length);
    handler.endElement(null, null, "template");

    Template tag = (Template) stack.peek();
    assertEquals(new Template("Hello ", new Star(1), ", nice to meet you, too."), tag);
  }

  public void testTemplatePreserveWhitespace() throws Exception
  {
    char[] text;
    AttributesImpl attributes = new AttributesImpl();
    attributes.addAttribute(null, "space", "xml:space", "String", "preserve");

    handler.startElement(null, null, "template", attributes);
      handler.characters(text = toCharArray("   Hello unknown person,\nnice to meet you."), 0, text.length);
    handler.endElement(null, null, "template");

    Template tag = (Template) stack.peek();
    assertEquals(new Template("   Hello unknown person,\nnice to meet you."), tag);
  }

  public void testThat() throws Exception
  {
    AttributesImpl attributes = new AttributesImpl();
    attributes.addAttribute(null, "index", null, "String", "1, 2");
    handler.startElement(null, null, "that", attributes);

    That expected = new That(1, 2);
    That actual = (That) stack.peek();
    assertEquals(expected, actual);
  }

  public void testThatstar() throws Exception
  {
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "thatstar", attributes);

    Thatstar expected = new Thatstar(1);
    Thatstar actual = (Thatstar) stack.peek();
    assertEquals(expected, actual);

    attributes = new AttributesImpl();
    attributes.addAttribute(null, "index", null, "String", "2");
    handler.startElement(null, null, "thatstar", attributes);

    expected = new Thatstar(2);
    actual = (Thatstar) stack.peek();
    assertEquals(expected, actual);
  }

  public void testThink() throws Exception
  {
    char[] text;
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "think", attributes);
      handler.characters(text = toCharArray("Thinking..."), 0, text.length);
    handler.endElement(null, null, "think");

    Think expected = new Think("Thinking...");
    Think actual = (Think) stack.peek();
    assertEquals(expected, actual);

    assertEquals("", actual.process(null));
  }

  public void testTopic() throws Exception
  {
    char[] text;
    AttributesImpl attributes = new AttributesImpl();
    AttributesImpl topicAtts = new AttributesImpl();
    topicAtts.addAttribute(null, "name", null, "String", "TEST TOPIC");
    handler.startElement(null, null, "topic", topicAtts);
      handler.startElement(null, null, "category", attributes);
        handler.startElement(null, null, "pattern", attributes);
          handler.characters(text = toCharArray("HELLO ALICE I AM *"), 0, text.length);
        handler.endElement(null, null, "pattern");
        handler.startElement(null, null, "template", attributes);
          handler.characters(text = toCharArray("Hello "), 0, text.length);
          handler.startElement(null, null, "star", attributes);
          handler.characters(text = toCharArray(", nice to meet you."), 0, text.length);
        handler.endElement(null, null, "template");
      handler.endElement(null, null, "category");
    handler.endElement(null, null, "topic");

    Topic actual = (Topic) stack.peek();
    Topic expected = new Topic("TEST TOPIC",
                       new Category(
                         new Pattern("HELLO ALICE I AM *"),
                         new Template("Hello ", new Star(1), ", nice to meet you.")
                       )
                     );
    assertEquals(expected, actual);
    assertEquals("TEST TOPIC", actual.getName());
  }

  public void testTopicstar() throws Exception
  {
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "topicstar", attributes);

    Topicstar expected = new Topicstar(1);
    Topicstar actual = (Topicstar) stack.peek();
    assertEquals(expected, actual);

    attributes = new AttributesImpl();
    attributes.addAttribute(null, "index", null, "String", "2");
    handler.startElement(null, null, "topicstar", attributes);

    expected = new Topicstar(2);
    actual = (Topicstar) stack.peek();
    assertEquals(expected, actual);
  }

  public void testUppercase() throws Exception
  {
    char[] text;
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "uppercase", attributes);
      handler.characters(text = toCharArray("Convert this to uppercase"), 0, text.length);
    handler.endElement(null, null, "uppercase");

    Uppercase expected = new Uppercase("Convert this to uppercase");
    Uppercase actual = (Uppercase) stack.peek();
    assertEquals(expected, actual);

    assertEquals("CONVERT THIS TO UPPERCASE", actual.process(null));
  }

  public void testVersion() throws Exception
  {
    char[] text;
    AttributesImpl attributes = new AttributesImpl();
    handler.startElement(null, null, "version", attributes);

    Version expected = new Version();
    Version actual   = (Version) stack.peek();
    assertEquals(expected, actual);
  }
}
