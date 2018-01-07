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

/**
Interpreter for Beanshell scripts.
*/
public class BeanshellInterpreter implements Interpreter
{
  /*
  Attribute Section
  */
  
  /** Beanshell interpreter. */
  private final bsh.Interpreter interpreter = new bsh.Interpreter();

  /*
  Method Section
  */
  
  public Object evaluate(String script) throws InterpretingException
  {
    try
    {
      return interpreter.eval(script);
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
      return interpreter.get(name);
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
      interpreter.set(name, value);
    }
    catch (Exception e)
    {
      throw new InterpretingException(e);
    }
  }
}
