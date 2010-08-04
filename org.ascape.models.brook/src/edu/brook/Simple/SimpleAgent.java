/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */
//-------------------------------------------------------------------

package edu.brook.Simple;

import org.ascape.model.CellOccupant;
import org.ascape.model.Scape;


//-------------------------------------------------------------------

public class SimpleAgent extends CellOccupant {

    /**
     * 
     */
    private static final long serialVersionUID = -4066901383850640902L;
    static int counter;
    static SimpleAgent theAgent;

    //The agent's current happiness.
    protected double happiness;
    protected int behaviour;

    //----------------------------------------------------------------
    SimpleAgent() {
    }

    //----------------------------------------------------------------
    public void initialize() {
        super.initialize();
        this.setHappiness();
        behaviour = counter;
        counter++;
        if (behaviour == 0) theAgent = this;
    }

    //----------------------------------------------------------------
    public void scapeCreated() {
        scape.addRule(UPDATE_RULE);
    }

    //----------------------------------------------------------------
    public void update() {
        if (behaviour == 0) {
            happiness += happiness;
            if (happiness > 1000) happiness = 1000;
            System.out.println("Agent happiness is " + happiness);
        } else {
            if (randomInRange(0, 9) < 1) {
                theAgent.setHappiness();
                System.out.println("Environment has broken agent's happiness");
            }
        }
    }

    //----------------------------------------------------------------
    public void setScape(Scape scape) {
        this.scape = scape;
    }

    //----------------------------------------------------------------
    public double getHappiness() {
        if (behaviour == 0)
            return happiness;
        else
            return 0;
    }

    //----------------------------------------------------------------
    public void setHappiness() {
        this.happiness = 3;
    }

}
