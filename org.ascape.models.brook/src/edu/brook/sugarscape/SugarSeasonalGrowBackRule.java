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

import org.ascape.model.Agent;
import org.ascape.model.Cell;
import org.ascape.model.rule.Rule;
import org.ascape.model.space.Coordinate2DDiscrete;

public class SugarSeasonalGrowBackRule extends Rule {

    private float epsilon = 0.125f;

    private int seasonLength = 50;

    public SugarSeasonalGrowBackRule() {
        super("Sugar Grow Back Seasonally");
    }

    public SugarSeasonalGrowBackRule(float epsilon, int seasonLength) {
        super("Sugar Grow Back Seasonally");
        this.epsilon = epsilon;
        this.seasonLength = seasonLength;
    }

    public void execute(Agent agent) {
        int iter = agent.getScape().getIteration();
        if ((((float) iter / ((float) seasonLength * 2)) - (iter / (seasonLength * 2))) > .5) {
            //Summer
            if (((Coordinate2DDiscrete) ((Cell) agent).getCoordinate()).getYValue() < 25) {
                ((SugarCell) agent).sugarGrowBack1();
            } else {
                ((SugarCell) agent).sugarGrowBackEpsilon(epsilon);
            }
        } else {
            //Winter
            if (((Coordinate2DDiscrete) ((Cell) agent).getCoordinate()).getYValue() < 25) {
                ((SugarCell) agent).sugarGrowBackEpsilon(epsilon);
            } else {
                ((SugarCell) agent).sugarGrowBack1();
            }
        }
    }

    /**
     * Returns the rate at which sugar grows back in the winter.
     * Model parameter.
     */
    public float getWinterSugarGrowbackRate() {
        return epsilon;
    }

    /**
     * Sets the rate at which sugar grows back in the winter.
     * Model parameter.
     */
    public void setWinterSugarGrowbackRate(float epsilon) {
        this.epsilon = epsilon;
    }

    /**
     * Returns the rate at which sugar grows back in the winter.
     * Model parameter.
     */
    public int getSeasonLength() {
        return seasonLength;
    }

    /**
     * Sets the rate at which sugar grows back in the winter.
     * Model parameter.
     */
    public void setSeasonLength(int seasonLength) {
        this.seasonLength = seasonLength;
    }
}
