/*
Copyleft (C) 2005 H�lio Perroni Filho
xperroni@yahoo.com
ICQ: 2490863

This file is part of ChatterBean.

ChatterBean is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

ChatterBean is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with ChatterBean (look at the Documents/ directory); if not, either write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA, or visit (http://www.gnu.org/licenses/gpl.txt).
 */

package bitoflife.chatterbean.aiml;

import org.xml.sax.Attributes;

import bitoflife.chatterbean.AliceBot;
import bitoflife.chatterbean.Context;
import bitoflife.chatterbean.Match;
import bitoflife.chatterbean.text.Transformations;

public class Condition extends TemplateElement {
	/*
	 * Attributes
	 */

	private String name;
	private String value;

	/*
	 * Constructors
	 */

	public Condition() {
	}

	public Condition(Attributes attributes) {
		name = attributes.getValue("name");
		value = attributes.getValue("value");
	}

	public Condition(String name, String value, Object... children) {
		super(children);
		this.name = name;
		this.value = value;
	}

	/*
	 * Methods
	 */

	private Li find(Context context, Transformations transformations) {
		for (AIMLElement child : getChildren()) {
			Li li = (Li) child;
			String comparing = li.getValue();
			if (comparing == null)
				return li;

			String compared = (String) context.property("predicate."
					+ li.getName());
			if ("".equals(compared))
				continue;

			comparing = transformations.normalization(comparing);
			compared = transformations.normalization(compared);
			// java.lang.System.out.println("comparing:"
			// + comparing.replace(" ", "#"));
			// java.lang.System.out.println("compared:"
			// + compared.replace(" ", "#"));
			if (comparing.equals(compared))
				return li;
		}

		return null;
	}

	private Li find(String compared, Transformations transformations) {
		for (TemplateElement child : getChildren()) {
			Li li = (Li) child;
			String comparing = li.getValue();
			if (comparing == null) // 从这里可以看出，加入碰到一个没有value的<li>，那么就返回该值。
				return li;

			comparing = transformations.normalization(comparing);
			if (comparing.equals(compared))
				return li;
		}

		return null;
	}

	// 这里有一个问题：一个函数怎样合理的检查传进来的参数？
	public String process(Match match) {
		AliceBot bot = match.getCallback();
		Context context = bot.getContext();
		Transformations transformations = context.getTransformations();

		String compared = (String) context.property("predicate." + name);// 如果name为null怎么办？？？
		compared = transformations.normalization(compared);
		if (value != null) {
			String comparing = transformations.normalization(value);
			return (comparing.equals(compared) ? super.process(match) : "");
		} else if (name != null) {
			Li li = find(compared, transformations);
			return (li != null ? li.process(match) : "");
		} else {
			Li li = find(context, transformations);
			return (li != null ? li.process(match) : "");
		}
	}

	/*
	 * Properties
	 */

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
