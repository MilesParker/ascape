/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.cv;

import java.awt.Color;
import java.awt.Image;

import org.ascape.model.Agent;
import org.ascape.model.CellOccupant;
import org.ascape.model.HostCell;
import org.ascape.model.rule.Rule;
import org.ascape.util.Conditional;
import org.ascape.util.vis.ImageFeatureFixed;

public class Cop extends CellOccupant {

    //public double vision = 0.0;

    /**
     * 
     */
    private static final long serialVersionUID = 7755622689815803828L;
    public static Conditional CONTAINS_COP = new Conditional() {
        /**
         * 
         */
        private static final long serialVersionUID = -5847480327062056459L;

        public boolean meetsCondition(Object o) {
            if (((HostCell) o).getOccupant() instanceof Cop) {
                return true;
            }
            return false;
        }
    };

    public void patrol() {
        HostCell a = (HostCell) getHostCell().findNearest(Citizen.CONTAINS_ACTIVE, ((CVModel) getRoot()).getCopVision());
        if (a != null) {
            //moveToward(a);
            ((Citizen) a.getOccupant()).capture();
            moveTo(a);
        }
    }

    public void scapeCreated() {
        scape.addInitialRule(MOVE_RANDOM_LOCATION_RULE);
        //scape.addInitialRule(new MoveRandomWithin(0, 0, 10, 10));
        scape.addRule(new Rule("Patrol") {
            /**
             * 
             */
            private static final long serialVersionUID = 4416435136439233492L;

            public void execute(Agent a) {
                ((Cop) a).patrol();
            }
        });
        scape.addRule(RANDOM_WALK_RULE);
    }

    public Color getColor() {
        return Color.black;
    }

    public Image getImage() {
        return ImageFeatureFixed.blackBall;
    }

    /**
     * A small string representation of this agent.
     */
    public String getName() {
        return "Cop";
    }
}
