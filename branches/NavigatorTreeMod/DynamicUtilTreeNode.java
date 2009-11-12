package org.ascape.runtime.swing;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * <code>DynamicUtilTreeNode</code> can wrap vectors/hashtables/arrays/strings
 * and create the appropriate children tree nodes as necessary. It is dynamic in
 * that it will only create the children as necessary.
 * <p>
 * <strong>Warning:</strong> Serialized objects of this class will not be
 * compatible with future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running the
 * same version of Swing. As of 1.4, support for long term storage of all
 * JavaBeans<sup><font size="-2">TM</font></sup> has been added to the
 * <code>java.beans</code> package. Please see {@link java.beans.XMLEncoder}.
 */
public class DynamicUtilTreeNode extends DefaultMutableTreeNode {
	/**
	 * Does the this <code>JTree</code> have children? This property is
	 * currently not implemented.
	 */
	protected boolean hasChildren;
	/** Value to create children with. */
	protected Object childValue;
	/** Have the children been loaded yet? */
	protected boolean loadedChildren;

	/**
	 * Adds to parent all the children in <code>children</code>. If
	 * <code>children</code> is an array or vector all of its elements are added
	 * is children, otherwise if <code>children</code> is a hashtable all the
	 * key/value pairs are added in the order <code>Enumeration</code> returns
	 * them.
	 */
	public static void createChildren(DefaultMutableTreeNode parent,
			Object children) {
		if (children instanceof Vector) {
			Vector childVector = (Vector) children;

			for (int counter = 0, maxCounter = childVector.size(); counter < maxCounter; counter++)
				parent.add(new DynamicUtilTreeNode(childVector
						.elementAt(counter), childVector.elementAt(counter)));
		} else if (children instanceof Hashtable) {
			Hashtable childHT = (Hashtable) children;
			Enumeration keys = childHT.keys();
			Object aKey;

			while (keys.hasMoreElements()) {
				aKey = keys.nextElement();
				parent.add(new DynamicUtilTreeNode(aKey, childHT.get(aKey)));
			}
		} else if (children instanceof Object[]) {
			Object[] childArray = (Object[]) children;

			for (int counter = 0, maxCounter = childArray.length; counter < maxCounter; counter++)
				parent.add(new DynamicUtilTreeNode(childArray[counter],
						childArray[counter]));
		}
	}

	/**
	 * Creates a node with the specified object as its value and with the
	 * specified children. For the node to allow children, the children-object
	 * must be an array of objects, a <code>Vector</code>, or a
	 * <code>Hashtable</code> -- even if empty. Otherwise, the node is not
	 * allowed to have children.
	 * 
	 * @param value
	 *            the <code>Object</code> that is the value for the new node
	 * @param children
	 *            an array of <code>Object</code>s, a <code>Vector</code>, or a
	 *            <code>Hashtable</code> used to create the child nodes; if any
	 *            other object is specified, or if the value is
	 *            <code>null</code>, then the node is not allowed to have
	 *            children
	 */
	public DynamicUtilTreeNode(Object value, Object children) {
		super(value);
		loadedChildren = false;
		childValue = children;
		if (children != null) {
			if (children instanceof Vector)
				setAllowsChildren(true);
			else if (children instanceof Hashtable)
				setAllowsChildren(true);
			else if (children instanceof Object[])
				setAllowsChildren(true);
			else
				setAllowsChildren(false);
		} else
			setAllowsChildren(false);
	}

	/**
	 * Returns true if this node allows children. Whether the node allows
	 * children depends on how it was created.
	 * 
	 * @return true if this node allows children, false otherwise
	 * @see #JTree
	 */
	public boolean isLeaf() {
		return !getAllowsChildren();
	}

	/**
	 * Returns the number of child nodes.
	 * 
	 * @return the number of child nodes
	 */
	public int getChildCount() {
		if (!loadedChildren)
			loadChildren();
		return super.getChildCount();
	}

	/**
	 * Loads the children based on <code>childValue</code>. If
	 * <code>childValue</code> is a <code>Vector</code> or array each element is
	 * added as a child, if <code>childValue</code> is a <code>Hashtable</code>
	 * each key/value pair is added in the order that <code>Enumeration</code>
	 * returns the keys.
	 */
	protected void loadChildren() {
		loadedChildren = true;
		createChildren(this, childValue);
	}

	/**
	 * Subclassed to load the children, if necessary.
	 */
	public TreeNode getChildAt(int index) {
		if (!loadedChildren)
			loadChildren();
		return super.getChildAt(index);
	}

	/**
	 * Subclassed to load the children, if necessary.
	 */
	public Enumeration children() {
		if (!loadedChildren)
			loadChildren();
		return super.children();
	}
}
