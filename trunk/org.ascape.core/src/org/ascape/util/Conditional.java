/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util;

import java.io.Serializable;

/**
 * Describes an object which can test whether an object meets some condition.
 *
 * @author Miles Parker
 * @version 1.0
 * @since 1.0
 */
public interface Conditional extends Serializable {

    /**
     * Does the object meet the specified condition?
     * @param object the object to test for condition
     * @return true if test passed, otherwise false
     */
    public boolean meetsCondition(Object object);
}
