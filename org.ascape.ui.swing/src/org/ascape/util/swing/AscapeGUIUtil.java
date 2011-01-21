package org.ascape.util.swing;

import java.awt.Dimension;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.ascape.model.Scape;
import org.ascape.runtime.RuntimeEnvironment;
import org.ascape.runtime.swing.DesktopEnvironment;
import org.ascape.runtime.swing.SwingEnvironment;
import org.ascape.runtime.swing.UserFrame;
import org.ascape.runtime.swing.ViewFrameBridge;
import org.ascape.runtime.swing.navigator.Navigator;
import org.ascape.runtime.swing.navigator.TreeBuilder;
import org.ascape.view.vis.PanelView;
import org.ascape.view.vis.control.MenuBarView;

/**
 * Utility functions related to the Ascape Swing GUI.
 * 
 * @author Oliver Mannion
 * @version $Revision: 44 $
 */
public final class AscapeGUIUtil {

	/**
	 * The border around the max size of a {@link PanelView} displaying a table.
	 * See {@link #createPanelView(JTable, Dimension)}.
	 */
	public static final Dimension TABLE_BORDER_EDGES = new Dimension(35, 72);
	
	/**
	 * Private constructor to prevent instantiation.
	 */
	private AscapeGUIUtil() {
	}

	/**
	 * Return the dimensions of Ascape's desktop pane, i.e: the pane where
	 * charts, tables etc. are displayed.
	 * 
	 * @param scape
	 *            scape
	 * @return dimensions of Ascape's desktop pane.
	 * @deprecated use {@link #getDesktopSize()} instead.
	 */
	public static Dimension getDesktopSize(Scape scape) {
		UserFrame frame = getUserFrame(scape);
		return (frame == null) ? null : frame.getDeskScrollPane()
				.getViewport().getSize();
	}

	/**
	 * Return the dimensions of Ascape's desktop pane, i.e: the pane where
	 * charts, tables etc. are displayed.
	 * 
	 * @return dimensions of Ascape's desktop pane.
	 */
	public static Dimension getDesktopSize() {
		UserFrame frame = getUserFrame();
		return (frame == null) ? null : frame.getDeskScrollPane()
				.getViewport().getSize();
	}

	public static void flushConsoleLog(Scape scape) {
		UserFrame frame = getUserFrame();
		if (frame != null) {
			frame.flushConsoleLog();
		}
	}

	/**
	 * Show the error dialog. 
	 * 
	 * @param scape
	 *            scape. If specified the error dialog will contain a "Restart"
	 *            option which will close and restart the model/scape. If
	 *            {@code null} no "Restart" option will be provided.
	 * @param e
	 *            exception thrown
	 */
	public static void showErrorDialog(Scape scape, Exception e) {
		if (SwingUtilities.isEventDispatchThread()) {
			/* If we throw an exception from code on the AWT
			 * event thread when need to explicitly call
			 * {@link DesktopEnvironment#showErrorDialog(Scape, Exception)}.
			 */
			AscapeGUIUtil.getDesktopEnvironment().showErrorDialog(scape, e);
		} else {
			// just throw higher and it'll be caught
			throw new RuntimeException(e);
		}
	}

	/**
	 * Get the {@link UserFrame}.
	 * 
	 * @param scape
	 *            scape
	 * @return {@link UserFrame} or {@code null} if not running in desktop
	 *         environment
	 * @deprecated use {@link #getUserFrame()} instead
	 */
	public static UserFrame getUserFrame(Scape scape) {
		DesktopEnvironment desktop = getDesktopEnvironment(scape);
		if (desktop == null) {
			return null;
		}
		return desktop.getUserFrame();
	}

	/**
	 * Get the {@link UserFrame}.
	 * 
	 * @return {@link UserFrame} or {@code null} if not running in desktop
	 *         environment
	 */
	public static UserFrame getUserFrame() {
		DesktopEnvironment desktop = getDesktopEnvironment();
		if (desktop == null) {
			return null;
		}
		return desktop.getUserFrame();
	}

	/**
	 * Get the {@link Navigator}.
	 * 
	 * @return {@link Navigator} or {@code null} if not running in desktop
	 *         environment
	 */
	public static Navigator getNavigator() {
		UserFrame userFrame = getUserFrame();
		if (userFrame == null) {
			return null;
		}
		return userFrame.getNavigator();
	}
	
	/**
	 * Programatically select the supplied navigator tree node
	 * 
	 * @param node tree node to select
	 */
	public static void selectNavigatorNode(DefaultMutableTreeNode node) {
		getNavigator().setSelectionPath(
				new TreePath(node.getPath()));
	}
	
	/**
	 * Get the {@link DesktopEnvironment}.
	 * 
	 * @param scape
	 *            scape
	 * @return {@link DesktopEnvironment} or {@code null} if not running in
	 *         desktop environment
	 * @deprecated use {@link #getDesktopEnvironment()} instead
	 */
	public static DesktopEnvironment getDesktopEnvironment(Scape scape) {
		RuntimeEnvironment runtime = scape.getRunner().getEnvironment();
		if (runtime instanceof DesktopEnvironment) {
			return (DesktopEnvironment) runtime;
		} else {
			return null;
		}
	}

	/**
	 * Get the {@link DesktopEnvironment}.
	 * 
	 * @return {@link DesktopEnvironment} or {@code null} if not running in
	 *         desktop environment
	 */
	public static DesktopEnvironment getDesktopEnvironment() {
		SwingEnvironment runtime = getSwingEnvironment();
		if (runtime instanceof DesktopEnvironment) {
			return (DesktopEnvironment) runtime;
		} else {
			return null;
		}
	}

	/**
	 * Get the currently selected frame inside the desktop.
	 * 
	 * @return {@link JInternalFrame}
	 */
	public static JInternalFrame getSelectedInternalFrame() {
		Vector allFrames =
				SwingEnvironment.DEFAULT_ENVIRONMENT.getAllFrames();
		JInternalFrame selectedInternalFrame = null;

		for (Object frame : allFrames) {
			ViewFrameBridge vfb = (ViewFrameBridge) frame;
			JInternalFrame currentFrame =
					(JInternalFrame) (vfb.getFrameImp());

			if (currentFrame.isSelected()) {
				selectedInternalFrame = currentFrame;
				break;
			}
		}

		return selectedInternalFrame;

	}

	/**
	 * Get the {@link SwingEnvironment}.
	 * 
	 * @param scape
	 *            scape
	 * @return {@link SwingEnvironment} or {@code null} if not running in swing
	 *         environment
	 * @deprecated use {@link #getSwingEnvironment()} instead
	 */
	public static SwingEnvironment getSwingEnvironment(Scape scape) {
		RuntimeEnvironment runtime = scape.getRunner().getEnvironment();
		if (runtime instanceof SwingEnvironment) {
			return (SwingEnvironment) runtime;
		} else {
			return null;
		}
	}

	/**
	 * Get the {@link SwingEnvironment}.
	 * 
	 * @return {@link SwingEnvironment} or {@code null} if not running in swing
	 *         environment
	 */
	public static SwingEnvironment getSwingEnvironment() {
		return SwingEnvironment.DEFAULT_ENVIRONMENT;
	}

	/**
	 * Get the {@link TreeBuilder} from the runtime environment.
	 * 
	 * @param scape
	 *            scape
	 * @return tree builder
	 * @deprecated use {@link #getNavigatorTreeBuilder()} instead.
	 */
	public static TreeBuilder getNavigatorTreeBuilder(Scape scape) {
		SwingEnvironment runtime = getSwingEnvironment(scape);
		return (runtime == null) ? null : runtime.getNavigatorTreeBuilder();
	}

	/**
	 * Get the {@link TreeBuilder} from the runtime environment.
	 * 
	 * @return tree builder
	 */
	public static TreeBuilder getNavigatorTreeBuilder() {
		SwingEnvironment runtime = getSwingEnvironment();
		return (runtime == null) ? null : runtime.getNavigatorTreeBuilder();
	}

	/**
	 * Set the {@link TreeBuilder} on the runtime environment.
	 * 
	 * @param scape
	 *            scape
	 * @param treeBuilder
	 *            tree builder
	 * @deprecated use {@link #setNavigatorTreeBuilder(TreeBuilder)} instead.
	 */
	public static void setNavigatorTreeBuilder(Scape scape,
			TreeBuilder treeBuilder) {
		SwingEnvironment runtime = getSwingEnvironment(scape);
		if (runtime != null) {
			runtime.setNavigatorTreeBuilder(treeBuilder);
		}
	}

	/**
	 * Set the {@link TreeBuilder} on the runtime environment.
	 * 
	 * @param treeBuilder
	 *            tree builder
	 */
	public static void setNavigatorTreeBuilder(TreeBuilder treeBuilder) {
		SwingEnvironment runtime = getSwingEnvironment();
		if (runtime != null) {
			runtime.setNavigatorTreeBuilder(treeBuilder);
		}
	}

	/**
	 * Add an additional menu to the menu bar.
	 * 
	 * @param menu
	 *            menu to add
	 */
	public static void addMenu(final JMenu menu) {
		final JMenuBar menuBar = AscapeGUIUtil.getUserFrame().getJMenuBar();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				menuBar.add(menu);

				// needed for newly added menu to
				// become visible immediately
				menuBar.revalidate();

			}
		});

	}

	/**
	 * A helper method to add a button to the provided menu for the specified
	 * action.
	 * 
	 * @param menu
	 *            the menu
	 * @param action
	 *            the action
	 */
	public static void addMenuActionNoIcon(JMenu menu, Action action) {
		JMenuItem item = new JMenuItem(action);
		// Icons in menus are annoying...
		// We set a blank one so that checkboxes won't look weird.
		item.setIcon(MenuBarView.EMPTY_7_ICON);
		menu.add(item);
	}

	/**
	 * Set the location of the console pane.
	 * 
	 * @param location
	 *            location
	 */
	public static void setConsoleDividerLocation(final int location) {
		DesktopEnvironment desktop = AscapeGUIUtil.getDesktopEnvironment();
		if (desktop == null) {
			throw new IllegalStateException(
					"Not running in desktop environment, "
							+ "or desktop environment not yet loaded.");
		}

		final JSplitPane consoleSplit =
				desktop.getUserFrame().getConsoleSplit();

		Runnable doWorkRunnable = new Runnable() {
			public void run() {
				// move the console pane at the bottom
				// by changing the split divider location
				consoleSplit.setDividerLocation(location);
			}
		};
		SwingUtilities.invokeLater(doWorkRunnable);

	}

	/**
	 * Get the additional tool bar.
	 * 
	 * @return additional tool bar.
	 */
	public static JToolBar getAdditionalBar() {
		return getSwingEnvironment().getControlBarView().getAdditionalBar();
	}

	/**
	 * Add a button to the additional tool bar.
	 * 
	 * @param action
	 *            action to add as a button
	 * @return newly created and added button
	 */
	public static JButton addAdditionalBarButton(Action action) {
		
		JToolBar addBar = AscapeGUIUtil.getAdditionalBar();
		
		// add button to toolbar
		JButton button =
				DesktopEnvironment.addToolBarButton(addBar, action);

		// this is needed to get the button to display
		getUserFrame().validate();

		return button;

	}
	

	/**
	 * Size a table to the smaller of its preferred size or maxSize.
	 * 
	 * @param table
	 *            table to display.
	 * @param maxSize
	 *            max display dimensions of the table, less
	 *            {@link #TABLE_BORDER_EDGES}. If {@code null} will display the
	 *            table at it's preferred size.
	 * @return PanelView containing the table.
	 */
	public static void sizeTable(JTable table, Dimension maxSize) {
		// sizeAllColumnsToHeaderWidths(table);

		// tell the scroll pane enclosing the table to size its viewport
		// to the smaller of the table's preferred size or maxSize
		if (maxSize == null) {
			table
					.setPreferredScrollableViewportSize(table
							.getPreferredSize());
		} else {

			Dimension prefSize = table.getPreferredSize();

			Dimension setSize =
					min(prefSize, subtract(maxSize, TABLE_BORDER_EDGES));

			table.setPreferredScrollableViewportSize(setSize);
		}

	}

	/**
	 * Subtract one dimension from another.
	 * 
	 * @param d1
	 *            dimension 1
	 * @param d2
	 *            dimension 2
	 * @return {@code d1 - d2}
	 */
	public static Dimension subtract(Dimension d1, Dimension d2) {
		return new Dimension(d1.width - d2.width, d1.height - d2.height);
	}

	/**
	 * Return the smallest width and height of two dimensions.
	 * 
	 * @param d1
	 *            dimension 1
	 * @param d2
	 *            dimension 2
	 * @return new dimensions with {@code width = min(d1.width,d2.width} and
	 *         {@code height = min(d1.height,d2.height} .
	 */
	public static Dimension min(Dimension d1, Dimension d2) {
		return new Dimension(Math.min(d1.width, d2.width), Math.min(
				d1.height, d2.height));
	}

}