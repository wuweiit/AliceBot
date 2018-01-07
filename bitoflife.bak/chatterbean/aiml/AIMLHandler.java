/*
Copyleft (C) 2005 H�lio Perroni Filho
xperroni@bol.com.br
ICQ: 2490863

This file is part of ChatterBean.

ChatterBean is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

ChatterBean is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with ChatterBean (look at the Documents/ directory); if not, either write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA, or visit (http://www.gnu.org/licenses/gpl.txt).
 */

package bitoflife.chatterbean.aiml;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AIMLHandler extends DefaultHandler {
	/*
	 * Attribute Section
	 */

	private final Set<String> ignored = new HashSet<String>();
	final StringBuilder text = new StringBuilder(); // 这个变量被重复使用，而不是每次都new一个。

	private boolean ignoreWhitespace = true;

	/**
	 * The stack of AIML objects is used to build the Categories as AIML
	 * documents are parsed. The scope is defined as package for testing
	 * purposes.
	 */
	final AIMLStack stack = new AIMLStack();


	{
        ignored.add("ul");
        ignored.add("p");
    }

	/*
	 * Constructor Section
	 */

	public AIMLHandler(String... ignore) {
		ignored.addAll(Arrays.asList(ignore));
	}

	/*
	 * Method Section
	 */

	private String buildClassName(String tag) {
		return "bitoflife.chatterbean.aiml."
				+ tag.substring(0, 1).toUpperCase()
				+ tag.substring(1).toLowerCase();
	}

	private void pushTextNode() {
		String pushed = text.toString();
		text.delete(0, text.length());
		if (ignoreWhitespace)
			// 这个正则表达式是不是写的有点累赘啊？？？
			pushed = pushed.replaceAll("^[\\s\n]+|[\\s\n]{2,}|\n", " ");// 转义符到底怎么用？

		if (!"".equals(pushed.trim()))
			stack.push(new Text(pushed));
	}

	private void updateIgnoreWhitespace(Attributes attributes) {
		try {
			ignoreWhitespace = !"preserve".equals(attributes
					.getValue("xml:space"));
		} catch (NullPointerException e) {
		}
	}

	public List<Category> unload() {
		List<Category> result = new LinkedList<Category>();

		Object poped;
		while ((poped = stack.pop()) != null)
			if (poped instanceof Aiml)
				result.addAll(((Aiml) poped).children());
		// for (Category c : result) {
		// java.lang.System.out.println(c.toString());
		// }
		return result;
	}

	/*
	 * Event Handling Section
	 */

	public void startElement(String namespace, String name, String qname,
			Attributes attributes) throws SAXException {


		if (ignored.contains(qname)) // ignored这个变量好像没有被赋值过？？？
			return;
		updateIgnoreWhitespace(attributes);// 这个是干什么用的？
		pushTextNode();
		String className = buildClassName(qname);
		try {
			Class tagClass = Class.forName(className);// 这里使用了反射机制。
			Constructor constructor = tagClass.getConstructor(Attributes.class);
			Object tag = constructor.newInstance(attributes);
			stack.push(tag);
		} catch (Exception e) {
			throw new RuntimeException("Cannot instantiate class " + className,
					e);
		}
	}

	public void characters(char[] chars, int start, int length) {

		text.append(chars, start, length);
	}

	public void endElement(String namespace, String name, String qname)
			throws SAXException {

		if (ignored.contains(qname))
			return;
		pushTextNode();
		ignoreWhitespace = true;
		String className = buildClassName(qname);
		for (List<AIMLElement> children = new LinkedList<AIMLElement>();;) {
			Object tag = stack.pop();
			if (tag == null)
				throw new SAXException("No matching start tag found for "
						+ qname);
			else if (!className.equals(tag.getClass().getName()))// 类名不相等就说明当前弹出来的是文本。
				children.add(0, (AIMLElement) tag);// 在此列表中指定的位置插入指定的元素。移动当前在该位置处的元素（如果有），所有后续元素都向右移（在其索引中添加
													// 1）。
			else
				try {
					if (children.size() > 0)
						((AIMLElement) tag).appendChildren(children);
					stack.push(tag);
					return;
				} catch (ClassCastException e) {
					throw new RuntimeException(
							"Tag <"
									+ qname
									+ "> used as node, but implementing "
									+ "class does not implement the AIMLElement interface",
							e);
				} catch (Exception e) {
					throw new SAXException(e);
				}
		}
	}
}
