package org.ascape.runtime.swing.navigator;

import java.awt.Container;

import org.ascape.view.vis.PanelView;

/**
 * Provides a {@link PanelView}. Used by {@link PanelViewNode} to create a
 * Navigator node that displays a {@link PanelView} when clicked on.
 * 
 * @author Oliver Mannion
 * @version $Revision$
 */
public interface PanelViewProvider {

	/**
	 * Name of the {@link PanelView}.
	 * 
	 * @return panel view node
	 */
	String getName();

	/**
	 * A {@link PanelView}.
	 * 
	 * @return panel view
	 */
	PanelView getPanelView();
	
	/**
	 * 
	 * Called after the {@link PanelView} is added to a frame and displayed.
	 * @param pvFrameImp frame container of panel view
	 */
	void panelViewAdded(Container pvFrameImp);

	/**
	 * After the {@link PanelView} frame is closed, this event is called.
	 */
	void frameClosed();
}
