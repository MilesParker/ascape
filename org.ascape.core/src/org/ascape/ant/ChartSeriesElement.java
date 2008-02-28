/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.ant;

import java.awt.Color;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * The Class ChartSeriesElement.
 */
public class ChartSeriesElement implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The name.
     */
    String name;

    /**
     * The color.
     */
    Color color;

    /**
     * The color for name.
     */
    static Map colorForName = new HashMap();

    static {
        colorForName.put("BLACK", Color.BLACK);
        colorForName.put("BLUE", Color.BLUE);
        colorForName.put("CYAN", Color.CYAN);
        colorForName.put("DARK_GRAY", Color.DARK_GRAY);
        colorForName.put("GRAY", Color.GRAY);
        colorForName.put("GREEN", Color.GREEN);
        colorForName.put("LIGHT_GRAY", Color.LIGHT_GRAY);
        colorForName.put("MAGENTA", Color.MAGENTA);
        colorForName.put("ORANGE", Color.ORANGE);
        colorForName.put("PINK", Color.PINK);
        colorForName.put("RED", Color.RED);
        colorForName.put("WHITE", Color.WHITE);
        colorForName.put("YELLOW", Color.YELLOW);
    }

    /**
     * Gets the color.
     * 
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the color name.
     * 
     * @param colorName
     *            the new color name
     */
    public void setColorName(String colorName) {
        this.color = (Color) colorForName.get(colorName.toUpperCase());
    }

    /**
     * Sets the color hex.
     * 
     * @param colorHexCode
     *            the new color hex
     */
    public void setColorHex(String colorHexCode) {
        this.color = Color.decode(colorHexCode);
    }

    /**
     * Gets the value name.
     * 
     * @return the value name
     */
    public String getValueName() {
        return name;
    }

    /**
     * Sets the value name.
     * 
     * @param name
     *            the new value name
     */
    public void setValueName(String name) {
        this.name = name;
    }
}
