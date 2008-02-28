/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.classes;

import java.awt.Color;

import org.ascape.model.Agent;



public class BargainerTagged extends Bargainer {

    /**
     * 
     */
    private static final long serialVersionUID = 6934825613101465571L;

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
