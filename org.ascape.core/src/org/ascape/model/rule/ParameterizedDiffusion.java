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
 * A rule causing some agent value to diffuse to its neighbors at a particualar
 * constant. Also supports evaporation at a given rate.
 * 
 * @author Miles Parker
 * @version 1.9.2
 * @history 1.9.2 2/6/01 added named constructor
 * @history 10/5/99 1.2.5 first in
 * @since 1.2.5
 */
public abstract class ParameterizedDiffusion extends Diffusion {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The evaporation rate.
     */
    private double evaporationRate = 0.0;

    /**
     * The diffuse constant.
     */
    private double diffuseConstant = 1.0;

    /**
     * Constructs a paramterized diffusion rule with the provided name.
     * 
     * @param name
     *            the name of this object
     */
    public ParameterizedDiffusion(String name) {
        super(name);
    }

    /**
     * Constructs a paramterized diffusion rule with the provided name and
     * paramaters.
     * 
     * @param name
     *            the name of this object
     * @param diffuseConstant
     *            the diffuse constant
     * @param evaporationRate
     *            the evaporation rate
     */
    public ParameterizedDiffusion(String name, float diffuseConstant, float evaporationRate) {
        super(name);
        setDiffusionConstant(diffuseConstant);
        setEvaporationRate(evaporationRate);
    }

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
        valueSum -= neighbors.size() * getDiffusionValue(agent);
        valueSum /= neighbors.size();
        valueSum = getDiffusionValue(agent) + valueSum * getDiffusionConstant();
        valueSum *= 1.0f - getEvaporationRate();
        ((Diffusable) agent).setDiffusionTemp(valueSum);
    }

    /**
     * Gets the diffusion constant.
     * 
     * @return the diffusion constant
     */
    public double getDiffusionConstant() {
        return diffuseConstant;
    }

    /**
     * Sets the diffusion constant.
     * 
     * @param diffuseConstant
     *            the new diffusion constant
     */
    public void setDiffusionConstant(double diffuseConstant) {
        this.diffuseConstant = diffuseConstant;
    }

    /**
     * Gets the evaporation rate.
     * 
     * @return the evaporation rate
     */
    public double getEvaporationRate() {
        return evaporationRate;
    }

    /**
     * Sets the evaporation rate.
     * 
     * @param evaporationRate
     *            the new evaporation rate
     */
    public void setEvaporationRate(double evaporationRate) {
        this.evaporationRate = evaporationRate;
    }
}

