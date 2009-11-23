package org.ascape.runtime.swing.navigator;

import org.ascape.model.Scape;

/**
 * The Class ScapeGroupNode.
 */
public class ScapeGroupNode extends ScapeMembersNode {

	/**
     * 
     */
	private static final long serialVersionUID = 3489960978154121203L;

	/**
	 * The start index.
	 */
	int startIndex;

	/**
	 * The end index.
	 */
	int endIndex;

	/**
	 * Instantiates a new scape group node.
	 * 
	 * @param scape
	 *            the scape
	 * @param treeBuilder
	 *            tree builder for creating children
	 * @param startIndex
	 *            the start index
	 * @param endIndex
	 *            the end index
	 */
	public ScapeGroupNode(Scape scape, TreeBuilder treeBuilder,
			int startIndex, int endIndex) {
		super(scape, treeBuilder, desc(startIndex, endIndex));
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}

	/**
	 * Update this group's children.
	 */
	@Override
	protected void updateChildren() {
		updateChildren(startIndex, endIndex);
	}
}
