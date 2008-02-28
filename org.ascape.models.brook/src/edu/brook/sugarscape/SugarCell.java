/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
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

    /**
     * 
     */
    private static final long serialVersionUID = 4734924131622807190L;

    public final static Rule SUGAR_GROW_BACK_1_RULE = new Rule("Sugar-Grow Back 1") {
        /**
         * 
         */
        private static final long serialVersionUID = -8547038931024510748L;

        public void execute(Agent agent) {
            ((SugarCell) agent).sugarGrowBack1();
        }
    };

    public final static Rule SUGAR_GROW_BACK_INF_RULE = new Rule("Sugar-Grow Back Infinite") {
        /**
         * 
         */
        private static final long serialVersionUID = -6094304727680075963L;

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
