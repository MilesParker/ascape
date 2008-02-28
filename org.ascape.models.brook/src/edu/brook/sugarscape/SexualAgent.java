/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.sugarscape;


public class SexualAgent extends SugarAgent {

    /**
     * 
     */
    private static final long serialVersionUID = 6828857877206650643L;

    private static int minReproductionBeginsAge = 12;

    private static int maxReproductionBeginsAge = 15;

    private static int minFemaleReproductionEndsAge = 40;

    private static int maxFemaleReproductionEndsAge = 50;

    private static int minMaleReproductionEndsAge = 50;

    private static int maxMaleReproductionEndsAge = 60;

    protected static int minInitialSugar = 50;

    protected static int maxInitialSugar = 100;

    private boolean female;

    private float initialSugarEndowment;

    private int beginReproductionAge;

    private int endReproductionAge;

    public void initialize() {
        super.initialize();
        initialSugarEndowment = getSugar().getStock();
        female = randomIs();
        beginReproductionAge = randomInRange(minReproductionBeginsAge, maxReproductionBeginsAge);
        if (female) {
            endReproductionAge = randomInRange(minFemaleReproductionEndsAge, maxFemaleReproductionEndsAge);
        } else {
            endReproductionAge = randomInRange(minMaleReproductionEndsAge, maxMaleReproductionEndsAge);
        }
    }

    public void setVision(int vision) {
        super.setVision(vision);
    }

    public void setSugarMetabolism(int metabolism) {
        super.setSugarMetabolism(metabolism);
    }

    public float getContributionToChild() {
        return Math.min(5, getSugarStock());
    }

    public boolean isFertile() {
        return ((getSugar().getStock() >= initialSugarEndowment) && (age >= beginReproductionAge) && (age <= endReproductionAge));
    }

    public boolean isMale() {
        return !female;
    }

    public boolean isFemale() {
        return female;
    }

    public void setFemale(boolean female) {
        this.female = female;
    }
}
