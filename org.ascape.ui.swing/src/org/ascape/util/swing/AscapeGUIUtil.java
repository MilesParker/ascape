package org.ascape.util.swing;

import java.awt.Dimension;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import org.ascape.model.Scape;
import org.ascape.runtime.RuntimeEnvironment;
import org.ascape.runtime.swing.DesktopEnvironment;
import org.ascape.runtime.swing.SwingEnvironment;
import org.ascape.runtime.swing.UserFrame;
import org.ascape.runtime.swing.ViewFrameBridge;
import org.ascape.runtime.swing.navigator.TreeBuilder;
import org.ascape.view.vis.control.MenuBarView;

/**
 * Utility functions related to the Ascape Swing GUI.
 * 
 * @author Oliver Mannion
 * @version $Revision: 44 $
 */
public final class AscapeGUIUtil {

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

	public static JInternalFrame getCurrentlySelected() {
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
		final JMenuBar menuBar =
				AscapeGUIUtil.getUserFrame().getJMenuBar();

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

}