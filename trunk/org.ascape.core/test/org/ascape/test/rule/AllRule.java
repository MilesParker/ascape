/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.test.rule;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * TestSuite that runs tests for the ascape model suite
 *
 */
public class AllRule extends TestSuite {

    public AllRule(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Ascape Rule");
        suite.addTest(new TestSuite(org.ascape.test.rule.PropogateTest.class));
        suite.addTest(new TestSuite(org.ascape.test.rule.SearchRuleTest.class));
        return suite;
    }
}