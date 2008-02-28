/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.aa;

import org.ascape.model.Agent;

public class ClanFindMateRule extends FindMateRule {

    /**
     * 
     */
    private static final long serialVersionUID = 938548733973182435L;

    public ClanFindMateRule(Person candidate) {
        super(candidate);
    }

    public void execute(Agent a) {
        if ((((Person) a).getMate() == null) && (((Person) a).getSex() == Person.MALE) && (((Person) a).getAge() > ((LHV) a.getRoot()).getMinFertilityAge()) && (((Person) a).getHousehold().getClan() != candidate.getHousehold().getClan()) && (((Person) a).getHousehold() != candidate.getHousehold())) {
            lastMate = ((Person) a);
        }
    }
}

;
