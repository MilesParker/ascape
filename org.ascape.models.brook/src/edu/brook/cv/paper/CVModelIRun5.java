/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.cv.paper;

import java.awt.Color;

import org.ascape.model.event.ScapeEvent;


public class CVModelIRun5 extends CVModelIRun3and4and5 {

    /**
     * 
     */
    private static final long serialVersionUID = 7423322517274724326L;

    public void scapeSetup(ScapeEvent scapeEvent) {
        super.scapeSetup(scapeEvent);
        setLegitimacy(0.80);
        setTargetCopDensity(.06);
    }

    public void scapeIterated(ScapeEvent event) {
        super.scapeIterated(event);
        if (getTargetCopDensity() - 0.001 > 0.0) {
            setTargetCopDensity(getTargetCopDensity() - 0.001);
        } else {
            setTargetCopDensity(0.0);
        }
        chart.removeSeries("Sum Legitimacy");
        chart.addSeries("Sum Cop Density", Color.gray);
    }
}
