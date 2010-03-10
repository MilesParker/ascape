package org.ascape.runtime.swing.navigator;

import org.ascape.view.vis.PanelView;

/**
 * Provides a {@link PanelView}. Used by {@link PanelViewNode} to create a
 * Navigator node that displays a {@link PanelView} when clicked on.
 * 
 * @author Oliver Mannion
 * @version $Revision$
 */
public interface PanelViewNodeProvider {

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
}
