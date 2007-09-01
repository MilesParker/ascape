/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.movie;

import java.awt.Component;


/**
 * An interface for a recording target that a recorder can use.
 *
 * @author Miles Parker
 * @version 2.9 (Ascape)
 * @history 5/9/2002 first in
 */
public interface RecorderTarget {

    /**
     * Returns the root component to be recorded.
     */
    public Component getComponent();
}
