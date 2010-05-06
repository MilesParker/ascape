package org.ascape.runtime.swing.navigator;

import java.awt.Container;

import org.ascape.view.vis.PanelView;

/**
 * Provides a {@link PanelView} based on an existing {@link PanelView} supplied
 * during construction.
 * 
 * @author Oliver Mannion
 * @version $Revision$
 */
public class PanelViewExisting implements PanelViewProvider {

	private final PanelView pview;
	private final String name;
	
	/**
	 * Construct a {@link PanelViewExisting} using the name 
	 * supplied by {@code pview}.
	 * 
	 * @param pview panel view
	 */
	public PanelViewExisting(PanelView pview) {
		this(pview, pview.getName());
	}

	/**
	 * Construct a {@link PanelViewExisting} using the name 
	 * supplied.
	 * 
	 * @param pview panel view
	 * @param name name
	 */
	public PanelViewExisting(PanelView pview, String name) {
		this.pview = pview;
		this.name = name;
	}

	
	public String getName() {
		return name;
	}

	public PanelView getPanelView() {
		return pview;
	}

	public void panelViewAdded(Container pvFrameImp) {
		// nothing to do
	}

	public void frameClosed() {
		// nothing to do
	}

}
