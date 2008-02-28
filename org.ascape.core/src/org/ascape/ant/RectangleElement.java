/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.ant;

import java.awt.Rectangle;
import java.io.Serializable;

/**
 * Neccessary only because Java still doesn't allow x,y width and height vlaues
 * to be set seperatly!.
 */
public class RectangleElement extends Rectangle implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Sets the x.
     * 
     * @param x
     *            the new x
     */
    public void setX(int x) {
        setLocation(x, y);
    }

    /**
     * Sets the y.
     * 
     * @param y
     *            the new y
     */
    public void setY(int y) {
        setLocation(x, y);
    }

    /**
     * Sets the width.
     * 
     * @param width
     *            the new width
     */
    public void setWidth(int width) {
        setSize(width, height);
    }

    /**
     * Sets the height.
     * 
     * @param height
     *            the new height
     */
    public void setHeight(int height) {
        setSize(width, height);
    }
}
