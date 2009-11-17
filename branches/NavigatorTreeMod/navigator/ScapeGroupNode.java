package org.ascape.runtime.swing.navigator;

import javax.swing.tree.DefaultTreeModel;

import org.ascape.model.Scape;
import org.ascape.util.ResetableIterator;

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
	 * The iterator.
	 */
	ResetableIterator iterator;

	/**
	 * Instantiates a new scape group node.
	 * 
	 * @param scape
	 *            the scape
	 * @param startIndex
	 *            the start index
	 * @param endIndex
	 *            the end index
	 */
	public ScapeGroupNode(Scape scape, DefaultTreeModel treeModel, int startIndex, int endIndex) {
		super(scape, treeModel, desc(startIndex, endIndex));
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

