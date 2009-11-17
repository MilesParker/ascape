package org.ascape.runtime.swing.navigator;

import org.ascape.util.PropertyAccessor;

/**
 * The Class AccessorNode.
 */
public class AccessorNode extends BaseNode {

	/**
     * 
     */
	private static final long serialVersionUID = 6327681491877012320L;
	
	/**
	 * The accessor.
	 */
	private final PropertyAccessor accessor;

	/**
	 * Instantiates a new accessor node.
	 * 
	 * @param accessor
	 *            the accessor
	 */
	public AccessorNode(PropertyAccessor accessor) {
		super(accessor);
		this.accessor = accessor;
	}

	@Override
	public String toString() {
		return accessor.getDescriptor().getShortDescription() + "="
				+ accessor.getValue();
	}
}
