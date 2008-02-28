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
import org.ascape.util.Utility;



/**
 * This trade rule orders neighbors randomly, and trades once
 * with each of the neighbors. Methods for making
 * and accepting offers are attributes of the trading agents, themselves.
 *
 * @author Alan Lockard
 * @version 1.0
 */
public class TradeT2 extends TradeT {

    /**
     * 
     */
    private static final long serialVersionUID = -6862251579748082480L;
    private Exchange2 exchange;

    public void execute(Agent agent) {
        List neighbors = ((CellOccupant) agent).findNeighborsOnHost();
        //pick a random series from the set of series that match the number of neighbors
        //(Assumes von Neumann neighborhood, otherwise creates error)
        int[] series = Utility.uniqueSeries[neighbors.size()][randomToLimit(Utility.uniqueSeries[neighbors.size()].length)];
        for (int i = 0; i < series.length; i++) {
            exchange = new Exchange2(((SpiceAgent) agent), ((SpiceAgent) neighbors.get(i)));
            if (((SpiceAgent) neighbors.get(i)).acceptOffer(exchange)) {
                executeTrade(exchange);
            }
        }
    }
}
