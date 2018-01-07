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

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SplitterHandler extends DefaultHandler {
	/*
	 * Attribute Section
	 */

	private List<String> splitters;

	/*
	 * Constructor Section
	 */

	public SplitterHandler() {
		splitters = new ArrayList<String>(4);// 这里为什么把他的长度定义为4呢？？？
	}

	public SplitterHandler(List<String> splitters) {
		this.splitters = splitters;
	}

	/*
	 * Event Section
	 */

	// 这是专门把“句子分隔符”挑选出来了。
	public void startElement(String namespace, String name, String qname,
			Attributes attributes) throws SAXException {
		if (qname.equals("splitter")
				&& !"word".equals(attributes.getValue("type")))
			splitters.add(attributes.getValue(0));
	}

	/*
	 * Method Section
	 */

	public void clear() {
		splitters.clear();
	}

	public List<String> parsed() {
		return new ArrayList<String>(splitters);// 这里重新复制了一个列表！
	}
}