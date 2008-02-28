/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.cv.paper;

import java.awt.Color;

import org.ascape.model.event.ScapeEvent;


public class CVModelIRun3and4and5 extends CVModelI {

    /**
     * 
     */
    private static final long serialVersionUID = -3581711081946699315L;

    public void createScape() {
        super.createScape();
        copVision = 7.0;
        personVision = 7.0;
        initialCopDensity = .074;
        jailTerm = Integer.MAX_VALUE;
    }

    public void scapeSetup(ScapeEvent scapeEvent) {
        super.scapeSetup(scapeEvent);
        setLegitimacy(0.90);
    }

    public void scapeIterated(ScapeEvent event) {
        super.scapeIterated(event);
        chart.removeSeries("Count Quiescent");
        chart.addSeries("Sum Legitimacy", Color.gray);
    }
}
