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

//Temporary for JDK 1.1 compatibility
//import com.sun.java.util.collections.*;

import org.ascape.model.Agent;
import org.ascape.model.Scape;
import org.ascape.model.rule.Rule;
import org.ascape.model.space.Coordinate1DDiscrete;
import org.ascape.util.data.StatCollector;
import org.ascape.util.data.StatCollectorCond;

/**
 * This class involves very preliminary exploration.
 a */
public class LHVDisaggregate extends LHV {

    public Scape people;

    protected int personMinInitialAge = 0;

    protected int personMaxInitialAge = 32;

    public void createScape() {
        super.createScape();
        people = new Scape();
        people.setName("People");
        people.setPrototypeAgent(new Person());
        add(people);
        people.addRule(METABOLISM_RULE);
        people.addRule(DEATH_RULE);
        people.addRule(new Rule("Form Household") {
            public void execute(Agent a) {
                ((Person) a).householdFormation();
            }
        });
        people.addRule(FISSIONING_RULE);
        people.setAutoCreate(true);
        HouseholdDisaggregate protoHousehold = new HouseholdDisaggregate();
        protoHousehold.setMembersActive(false);
        households.setPrototypeAgent(protoHousehold);
        households.addInitialRule(CREATE_SCAPE_RULE);
        setMinFertility(.25);
        //households.setAutoPopulate(false);
    }

    public void initialize() {
        super.initialize();
        //This is a little awkward.
        //We have to set the size of the people vector to 0 because it will be populated from households.
        //If we didn't resize it to sero, we might leave people hanging around from the previous run.
        people.setExtent(new Coordinate1DDiscrete(0));
    }

    public void createViews() {
        super.createViews();
        StatCollector[] stats = new StatCollector[9];
        stats[0] = new StatCollectorCond("Size = 1") {
            public boolean meetsCondition(Object object) {
                return (((Person) object).getHousehold().getSize() == 1);
            }
        };
        stats[1] = new StatCollectorCond("Size = 2") {
            public boolean meetsCondition(Object object) {
                return (((Person) object).getHousehold().getSize() == 2);
            }
        };
        stats[2] = new StatCollectorCond("Size = 3") {
            public boolean meetsCondition(Object object) {
                return (((Person) object).getHousehold().getSize() == 3);
            }
        };
        stats[3] = new StatCollectorCond("Size = 4") {
            public boolean meetsCondition(Object object) {
                return (((Person) object).getHousehold().getSize() == 4);
            }
        };
        stats[4] = new StatCollectorCond("Size = 5") {
            public boolean meetsCondition(Object object) {
                return (((Person) object).getHousehold().getSize() == 5);
            }
        };
        stats[5] = new StatCollectorCond("Size = 6") {
            public boolean meetsCondition(Object object) {
                return (((Person) object).getHousehold().getSize() == 6);
            }
        };
        stats[6] = new StatCollectorCond("Size = 7") {
            public boolean meetsCondition(Object object) {
                return (((Person) object).getHousehold().getSize() == 7);
            }
        };
        stats[7] = new StatCollectorCond("Size = 8") {
            public boolean meetsCondition(Object object) {
                return (((Person) object).getHousehold().getSize() == 8);
            }
        };
        stats[8] = new StatCollectorCond("Size = 9+") {
            public boolean meetsCondition(Object object) {
                return (((Person) object).getHousehold().getSize() >= 9);
            }
        };
        people.addStatCollectors(stats);

        /*ChartView chart = new ChartView(ChartView.HISTOGRAM);
        //Add it to the agents view, just like any other view
        valley.addView(chart);
        //And add some of the stat series we've just created to it
        chart.addSeries("Count Size = 1", Color.red);
        chart.addSeries("Count Size = 2", Color.red);
        chart.addSeries("Count Size = 3", Color.red);
        chart.addSeries("Count Size = 4", Color.red);
        chart.addSeries("Count Size = 5", Color.blue);
        chart.addSeries("Count Size = 6", Color.red);
        chart.addSeries("Count Size = 7", Color.red);
        chart.addSeries("Count Size = 8", Color.red);
        chart.addSeries("Count Size = 9+", Color.red);*/
        //chart.addSeries("Average HouseholdDisaggregate Size", Color.red);
        //chart.addSeries("Average HouseholdDisaggregate Size", Color.green);
        //chart.addSeries("Minimum HouseholdDisaggregate Size", Color.black);
        //chart.addSeries("Maximum HouseholdDisaggregate Size", Color.black);
        //chart.addSeries("Count Births", Color.green);
        //chart.addSeries("Count Deaths", Color.black);
        //chart.addSeries("Count HouseholdDisaggregates Formed", Color.blue);
        //chart.addSeries("Count HouseholdDisaggregates Disbanded", Color.white);
    }

    public Scape getPeople() {
        return people;
    }

    public int getPersonMinInitialAge() {
        return personMinInitialAge;
    }

    public void setPersonMinInitialAge(int personMinInitialAge) {
        this.personMinInitialAge = personMinInitialAge;
    }

    public int getPersonMaxInitialAge() {
        return personMaxInitialAge;
    }

    public void setPersonMaxInitialAge(int personMaxInitialAge) {
        this.personMaxInitialAge = personMaxInitialAge;
    }
}
