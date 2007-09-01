/*
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
 * _Heatbugs_
 * This is an ascape implementation of the canonical swarm model 'heatbugs'.
 * Hopefully, it will give Swarm users some insight into how ascape is organized.
 * I have tried to mirror swarm names and usage where consistent with ascape.
 * For the orignal Swarm implementation and more about swarm, see http://www.santafe.edu/projects/swarm
 *
 * _Ascape Contact_
 * Miles Parker
 * mparker@brook.edu
 * The Brookings Institution
 * Washington, D.C.
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
     * A data point used within a maximizing search to find the cell with the highest heat point.
     */
    public final static DataPoint MAXIMUM_HEAT_POINT = new DataPointConcrete("Maximum Heat") {
        public double getValue(Object o) {
            return ((HeatCell) o).getHeat();
        }
    };

    /**
     * A data point used within a maximizing search to find the cell with the lowest heat point.
     */
    public final static DataPoint MINIMUM_HEAT_POINT = new DataPointConcrete("Minimum Heat") {
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
