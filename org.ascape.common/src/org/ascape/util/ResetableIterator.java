/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util;

import java.util.Iterator;

/**
 * An iterator that can be reused - e.g. its cursor can be set back to the first element.
 *
 * @author Miles Parker
 * @version 1.5
 * @history 1.5 11/30/99 first in
 * @since 1.5
 */
public interface ResetableIterator extends Iterator {

    public void first();
}
