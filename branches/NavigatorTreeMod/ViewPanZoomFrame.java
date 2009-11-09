/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.runtime.swing;

import java.awt.Dimension;

/**
 * A wrapper for JInternalFrame that provides an approriate sizing.
 * 
 * @author Miles Parker
 * @version 3.0
 * @history 3.0 9/1/02 first in
 * @since 3.0
 */
public interface ViewPanZoomFrame {

    /**
     * Gets the preferred size within.
     * 
     * @param d
     *            the d
     * @return the preferred size within
     */
    public Dimension getPreferredSizeWithin(Dimension d);

    /**
     * Gets the size for agent size.
     * 
     * @param size
     *            the size
     * @return the size for agent size
     */
    public Dimension getSizeForAgentSize(int size);

    /**
     * Gets the bridge.
     * 
     * @return the bridge
     */
    public ViewFrameBridge getBridge();
}
