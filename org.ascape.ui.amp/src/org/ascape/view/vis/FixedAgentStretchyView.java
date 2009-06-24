/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.view.vis;

/**
 * A class providing a view of an scape vector. <i>Currently unmaintained.</i>
 * 
 * @since 1.0
 */
public class FixedAgentStretchyView extends FixedStretchyView {

    private static final long serialVersionUID = 260121859633490800L;

    public FixedAgentStretchyView() {
        super("Not Used", 1000, 1000);
    }

    // /* (non-Javadoc)
    // * @see org.ascape.view.vis.FixedStretchyView#drawAgentAt(org.ascape.model.Agent, int)
    // */
    // public void drawAgentAt(Graphics graphics, Agent agent, int position) {
    // if (agent != null) {
    // int s = ((Scape) agent).getSize() * agentSize;
    // Object[] members = ((Scape) agent).toArray();
    // for (int i = 0; i < members.length; i++) {
    // graphics.setBackgroundColor((Color) ((Agent) members[members.length - i - 1]).getPlatformColor());
    // graphics.fillRectangle(position * agentSize, getSize().width - s + i * agentSize, agentSize,
    // agentSize);
    // }
    // }
    // }
}
