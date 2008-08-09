/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.vis;

import java.awt.Graphics;

/**
 * A Draw feature that fills a cell with the color defined by the color feature.
 * @author Miles Parker
 * @version 1.0
 * @since 1.0
 */
public class FillCellFeature extends DrawColorFeature {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs the feature with the supplied name.
     * @param name the name of this draw feature
     */
    public FillCellFeature(String name) {
        super(name);
    }

    /**
     * Constructs the feature with a name and color feature.
     * @param name the name of this draw feature
     */
    public FillCellFeature(String name, ColorFeature colorFeature) {
        super(name);
        this.colorFeature = colorFeature;
    }

    public void draw(Graphics g, Object object, int width, int height) {
        g.setColor(getColor(object));
        g.fillRect(0, 0, width, height);
    }
}

;


