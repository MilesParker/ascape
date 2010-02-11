package org.ascape.runtime.swing.navigator;

import javax.swing.JTable;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.ascape.model.Scape;
import org.ascape.view.vis.PanelView;

/**
 * A node that has {@link PanelViewNode} children which display a
 * {@link PanelView} when clicked on. For clicking to work, the
 * {@link PeformActionOnSelectedNode} listener must be installed on the JTree.
 * Methods for creating children nodes are publicly exposed.
 * 
 * @author Oliver Mannion
 * @version $Revision$
 * 
 */
public class PanelViewNodeFolder extends DefaultMutableTreeNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5825053757164334222L;
	private final DefaultTreeModel treeModel;
	private final Scape scape;

	/**
	 * Construct a {@link PanelViewNodeFolder}.
	 * 
	 * @param name
	 *            name of node in the tree
	 * @param scape
	 *            scape that children nodes will be associated with
	 * @param treeModel
	 *            tree model to add children nodes
	 */
	public PanelViewNodeFolder(String name, Scape scape,
			DefaultTreeModel treeModel) {
		super(name);
		this.scape = scape;
		this.treeModel = treeModel;
	}

	/**
	 * Add a child node that displays the given {@link TableModel} when the node
	 * is clicked on.
	 * 
	 * @param name
	 *            name of node
	 * @param tmodel
	 *            table model
	 */
	public void addChildTableNode(String name, TableModel tmodel) {
		JTable table = new JTable(tmodel);
		table.setName(name);
		addChildTableNode(table);
	}

	/**
	 * Add a child node that displays the given {@link TableModel} when the node
	 * is clicked on.
	 * 
	 * @param table
	 *            table to display when the child is clicked on
	 */
	public void addChildTableNode(JTable table) {
		PanelViewNode newNode = new PanelViewNode(scape, table);
		addChildNode(newNode);
	}

	/**
	 * Add a child {@link PanelViewNode}.
	 * 
	 * @param newNode
	 *            panel view node
	 */
	public void addChildNode(PanelViewNode newNode) {
		treeModel.insertNodeInto(newNode, this, this.getChildCount());
	}
}
