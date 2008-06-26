/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.explorer;

import java.applet.Applet;
import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.ascape.model.ModelRoot;
import org.ascape.model.Scape;


/**
 * This class just provides a scape aware applet. It allows any scape model
 * (that adds views to the applet panel correctly) to be run within an applet,
 * delegating the applet init start and stop methods to the scape. Example:
 * <code>&lt;APPLET name=AppletName codebase=[path] &lt;param name="Scape" value="mypath.MyModel"&gt;&gt;&lt;/APPLET&gt;&lt;BR&gt;</code>
 * 
 * @see org.ascape.model.Scape
 * @author Miles Parker
 * @version 1.0.1
 * @history 1.0.1 03/06/1999 Cleaned up, documentation
 * @since 1.0
 */
public class ModelApplet extends Applet {

    /**
     * The root (model) scape that this applet is displaying.
     */
    private Scape scape;

    /**
     * The main applet panel.
     */
    private JPanel panel;

    /**
     * Returns the model (root) scape that this applet is displaying.
     * 
     * @return the scape
     */
    public Scape getScape() {
        return scape;
    }

    /**
     * Sets the model (root) scape that this Applet is displaying.
     * 
     * @param scape
     *            the scape to display.
     */
    public void setScape(Scape scape) {
        this.scape = scape;
    }

    /**
     * Returns the main panel for this applet.
     * 
     * @return the panel
     */
    public JPanel getPanel() {
        return panel;
    }

    /**
     * Delegates to the start method of the scape model.
     */
    public void start() {
        UserEnvironment.getDefaultEnvironment().setViewMode(UserEnvironment.APPLET_VIEW_MODE);
        scape = ModelRoot.open(getParameter("Model"), this);
    }

    /**
     * Delegates to the stop method of the scape model.
     */
    public void stop() {
        scape.getModel().stop();
    }

    /**
     * Sets up the Applet, creating the main panel. Essentially delgates the
     * functionality of Scape's main method, opening the scape specified by the
     * Scape parameter and launching it.
     */
    public void init() {
        panel = new JPanel();
        setLayout(new BorderLayout());
        add("Center", panel);
    }
}
