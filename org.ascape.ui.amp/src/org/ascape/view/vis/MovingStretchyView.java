/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.view.vis;

import java.util.Iterator;

import org.ascape.model.Agent;
import org.eclipse.draw2d.Graphics;

/**
 * A view of a scape vector. <i>Currently unmaintained, but works.</i>
 * 
 * @since 1.0
 */
public class MovingStretchyView extends StretchyView {

    private static final long serialVersionUID = 3927697494980335781L;

    /**
     * Instantiates a new moving stretchy view.
     * 
     * @param maxElements
     *        the max elements
     * @param maxDisplayAgents
     *        the max display agents
     */
    public MovingStretchyView() {
        super("Moving Stretchy View");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.view.vis.AgentView#updateScapeGraphics(Graphics)
     */
    public synchronized void updateScapeGraphics(Graphics graphics) {
        if (getScape() != null && getScape().isInitialized()) {
            Iterator n = getScape().iterator();
            int i = 0;
            while (n.hasNext()) {
                Agent agent = (Agent) n.next();
                // int s = (int) agent.getAttributeValue(barAttribute1) * magnification;
                drawAgentAt(graphics, agent, i);
                i++;
            }
        }
        super.updateScapeGraphics(graphics);
    }
}
