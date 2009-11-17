package org.ascape.runtime.swing.navigator;

import java.util.Collections;
import java.util.List;

import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.ascape.model.Agent;
import org.ascape.model.AscapeObject;
import org.ascape.model.Scape;
import org.eclipse.amp.agf.tree.AgentTreePart;
import org.eclipse.amp.agf.tree.ScapeTreePart;

public class DefaultNavigatorBuilder implements TreeBuilder {

	private final DefaultTreeModel treeModel;
	
	public DefaultNavigatorBuilder(DefaultTreeModel treeModel) {
		this.treeModel = treeModel;
	}
	
	public TreeNode getTreeNode(AscapeObject modelElement) {

        if (modelElement instanceof Scape) {
            return new ScapeTreePart(provider, provider);
        } else if (modelElement instanceof Agent) {
            return new AgentTreePart();
        } else {
            throw new RuntimeException("No node for the model element: " + modelElement
                                       + (modelElement != null ? "[" + modelElement.getClass() + "]" : ""));
        }
	}

	public DefaultTreeModel getDefaultTreeModel() {
		return treeModel;
	}

	public List<TreeSelectionListener> getTreeSelectionListeners() {
		return Collections.emptyList();
	}

}
