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
import org.ascape.model.rule.Rule;
import org.ascape.util.Utility;


/**
 * This trade rule orders neighbors randomly, and repeatedly trades
 * with each of the neighbors until potential gains from trade are
 * exhausted, then moves on to the next neighbor. Methods for making
 * and accepting offers are attributes of the trading agents, themselves.
 *
 * @author Alan Lockard
 * @version 1.0
 */
public class TradeT extends Rule {

    /**
     * 
     */
    private static final long serialVersionUID = -2269346057039381061L;

    public TradeT() {
        super("Trade");
    }

    private Exchange exchange;
    boolean offerIsAccepted;

    public void execute(Agent agent) {
        List neighbors = ((CellOccupant) agent).findNeighborsOnHost();
        //pick a random series from the set of series that match the number of neighbors
        //(Assumes von Neumann neighborhood, otherwise creates error)
        int[] series = Utility.uniqueSeries[neighbors.size()][randomToLimit(Utility.uniqueSeries[neighbors.size()].length)];
        for (int i = 0; i < series.length; i++) {
            exchange = new Exchange(((SpiceAgent) agent), ((SpiceAgent) neighbors.get(i)));
            while (((SpiceAgent) neighbors.get(i)).acceptOffer(exchange)) {
                executeTrade(exchange);
                exchange = new Exchange(((SpiceAgent) agent), ((SpiceAgent) neighbors.get(i)));
            }
        }
    }

    protected void executeTrade(Exchange exchange) {
//System.out.println ("before self= " + (exchange.self).getSugarStock() + " " + (exchange.self).getSpiceStock() + " partner= " + (exchange.partner).getSugarStock() + " " + (exchange.partner).getSpiceStock());
        (exchange.self).setSugar(exchange.selfExPostSugar);
        (exchange.self).setSpice(exchange.selfExPostSpice);
        (exchange.partner).setSugar(exchange.partnerExPostSugar);
        (exchange.partner).setSpice(exchange.partnerExPostSpice);
//        scape.getData().getStatCollector("Trades").addValue(0.0);
//        scape.getData().getStatCollector("Price").addValue(exchange.price);
//        scape.getData().getStatCollector("Log Price").addValue(exchange.logPrice);
//        scape.getData().getStatCollector("Self Gains from Trade").addValue(exchange.selfGainsFromTrade);
//        scape.getData().getStatCollector("Partner Gains from Trade").addValue(exchange.partnerGainsFromTrade);
//System.out.println("T p=" + exchange.price + ", Qu= " + exchange.sugarQuantity
//	+ ", Qi= " + exchange.spiceQuantity + ", " + exchange.isBuyingSugar() + " gain= "
//	+ exchange.selfGainsFromTrade + " " +  exchange.partnerGainsFromTrade);
//System.out.println ("after self= " + (exchange.self).getSugarStock() + " " + (exchange.self).getSpiceStock() + " partner= " + (exchange.partner).getSugarStock() + " " + (exchange.partner).getSpiceStock());
    }

    public boolean isRandomExecution() {
        return false;
    }

    public boolean isCauseRemoval() {
        return false;
    }

}
