/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.test.util;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * TestSuite that runs tests for the ascape model suite
 *
 */
public class AllUtil extends TestSuite {

    public AllUtil(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Ascape Utilities");
        suite.addTest(new TestSuite(org.ascape.test.util.stat.DataPointConcreteTest.class));
        suite.addTest(new TestSuite(org.ascape.test.util.stat.DataPointComparatorTest.class));
        suite.addTest(new TestSuite(org.ascape.test.util.stat.StatCollectorCSAMMTest.class));
        suite.addTest(new TestSuite(org.ascape.test.util.stat.StatCollectorCondCSAMMTest.class));
        suite.addTest(new TestSuite(org.ascape.test.util.stat.StatCollectorCSAMMVarTest.class));
        suite.addTest(new TestSuite(org.ascape.test.util.stat.StatCollectorCondCSAMMVarTest.class));
        suite.addTest(new TestSuite(org.ascape.test.util.sweep.SweepDimensionTest.class));
        suite.addTest(new TestSuite(org.ascape.test.util.sweep.SweepGroupTest.class));
        suite.addTest(new TestSuite(org.ascape.test.util.sweep.SweepLinkTest.class));
        suite.addTest(new TestSuite(org.ascape.test.util.UtilityTest.class));
        suite.addTest(new TestSuite(org.ascape.test.util.stat.LongitudinalDataCollectionTest.class));
        return suite;
    }
}