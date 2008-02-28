/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */
/**
 * @history changed variables from private to protected so SpiceCell could inherit
 * seperated peak creation from initialize()
 */

package edu.brook.sugarscape;

import org.ascape.model.Agent;
import org.ascape.model.rule.Rule;
import org.ascape.model.space.Coordinate2DDiscrete;
import org.ascape.util.data.StatCollector;
import org.ascape.util.data.StatCollectorCSAMMVar;

public class SpiceCell extends SugarCell {

    /**
     * 
     */
    private static final long serialVersionUID = 7223621178832422771L;

    public final static Rule SUGAR_SPICE_GROW_BACK_1_RULE = new Rule("Spice Grow Back 1") {
        /**
         * 
         */
        private static final long serialVersionUID = -4775209587056933328L;

        public void execute(Agent agent) {
            ((SugarCell) agent).sugarGrowBack1();
            ((SpiceCell) agent).spiceGrowBack1();
        }
    };

    public final static Rule SUGAR_SPICE_GROW_BACK_INF_RULE = new Rule("Spice Grow Back Infinte") {
        /**
         * 
         */
        private static final long serialVersionUID = -2258096138873875633L;

        public void execute(Agent agent) {
            ((SugarCell) agent).sugarGrowBackInf();
            ((SpiceCell) agent).spiceGrowBackInf();
        }
    };

    public static Coordinate2DDiscrete[] spicePeaks;

    public static int MAX_SPICE = 5; // was 4

    private CommoditySource spice;

    public void initialize() {
        super.initialize();
        spice = new CommoditySource();
        spice.setName("spice");
        spice.setOwner(this);
        Coordinate2DDiscrete size = (Coordinate2DDiscrete) getScape().getExtent();
        if (spicePeaks == null) {
            spicePeaks = new Coordinate2DDiscrete[2];
            //spicePeaks[0] = new Coordinate2DDiscrete((int) (size.getXValue() * .8), (int) (size.getYValue() * .2));
            //spicePeaks[1] = new Coordinate2DDiscrete((int) (size.getXValue() * .3), (int) (size.getYValue() * .7));
            spicePeaks[0] = new Coordinate2DDiscrete((int) (size.getXValue() * .3), (int) (size.getYValue() * .2));
            spicePeaks[1] = new Coordinate2DDiscrete((int) (size.getXValue() * .8), (int) (size.getYValue() * .7));
        }
        Coordinate2DDiscrete here = (Coordinate2DDiscrete) getCoordinate();
        float calc = 0.0f;
        for (int i = 0; i < spicePeaks.length; i++) {
            calc += Math.exp(-((GAS_SpiceBase) scape.getRoot()).getSpiceMoundness() * Math.pow(here.getDistance(spicePeaks[i]), 2));
        }
        spice.setCapacity((float) Math.round(MAX_SPICE * Math.min(1, calc)));
        spice.setQuantity(spice.getCapacity());
    }

    /**
     * Override default cell method so that iterate won't be called on each cell.
     */
//    public void scapeCreated() {
//    }

    public double getValue(Object object) {
        return (1.0 - (double) ((CommoditySource) object).getQuantity() / (double) SpiceCell.MAX_SPICE);
    }

    public float getPerceivedValue() {
        return spice.getQuantity() * sugar.getQuantity();
    }

    public float getPotentialValue(float someSugar,
                                   float someSpice,
                                   int sugarMetabolism,
                                   int spiceMetabolism) {
        return ((someSugar + sugar.getQuantity()) / sugarMetabolism) *
            ((someSpice + spice.getQuantity()) / spiceMetabolism);
    }

    public CommoditySource getSpice() {
        return spice;
    }

    private void spiceGrowBack1() {
        spice.growBack1();
    }

    private void spiceGrowBackInf() {
        spice.growBackInf();
    }

    public void spiceGrowBackEpsilon(float epsilon) {
        spice.growBackEpsilon(epsilon);
    }

    public float takeSpice() {
        return spice.takeQuantity();
    }

    public float getSpiceQuantity() {
        return spice.getQuantity();
    }

    public void setSpiceMoundness(float moundness) {
        ((GAS_SpiceBase) scape.getRoot()).setSpiceMoundness(moundness);
    }

    public String getName() {
        return "Spice Cell";
    }

    public void scapeCreated() {
        StatCollector[] stats = {
            new StatCollectorCSAMMVar() {
                /**
                 * 
                 */
                private static final long serialVersionUID = 8078025333157961684L;

                public double getValue(Object object) {
                    return ((SpiceCell) object).getSugarQuantity();
                }

                public String getName() {
                    return "Cell Sugar";
                }
            },
            new StatCollectorCSAMMVar() {
                /**
                 * 
                 */
                private static final long serialVersionUID = -3051490002423025967L;

                public double getValue(Object object) {
                    return ((SpiceCell) object).getSpiceQuantity();
                }

                public String getName() {
                    return "Cell Spice";
                }
            }
        };

        scape.addStatCollectors(stats);
    }
}
