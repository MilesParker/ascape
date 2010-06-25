package org.ascape.runtime.swing.navigator;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.ascape.runtime.swing.DesktopEnvironment;

/**
 * A {@link MouseListener} that displays a popup menu when right click is
 * detected. The popup menu allows the user to close all open desktop windows.
 * 
 * @author Oliver Mannion
 * @version $Revision$
 */
public class RightClickPopupDesktopMenu extends MouseAdapter {

	private final DesktopEnvironment desktop;

	public RightClickPopupDesktopMenu(DesktopEnvironment desktop) {
		this.desktop = desktop;
	}

	private void myPopupEvent(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		Component source = (Component) e.getSource();

		JPopupMenu popup = getPopupMenu();
		popup.show(source, x, y);

	}

	/**
	 * Right click popup menu that allows saving of this node.
	 * 
	 * @return popup menu.
	 */
	public JPopupMenu getPopupMenu() {
		String label = "Close all windows";

		JPopupMenu popup = new JPopupMenu();
		JMenuItem item = new JMenuItem(label);
		item.addActionListener(new CloseAction());

		popup.add(item);
		return popup;
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

	/**
	 * Action that closes all frames.
	 */
	private class CloseAction implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			desktop.closeOpenFrames(true);
		}

	}
}
