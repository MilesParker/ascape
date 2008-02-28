/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.aa;

import org.ascape.model.Cell;
import org.ascape.util.data.StatCollector;


public class Person extends Cell {

    /**
     * 
     */
    private static final long serialVersionUID = 2643326983426692833L;

    public final static boolean FEMALE = true;

    public final static boolean MALE = false;

    protected int age;

    protected boolean sex;

    protected boolean headOfHousehold;

    protected Person mate;

    protected HouseholdDisaggregate household;

    private int nutritionNeedRemaining;

    public void initialize() {
        super.initialize();
        age = randomInRange(((LHVDisaggregate) getRoot()).getPersonMinInitialAge(), ((LHVDisaggregate) getRoot()).getPersonMaxInitialAge());
        sex = randomIs();
        household = null;
        mate = null;
        headOfHousehold = false;
        nutritionNeedRemaining = 0;
        //((LHVDisaggregate) getRoot()).getPeople().add(this);
    }

    public int getNutritionNeed() {
        if (age > ((LHVDisaggregate) getRoot()).getMinFertilityAge()) {
            return ((LHVDisaggregate) getRoot()).getBaseNutritionNeed();
        } else {
            return (int) ((float) ((LHVDisaggregate) getRoot()).getBaseNutritionNeed() * (.2 + (.8 * ((float) age / (float) ((LHVDisaggregate) getRoot()).getMinFertilityAge()))));
        }
    }

    public void metabolism() {
        if (!household.contains(this)) {
            throw new RuntimeException("Internal Error: Agent not in household");
        }
        super.metabolism();
        age++;
        //Deplete current inventory of nutrition need
        //nutritionNeedRemaining = consumeCorn(getNutritionNeed());
    }

    public void leave() {
        if ((household != null) && (!household.remove(this))) {
            throw new RuntimeException("Internal Error: Agent Couldn't be deleted");
        }
        household = null;
    }

    public void die() {
        scape.getData().getStatCollector("Deaths").addValue(0.0);
        if (mate != null) {
            mate.setMate(null);
            setMate(null);
        }
        leave();
        super.die();
    }

    public boolean deathCondition() {
        if (nutritionNeedRemaining > 0) {
            scape.getData().getStatCollector("Deaths Starvation").addValue(0.0);
            return true;
        }
        if (age < 1) {
            if (getRandom().nextFloat() < .233) {
                return true;
            }
            return false;
        } else if (age < 5) {
            if (getRandom().nextFloat() < (.140 / 4.0)) {
                return true;
            }
            return false;
        } else if (age < 15) {
            if (getRandom().nextFloat() < (.166 / 10.0)) {
                return true;
            }
            return false;
        } else if (age < 25) {
            if (getRandom().nextFloat() < (.247 / 10.0)) {
                return true;
            }
            return false;
        } else if (age < 45) {
            if (getRandom().nextFloat() < (.362 / 20.0)) {
                return true;
            }
            return false;
        } else {
            return true;
        }
        //(If a person might starve and die of old age on the same turn, they are assumed to have starved,
        //and are not counted in both categories.)
        /*else if (age > ((LHV) getRoot()).getDeathAge()) {
            scape.getData().getStatCollector("Deaths").addValue(0.0);
            scape.getData().getStatCollector("Deaths Old Age").addValue(0.0);
            return true;
        }*/
        //return false;
        //return ((age > ((LHV) getRoot()).getHouseholdDeathAge()) || (nutritionNeedRemaining > 0));
    }

    public void fission() {
        Person child = new PersonClan();
        scape.add(child);
        child.initialize();
        child.setHousehold(household);
        child.age = 0;
        //if ((child.farm.getLocation() != null) && (child.settlement != null)) {
        //For now, record fissions regardless of successful move to match C++ code
        scape.getData().getStatCollector("Births").addValue(0.0);
        //}
        //System.out.println(household.getSize());
    }

    public boolean fissionCondition() {
        //The table below is based on data Alan Sweedlund developed for feamle births per woman, hence we double the chance
        if ((mate != null) && (sex == FEMALE)) {
            if (age < ((LHVDisaggregate) getRoot()).getMinFertilityAge()) {
                return false;
            } else if (age < 20) {
                if (getRandom().nextFloat() < .047 * 2.0) {
                    return true;
                }
                return false;
            } else if (age < 30) {
                if (getRandom().nextFloat() < .128 * 2.0) {
                    return true;
                }
                return false;
            } else if (age < 35) {
                if (getRandom().nextFloat() < .104 * 2.0) {
                    return true;
                }
                return false;
            } else if (age < 40) {
                if (getRandom().nextFloat() < .072 * 2.0) {
                    return true;
                }
                return false;
            } else if (age < 45) {
                if (getRandom().nextFloat() < .03 * 2.0) {
                    return true;
                }
                return false;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public FindMateRule findMate = new FindMateRule(this);

    public boolean householdCondition() {
        //mtp 8/19/00 note change, once head of household you never form a new household
        return ((mate == null) && (!headOfHousehold) && (sex != MALE) && (age > ((LHV) getRoot()).getMinFertilityAge()));
    }

    public void formHousehold() {
        findMate.clear();
        scape.executeOnMembers(findMate);
        if (findMate.getMate() != null) {
            HouseholdDisaggregate oldHousehold = household;
            this.setMate(findMate.getMate());
            mate.setMate(this);
            scape.getData().getStatCollector("Households Formed").addValue(0.0);
            HouseholdDisaggregate newHousehold = new HouseholdDisaggregate();
            ((LHVDisaggregate) getRoot()).getHouseholds().add(newHousehold);
            newHousehold.initialize();
            this.setHousehold(newHousehold);
            mate.setHousehold(newHousehold);
            oldHousehold.giveMaizeGift(newHousehold);
            newHousehold.move();
        }
    }

    public void householdFormation() {
        if (householdCondition()) {
            formHousehold();
        }
    }

    public void scapeCreated() {
        super.scapeCreated();

        StatCollector[] stats = new StatCollector[4];
        stats[0] = new StatCollector("Deaths", false);
        stats[1] = new StatCollector("Deaths Starvation", false);
        stats[2] = new StatCollector("Deaths Old Age", false);
        stats[3] = new StatCollector("Births", false);
        scape.addStatCollectors(stats);
    }

    public Person getMate() {
        return mate;
    }

    public void setMate(Person mate) {
        if (mate != null) {
            headOfHousehold = true;
        }
        this.mate = mate;
    }

    public boolean getSex() {
        return sex;
    }

    public String getSexName() {
        if (sex == FEMALE) {
            return "Female";
        } else {
            return "Male";
        }
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    /**
     * @return the household this person is a member of.
     */
    public HouseholdDisaggregate getHousehold() {
        return household;
    }

    /**
     * Sets the household this person is a memebr of.
     */
    public void setHousehold(HouseholdDisaggregate household) {
        leave();
        this.household = household;
        household.add(this, false);
    }

    /**
     * Returns a description of the person including age, sex and household.
     */
    public String toString() {
        String desc = "";
        if (sex == FEMALE) {
            desc += "Female";
        } else {
            desc += "Male";
        }
        desc += " age " + age;
        if (household != null) {
            desc += " living in a " + household.toInnerString();
        }
        return desc;
    }
}
