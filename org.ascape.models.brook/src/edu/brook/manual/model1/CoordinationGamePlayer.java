/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */
package edu.brook.manual.model1;

import java.awt.Color;
import java.util.List;

import org.ascape.model.Agent;
import org.ascape.model.CellOccupant;

public class CoordinationGamePlayer extends CellOccupant {

/**
     * 
     */
    private static final long serialVersionUID = 2019376126545647516L;

    // cgplayer variables
    protected Color myColor;

    protected int totalScore;

    protected int[] recentPlays;

    protected int count = 0;

    protected int totalreds;

    protected int totalblues;

// agent initialization method
    public void initialize() {
        if (randomInRange(0, 1) > 0) {
            myColor = Color.red;
        } else {
            myColor = Color.blue;
        }
        recentPlays = new int[5];
    }

// add rules to the scape
    public void scapeCreated() {
        getScape().addInitialRule(MOVE_RANDOM_LOCATION_RULE);
        getScape().addRule(RANDOM_WALK_RULE);
        getScape().addRule(UPDATE_RULE);
        getScape().addRule(PLAY_RANDOM_NEIGHBOR_RULE);

    }

// imitation rule
    public void update() {
        count++;
        if (count > recentPlays.length) {
            int currScore = 0;
            int bestScore = this.totalScore;
            List neighbors = findNeighborsOnHost();
            for (Object neighbor : neighbors) {
                currScore = ((CoordinationGamePlayer) neighbor).totalScore;
                if (currScore > bestScore) {
                    bestScore = currScore;
                    myColor = ((CoordinationGamePlayer) neighbor).getColor();
                }
            }
        }
    }

    // play rule
    public void play(Agent partner) {
        int score;
        if ((((CoordinationGamePlayer) partner).getColor() == myColor) && (myColor == Color.blue)) {
            score = ((CoordinationGame) getRoot()).getBlueScore();
        } else if ((((CoordinationGamePlayer) partner).getColor() == myColor) && (myColor == Color.red)) {
            score = ((CoordinationGame) getRoot()).getRedScore();
        } else {
            score = 0;
        }
        count = count + 1;
        updateRecentPlays(score);
    }

    // keep track of the recent scores
    public void updateRecentPlays(int score) {
        totalScore = 0;
        for (int i = 0; i < (recentPlays.length - 1); i++) {
            recentPlays[i] = recentPlays[i + 1];
            totalScore = totalScore + recentPlays[i];
        }
        recentPlays[recentPlays.length - 1] = score;
        totalScore = totalScore + recentPlays[recentPlays.length - 1];
    }

    // cgplayer get() and set() methods
    public Color getColor() {
        return myColor;
    }

    public void setColor(Color ncolor) {
        myColor = ncolor;
    }
}
