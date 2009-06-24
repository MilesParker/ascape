/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.view.vis;

import org.ascape.model.Agent;
import org.ascape.model.Scape;
import org.ascape.util.vis.ColorFeature;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;



/**
 * A view of a scape vector. <i>Currently unmaintained, but should work.</i>
 * 
 * @author Miles Parker
 * @version 1.0
 * @since 1.0
 */
public class StretchyView extends CellView {

    private static final long serialVersionUID = 5067468202964334076L;

    /**
     * The maximum number of agents that could ever appear in the view.
     */
    int maxAgents = 50;

    /**
     * The number of agents that will be displayed, fixed.
     */
    int maxDisplayAgents;

    /**
     * Instantiates a new stretchy view.
     */
    public StretchyView(String name) {
        super(name);
    }

    /**
     * Instantiates a new stretchy view.
     * 
     * @param maxAgents
     *        the max agents
     * @param maxDisplayAgents
     *        the max display agents
     */
    public StretchyView(String name, int maxAgents, int maxDisplayAgents) {
        super(name);
        this.maxAgents = maxAgents;
        this.maxDisplayAgents = maxDisplayAgents;
        setCellSize(4);
        //setSize(new Dimension(600, 300));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.view.vis.AgentSizedView#calculateViewSizeForAgentSize(int)
     */
    public Dimension calculateViewSizeForAgentSize(Dimension availableSize, int cellSize) {
        int tempCellSize = calculateAgentSizeForViewSize(availableSize);
        return new Dimension(Math.max(1, maxDisplayAgents * tempCellSize), availableSize.height);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.view.vis.AgentSizedView#calculateAgentSizeForViewSize(java.awt.Dimension)
     */
    public int calculateAgentSizeForViewSize(Dimension d) {
        int width = d.width / Math.max(maxDisplayAgents, 1);
        return Math.max(1, width);
    }

    /**
     * Sets the bar value attribute1.
     * 
     * @param barAttribute1
     *        the new bar value attribute1
     */
    public void setBarValueAttribute1(String barAttribute1) {
    }

    /**
     * Sets the bar value attribute2.
     * 
     * @param barAttribute2
     *        the new bar value attribute2
     */
    public void setBarValueAttribute2(String barAttribute2) {
    }

    public void drawAgentAt(Graphics graphics, Agent agent, int position) {
        // int s = ((Firm) e).age * agentSize;
        // int s0 = (int) agent.getAttributeValue(barAttribute2) * agentSize;

        int s = ((Scape) agent).getSize() * agentSize;
        int s0 = 0;
        int h = getImage().getBounds().height;
        Color agentColor = agentColorFeature.getColor(agent);
        if (s0 > s) {
            // int s = ((Firm) e).age * agentSize;
            Color brighter = adjustBrightness(agentColor, 1.2f);
            graphics.setBackgroundColor(brighter);
            graphics.fillRectangle(position * agentSize, h - s0, agentSize - 1, s0 - 1);
            graphics.setBackgroundColor(ColorFeature.BLACK);
            graphics.drawRectangle(position * agentSize - 1, h - s - 1, agentSize, s);
            graphics.setBackgroundColor(agentColor);
            graphics.fillRectangle(position * agentSize, h - s, agentSize - 1, s - 1);
        } else { // (s0 <= s)
            Color darker = adjustBrightness(agentColor, 1.2f);
            graphics.setForegroundColor(ColorFeature.BLACK);
            graphics.drawRectangle(position * agentSize - 1, h - s - 1, agentSize, s);
            graphics.setBackgroundColor(darker);
            graphics.fillRectangle(position * agentSize, h - s, agentSize - 1, s - s0);
            graphics.setBackgroundColor(agentColor);
            graphics.fillRectangle(position * agentSize, h - s0, agentSize - 1, s0 - 1);
        }
    }

    public static Color adjustBrightness(Color color, float factor) {
        // TODO Cache generated values
        float[] agentHSB = color.getRGB().getHSB();
        float newBright = Math.min(1.0f, agentHSB[1] * factor);
        Color brighter = new Color(Display.getCurrent(), new RGB(agentHSB[0], newBright, agentHSB[2]));
        return brighter;
    }
    // public int getMaxAgents() {
    // return maxAgents;
    // }
    //
    // public void setMaxAgents(int maxAgents) {
    // this.maxAgents = maxAgents;
    // }
    //
    // public int getMaxDisplayAgents() {
    // return maxDisplayAgents;
    // }
    //
    // public void setMaxDisplayAgents(int maxDisplayAgents) {
    // this.maxDisplayAgents = maxDisplayAgents;
    // }
}
