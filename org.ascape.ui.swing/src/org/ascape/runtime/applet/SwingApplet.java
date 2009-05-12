/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.runtime.applet;

import javax.swing.JApplet;

import org.ascape.model.Scape;
import org.ascape.runtime.swing.BasicSwingRunner;

/**
 * This class just provides a scape aware applet. It allows any scape model (that adds views to the applet panel
 * correctly) to be run within an applet, delegating the applet init start and stop methods to the scape. Example:
 * 
 * <code>&lt;APPLET name=AppletName codebase=[path] &lt;param name="Scape" value="mypath.MyModel"&gt;&gt;&lt;/APPLET&gt;&lt;BR&gt;</code>
 * 
 * @see org.ascape.model.Scape
 * @author Miles Parker
 * @version 5.1.5
 * @since 1.0
 */
public class SwingApplet extends JApplet {

    /**
     * 
     */
    private static final long serialVersionUID = 5306407518107234640L;

    /**
     * The root (model) scape that this applet is displaying.
     */
    private Scape scape;

    /**
     * Delegates to the start method of the scape model.
     */
    public void start() {
        BasicSwingRunner appletRunner = new AppletRunner();
        ((AppletEnvironment) appletRunner.getEnvironment()).setApplet(this);
        scape = appletRunner.open(getParameter("Model"), new String[] {});
        ((AppletEnvironment) appletRunner.getEnvironment()).initialize();
    }

    /**
     * Delegates to the stop method of the scape model.
     */
    public void stop() {
        scape.getRunner().stop();
    }

    /**
     * D.
     */
    public void init() {
    }
}
