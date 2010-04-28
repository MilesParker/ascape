package org.ascape.runtime.swing.navigator;

import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JTable;

import org.ascape.util.swing.AscapeGUIUtil;
import org.ascape.util.swing.PanelViewUtil;
import org.ascape.view.vis.PanelView;

/**
 * Provides a {@link PanelView} based on an existing {@link JTable} supplied
 * during construction.
 * 
 * @author Oliver Mannion
 * @version $Revision$
 */
public class PanelViewTable implements PanelViewProvider {

	private final JTable table;

	public PanelViewTable(JTable table) {
		this.table = table;
	}

	public String getName() {
		return table.getName();
	}

	public PanelView getPanelView() {
		// Create a PanelView from the Table
		Dimension desktopSize = AscapeGUIUtil.getDesktopSize();
		return PanelViewUtil.createPanelView(table, desktopSize);
	}

	public void panelViewAdded(Container pvFrameImp) {
		// nothing to do
	}

	public void frameClosed() {
		// nothing to do
	}

}
