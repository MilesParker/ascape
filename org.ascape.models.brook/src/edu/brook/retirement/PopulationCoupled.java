/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.retirement;

//Temporary for JDK 1.1 compatibility
//import com.sun.java.util.collections.*;

import java.util.ArrayList;
import java.util.List;

import org.ascape.model.space.Array2D;
import org.ascape.model.space.Coordinate2DDiscrete;


class PersonCoupled extends Person {

    public void initialize() {
        super.initialize();
        //age = ((Population) getScape()).getAge(((Coordinate2DDiscrete) getCoordinate()).getYValue());
        float strategyChance = getRandom().nextFloat();
        if (strategyChance < ((Population) getScape()).getFractionRational()) {
            if (((org.ascape.model.space.Coordinate2DDiscrete) getCoordinate()).getXValue() < ((PopulationCoupled) scape).getSubPopulationBorder()) {
                strategy = RATIONAL;
            } else {
                strategy = IMITATOR;
            }
        } else if (strategyChance >= (1.0 - ((Population) getScape()).getFractionRandom())) {
            strategy = RANDOM;
        } else {
            strategy = IMITATOR;
        }
    }

    public void calculateNetwork() {
        int netSize = randomInRange(((Population) getScape()).getMinNetworkSize(), ((Population) getScape()).getMaxNetworkSize());
        List network = new ArrayList(netSize);
        int coupledSize = (int) (netSize * ((PopulationCoupled) getScape()).getFractionCoupled());
        int r = randomInRange(0, ((Population) scape.getSpace()).getMaxNetworkAgeDifference());
        int row = ((Coordinate2DDiscrete) getCoordinate()).getYValue();
        int p = ((PopulationCoupled) scape).getSubPopulationBorder();
        if (((Coordinate2DDiscrete) getCoordinate()).getXValue() > p) {
            for (int i = 0; i < coupledSize; i++) {
                network.add(((Array2D) scape.getSpace()).findRandom(this, 0, row - r, p, r * 2 + 1));
            }
            for (int i = coupledSize; i < network.size(); i++) {
                network.add(((Array2D) scape.getSpace()).findRandom(this, p, row - r, p, r * 2 + 1));
            }
        } else {
            for (int i = 0; i < coupledSize; i++) {
                network.add(((Array2D) scape.getSpace()).findRandom(this, p, row - r, p, r * 2 + 1));
            }
            for (int i = coupledSize; i < network.size(); i++) {
                network.add(((Array2D) scape.getSpace()).findRandom(this, 0, row - r, p, r * 2 + 1));
            }
        }
        setNetwork(network);
    }
}

public class PopulationCoupled extends Population {

    private double fractionCoupled = .05;

    public PopulationCoupled() {
        super();
        getRules().clear();
        setPrototypeAgent(new PersonCoupled());
    }

    /**
     * Returns the location of the border for the two subpopulations, also the size
     * of the population for even size populations.
     * The cell at this index is considered to be in the high index subpopulation.
     */
    public int getSubPopulationBorder() {
        return ((Array2D) getSpace()).getXSize() / 2;
    }

    public void setFractionCoupled(double fractionCoupled) {
        if (fractionCoupled <= 1.0) {
            this.fractionCoupled = fractionCoupled;
        } else {
            throw new IllegalArgumentException("Tried to set fraction coupled greater than 1.0");
        }
    }

    public double getFractionCoupled() {
        return fractionCoupled;
    }
}
