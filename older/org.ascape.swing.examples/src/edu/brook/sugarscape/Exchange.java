/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
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
