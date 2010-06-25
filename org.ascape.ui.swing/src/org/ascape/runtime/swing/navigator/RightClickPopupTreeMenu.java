package org.ascape.runtime.swing.navigator;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 * A {@link MouseListener} that displays a popup menu when right click is
 * detected on a tree node. The tree node must implement
 * {@link PopupMenuProvider}.
 * 
 * @author Oliver Mannion
 * @version $Revision$
 */
public class RightClickPopupTreeMenu extends MouseAdapter {

	private void myPopupEvent(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		JTree tree = (JTree) e.getSource();
		TreePath path = tree.getPathForLocation(x, y);
		if (path == null)
			return;

		// select the right clicked node
		// tree.setSelectionPath(path);

		Object node = path.getLastPathComponent();

		if (node instanceof PopupMenuProvider) {
			JPopupMenu popup = ((PopupMenuProvider) node).getPopupMenu();
			popup.show(tree, x, y);
		}

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.isPopupTrigger())
			myPopupEvent(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.isPopupTrigger())
			myPopupEvent(e);
	}

}
