/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.classes;

import org.ascape.model.Agent;
import org.ascape.model.CellOccupant;
import org.ascape.util.Utility;

public class Bargainer extends CellOccupant {

    /**
     * 
     */
    private static final long serialVersionUID = 2002542863366259012L;

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
