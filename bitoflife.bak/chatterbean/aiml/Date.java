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

import java.text.SimpleDateFormat;

import org.xml.sax.Attributes;

import bitoflife.chatterbean.Match;

public class Date extends TemplateElement {
	/*
	 * Attributes
	 */

	private final SimpleDateFormat format = new SimpleDateFormat();

	/** date tag format value, add by lcl **/
	private String formatStr = "";

	/*
	 * Constructors
	 */

	public Date() {
	}

	public Date(Attributes attributes) {
		formatStr = attributes.getValue(0);
	}

	/*
	 * Methods
	 */

	public int hashCode() {
		return 13;// 直接return一个数字，不是吧？？？
	}

	public String process(Match match) {
		try {
			format.applyPattern(formatStr);
			return format.format(new java.util.Date());
		} catch (Exception e) {
			return defaultDate(match);// 可以自己处理的异常，就不要抛出去。
		}
	}

	private String defaultDate(Match match) {
		try {
			format.applyPattern((String) match.getCallback().getContext()
					.property("predicate.dateFormat"));
			return format.format(new java.util.Date());
		} catch (NullPointerException e) {
			return "";
		}
	}

}
