package org.ascape.runtime.swing.navigator;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import org.ascape.model.Agent;
import org.ascape.model.Scape;

/**
 * The Class ScapeMembersNode.
 */
public class ScapeMembersNode extends BaseNode {

	/**
     * 
     */
	private static final long serialVersionUID = 3696674806064654573L;

	/**
	 * The group size.
	 */
	private static final int groupSize = 20;
	
	
	private final Scape scape;
	private final DefaultTreeModel treeModel;
	private final TreeBuilder treeBuilder;

	/**
	 * Instantiates a new scape members node.
	 * 
	 * @param scape
	 *            the scape
	 * @param desc
	 *            the desc
	 */
	public ScapeMembersNode(Scape scape, TreeBuilder treeBuilder,
			String desc) {
		super(desc);
		this.scape = scape;
		this.treeModel = treeBuilder.getTreeModel();
		this.treeBuilder = treeBuilder;
	}

	/**
	 * Instantiates a new scape members node.
	 * 
	 * @param scape
	 *            the scape
	 */
	public ScapeMembersNode(Scape scape, TreeBuilder treeBuilder) {
		this(scape, treeBuilder, "Members");
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
			treeModel.nodeChanged(node);
		}
		scapeMembers.removeAll(remain);

		// add all the scape members that are not already children of this node
		for (Iterator addIterator = scapeMembers.iterator(); addIterator
				.hasNext();) {
			addNodes((Agent) addIterator.next());
		}

		// remove children of this node that are no longer scape members
		for (Iterator delIterator = delete.iterator(); delIterator.hasNext();) {
			MutableTreeNode node = (MutableTreeNode) delIterator.next();
			treeModel.removeNodeFromParent(node);
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
				treeModel.removeNodeFromParent(node);
			} else {

				// child is a scape group node
				// set the start and end indices, and description

				((ScapeGroupNode) node).startIndex = groupStep;
				((ScapeGroupNode) node).endIndex =
						Math.min(end, groupStep + numChildren);
				((ScapeGroupNode) node).setUserObject(desc(groupStep,
						((ScapeGroupNode) node).endIndex));
			}

			// if (groupStep >= end) {
			// scapeModel.removeNodeFromParent(node);
			// }

			childIndex++;
			groupStep = Math.min(end, groupStep + numChildren);
		}

		// create new ScapeGroup nodes
		for (; groupStep < end; groupStep =
				Math.min(end, groupStep + numChildren)) {
			ScapeGroupNode node =
					new ScapeGroupNode(scape, treeBuilder, groupStep, Math.min(
							end, groupStep + numChildren));
			treeModel.insertNodeInto(node, this, getChildCount());
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
					new ScapeGroupNode(scape, treeBuilder, i, Math.min(end, i
							+ numChildren));
			treeModel.insertNodeInto(node, this, index);
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
		MutableTreeNode treeNode = treeBuilder.getTreeNode(agent);
		treeModel.insertNodeInto(treeNode, ScapeMembersNode.this,
				getChildCount());
	}

}
