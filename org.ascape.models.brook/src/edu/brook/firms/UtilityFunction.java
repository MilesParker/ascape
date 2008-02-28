/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.firms;

import org.ascape.util.Function;

/**
 * The Class UtilityFunction.
 */
public class UtilityFunction extends Function {

    /**
     * 
     */
    private static final long serialVersionUID = -6110885231623948819L;

    /**
     * Preference for income (inversely, for leisure).
     */
    protected double theta;

    //Economy of scale factor. (Firm productivity per employee improvement as a function of firm size.)
    /**
     * The sigma.
     */
    private static double sigma = 1;

    //Exponent for economy of scale factor in firm output equation.
    /**
     * The beta.
     */
    private static int beta = 2;

    /**
     * The base size.
     */
    protected double baseSize;

    /**
     * The base effort.
     */
    protected double baseEffort;

    /**
     * Output as a function of input (effort.)
     * 
     * @param e
     *            the e
     * @return the double
     */
    public static double output(double e) {
        return e + (sigma * Math.pow(e, beta));
    }

    /* (non-Javadoc)
     * @see org.ascape.util.Function#solveFor(double)
     */
    public double solveFor(double e) {
        return Math.pow((output(baseEffort + e) / (baseSize + 1)), theta)
            * Math.pow(1 - e, 1 - theta);
    }
}
