package org.ascape.runtime.swing.navigator;

import java.util.Set;

import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import org.ascape.model.AscapeObject;

/**
 * Specifies elements that are needed to build the Navigator tree.
 * 
 * @author Oliver Mannion
 * @version $Revision$
 * 
 */
public interface TreeBuilder {

	/**
	 * Creates a new tree node for a given model element (eg: scape or agent).
	 * 
	 * @param modelElement
	 *            ascape model element
	 * @return tree node
	 */
	MutableTreeNode createTreeNode(AscapeObject modelElement);

	/**
	 * Get the tree model. This may be used by nodes to create children.
	 * 
	 * @return tree model
	 */
	DefaultTreeModel getTreeModel();

	/**
	 * Set the tree model. This must be called before any
	 * {@link #createTreeNode(AscapeObject)} calls.
	 * 
	 * @param treeModel
	 *            tree model
	 */
	void setTreeModel(DefaultTreeModel treeModel);

	/**
	 * Set of {@link TreeSelectionListener}s to add to the Navigator JTree.
	 * 
	 * @return tree selections listeners
	 */
	Set<TreeSelectionListener> getTreeSelectionListeners();

}