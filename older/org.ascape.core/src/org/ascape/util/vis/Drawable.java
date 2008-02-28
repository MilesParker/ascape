/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.vis;

import java.awt.Graphics;

/**
 * An interface for a class that is capable of drawing itself into a  graphics context with a defined area.
 * @author Miles Parker
 * @version 1.0
 * @since 1.0
 */
public interface Drawable {

    /**
     * Draws a graphic interpretation of this object, or a delegate to this object.
     * within the supplied dimensions, assuming origin as {0, 0}.
     * @param g the Graphics context to draw into
     * @param width the width of the space that should be drawn into
     * @param height the height of the space that should be drawn into
     */
    public void draw(Graphics g, int width, int height);
}
