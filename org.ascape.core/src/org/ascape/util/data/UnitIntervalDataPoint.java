/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.data;

import java.io.Serializable;

/**
 * An abstract class providing some value 0..1 based on the state of the instance provided.
 *
 * @author Miles Parker
 * @version 1.2
 * @history 1.2 7/8/99 added more constructor options (including name) to simplify subclassing
 * @since 1.0
 */
public abstract class UnitIntervalDataPoint implements DataPoint, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * The name of the stat.
     */
    private String name = "Unnamed";

    /**
     * Constructs a UnitIntervalDataPoint.
     */
    public UnitIntervalDataPoint() {
    }

    /**
     * Constructs a UnitIntervalDataPoint.
     * @param name the name of this draw feature
     */
    public UnitIntervalDataPoint(String name) {
        this.name = name;
    }

    /**
     * Returns a value that is supposed to be between 0..1 based on the state of the object.
     * @param object the object to get a value from.
     */
    public abstract double getValue(Object object);

    /**
     * Returns the name of this unit interval data point.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a value guaranteed to be within 0..1 based on the state of the object as defined by getValue.
     * If value returned by getValue is less, return 0, if greater, returns 1.
     * @param object the object to get a value from.
     */
    public double getBracketedValue(Object object) {
        return Math.min(Math.max(getValue(object), 0.0), 1.0);
    }

    /**
     * Returns a value guaranteed to be within 0..1 based on the state of the object as defined by getValue.
     * @throws org.ascape.util.data.ValueNotInRangeException if value is not within limit
     * @param object the object to get a value from.
     */
    public double getAssertedValue(Object object) throws ValueNotInRangeException {
        double value = getValue(object);
        if ((value < 0.0) || (value > 1.0)) {
            throw new ValueNotInRangeException("Unit interval value outside of asserted range (0.0 - 1.0): " + value);
        }
        return value;
    }
}
