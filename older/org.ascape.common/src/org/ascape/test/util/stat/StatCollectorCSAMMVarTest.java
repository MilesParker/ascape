/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.test.util.stat;

import java.util.ArrayList;

import org.ascape.util.data.DataPointConcrete;
import org.ascape.util.data.StatCollectorCSAMMVar;


//For now, just making sure that min max and constructors work..stat and variaince to be tested later

public class StatCollectorCSAMMVarTest extends StatCollectorCSAMMTest {

    public StatCollectorCSAMMVarTest(String name) {
        super(name);
    }

    public void testConstructorsAndClear() {
        stat = new StatCollectorCSAMMVar();
        subTestBasic();
        stat = new StatCollectorCSAMMVar();
        subTestNegative();
        stat = new StatCollectorCSAMMVar();
        subTestPositive();

        stat = new StatCollectorCSAMMVar("Test Stat");
        subTestBasic();
        stat = new StatCollectorCSAMMVar("Test Stat");
        subTestNegative();
        stat = new StatCollectorCSAMMVar("Test Stat");
        subTestPositive();

        stat = new StatCollectorCSAMMVar("Test Stat", true);
        subTestBasic();
        stat = new StatCollectorCSAMMVar("Test Stat", true);
        subTestNegative();
        stat = new StatCollectorCSAMMVar("Test Stat", true);
        subTestPositive();

        stat.clear();
        subTestBasic();
        stat.clear();
        subTestNegative();
        stat.clear();
        subTestPositive();
    }

    class TestClass {

        double value;

        public TestClass(double value) {
            this.value = value;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }
    }

    public void testArrayFunctionalityAndAlgorithms() {
        StatCollectorCSAMMVar myCollector = new StatCollectorCSAMMVar("Test") {
            public double getValue(Object o) {
                return ((TestClass) o).getValue();
            }
        };
        ArrayList list = new ArrayList();
        list.add(new TestClass(1.0));
        list.add(new TestClass(2.0));
        list.add(new TestClass(3.0));
        list.add(new TestClass(4.0));
        list.add(new TestClass(5.0));
        myCollector.calculateCollection(list);

        assertTrue(DataPointConcrete.equals(myCollector.getCount(), 5.0));
        assertTrue(DataPointConcrete.equals(myCollector.getSum(), 15.0));
        assertTrue(DataPointConcrete.equals(myCollector.getAvg(), 3.0));
        assertTrue(DataPointConcrete.equals(myCollector.getMin(), 1.0));
        assertTrue(DataPointConcrete.equals(myCollector.getMax(), 5.0));

        ArrayList list2 = new ArrayList();
        list2.add(new TestClass(1.01));
        list2.add(new TestClass(1.3));
        list2.add(new TestClass(-20));
        list2.add(new TestClass(18));
        list2.add(new TestClass(12));
        list2.add(new TestClass(1243));
        list2.add(new TestClass(-987));
        list2.add(new TestClass(12));
        list2.add(new TestClass(123.54));
        list2.add(new TestClass(876.08));
        myCollector.calculateCollection(list2);

        //Per Excel calcs...
        assertTrue(DataPointConcrete.equals(myCollector.getAvg(), 127.993));
        assertTrue(DataPointConcrete.equals(myCollector.getCount(), 10.0));
        assertTrue(DataPointConcrete.equals(myCollector.getMin(), -987));
        assertTrue(DataPointConcrete.equals(myCollector.getMax(), 1243));
        assertTrue(DataPointConcrete.equals(myCollector.getStDev(), 590.5918809));
        assertTrue(DataPointConcrete.equals(myCollector.getSum(), 1279.93));
        assertTrue(DataPointConcrete.equals(myCollector.getVar(), 348798.7697));
    }
}