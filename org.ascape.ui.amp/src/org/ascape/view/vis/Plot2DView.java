/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.view.vis;

import java.util.Iterator;

import org.ascape.model.Agent;
import org.ascape.util.data.UnitIntervalDataPoint;
import org.ascape.util.data.ValueNotInRangeException;
import org.ascape.util.vis.ColorFeature;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;



/**
 * A scape view that represents two parameters of agent state in a two-dimensional plot.
 * 
 * @author Miles Parker
 * @history 1.5 12/1/99 first in
 * @version 1.5
 * @since 1.5
 */
public class Plot2DView extends AgentView {

    private static final long serialVersionUID = 431317441800986544L;

    /**
     * The x data point.
     */
    private UnitIntervalDataPoint xDataPoint;

    /**
     * The y data point.
     */
    private UnitIntervalDataPoint yDataPoint;

    /**
     * The color feature.
     */
    private ColorFeature colorFeature;

    /**
     * The plot size.
     */
    private int plotSize;

    /**
     * Constructs an overhead two-dimensional view.
     */
    public Plot2DView() {
        super("Plot 2D View");
    }

    /**
     * Returns the preferred size of this view, which is the size of the lattice times this views agentSize.
     * 
     * @return the preferred size
     */
    public Dimension getPreferredSize() {
        return new Dimension(300, 300);
    }

    /**
     * Draw agent.
     * 
     * @param a
     *        the a
     */
    private void drawAgent(Graphics graphics, Agent a) {
        try {
            graphics.drawRectangle((int) (xDataPoint.getAssertedValue(a) * plotSize), (int) ((1.0 - yDataPoint
                    .getAssertedValue(a)) * plotSize) - 1, 1, 1);
        } catch (ValueNotInRangeException e) {
            // System.out.println("Bad value for agent");
        }
    }

    /**
     * On notification of a scape update, draws the actual overhead view.
     */
    public void updateScapeGraphics(Graphics graphics) {
        plotSize = getSize().height;
        // graphics.fillRectangle(getPanel().getBounds());
        // If the color feature is fixed, we don't haveto set it each time.
        for (Iterator iterator = getScape().iterator(); iterator.hasNext();) {
            Agent agent = (Agent) iterator.next();
            graphics.setForegroundColor(colorFeature.getColor(agent));
            drawAgent(graphics, agent);
        }
        super.updateScapeGraphics(graphics);
    }

    /**
     * Returns the data point responsible for interpreting each agent's x position.
     * 
     * @return the x data point
     */
    public UnitIntervalDataPoint getXDataPoint() {
        return xDataPoint;
    }

    /**
     * Sets the data point responsible for interpreting each agent's x position.
     * 
     * @param xDataPoint
     *        the data point to use
     */
    public void setXDataPoint(UnitIntervalDataPoint xDataPoint) {
        this.xDataPoint = xDataPoint;
    }

    /**
     * Returns the data point responsible for interpreting each agent's y position.
     * 
     * @return the y data point
     */
    public UnitIntervalDataPoint getYDataPoint() {
        return yDataPoint;
    }

    /**
     * Sets the data point responsible for interpreting each agent's y position.
     * 
     * @param yDataPoint
     *        the data point to use
     */
    public void setYDataPoint(UnitIntervalDataPoint yDataPoint) {
        this.yDataPoint = yDataPoint;
    }

    /**
     * Returns the color feature responsible for interpreting the agent's color.
     * 
     * @return the color feature
     */
    public ColorFeature getColorFeature() {
        return colorFeature;
    }

    /**
     * Sets the data point responsible for interpreting the agent's color. Note: if you use a color feature fixed, the
     * view can optimize for it.
     * 
     * @param colorFeature
     *        the color feature to use.
     */
    public void setColorFeature(ColorFeature colorFeature) {
        this.colorFeature = colorFeature;
    }
}
