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


/**
 * An object to manage the data associated with a potential
 * or actual exchange.
 *
 * @author Alan Lockard
 * @version 1.0
 **/
public class Exchange {

    /**
     * The agent who is trading
     */
    public SpiceAgent self;
    /**
     * The agent with whom our self agent is trading
     */
    public SpiceAgent partner;
    /**
     * Price of spice - units of spice / units of sugar
     */
    public float price;
    /**
     * natural log of the price
     */
    public float logPrice;
    /**
     * amount of sugar that would change hands
     */
    protected float sugarQuantity;
    /**
     * amount of spice that would change hands
     */
    protected float spiceQuantity;
    /**
     * Marginal Rate of Substitution of agent, prior to trading
     */
    public float selfExAnteMRS;
    /**
     * Marginal Rate of Substitution of agent's trading partner,
     * prior to trading
     */
    public float partnerExAnteMRS;
    /**
     * anticipated Marginal Rate of Substitution of agent, after trading
     */
    public float selfExPostMRS;
    /**
     * anticipated Marginal Rate of Substitution of agent's trading partner,
     * after trading
     */
    public float partnerExPostMRS;
    /**
     * increase in agent's utility resulting from this trade
     */
    public float selfGainsFromTrade;
    /**
     * increase in agent's trading partner's utility resulting from this trade
     */
    public float partnerGainsFromTrade;
    /**
     * true if agent is giving spice to get sugar
     * false if agent is giving sugar to get spice
     */
    protected boolean buyingSugar;
    /**
     * How much sugar agent will have if this trade goes through
     */
    public float selfExPostSugar;
    /**
     * How much spice agent will have if this trade goes through
     */
    public float selfExPostSpice;
    /**
     * How much sugar agent's trading partner will have
     * if this trade goes through
     */
    public float partnerExPostSugar;
    /**
     * How much spice agent's trading partner will have
     * if this trade goes through
     */
    public float partnerExPostSpice;
    /**
     * Flag indicating illegal trades, such as paying more than you have
     */
//    public boolean tradeIsValid;

    /**
     * Constructor for this (Exchange) class
     */
    public Exchange(SpiceAgent self, SpiceAgent partner) {
        this.self = self;
        this.partner = partner;
        selfExAnteMRS = self.calculateMRS();
        partnerExAnteMRS = partner.calculateMRS();
        price = self.makeOffer(partner);
        logPrice = (float) Math.log(price);
        if (price > 1) {
            sugarQuantity = 1.0f;
            spiceQuantity = price;
        } else {
            sugarQuantity = price;
            spiceQuantity = 1.0f;
        }
        buyingSugar = isBuyingSugar();
        if (isBuyingSugar()) {
            selfExPostSugar = self.getSugarStock() + sugarQuantity;
            selfExPostSpice = self.getSpiceStock() - spiceQuantity;
            partnerExPostSugar = partner.getSugarStock() - sugarQuantity;
            partnerExPostSpice = partner.getSpiceStock() + spiceQuantity;
        } else {
            selfExPostSugar = self.getSugarStock() - sugarQuantity;
            selfExPostSpice = self.getSpiceStock() + spiceQuantity;
            partnerExPostSugar = partner.getSugarStock() + sugarQuantity;
            partnerExPostSpice = partner.getSpiceStock() - spiceQuantity;
        }
        selfGainsFromTrade = self.calculateUtility(selfExPostSugar, selfExPostSpice)
            - self.calculateUtility();
        partnerGainsFromTrade = partner.calculateUtility(partnerExPostSugar, partnerExPostSpice)
            - partner.calculateUtility();
        selfExPostMRS = self.calculateMRS(selfExPostSugar, selfExPostSpice);
        partnerExPostMRS = partner.calculateMRS(partnerExPostSugar, partnerExPostSpice);
    }

    public Exchange(SpiceAgent self, SpiceAgent partner, float setPrice) {
        this.self = self;
        this.partner = partner;
        selfExAnteMRS = self.calculateMRS();
        partnerExAnteMRS = partner.calculateMRS();
        price = setPrice;
        logPrice = (float) Math.log(price);
        if (price > 1) {
            sugarQuantity = 1.0f;
            spiceQuantity = price;
        } else {
            sugarQuantity = price;
            spiceQuantity = 1.0f;
        }
        buyingSugar = isBuyingSugar();
        if (isBuyingSugar()) {
            selfExPostSugar = self.getSugarStock() + sugarQuantity;
            selfExPostSpice = self.getSpiceStock() - spiceQuantity;
            partnerExPostSugar = partner.getSugarStock() - sugarQuantity;
            partnerExPostSpice = partner.getSpiceStock() + spiceQuantity;
        } else {
            selfExPostSugar = self.getSugarStock() - sugarQuantity;
            selfExPostSpice = self.getSpiceStock() + spiceQuantity;
            partnerExPostSugar = partner.getSugarStock() + sugarQuantity;
            partnerExPostSpice = partner.getSpiceStock() - spiceQuantity;
        }
        selfGainsFromTrade = self.calculateUtility(selfExPostSugar, selfExPostSpice)
            - self.calculateUtility();
        partnerGainsFromTrade = partner.calculateUtility(partnerExPostSugar, partnerExPostSpice)
            - partner.calculateUtility();
        selfExPostMRS = self.calculateMRS(selfExPostSugar, selfExPostSpice);
        partnerExPostMRS = partner.calculateMRS(partnerExPostSugar, partnerExPostSpice);
    }

    public boolean isBuyingSugar() {
//System.out.println(selfExAnteMRS + " > " + partnerExAnteMRS + " = " + (selfExAnteMRS > partnerExAnteMRS));
        return (selfExAnteMRS > partnerExAnteMRS);
    }

    public boolean tradeIsValid() {
//	return (selfExPostSugar > 0) & (selfExPostSpice > 0) &
//                 (partnerExPostSugar > 0) & (partnerExPostSpice > 0);
//System.out.println("selfGains = " + selfGainsFromTrade + ", partnerGains = " + partnerGainsFromTrade + ", this step = " + ((selfGainsFromTrade >= 0) & (partnerGainsFromTrade >= 0)));
//System.out.println("  sugarQuantity = " + sugarQuantity + ", spiceQuantity = " + spiceQuantity + ", this step = " + ((sugarQuantity > 0) & (spiceQuantity > 0)));
        if (!((selfGainsFromTrade >= 0) & (partnerGainsFromTrade >= 0))) {
            return false;
        } else if (!((sugarQuantity > 0) & (spiceQuantity > 0))) {
            return false;
        } else {
            return true;
        }
    }
}
