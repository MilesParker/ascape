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
import java.util.List;

import org.ascape.model.Agent;
import org.ascape.model.Cell;
import org.ascape.model.CellOccupant;
import org.ascape.model.HostCell;
import org.ascape.model.rule.Rule;
import org.ascape.util.Conditional;
import org.ascape.util.vis.ImageFeatureFixed;

public class Citizen extends CellOccupant {

    /**
     * A rule causing agents to check its jail term, releasing itself if the term is finished.
     */
    public static final Rule CHECK_JAIL = new Rule("Check Jail") {
        public void execute(Agent agent) {
            ((Citizen) agent).checkJail();
        }
    };

    /**
     * A rule causing the agent to decide wether it is active or not.
     */
    public static final Rule DECIDE_STATE = new Rule("Decide State") {
        public void execute(Agent agent) {
            ((Citizen) agent).decideState();
        }
    };

    public final static int QUIESCENT = 1;

    public final static int ACTIVE = 2;

    public final static int IN_JAIL = -1;

    protected int state = QUIESCENT;

    private double hardship;

    private double riskAversion;

    private double jailTermsLeft;

    private static double neighborCaptureHardshipChange = .8;

    /**
     * The agent's current age.
     */
    protected int age;

    /**
     * The arrest probability modifier.
     * Constant k is set so that the probability of arrest is .9, if only this agent is active
     * and one cop is present (C/A = 1)
     */
    protected static double k = -Math.log(.1);

    public static Conditional CONTAINS_ACTIVE = new Conditional() {
        public boolean meetsCondition(Object o) {
            CellOccupant a = (CellOccupant) ((HostCell) o).getOccupant();
            if ((a instanceof Citizen) && (((Citizen) a).getState() == Citizen.ACTIVE)) {
                return true;
            }
            return false;
        }
    };

    /**
     * Begining population values. Cooperation is random draw, coordinate placement is random
     * in scape. Age is random to maximum age.
     */
    public void initialize() {
        super.initialize();
        hardship = getRandom().nextDouble();
        //hardship = .5;
        riskAversion = getRandom().nextDouble();
        //Age may or may not be used, depending on wether the death rule is set..
        age = randomInRange(0, ((CVModel) scape.getRoot()).getDeathAge());
    }

    public void randomWalk() {
        if (state != IN_JAIL) {
            super.randomWalk();
        }
    }

    public Color getGroupColor() {
        return Color.blue;
    }

    public Color getColor() {
        if (state == QUIESCENT) {
            return Color.blue;
        } else {
            return Color.red;
        }
    }

    public Image getImage() {
        if (state == QUIESCENT) {
            return ImageFeatureFixed.blueBall;
        } else {
            return ImageFeatureFixed.redBall;
        }
    }

    public void metabolism() {
        age++;
    }

    public void fission() {
        if ((getHostCell() != null) && (getHostCell().isNeighborAvailable())) {
            Citizen child = (Citizen) this.clone();
            getScape().add(child);
            child.moveTo(getHostCell().findRandomAvailableNeighbor());
            child.age = 0;
        }
    }

    public boolean fissionCondition() {
        return (getRandom().nextFloat() < ((CVModelInterGroup) scape.getRoot()).getFissionProbability());
    }

    public boolean deathCondition() {
        return (age > ((CVModel) getScape().getRoot()).getDeathAge());
    }

    private HostCell oldHome;

    public void capture() {
        state = IN_JAIL;
        if (((CVModel) getRoot()).getJailTerm() != Integer.MAX_VALUE) {
            jailTermsLeft = randomInRange(0, ((CVModel) getRoot()).getJailTerm());
        } else {
            jailTermsLeft = ((CVModel) getRoot()).getJailTerm();
        }
        oldHome = getHostCell();
        leave();
    }

    private static Rule NEIGHBOR_CAPTURED = new Rule("Neighbor Captured") {
        public void execute(Agent agent) {
            ((Citizen) agent).setHardship(((Citizen) agent).getHardship() + neighborCaptureHardshipChange);
        }
    };

    public void notifyNeighborsOfCapture() {
        List candidates = getHostCell().findNeighbors();
        for (int i = 0; i < candidates.size(); i++) {
            Cell candidate = (Cell) ((HostCell) candidates.get(i)).getOccupant();
            if (candidate != null) {
                if (candidate instanceof Citizen) {
                    NEIGHBOR_CAPTURED.execute(candidate);
                }
            }
        }
    }

    public boolean isInJail() {
        return state == IN_JAIL;
    }

    private void checkJail() {
        if (isInJail()) {
            jailTermsLeft--;
            if (jailTermsLeft <= 0) {
                state = QUIESCENT;
                HostCell closestSiteToHome = (HostCell) getHostScape().findNearest(oldHome.getCoordinate(), HostCell.IS_AVAILABLE, true, 16.0);
                if (closestSiteToHome != null) {
                    moveTo(closestSiteToHome);
                } else {
                    moveToRandomLocation();
                }
                //moveToRandomLocation();
            }
        }
    }

    protected void decideStateOn(double n) {
//System.out.println(getGrievance() + " - " + getRiskCalculation() + " - " + n);
        if (n > ((CVModel) getRoot()).getActiveThreshold()) {
            if (state != ACTIVE) {
                state = ACTIVE;
                requestUpdate();
            }
        } else {
            if (state != QUIESCENT) {
                state = QUIESCENT;
                requestUpdate();
            }
        }
    }

    private void decideState() {
        if (!isInJail()) {
            decideStateOn(getGrievance() - getRiskCalculation());
        }
    }

    public double getProbabilityArrestEstimate() {
        int c = getHostCell().countWithin(Cop.CONTAINS_COP, false, ((CVModel) getRoot()).getCitizenVision());
        int a = getHostCell().countWithin(CONTAINS_ACTIVE, false, ((CVModel) getRoot()).getCitizenVision());
        return 1 - Math.pow(Math.E, -k * (c / (a + 1)));
    }

    public double getRiskCalculation() {
        return (getProbabilityArrestEstimate() * getRiskAversion());
    }

    public int getState() {
        return state;
    }

    public double getGrievance() {
        //return hardship * (1 - ((CVModel) getRoot()).getLegitimacy());
        return (hardship * (1.0 - ((CVModel) getRoot()).getLegitimacy())) * (1.0 - ((CVModel) getRoot()).getActiveThreshold());
    }

    public double getHardship() {
        return hardship;
    }

    public void setHardship(double hardship) {
        if (hardship <= 1.0) {
            this.hardship = hardship;
        } else {
            this.hardship = 1.0;
        }
    }

    public double getRiskAversion() {
        return riskAversion;
    }

    public void setRiskAversion(double riskAversion) {
        this.riskAversion = riskAversion;
    }

    public double getK_ArrestProbabilityModifier() {
        return k;
    }

    public void setK_ArrestProbabilityModifier(double arrestProbabilityModifier) {
        k = arrestProbabilityModifier;
    }

    public void moveTowardsActivity() {
        if (state == ACTIVE) {
            if (getHostCell() != null) {
                HostCell closestActive = (HostCell) getHostScape().findNearest(getHostCell().getCoordinate(), CONTAINS_ACTIVE, false, ((CVModel) getRoot()).getCitizenVision());
//HostCell closestActive = (HostCell) ((ScapeArray2D) getHostScape()).getCell(0,0);
                //moveToward(closestActive);
                if (closestActive != null) {
                    moveToward(closestActive);
                }
            }
        } else {
            randomWalk();
        }
    }

    public void moveFromCop() {
        if (state == ACTIVE) {
            if (getHostCell() != null) {
                HostCell closestCop = (HostCell) getHostScape().findNearest(getHostCell().getCoordinate(), Cop.CONTAINS_COP, false, ((CVModel) getRoot()).getCitizenVision());
//HostCell closestActive = (HostCell) ((ScapeArray2D) getHostScape()).getCell(0,0);
                if (closestCop != null) {
                    moveAway(closestCop);
                }
            }
        }
    }

    /**
     * A small string representation of this agent.
     */
    public String getName() {
        return "Citizen";
    }
}
