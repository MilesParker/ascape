package org.ascape.runtime.swing.navigator;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.ascape.model.Scape;

/**
 * Navigator node that displays subfolders of {@link PanelViewNode}s for
 * simulation runs. Each run has its own subfolder of nodes, and there can also
 * be a "All Runs" subfolder of nodes.
 * 
 * @author Oliver Mannion
 * @version $Revision$
 */
public class NodesByRunFolder extends DefaultMutableTreeNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6134673429670006279L;
	private final DefaultTreeModel treeModel;
	private final Scape scape;

	private final Map<Integer, PanelViewNodeFolder> runNodes =
			new HashMap<Integer, PanelViewNodeFolder>();
	private final String allRunsNodeName;

	/**
	 * The run number of the node representing all runs.
	 */
	public static final int ALLRUNS = 0;

	/**
	 * Construct a {@link NodesByRunFolder} with the All Runs node named
	 * "All runs".
	 * 
	 * @param name
	 *            name of node in the tree
	 * @param scape
	 *            scape that children nodes will be associated with
	 * @param treeModel
	 *            tree model to add children nodes
	 */
	public NodesByRunFolder(String name, Scape scape, DefaultTreeModel treeModel) {
		this(name, scape, treeModel, "All runs");
	}

	/**
	 * Construct a {@link NodesByRunFolder}.
	 * 
	 * @param name
	 *            name of node in the tree
	 * @param scape
	 *            scape that children nodes will be associated with
	 * @param treeModel
	 *            tree model to add children nodes
	 * @param allRunsNodeName
	 *            name of the node 0, ie: the node that contains results for all
	 *            runs
	 */
	public NodesByRunFolder(String name, Scape scape,
			DefaultTreeModel treeModel, String allRunsNodeName) {
		super(name);
		this.scape = scape;
		this.treeModel = treeModel;
		this.allRunsNodeName = allRunsNodeName;
	}

	private PanelViewNodeFolder getRunSubFolderNode(int runNumber) {

		PanelViewNodeFolder runNode = runNodes.get(runNumber);

		// create node if it doesn't already exist
		if (runNode == null) {

			if (runNumber == ALLRUNS) {
				runNode =
						new PanelViewNodeFolder(allRunsNodeName, scape,
								treeModel);

			} else {
				runNode =
						new PanelViewNodeFolder("Run " + runNumber, scape,
								treeModel);
			}
			runNodes.put(runNumber, runNode);

			// add to tree
			treeModel.insertNodeInto(runNode, this, this.getChildCount());
		}

		return runNode;
	}

	/**
	 * Add a child node that displays the given {@link TableModel} when the node
	 * is clicked on.
	 * 
	 * @param runNumber
	 *            the run number subfolder under which to create the child node.
	 *            If the run number is {@link #ALLRUNS}, then it is created
	 *            under the "All Runs" node.
	 * @param name
	 *            name of node
	 * @param tmodel
	 *            table model
	 */
	public void addChildTableNode(int runNumber, String name,
			TableModel tmodel) {
		JTable table = new JTable(tmodel);
		table.setName(name);
		addChildTableNode(runNumber, table);
	}

	/**
	 * Add a child node that displays the given {@link TableModel} when the node
	 * is clicked on.
	 * 
	 * @param runNumber
	 *            the run number subfolder under which to create the child node.
	 *            If the run number is {@link #ALLRUNS}, then it is created
	 *            under the "All Runs" node.
	 * @param table
	 *            table to display when the child is clicked on
	 */
	public void addChildTableNode(int runNumber, JTable table) {
		PanelViewNodeProvider provider = new PanelViewTable(table);
		PanelViewNode newNode = new PanelViewNode(scape, provider);
		addChildNode(runNumber, newNode);
	}

	/**
	 * Add a child {@link PanelViewNode}.
	 * 
	 * @param runNumber
	 *            the run number subfolder under which to create the child node.
	 *            If the run number is {@link #ALLRUNS}, then it is created
	 *            under the "All Runs" node.
	 * @param newNode
	 *            panel view node
	 */
	public void addChildNode(int runNumber, DefaultMutableTreeNode newNode) {
		PanelViewNodeFolder runParent = getRunSubFolderNode(runNumber);
		treeModel.insertNodeInto(newNode, runParent, runParent
				.getChildCount());
	}
}
