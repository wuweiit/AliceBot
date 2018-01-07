/*
Copyleft (C) 2005 H�lio Perroni Filho
xperroni@yahoo.com
ICQ: 2490863

This file is part of ChatterBean.

ChatterBean is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

ChatterBean is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with ChatterBean (look at the Documents/ directory); if not, either write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA, or visit (http://www.gnu.org/licenses/gpl.txt).
 */

package bitoflife.chatterbean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bitoflife.chatterbean.aiml.Category;

public class Graphmaster {
	/*
	 * Attributes
	 */

	/* The children of this node. */
	private final Map<String, Graphmaster> children = new HashMap<String, Graphmaster>();

	private int size = 0;
	private Graphmaster parent;
	private Category category;
	private String name; // The name of a node is the pattern element it
							// represents.

	private Graphmaster(String name) {
		this.name = name;
	}

	/**
	 * Constructs a new root node.
	 */
	public Graphmaster() {
	}

	/**
	 * Constructs a new tree, with this node as the root.
	 */
	public Graphmaster(List<Category> categories) {
		append(categories);
	}

	/*
	 * Methods
	 */
	/**
	 * 由于MatchPath(也就是这里的elements参数)是按照：Pattern，that,topic的顺序来的，而
	 * child.parent是反向指定的，所以我感觉，在进行和输入匹配的时候，应该是从topic开始匹配，然后
	 * 才能通过parent来找到他的下一个匹配项。是这样吗？那这个大体的顺序如果是反者匹配的，那里面的元素 是不是在处理的时候也反者来的呢？
	 * 如果是，那我的感觉就是对的，如果不是，那是怎么搞的呢？
	 * 
	 * 这个函数是这样处理的：如果找到了相应的element那么就找下一个，如果没有找到就new一个child
	 * 然后把这个child加进去，在后指定该child的parent。
	 * 
	 * 让我疑惑的是，这个链表是向前的，例如：“你*的主人是谁”的链表形式：你<-*<-的<-主<-人<-是<-谁
	 * 也就是说每一个element都能找到他的parent从而把一条链拎出来，但是这样怎样从头匹配呢？？
	 * 
	 * @param category
	 * @param elements
	 * @param index
	 */
	private void append(Category category, String[] elements, int index) {
		Graphmaster child = children.get(elements[index]);
		if (child == null)
			appendChild(child = new Graphmaster(elements[index]));

		int nextIndex = index + 1;
		if (elements.length <= nextIndex)
			child.category = category;
		else
			child.append(category, elements, nextIndex);// 递归调用。
	}

	private void appendChild(Graphmaster child) {
		children.put(child.name, child);
		child.parent = this;// 这里的this指的是调用该方法的对象，也就是说这里的parent指向前一个child，其实就相当与一个链表。
	}

	/**
	 * <p>
	 * Returns an array with three child nodes, in the following order:
	 * </p>
	 * <ul>
	 * <li>The "_" node;</li>
	 * <li>The node with the given name;</li>
	 * <li>The "*" node.</li>
	 * </ul>
	 * <p>
	 * If any of these nodes can not be found among this node's children, its
	 * position is filled by <code>null</code>.
	 * </p>
	 */
	private Graphmaster[] children(String name) {
		// ##############
		// System.out.println("name:" + name);
		return new Graphmaster[] { children.get("_"), children.get(name),// 这里为什么要得到"_"和“*”呢？？？
				children.get("*") };
	}

	private boolean isWildcard() {
		return ("_".equals(name) || "*".equals(name));
	}

	private Category match(Match match, int index) {
		if (isWildcard()) {
			return matchWildcard(match, index); // 这个分支还不清楚?????
		}
		if (!name.equals(match.getMatchPathByIndex(index)))// 我觉得这个分支没有必要！因为既然能进这个函数，如果不是通配符的话，就一定是相等的。
			return null;

		int nextIndex = index + 1;
		if (match.getMatchPathLength() <= nextIndex)
			return category;

		return matchChildren(match, nextIndex);
	}

	private Category matchChildren(Match match, int nextIndex) {
		Graphmaster[] nodes = children(match.getMatchPathByIndex(nextIndex));
		for (int i = 0, n = nodes.length; i < n; i++) {

			Category category = (nodes[i] != null ? nodes[i].match(match,// 这个node[i]使得对象一个一个往下面传递。
					nextIndex) : null);

			if (category != null)
				return category;
		}

		return null;
	}

	private Category matchWildcard(Match match, int index) {
		int n = match.getMatchPathLength();
		for (int i = index; i < n; i++) {
			Category category = matchChildren(match, i);
			if (category != null) {
				match.appendWildcard(index, i);// 这个函数的功能就是找出，*号所匹配的内容（包括input中，that中，还有topic中）
				return category;
			}
		}

		if (category != null)
			match.appendWildcard(index, n);
		return category;
	}

	public void append(List<Category> categories) {
		for (Category category : categories)
			append(category);
	}

	public void append(Category category) {
		String matchPath[] = category.getMatchPath();
		// for (String string : matchPath) {
		// System.out.print(string + "|");
		// }
		// System.out.println("--------matchPath:");
		// for (String string : matchPath) {
		// System.out.println(string);
		// }
		append(category, matchPath, 0);
		size++;
	}

	/**
	 * Returns the Catgeory which Pattern matches the given Sentence, or
	 * <code>null</code> if it cannot be found.
	 */
	public Category match(Match match) {
		return matchChildren(match, 0);
	}

	public int size() {
		return size;
	}

	public String toString() {
		return "[name]:" + name;
	}
}
