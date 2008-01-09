/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.sweep;

/**
 * An interface describing something that can be sweeped across.
 *
 * @author Miles Parker
 * @version 1.0
 * @since 1.0
 */
public interface Sweepable {

    public void reset();

    public boolean hasNext();

    public Object next();
}
