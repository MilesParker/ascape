package org.ascape.runtime.swing.navigator;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * A {@link DefaultMutableTreeNode} that lazily creates children when they are
 * needed.
 * <p>
 * Creates children (via abstract method {@link #updateChildren()}) whenever
 * {@link #getChildCount()} is called. This can happen when the following are
 * called:
 * <ul>
 * <li>{@link DefaultTreeModel#nodeStructureChanged(javax.swing.tree.TreeNode)}
 * <li>{@link DefaultMutableTreeNode#add(javax.swing.tree.MutableTreeNode)}
 * <li> {@link JTree#expandPath(javax.swing.tree.TreePath)}
 * </ul>
 * The overall effect is that children are created as nodes are needed, i.e.:
 * when expanded. In addition, more than what is expanded is loaded,
 * particularly the children of the last unexpanded node at the currently
 * expanded level of the hierarchy.
 * 
 * Adapted from com.jidesoft.plaf.basic.LazyMutableTreeNode.
 */
public abstract class LazyMutableTreeNode extends DefaultMutableTreeNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5234855334034342509L;
	
	protected boolean _loaded = false;

	public LazyMutableTreeNode() {
		super();
	}

	public LazyMutableTreeNode(Object userObject) {
		super(userObject);
	}

	public LazyMutableTreeNode(Object userObject, boolean allowsChildren) {
		super(userObject, allowsChildren);
	}

	@Override
	public int getChildCount() {
		synchronized (this) {
			if (!_loaded) {
				_loaded = true;
				updateChildren();
			}
		}
		return super.getChildCount();
	}

	public void clear() {
		removeAllChildren();
		_loaded = false;
	}

	public boolean isLoaded() {
		return _loaded;
	}

	protected abstract void updateChildren();

}
