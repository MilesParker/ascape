package org.ascape.util.swing;

import java.awt.Dimension;

import org.ascape.model.Scape;
import org.ascape.runtime.RuntimeEnvironment;
import org.ascape.runtime.swing.DesktopEnvironment;
import org.ascape.runtime.swing.SwingEnvironment;
import org.ascape.runtime.swing.UserFrame;
import org.ascape.runtime.swing.navigator.TreeBuilder;

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
	 */
	public static Dimension getDesktopSize(Scape scape) {
		UserFrame frame = getUserFrame(scape);
		return (frame == null) ? null : frame.getDeskScrollPane()
				.getViewport().getSize();
	}

	public static void flushConsoleLog(Scape scape) {
		UserFrame frame = getUserFrame(scape);
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
	 */
	public static UserFrame getUserFrame(Scape scape) {
		DesktopEnvironment desktop = getDesktopEnvironment(scape);
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
	 * Get the {@link SwingEnvironment}.
	 * 
	 * @param scape
	 *            scape
	 * @return {@link SwingEnvironment} or {@code null} if not running in swing
	 *         environment
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
	 * Get the {@link TreeBuilder} from the runtime environment.
	 * 
	 * @param scape
	 *            scape
	 * @return tree builder
	 */
	public static TreeBuilder getNavigatorTreeBuilder(Scape scape) {
		SwingEnvironment runtime = getSwingEnvironment(scape);
		return (runtime == null) ? null : runtime.getNavigatorTreeBuilder();
	}

	/**
	 * Set the {@link TreeBuilder} on the runtime environment.
	 * 
	 * @param scape
	 *            scape
	 * @param treeBuilder
	 *            tree builder
	 */
	public static void setNavigatorTreeBuilder(Scape scape,
			TreeBuilder treeBuilder) {
		SwingEnvironment runtime = getSwingEnvironment(scape);
		if (runtime != null) {
			runtime.setNavigatorTreeBuilder(treeBuilder);
		}
	}

}