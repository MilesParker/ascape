/*
 * This software and documentation is subject to, and may be only used in accordance with, the terms and conditions of
 * the license agreement described or set forth in the file titled License.txt.  This software and documentation
 * contains information that is confidential and proprietary to NuTech Solutions, Inc.
 *
 *             Copyright (c) 2000-2004
 *             NuTech Solutions, Inc.
 *
 * Portions of the software are licensed from The Brookings Institution
 *             Copyright (c) 1998-2000
 *            The Brookings Institution
 *           All rights reserved worldwide.
 *
 *
 * U.S. GOVERNMENT END USERS. The software is a "commercial item," as that term is defined in 48 C.F.R. 2.101,
 * consisting of "commercial computer software" and "commercial computer software documentation," as such terms are
 * used in 48 C.F.R. 12.212. Consistent with 48 C.F.R. 12.212 and 48 C.F.R. 227.7202-1 through 227.7202-4, and other
 * relevant sections of the Code of Federal Regulations, as applicable all U.S. Government End Users acquire the
 * software with only those rights set forth in the license agreement. This software includes trade secrets that
 * are exempt from disclosure under the Freedom of Information Act.
 *
 *
 * Artificial Anasazi - Long House Valley
 * see _Understanding Anasazi Culture Change Through Agent-Based Modeling_
 * Jeffrey S. Dean, George J. Gumerman, Joshua M. Epstein, Robert Axtell,
 * Alan C. Swedlund, Miles Parker, and Steven McCarroll
 *
 * Java Implementation:
 * Miles Parker
 * mparker@brook.edu
 * Original C++ Implementation:
 * Steven McCarroll
 * The Brookings Institution
 * Washington, D.C.
 */

package edu.brook.aa;


import org.ascape.util.data.StatCollector;
import org.ascape.util.data.StatCollectorCSAMM;

public class HouseholdAggregate extends HouseholdBase {

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
            public final double getValue(Object o) {
                return 5;
            }
        };
        scape.addStatCollectors(stats);
    }
}
