/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.view.vis;


import org.ascape.model.Agent;
import org.ascape.model.Scape;

/**
 * A class providing a view of an scape vector. <i>Currently unmaintained.</i>
 * 
 * @since 1.0
 */
public class FixedAgentStretchyView extends FixedStretchyView {

    /**
     * Instantiates a new fixed agent stretchy view.
     * 
     * @param maxElements
     *            the max elements
     * @param maxDisplayAgents
     *            the max display agents
     */
    public FixedAgentStretchyView(int maxElements, int maxDisplayAgents) {
        super(maxElements, maxDisplayAgents);
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.FixedStretchyView#drawAgentAt(org.ascape.model.Agent, int)
     */
    public void drawAgentAt(Agent agent, int position) {
        if (agent != null) {
            int s = ((Scape) agent).getSize() * agentSize;
            Object[] members = ((Scape) agent).toArray();
            for (int i = 0; i < members.length; i++) {
                bufferedGraphics.setColor(((Agent) members[members.length - i - 1]).getColor());
                bufferedGraphics.fillRect(position * agentSize, bufferedImage.getHeight(null) - s + (i * agentSize), agentSize, agentSize);
            }
        }
    }
}
