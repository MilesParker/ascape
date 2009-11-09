package org.ascape.runtime.swing;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Scapes that wish to modify the Navigator tree should implement this
 * interface.
 * 
 * @author Oliver Mannion
 * 
 */
public interface TreeModifier {

	/**
	 * Modify the Ascape Navigator tree to include nodes for datasets and output
	 * tables.
	 * 
	 * Scapes that implement this method with have it called by the {@code
	 * org.ascape.runtime.swing.Navigator.ScapeNode} constructor during the
	 * construction of the Navigator tree. This happens after
	 * {@link #createScape()}.
	 * 
	 * @param tree
	 *            the Navigator tree
	 * @param parentNode
	 *            this scape's node in the Navigator tree
	 */
	void modifyTree(JTree tree, DefaultMutableTreeNode parentNode);

}
