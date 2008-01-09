/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.data;

import org.ascape.util.HasName;

/**
 * An interface for a class providing a data point for a given object.
 * A data point is just some interpretation of an object's state as a double value.
 *
 * @author Miles Parker
 * @version 1.0.1
 * @history 1.0.1 3/9/1999 renamed from ValueSource
 * @since 1.0
 */
public interface DataPoint extends HasName {

    /**
     * Returns the value of a given data point from a given object.
     * @param object the object to extract the value from.
     */
    public double getValue(Object object);

    /**
     * Returns a very short string description of the data point.
     */
    public String getName();
}
