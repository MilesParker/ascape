package org.ascape;

import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class AscapeRuntimePlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.eclipse.amp.escape.runtime";

    // The shared instance
    private static AscapeRuntimePlugin plugin;

    /**
     * The constructor
     */
    public AscapeRuntimePlugin() {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static AscapeRuntimePlugin getDefault() {
        return plugin;
    }

    Color createCurrent;

    public Color createColor(final int colorCode) {
        getWorkbench().getDisplay().syncExec(new Runnable() {
            public void run() {
                createCurrent = getWorkbench().getDisplay().getSystemColor(colorCode);
            }
        });
        return createCurrent;
    }

    public Color createColor(final int red, final int green, final int blue) {
        getWorkbench().getDisplay().syncExec(new Runnable() {
            public void run() {
                createCurrent = new Color(getWorkbench().getDisplay(), red, green, blue);
            }
        });
        return createCurrent;
    }
}
