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

import java.awt.Color;

import org.ascape.model.Agent;



public class BargainerTagged extends Bargainer {

    public static final int LIGHT_TAG = -1;

    public static final int DARK_TAG = 1;

    private BargainerTagged[] opponentMemory;

    protected int tag;

    private int interPlayers;

    private int intraPlayers;

    int interLowPlayers;

    int interMediumPlayers;

    int interHighPlayers;

    public void initialize() {
        super.initialize();
        if (randomIs()) {
            tag = LIGHT_TAG;
        } else {
            tag = DARK_TAG;
        }
        initializeOpponentMemory();
    }

    public void initializeOpponentMemory() {
        for (int i = 0; i < strategyMemory.length; i++) {
            Agent randomAgent = getScape().findRandom();
            while (randomAgent == this) {
                randomAgent = getScape().findRandom();
            }
            opponentMemory[i] = (BargainerTagged) randomAgent;
        }
    }

    public void initializeMemoryRandom(int forTag) {
        for (int i = 0; i < strategyMemory.length; i++) {
            if (opponentMemory[i].tag == forTag) {
                strategyMemory[i] = Strategy.randomStrategy(getRandom());
            }
        }
    }

    public void initializeMemory(int forTag, Strategy strategy) {
        for (int i = 0; i < strategyMemory.length; i++) {
            if (opponentMemory[i].tag == forTag) {
                strategyMemory[i] = strategy;
            }
        }
    }

    public void initializeMemoryHiLow(int forTag) {
        for (int i = 0; i < strategyMemory.length; i++) {
            if (opponentMemory[i].tag == forTag) {
                if (randomIs()) {
                    strategyMemory[i] = Strategy.LOW_STRATEGY;
                } else {
                    strategyMemory[i] = Strategy.HIGH_STRATEGY;
                }
            }
        }
    }

    public void calculateMemoryCounts() {
        intraPlayers = 0;
        intraLowPlayers = 0;
        intraMediumPlayers = 0;
        intraHighPlayers = 0;
        interPlayers = 0;
        interLowPlayers = 0;
        interMediumPlayers = 0;
        interHighPlayers = 0;
        for (int i = 0; i < strategyMemory.length; i++) {
            if (opponentMemory[i].tag == this.tag) {
                intraPlayers++;
                if (strategyMemory[i] == Strategy.LOW_STRATEGY) {
                    intraLowPlayers++;
                } else if (strategyMemory[i] == Strategy.MEDIUM_STRATEGY) {
                    intraMediumPlayers++;
                } else if (strategyMemory[i] == Strategy.HIGH_STRATEGY) {
                    intraHighPlayers++;
                } else
                    throw new RuntimeException("Internal state errror in BargainerTagged.");
            } else {
                interPlayers++;
                if (strategyMemory[i] == Strategy.LOW_STRATEGY) {
                    interLowPlayers++;
                } else if (strategyMemory[i] == Strategy.MEDIUM_STRATEGY) {
                    interMediumPlayers++;
                } else if (strategyMemory[i] == Strategy.HIGH_STRATEGY) {
                    interHighPlayers++;
                } else
                    throw new RuntimeException("Internal state errror in BargainerTagged.");
            }
        }
    }

    public Strategy calculateNextStrategy(Bargainer agent) {
        calculateMemoryCounts();
        if (((BargainerTagged) agent).tag == this.tag) {
            float lowPlayWinEstimate = Strategy.LOW_STRATEGY.getDemand() * intraPlayers;
            float mediumPlayWinEstimate = Strategy.MEDIUM_STRATEGY.getDemand() * (intraMediumPlayers + intraLowPlayers);
            float highPlayWinEstimate = Strategy.HIGH_STRATEGY.getDemand() * (intraLowPlayers);
            return calculateBestStrategy(lowPlayWinEstimate, mediumPlayWinEstimate, highPlayWinEstimate);
        } else {
            float lowPlayWinEstimate = Strategy.LOW_STRATEGY.getDemand() * interPlayers;
            float mediumPlayWinEstimate = Strategy.MEDIUM_STRATEGY.getDemand() * (interMediumPlayers + interLowPlayers);
            float highPlayWinEstimate = Strategy.HIGH_STRATEGY.getDemand() * (interLowPlayers);
            return calculateBestStrategy(lowPlayWinEstimate, mediumPlayWinEstimate, highPlayWinEstimate);
        }
    }

    public void setMemorySize(int size) {
        opponentMemory = new BargainerTagged[size];
        super.setMemorySize(size);
    }

    public void addMemory(Bargainer a, Strategy s) {
        opponentMemory[oldestMemory] = (BargainerTagged) a;
        super.addMemory(a, s);
    }

    public int countIntra() {
        return intraPlayers;
    }

    public int countInter() {
        return interPlayers;
    }

    public int countInterLow() {
        return interLowPlayers;
    }

    public int countInterMedium() {
        return interMediumPlayers;
    }

    public int countInterHigh() {
        return interHighPlayers;
    }

    public Color getColor() {
        if (tag == LIGHT_TAG) {
            return Color.lightGray;
        } else {
            return Color.darkGray;
        }
    }
}
