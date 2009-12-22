/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */
package edu.brook.manual.model3;

import org.ascape.model.Agent;
import org.ascape.model.CellOccupant;
import org.ascape.util.vis.ColorFeature;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.swt.graphics.Color;

public class CoordinationGamePlayer extends CellOccupant implements IColorProvider {

    /**
     * 
     */
    private static final long serialVersionUID = -8028798716898678390L;

    // cgplayer variables
    protected Color myColor;

    protected int totalScore;

    protected int[] recentPlays;

    protected Color[] recentPartners;

    protected int count = 0;

    protected int totalreds;

    protected int totalblues;

    // agent initialization method
    public void initialize() {
        doRandom();
        recentPlays = new int[5];
        recentPartners = new Color[5];
    }

    // add rules to the scape
    public void scapeCreated() {
        getScape().addInitialRule(MOVE_RANDOM_LOCATION_RULE);
        getScape().addRule(RANDOM_WALK_RULE);
        getScape().addRule(UPDATE_RULE);
        getScape().addRule(PLAY_RANDOM_NEIGHBOR_RULE);

    }

    // best reply rule
    public void update() {
        count++;
        if (count > recentPlays.length) {

            if (randomInRange(0, 100) >= ((CoordinationGame) getRoot()).error) {
                int redScore;
                int blueScore;
                blueScore = totalblues * ((CoordinationGame) getRoot()).getBlueScore();

                redScore = totalreds * ((CoordinationGame) getRoot()).getRedScore();
                if (redScore > blueScore) {
                    myColor = ColorFeature.RED;
                } else if (blueScore > redScore) {
                    myColor = ColorFeature.BLUE;
                } else {
                    doRandom();
                }

            } else {
                doRandom();
            }
        }
    }

    public void doRandom() {
        if (randomInRange(0, 1) > 0) {
            myColor = ColorFeature.RED;
        } else {
            myColor = ColorFeature.BLUE;
        }
    }

    // play rule
    public void play(Agent partner) {
        int score;
        if ((((CoordinationGamePlayer) partner).getBackground(partner) == myColor) && (myColor == ColorFeature.BLUE)) {
            score = ((CoordinationGame) getRoot()).getBlueScore();
        } else if ((((CoordinationGamePlayer) partner).getBackground(partner) == myColor)
            && (myColor == ColorFeature.RED)) {
            score = ((CoordinationGame) getRoot()).getRedScore();
        } else {
            score = 0;
        }
        count = count + 1;
        updateRecentPlays(score);
        updateRecentPartners(((CoordinationGamePlayer) partner).getBackground(partner));
    }

    // keep track of recent partners
    public void updateRecentPartners(Color ncolor) {
        totalreds = 0;
        totalblues = 0;
        for (int i = 0; i < (recentPartners.length - 1); i++) {
            recentPartners[i] = recentPartners[i + 1];
            if (recentPartners[i] == ColorFeature.RED) {
                totalreds = totalreds + 1;
            } else if (recentPartners[i] == ColorFeature.BLUE) {
                totalblues = totalblues + 1;
            }
        }
        recentPartners[recentPartners.length - 1] = ncolor;
        if (ncolor == ColorFeature.RED) {
            totalreds = totalreds + 1;
        }
        if (ncolor == ColorFeature.BLUE) {
            totalblues = totalblues + 1;
        }
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
    public Color getBackground(Object object) {
        return myColor;
    }

    public Color getForeground(Object object) {
        return getBackground(object);
    }

    public void setColor(Color ncolor) {
        myColor = ncolor;
    }

    public int getRunningTotal() {
        return (totalScore * 10);
    }

}
