/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.view.vis;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Iterator;

import org.ascape.model.Agent;
import org.ascape.model.Cell;
import org.ascape.model.LocatedAgent;
import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;

/**
 * A class providing a view of an scape vector.
 * 
 * @version 1.9
 * @history 1.9 5/1/00 added support for cell customizer
 * @since 1.0
 */
public class FixedStretchyView extends StretchyView {

    /**
     * The agent view.
     */
    private Agent[] agentView;

    /**
     * The agent found at.
     */
    private boolean[] agentFoundAt;

    /**
     * Instantiates a new fixed stretchy view.
     * 
     * @param maxElements
     *            the max elements
     * @param maxDisplayElements
     *            the max display elements
     */
    public FixedStretchyView(int maxElements, int maxDisplayElements) {
        super(maxElements, maxDisplayElements);
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.PanelView#scapeSetup(org.ascape.model.event.ScapeEvent)
     */
    public void scapeSetup(ScapeEvent scapeEvent) {
        agentView = new Agent[maxAgents];
        agentFoundAt = new boolean[maxAgents];
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.AgentView#updateScapeGraphics()
     */
    public void updateScapeGraphics() {
        if ((scape != null) && (scape.isInitialized())) {
            bufferedGraphics.setColor(getBackground());
            bufferedGraphics.fillRect(0, 0, bufferedImage.getWidth(null), bufferedImage.getHeight(null));
            bufferedGraphics.setColor(getForeground());
            int agentsSize = scape.getSize();
            for (int i = 0; i < agentView.length; i++) {
                agentFoundAt[i] = false;
            }
            Iterator n = scape.iterator();
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
                        //agentMax = i;
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
                    drawAgentAt(agent, i);
                }
            }
        }
        super.updateScapeGraphics();
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.AgentView#getAgentAtPixel(int, int)
     */
    public Agent getAgentAtPixel(int x, int y) {
        int position = x / agentSize;
        return (Cell) agentView[position];
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.AgentView#drawSelectedAgent(java.awt.Graphics, org.ascape.model.LocatedAgent)
     */
    public void drawSelectedAgent(Graphics g, LocatedAgent a) {
        for (int i = 0; i < agentView.length; i++) {
            if (a == agentView[i]) {
                int s = ((Scape) a).getSize() * agentSize;
                bufferedGraphics.setColor(Color.yellow);
                bufferedGraphics.drawRect(i * agentSize - 1, bufferedImage.getHeight(null) - s - 1, agentSize, s);
            }
        }
    }

    /**
     * Draw agent at.
     * 
     * @param agent
     *            the agent
     * @param position
     *            the position
     */
    public void drawAgentAt(Agent agent, int position) {
        if (agent != null) {
            int s = ((Scape) agent).getSize() * agentSize;
            bufferedGraphics.setColor(Color.black);
            bufferedGraphics.drawRect(position * agentSize - 1, getHeight() - s - 1, agentSize, s);
            bufferedGraphics.setColor(agentColorFeature.getColor(agent));
            bufferedGraphics.fillRect(position * agentSize, getHeight() - s, agentSize - 1, s);
        }
    }
}
