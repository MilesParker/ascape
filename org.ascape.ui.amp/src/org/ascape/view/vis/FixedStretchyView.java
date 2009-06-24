/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.view.vis;

import java.util.Iterator;

import org.ascape.model.Agent;
import org.ascape.model.LocatedAgent;
import org.ascape.model.event.ScapeEvent;
import org.ascape.util.vis.ColorFeature;
import org.eclipse.draw2d.Graphics;



/**
 * A class providing a view of an scape vector.
 * 
 * @version 1.9
 * @history 1.9 5/1/00 added support for cell customizer
 * @since 1.0
 */
public class FixedStretchyView extends StretchyView {

    private static final long serialVersionUID = 8924203714365438295L;

    /**
     * The agent view.
     */
    private Agent[] agentView;

    /**
     * The agent found at.
     */
    private boolean[] agentFoundAt;

    boolean removeGaps;

    /**
     * Instantiates a new fixed stretchy view.
     * 
     * @param maxElements
     *        the max elements
     * @param maxDisplayElements
     *        the max display elements
     */
    public FixedStretchyView(String name, int maxElements, int maxDisplayElements) {
        super(name, maxElements, maxDisplayElements);
    }

    public FixedStretchyView() {
        this("Fixed Stretchy view", 5000, 5000);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.view.vis.PanelView#scapeSetup(org.ascape.model.event.ScapeEvent)
     */
    public void scapeInitialized(ScapeEvent scapeEvent) {
        agentView = new Agent[maxAgents];
        agentFoundAt = new boolean[maxAgents];
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.view.vis.AgentView#updateScapeGraphics(Graphics)
     */
    public void updateScapeGraphics(Graphics graphics) {
        // TODO reimplement w/ hashmaps

        graphics.fillRectangle(0, 0, image.getBounds().width, image.getBounds().height);
        if (getScape() != null && getScape().isInitialized()) {
            int agentsSize = getScape().getSize();
            if (!removeGaps) {
                for (int i = 0; i < agentView.length; i++) {
                    agentFoundAt[i] = false;
                }
            } else {
                for (int i = 0; i < agentView.length; i++) {
                    agentFoundAt[i] = false;
                    agentView[i] = null;
                }
            }
            synchronized (getScape()) {
                Iterator n = getScape().iterator();
                while (n.hasNext()) {
                    Agent agent = (Agent) n.next();
                    boolean agentFound = false;
                    int firstNull = agentsSize;
                    for (int i = 0; i < agentView.length; i++) {
                        if (agentView[i] == null) {
                            if (firstNull == agentsSize) {
                                firstNull = i;
                            }
                        } else if (agentView[i] == agent) {
                            agentFound = true;
                            agentFoundAt[i] = true;
                        }
                    }
                    if (!agentFound) {
                        agentView[firstNull] = agent;
                        agentFoundAt[firstNull] = true;
                    }
                }
                for (int i = 0; i < agentView.length; i++) {
                    if (!agentFoundAt[i]) {
                        agentView[i] = null;
                    }
                }
                for (int i = 0; i < agentView.length; i++) {
                    Agent agent = agentView[i];
                    if (agent != null) {
                        drawAgentAt(graphics, agent, i);
                    }
                }
            }
        }
        super.updateScapeGraphics(graphics);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.view.vis.AgentView#getAgentAtPixel(int, int)
     */
    public Agent getAgentAtPixel(int x, int y) {
        int position = x / agentSize;
        return agentView[position];
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.view.vis.AgentView#drawSelectedAgent(java.awt.Graphics, org.ascape.model.LocatedAgent)
     */
    public void drawSelectedAgent(Graphics graphics, LocatedAgent a) {
        // TODO -- optomize
        for (int i = 0; i < agentView.length; i++) {
            if (a == agentView[i]) {
                int s = maxDisplayAgents * agentSize;
                graphics.setForegroundColor(ColorFeature.HIGHLIGHT_AGENT_COLOR);
                graphics.drawRectangle(i * agentSize - 1, getSize().width - s - 1, agentSize, s);
            }
        }
    }

    /**
     * Should gaps be removed? Default is false.
     * 
     * @return
     */
    public boolean isRemoveGaps() {
        return removeGaps;
    }

    /**
     * Should gaps be removed from model as firms are removed? (New agents then appear to the right.)
     * 
     * @param removeGaps
     */
    public void setRemoveGaps(boolean removeGaps) {
        this.removeGaps = removeGaps;
    }

    // /**
    // * Draw agent at.
    // *
    // * @param agent
    // * the agent
    // * @param position
    // * the position
    // */
    // public void drawAgentAt(Graphics graphics, Agent agent, int position) {
    // if (agent != null) {
    // int s = ((Scape) agent).getSize() * agentSize;
    // graphics.setForegroundColor(agentColorFeature.getColor(agent));
    // graphics.drawRectangle(position * agentSize - 1, getSize().height - s - 1, agentSize, s);
    // graphics.setBackgroundColor(agentColorFeature.getColor(agent));
    // graphics.fillRectangle(position * agentSize, getSize().height - s, agentSize - 1, s);
    // }
    // }
}
