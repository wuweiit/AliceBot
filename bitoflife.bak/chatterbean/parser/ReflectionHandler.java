/*
Copyleft (C) 2005 H�lio Perroni Filho
xperroni@yahoo.com
ICQ: 2490863

This file is part of ChatterBean.

ChatterBean is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

ChatterBean is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with ChatterBean (look at the Documents/ directory); if not, either write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA, or visit (http://www.gnu.org/licenses/gpl.txt).
 */

package bitoflife.chatterbean.parser;

import java.lang.reflect.Method;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/*
 * 这个控制器其实根本就没有对xml配置文件做什么。
 */
public class ReflectionHandler extends DefaultHandler {
	/*
	 * Attributes
	 */

	private ReflectionBuilder builder = null;

	/*
	 * Constructor
	 */

	public ReflectionHandler() {
	}

	public ReflectionHandler(ReflectionBuilder builder) {
		this.builder = builder;
	}

	/*
	 * Methods
	 */

	/*
	 * 这个函数写的厉害，这里用到了反射机制。如果不用反射机制，那么就要用分支语句把所有可能的qname列举出来
	 * 分别处理，但是现在通过反射机制，想要调用什么，只要构造函数名就能调用了。
	 */

	public void startElement(String namespace, String name, String qname,
			Attributes attributes) {
		try {
			String methodName = "start" + qname.substring(0, 1).toUpperCase()
					+ qname.substring(1);
			Method event = builder.getClass().getMethod(methodName,
					Attributes.class);
			event.invoke(builder, attributes);
		} catch (NoSuchMethodException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// substitutions.xml中没有文本内容，所以可以没有这个方法。
	public void characters(char[] chars, int start, int length) {
		builder.characters(chars, start, length);
	}

	public void endElement(String namespace, String name, String qname) {
		try {
			String methodName = "end" + qname.substring(0, 1).toUpperCase()
					+ qname.substring(1);
			// System.out.println("methodname:" + methodName);
			Method event = builder.getClass().getMethod(methodName);
			event.invoke(builder);
		} catch (NoSuchMethodException e) {// 实际上根本就没有这个end的方法，这里会出现异常的。但是作者把NoSuchMethodException这个异常给隐藏起来了，有没有搞错啊。
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Properties
	 */

	public ReflectionBuilder getReflectionBuilder() {
		return builder;
	}

	public void setReflectionBuilder(ReflectionBuilder builder) {
		this.builder = builder;
	}
}
