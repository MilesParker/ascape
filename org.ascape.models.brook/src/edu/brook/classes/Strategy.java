/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.classes;

import java.util.Random;

import org.ascape.util.Utility;


public abstract class Strategy {

    public abstract float getDemand();

    public final static Strategy LOW_STRATEGY = new Strategy() {
        public final float getDemand() {
            return BargainingModelBase.getLowPayoffStatic();
        }
    };

    public final static Strategy MEDIUM_STRATEGY = new Strategy() {
        public final float getDemand() {
            return BargainingModelBase.getMediumPayoffStatic();
        }
    };

    public final static Strategy HIGH_STRATEGY = new Strategy() {
        public final float getDemand() {
            return BargainingModelBase.getHighPayoffStatic();
        }
    };

    public static Strategy randomStrategy(Random random) {
        int strategy = Utility.randomToLimit(random, 3);
        switch (strategy) {
            case 0:
                return LOW_STRATEGY;
            case 1:
                return MEDIUM_STRATEGY;
            case 2:
                return HIGH_STRATEGY;
            default:
                //Should never get here, but to satisfy compiler..
                throw new RuntimeException("Unexpected internal error in Bargainer.");
        }
    }
}
