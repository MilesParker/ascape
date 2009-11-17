package org.ascape.runtime.swing.navigator;

import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.ascape.model.Agent;
import org.ascape.model.AscapeObject;
import org.ascape.model.Scape;
import org.ascape.model.event.DefaultScapeListener;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.event.ScapeListener;
import org.ascape.model.rule.Rule;
import org.ascape.util.PropertyAccessor;
import org.ascape.util.ResetableIterator;

public class NavigatorTreeBuilder implements TreeBuilder {


	public DefaultTreeModel getDefaultTreeModel() {
		return scapeTreeModel;
	}

	public TreeNode getTreeNode(AscapeObject modelElement) {
        if (modelElement instanceof Scape) {
        	Scape scape = (Scape) modelElement;
            return new ScapeNode(scape, scapeTreeModel);
        } else if (modelElement instanceof Agent) {
        	Agent agent = (Agent) modelElement;
        	return new AgentNode(agent);
        } else {
            throw new RuntimeException("No node for the model element: " + modelElement
                                       + (modelElement != null ? "[" + modelElement.getClass() + "]" : ""));
        }
	}

	public List<TreeSelectionListener> getTreeSelectionListeners() {
		return Collections.emptyList();
	}


	/**
	 * The tree.
	 */
	private final JTree tree;

	/**
	 * The scape model. Initially blank (i.e.: no root node). A root node is
	 * added when the scape is added via {@link #scapeAdded(ScapeEvent)}.
	 */
	private final DefaultTreeModel scapeTreeModel = new DefaultTreeModel(null);

	public DefaultTreeModel getScapeTreeModel() {
		return scapeTreeModel;
	}

	public NavigatorTreeBuilder(JTree tree) {
		super();
		this.tree = tree;
	}

	public void build(Scape scape) {
		scapeTreeModel.setRoot(new ScapeNode(scape, scapeTreeModel));
	}









}
