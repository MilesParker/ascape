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
 * Artificial Anasazi - Long House Valley
 * see _Understanding Anasazi Culture Change Through Agent-Based Modeling_
 * Jeffrey S. Dean, George J. Gumerman, Joshua M. Epstein, Robert Axtell,
 * Alan C. Swedlund, Miles Parker, and Steven McCarroll
 *
 * Java Implementation:
 * Miles Parker
 * mparker@brook.edu
 * Original C++ Implementation:
 * Steven McCarroll
 * The Brookings Institution
 * Washington, D.C.
 */

package edu.brook.aa;

import java.awt.Color;

import org.ascape.model.Scape;
import org.ascape.model.space.Singleton;

public class EnvironmentZone extends Scape {

    private double pdsi;

    private double hydro;

    private double ag;

    private double apdsi;

    protected Color color;

    private boolean isWaterSource = false;

    public EnvironmentZone(String name, Color color) {
        super(new Singleton());
        setName(name);
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public double getPDSI() {
        return pdsi;
    }

    public void setPDSI(double pdsi) {
        this.pdsi = pdsi;
    }

    public double getHydrology() {
        return hydro;
    }

    public void setHydrology(double hydro) {
        if (this.hydro != hydro) {
            requestUpdate();
        }
        this.hydro = hydro;
    }

    public double getAggredation() {
        return ag;
    }

    public void setAggredation(double ag) {
        if (this.ag != ag) {
            //requestUpdate();
        }
        this.ag = ag;
    }

    public double getAPDSI() {
        return apdsi;
    }

    public void setAPDSI(double apdsi) {
        if (this.apdsi != apdsi) {
            requestUpdate();
        }
        this.apdsi = apdsi;
    }

    public boolean isUpdateNeeded(int within) {
        if (super.isUpdateNeeded(within)) {
            return true;
        }
        return false;
    }

    public boolean isWaterSource() {
        return isWaterSource;
    }

    public void setIsWaterSource(boolean isWaterSource) {
        if (this.isWaterSource != isWaterSource) {
            requestUpdate();
        }
        this.isWaterSource = isWaterSource;
    }
}
