/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.test.util.stat;

import junit.framework.TestCase;

import org.ascape.util.data.DataPointConcrete;
import org.ascape.util.data.StatCollectorCSAMM;


public class StatCollectorCSAMMTest extends TestCase {

    public StatCollectorCSAMMTest(String name) {
        super(name);
    }

    StatCollectorCSAMM stat;

    public void subTestBasic() {
        assertTrue(stat.getCount() == 0);
        assertTrue(DataPointConcrete.equals(stat.getSum(), 0.0));
        assertTrue(DataPointConcrete.equals(stat.getAvg(), 0.0));
        assertTrue(DataPointConcrete.equals(stat.getMin(), 0.0));
        assertTrue(DataPointConcrete.equals(stat.getMax(), 0.0));

        stat.addValue(2.0);
        assertTrue(stat.getCount() == 1);
        assertTrue(DataPointConcrete.equals(stat.getSum(), 2.0));
        assertTrue(DataPointConcrete.equals(stat.getAvg(), 2.0));
        assertTrue(DataPointConcrete.equals(stat.getMin(), 2.0));
        assertTrue(DataPointConcrete.equals(stat.getMax(), 2.0));

        stat.addValue(2.0);
        assertTrue(stat.getCount() == 2);
        assertTrue(DataPointConcrete.equals(stat.getSum(), 4.0));
        assertTrue(DataPointConcrete.equals(stat.getAvg(), 2.0));
        assertTrue(DataPointConcrete.equals(stat.getMin(), 2.0));
        assertTrue(DataPointConcrete.equals(stat.getMax(), 2.0));
    }


    public void subTestNegative() {
        stat.addValue(-324324.0);
        assertTrue(stat.getCount() == 1);
        assertTrue(DataPointConcrete.equals(stat.getSum(), -324324.0));
        assertTrue(DataPointConcrete.equals(stat.getAvg(), -324324.0));
        assertTrue(DataPointConcrete.equals(stat.getMin(), -324324.0));
        assertTrue(DataPointConcrete.equals(stat.getMax(), -324324.0));

        stat.addValue(324324.0);
        assertTrue(stat.getCount() == 2);
        assertTrue(DataPointConcrete.equals(stat.getSum(), 0.0));
        assertTrue(DataPointConcrete.equals(stat.getAvg(), 0.0));
        assertTrue(DataPointConcrete.equals(stat.getMin(), -324324.0));
        assertTrue(DataPointConcrete.equals(stat.getMax(), 324324.0));
    }

    public void subTestPositive() {
        stat.addValue(324324.0);
        assertTrue(stat.getCount() == 1);
        assertTrue(DataPointConcrete.equals(stat.getSum(), 324324.0));
        assertTrue(DataPointConcrete.equals(stat.getAvg(), 324324.0));
        assertTrue(DataPointConcrete.equals(stat.getMin(), 324324.0));
        assertTrue(DataPointConcrete.equals(stat.getMax(), 324324.0));

        stat.addValue(-324324.0);
        assertTrue(stat.getCount() == 2);
        assertTrue(DataPointConcrete.equals(stat.getSum(), 0.0));
        assertTrue(DataPointConcrete.equals(stat.getAvg(), 0.0));
        assertTrue(DataPointConcrete.equals(stat.getMin(), -324324.0));
        assertTrue(DataPointConcrete.equals(stat.getMax(), 324324.0));
    }

    public void subTestAll() {
        subTestBasic();
        subTestNegative();
        subTestPositive();
    }

    public void testConstructorsAndClear() {
        stat = new StatCollectorCSAMM();
        subTestBasic();
        stat = new StatCollectorCSAMM();
        subTestNegative();
        stat = new StatCollectorCSAMM();
        subTestPositive();

        stat = new StatCollectorCSAMM("Test Stat");
        subTestBasic();
        stat = new StatCollectorCSAMM("Test Stat");
        subTestNegative();
        stat = new StatCollectorCSAMM("Test Stat");
        subTestPositive();

        stat = new StatCollectorCSAMM("Test Stat", true);
        subTestBasic();
        stat = new StatCollectorCSAMM("Test Stat", true);
        subTestNegative();
        stat = new StatCollectorCSAMM("Test Stat", true);
        subTestPositive();

        stat.clear();
        subTestBasic();
        stat.clear();
        subTestNegative();
        stat.clear();
        subTestPositive();
    }
}