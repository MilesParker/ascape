package org.ascape.runtime.swing.navigator;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTable;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;

import org.ascape.model.Scape;
import org.ascape.util.swing.AscapeGUIUtil;
import org.ascape.util.swing.PanelViewUtil;
import org.ascape.view.vis.PanelView;

/**
 * Tree node that displays a panel view when clicked on. For clicking to work,
 * the {@link PeformActionOnSelectedNode} listener must be installed on the
 * JTree.
 * 
 * @author Oliver Mannion
 * @version $Revision$
 */
public class PanelViewNode extends DefaultMutableTreeNode implements
		ActionListener {

	private static final long serialVersionUID = 6327681491877012320L;
	private PanelView panelView;
	private final JTable table;
	private final Scape scape;
	private final String name;

	/**
	 * Create a PanelViewNode that when clicked will display the passed in
	 * JTable. The node's title is the name of the table.
	 * 
	 * @param scape
	 *            scape to associate this instance with.
	 * @param table
	 *            JTable to display in a PanelView when this node is clicked.
	 */
	public PanelViewNode(Scape scape, JTable table) {
		super(table);
		this.scape = scape;
		this.name = table.getName();
		this.table = table;
	}

	/**
	 * Called by
	 * {@link PeformActionOnSelectedNode#valueChanged(TreeSelectionEvent)} when
	 * this node is selected.
	 * 
	 * @param e
	 *            event
	 */
	public void actionPerformed(ActionEvent e) {
		displayPanelView();
	}

	/**
	 * Display the PanelView associated with this node.
	 */
	public void displayPanelView() {

		// check to see that panelView is not currently displayed
		// (i.e: has been added to the scape)
		if (panelView == null
				|| scape.getScapeListeners().indexOf(panelView) == -1) {

			// Create a PanelView from the Table
			Dimension desktopSize = AscapeGUIUtil.getDesktopSize(scape);
			panelView = PanelViewUtil.createPanelView(table, desktopSize);

			// add the panelView to the scape,
			// which will display it in the GUI
			scape.addView(panelView);

		}
	}

	@Override
	public String toString() {
		if (name == null) {
			return super.toString();
		} else {
			return name;
		}
	}

}
