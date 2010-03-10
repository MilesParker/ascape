package org.ascape.runtime.swing.navigator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;

import org.ascape.model.Scape;
import org.ascape.view.vis.PanelView;

/**
 * Tree node that displays a panel view with a table when clicked on. For
 * clicking to work, the {@link PeformActionOnSelectedNode} listener must be
 * installed on the JTree.
 * 
 * @author Oliver Mannion
 * @version $Revision$
 */
public class PanelViewNode extends DefaultMutableTreeNode implements
		ActionListener {

	private static final long serialVersionUID = 6327681491877012320L;
	private PanelView panelView;
	private final PanelViewNodeProvider provider;
	private final Scape scape;
	private final String name;

	/**
	 * Create a {@link PanelViewNode} that when clicked will display the
	 * {@link PanelView} supplied by a {@link PanelViewNodeProvider}.
	 * 
	 * @param scape
	 *            scape to associate this instance with.
	 * @param provider
	 *            {@link PanelViewNodeProvider} to execute when this node is clicked.
	 */
	public PanelViewNode(Scape scape, PanelViewNodeProvider provider) {
		super(provider);
		this.scape = scape;
		this.name = provider.getName();
		this.provider = provider;
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

			panelView = provider.getPanelView();

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
