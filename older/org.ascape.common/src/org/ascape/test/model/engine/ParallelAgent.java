/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */
package org.ascape.test.model.engine;

/*
 * User: Miles Parker
 * Date: Feb 24, 2005
 * Time: 11:01:44 AM
 */

public interface ParallelAgent {
    void incrState();

    void calcState();

    void updateState();
}
