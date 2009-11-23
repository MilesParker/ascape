package org.ascape.runtime.swing.navigator;

/**
 * A tree node that provides lazy creation of children (i.e.: when expanded) and
 * a method call ({@link #iterate()}) each iteration of the scape.
 * 
 * @author Oliver Mannion
 * @version $Revision$ 
 */
public abstract class LazyIterableNode extends LazyMutableTreeNode {

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
	public LazyIterableNode(Object userObject) {
		super(userObject);
	}

	/**
	 * Operation to perform every iteration of the scape.
	 */
	protected abstract void iterate();

}
