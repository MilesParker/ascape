/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.sugarscape;

import org.ascape.model.Agent;
import org.ascape.model.Cell;
import org.ascape.model.rule.Rule;
import org.ascape.model.space.Coordinate2DDiscrete;

public class SugarSeasonalGrowBackRule extends Rule {

    /**
     * 
     */
    private static final long serialVersionUID = 3368077103086496613L;

    private float epsilon = 0.125f;

    private int seasonLength = 50;

    public SugarSeasonalGrowBackRule() {
        super("Sugar Grow Back Seasonally");
    }

    public SugarSeasonalGrowBackRule(float epsilon, int seasonLength) {
        super("Sugar Grow Back Seasonally");
        this.epsilon = epsilon;
        this.seasonLength = seasonLength;
    }

    public void execute(Agent agent) {
        int iter = agent.getScape().getIteration();
        if ((((float) iter / ((float) seasonLength * 2)) - (iter / (seasonLength * 2))) > .5) {
            //Summer
            if (((Coordinate2DDiscrete) ((Cell) agent).getCoordinate()).getYValue() < 25) {
                ((SugarCell) agent).sugarGrowBack1();
            } else {
                ((SugarCell) agent).sugarGrowBackEpsilon(epsilon);
            }
        } else {
            //Winter
            if (((Coordinate2DDiscrete) ((Cell) agent).getCoordinate()).getYValue() < 25) {
                ((SugarCell) agent).sugarGrowBackEpsilon(epsilon);
            } else {
                ((SugarCell) agent).sugarGrowBack1();
            }
        }
    }

    /**
     * Returns the rate at which sugar grows back in the winter.
     * Model parameter.
     */
    public float getWinterSugarGrowbackRate() {
        return epsilon;
    }

    /**
     * Sets the rate at which sugar grows back in the winter.
     * Model parameter.
     */
    public void setWinterSugarGrowbackRate(float epsilon) {
        this.epsilon = epsilon;
    }

    /**
     * Returns the rate at which sugar grows back in the winter.
     * Model parameter.
     */
    public int getSeasonLength() {
        return seasonLength;
    }

    /**
     * Sets the rate at which sugar grows back in the winter.
     * Model parameter.
     */
    public void setSeasonLength(int seasonLength) {
        this.seasonLength = seasonLength;
    }
}
