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
 * _Emergence of classes_
 * See _The Emergence of Economic Classes in an Agent-Based Bargaining Model_
 * Joshua Epstein
 * jepstein@brook.edu
 * Robert Axtell
 * raxtell@brook.edu
 * Peyton Young
 * pyoung@brook.edu
 *
 * Implementation:
 * Miles Parker
 * mparker@brook.edu
 * The Brookings Institution
 * Washington, D.C.
 */

package edu.brook.classes;

import org.ascape.model.Agent;
import org.ascape.model.CellOccupant;
import org.ascape.util.Utility;

public class Bargainer extends CellOccupant {

    protected Strategy[] strategyMemory;

    protected int oldestMemory;

    int intraLowPlayers;

    int intraMediumPlayers;

    int intraHighPlayers;

    public void initialize() {
        setMemorySize(randomInRange(((BargainingModelBase) getRoot()).getMinimumMemorySize(), ((BargainingModelBase) getRoot()).getMaximumMemorySize()));
        initializeMemoryRandom();
        lastStrategy = Strategy.randomStrategy(getRandom());
    }

    public void initializeMemoryRandom() {
        for (int i = 0; i < strategyMemory.length; i++) {
            strategyMemory[i] = Strategy.randomStrategy(getRandom());
        }
    }

    public void initializeMemory(Strategy strategy) {
        for (int i = 0; i < strategyMemory.length; i++) {
            strategyMemory[i] = strategy;
        }
    }

    public void initializeMemoryHiLow() {
        for (int i = 0; i < strategyMemory.length; i++) {
            if (Utility.randomIs(getRandom())) {
                strategyMemory[i] = Strategy.LOW_STRATEGY;
            } else {
                strategyMemory[i] = Strategy.HIGH_STRATEGY;
            }
        }
    }

    protected final Strategy calculateBestStrategy(float lowPlayWinEstimate, float mediumPlayWinEstimate, float highPlayWinEstimate) {
        if (lowPlayWinEstimate > mediumPlayWinEstimate) {
            if (lowPlayWinEstimate > highPlayWinEstimate) {
                return Strategy.LOW_STRATEGY;
            } else if (lowPlayWinEstimate < highPlayWinEstimate) {
                return Strategy.HIGH_STRATEGY;
            } else {
                if (randomIs()) {
                    return Strategy.LOW_STRATEGY;
                } else {
                    return Strategy.HIGH_STRATEGY;
                }
            }
        } else if (lowPlayWinEstimate < mediumPlayWinEstimate) {
            if (mediumPlayWinEstimate > highPlayWinEstimate) {
                return Strategy.MEDIUM_STRATEGY;
            } else if (mediumPlayWinEstimate < highPlayWinEstimate) {
                return Strategy.HIGH_STRATEGY;
            } else {
                if (randomIs()) {
                    return Strategy.MEDIUM_STRATEGY;
                } else {
                    return Strategy.HIGH_STRATEGY;
                }
            }
        } else {
            if (highPlayWinEstimate > lowPlayWinEstimate) { //&>medPlay
                return Strategy.HIGH_STRATEGY;
            } else if (highPlayWinEstimate < lowPlayWinEstimate) {
                if (randomIs()) {
                    return Strategy.LOW_STRATEGY;
                } else {
                    return Strategy.MEDIUM_STRATEGY;
                }
            } else {
                //All equal, pick one of three
                //This will  be called if all values are 0.0 of course, taking care of the case where agents have no memory
                float pick = getRandom().nextFloat();
                if (pick < 1.0 / 3.0) {
                    return Strategy.LOW_STRATEGY;
                } else if (pick < 2.0 / 3.0) {
                    return Strategy.MEDIUM_STRATEGY;
                } else {
                    return Strategy.HIGH_STRATEGY;
                }
            }
        }
    }

    public Strategy calculateNextStrategy(Bargainer agent) {
        Strategy nextStrategy;
        intraLowPlayers = 0;
        intraMediumPlayers = 0;
        intraHighPlayers = 0;
        for (int i = 0; i < strategyMemory.length; i++) {
            if (strategyMemory[i] == Strategy.LOW_STRATEGY) {
                intraLowPlayers++;
            } else if (strategyMemory[i] == Strategy.MEDIUM_STRATEGY) {
                intraMediumPlayers++;
            } else if (strategyMemory[i] == Strategy.HIGH_STRATEGY) {
                intraHighPlayers++;
            }
        }
        float lowPlayWinEstimate = Strategy.LOW_STRATEGY.getDemand() * strategyMemory.length;
        float mediumPlayWinEstimate = Strategy.MEDIUM_STRATEGY.getDemand() * (intraMediumPlayers + intraLowPlayers);
        float highPlayWinEstimate = Strategy.HIGH_STRATEGY.getDemand() * (intraLowPlayers);
        return calculateBestStrategy(lowPlayWinEstimate, mediumPlayWinEstimate, highPlayWinEstimate);
    }

    public void addMemory(Bargainer a, Strategy s) {
        strategyMemory[oldestMemory] = s;
        oldestMemory++;
        if (oldestMemory >= strategyMemory.length) {
            oldestMemory = 0;
        }
    }

    public int getMemorySize() {
        if (strategyMemory != null) {
            return strategyMemory.length;
        } else {
            return 0;
        }
    }

    public void setMemorySize(int size) {
        strategyMemory = new Strategy[size];
    }

    public int countIntra() {
        return getMemorySize();
    }

    public int countIntraLow() {
        return intraLowPlayers;
    }

    public int countIntraMedium() {
        return intraMediumPlayers;
    }

    public int countIntraHigh() {
        return intraHighPlayers;
    }

    protected Strategy lastStrategy;

    public void play(Agent agent) {
        this.playSide(agent);
        ((Bargainer) agent).playSide(this);
        this.addMemory(((Bargainer) agent), ((Bargainer) agent).lastStrategy);
        ((Bargainer) agent).addMemory(this, lastStrategy);
    }

    public void playSide(Agent agent) {
        if (getRandom().nextFloat() > ((BargainingModelBase) getRoot()).getRandomStrategyProbability()) {
            lastStrategy = calculateNextStrategy((Bargainer) agent);
        } else {
            lastStrategy = Strategy.randomStrategy(getRandom());
        }
    }

    public Strategy getLastStrategy() {
        return lastStrategy;
    }
}
