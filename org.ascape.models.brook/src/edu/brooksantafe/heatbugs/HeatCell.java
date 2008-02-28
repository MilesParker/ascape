/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brooksantafe.heatbugs;

import org.ascape.model.HostCell;
import org.ascape.model.rule.Diffusable;
import org.ascape.util.data.DataPoint;
import org.ascape.util.data.DataPointConcrete;

/**
 * A heat cell, a cell capable of storing heat and diffusing it to other cells.
 **/
public class HeatCell extends HostCell implements Diffusable {

    /**
     * 
     */
    private static final long serialVersionUID = -3256626039608216834L;

    /**
     * A data point used within a maximizing search to find the cell with the highest heat point.
     */
    public final static DataPoint MAXIMUM_HEAT_POINT = new DataPointConcrete("Maximum Heat") {
        /**
         * 
         */
        private static final long serialVersionUID = 3755810443335739012L;

        public double getValue(Object o) {
            return ((HeatCell) o).getHeat();
        }
    };

    /**
     * A data point used within a maximizing search to find the cell with the lowest heat point.
     */
    public final static DataPoint MINIMUM_HEAT_POINT = new DataPointConcrete("Minimum Heat") {
        /**
         * 
         */
        private static final long serialVersionUID = 6976409253482738480L;

        public double getValue(Object o) {
            return -((HeatCell) o).getHeat();
        }
    };

    /**
     * The maximum heat that a cell can contain.
     */
    public static final float MAX_HEAT = (float) 0x7fff;

    /**
     * The heat value.
     */
    private float heat;

    /**
     * Returns the heat at this cell.
     */
    public final float getHeat() {
        return heat;
    }

    /**
     * Sets the heat at this cell.
     */
    public final void setHeat(float heat) {
        this.heat = heat;
        if (this.heat > MAX_HEAT) {
            this.heat = MAX_HEAT;
        }
    }

    /**
     * Adds the provided heat to this cell.
     */
    public final void addHeat(float heat) {
        setHeat(this.heat + heat);
    }

    /**
     * A temporary value used to store a diffusion value while it is being calculated.
     */
    private double diffusionTemp;

    /**
     * Returns the temporary diffusion value. Required by the diffusable interface.
     */
    public final double getDiffusionTemp() {
        return diffusionTemp;
    }

    /**
     * Sets the temporary diffusion value. Required by the diffusable interface.
     */
    public final void setDiffusionTemp(double diffusionTemp) {
        this.diffusionTemp = diffusionTemp;
    }

    public String getName() {
        return "Heatbug " + coordinate;
    }
}
