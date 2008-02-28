/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util;

import org.ascape.util.data.DataPoint;

/**
 * A data point that also serves as a setter for the point's value.
 *
 * @author Miles Parker
 * @version 1.5
 * @history 1.0.3 3/9/1999 renamed from ValueSourceSetter
 * @since 1.0.3
 */
public abstract class ValueSetter implements DataPoint {

    /**
     * Override to set a value for the given object.
     */
    public abstract void setValue(Object object);
}
