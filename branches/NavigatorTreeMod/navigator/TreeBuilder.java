package org.ascape.runtime.swing.navigator;

import java.util.List;

import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.ascape.model.AscapeObject;

public interface TreeBuilder {

	TreeNode getTreeNode(AscapeObject modelElement);
	
	DefaultTreeModel getDefaultTreeModel();
	
	List<TreeSelectionListener> getTreeSelectionListeners();
	
	
}
