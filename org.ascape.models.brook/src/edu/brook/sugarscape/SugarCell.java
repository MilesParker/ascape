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
 */

package edu.brook.sugarscape;

import java.awt.Color;

import org.ascape.model.Agent;
import org.ascape.model.HostCell;
import org.ascape.model.rule.Rule;
import org.ascape.model.space.Coordinate2DDiscrete;

/**
 * @history 19990624 AAL changed variables from private to protected so SpiceCell could inherit
 * seperated peak creation from initialize()
 * @history 19990629 AAL removed a variety of "sugar"related methods and
 *          attributes, and put them in new class CommoditySource
 */
public class SugarCell extends HostCell {

    public final static Rule SUGAR_GROW_BACK_1_RULE = new Rule("Sugar-Grow Back 1") {
        public void execute(Agent agent) {
            ((SugarCell) agent).sugarGrowBack1();
        }
    };

    public final static Rule SUGAR_GROW_BACK_INF_RULE = new Rule("Sugar-Grow Back Infinite") {
        public void execute(Agent agent) {
            ((SugarCell) agent).sugarGrowBackInf();
        }
    };

    public static Coordinate2DDiscrete[] sugarPeaks;

    protected CommoditySource sugar;

    public static int MAX_SUGAR = 4; // was 4

    public void initialize() {
        super.initialize();
        sugar = new CommoditySource();
        sugar.setName("sugar");
        sugar.setOwner(this);
        if (sugarPeaks == null) {
            Coordinate2DDiscrete size = (Coordinate2DDiscrete) getScape().getExtent();
            sugarPeaks = new Coordinate2DDiscrete[2];
            sugarPeaks[0] = new Coordinate2DDiscrete((int) (size.getXValue() * .8), (int) (size.getYValue() * .2));
            sugarPeaks[1] = new Coordinate2DDiscrete((int) (size.getXValue() * .3), (int) (size.getYValue() * .7));
        }
        Coordinate2DDiscrete here = (Coordinate2DDiscrete) getCoordinate();
        float calc = 0.0f;
        for (int i = 0; i < sugarPeaks.length; i++) {
            calc += Math.exp(-((GAS_Base) scape.getRoot()).getSugarMoundness() * Math.pow(here.getDistance(sugarPeaks[i]), 2));
        }
        sugar.setCapacity((float) Math.round(MAX_SUGAR * Math.min(1, calc)));
        sugar.setQuantity(sugar.getCapacity());
    }

    /**
     * Override default cell method so that iterate won't be called on each cell.
     */
    public void scapeCreated() {
    }

    public double getValue(Object object) {
        return (1.0 - (double) ((CommoditySource) object).getQuantity() / (double) SugarCell.MAX_SUGAR);
    }

    public float getPerceivedValue() {
        return sugar.getQuantity();
    }

    public Color getColor() {
        return Color.black;
    }

    public String getName() {
        return "Sugar Cell";
    }

    public CommoditySource getSugar() {
        return sugar;
    }

    public float getSugarAmount() {
        return sugar.getQuantity();
    }

    public void setSugarAmount(float sugarAmout) {
        sugar.setQuantity(sugarAmout);
    }

    public void sugarGrowBack1() {
        sugar.growBack1();
    }

    protected void sugarGrowBackInf() {
        sugar.growBackInf();
    }

    public void sugarGrowBackEpsilon(float epsilon) {
        sugar.growBackEpsilon(epsilon);
    }

    public float takeSugar() {
        return sugar.takeQuantity();
    }

    public float getSugarQuantity() {
        return sugar.getQuantity();
    }

    public void setSugarMoundness(float moundness) {
        ((GAS_Base) scape.getRoot()).setSugarMoundness(moundness);
    }
}
