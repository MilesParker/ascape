/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util;

import java.util.Random;

public interface RandomFunctions {

    public int randomInRange(int low, int high);

    /**
     * Generate a double uniformly distributed across some range.
     * @param low the lowest number (inclusive) that the resulting double might be
     * @param high the hignest number (exclusive) that the resulting double might be
     * @return uniformly distributed pseudorandom double
     */
    public double randomInRange(double low, double high);

    /**
     * Generate an integer uniformly distributed across 0...limit - 1.
     * @param limit the maximum limit (exclusive) of the resulting int
     * @return uniformly distributed pseudorandom int
     */
    public int randomToLimit(int limit);

    /**
     * Returns a random boolean value.
     */
    public boolean randomIs();

    Random getRandom();

    void setRandom(Random random);
}
