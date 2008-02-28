/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.test.model;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.ascape.test.model.strategy.AllStrategy;


/**
 * TestSuite that runs tests for the ascape model suite
 *
 */
public class AllModel extends TestSuite {

    public AllModel(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Ascape Model");
        suite.addTest(new TestSuite(org.ascape.test.model.AscapeObjectTest.class));
        suite.addTest(new TestSuite(org.ascape.test.model.CellOccupantTest.class));
        suite.addTest(new TestSuite(org.ascape.test.model.ScapeTest.class));
        suite.addTest(new TestSuite(org.ascape.test.model.ScapeTest.class));
        suite.addTest(new TestSuite(org.ascape.test.model.space.DiscreteTest.class));
        suite.addTest(new TestSuite(org.ascape.test.model.space.Array1DBaseTest.class));
        suite.addTest(new TestSuite(org.ascape.test.model.space.Array1DNonPeriodicTest.class));
        suite.addTest(new TestSuite(org.ascape.test.model.space.Array1DTest.class));
        suite.addTest(new TestSuite(org.ascape.test.model.space.Array2DTest.class));
        suite.addTest(new TestSuite(org.ascape.test.model.space.GraphTest.class));
        suite.addTest(new TestSuite(org.ascape.test.model.space.ListTest.class));
        suite.addTest(new TestSuite(org.ascape.test.model.event.ScapeListenerTest.class));
        suite.addTest(new TestSuite(org.ascape.test.model.space.SubListTest.class));
        suite.addTest(new TestSuite(org.ascape.test.model.space.Continuous2DTest.class));
        suite.addTest(new TestSuite(org.ascape.test.model.space.SubContinuous2DTest.class));
        //suite.addTest(new TestSuite(org.ascape.test.model.GlobeTest.class));

        suite.addTest(new TestSuite(org.ascape.test.model.space.Array2DSmallWorldTest.class));
//Temporary removal of serialization test to see if it fixes CMA problem...s
        //suite.addTest(new TestSuite(org.ascape.test.model.SerializationTest.class));

        suite.addTest(AllStrategy.suite());

        return suite;
    }
}