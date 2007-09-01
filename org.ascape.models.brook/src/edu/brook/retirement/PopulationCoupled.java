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
 * Miles Parker
 * mparker@brook.edu
 * The Brookings Institution
 * Washington, D.C.
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
