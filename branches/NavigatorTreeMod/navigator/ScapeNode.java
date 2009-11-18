package org.ascape.runtime.swing.navigator;

import javax.swing.tree.DefaultMutableTreeNode;

import org.ascape.model.Scape;

/**
 * The Class ScapeNode.
 */
public class ScapeNode extends DefaultMutableTreeNode {

	/**
     * 
     */
	private static final long serialVersionUID = -7469682418324424534L;

	/**
	 * Instantiates a new scape node.
	 * 
	 * @param scape
	 *            the scape
	 * @param treeBuilder
	 *            tree builder for creating children
	 */
	public ScapeNode(final Scape scape, TreeBuilder treeBuilder) {
		super(scape);
		add(new PropertiesNode(scape));
		add(new RulesNode(scape, treeBuilder.getTreeModel()));
		if (scape instanceof Scape) {
			ScapeMembersNode membersNode =
					new ScapeMembersNode(scape, treeBuilder);
			add(membersNode);
		}
	}
}
