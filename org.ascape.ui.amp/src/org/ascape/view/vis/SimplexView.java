/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.view.vis;

import java.util.Iterator;

import org.ascape.model.Agent;
import org.ascape.util.vis.ColorFeature;
import org.ascape.util.vis.SimplexFeature;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Polygon;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;



/**
 * A scape view that represents some aspect of agent state in a simplex diagram.
 * 
 * @author Miles Parker
 * @history 1.5 12/1/99 first in
 * @version 1.5
 * @since 1.5
 */
public class SimplexView extends GraphicsView {

    private static final long serialVersionUID = -1696040917980876267L;

    /**
     * The simplex feature.
     */
    private SimplexFeature simplexFeature;

    /**
     * The Class ConcreteSimplexFeature.
     */
    public class ConcreteSimplexFeature extends SimplexFeature {

        /**
         * The axis1 value.
         */
        float axis1Value;

        /**
         * The axis2 value.
         */
        float axis2Value;

        /**
         * The axis3 value.
         */
        float axis3Value;

        /**
         * Instantiates a new concrete simplex feature.
         * 
         * @param axis1Value
         *        the axis1 value
         * @param axis2Value
         *        the axis2 value
         * @param axis3Value
         *        the axis3 value
         */
        public ConcreteSimplexFeature(float axis1Value, float axis2Value, float axis3Value) {
            this.axis1Value = axis1Value;
            this.axis2Value = axis2Value;
            this.axis3Value = axis3Value;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.ascape.util.vis.SimplexFeature#getAxis1Value(java.lang.Object)
         */
        public float getAxis1Value(Object object) {
            return axis1Value;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.ascape.util.vis.SimplexFeature#getAxis1Name()
         */
        public String getAxis1Name() {
            return "";
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.ascape.util.vis.SimplexFeature#getAxis2Value(java.lang.Object)
         */
        public float getAxis2Value(Object object) {
            return axis2Value;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.ascape.util.vis.SimplexFeature#getAxis2Name()
         */
        public String getAxis2Name() {
            return "";
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.ascape.util.vis.SimplexFeature#getAxis3Value(java.lang.Object)
         */
        public float getAxis3Value(Object object) {
            return axis3Value;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.ascape.util.vis.SimplexFeature#getAxis3Name()
         */
        public String getAxis3Name() {
            return "";
        }
    }

    /**
     * The display centroid.
     */
    private boolean displayCentroid;

    /**
     * The centroid.
     */
    private ConcreteSimplexFeature centroid;

    /**
     * The z1.
     */
    private ConcreteSimplexFeature z1;

    /**
     * The z2.
     */
    private ConcreteSimplexFeature z2;

    /**
     * The z3.
     */
    private ConcreteSimplexFeature z3;

    /**
     * The gap.
     */
    private static int gap = 30;

    /**
     * The x1.
     */
    private int x1;

    /**
     * The y1.
     */
    private int y1;

    /**
     * The x2.
     */
    private int x2;

    /**
     * The y2.
     */
    private int y2;

    /**
     * The x3.
     */
    private int x3;

    /**
     * The y3.
     */
    private int y3;

    /**
     * The x span.
     */
    private int xSpan;

    /**
     * The y span.
     */
    private int ySpan;

    /**
     * The inner triangle.
     */
    private Polygon innerTriangle;

    /**
     * The play low poly.
     */
    private Polygon playLowPoly;

    /**
     * The play medium poly.
     */
    private Polygon playMediumPoly;

    /**
     * The play high poly.
     */
    private Polygon playHighPoly;

    private boolean firstTime = true;

    /**
     * Constructs an overhead two-dimensional view.
     */
    public SimplexView(String name) {
        super(name);
        // Just a place holder
        setSimplexFeature(new ConcreteSimplexFeature(0.0f, 0.0f, 0.0f));
    }

    /**
     * Constructs an overhead two-dimensional view.
     * 
     * @param data.name
     *        a user relevant name for this view
     * @param centroidTradeOff
     *        the centroid trade off
     */
    public SimplexView() {
        this("Simplex View");
    }

    /**
     * Returns the preferred size of this view, which is the size of the lattice times this views agentSize.
     * 
     * @return the preferred size
     */
    public Dimension getPreferredSize() {
        return new Dimension(400, 300);
    }

    /**
     * Sets the centroid tradeoff.
     * 
     * @param t
     *        the new centroid tradeoff
     */
    public synchronized void setCentroidTradeoff(float t) {
        displayCentroid = true;
        float cl = t / (1.0f - t);
        float cm = (t - 2.0f * (float) Math.pow(t, 2.0f)) / (1.0f - t);
        float ch = 1.0f - 2.0f * t;
        centroid = new ConcreteSimplexFeature(ch, cm, cl);
        float z1l = 1.0f / (2.0f * (1.0f - t));
        float z1m = (1.0f - 2.0f * t) / (2.0f - 2.0f * t);
        float z1h = 0.0f;
        z1 = new ConcreteSimplexFeature(z1h, z1m, z1l);
        float z2l = t / (1.0f - t);
        float z2m = 0.0f;
        float z2h = (1.0f - 2.0f * t) / (1.0f - t);
        z2 = new ConcreteSimplexFeature(z2h, z2m, z2l);
        float z3l = 0.0f;
        float z3m = 2.0f * t;
        float z3h = 1.0f - 2.0f * t;
        z3 = new ConcreteSimplexFeature(z3h, z3m, z3l);
    }

    /**
     * Gets the x position.
     * 
     * @param f
     *        the f
     * @param a
     *        the a
     * @return the x position
     */
    public int getXPosition(SimplexFeature f, Agent a) {
        return (int) (xSpan * f.getAxis3Value(a) + xSpan / 2.0 * f.getAxis1Value(a)) + x2;
    }

    /**
     * Gets the y position.
     * 
     * @param f
     *        the f
     * @param a
     *        the a
     * @return the y position
     */
    public int getYPosition(SimplexFeature f, Agent a) {
        return (int) (ySpan * (1.0 - f.getAxis1Value(a))) + y1;
    }

    /**
     * On notification of a scape update, draws the actual overhead view.
     */
    public synchronized void updateScapeGraphics(Graphics graphics) {
        super.updateScapeGraphics(graphics);
        if (firstTime) {
            // graphics.fillRectangle(getPanel().getBounds());
            graphics.setForegroundColor(ColorFeature.BLACK);
            x1 = getPreferredSize().width / 2;
            y1 = gap;
            x2 = gap;
            y2 = getPreferredSize().height - gap;
            x3 = getPreferredSize().width - gap;
            y3 = getPreferredSize().height - gap;
            innerTriangle = new Polygon();
            innerTriangle.addPoint(new Point(x1, y1 + 2));
            innerTriangle.addPoint(new Point(x2 + 2, y2 - 1));
            innerTriangle.addPoint(new Point(x3 - 2, y3 - 1));
            graphics.drawOval(x1 - 1, y1 - 1, 2, 2);
            // graphics.drawLine(x1, y1, x2, y2);
            graphics.drawString(simplexFeature.getAxis1Name(), x1, y1 - 15);
            graphics.drawOval(x2 - 1, y2 - 1, 2, 2);
            // graphics.drawLine(x2, y2, x3, y3);
            graphics.drawString(simplexFeature.getAxis2Name(), x2, y2 + 15);
            graphics.drawOval(x3 - 2, y3 - 2, 2, 2);
            // graphics.drawLine(x3, y3, x1, y1);
            graphics.drawString(simplexFeature.getAxis3Name(), x3, y3 + 15);
            /*
             * y1 += 6; x2 += 6; y2 -= 1; x3 -= 6; y3 -= 1;
             */
            xSpan = x3 - x2;// - 2;
            ySpan = y2 - y1;// - 2;
            if (displayCentroid) {
                playLowPoly = new Polygon();
                playLowPoly.addPoint(new Point(getXPosition(z3, null) - 1, getYPosition(z3, null)));
                playLowPoly.addPoint(new Point(getXPosition(centroid, null), getYPosition(centroid, null)));
                playLowPoly.addPoint(new Point(getXPosition(z2, null) + 2, getYPosition(z2, null)));
                playLowPoly.addPoint(new Point(x1, y1 - 2));
                playMediumPoly = new Polygon();
                playMediumPoly.addPoint(new Point(getXPosition(z3, null) - 1, getYPosition(z3, null)));
                playMediumPoly.addPoint(new Point(getXPosition(centroid, null), getYPosition(centroid, null)));
                playMediumPoly.addPoint(new Point(getXPosition(z1, null), getYPosition(z1, null) + 2));
                playMediumPoly.addPoint(new Point(x2 - 3, y2 + 2));
                playHighPoly = new Polygon();
                playHighPoly.addPoint(new Point(getXPosition(z2, null) + 2, getYPosition(z2, null)));
                playHighPoly.addPoint(new Point(getXPosition(centroid, null), getYPosition(centroid, null)));
                playHighPoly.addPoint(new Point(getXPosition(z1, null), getYPosition(z1, null) + 2));
                playHighPoly.addPoint(new Point(x3 + 3, y3 + 2));
            }
            firstTime = false;
        }
        if (displayCentroid) {
            graphics.setBackgroundColor(ColorFeature.BLUE);
            graphics.fillPolygon(playLowPoly.getPoints().toIntArray());
            graphics.setBackgroundColor(ColorFeature.GREEN);
            graphics.fillPolygon(playMediumPoly.getPoints().toIntArray());
            graphics.setForegroundColor(ColorFeature.RED);
            graphics.fillPolygon(playHighPoly.getPoints().toIntArray());
        } else {
            graphics.setBackgroundColor(ColorFeature.WHITE);
            graphics.fillPolygon(innerTriangle.getPoints().toIntArray());
        }
        for (Iterator iterator = getScape().iterator(); iterator.hasNext();) {
            Agent agent = (Agent) iterator.next();
            graphics.setBackgroundColor(ColorFeature.BLACK);
            graphics.fillRectangle(getXPosition(simplexFeature, agent), getYPosition(simplexFeature, agent), 2, 2);
        }
    }

    /**
     * Returns the color feature this object is using to interpret the object's color.
     * 
     * @return the simplex feature
     */
    public SimplexFeature getSimplexFeature() {
        return simplexFeature;
    }

    /**
     * Sets the color feature this object uses to interpret the object's color.
     * 
     * @param simplexFeature
     *        the feature to use for coloring
     */
    public void setSimplexFeature(SimplexFeature simplexFeature) {
        this.simplexFeature = simplexFeature;
        firstTime = true;
    }
}
