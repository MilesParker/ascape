package org.ascape.util.swing;

import java.awt.Dimension;

import org.ascape.model.Scape;
import org.ascape.runtime.RuntimeEnvironment;
import org.ascape.runtime.swing.DesktopEnvironment;
import org.ascape.runtime.swing.SwingEnvironment;
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
		RuntimeEnvironment runtime = scape.getRunner().getEnvironment();
		if (runtime instanceof DesktopEnvironment) {
			return ((DesktopEnvironment) runtime).getUserFrame()
					.getDeskScrollPane().getViewport().getSize();
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
		RuntimeEnvironment runtime = scape.getRunner().getEnvironment();
		if (runtime instanceof SwingEnvironment) {
			return ((SwingEnvironment) runtime).getNavigatorTreeBuilder();
		} else {
			return null;
		}
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
		RuntimeEnvironment runtime = scape.getRunner().getEnvironment();
		if (runtime instanceof SwingEnvironment) {
			((SwingEnvironment) runtime).setNavigatorTreeBuilder(treeBuilder);
		}
	}

}