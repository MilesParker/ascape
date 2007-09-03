/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.classes;

import org.ascape.model.Agent;
import org.ascape.model.rule.Rule;

public class Figure6 extends BargainingModelTwo {

    public void createScape() {
        super.createScape();
        setMinimumMemorySize(20);
        setMaximumMemorySize(20);
        setRandomStrategyProbability(0.05f);
        agents.addInitialRule(new Rule("Intra High/Low") {
            public void execute(Agent a) {
                if (((BargainerTagged) a).tag == BargainerTagged.LIGHT_TAG) {
                    ((BargainerTagged) a).initializeMemoryHiLow(BargainerTagged.LIGHT_TAG);
                }
            }
        }, false);  //initially inactive
        agents.addInitialRule(new Rule("Light Inter Low; Dark Inter High") {
            public void execute(Agent a) {
                if (((BargainerTagged) a).tag == BargainerTagged.LIGHT_TAG) {
                    ((BargainerTagged) a).initializeMemory(-((BargainerTagged) a).tag, Strategy.LOW_STRATEGY);
                } else {
                    ((BargainerTagged) a).initializeMemory(-((BargainerTagged) a).tag, Strategy.HIGH_STRATEGY);
                }
            }
        }, false);  //initially inactive
        agents.addInitialRule(new Rule("Light Inter Low, Intra High/Low; Dark Inter High") {
            public void execute(Agent a) {
                if (((BargainerTagged) a).tag == BargainerTagged.LIGHT_TAG) {
                    ((BargainerTagged) a).initializeMemory(-((BargainerTagged) a).tag, Strategy.LOW_STRATEGY);
                    ((BargainerTagged) a).initializeMemoryHiLow(((BargainerTagged) a).tag);
                } else {
                    ((BargainerTagged) a).initializeMemory(-((BargainerTagged) a).tag, Strategy.HIGH_STRATEGY);
                }
            }
        }, false);  //initially inactive
        agents.addInitialRule(new Rule("Intra High/Low") {
            public void execute(Agent a) {
                ((BargainerTagged) a).calculateMemoryCounts();
            }
        });  //should always be active
    }
}
