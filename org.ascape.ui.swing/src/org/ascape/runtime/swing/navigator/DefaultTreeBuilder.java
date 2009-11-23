package org.ascape.runtime.swing.navigator;

import java.util.Collections;
import java.util.Set;

import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import org.ascape.model.Agent;
import org.ascape.model.AscapeObject;
import org.ascape.model.Scape;

/**
 * Default implementation of {@link TreeBuilder}. Produces {@link ScapeNode}s
 * for scapes, and {@link AgentNode}s for agents.
 * 
 * @author Oliver Mannion
 * @version $Revision$
 * 
 */
public class DefaultTreeBuilder implements TreeBuilder {

	private DefaultTreeModel scapeTreeModel;

	public DefaultTreeModel getTreeModel() {
		return scapeTreeModel;
	}

	public void setTreeModel(DefaultTreeModel treeModel) {
		this.scapeTreeModel = treeModel;
	}

	/**
	 * Returns {@link ScapeNode} for scapes, and {@link AgentNode} for agents.
	 * 
	 * @param modelElement
	 *            model element to create tree node for
	 * @return returns {@link ScapeNode} for scapes, and {@link AgentNode} for
	 *         agents.
	 */
	public MutableTreeNode createTreeNode(AscapeObject modelElement) {
		if (modelElement instanceof Scape) {
			Scape scape = (Scape) modelElement;
			return new ScapeNode(scape, this);
		} else if (modelElement instanceof Agent) {
			Agent agent = (Agent) modelElement;
			return new AgentNode(agent);
		} else {
			throw new RuntimeException("No node for the model element: "
					+ modelElement
					+ (modelElement != null ? "[" + modelElement.getClass()
							+ "]" : ""));
		}
	}

	/**
	 * Returns an empty set.
	 * 
	 * @return empty set.
	 */
	public Set<TreeSelectionListener> getTreeSelectionListeners() {
		return Collections.emptySet();
	}

}
