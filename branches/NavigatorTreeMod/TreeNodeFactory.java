package org.ascape.runtime.swing;

import javax.swing.tree.TreeNode;

import org.ascape.model.AscapeObject;

public interface TreeNodeFactory {

	TreeNode getTreeNode(AscapeObject modelElement);
}
