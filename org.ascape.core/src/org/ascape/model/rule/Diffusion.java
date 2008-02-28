/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.rule;

import java.util.List;

import org.ascape.model.Agent;
import org.ascape.model.Cell;

/**
 * A rule causing some agent value to diffuse to its neighbors.
 * 
 * @author Miles Parker
 * @version 1.9.2
 * @history 1.9.2 2/6/01 added named constructor
 * @since 1.0
 */
public abstract class Diffusion extends ExecuteThenUpdate {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a diffusion rule with the provided name.
     * 
     * @param name
     *            the name of this object
     */
    public Diffusion(String name) {
        super(name);
    }

    /**
     * Override with a call to the getter of the value to diffuse.
     * 
     * @param agent
     *            the agent
     * @return the diffusion value
     */
    public abstract double getDiffusionValue(Agent agent);

    /**
     * Override with a call to the setter of the diffused value.
     * 
     * @param agent
     *            the agent
     * @param value
     *            the value
     */
    public abstract void setDiffusionValue(Agent agent, double value);

    /**
     * Calculate and store diffusion value to neighbors.
     * 
     * @param agent
     *            the playing agent
     */
    public void execute(Agent agent) {
        double valueSum = 0.0f;
        List neighbors = ((Cell) agent).findNeighbors();
        for (int i = 0; i < neighbors.size(); i++) {
            valueSum += getDiffusionValue((Agent) neighbors.get(i));
        }
        ((Diffusable) agent).setDiffusionTemp(valueSum / neighbors.size());
    }

    /**
     * Update diffusion value. Occurs only when all diffusion has been
     * calculated.
     * 
     * @param agent
     *            the agent
     */
    public void update(Agent agent) {
        setDiffusionValue(agent, ((Diffusable) agent).getDiffusionTemp());
    }

    /* (non-Javadoc)
     * @see org.ascape.model.rule.Rule#isRandomExecution()
     */
    public boolean isRandomExecution() {
        return false;
    }
}

