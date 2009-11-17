package org.ascape.runtime.swing.navigator;

import javax.swing.JTable;
import javax.swing.tree.DefaultTreeModel;

import org.ascape.model.Scape;
import org.jamsim.ascape.TreeUtil;

public class OutputTablesNode extends BaseNode {

	private final DefaultTreeModel treeModel;
	private final Scape scape;
	
	public OutputTablesNode(Scape scape, DefaultTreeModel treeModel) {
		super("Output Tables");
		this.scape = scape;
		this.treeModel = treeModel;
	}

	public void addOutputTableNode(JTable table) {
		TreeUtil.PanelViewNode newNode =
			new TreeUtil.PanelViewNode(scape, table);

		treeModel.insertNodeInto(newNode, this, this.getChildCount());
	
	}
	
	
}
