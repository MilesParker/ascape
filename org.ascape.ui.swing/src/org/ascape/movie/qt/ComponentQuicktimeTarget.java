/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.movie.qt;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import quicktime.app.image.QTImageDrawer;


/**
 * Manages as a component as a target of a quicktime movie.
 *
 * @author Miles Parker
 * @version 2.9 (Ascape)
 * @history 5/9/2002 first in
 */
public class ComponentQuicktimeTarget implements QuicktimeTarget {

    private Component component;

    private int width, height;
    //private int loopslot = 1;
    private Rectangle[] ret = new Rectangle[1];

    /**
     * Constructs the target.
     */
    public ComponentQuicktimeTarget() {
    }

    /**
     * Constructs the target.
     *
     * @param component the component that we are recording
     */
    public ComponentQuicktimeTarget(Component component) {
        this.component = component;
    }

    /**
     * Manages notification of resizing from quicktime.
     */
    public void newSizeNotified(QTImageDrawer drawer, Dimension d) {
        width = d.width;
        height = d.height;
        ret[0] = new Rectangle(width, height);
    }

    /**
     * Manages painting for quicktime.
     */
    public Rectangle[] paint(Graphics g) {
        ret[0] = new Rectangle(width, height);
        component.paintAll(g);
        return ret;
    }

    /**
     * Returns the component being recorded.
     */
    public Component getComponent() {
        return component;
    }

    /**
     * Sets the component to record.
     */
    public void setComponent(Component component) {
        this.component = component;
    }

    /**
     * Returns the component string.
     */
    public String toString() {
        return component.toString();
    }
}
