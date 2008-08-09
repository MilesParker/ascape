/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.vis;

import java.awt.Graphics;


/**
 * An drawing method that draws a symbol irrespective of the supplied object's state.
 * Analagous to ColorFeatureFixed
 * @see DrawFeature
 * @see ColorFeatureFixed
 * @author Miles Parker
 * @version 1.0.1
 * @history 1.0.1 3/13/99 renamed from GraphicsSymbol
 * @since 1.0
 */
public abstract class DrawSymbol extends DrawFeature implements Drawable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * A symbol that calls fillRect.
     */
    public final static DrawSymbol FILL_RECT = new DrawSymbol() {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public void draw(Graphics g, int width, int height) {
            g.fillRect(0, 0, width - 1, height - 1);
        }
    };

    /**
     * A symbol that calls drawRect.
     */
    public final static DrawSymbol DRAW_RECT = new DrawSymbol() {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public void draw(Graphics g, int width, int height) {
            g.drawRect(0, 0, width - 1, height - 1);
        }
    };

    /**
     * A symbol that calls drawRect.
     */
    public final static DrawSymbol DRAW_RECT_2 = new DrawSymbol() {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public void draw(Graphics g, int width, int height) {
            g.drawRect(0, 0, width - 1, height - 1);
            g.drawRect(1, 1, width - 3, height - 3);
        }
    };

    /**
     * A symbol that calls fillOval.
     */
    public final static DrawSymbol FILL_OVAL = new DrawSymbol() {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public void draw(Graphics g, int width, int height) {
            g.fillOval(0, 0, width - 1, height - 1);
        }
    };

    /**
     * A symbol that calls drawOval.
     */
    public final static DrawSymbol DRAW_OVAL_2 = new DrawSymbol() {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public void draw(Graphics g, int width, int height) {
            g.drawOval(0, 0, width - 1, height - 1);
            g.drawOval(1, 1, width - 3, height - 3);
        }
    };

    /**
     * A symbol that calls drawOval.
     */
    public final static DrawSymbol DRAW_OVAL = new DrawSymbol() {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public void draw(Graphics g, int width, int height) {
            g.drawOval(0, 0, width - 1, height - 1);
        }
    };

    /**
     * A symbol that draws an 'oval' that looks good in smaller sizes.
     * (Just a rect with the end points missing.)
     */
    public final static DrawSymbol DRAW_OVOID = new DrawSymbol() {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public void draw(Graphics g, int width, int height) {
            g.drawLine(0, 1, 0, height - 2);
            g.drawLine(1, 0, width - 2, 0);
            g.drawLine(width - 1, 1, width - 1, height - 2);
            g.drawLine(1, height - 1, width - 2, height - 1);
        }
    };

    /**
     * Fills in the space inside a DRAW_OVOID.
     */
    public final static DrawSymbol FILL_OVOID = new DrawSymbol() {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public void draw(Graphics g, int width, int height) {
            g.fillRect(1, 1, width - 2, height - 2);
        }
    };

    /**
     * Draws a diagonal cross.
     */
    public final static DrawSymbol DRAW_X = new DrawSymbol() {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public void draw(Graphics g, int width, int height) {
            g.drawLine(0, 0, width - 1, height - 1);
            g.drawLine(width - 1, 0, 0, height - 1);
        }
    };

    /**
     * Draws a hash mark.
     */
    public final static DrawSymbol DRAW_HATCH = new DrawSymbol() {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public void draw(Graphics g, int width, int height) {
            for (int x = 0; x < width; x += 2) {
                g.drawLine(x, 0, x, height);
            }
            for (int y = 0; y < height; y += 2) {
                g.drawLine(0, y, width, y);
            }
        }
    };

    /**
     * Draws a hash mark with a gap of 2 spaces and width 1.
     */
    public final static DrawSymbol DRAW_HATCH_G1_W2 = new DrawSymbol() {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public void draw(Graphics g, int width, int height) {
            for (int x = 0; x - 1 < width; x += 3) {
                g.drawLine(x, 0, x, height);
                g.drawLine(x + 1, 0, x + 1, height);
            }
            for (int y = 0; y - 1 < height; y += 3) {
                g.drawLine(0, y, width, y);
                g.drawLine(0, y + 1, width, y + 1);
            }
        }
    };

    /**
     * Draws a hash mark with a gap of 2 spaces and width 1.
     */
    public final static DrawSymbol DRAW_HATCH_G2_W1 = new DrawSymbol() {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public void draw(Graphics g, int width, int height) {
            for (int x = 0; x < width; x += 3) {
                g.drawLine(x, 0, x, height);
            }
            for (int y = 0; y < height; y += 3) {
                g.drawLine(0, y, width, y);
            }
        }
    };

    /**
     * Draws a hash mark with a gap of 2 spaces and width 1.
     */
    public final static DrawSymbol DRAW_HATCH_G2_W2 = new DrawSymbol() {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public void draw(Graphics g, int width, int height) {
            for (int x = 0; x < width - 1; x += 4) {
                g.drawLine(x, 0, x, height);
                g.drawLine(x + 1, 0, x + 1, height);
            }
            for (int y = 0; y < height - 1; y += 4) {
                g.drawLine(0, y + 1, width, y + 1);
                g.drawLine(0, y + 1, width, y + 1);
            }
        }
    };

    /**
     * Draws a hash mark.
     */
    public final static DrawSymbol DRAW_HATCH_G3_W1 = new DrawSymbol() {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public void draw(Graphics g, int width, int height) {
            for (int x = 0; x < width; x += 4) {
                g.drawLine(x, 0, x, height);
            }
            for (int y = 0; y < height; y += 4) {
                g.drawLine(0, y, width, y);
            }
        }
    };

    /**
     * Draws a hash mark.
     */
    public final static DrawSymbol DRAW_HATCH_G3_W2 = new DrawSymbol() {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public void draw(Graphics g, int width, int height) {
            for (int x = 0; x < width; x += 5) {
                g.drawLine(x, 0, x, height);
                g.drawLine(x + 1, 0, x + 1, height);
            }
            for (int y = 0; y < height; y += 5) {
                g.drawLine(0, y, width, y);
                g.drawLine(0, y + 1, width, y + 1);
            }
        }
    };

    /**
     * Calls a draw method that ignores object and provides a drawable interface.
     * @param g the Graphics context to draw into
     * @param object normally, the object to interpret for drawing, ignored here
     * @param width the width of the space that should be drawn into
     * @param height the height of the space that should be drawn into
     */
    public final void draw(Graphics g, Object object, int width, int height) {
        draw(g, width, height);
    }
}
