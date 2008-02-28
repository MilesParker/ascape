/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
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

    /**
     * 
     */
    private static final long serialVersionUID = -6752232051261561854L;

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
            /**
             * 
             */
            private static final long serialVersionUID = -1104523698642038557L;

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
            /**
             * 
             */
            private static final long serialVersionUID = -6187477562807067038L;

            public boolean meetsCondition(Object object) {
                return (((Person) object).getHousehold().getSize() == 1);
            }
        };
        stats[1] = new StatCollectorCond("Size = 2") {
            /**
             * 
             */
            private static final long serialVersionUID = -2088909634350675904L;

            public boolean meetsCondition(Object object) {
                return (((Person) object).getHousehold().getSize() == 2);
            }
        };
        stats[2] = new StatCollectorCond("Size = 3") {
            /**
             * 
             */
            private static final long serialVersionUID = -8327106276659553656L;

            public boolean meetsCondition(Object object) {
                return (((Person) object).getHousehold().getSize() == 3);
            }
        };
        stats[3] = new StatCollectorCond("Size = 4") {
            /**
             * 
             */
            private static final long serialVersionUID = -2207871416202810012L;

            public boolean meetsCondition(Object object) {
                return (((Person) object).getHousehold().getSize() == 4);
            }
        };
        stats[4] = new StatCollectorCond("Size = 5") {
            /**
             * 
             */
            private static final long serialVersionUID = -3782509565955129394L;

            public boolean meetsCondition(Object object) {
                return (((Person) object).getHousehold().getSize() == 5);
            }
        };
        stats[5] = new StatCollectorCond("Size = 6") {
            /**
             * 
             */
            private static final long serialVersionUID = 6268992466489178017L;

            public boolean meetsCondition(Object object) {
                return (((Person) object).getHousehold().getSize() == 6);
            }
        };
        stats[6] = new StatCollectorCond("Size = 7") {
            /**
             * 
             */
            private static final long serialVersionUID = 8816932454170339947L;

            public boolean meetsCondition(Object object) {
                return (((Person) object).getHousehold().getSize() == 7);
            }
        };
        stats[7] = new StatCollectorCond("Size = 8") {
            /**
             * 
             */
            private static final long serialVersionUID = 8428713312506109519L;

            public boolean meetsCondition(Object object) {
                return (((Person) object).getHousehold().getSize() == 8);
            }
        };
        stats[8] = new StatCollectorCond("Size = 9+") {
            /**
             * 
             */
            private static final long serialVersionUID = -7184518477319545048L;

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
