/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.ant.swing;

import org.ascape.ant.AntViewElement;
import org.ascape.ant.SelectionSet;
import org.ascape.model.Scape;
import org.ascape.view.vis.AgentView;


/**
 * The Class AntAgentViewElement.
 */
public class AntAgentViewElement extends AntViewElement {

    /**
     * 
     */
    private static final long serialVersionUID = 4297846855885337497L;
    /**
     * The draw set.
     */
    SelectionSet drawSet;

    /**
     * Adds the draw set.
     * 
     * @param drawSet
     *            the draw set
     */
    public void addDrawSet(SelectionSet drawSet) {
        this.drawSet = drawSet;
    }

    /* (non-Javadoc)
     * @see org.ascape.ant.AntViewElement#addToScape(org.ascape.model.Scape)
     */
    public void addToScape(Scape scape) {
        super.addToScape(scape);
        drawSet.apply(((AgentView) getView()).getDrawSelection());
    }
}
