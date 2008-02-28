/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.cv.paper;

import edu.brook.cv.Citizen;

public class CVModelIRun1 extends CVModelI {

    /**
     * 
     */
    private static final long serialVersionUID = -7577047616416112439L;

    public void createScape() {
        super.createScape();
        copVision = 1.7;
        personVision = 1.7;
        legitimacy = .89;
        //No Movement for this run
        people.getRules().clear();
        //people.addInitialRule(MOVE_RANDOM_LOCATION_RULE);
        people.addRule(Citizen.CHECK_JAIL);
        people.addRule(Citizen.DECIDE_STATE);
    }
}
