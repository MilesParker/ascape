package org.ascape.runtime.swing.navigator;

/**
 * The Class BaseNode.
 */
public class BaseNode extends LazyMutableTreeNode {

	/**
     * 
     */
	private static final long serialVersionUID = -1339361794486509129L;

	/**
	 * Instantiates a new base node.
	 * 
	 * @param userObject
	 *            the user object
	 */
	public BaseNode(Object userObject) {
		super(userObject);
	}

	@Override
	protected void updateChildren() {
		// debugPrintMe();
	}

	protected void debugPrintMe() {
		System.out.println("updateChildren: "
				+ this.getClass().getSimpleName() + " " + "\""
				+ this.toString() + "\" parent: " + parent);
	}

	/**
	 * Operation to perform every iteration of the scape.
	 */
	protected void iterate() {
	}

}
