package org.ascape.runtime.swing.navigator;

import java.beans.IntrospectionException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.List;

import org.ascape.model.Agent;
import org.ascape.model.Scape;
import org.ascape.util.PropertyAccessor;

/**
 * The Class PropertiesNode.
 */
public class PropertiesNode extends BaseNode {

	/**
     * 
     */
	private static final long serialVersionUID = 8638479208005464388L;

	/**
	 * The accessors.
	 */
	private PropertyAccessor[] accessors;

	/**
	 * The accessor ind.
	 */
	int[] accessorInd;

	/**
	 * The property support.
	 */
	PropertyChangeSupport propertySupport;

	/**
	 * The property listener.
	 */
	PropertyChangeListener propertyListener;

	/**
	 * Instantiates a new properties node.
	 * 
	 * @param scape
	 *            the scape
	 * @param agent
	 *            the agent
	 */
	public PropertiesNode(Agent agent) {
		super(agent);
		try {
			if (!(agent instanceof Scape)) {
				List<?> accessorsList =
						PropertyAccessor.determineReadWriteAccessors(agent,
								Agent.class, true);
				accessors =
						accessorsList
								.toArray(new PropertyAccessor[accessorsList
										.size()]);
			} else {
				List<?> accessorsList =
						PropertyAccessor.determineReadWriteAccessors(agent,
								Scape.class, true);
				accessors =
						accessorsList
								.toArray(new PropertyAccessor[accessorsList
										.size()]);
			}
			Arrays.sort(accessors);
			accessorInd = new int[accessors.length];
			for (int i = 0; i < accessors.length; i++) {
				add(new AccessorNode(accessors[i]));
				accessorInd[i] = i;
			}

		} catch (IntrospectionException e) {
			e.printStackTrace();
		}

	}

}
