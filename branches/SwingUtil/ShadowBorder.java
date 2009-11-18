/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.BevelBorder;


/**
 * A class which implements a shadow border, or BevelBorder of thickness 1. Copied from sun's BevelBorder.
 *
 * @version 1.2 7/16/99
 * @author Miles Parker
 * @author David Kloba
 */
public class ShadowBorder extends BevelBorder {


    /**
     * 
     */
    private static final long serialVersionUID = -6329334022969277925L;

    /**
     * Creates a bevel border with the specified type and whose
     * colors will be derived from the background color of the
     * component passed into the paintBorder method.
     * @param bevelType the type of bevel for the border
     */
    public ShadowBorder(int bevelType) {
        super(bevelType);
    }

    /**
     * Creates a bevel border with the specified type, highlight and
     * shadow colors.
     * @param bevelType the type of bevel for the border
     * @param highlight the color to use for the bevel highlight
     * @param shadow the color to use for the bevel shadow
     */
    public ShadowBorder(int bevelType, Color highlight, Color shadow) {
        this(bevelType, highlight.brighter(), highlight, shadow, shadow.brighter());
    }

    /**
     * Creates a bevel border with the specified type, highlight
     * shadow colors.
     * @param bevelType the type of bevel for the border
     * @param highlightOuter the color to use for the bevel outer highlight
     * @param highlightInner the color to use for the bevel inner highlight
     * @param shadowOuter the color to use for the bevel outer shadow
     * @param shadowInner the color to use for the bevel inner shadow
     */
    public ShadowBorder(int bevelType, Color highlightOuter, Color highlightInner,
                        Color shadowOuter, Color shadowInner) {
        this(bevelType);
        this.highlightOuter = highlightOuter;
        this.highlightInner = highlightInner;
        this.shadowOuter = shadowOuter;
        this.shadowInner = shadowInner;
    }

    /**
     * Returns the insets of the border.
     * @param c the component for which this border insets value applies
     */
    public Insets getBorderInsets(Component c) {
        return new Insets(1, 1, 1, 1);
    }

    /**
     * Reinitialize the insets parameter with this Border's current Insets.
     * @param c the component for which this border insets value applies
     * @param insets the object to be reinitialized
     */
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.top = insets.right = insets.bottom = 1;
        return insets;
    }

    /**
     * Paints the border for the specified component with the specified
     * position and size.
     * @param c the component for which this border is being painted
     * @param g the paint graphics
     * @param x the x position of the painted border
     * @param y the y position of the painted border
     * @param width the width of the painted border
     * @param height the height of the painted border
     */
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        if (bevelType == RAISED) {
            paintRaisedBevel(c, g, x, y, width, height);

        } else if (bevelType == LOWERED) {
            paintLoweredBevel(c, g, x, y, width, height);
        }
    }

    protected void paintRaisedBevel(Component c, Graphics g, int x, int y,
                                    int width, int height) {
        Color oldColor = g.getColor();
        int h = height;
        int w = width;

        g.translate(x, y);

        g.setColor(getHighlightOuterColor(c));
        g.drawLine(0, 0, 0, h - 1);
        //g.drawLine(1, 0, w-1, 0);

        g.setColor(getHighlightInnerColor(c));
        g.drawLine(1, 1, 1, h - 2);
        //g.drawLine(2, 1, w-2, 1);

        g.setColor(getShadowOuterColor(c));
        g.drawLine(1, h - 1, w - 1, h - 1);
        //g.drawLine(w-1, 1, w-1, h-2);

        g.setColor(getShadowInnerColor(c));
        g.drawLine(2, h - 2, w - 2, h - 2);
        //g.drawLine(w-2, 2, w-2, h-3);

        g.translate(-x, -y);
        g.setColor(oldColor);

    }

    protected void paintLoweredBevel(Component c, Graphics g, int x, int y,
                                     int width, int height) {
        Color oldColor = g.getColor();
        int h = height;
        int w = width;

        g.translate(x, y);

        g.setColor(getShadowOuterColor(c));
        g.drawLine(0, 0, 0, h - 1);
        g.drawLine(1, 0, w - 1, 0);

        /*g.setColor(getShadowOuterColor(c));
        g.drawLine(1, 1, 1, h-2);
        g.drawLine(2, 1, w-2, 1);*/

        g.setColor(getHighlightOuterColor(c));
        g.drawLine(1, h - 1, w - 1, h - 1);
        g.drawLine(w - 1, 1, w - 1, h - 2);

        /*g.setColor(getHighlightInnerColor(c));
        g.drawLine(2, h-2, w-2, h-2);
        g.drawLine(w-2, 2, w-2, h-3);*/

        g.translate(-x, -y);
        g.setColor(oldColor);

    }
}

