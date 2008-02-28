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
public class TradeT4 extends TradeT2 {

    /**
     * 
     */
    private static final long serialVersionUID = 7503805673932392197L;
    private Exchange2 exchange;
    private int response;
    private NegotiatingAgent self;
    private NegotiatingAgent other;
    private NegotiatingAgent temp;
    SpiceAgent debugo;
    private float price;
    private float[] offerList = new float[Exchange2.MAXNUMOFFERS + 1];

    public void execute(Agent agent) {
        List neighbors = ((CellOccupant) agent).findNeighborsOnHost();
        //pick a random series from the set of series that match the number of neighbors
        //(Assumes von Neumann neighborhood, otherwise creates error)
        int[] series = Utility.uniqueSeries[neighbors.size()][randomToLimit(Utility.uniqueSeries[neighbors.size()].length)];
        big_loop: for (int i = 0; i < series.length; i++) {
            int tradeIteration = 0;
            self = (NegotiatingAgent) agent;
            other = (NegotiatingAgent) neighbors.get(i);
            exchange = new Exchange2(self, other);
            response = ((NegotiatingAgent) neighbors.get(i)).offerResponse(exchange);
            if (response == Exchange2.ACCEPT) {
//System.out.println("1 selfMRS = " + exchange.selfExAnteMRS + ", pMRS = " + exchange.partnerExAnteMRS + ", isBS = " + exchange.isBuyingSugar() + ", price = " + exchange.price + ", response = " + response);
                executeTrade(exchange);
//System.out.println("1    ACCEPTED");
                continue big_loop;
            } else if (response == Exchange2.REJECT) {
//System.out.println("1    REJECTED");
                continue big_loop; // offer rejected outright
            }
            while (response == Exchange2.COUNTER) { // counter offer made
                price = other.counterOffer(exchange);
//System.out.print("+ CounterOffer returned " + price + ", exchange.price = " + exchange.price);
                offerList[tradeIteration] = exchange.price;
//System.out.println(" offerList[" + tradeIteration + "] = " + exchange.price);
                tradeIteration++;
                temp = self;
                self = other;
                other = temp;
                exchange = new Exchange2(self, other, price, offerList, tradeIteration);
                response = ((NegotiatingAgent) neighbors.get(i)).offerResponse(exchange);
//System.out.println("2 response = " + response + " iteration = " + tradeIteration);
//int k = tradeIteration-1;
//System.out.println("2   selfMRS = " + exchange.selfExAnteMRS + ", pMRS = " + exchange.partnerExAnteMRS + ", isBS = " + exchange.isBuyingSugar() + ", price = " + exchange.price + ", last offer[" + k + "] = " + offerList[k] + ", response = " + response + ", cntrofr = " + other.counterOffer (exchange));
            }
            if (response == Exchange2.ACCEPT) {
                executeTrade(exchange);
//System.out.println("2      ACCEPTED");
//for (int k = 0; k < tradeIteration; k++) {
//System.out.println("   " + exchange.offerList[k]);
//}
//System.out.println("final price = " + exchange.price);
                continue big_loop;
            } else {
//System.out.println("2      REJECTED");
                continue big_loop; // final offer rejected outright
            }
        }
    }
}
