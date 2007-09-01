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
 * _Sugarscape_
 * See _Growing Artificial Societies: Social Science from the Ground Up_
 * Brookings Institution Press, The MIT Press
 * Joshua Epstein
 * jepstein@brook.edu
 * Robert Axtell
 * raxtell@brook.edu
 *
 * Miles Parker
 * mparker@brook.edu

 * http://www.brook.edu/es/dynamics/models/ascape
 * The Brookings Institution
 * Washington, D.C.
 *
 * Alan Lockard
 * alockard@gmu.edu
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

    public final static Rule SUGAR_SPICE_GROW_BACK_1_RULE = new Rule("Spice Grow Back 1") {
        public void execute(Agent agent) {
            ((SugarCell) agent).sugarGrowBack1();
            ((SpiceCell) agent).spiceGrowBack1();
        }
    };

    public final static Rule SUGAR_SPICE_GROW_BACK_INF_RULE = new Rule("Spice Grow Back Infinte") {
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
                public double getValue(Object object) {
                    return ((SpiceCell) object).getSugarQuantity();
                }

                public String getName() {
                    return "Cell Sugar";
                }
            },
            new StatCollectorCSAMMVar() {
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
