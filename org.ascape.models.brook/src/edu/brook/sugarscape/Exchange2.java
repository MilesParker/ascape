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
public class Exchange2 extends Exchange {

    /**
     * Quantity of sugar that would leave agent with MRS = 1
     * at the offered price
     */
    private float selfOptSpiceQuantity;
    /**
     * Quantity of sugar that would leave partner with MRS = 1
     * at the offered price
     */
    private float partnerOptSpiceQuantity;
    /**
     * potential value of offerResponse, indicates offer is accepted
     */
    public static final int ACCEPT = 0;
    /**
     * potential value of offerResponse, indicates offer is rejected
     */
    public static final int REJECT = 1;
    /**
     * potential value of offerResponse, indicates offer is rejected, but counter
     * offer is made.
     */
    public static final int COUNTER = 2;
    /**
     * Indicates if offered exchange is reject, accepted, or countered
     */
    public int offerResponse;
    /**
     * Keeps tracks of how many offers and counteroffers have been made
     */
    public int iteration;
    /**
     * Maximum number of offers and counter offers in a negotiation
     */
    public static final int MAXNUMOFFERS = 20;
    /**
     * Holds list of offers and counter offers
     */
    public float offerList[] = new float[MAXNUMOFFERS + 1];

    /**
     * Constructor for this (Exchange2) class
     */
    public Exchange2(SpiceAgent self, SpiceAgent partner) {
        super(self, partner);
        selfOptSpiceQuantity = Math.abs(optSpiceQuantity(self));
        partnerOptSpiceQuantity = Math.abs(optSpiceQuantity(partner));
//        spiceQuantity = Math.min(selfOptSpiceQuantity, partnerOptSpiceQuantity);
//	sugarQuantity = spiceQuantity / price;
        calculateQuantities();
        iteration = 0;
    }

    public Exchange2(SpiceAgent self, SpiceAgent partner, float price) {
        super(self, partner); // don't really keep anything from super class, but compiler wants this
        this.self = self;
        this.partner = partner;
        this.price = price;  // This is what's different about Exchange3
        logPrice = (float) Math.log(price);
        iteration = 0;
        selfExAnteMRS = self.calculateMRS();
        partnerExAnteMRS = partner.calculateMRS();
        selfOptSpiceQuantity = Math.abs(optSpiceQuantity(self));
        partnerOptSpiceQuantity = Math.abs(optSpiceQuantity(partner));
//        spiceQuantity = Math.min(selfOptSpiceQuantity, partnerOptSpiceQuantity);
//	sugarQuantity = spiceQuantity / price;
        calculateQuantities();
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
//	tradeIsValid = (selfExPostSugar > 0) & (selfExPostSpice > 0) &
//                       (partnerExPostSugar > 0) & (partnerExPostSpice > 0);
    }

    public Exchange2(SpiceAgent self, SpiceAgent partner, float price, float[] list, int count) {
        super(self, partner); // don't really keep anything from super class, but compiler wants this
        this.self = self;
        this.partner = partner;
        this.price = price;  // This is what's different about Exchange3
        logPrice = (float) Math.log(price);
        this.iteration = count;
        for (int i = 0; i < count; i++) {
            offerList[i] = list[i];
        }
        selfExAnteMRS = self.calculateMRS();
        partnerExAnteMRS = partner.calculateMRS();
        selfOptSpiceQuantity = Math.abs(optSpiceQuantity(self));
        partnerOptSpiceQuantity = Math.abs(optSpiceQuantity(partner));
        calculateQuantities();
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
//	tradeIsValid = (selfExPostSugar > 0) & (selfExPostSpice > 0) &
//                       (partnerExPostSugar > 0) & (partnerExPostSpice > 0);
    }

    /**
     * Amount of spice traded that would cause agent's MRS to become = 1
     * at the offered price
     */
    private float optSpiceQuantity(SpiceAgent agent) {
        float mr = (float) agent.getSugarMetabolism() / agent.getSpiceMetabolism();
        return ((mr * agent.spice.getStock()) - agent.sugar.getStock()) / (1.0f + (price * mr));
    }

    private void calculateQuantities() {
        // Determines the maximum beneficial amount of goods traded at current price.
        // Insures that traders don't trade more than they have
        float selfSpiceReqd = self.getSpiceStock() - self.getSpiceMetabolism();
        float selfSugarReqd = self.getSugarStock() - self.getSugarMetabolism();
        float partnerSpiceReqd = partner.getSpiceStock() - partner.getSpiceMetabolism();
        float partnerSugarReqd = partner.getSugarStock() - partner.getSugarMetabolism();
        float minSpice = minOfFour(selfOptSpiceQuantity, partnerOptSpiceQuantity, selfSpiceReqd, partnerSpiceReqd);
        float minSugar = minOfFour(selfOptSpiceQuantity / price, partnerOptSpiceQuantity / price, selfSugarReqd, partnerSugarReqd);
        if (minSpice <= minSugar * price) { // availability of spice is limiting factor
            spiceQuantity = minSpice;
            sugarQuantity = spiceQuantity / price;
        } else {
            sugarQuantity = minSugar;
            spiceQuantity = sugarQuantity * price;
        }
        // sugarQuantity and spiceQuantity are set as side effects, so nothing is returned
    }


/*
	float minSugar;
	// Set sugar and spice as side effects. These values will then be
	// overridden if reqd.
        spiceQuantity = Math.min(selfOptSpiceQuantity, partnerOptSpiceQuantity);
	sugarQuantity = spiceQuantity / price;
	if (isBuyingSugar()) {
	    spiceReqd = self.getSpiceStock() - self.getSpiceMetabolism();
	    sugarReqd = partner.getSugarStock() - partner.getSugarMetabolism();
	}
	else {
	    spiceReqd = partner.getSpiceStock() - partner.getSpiceMetabolism();
	    sugarReqd = self.getSugarStock() - self.getSugarMetabolism();
	}
	sugarRatio = sugarQuantity / sugarReqd;
	spiceRatio = spiceQuantity / spiceReqd;
	if (sugarRatio < 1) {
	    if (spiceRatio < sugarRatio) {
		spiceQuantity = spiceReqd;
		sugarQuantity = spiceQuantity / price;
	    }
	    else {
		sugarQuantity = sugarReqd;
		spiceQuantity = sugarQuantity * price;
	    }
	}
	else if (spiceRatio < 1) {
	    spiceQuantity = spiceReqd;
	    sugarQuantity = spiceQuantity / price;
	}
    // sugarQuantity and spiceQuantity are set as side effects, so nothing is returned
    }
*/


    public void setOfferList(float price, int index) {
        offerList[index] = price;
    }

    public float getOfferList(int index) {
        return offerList[index];
    }

    private float minOfFour(float a, float b, float c, float d) {
        return Math.min(Math.min(a, b), Math.min(c, d));
    }
}

