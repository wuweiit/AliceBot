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

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.UNICODE_CASE;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides operations for normalizing a request, before submiting it to the
 * matching operation.
 */
public class Transformations {
	/*
	 * Inner Classes
	 */

	private class Mapper {
		/*
		 * Attributes
		 */

		private int charIndex;
		private int listIndex;
		private int spaceCount;

		private final List<Integer> mappings = new LinkedList<Integer>();

		private String input;
		private String find;
		private String replace;

		/*
		 * Constructor
		 */

		public Mapper(String input) {
			char[] chars = input.toCharArray();
			for (int i = 0, n = chars.length; i < n; i++)
				if (chars[i] == ' ')
					mappings.add(i);// 把空格的位置记录了下来。有什么用呢？？？？
		}

		/*
		 * Methods
		 */

		private int spaceCount(String string) {
			return spaceCount(string, 0, string.length());
		}

		private int spaceCount(String string, int beginIndex, int endIndex) {
			int spaces = 0;
			char[] chars = string.toCharArray();
			for (int i = beginIndex, n = endIndex; i < n; i++)
				if (chars[i] == ' ')
					spaces++;
			return spaces;
		}

		public void prepare(String input, String find, String replace) {
			this.input = input;
			this.find = find;
			this.replace = replace;
			// System.out.println("*****input:" + input);
			// System.out.println("*****find:" + input);
			// System.out.println("*****replace:" + input);
			spaceCount = spaceCount(find);
			// System.out.println("*****spaceCount:" + spaceCount);
			listIndex = 0;
			charIndex = 0;
		}

		public void update(int beginIndex) {
			listIndex += spaceCount(input, charIndex, beginIndex);
			charIndex = beginIndex;

			int n = spaceCount;
			for (int j = 0, m = replace.length(); j < m; j++)
				if (replace.charAt(j) == ' ' && --n < 0)
					mappings.add(listIndex++, null);

			while (n-- > 0 && mappings.size() > listIndex)
				// fixed by lcl
				mappings.remove(listIndex);
		}

		public Integer[] toArray() {
			return mappings.toArray(INTEGER_ARRAY);
		}
	}

	/*
	 * Attribute Section
	 */

	private static final Integer[] INTEGER_ARRAY = new Integer[0];

	private final Tokenizer tokenizer;
	private final Pattern fitting = Pattern.compile("[^A-Z0-9\u4E00-\u9FA5]+"); // 修改正则表达式使其支持中文。
	private final Pattern wordBreakers = Pattern
			.compile("([,，;；:：])([A-Za-z\u4E00-\u9FA5]|\\s{2,})");

	// The regular expression which will split entries by sentence splitters.
	private final SentenceSplitter splitter;

	// The collection of substitutions known to the system.
	private Map<String, String> correction;
	private Map<String, String> protection;
	private List<Substitution> person;
	private List<Substitution> person2;
	private List<Substitution> gender;

	/*
	 * Constructor Section
	 */

	/**
	 * Constructs a new Transformations out of a list of sentence splitters and
	 * several substitution maps.
	 */
	public Transformations(List<String> splitters,
			Map<String, Map<String, String>> substitutions, Tokenizer tokenizer) {
		this.tokenizer = tokenizer;
		this.splitter = new SentenceSplitter(substitutions.get("protection"),// 不知道protection是用来干什么的？？？
				splitters);

		correction = substitutions.get("correction");
		person = newSubstitutionList(substitutions.get("person"));
		person2 = newSubstitutionList(substitutions.get("person2"));
		gender = newSubstitutionList(substitutions.get("gender"));

	}

	/*
	 * Method Section
	 */

	private List<Substitution> newSubstitutionList(Map<String, String> inputs) {
		List<Substitution> subsitutions = new ArrayList<Substitution>(
				inputs.size());
		for (Entry<String, String> entry : inputs.entrySet()) {
			Substitution substitution = new Substitution(entry.getKey(),
					entry.getValue(), tokenizer);
			subsitutions.add(substitution);
		}

		return subsitutions;
	}

	/**
	 * "，(1)I'm wangxiaolong, (2)who are you:  (3)Do you know me;"
	 * 如果标点符号后面直接跟字符，或跟2个以上的空格，那么就匹配成功了，比如1，3位置。
	 * 
	 * @param input
	 * @return
	 */
	private String breakWords(String input) {
		/*
		 * See the description of java.util.regex.Matcher.appendReplacement() in
		 * the Javadocs to understand this code.
		 */
		Matcher matcher = wordBreakers.matcher(input);// 全局匹配
		StringBuffer buffer = new StringBuffer();
		while (matcher.find()) { // 局部匹配
			String replace = matcher.group(2);
			if (replace.charAt(0) != ' ')
				replace = matcher.group(1) + ' ' + replace;
			else
				replace = matcher.group(1) + ' ';

			matcher.appendReplacement(buffer, replace);
		}

		matcher.appendTail(buffer);
		return buffer.toString();
	}

	private String fit(String input) {
		input = input.toUpperCase();
		Matcher matcher = fitting.matcher(input);
		return matcher.replaceAll(" ");
	}

	/**
	 * Turns the entry to UPPERCASE, takes sequences of non-alphanumeric
	 * characters out of it (replacing them with a single whitespace) and sees
	 * that the entry is trimmed off leading and trailing whitespaces.
	 */
	private String fit(String input, Mapper mapper) {

		input = input.toUpperCase();

		Matcher matcher = fitting.matcher(input);
		StringBuffer buffer = new StringBuffer();
		while (!matcher.hitEnd() && matcher.find()) {
			mapper.prepare(input, matcher.group(), " ");
			mapper.update(matcher.start());
			matcher.appendReplacement(buffer, " ");
		}

		matcher.appendTail(buffer);
		return buffer.toString();
	}

	private String substitute(String input) {
		for (String find : correction.keySet()) {
			Pattern pattern = Pattern.compile(find, CASE_INSENSITIVE
					| UNICODE_CASE);
			Matcher matcher = pattern.matcher(input);
			String replace = correction.get(find);

			input = matcher.replaceAll(replace);
		}

		return input;
	}

	private String substitute(String input, Mapper mapper) {
		StringBuffer buffer = new StringBuffer();
		for (String find : correction.keySet()) {
			Pattern pattern = Pattern.compile(find, CASE_INSENSITIVE
					| UNICODE_CASE);
			Matcher matcher = pattern.matcher(input);
			String replace = correction.get(find);

			mapper.prepare(input, find, replace);
			while (!matcher.hitEnd() && matcher.find()) {
				mapper.update(matcher.start() + 1);
				matcher.appendReplacement(buffer, replace);
			}

			matcher.appendTail(buffer);
			input = buffer.toString();
			buffer.delete(0, buffer.length());
		}

		return input;
	}

	// 这个函数还不明白？？？？？
	private String transform(String input, List<Substitution> substitutions) {
		List<String> tokens = tokenizer.tokenize(input);
		// fixed by lcl
		if (tokens.size() == 0)
			return "";

		outer: for (int i = 0; i < tokens.size();) {
			int offset = i;
			for (final Substitution substitution : substitutions) {
				i = substitution.substitute(offset, tokens);
				if (i > offset)
					continue outer; //
			}
			// Only gets here if no substitution matches.
			i++;
		}

		return tokenizer.toString(tokens);
	}

	// outer的测试代码。
	// public class TestLable {
	// public static void main(String[] args) {
	// outer: for (int i = 0; i < 10;) {
	// System.out.println("\nouter_loop:" + i);
	// inner: for (int k = 0; i < 10; k++) {
	// System.out.println(k + " ");
	// if (k == 4)
	// continue outer;
	// // break outer;
	// }
	// i++;
	// }
	// System.out.println("------>>>所有循环执行完毕！");
	// }
	// }

	/**
	 * 该方法的作用是规范化request。
	 * 
	 * 规范化过程: 1.在原始输入的内容两头加空格。 2.把句子中间的任何2个以上连续的空白字符都替换成一个空格。
	 * 3.将输入内容按照约定好的句子结束符断句，放到Sentence[]中。 4.再将Sentence[i]进行规范化处理
	 * 
	 * @param request
	 */
	public void normalization(Request request) {
		String original = chineseTranslate(request.getOriginal());
		original = ' ' + original + ' ';
		original = original.replaceAll("\\s{2,}", " ");
		String input[] = splitter.split(original);// 这里句子的分隔还有问题，只能分隔英文的句号，不能识别中文的句号？？？？

		Sentence[] sentences = new Sentence[input.length];
		for (int i = 0, n = input.length; i < n; i++) {
			sentences[i] = new Sentence(input[i]);
			normalization(sentences[i]);
		}

		request.setOriginal(original);
		request.setSentences(sentences);
	}

	public void normalization(Sentence sentence) {
		String input = breakWords(sentence.getOriginal());
		// 中文分词
		input = ChineseSegmenter.IKAnalysis(input);

		input = ' ' + input + ' ';
		input = chineseTranslate(input);
		input = input.replaceAll("\\s{2,}", " ");
		sentence.setOriginal(input);

		Mapper mapper = new Mapper(input);
		input = substitute(input, mapper);

		input = fit(input, mapper);
		sentence.setMappings(mapper.toArray());

		sentence.setSplittedOfSentence(input);
	}

	public String normalization(String input) {
		input = chineseTranslate(input);
		input = ' ' + input + ' ';
		input = input.replaceAll("\\s{2,}", " ");
		input = substitute(input);
		input = fit(input);

		return input;
	}

	/**
	 * 该方法将中文字之间加上空格。
	 * 
	 * @param input
	 * @return
	 */
	public static String chineseTranslate(String input) {
		StringBuffer newStr = new StringBuffer("");
		java.util.regex.Pattern p = java.util.regex.Pattern
				.compile("[\u4E00-\u9FA5]");
		char[] chars = new char[1];
		Matcher m;
		for (int i = 0; i < input.length(); i++) {
			chars[0] = input.charAt(i);
			m = p.matcher(new String(chars));
			if (m.matches())
				newStr.append(" ").append(input.charAt(i)).append(" ");
			else
				newStr.append(input.charAt(i));
		}
		// java.lang.System.out.println("#" + newStr.toString());
		// strTemp = newStr.toString().replaceAll("\\s{2,}", " ");//
		// 把2个连续的空格替换成一个空格。
		// strTemp = strTemp.replaceAll("^\\s*|\\s*$", ""); // 把头和尾的空格去掉。
		return newStr.toString();
	}

	public String gender(String input) {
		return transform(input, gender);
	}

	public String person(String input) {
		return transform(input, person);
	}

	public String person2(String input) {
		return transform(input, person2);
	}
}
