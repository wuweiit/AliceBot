/*
Copyleft (C) 2005 Hélio Perroni Filho
xperroni@yahoo.com
ICQ: 2490863

This file is part of ChatterBean.

ChatterBean is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

ChatterBean is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with ChatterBean (look at the Documents/ directory); if not, either write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA, or visit (http://www.gnu.org/licenses/gpl.txt).
*/

package bitoflife.chatterbean.script;

import netscape.javascript.JSObject;

import java.applet.Applet;

/**
Interpreter for Javascript scripts.
*/
public class JavascriptInterpreter implements Interpreter
{
  /*
  Attribute Section
  */
  
  /** Reference to the Java Applet containing the AliceBot. */
  private final Applet applet;

  /*
  Constructor Section
  */
  
  /**
  Creates a new Javascript interpreter.
  
  @param window Reference to the Java Applet containing the AliceBot.
  */
  public JavascriptInterpreter(Applet applet)
  {
    this.applet = applet;
  }

  /*
  Method Section
  */
  
  /**
  Returns a reference to the callback object for the Javascript environment.
  
  @return Callback object for the Javascript environment.
  */
  private JSObject window()
  {
    return JSObject.getWindow(applet);
  }
  
  public Object evaluate(String script) throws InterpretingException
  {
    try
    {
      return window().eval(script);
    }
    catch (Exception e)
    {
      throw new InterpretingException(e);
    }
  }
  
  public Object variable(String name) throws InterpretingException
  {
    try
    {
      return window().getMember(name);
    }
    catch (Exception e)
    {
      throw new InterpretingException(e);
    }
  }
  
  public void variable(String name, Object value) throws InterpretingException
  {
    try
    {
      window().setMember(name, value);
    }
    catch (Exception e)
    {
      throw new InterpretingException(e);
    }
  }
}
