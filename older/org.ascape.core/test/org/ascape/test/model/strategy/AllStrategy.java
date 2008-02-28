/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.test.model.strategy;

import junit.framework.Test;
import junit.framework.TestSuite;

/*
 * User: Miles Parker  
 * Date: Sep 24, 2003
 * Time: 3:31:33 PM
 * To change this template use Options | File Templates.
 */

public class AllStrategy extends TestSuite {

    public AllStrategy(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Ascape Model Strategy");
        suite.addTest(new TestSuite(org.ascape.test.model.strategy.ChainedStrategyTest.class));

        return suite;
    }
}
