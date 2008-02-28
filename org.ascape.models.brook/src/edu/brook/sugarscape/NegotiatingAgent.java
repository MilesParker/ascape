/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.sugarscape;


/**
 * A basic sugarscape citizen. Provides basic funtionality for sugarscape agents
 * as well as all desired functionality that could be included in base class without
 * compromising good design or supporting unnecessary member variables.
 * Despite the relativly large size of this class it is actually
 * quite simple; much of the code is simply getters and setters for various
 * initialization paramaters.
 *
 * @author Alan Lockard
 * alockard@gmu.edu
 * @version 1.0
 */
public class NegotiatingAgent extends SpiceAgent {

    /**
     * 
     */
    private static final long serialVersionUID = 8866499397576236724L;
    /**
     * Factor by which one would like to inflate (deflate) price
     * when buying spice (sugar)
     */
    private float negotiatingFactor = .5f;
    private float firstOfferFactor = 2.0f;
    private int maxNumOffers = 6;

    public void initialize() {
        super.initialize();
        maxNumOffers = randomInRange(2, 10); // randomize how long agent will haggle
        firstOfferFactor = 2.0f; //randomFloatInRange (1.5f, 3.5f); // use local routine to randomize
        negotiatingFactor = .5f; //randomFloatInRange (.125f, .5f);
// maybe randomize negotiating factors
    }

    public void setNegotiatingFactor(float factor) {
        this.negotiatingFactor = factor;
    }

    public float getNegotiatingFactor() {
        return negotiatingFactor;
    }

    public void setFirstOfferFactor(float factor) {
        this.firstOfferFactor = factor;
    }

    public float getFirstOfferFactor() {
        return firstOfferFactor;
    }

//    public float counterOffer (Exchange2 exchange) {
//	if (exchange.isBuyingSugar()) {
//	    return Math.min (calculateMRS(), exchange.price / negotiatingFactor);
//	}
//	else {
//	    return Math.max (calculateMRS(), exchange.price * negotiatingFactor);
//	}
//    }

    public float makeOffer(SpiceAgent partner) {
        // overrides GAS price offer in class Exchange
        // Assume they communicate enough to kow if they are buying or selling sugar
        // We will compare MRSs, but, presumably, each individual would only know his own, precisely
//System.out.println("Making offer " + calculateMRS() + " " + partner.calculateMRS() + " " + calculateMRS() / firstOfferFactor + " " + calculateMRS() * firstOfferFactor);
        if (calculateMRS() > partner.calculateMRS()) { // buying sugar
            return calculateMRS() / firstOfferFactor;
        } else {
            return calculateMRS() * firstOfferFactor;
        }
    }


    public float counterOffer(Exchange2 exchange) {
        float offer;
        float delta;
        int last;
        if (exchange.iteration > 0) {
            last = exchange.iteration - 1;
        } else {
            last = 0;
        }
        if (exchange.iteration < 1) {  // initial counter offer
// isBuyingSugar reflects attitude of self, while counterOffer comes from partner
            if (!exchange.isBuyingSugar()) { //
                offer = Math.min(calculateMRS(), exchange.price) / firstOfferFactor;
            } else {
                offer = Math.max(calculateMRS(), exchange.price) * firstOfferFactor;
            }
        } else if (exchange.iteration >= maxNumOffers - 2) { // stop haggling and make last best offer
            return calculateMRS();
        } else {
            if (exchange.isBuyingSugar()) {
                delta = exchange.offerList[last] - exchange.price;
                offer = exchange.price + (delta * negotiatingFactor);
//System.out.println("a offer = " + offer + " = " + exchange.price + " + (" + delta + " * " + negotiatingFactor + ")");
            } else {
                delta = exchange.price - exchange.offerList[last];
                offer = exchange.price - (delta * negotiatingFactor);
//System.out.println("a offer = " + offer + " = " + exchange.price + " - (" + delta + " * " + negotiatingFactor + ")");
            }
        }
//System.out.println("CO " + exchange.iteration + " " + exchange.isBuyingSugar() + " " + exchange.price + " -> " + offer + ", delta = " + delta + ", list[" + last + "] = " + exchange.offerList[last]);
        return offer;
    }

    public int offerResponse(Exchange2 exchange) {
        // If we get our dream price, take it
        if ((exchange.isBuyingSugar() & (exchange.price > calculateMRS() * firstOfferFactor) & exchange.tradeIsValid())
            | (!exchange.isBuyingSugar() & (exchange.price < calculateMRS() / firstOfferFactor) & exchange.tradeIsValid())) {
            return Exchange2.ACCEPT;
        }
        // If tired of haggling, accept any beneficial offer
        if (exchange.iteration >= maxNumOffers - 2) {
            if ((exchange.isBuyingSugar() & exchange.tradeIsValid() & exchange.price > calculateMRS())
                | (!exchange.isBuyingSugar() & exchange.tradeIsValid() & exchange.price < calculateMRS())) {
                return Exchange2.ACCEPT;
            }
        }
        // If our last, best offer has already been rejected, give up
        for (int i = exchange.iteration; i >= 0; i = i - 2) {
            if (exchange.offerList[i] == calculateMRS()) {
                return Exchange2.REJECT;
            }
        }
        // last ditch try to not forgo potential gains from trade
        if (exchange.iteration >= maxNumOffers - 1) {
            if (((!exchange.isBuyingSugar()) & exchange.tradeIsValid() & (exchange.price <= calculateMRS()))
                | ((exchange.isBuyingSugar()) & exchange.tradeIsValid() & (exchange.price >= calculateMRS()))) {
//System.out.println("returning ACCEPT = 0 c");
//System.out.print("ACCEPT c ");
                return Exchange2.ACCEPT;
            } else {
//System.out.println("returning REJECT = 0 e, it = " + exchange.iteration);
                return Exchange2.REJECT;
            }
        }
        // If no other criteria is met, make counter offer
//System.out.println("returning COUNTER = 2 f, it = " + exchange.iteration);
        return Exchange2.COUNTER;
    }

    public float randomFloatInRange(float low, float high) {
        float range = high - low; // still OK if low > high
        return low + (getRandom().nextFloat() * range);
    }
}
