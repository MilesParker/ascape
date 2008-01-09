/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.view.vis;

import java.awt.Color;
import java.util.Iterator;

import org.ascape.model.Agent;


/**
 * A view of a scape vector. <i>Currently unmaintained, but works.</i>
 * 
 * @since 1.0
 */
public class MovingStretchyView extends StretchyView {

    /**
     * Instantiates a new moving stretchy view.
     * 
     * @param maxElements
     *            the max elements
     * @param maxDisplayAgents
     *            the max display agents
     */
    public MovingStretchyView(int maxElements, int maxDisplayAgents) {
        super(maxElements, maxDisplayAgents);
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.AgentView#updateScapeGraphics()
     */
    public synchronized void updateScapeGraphics() {
        if ((getScape() != null) && (getScape().isInitialized())) {
            bufferedGraphics.setColor(getBackground());
            bufferedGraphics.fillRect(0, 0, bufferedImage.getWidth(null), bufferedImage.getHeight(null));
            bufferedGraphics.setColor(getForeground());
            Iterator n = getScape().iterator();
            int i = 0;
            while (n.hasNext()) {
                Agent agent = (Agent) n.next();
                //int s = (int) agent.getAttributeValue(barAttribute1) * magnification;
                int s = 10;
                int s0 = 0;
                //int s = ((Firm) e).age * agentSize;
                //int s0 = (int) agent.getAttributeValue(barAttribute2) * agentSize;
                if (s0 > s) {
                    //int s = ((Firm) e).age * agentSize;
                    bufferedGraphics.setColor(agent.getColor().brighter());
                    bufferedGraphics.fillRect(i * agentSize, bufferedImage.getHeight(null) - s0, agentSize - 1, s0 - 1);
                    bufferedGraphics.setColor(Color.black);
                    bufferedGraphics.drawRect(i * agentSize - 1, bufferedImage.getHeight(null) - s - 1, agentSize, s);
                    bufferedGraphics.setColor(agent.getColor());
                    bufferedGraphics.fillRect(i * agentSize, bufferedImage.getHeight(null) - s, agentSize - 1, s - 1);
                } else { //(s0 <= s)
                    bufferedGraphics.setColor(Color.black);
                    bufferedGraphics.drawRect(i * agentSize - 1, bufferedImage.getHeight(null) - s - 1, agentSize, s);
                    bufferedGraphics.setColor(agent.getColor().darker());
                    bufferedGraphics.fillRect(i * agentSize, bufferedImage.getHeight(null) - s, agentSize - 1, s - s0);
                    bufferedGraphics.setColor(agent.getColor());
                    bufferedGraphics.fillRect(i * agentSize, bufferedImage.getHeight(null) - s0, agentSize - 1, s0 - 1);
                }
                i++;
            }
        }
        super.updateScapeGraphics();
    }
}
