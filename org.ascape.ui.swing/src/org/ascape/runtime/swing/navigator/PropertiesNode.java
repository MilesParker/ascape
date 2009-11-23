package org.ascape.runtime.swing.navigator;

import java.beans.IntrospectionException;
import java.util.Arrays;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import org.ascape.model.Agent;
import org.ascape.model.Scape;
import org.ascape.util.PropertyAccessor;

/**
 * The Class PropertiesNode.
 */
public class PropertiesNode extends DefaultMutableTreeNode {

	/**
     * 
     */
	private static final long serialVersionUID = 8638479208005464388L;

	/**
	 * Instantiates a new properties node.
	 * 
	 * @param agent
	 *            the agent
	 */
	public PropertiesNode(Agent agent) {
		super(agent);
		try {
			
			PropertyAccessor[] accessors;
			
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
			int[] accessorInd = new int[accessors.length];
			for (int i = 0; i < accessors.length; i++) {
				add(new AccessorNode(accessors[i]));
				accessorInd[i] = i;
			}

		} catch (IntrospectionException e) {
			e.printStackTrace();
		}

	}

}
