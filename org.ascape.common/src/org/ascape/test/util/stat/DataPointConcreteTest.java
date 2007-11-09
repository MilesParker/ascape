/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.test.util.stat;

import junit.framework.TestCase;

import org.ascape.util.data.DataPointConcrete;


public class DataPointConcreteTest extends TestCase {

    public DataPointConcreteTest(String name) {
        super(name);
    }

    public void testEquals() {
        assertTrue(!DataPointConcrete.equals(1.0, -1.0));
        assertTrue(!DataPointConcrete.equals(10000000.0, -10000000.0));
        assertTrue(!DataPointConcrete.equals(-10000000.0, 10000000.0));
        assertTrue(DataPointConcrete.equals(10000000.0, 10000000.0));
        assertTrue(DataPointConcrete.equals(-10000000.0, -10000000.0));
        assertTrue(!DataPointConcrete.equals(0.000000002, -0.000000002));
        assertTrue(!DataPointConcrete.equals(-0.00000002, 0.00000002));
        assertTrue(DataPointConcrete.equals(0.00000002, 0.00000002));
        assertTrue(DataPointConcrete.equals(0.0, 0.000000000002));
        assertTrue(DataPointConcrete.equals(-0.00000002, -0.00000002));
        assertTrue(DataPointConcrete.equals(0.0, 0.0));
        assertTrue(DataPointConcrete.equals(1.0 / 3.0, 0.333333333333333333));

        assertTrue(DataPointConcrete.equals(1.0, 1.0 + 0.5 * DataPointConcrete.equalsEpsilon));
        assertTrue(!DataPointConcrete.equals(1.0, 1.0 + 2.0 * DataPointConcrete.equalsEpsilon));
    }

    public void testEqualsFloat() {
        assertTrue(DataPointConcrete.equals(1.0f, 1.0f + 0.5f * DataPointConcrete.equalsEpsilonFloat));
        assertTrue(!DataPointConcrete.equals(1.0f, 1.0f + 2.0f * DataPointConcrete.equalsEpsilonFloat));
    }
}