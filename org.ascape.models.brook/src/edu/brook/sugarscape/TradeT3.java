/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.sugarscape;

import java.util.List;

import org.ascape.model.Agent;
import org.ascape.model.CellOccupant;



/**
 * This trade rule solicits offers from all partners, takes best
 * offer, and counteroffers remaining partners the chance to
 * trade on those same, most favorable terms
 *
 * @author Alan Lockard
 * @version 1.0
 */
public class TradeT3 extends TradeT2 {

    /**
     * 
     */
    private static final long serialVersionUID = 1130443555316578860L;
    private Exchange2[] exchanges = new Exchange2[4]; // max number Von Neuman neighbors
    private Exchange2 exchange;

    public void execute(Agent agent) {
        List neighbors = ((CellOccupant) agent).findNeighborsOnHost();
        // If only 1 neighbor, life is easy
        if (neighbors.size() == 1) {
            exchange = new Exchange2(((SpiceAgent) agent), ((SpiceAgent) neighbors));
            if (((SpiceAgent) neighbors.get(0)).acceptOffer(exchange)) {
                executeTrade(exchange);
            }
        } else { // Find best offer. Note that order is irrelevant, needn't be randomized.
            int imax = -1;
            float maxDif = 0;
            float transDif;
            for (int i = 0; i < neighbors.size(); i++) {
                exchanges[i] = new Exchange2(((SpiceAgent) agent), ((SpiceAgent) neighbors.get(i)));
                transDif = (float) Math.abs((exchanges[i]).logPrice - Math.log((exchanges[i]).selfExAnteMRS));
                if (transDif > maxDif) {
                    maxDif = transDif;
                    imax = i;
                }
            }
            if (imax >= 0) { // At least one beneficial trade found
// Deal with trading partner offering best price
                if (((SpiceAgent) neighbors.get(imax)).acceptOffer(exchanges[imax])) {
                    executeTrade(exchanges[imax]);
                }
                // give trading partners a chance to match the best price
                for (int i = 0; i < neighbors.size(); i++) {
                    if (i == imax) continue;
                    exchange = new Exchange2((SpiceAgent) agent, (SpiceAgent) neighbors.get(i), (exchanges[imax]).price);
                    if (((SpiceAgent) neighbors.get(i)).acceptOffer(exchange)) {
                        executeTrade(exchange);
                    }
                }
            }
        }
    }
}
