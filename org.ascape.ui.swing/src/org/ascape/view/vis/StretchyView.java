/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.view.vis;

import java.awt.Dimension;
import java.awt.Graphics;

/**
 * A view of a scape vector. <i>Currently unmaintained, but should work.</i>
 * 
 * @author Miles Parker
 * @version 1.0
 * @since 1.0
 */
public class StretchyView extends CellView {

    /**
     * The maximum number of agents that could ever appear in the view.
     */
    int maxAgents = 50;

    /**
     * The number of agents that will be displayed, fixed.
     */
    private int maxDisplayAgents;

    /**
     * Instantiates a new stretchy view.
     */
    public StretchyView() {
    }

    /**
     * Instantiates a new stretchy view.
     * 
     * @param maxAgents
     *            the max agents
     * @param maxDisplayAgents
     *            the max display agents
     */
    public StretchyView(int maxAgents, int maxDisplayAgents) {
        this.maxAgents = maxAgents;
        this.maxDisplayAgents = maxDisplayAgents;
        setCellSize(4);
        //setSize(new Dimension(600, 300));
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.AgentSizedView#getPreferredSizeWithin(java.awt.Dimension)
     */
    public Dimension getPreferredSizeWithin(Dimension d) {
        int tempCellSize = calculateAgentSizeForViewSize(d);
        return new Dimension(maxDisplayAgents * tempCellSize,
            (int) d.getHeight());
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.AgentSizedView#calculateViewSizeForAgentSize(int)
     */
    public Dimension calculateViewSizeForAgentSize(int cellSize) {
        if ((int) getSize().getHeight() > 0) {
            return new Dimension(maxDisplayAgents * cellSize,
                (int) getSize().getHeight());
        } else {
            return new Dimension(maxDisplayAgents * cellSize,
                300);
        }
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.AgentSizedView#calculateAgentSizeForViewSize(java.awt.Dimension)
     */
    public int calculateAgentSizeForViewSize(Dimension d) {
        int width = (int) d.getWidth() / maxDisplayAgents;
        return Math.max(1, width);
    }

    /**
     * Sets the bar value attribute1.
     * 
     * @param barAttribute1
     *            the new bar value attribute1
     */
    public void setBarValueAttribute1(String barAttribute1) {
    }

    /**
     * Sets the bar value attribute2.
     * 
     * @param barAttribute2
     *            the new bar value attribute2
     */
    public void setBarValueAttribute2(String barAttribute2) {
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.AgentView#paintComponent(java.awt.Graphics)
     */
    public synchronized void paintComponent(Graphics g) {
        drawSelectedAgent(bufferedGraphics);
        if (bufferedImage != null) {
            if (g.drawImage(bufferedImage, 0, 0, null)) {
                getDelegate().viewPainted();
            }
        }
    }
}
