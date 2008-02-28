/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.sugarscape;

import org.ascape.model.Agent;
import org.ascape.model.rule.Diffusable;
import org.ascape.model.rule.Diffusion;

public class PollutableSugarCell extends SugarCell implements Diffusable {

    /**
     * 
     */
    private static final long serialVersionUID = 8290780660743245711L;

    private static float maxPollution = 1;

    /**
     * Factor for pollution from gathering.
     */
    private static int sigma = 8;

    private float pollution;

    public void initialize() {
        super.initialize();
        pollution = 0;
        maxPollution = 1;
    }

    public void scapeCreated() {
        scape.addRule(new Diffusion("Pollution") {
            /**
             * 
             */
            private static final long serialVersionUID = 572246971519175438L;

            public double getDiffusionValue(Agent agent) {
                return ((PollutableSugarCell) agent).getPollution();
            }

            public void setDiffusionValue(Agent agent, double value) {
                ((PollutableSugarCell) agent).setPollution((float) value);
            }
        });
    }

    public float takeSugar() {
        addPollution(sigma * sugar.getQuantity());
        return super.takeSugar();
    }

    public float getPerceivedValue() {
        return sugar.getQuantity() / (1 + pollution);
    }

    public float getPollution() {
        return pollution;
    }

    public float getMaxPollution() {
        return maxPollution;
    }

    public void setPollution(float pollution) {
        this.pollution = pollution;
        if (pollution > maxPollution) {
            maxPollution = pollution;
        }
        requestUpdate();
    }

    public void addPollution(float pollution) {
        setPollution(this.pollution + pollution);
    }

    private double diffusionTemp;

    public double getDiffusionTemp() {
        return diffusionTemp;
    }

    public void setDiffusionTemp(double diffusionTemp) {
        this.diffusionTemp = diffusionTemp;
    }

    public double getValue(Object object) {
        return (1.0F - ((PollutableSugarCell) object).getPollution() / ((PollutableSugarCell) object).getMaxPollution());
    }
}
