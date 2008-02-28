/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.sugarscape;


public class GAS_II_7 extends GAS_II_2 {

    /**
     * 
     */
    private static final long serialVersionUID = 7761990625024232408L;
    private SugarSeasonalGrowBackRule seasonRule;

    public void createScape() {
        super.createScape();
        //Get rid of grow back infinite rule added in superclass
        sugarscape.getRules().clearSelection();
        seasonRule = new SugarSeasonalGrowBackRule();
        sugarscape.addRule(seasonRule);
    }

    /**
     * Returns the rate at which sugar grows back in the winter.
     * Model parameter.
     */
    public float getWinterSugarGrowbackRate() {
        return seasonRule.getWinterSugarGrowbackRate();
    }

    /**
     * Sets the rate at which sugar grows back in the winter.
     * Model parameter.
     */
    public void setWinterSugarGrowbackRate(float epsilon) {
        seasonRule.setWinterSugarGrowbackRate(epsilon);
    }

    /**
     * Returns the rate at which sugar grows back in the winter.
     * Model parameter.
     */
    public int getSeasonLength() {
        return seasonRule.getSeasonLength();
    }

    /**
     * Sets the rate at which sugar grows back in the winter.
     * Model parameter.
     */
    public void setSeasonLength(int seasonLength) {
        seasonRule.setSeasonLength(seasonLength);
    }
}
