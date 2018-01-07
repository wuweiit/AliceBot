/*
Copyleft (C) 2005 H�lio Perroni Filho
xperroni@yahoo.com
ICQ: 2490863

This file is part of ChatterBean.

ChatterBean is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

ChatterBean is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with ChatterBean (look at the Documents/ directory); if not, either write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA, or visit (http://www.gnu.org/licenses/gpl.txt).
 */

package bitoflife.chatterbean.text;

import static bitoflife.chatterbean.util.Escaper.escapeRegex;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bitoflife.chatterbean.config.TokenizerConfig;

public class Tokenizer {
	/*
	 * Attribute Section
	 */

	private Boolean ignoreWhitespace;
	/*
	 * 这里的splitters原则上是包含了splitters.xml中所有的分隔符（包括句子和词之间的分隔符）
	 * 但是实际上这个列表被定义成长度为6，也就是说如果splitters.xml中的配置多于6项，那么 其余的都被忽略了。
	 */
	private String[] splitters;

	private Pattern pattern;

	/*
	 * Constructor Section
	 */

	public Tokenizer() {
	}

	public Tokenizer(String... splitters) {
		setIgnoreWhitespace(true);
		setSplitters(splitters);
	}

	public Tokenizer(TokenizerConfig config) {
		this(config.splitters());
	}

	/*
	 * Event Section
	 */

	/**
	 * 把所有的splitter词用|分割开
	 */
	private void afterSetProperty() {
		if (splitters == null || ignoreWhitespace == null)
			return;

		String expression = "";
		for (int i = 0, n = splitters.length;;) {
			expression += escapeRegex(splitters[i]);
			if (++i >= n)
				break;
			expression += '|';
		}

		// 这个有什么区别还是不太清楚？？？
		if (ignoreWhitespace)
			expression = "(" + expression + ")\\s*|\\s+";
		else
			expression = "(" + expression + "|\\s+)";

		pattern = Pattern.compile(expression);
	}

	/*
	 * Method Section
	 */

	public List<String> tokenize(String input) {
		List<String> output = new ArrayList<String>();
		Matcher matcher = pattern.matcher(input);
		int beginIndex = 0;

		while (matcher.find()) {
			int endIndex = matcher.start();
			String token = input.substring(beginIndex, endIndex);
			if (token.length() > 0)
				output.add(token);

			String symbol = matcher.group(1);
			if (symbol != null)
				output.add(symbol);

			String breaker = matcher.group();
			beginIndex = endIndex + breaker.length();
		}

		if (beginIndex < input.length()) {
			String token = input.substring(beginIndex);
			output.add(token);
		}

		return output;
	}

	public String toString(List<String> tokens) {
		String output = "";
		int i = 0, n = tokens.size();
		String next = tokens.get(0);

		for (;;) {
			output += next;
			if (++i >= n)
				break;
			next = tokens.get(i);
			Matcher matcher = pattern.matcher(next);
			if (!matcher.matches())
				output += ' ';
		}

		return output;
	}

	/*
	 * Property Section
	 */

	public boolean getIgnoreWhitespace() {
		return ignoreWhitespace;
	}

	public void setIgnoreWhitespace(boolean ignore) {
		ignoreWhitespace = ignore;
		afterSetProperty();
	}

	public String[] getSplitters() {
		return splitters;
	}

	public void setSplitters(String[] splitters) {
		this.splitters = splitters;
		afterSetProperty();
	}
}
