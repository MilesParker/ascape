package org.ascape.runtime.swing.navigator;

import javax.swing.JTable;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultTreeModel;

import org.ascape.model.Scape;
import org.ascape.util.swing.TreeUtil;

public class TablesNode extends BaseNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5825053757164334222L;
	private final DefaultTreeModel treeModel;
	private final Scape scape;
	
	public TablesNode(String name, Scape scape, DefaultTreeModel treeModel) {
		super(name);
		this.scape = scape;
		this.treeModel = treeModel;
	}

	public void addChildTableNode(String name, TableModel tmodel) {
		JTable table = new JTable(tmodel); 
		table.setName(name);
		addChildTableNode(table);
	}
	
	public void addChildTableNode(JTable table) {
		TreeUtil.PanelViewNode newNode =
			new TreeUtil.PanelViewNode(scape, table);

		treeModel.insertNodeInto(newNode, this, this.getChildCount());
	}
	
	
}
