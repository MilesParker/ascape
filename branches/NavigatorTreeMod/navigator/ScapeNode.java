package org.ascape.runtime.swing.navigator;

import org.ascape.model.Scape;

/**
 * The Class ScapeNode.
 */
public class ScapeNode extends BaseNode {

	/**
     * 
     */
	private static final long serialVersionUID = -7469682418324424534L;

	/**
	 * Instantiates a new scape node.
	 * 
	 * @param scape
	 *            the scape
	 */
	public ScapeNode(final Scape scape, TreeBuilder treeBuilder) {
		super(scape);
		add(new PropertiesNode(scape));
		add(new RulesNode(scape, treeBuilder.getTreeModel()));
		if (scape instanceof Scape) {
			ScapeMembersNode membersNode =
					new ScapeMembersNode(scape, treeBuilder);
			add(membersNode);

			// if scape can modify the tree, call the modifyTree method
			// of the scape on the AWT Event thread
/*			if (scape instanceof TreeModifier) {
				Runnable doWorkRunnable = new Runnable() {
					public void run() {
						((TreeModifier) scape).modifyTree(tree,
								(DefaultMutableTreeNode) ScapeNode.this);
					}
				};
				SwingUtilities.invokeLater(doWorkRunnable);
			}
*/
		}
	}
}
