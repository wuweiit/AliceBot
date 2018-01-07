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

public class Set extends TemplateElement {
	/*
	 * Attributes
	 */

	private String name;

	/*
	 * Constructors
	 */

	public Set(Attributes attributes) {
		name = attributes.getValue(0);
	}

	public Set(String name, Object... children) {
		super(children);
		this.name = name;
	}

	/*
	 * Methods
	 */

	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		Set compared = (Set) obj;
		if (!name.equals(compared.name))
			return false;
		return super.equals(compared);
	}

	public String process(Match match) {
		// for (String str : match.getMatchPath()) {
		// java.lang.System.out.println(str);
		// }
		String output = super.process(match);
		if (match == null) // 这里往往会做一些参数的检查。
			output = "<set name=\"" + name + "\">" + output + "</set>";
		else {
			AliceBot bot = match.getCallback();
			Context context = (bot != null ? bot.getContext() : null);// 时时刻刻都在检查变量的合法性。
			if (context != null)
				context.property("predicate." + name, output);// 这里应该会触发propertyListener
		}
		// java.lang.System.out.println("#name:" + name);
		// java.lang.System.out.println("#output:" + output);
		return output;
	}
}
