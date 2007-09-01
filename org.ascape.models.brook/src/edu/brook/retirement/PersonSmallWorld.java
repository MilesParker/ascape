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
 * Miles Parker
 * mparker@brook.edu
 * The Brookings Institution
 * Washington, D.C.
 */

package edu.brook.retirement;

import java.awt.Color;
import java.util.List;

import org.ascape.model.Cell;
import org.ascape.model.Scape;
import org.ascape.model.space.Coordinate2DDiscrete;


/**
 * A player in the prisoner's dilemma game.
 *
 * @author Miles Parker
 * @version 1.0
 **/
public class PersonSmallWorld extends Cell {

    public final static int RATIONAL = 1;

    public final static int IMITATOR = 2;

    public final static int RANDOM = 3;

    public final static int WORKING = -1;

    public final static int RETIRED = -2;

    public final static int DEAD = -3;

    /**
     * The agent's current (fixed for now) strategy.
     */
    private int strategy;

    /**
     * The agent's current (fixed for now) strategy.
     */
    private int status;

    /**
     * The agent's current (fixed for now) strategy.
     */
    private boolean eligible = false;

    /**
     * The agent's current age.
     */
    private int age;

    /**
     * The agent's current age.
     */
    private int deathAge;

    /**
     * Begining PopulationSmallWorld values. Cooperation is random draw, coordinate placement is random
     * in scape. Age is random to maximum age.
     */
    public void initialize() {
        super.initialize();
        //age = ((PopulationSmallWorld) getScape()).getAge(((Coordinate2DDiscrete) getCoordinate()).getYValue());
        float strategyChance = getRandom().nextFloat();
        if (strategyChance < ((PopulationSmallWorld) getScape()).getFractionRational()) {
            strategy = RATIONAL;
        } else if (strategyChance >= (1.0 - ((PopulationSmallWorld) getScape()).getFractionRandom())) {
            strategy = RANDOM;
        } else {
            strategy = IMITATOR;
        }
        status = WORKING;
        eligible = false;
        deathAge = randomInRange(60, 100);
        age = ((PopulationSmallWorld) getScape()).getAgeForRow(((Coordinate2DDiscrete) getCoordinate()).getYValue());
        if (age >= deathAge) {
            status = DEAD;
        }
        if (age >= ((PopulationSmallWorld) scape).getRetirementEligibilityAge()) {
            eligible = true;
        }
    }

    public void scapeCreated() {
        scape.addRule(METABOLISM_RULE);
        scape.addRule(ITERATE_RULE);
    }

    public void metabolism() {
        if (age == ((PopulationSmallWorld) scape).getOldestAge()) {
            age = ((PopulationSmallWorld) scape).getYoungestAge();
            initialize();
        } else {
            age++;
            if (age == deathAge) {
                status = DEAD;
            }
        }
        if (age >= ((PopulationSmallWorld) scape).getRetirementEligibilityAge()) {
            eligible = true;
        }
    }

    public void iterate() {
        //callOrder = ((PopulationSmallWorld) scape).callOrder;
        //((PopulationSmallWorld) scape).callOrder++;
        if ((eligible) && (status == WORKING)) {
            switch (strategy) {
                case RATIONAL:
                    status = RETIRED;
                    break;
                case IMITATOR:
                    double poll = 0.0;
                    double sample = 0.0;
                    List network = findNeighbors();
                    for (Object person : network) {
                        PersonSmallWorld personSmallWorld = ((PersonSmallWorld) person);
                        if (personSmallWorld.status == RETIRED) {
                            poll++;
                        }
                        if ((personSmallWorld.eligible) && (personSmallWorld.status != DEAD)) {
                            sample++;
                        }
                    }
                    if ((poll != 0.0) && (poll >= (sample / 2.0))) {
                        status = RETIRED;
                    }
                    break;
                case RANDOM:
                    if (getRandom().nextFloat() > .50) {
                        status = RETIRED;
                    }
                    break;
            }
        }
    }

    /**
     * Sets the agent scape that all scape of this class will belong to.
     **/
    public void setScape(Scape scape) {
        this.scape = scape;
    }

    /**
     * The color to paint this agent; blue if cooperate, red if defect.
     */
    public Color getColor() {
        /*switch (strategy) {
            case RATIONAL:
                return Color.yellow;
            case IMITATOR:
                return Color.magenta;
            case RANDOM:
                return Color.blue;
            default:
                throw new RuntimeException("Undefined color");
        }*/
        /*if (eligible) {
            return Color.yellow;
        }
        else {
            return Color.red;
        }*/
        if (strategy == RATIONAL) {
            return Color.magenta;
        } else if (strategy == RANDOM) {
            return Color.yellow;
        } else {
            switch (status) {
                case WORKING:
                    return Color.blue;
                case RETIRED:
                    return Color.red;
                case DEAD:
                    return Color.white;
                default:
                    return Color.gray;
            }
        }
    }

    //public void setAge(int age) {
    //    this.age = age;
    //}

    public int getAge() {
        return age;
    }

    /**
     * A small string representation of this agent.
     */
    public String toString() {
        return "PersonSmallWorld " + coordinate;
    }
}
