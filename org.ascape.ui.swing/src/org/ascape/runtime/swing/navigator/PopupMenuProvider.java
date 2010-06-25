package org.ascape.runtime.swing.navigator;

import javax.swing.JPopupMenu;

/**
 * Provides a {@link JPopupMenu}.
 * 
 * @author Oliver Mannion
 * @version $Revision$
 */
public interface PopupMenuProvider {

	/**
	 * Provides a {@link JPopupMenu}.
	 *  
	 * @return popup menu
	 */
	JPopupMenu getPopupMenu();
}
