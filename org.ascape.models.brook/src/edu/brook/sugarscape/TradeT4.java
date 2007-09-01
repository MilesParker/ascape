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
