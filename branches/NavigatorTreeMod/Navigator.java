/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.runtime.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TooManyListenersException;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.ascape.model.Agent;
import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;
import org.ascape.runtime.swing.NavigatorTreeBuilder.AccessorNode;
import org.ascape.runtime.swing.NavigatorTreeBuilder.AgentNode;
import org.ascape.runtime.swing.NavigatorTreeBuilder.BaseNode;
import org.ascape.runtime.swing.NavigatorTreeBuilder.PropertiesNode;
import org.ascape.runtime.swing.NavigatorTreeBuilder.RuleNode;
import org.ascape.runtime.swing.NavigatorTreeBuilder.RulesNode;
import org.ascape.runtime.swing.NavigatorTreeBuilder.ScapeMembersNode;
import org.ascape.runtime.swing.NavigatorTreeBuilder.ScapeNode;
import org.ascape.view.vis.ComponentViewDelegate;
import org.ascape.view.vis.PanelView;

/**
 * The Class Navigator.
 */
public class Navigator extends PanelView {

	/**
	 * The tree.
	 */
	private final JTree tree = new JTree();

	/**
	 * The waiting for update.
	 */
	private final Set<BaseNode> waitingForUpdate = new HashSet();

	private final NavigatorTreeBuilder treeBuilder =
			new NavigatorTreeBuilder(tree, waitingForUpdate);

	/**
	 * Instantiates a new navigator.
	 */
	public Navigator() {
		super("Scape Navigator");

		createTree(treeBuilder.getScapeModel());

		delegate = new ComponentViewDelegate(this) {
			/**
             * 
             */
			private static final long serialVersionUID = 6905412538045347381L;

			public void scapeAdded(ScapeEvent scapeEvent)
					throws TooManyListenersException {
				scape = (Scape) scapeEvent.getSource();
			}
		};
	}

	/**
	 * The lock.
	 */
	Object lock = new Object();

	/**
	 * The Class ScapeIcon.
	 */
	class ScapeIcon implements Icon {

		/**
		 * The scape.
		 */
		Scape scape;

		/**
		 * Instantiates a new scape icon.
		 * 
		 * @param agent
		 *            the agent
		 */
		public ScapeIcon(Scape agent) {
			this.scape = agent;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.Icon#getIconHeight()
		 */
		public final int getIconHeight() {
			return 16;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.Icon#getIconWidth()
		 */
		public final int getIconWidth() {
			return 16;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.Icon#paintIcon(java.awt.Component,
		 * java.awt.Graphics, int, int)
		 */
		public synchronized void paintIcon(Component c, Graphics g, int x,
				int y) {
			((Graphics2D) g).setRenderingHint(
					RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g.setColor(scape.getColor());
			g.drawRect(x, y, 14, 14);
			((Graphics2D) g).setRenderingHint(
					RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			if (scape.isInitialized() && scape.getSize() > 0) {
				Object[] agents = new Object[Math.min(scape.getSize(), 4)];
				// To prevent comodification errors..
				for (int i = 0; i < agents.length; i++) {
					agents[i] = scape.findRandom();
				}
			}
		}
	}

	;

	/**
	 * The Class AgentIcon.
	 */
	class AgentIcon implements Icon {

		/**
		 * The agent.
		 */
		Agent agent;

		/**
		 * Instantiates a new agent icon.
		 * 
		 * @param agent
		 *            the agent
		 */
		public AgentIcon(Agent agent) {
			this.agent = agent;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.Icon#getIconHeight()
		 */
		public final int getIconHeight() {
			return 16;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.Icon#getIconWidth()
		 */
		public final int getIconWidth() {
			return 16;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.Icon#paintIcon(java.awt.Component,
		 * java.awt.Graphics, int, int)
		 */
		public synchronized void paintIcon(Component c, Graphics g, int x,
				int y) {
			((Graphics2D) g).setRenderingHint(
					RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g.setColor(agent.getColor());
			g.fillOval(x, y, 14, 14);
		}
	}

	private void createTree(TreeModel treeModel) {
		tree.setFont(getFont().deriveFont(10.0f));
		tree.setModel(treeModel);
		setLayout(new BorderLayout());
		add(tree, BorderLayout.CENTER);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setLargeModel(true);
		tree.setToggleClickCount(1);
		tree.setShowsRootHandles(true);

		// tree.putClientProperty("JTree.lineStyle", "Angled");
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer() {
			/**
             * 
             */
			private static final long serialVersionUID = 1371869534479837978L;

			public Component getTreeCellRendererComponent(JTree tree,
					Object value, boolean sel, boolean expanded,
					boolean leaf, int row, boolean hasFocus) {
				Component c =
						super.getTreeCellRendererComponent(tree, value, sel,
								expanded, leaf, row, hasFocus);
				if (value instanceof ScapeMembersNode) {
					setIcon(DesktopEnvironment.getIcon("Members"));
				} else if (value instanceof AgentNode) {
					setIcon(new AgentIcon((Agent) ((AgentNode) value)
							.getUserObject()));
				} else if (value instanceof ScapeNode) {
					setIcon(new ScapeIcon((Scape) ((ScapeNode) value)
							.getUserObject()));
					// Order matters for line below, as agent node subclasses
					// property node
				} else if (value instanceof PropertiesNode) {
					setIcon(DesktopEnvironment.getIcon("Properties"));
					setText("Properties");
				} else if (value instanceof RulesNode) {
					setIcon(DesktopEnvironment.getIcon("Rules"));
				} else if (value instanceof RuleNode) {
					setIcon(DesktopEnvironment.getIcon("Rule"));
				} else if (value instanceof AccessorNode) {
					setIcon(DesktopEnvironment.getIcon("Property"));
				}
				return c;
			}
		};
		tree.setCellRenderer(renderer);
		// tree.setCellEditor(new DefaultTreeCellEditor(tree, renderer) {
		// public Component getTreeCellEditorComponent(JTree tree, Object value,
		// boolean isSelected,
		// boolean expanded,
		// boolean leaf, int row) {
		// System.out.println(": "+value.getClass());
		// return super.getTreeCellEditorComponent(tree, value, isSelected,
		// expanded, leaf, row);
		// }
		// });
	}

	@Override
	public void scapeAdded(ScapeEvent scapeEvent)
			throws TooManyListenersException {
		super.scapeAdded(scapeEvent);

		// build the tree for scape
		treeBuilder.build(scape);

	}

	@Override
	public void scapeRemoved(ScapeEvent scapeEvent) {
		super.scapeRemoved(scapeEvent);
		scape = null;
	}

	@Override
	public boolean isLifeOfScape() {
		return false;
	}

	/**
	 * When scape is iterated, update children of nodes that are waiting for an
	 * update if the node is visible.
	 * 
	 * @param scapeEvent
	 *            scape event
	 */
	@Override
	public void scapeNotification(ScapeEvent scapeEvent) {
		if (scapeEvent.getID() == ScapeEvent.REPORT_ITERATE) {
			synchronized (waitingForUpdate) {

				// update children of nodes that are waiting for an update
				// if they are visible
				for (Iterator iterator = waitingForUpdate.iterator(); iterator
						.hasNext();) {
					BaseNode node = (BaseNode) iterator.next();
					TreePath nodePath = new TreePath(node);
					if (tree.isVisible(nodePath)) {
						node.updateChildren();
					}
				}

				waitingForUpdate.clear();
			}
		}
		tree.repaint();
		notifyScapeUpdated();
	}
}
