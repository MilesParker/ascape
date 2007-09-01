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
 * _Demographic Prisoners Dilemma_
 * See _Zones of Cooperation in Demographic Prisoner's Dilemma_
 * Santa Fe Institute Economics Research Program Working Paper
 * Joshua Epstein
 * jepstein@brook.edu
 *
 * Miles Parker
 * mparker@brook.edu
 * The Brookings Institution
 * Washington, D.C.
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

    public static Conditional CONTAINS_COP = new Conditional() {
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
