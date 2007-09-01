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
import org.ascape.model.rule.Diffusable;
import org.ascape.model.rule.Diffusion;

public class PollutableSugarCell extends SugarCell implements Diffusable {

    private static float maxPollution = 1;

    /**
     * Factor for pollution from gathering.
     */
    private static int sigma = 8;

    private float pollution;

    public void initialize() {
        super.initialize();
        pollution = 0;
        maxPollution = 1;
    }

    public void scapeCreated() {
        scape.addRule(new Diffusion("Pollution") {
            public double getDiffusionValue(Agent agent) {
                return ((PollutableSugarCell) agent).getPollution();
            }

            public void setDiffusionValue(Agent agent, double value) {
                ((PollutableSugarCell) agent).setPollution((float) value);
            }
        });
    }

    public float takeSugar() {
        addPollution(sigma * sugar.getQuantity());
        return super.takeSugar();
    }

    public float getPerceivedValue() {
        return sugar.getQuantity() / (1 + pollution);
    }

    public float getPollution() {
        return pollution;
    }

    public float getMaxPollution() {
        return maxPollution;
    }

    public void setPollution(float pollution) {
        this.pollution = pollution;
        if (pollution > maxPollution) {
            maxPollution = pollution;
        }
        requestUpdate();
    }

    public void addPollution(float pollution) {
        setPollution(this.pollution + pollution);
    }

    private double diffusionTemp;

    public double getDiffusionTemp() {
        return diffusionTemp;
    }

    public void setDiffusionTemp(double diffusionTemp) {
        this.diffusionTemp = diffusionTemp;
    }

    public double getValue(Object object) {
        return (1.0F - ((PollutableSugarCell) object).getPollution() / ((PollutableSugarCell) object).getMaxPollution());
    }
}
