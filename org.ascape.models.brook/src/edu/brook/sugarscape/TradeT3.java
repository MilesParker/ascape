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



/**
 * This trade rule solicits offers from all partners, takes best
 * offer, and counteroffers remaining partners the chance to
 * trade on those same, most favorable terms
 *
 * @author Alan Lockard
 * @version 1.0
 */
public class TradeT3 extends TradeT2 {

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
