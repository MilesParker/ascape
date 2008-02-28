/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.ant;


import java.io.Serializable;

import org.ascape.util.sweep.SweepDimension;


/**
 * The Class SweepDimensionElement.
 */
public class SweepDimensionElement implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The parameter.
     */
    String parameter;
    
    /**
     * The start value.
     */
    String startValue;
    
    /**
     * The end value.
     */
    String endValue;
    
    /**
     * The increment.
     */
    String increment;

    /**
     * Sets the parameter.
     * 
     * @param parameter
     *            the new parameter
     */
    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    /**
     * Sets the end value.
     * 
     * @param endValue
     *            the new end value
     */
    public void setEndValue(String endValue) {
        this.endValue = endValue;
    }

    /**
     * Sets the increment.
     * 
     * @param increment
     *            the new increment
     */
    public void setIncrement(String increment) {
        this.increment = increment;
    }

    /**
     * Sets the start value.
     * 
     * @param startValue
     *            the new start value
     */
    public void setStartValue(String startValue) {
        this.startValue = startValue;
    }

    /**
     * As dimension.
     * 
     * @param obj
     *            the obj
     * @return the sweep dimension
     */
    public SweepDimension asDimension(Object obj) {
        return new SweepDimension(obj, parameter, startValue, endValue, increment);
    }
}
