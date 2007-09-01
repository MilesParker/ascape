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

public class CitizenBlueGreen extends Citizen {

    public final static int GREEN = 1;

    public final static int BLUE = 2;

    public int group;

    /**
     * A rule causing active blue agents to attack and kill green agents within their vision.
     * If active, not in jail, and blue, attacks a random green agent (if any) within vision.
     */
    public static final Rule ATTACK_BLUE_ON_GREEN_RULE = new Rule("Attack Blue on Green") {
        public void execute(Agent agent) {
            ((CitizenBlueGreen) agent).attackBlueOnGreen();
        }
    };

    /**
     * A rule causing agents to attack and kill agents in other groups within their vision.
     * If active, and not in jail, attacks a random agent in other group (if any) within vision.
     */
    public static final Rule ATTACK_RETRIBUTION_RULE = new Rule("Attack Other Retribution") {
        public void execute(Agent agent) {
            ((CitizenBlueGreen) agent).attackOther();
        }
    };

    /**
     * A rule causing agents to attack and kill agents in other groups within their vision.
     * If active, and not in jail, attacks a random agent in other group (if any) within vision.
     */
    public static final Rule ATTACK_RETALIATION_RULE = new Rule("Attack Other Retaliation") {
        public void execute(Agent agent) {
            ((CitizenBlueGreen) agent).attackRetaliation();
        }
    };

    /**
     * A rule causing agents to attack and kill agents in other groups within their vision.
     * If active, and not in jail, attacks a random active agent in other group (if any) within vision.
     */
    public static final Rule ATTACK_OTHER_ACTIVE_RULE = new Rule("Attack Other Active Group") {
        public void execute(Agent agent) {
            ((CitizenBlueGreen) agent).attackOtherActive();
        }
    };

    /**
     * A rule causing the agent to decide wether it is active or not.
     */
    public static final Rule DECIDE_STATE_RETRIBUTION = new Rule("Decide State with Retaliation") {
        public void execute(Agent agent) {
            ((CitizenBlueGreen) agent).decideStateWithRetaliation();
        }
    };

    /**
     * A rule causing the agent to decide wether it is active or not.
     */
    public static final Rule DECIDE_STATE_RETRIBUTION_2 = new Rule("Decide State with Retaliation") {
        public void execute(Agent agent) {
            ((CitizenBlueGreen) agent).decideStateWithRetaliation2();
        }
    };

    /**
     * A rule causing the agent to decide wether it is active or not.
     */
    public static final Rule DECIDE_STATE_RETRIBUTION_3 = new Rule("Decide State with Retaliation") {
        public void execute(Agent agent) {
            ((CitizenBlueGreen) agent).decideStateWithRetaliation3();
        }
    };

    public static Conditional CONTAINS_GREEN = new Conditional() {
        public boolean meetsCondition(Object o) {
            CellOccupant a = (CellOccupant) ((HostCell) o).getOccupant();
            if ((a instanceof Citizen) && (((CitizenBlueGreen) a).group == GREEN)) {
                return true;
            }
            return false;
        }
    };

    public static Conditional CONTAINS_BLUE = new Conditional() {
        public boolean meetsCondition(Object o) {
            CellOccupant a = (CellOccupant) ((HostCell) o).getOccupant();
            if ((a instanceof Citizen) && (((CitizenBlueGreen) a).group == BLUE)) {
                return true;
            }
            return false;
        }
    };

    public static Conditional CONTAINS_GREEN_ACTIVE = new Conditional() {
        public boolean meetsCondition(Object o) {
            if (CONTAINS_ACTIVE.meetsCondition(o)) {
                CellOccupant a = (CellOccupant) ((HostCell) o).getOccupant();
                if ((a instanceof Citizen) && (((CitizenBlueGreen) a).group == GREEN)) {
                    return true;
                }
            }
            return false;
        }
    };

    public static Conditional CONTAINS_BLUE_ACTIVE = new Conditional() {
        public boolean meetsCondition(Object o) {
            if (CONTAINS_ACTIVE.meetsCondition(o)) {
                CellOccupant a = (CellOccupant) ((HostCell) o).getOccupant();
                if ((a instanceof Citizen) && (((CitizenBlueGreen) a).group == BLUE)) {
                    return true;
                }
            }
            return false;
        }
    };

    public void scapeCreated() {
        scape.addRule(Citizen.DECIDE_STATE);
    }

    /**
     * Begining population values. Cooperation is random draw, coordinate placement is random
     * in scape. Age is random to maximum age.
     */
    public void initialize() {
        super.initialize();
        group = randomInRange(1, 2);
    }

    public void fission() {
        if ((getHostCell() != null) && (getHostCell().isNeighborAvailable())) {
            CitizenBlueGreen child = (CitizenBlueGreen) this.clone();
            getScape().add(child);
            child.moveTo(getHostCell().findRandomAvailableNeighbor());
            child.age = 0;
        }
    }

    public Color getGroupColor() {
        if (group == BLUE) {
            return Color.blue;
        } else {
            return Color.green;
        }
    }

    public Color getColor() {
        if (state == QUIESCENT) {
            return getGroupColor();
        } else {
            return Color.red;
        }
    }

    public Image getImage() {
        if (state == QUIESCENT) {
            if (group == BLUE) {
                return ImageFeatureFixed.blueBall;
            } else {
                return ImageFeatureFixed.greenBall;
            }
        } else {
            return ImageFeatureFixed.redBall;
        }
    }

    /*public double getGrievance() {
        //return hardship * (1 - ((CVModel) getRoot()).getLegitimacy());
        if (group == BLUE) {
	        return (hardship * (1.0 - ((CVModelInterGroup) getRoot()).getBlueLegitimacy())) * (1.0 - ((CVModel) getRoot()).getActiveThreshold());
	    }
	    else {
	        return (hardship * (1.0 - ((CVModelInterGroup) getRoot()).getGreenLegitimacy())) * (1.0 - ((CVModel) getRoot()).getActiveThreshold());
	    }
    }*/

    protected void decideStateOn(double n) {
//System.out.println(getGrievance() + " - " + getRiskCalculation() + " - " + n);
        if (n > ((CVModel) getRoot()).getActiveThreshold()) {
            if (state != ACTIVE) {
                state = ACTIVE;
                requestUpdate();
            }
            int other = 0;
            if (group == BLUE) {
                other = getHostCell().countWithin(CONTAINS_GREEN, false, ((CVModel) getRoot()).getCitizenVision());
            } else {
                other = getHostCell().countWithin(CONTAINS_BLUE, false, ((CVModel) getRoot()).getCitizenVision());
            }
            if (other == 0) {
                state = QUIESCENT;
            }
        } else {
            if (state != QUIESCENT) {
                state = QUIESCENT;
                requestUpdate();
            }
        }
    }

    /*protected void decideStateOn(double n) {
//System.out.println(getGrievance() + " - " + getRiskCalculation() + " - " + n);
if (n > ((CVModel) getRoot()).getActiveThreshold()) {
if (state != ACTIVE) {
state = ACTIVE;
requestUpdate();
}
int other = 0;
if (group == BLUE) {
other = getHostScape().countWithin(getHostCell(), CONTAINS_GREEN, false, ((CVModel) getRoot()).getCitizenVision());
}
else {
other = getHostScape().countWithin(getHostCell(), CONTAINS_BLUE, false, ((CVModel) getRoot()).getCitizenVision());
}
if (other == 0) {
state = QUIESCENT;
}
}
else {
if (state != QUIESCENT) {
state = QUIESCENT;
requestUpdate();
}
}
}*/

    public double getProbabilityArrestOrRetaliationEstimate() {
        int c = getHostCell().countWithin(Cop.CONTAINS_COP, false, ((CVModel) getRoot()).getCitizenVision());
        int a = getHostCell().countWithin(CONTAINS_ACTIVE, false, ((CVModel) getRoot()).getCitizenVision());
        if (group == BLUE) {
            int og = getHostCell().countWithin(CONTAINS_GREEN, false, ((CVModel) getRoot()).getCitizenVision());
            return 1 - Math.pow(Math.E, -k * ((c + 0.5 * og) / (a + 1)));
        } else {
            int ob = getHostCell().countWithin(CONTAINS_BLUE, false, ((CVModel) getRoot()).getCitizenVision());
            return 1 - Math.pow(Math.E, -k * ((c + 0.5 * ob) / (a + 1)));
        }
    }

    public double getProbabilityArrestOrRetaliationEstimate2() {
        int c = getHostCell().countWithin(Cop.CONTAINS_COP, false, ((CVModel) getRoot()).getCitizenVision());
        int a = getHostCell().countWithin(CONTAINS_ACTIVE, false, ((CVModel) getRoot()).getCitizenVision());
        int ob = getHostCell().countWithin(CONTAINS_BLUE, false, ((CVModel) getRoot()).getCitizenVision());
        int og = getHostCell().countWithin(CONTAINS_GREEN, false, ((CVModel) getRoot()).getCitizenVision());
        if (group == BLUE) {
            int ox = (ob > 0 ? ((og - 1) / ob) : ((og - 1) * 2));
            return 1 - Math.pow(Math.E, -k * ((c + ox) / (a + 1)));
        } else {
            int ox = (og > 0 ? ((ob - 1) / og) : ((ob - 1) * 2));
            return 1 - Math.pow(Math.E, -k * ((c + ox) / (a + 1)));
        }
    }

    public double getProbabilityArrestOrRetaliationEstimate3() {
        int c = getHostCell().countWithin(Cop.CONTAINS_COP, false, ((CVModel) getRoot()).getCitizenVision());
        int a = getHostCell().countWithin(CONTAINS_ACTIVE, false, ((CVModel) getRoot()).getCitizenVision());
        int oa;
        if (group == BLUE) {
            oa = getHostCell().countWithin(CONTAINS_GREEN_ACTIVE, false, ((CVModel) getRoot()).getCitizenVision());
        } else {
            oa = getHostCell().countWithin(CONTAINS_BLUE_ACTIVE, false, ((CVModel) getRoot()).getCitizenVision());
        }
        return 1 - Math.pow(Math.E, -k * ((c + oa) / (a + 1)));
    }

    private void decideStateWithRetaliation() {
        if (!isInJail()) {
            decideStateOn(getGrievance() - getRiskWithRetaliationCalculation());
        }
    }

    private void decideStateWithRetaliation2() {
        if (!isInJail()) {
            decideStateOn(getGrievance() - getRiskWithRetaliationCalculation2());
        }
    }

    private void decideStateWithRetaliation3() {
        if (!isInJail()) {
            decideStateOn(getGrievance() - getRiskWithRetaliationCalculation3());
        }
    }

    public double getRiskWithRetaliationCalculation() {
        return (getProbabilityArrestOrRetaliationEstimate() * getRiskAversion());
    }

    public double getRiskWithRetaliationCalculation2() {
        return (getProbabilityArrestOrRetaliationEstimate2() * getRiskAversion());
    }

    public double getRiskWithRetaliationCalculation3() {
        return (getProbabilityArrestOrRetaliationEstimate3() * getRiskAversion());
    }

    public final void kill(Citizen victim) {
        if (group == BLUE) {
            getScape().getData().getStatCollector("Greens Killed By Blue").addValue(0.0);
        } else {
            getScape().getData().getStatCollector("Blues Killed By Green").addValue(0.0);
        }
        victim.die();
    }

    private void attackBlueOnGreen() {
        if ((state == ACTIVE) && (getHostCell() != null)) {
            HostCell a = null;
            if (group == BLUE) {
                a = (HostCell) getHostCell().findNearest(CONTAINS_GREEN, ((CVModel) getRoot()).getCitizenVision());
            }
            if (a != null) {
                kill((Citizen) a.getOccupant());
            }
        }
    }

    private void attackRetaliation() {
        if ((state != ACTIVE) && (getHostCell() != null)) {
            HostCell a = null;
            if (group == BLUE) {
                a = (HostCell) getHostCell().findNearest(CONTAINS_GREEN_ACTIVE, ((CVModel) getRoot()).getCitizenVision());
            } else {
                a = (HostCell) getHostCell().findNearest(CONTAINS_BLUE_ACTIVE, ((CVModel) getRoot()).getCitizenVision());
            }
            if (a != null) {
                decideStateOn(1 - getRiskWithRetaliationCalculation());
                if (state == ACTIVE) {
                    kill((Citizen) a.getOccupant());
                    moveTo(a);
                }
            }
        }
    }

    private void attackOther() {
        if ((state == ACTIVE) && (getHostCell() != null)) {
            HostCell a = null;
            if (group == BLUE) {
                a = (HostCell) getHostCell().findNearest(CONTAINS_GREEN, ((CVModel) getRoot()).getCitizenVision());
            } else {
                a = (HostCell) getHostCell().findNearest(CONTAINS_BLUE, ((CVModel) getRoot()).getCitizenVision());
            }
            if (a != null) {
                kill((Citizen) a.getOccupant());
                moveTo(a);
            }
        }
    }

    private void attackOtherActive() {
        if ((state == ACTIVE) && (getHostCell() != null)) {
            HostCell a = null;
            if (group == BLUE) {
                a = (HostCell) getHostCell().findNearest(CONTAINS_GREEN_ACTIVE, ((CVModel) getRoot()).getCitizenVision());
            } else {
                a = (HostCell) getHostCell().findNearest(CONTAINS_BLUE_ACTIVE, ((CVModel) getRoot()).getCitizenVision());
            }
            if (a != null) {
                kill((Citizen) a.getOccupant());
            }
        }
    }
}
