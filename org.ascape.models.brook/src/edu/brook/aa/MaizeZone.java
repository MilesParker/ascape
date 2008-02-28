/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.aa;

import java.awt.Color;

import org.ascape.model.Scape;
import org.ascape.model.space.Singleton;

public class MaizeZone extends Scape {

    /**
     * 
     */
    private static final long serialVersionUID = 7378302753497194685L;
    private Color color;

    public MaizeZone(String name, Color color) {
        super(new Singleton());
        this.name = name;
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
