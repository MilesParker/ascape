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

import org.ascape.model.Agent;
import org.ascape.model.rule.CollectStats;
import org.ascape.util.data.StatCollector;
import org.ascape.util.data.StatCollectorCSA;
import org.ascape.util.data.StatCollectorCSAMM;
import org.ascape.util.data.StatCollectorCond;

/**
 * This class involves very preliminary exploration.
 */
public class HouseholdDisaggregate extends HouseholdBase {

    public void initialize() {
        setAutoCreate(false);
        clear();
        super.initialize();
        //members = new Scape();
        //setPrototypeAgent(new Person());
        //setExtent(new Coordinate1DDiscrete(((LHV) getRoot()).getTypicalHouseholdSize()));
    }

    public void createScape() {
        setClan(Clan.randomClan(this));
        /*for (int i = 0; i < ((LHV) getRoot()).getTypicalHouseholdSize(); i++) {
            PersonClan newPerson = new PersonClan();
            ((LHVDisaggregate) getRoot()).getPeople().add(newPerson);
            newPerson.initialize();
            newPerson.setHousehold(this);
        }*/
        PersonClan mom = new PersonClan();
        ((LHVDisaggregate) getRoot()).getPeople().add(mom);
        mom.initialize();
        mom.setHousehold(this);
        mom.setAge(randomInRange(((LHV) getRoot()).getMinFertilityAge(), ((LHV) getRoot()).getMinDeathAge()));
        mom.setSex(Person.FEMALE);
        PersonClan dad = new PersonClan();
        ((LHVDisaggregate) getRoot()).getPeople().add(dad);
        dad.initialize();
        dad.setHousehold(this);
        dad.setAge(randomInRange(((LHV) getRoot()).getMinFertilityAge(), ((LHV) getRoot()).getMinDeathAge()));
        dad.setSex(Person.MALE);
        dad.setMate(mom);
        mom.setMate(dad);
        for (int i = 0; i < 3; i++) {
            PersonClan child = new PersonClan();
            ((LHVDisaggregate) getRoot()).getPeople().add(child);
            child.initialize();
            child.setHousehold(this);
            child.setAge(randomInRange(0, ((LHV) getRoot()).getMinFertilityAge()));
        }
    }

    public boolean remove(Agent agent) {
        if (super.remove(agent)) {
            //We don't want to call die if allready called
            if ((this.getSize() == 0) && (!isDelete())) {
                die();
            }
            return true;
        } else {
            return false;
        }
    }

    public void die() {
        //markForDeletion();
        //Any people remaining in the household also die..
        executeOnMembers(FORCE_DIE_RULE);
        //Executing the die rule will invoke this method when
        //the last agent is removed; we need to ensure that the deletion
        //only occurs once
        if (!isDelete()) {
            super.die();
            scape.getData().getStatCollector("Households Disbanded").addValue(0.0);
        }
    }

    private final static StatCollectorCSA[] calculateNutritionNeed = {new StatCollectorCSA() {
        public double getValue(Object object) {
            return ((Person) object).getNutritionNeed();
        }
    }};

    private final static CollectStats collectNutritionNeed = new CollectStats(calculateNutritionNeed);

    public int getNutritionNeed() {
        collectNutritionNeed.clear();
        executeOnMembers(collectNutritionNeed);
        return (int) calculateNutritionNeed[0].getSum();
    }

    public int getNumAdults() {
        return 6;
/*collectAdults.clear();
        executeOnMembers(collectAdults);
        if (calculateAdults[0].getCount() > 10) {
	        System.out.println();
	        executeOnMembers(new Rule("Temp") {
	        	public void execute(Agent a) {
			        System.out.print(((Person) a).getSexName()+" "+((Person) a).getAge()+" "+(((Person) a).getMate() == null)+", ");
			    }
			});
	        System.out.println();
        }
        return (int) calculateAdults[0].getCount();*/
    }

    public void scapeCreated() {
        super.scapeCreated();
        scape.addRule(METABOLISM_RULE);
        scape.addRule(MOVEMENT_RULE);

        StatCollector[] stats = new StatCollector[3];
        stats[0] = new StatCollector("Households Formed", false);
        stats[1] = new StatCollector("Households Disbanded", false);
        stats[2] = new StatCollectorCSAMM("Household Size") {
            public double getValue(Object o) {
                return ((HouseholdDisaggregate) o).getSize();
            }
        };
        scape.addStatCollectors(stats);

        StatCollector[] clanStats = new StatCollector[Clan.clans.length];
        for (int i = 0; i < clanStats.length; i++) {
            clanStats[i] = new ClanStat(Clan.clans[i]);
        }
        scape.addStatCollectors(clanStats);
    }
}

class ClanStat extends StatCollectorCond {

    Clan clan;

    public ClanStat(Clan clan) {
        super(clan.getName());
        this.clan = clan;
    }

    public boolean meetsCondition(Object o) {
        return (((HouseholdDisaggregate) o).getClan() == clan);
    }
}

