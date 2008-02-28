/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.aa;

import java.awt.Color;

import org.ascape.model.Scape;
import org.ascape.model.space.Singleton;

public class EnvironmentZone extends Scape {

    /**
     * 
     */
    private static final long serialVersionUID = -2493621424309157834L;

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
