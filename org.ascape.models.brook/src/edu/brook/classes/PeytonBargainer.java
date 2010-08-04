/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.classes;


public class PeytonBargainer extends BargainerTagged {

    /**
     * 
     */
    private static final long serialVersionUID = -6613748910643126385L;

    public Strategy calculateNextStrategy(Bargainer agent) {
        calculateMemoryCounts();
        if (((BargainerTagged) agent).tag == this.tag) {
            //Different counts, more of a coordination style..payoff only occurs if both side agree, so L, L and L, M produce nothing..
            float lowPlayWinEstimate = Strategy.LOW_STRATEGY.getDemand() * intraHighPlayers;
            float mediumPlayWinEstimate = Strategy.MEDIUM_STRATEGY.getDemand() * intraMediumPlayers;
            float highPlayWinEstimate = Strategy.HIGH_STRATEGY.getDemand() * intraLowPlayers;
            return calculateBestStrategy(lowPlayWinEstimate, mediumPlayWinEstimate, highPlayWinEstimate);
        } else {
            //Different counts, more of a coordination style..payoff only occurs if both side agree, so L, L and L, M produce nothing..
            float lowPlayWinEstimate = Strategy.LOW_STRATEGY.getDemand() * interHighPlayers;
            float mediumPlayWinEstimate = Strategy.MEDIUM_STRATEGY.getDemand() * interMediumPlayers;
            float highPlayWinEstimate = Strategy.HIGH_STRATEGY.getDemand() * interLowPlayers;
            return calculateBestStrategy(lowPlayWinEstimate, mediumPlayWinEstimate, highPlayWinEstimate);
        }
    }

}
