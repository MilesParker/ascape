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
 * _Sugarscape_
 * See _Growing Artificial Societies: Social Science from the Ground Up_
 * Brookings Institution Press, The MIT Press
 * Joshua Epstein
 * jepstein@brook.edu
 * Robert Axtell
 * raxtell@brook.edu
 *
 * Miles Parker
 * mparker@brook.edu

 * http://www.brook.edu/es/dynamics/models/ascape
 * The Brookings Institution
 * Washington, D.C.
 *
 * Alan Lockard
 * alockard@gmu.edu
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
