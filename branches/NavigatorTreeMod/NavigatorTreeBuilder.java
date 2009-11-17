package org.ascape.runtime.swing;

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
		return scapeModel;
	}

	public TreeNode getTreeNode(AscapeObject modelElement) {
        if (modelElement instanceof Scape) {
        	Scape scape = (Scape) modelElement;
            return new ScapeNode(scape);
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
	 * The group size.
	 */
	private final int groupSize = 20;

	/**
	 * The tree.
	 */
	private final JTree tree;

	/**
	 * The scape model. Initially blank (i.e.: no root node). A root node is
	 * added when the scape is added via {@link #scapeAdded(ScapeEvent)}.
	 */
	private final DefaultTreeModel scapeModel = new DefaultTreeModel(null);

	public DefaultTreeModel getScapeModel() {
		return scapeModel;
	}

	public NavigatorTreeBuilder(JTree tree) {
		super();
		this.tree = tree;
	}

	public void build(Scape scape) {
		scapeModel.setRoot(new ScapeNode(scape));
	}

	/**
	 * Desc.
	 * 
	 * @param startIndex
	 *            the start index
	 * @param endIndex
	 *            the end index
	 * @return the string
	 */
	public static String desc(int startIndex, int endIndex) {
		return "Members " + startIndex + "-" + (endIndex - 1);
	}

	/**
	 * The Class BaseNode.
	 */
	class BaseNode extends LazyMutableTreeNode {

		/**
	     * 
	     */
		private static final long serialVersionUID = -1339361794486509129L;

		/**
		 * Instantiates a new base node.
		 * 
		 * @param scape
		 *            the scape
		 * @param userObject
		 *            the user object
		 */
		public BaseNode(Object userObject) {
			super(userObject);
		}

		@Override
		protected void updateChildren() {
			//debugPrintMe();
		}

		protected void debugPrintMe() {
			System.out
					.println("updateChildren: "
							+ this.getClass().getSimpleName() + " "
							+ "\"" + this.toString() + "\" parent: " + parent);
		}
		
		/**
		 * Operation to perform every iteration of the scape.
		 */
		protected void iterate() {
		}
		
	}

	/**
	 * The Class AccessorNode.
	 */
	class AccessorNode extends BaseNode {

		/**
	     * 
	     */
		private static final long serialVersionUID = 6327681491877012320L;
		/**
		 * The accessor.
		 */
		private PropertyAccessor accessor;

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

	/**
	 * The Class RuleNode.
	 */
	class RuleNode extends BaseNode {

		/**
	     * 
	     */
		private static final long serialVersionUID = -6204254579116806384L;

		/**
		 * Instantiates a new rule node.
		 * 
		 * @param scape
		 *            the scape
		 * @param userObject
		 *            the user object
		 */
		public RuleNode(Object userObject) {
			super(userObject);
		}
	}

	/**
	 * The Class RulesNode.
	 */
	class RulesNode extends BaseNode {

		/**
	     * 
	     */
		private static final long serialVersionUID = 5237720048765754585L;

		/**
		 * The current rules.
		 */
		private Map<Rule, RuleNode> currentRules;
		
		private final Scape scape;

		/**
		 * Instantiates a new rules node.
		 * 
		 * @param scape
		 *            the scape
		 */
		public RulesNode(final Scape scape) {
			super("Rules");
			this.scape = scape;

			currentRules = new HashMap<Rule, RuleNode>();

			Observer selectionObserver = new Observer() {
				
				// when a rule is activated/deactivated
				// via Model Settings, update the RulesNode's
				// children
				public void update(Observable o, Object arg) {
					updateChildren();
				}
			};
			scape.getRules().addObserver(selectionObserver);
		}

		/**
		 * Add/remove rules.
		 */
		protected void updateChildren() {
			// todo does not handle updating order
			for (int i = 0; i < scape.getRules().getVector().size(); i++) {
				Rule rule = (Rule) scape.getRules().getVector().elementAt(i);
				if (scape.getRules().isSelected(i)) {
					
					// if rule doesn't exist as a child, add it
					if (currentRules.get(rule) == null) {
						RuleNode addNode = new RuleNode(rule);
						scapeModel.insertNodeInto(addNode, this, Math.min(
								scapeModel.getChildCount(this), scape
										.getRules().getSelectedIndex(rule)));
						currentRules.put(rule, addNode);
					}
				} else {
					
					// if rule exists as a child, remove it
					RuleNode node = currentRules.get(rule);
					if (node != null) {
						scapeModel.removeNodeFromParent(node);
						currentRules.remove(rule);
					}
				}

			}
		}
	}

	/**
	 * The Class PropertiesNode.
	 */
	class PropertiesNode extends BaseNode {

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
							PropertyAccessor.determineReadWriteAccessors(
									agent, Agent.class, true);
					accessors =
							accessorsList
									.toArray(new PropertyAccessor[accessorsList
											.size()]);
				} else {
					List<?> accessorsList =
							PropertyAccessor.determineReadWriteAccessors(
									agent, Scape.class, true);
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

	/**
	 * The Class AgentNode.
	 */
	class AgentNode extends PropertiesNode {

		/**
	     * 
	     */
		private static final long serialVersionUID = -5046377326408958875L;

		/**
		 * Instantiates a new agent node.
		 * 
		 * @param scape
		 *            the scape
		 * @param agent
		 *            the agent
		 */
		public AgentNode(Agent agent) {
			super(agent);
		}
	}

	/**
	 * The Class ScapeNode.
	 */
	class ScapeNode extends BaseNode {

		/**
	     * 
	     */
		private static final long serialVersionUID = -7469682418324424534L;

		/**
		 * Instantiates a new scape node.
		 * 
		 * @param scape
		 *            the scape
		 */
		public ScapeNode(final Scape scape) {
			super(scape);
			add(new PropertiesNode(scape));
			add(new RulesNode(scape));
			if (scape instanceof Scape) {
				ScapeMembersNode membersNode = new ScapeMembersNode(scape);
				add(membersNode);

				// if scape can modify the tree, call the modifyTree method
				// of the scape on the AWT Event thread
				if (scape instanceof TreeModifier) {
					Runnable doWorkRunnable = new Runnable() {
						public void run() {
							((TreeModifier) scape).modifyTree(
									tree,
									(DefaultMutableTreeNode) ScapeNode.this);
						}
					};
					SwingUtilities.invokeLater(doWorkRunnable);
				}

			}
		}
	}

	/**
	 * The Class ScapeGroupNode.
	 */
	class ScapeGroupNode extends ScapeMembersNode {

		/**
	     * 
	     */
		private static final long serialVersionUID = 3489960978154121203L;

		/**
		 * The start index.
		 */
		int startIndex;

		/**
		 * The end index.
		 */
		int endIndex;

		/**
		 * The iterator.
		 */
		ResetableIterator iterator;

		/**
		 * Instantiates a new scape group node.
		 * 
		 * @param scape
		 *            the scape
		 * @param startIndex
		 *            the start index
		 * @param endIndex
		 *            the end index
		 */
		public ScapeGroupNode(Scape scape, int startIndex, int endIndex) {
			super(scape, desc(startIndex, endIndex));
			this.startIndex = startIndex;
			this.endIndex = endIndex;
		}

		/**
		 * Update this group's children. 
		 */
		@Override
		protected void updateChildren() {
			updateChildren(startIndex, endIndex);
		}
	}

	/**
	 * The Class ScapeMembersNode.
	 */
	class ScapeMembersNode extends BaseNode {

		/**
	     * 
	     */
		private static final long serialVersionUID = 3696674806064654573L;

		private final Scape scape;
		
		/**
		 * Instantiates a new scape members node.
		 * 
		 * @param scape
		 *            the scape
		 * @param desc
		 *            the desc
		 */
		public ScapeMembersNode(Scape scape, String desc) {
			super(desc);
			this.scape = scape;
		}

		/**
		 * Instantiates a new scape members node.
		 * 
		 * @param scape
		 *            the scape
		 */
		public ScapeMembersNode(Scape scape) {
			this(scape, "Members");
		}

		
		@Override
		protected void iterate() {
			updateChildren();
		}

		@Override
		protected void updateChildren() {
			updateChildren(0, scape.getSize());
		}

		/**
		 * Update children. Insert new members.
		 * 
		 * @param startIndex
		 *            the start index
		 * @param endIndex
		 *            the end index
		 */
		void updateChildren(int startIndex, int endIndex) {
			int size = endIndex - startIndex;
			if (size <= groupSize) {
				if (scape.isMutable()) {
					insertMutableMembers(startIndex, endIndex);
				} else {
					insertImmutableMembers(startIndex, endIndex);
				}
			} else {
				if (scape.isMutable()) {
					insertMutableGroups(startIndex, endIndex);
				} else {
					insertImmutableGroups(startIndex, endIndex);
				}
			}
		}

		/**
		 * Insert mutable members.
		 * 
		 * @param startIndex
		 *            the start index
		 * @param endIndex
		 *            the end index
		 */
		void insertMutableMembers(int startIndex, int endIndex) {
			List scapeMembers = new ArrayList();
			// A little cheat here..to fix an issue w (I think) node getting
			// updted out of order.
			Iterator iterator =
					scape.getSpace().safeIterator(
							Math.min(startIndex, scape.getSize()),
							Math.min(endIndex, scape.getSize()));
			for (; iterator.hasNext();) {
				scapeMembers.add(iterator.next());
			}
			Set remain = new HashSet();
			List delete = new ArrayList();

			// get this node's existing children
			Enumeration e = children();
			
			
			while (e.hasMoreElements()) {
				BaseNode node = (BaseNode) e.nextElement();

				if (!scapeMembers.contains(node.getUserObject())) {
					delete.add(node);
				} else {
					remain.add(node.getUserObject());
				}
				scapeModel.nodeChanged(node);
			}
			scapeMembers.removeAll(remain);

			// add all the scape members that are not already children of this node
			for (Iterator addIterator = scapeMembers.iterator(); addIterator
					.hasNext();) {
				addNodes((Agent) addIterator.next());
			}

			// remove children of this node that are no longer scape members
			for (Iterator delIterator = delete.iterator(); delIterator
					.hasNext();) {
				MutableTreeNode node = (MutableTreeNode) delIterator.next();
				scapeModel.removeNodeFromParent(node);
			}
		}

		/**
		 * Insert immutable members.
		 * 
		 * @param startIndex
		 *            the start index
		 * @param endIndex
		 *            the end index
		 */
		void insertImmutableMembers(int startIndex, int endIndex) {
			Iterator iterator =
					scape.getSpace().safeIterator(startIndex, endIndex);
			if (getChildCount() == 0) {
				for (; iterator.hasNext();) {
					addNodes((Agent) iterator.next());
				}
			}
		}

		/**
		 * Insert mutable groups.
		 * 
		 * @param start
		 *            the start
		 * @param end
		 *            the end
		 */
		void insertMutableGroups(int start, int end) {
			int size = end - start;
			int numChildren = groupSize;
			if (size / groupSize >= groupSize) {
				numChildren = size / groupSize;
			}
			Enumeration e = children();
			int groupStep = start;
			int childIndex = 0;
			while (e.hasMoreElements()) {
				BaseNode node = (BaseNode) e.nextElement();
				if (node instanceof ScapeNode || node instanceof AgentNode) {
					// If we were formally displaying actual members here (e.g. the
					// size of scape has grown past group size) we need to delete
					// them.
					scapeModel.removeNodeFromParent(node);
				} else {
					
					// child is a scape group node
					// set the start and end indices, and description
					
					((ScapeGroupNode) node).startIndex = groupStep;
					((ScapeGroupNode) node).endIndex =
							Math.min(end, groupStep + numChildren);
					((ScapeGroupNode) node).setUserObject(desc(groupStep,
							((ScapeGroupNode) node).endIndex));
				}
				
				//if (groupStep >= end) {
				//	scapeModel.removeNodeFromParent(node);
				//}
				
				childIndex++;
				groupStep = Math.min(end, groupStep + numChildren);
			}
			
			// create new ScapeGroup nodes
			for (; groupStep < end; groupStep = Math.min(end, groupStep + numChildren)) {
				ScapeGroupNode node =
						new ScapeGroupNode(scape, groupStep, Math.min(end, groupStep
								+ numChildren));
				scapeModel.insertNodeInto(node, this, getChildCount());
			}
		}

		/**
		 * Insert immutable groups.
		 * 
		 * @param start
		 *            the start
		 * @param end
		 *            the end
		 */
		void insertImmutableGroups(int start, int end) {
			int size = end - start;
			int numChildren = groupSize;
			if (size / groupSize >= groupSize) {
				numChildren = size / groupSize;
			}
			int index = 0;
			for (int i = start; i < end; i = Math.min(end, i + numChildren)) {
				ScapeGroupNode node =
						new ScapeGroupNode(scape, i, Math.min(end, i
								+ numChildren));
				scapeModel.insertNodeInto(node, this, index);
				index++;
			}
		}

		/**
		 * Adds the nodes.
		 * 
		 * @param agent
		 *            the agent
		 */
		void addNodes(Agent agent) {
			MutableTreeNode treeNode;
			if (agent instanceof Scape) {
				treeNode = new ScapeNode((Scape) agent);
			} else { // Agent
				treeNode = new AgentNode(agent);
			}
			scapeModel.insertNodeInto(treeNode, ScapeMembersNode.this,
					getChildCount());
		}
		
	}

}
