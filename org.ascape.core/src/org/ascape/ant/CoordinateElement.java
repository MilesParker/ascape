/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */
package org.ascape.ant;

import java.io.Serializable;

import org.apache.tools.ant.BuildException;
import org.ascape.model.space.Coordinate;
import org.ascape.model.space.Coordinate1DDiscrete;
import org.ascape.model.space.Coordinate2DDiscrete;

/*
 * User: Miles Parker
 * Date: Jan 21, 2005
 * Time: 4:17:25 PM
 */

/**
 * The Class CoordinateElement.
 */
public class CoordinateElement implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The x value.
     */
    int xValue = Integer.MIN_VALUE;
    
    /**
     * The y value.
     */
    int yValue = Integer.MIN_VALUE;

    /**
     * Sets the x.
     * 
     * @param xValue
     *            the new x
     */
    public void setX(int xValue) {
        this.xValue = xValue;
    }

    /**
     * Sets the value.
     * 
     * @param xValue
     *            the new value
     */
    public void setValue(int xValue) {
        this.xValue = xValue;
    }

    /**
     * Sets the y.
     * 
     * @param yValue
     *            the new y
     */
    public void setY(int yValue) {
        this.yValue = yValue;
    }

    /**
     * Determine coordinate.
     * 
     * @return the coordinate
     */
    public Coordinate determineCoordinate() {
        if (xValue == Integer.MIN_VALUE) {
            throw new BuildException("No value defined for 1st coordinate dimension. (Use x or value.)");
        }
        if (yValue == Integer.MIN_VALUE) {
            return new Coordinate1DDiscrete(xValue);
        } else {
            return new Coordinate2DDiscrete(xValue, yValue);
        }
    }
}
