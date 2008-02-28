/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.aa;


import org.ascape.util.data.StatCollector;
import org.ascape.util.data.StatCollectorCSAMM;

public class HouseholdAggregate extends HouseholdBase {

    /**
     * 
     */
    private static final long serialVersionUID = 5091800912116536871L;

    private int age;

    private int nutritionNeed;

    private int nutritionNeedRemaining;

    public void initialize() {
        super.initialize();
        setMembersActive(false);
        age = randomInRange(((LHV) getRoot()).getHouseholdMinInitialAge(), ((LHV) getRoot()).getHouseholdMaxInitialAge());
        nutritionNeed = randomInRange(((LHV) getRoot()).getHouseholdMinNutritionNeed(), ((LHV) getRoot()).getHouseholdMaxNutritionNeed());
    }

    public void metabolism() {
        super.metabolism();
        age++;
        nutritionNeedRemaining = consumeCorn(nutritionNeed);
    }

    public void fission() {
        HouseholdAggregate child = new HouseholdAggregate();//(Household) this.clone();
        scape.add(child);
        child.initialize();
        child.age = 0;
        giveMaizeGift(child);
        child.move();
        //if ((child.farm.getLocation() != null) && (child.settlement != null)) {
        //For now, record fissions regardless of successful move to match C++ code
        scape.getData().getStatCollector("Fissions").addValue(0.0);
        //}
        //System.out.println(child.age);
    }

    public boolean deathCondition() {
        if (nutritionNeedRemaining > 0) {
            scape.getData().getStatCollector("Deaths Starvation").addValue(0.0);
            return true;
        }
        if (age > deathAge) {
            scape.getData().getStatCollector("Deaths Old Age").addValue(0.0);
            return true;
        }
        return false;
        //return ((age > ((LHV) getRoot()).getHouseholdDeathAge()) || (nutritionNeedRemaining > 0));
    }

    public boolean fissionCondition() {
        //return ((age > ((LHV) getRoot()).getFertilityAge()) && (getRandom().nextDouble() < ((LHV) getRoot()).getFertility()));
        //Rob's experiment
        return ((age > fertilityAge) && (age <= fertilityEndsAge) && (getRandom().nextDouble() < fertility));
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getNutritionNeed() {
        return nutritionNeed;
    }

    public int getNumAdults() {
        return 1;
    }

    public void scapeCreated() {
        super.scapeCreated();
        //scape.addInitialRule(FORCE_MOVE_RULE);
        scape.addRule(METABOLISM_RULE);
        scape.addRule(DEATH_RULE);
        scape.addRule(MOVEMENT_RULE);
        scape.addRule(FISSIONING_RULE);

        StatCollector[] stats = new StatCollector[4];
        stats[0] = new StatCollector("Deaths Starvation", false);
        stats[1] = new StatCollector("Deaths Old Age", false);
        stats[2] = new StatCollector("Births", false);
        stats[3] = new StatCollectorCSAMM("Household Size") {
            /**
             * 
             */
            private static final long serialVersionUID = 5919164193477195628L;

            public final double getValue(Object o) {
                return 5;
            }
        };
        scape.addStatCollectors(stats);
    }
}
