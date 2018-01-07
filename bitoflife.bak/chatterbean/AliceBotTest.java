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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import junit.framework.TestCase;
import bitoflife.chatterbean.aiml.Category;
import bitoflife.chatterbean.text.Request;
import bitoflife.chatterbean.text.Response;
import bitoflife.chatterbean.text.Sentence;

import static bitoflife.chatterbean.text.TransformationsTest.assertRequest;
import static bitoflife.chatterbean.text.TransformationsTest.assertSentence;

public class AliceBotTest extends TestCase
{
  /*
  Attribute Section
  */

  private final AliceBotMother mother = new AliceBotMother();
  
  private AliceBot bot;

  /*
  Event Section
  */

  public void setUp() throws Exception
  {
    mother.setUp();
    bot = mother.newInstance();
  }

  /*
  Method Section
  */

  public void testStar()
  {
    assertEquals("Yes, I see the fire in your eyes.", bot.respond("Do you see the fire in my eyes?"));
    assertEquals("My name is Alice, nice to meet you!", bot.respond("What is your name?"));
    assertEquals("Sorry, I don't know what a chatterbot is.", bot.respond("What is a chatterbot?"));
    assertEquals("Yes, I am an ALICE Bot.", bot.respond("Are you a bot, Alice?"));
    
    String expected = "What makes you think that if I am a human then you are a chatterbot?";
    String actual = bot.respond("If you are a human then I am a chatterbot.");
    assertEquals(actual, expected, actual);
  }
  
  public void testLastStar() 
  {
    String expected = "I am sorry, my answers are limited -- you must provide the right questions.";
    String actual = bot.respond("What are you?");
    assertEquals(expected, actual);    
  }
  
  public void testEmptyStar()
  {
    String expected = "Why, you're welcome!";
    String actual = bot.respond("Thank you!");
    assertEquals(expected, actual);
    
    expected = "Yes, I am happy for being christmas as well.";
    
    // Somewhat an incorrect sentence, but makes the point of the test case.
    actual = bot.respond("I am happy is christmas.");
    assertEquals(expected, actual);
  }
  
  public void testEmptySentence()
  {
    String expected = "Why, you're welcome!";
    String actual = bot.respond("...thank you.");
    assertEquals(expected, actual);    
  }

  public void testThat()
  {
    assertEquals("Hello Unknown Person! My name is Alice, who are you?", bot.respond("Hello?"));

    Response expected =
      new Response(" I said \"Hello Unknown Person!\". ",
        new Sentence(" I said \"Hello Unknown Person!\". ",
                     new Integer[] {0, 2, 7, 14, 22, 32},
                     " I SAID HELLO UNKNOWN PERSON "));
    Response actual = bot.respond(new Request("What did you just say?"));
    assertRequest(expected, actual);
  }

  public void testSrai()
  {
    assertEquals("Hello Unknown Person! My name is Alice, who are you?", bot.respond("HELLO"));
    assertEquals("Hello Unknown Person! My name is Alice, who are you?", bot.respond("Hi ya!"));
    assertEquals("Once more? \"that\".", bot.respond("You may say that again, Alice."));
  }

  public void testGossip()
  {
    String expected = "Nice to meet you, Makoto. :-) I am Alice, nice to meet you!";
    String actual = bot.respond("I am Makoto. Who are you?");
    assertEquals(actual, expected, actual);

    String gossip = mother.gossip();
    assertEquals(gossip, "Makoto logged in.\n", gossip);
  }

  public void testPredicates()
  {
    assertEquals("Nice to meet you, Hélio. :-)", bot.respond("My name is Hélio."));
    assertEquals("Hélio", (String) bot.getContext().property("predicate.name"));
    assertEquals("Thank you, Hélio.", bot.respond("Nice to meet you, too."));
    
    assertEquals("Nice to meet you, Green Lantern. :-)", bot.respond("I am called Green Lantern."));
    assertEquals("'kay. Nice to meet you, Freiya. :-) What's up?", bot.respond("Name's Freiya."));
    assertEquals("My engine is an Alpha series ChatterBean engine.", bot.respond("What series your engine is?"));
  }

  public void testSystem()
  {
    assertEquals("Hello System", bot.respond("Print this: Hello System"));
  }

  public void testSize()
  {
    int size = bot.getGraphmaster().size();
    assertEquals("I currently contain " + size + " categories.", bot.respond("What size are you?"));
  }

  public void testDate()
  {
    String date = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
    assertEquals("Now is " + date + ".", bot.respond("What time is now?"));
  }

  public void testInput()
  {
    String date = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
    assertEquals("Now is " + date + ".", bot.respond("What time is now?"));

    assertEquals("You said \"What time is now?\".", bot.respond("What did I just say?"));
  }
  
  public void testCondition()
  {
    assertEquals("The block condition is working.", bot.respond("First block condition test."));
    assertEquals("The block condition is working.", bot.respond("Second block condition test."));
    assertEquals("The block condition is working.", bot.respond("Third block condition test."));
    assertEquals("The block condition is working.", bot.respond("Fourth block condition test."));
    assertEquals("The block condition is working.", bot.respond("Fifth block condition test."));
    assertEquals("The block condition is working.", bot.respond("Sixth block condition test."));
  }

  public void testEmpty()
  {
    assertEquals("", bot.respond((String) null));
    assertEquals("", bot.respond("    "));
    assertEquals("", bot.respond(""));
  }

  public void testId()
  {
    assertEquals("My id is test_cases.", bot.respond("What is your id?"));
  }

  public void testLearn()
  {
    /* The third slash was added to make the test work under windows. Fortunately it is ignored by UNIX systems. */
    String url = "file:///" + System.getProperty("user.dir") + "/Bots/Alice/Learn/Learned.aiml";
    assertEquals("Ok.", bot.respond("Learn this resource: " + url));
    assertEquals("Yes, I have learned a new Category.", bot.respond("Have you learned anything?"));
  }

  public void testPerson()
  {
    assertEquals("he or she will do the test for me.", bot.respond("Do the person test."));
  }

  public void testPerson2()
  {
    assertEquals("I will do the test for you.", bot.respond("Do the person2 test."));
  }

  public void testGender()
  {
    assertEquals("she will do the test for him.", bot.respond("Do the gender test."));
  }

  public void testRandom()
  {
    String[] responses = {"This is the first random example.",
                          "This is the second random example.",
                          "This is the third random example."};

    Context context = bot.getContext();
    long seed = Long.parseLong((String) context.property("bot.randomSeed"));
    Random random = new Random(seed);

    for (int i = 0; i < 100; i++)
      assertEquals(responses[random.nextInt(3)], bot.respond("Do the Random example."));
  }

  public void testThatTopic()
  {
    assertEquals("Do you like cheese?", bot.respond("What do you want to know?"));

    Sentence expected = new Sentence(" Do you like cheese? ",
                                     new Integer[] {0, 3, 7, 12, 20},
                                     " DO YOU LIKE CHEESE ");
    Sentence that = bot.getContext().getThat();
    assertSentence(expected, that);

    expected = new Sentence(" like ", new Integer[] {0, 5}, " LIKE ");
    Sentence topic = bot.getContext().getTopic();
    assertSentence(expected, topic);

    assertEquals("Good for you.", bot.respond("Yes."));
  }

  public void testThatstar()
  {
    assertEquals("Ok. It'll be a bit silly, though.", bot.respond("Do the thatstar example."));

    Sentence expected = new Sentence(" It'll be a bit silly, though. ",
                                     new Integer[] {0, null, 6, 9, 11, 15, 22, 30},
                                     " IT WILL BE A BIT SILLY THOUGH ");
    Sentence actual = bot.getContext().getThat();
    assertSentence(expected, actual);

    assertEquals("This kind of arranged dialogue is always a bit silly.", bot.respond("Why so?"));
  }

  public void testTopicstar()
  {
    assertEquals("Alright. How is the weather today?", bot.respond("Do the topicstar example."));
    assertEquals("So is it going to be a rainy day?", bot.respond("The weather is rainy."));
    assertEquals("What do you think of rainy days?", bot.respond("I think so."));
  }

  public void testVersion()
  {
    assertEquals("My current version is 0.7.5 Alpha.", bot.respond("What is your version?"));
  }
}
