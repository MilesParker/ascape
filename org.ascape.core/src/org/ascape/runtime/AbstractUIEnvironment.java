package org.ascape.runtime;

import org.ascape.model.Scape;
import org.ascape.model.event.ScapeCustomizer;

public abstract class AbstractUIEnvironment extends RuntimeEnvironment {

    /**
     * 
     */
    private static final long serialVersionUID = 1851545384201366560L;

    /**
     * Symbol indicating the current runtime viewMode is setup for debug.
     */
    public static final int DEBUG_RUNTIME_MODE = -1;
    /**
     * Symbol indicating the current viewMode is setup for release.
     */
    public static final int RELEASE_RUNTIME_MODE = 1;
    /**
     * Runtime viewMode; one of debug or release.
     */
    protected int runtimeMode = RELEASE_RUNTIME_MODE;

    /**
     * Have appropriate copyright messages been shown on instantiaiting
     * userEnvironment?.
     */
    static transient boolean copyrightNoticeShown = false;

    /**
     * The redirect console.
     */
    private static boolean redirectConsole = true;

    /**
     * The show navigator.
     */
    private static boolean showNavigator = true;

    /**
     * Checks if is redirect console.
     * 
     * @return true, if is redirect console
     */
    public static boolean isRedirectConsole() {
        return redirectConsole;
    }

    /**
     * Sets the redirect console.
     * 
     * @param redirectConsole
     *            the new redirect console
     */
    public static void setRedirectConsole(boolean redirectConsole) {
        AbstractUIEnvironment.redirectConsole = redirectConsole;
    }

    /**
     * Checks if is show navigator.
     * 
     * @return true, if is show navigator
     */
    public static boolean isShowNavigator() {
        return showNavigator;
    }

    /**
     * Sets the show navigator.
     * 
     * @param showNavigator
     *            the new show navigator
     */
    public static void setShowNavigator(boolean showNavigator) {
        AbstractUIEnvironment.showNavigator = showNavigator;
    }

    /**
     * A customizer for the model.
     */
    private transient ScapeCustomizer customizer;

    /**
     * Gets the customizer for the ModelRoot object.
     * 
     * @return the customizer
     */
    public ScapeCustomizer getCustomizer() {
        return customizer;
    }

    /**
     * Sets customizer for the ModelRoot object.
     * 
     * @param customizer
     *            the customizer
     */
    public void setCustomizer(ScapeCustomizer customizer) {
        this.customizer = customizer;
    }

    public AbstractUIEnvironment() {
        super();
    }

    public abstract String openDialog();

    /**
     * Returns the run time mode being used. If RELEASE_RUNTIME_MODE, Ascape
     * will attempt to deal gracefully with any exceptions, providing a user
     * dialog to report them. If DEBUG_RELEASE_MODE, Ascape will not trap
     * exceptions so that normal debugging operations will not be interferred
     * with.
     * 
     * @return the runtime mode
     */
    public int getRuntimeMode() {
        return runtimeMode;
    }

    /**
     * Sets the runtime to use. If RELEASE_RUNTIME_MODE, Ascape will attempt to
     * deal gracefully with any exceptions, providing a user dialog to report
     * them. If DEBUG_RELEASE_MODE, Ascape will not trap exceptions so that
     * normal debugging operations will not be interferred with.
     * 
     * @param _runtimeMode
     *            the _runtime mode
     */
    public void setRuntimeMode(int _runtimeMode) {
        runtimeMode = _runtimeMode;
    }

    public abstract void showErrorDialog(Scape scape, Exception e);

    public abstract boolean isInApplet();
}