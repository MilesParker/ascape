/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.cv.paper;

import org.ascape.model.event.ScapeEvent;


public class CVModelIRun3 extends CVModelIRun3and4and5 {

    /**
     * 
     */
    private static final long serialVersionUID = 7267855718918516992L;

    public void scapeIterated(ScapeEvent event) {
        super.scapeIterated(event);
        if ((getIteration() % 3) == 0) {
            if (getLegitimacy() - 0.01 > 0.0) {
                setLegitimacy(getLegitimacy() - 0.01);
            } else {
                setLegitimacy(0.0);
            }
        }
    }
}
