/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.test.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;

import junit.framework.TestCase;

import org.ascape.model.Cell;
import org.ascape.model.Scape;
import org.ascape.model.event.ControlEvent;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.space.Array1D;
import org.ascape.model.space.CollectionSpace;
import org.ascape.util.Conditional;
import org.ascape.util.data.DataPoint;
import org.ascape.view.nonvis.NonGraphicView;

public class ScapeTest extends TestCase {

    public ScapeTest(String name) {
        super(name);
    }

    Scape testScape;

    protected void setUp() {
        //Because Scape and Scape are abstract, we're need to construct an implementation..
        //however, we will only be testing base Scape functionality
        testScape = new Scape(new Array1D());
    }

    boolean testRestartRequest = false;

    public void testScapeRestartBehavior() {
        //Make sure that scape restart is working correctly
        testScape.addView(new NonGraphicView() {
            public void scapeIterated(ScapeEvent scapeEvent) {
                if ((getScape().getPeriod() == 1) && (testRestartRequest)) {
                    assertTrue("Passed Restart Behavior", true);
                    getScape().getRunner().stop();
                }
                if (getScape().getPeriod() == 10) {
                    getScape().respondControl(new ControlEvent(this, ControlEvent.REQUEST_RESTART));
                    testRestartRequest = true;
                }
                if (getScape().getPeriod() > 10) {
                    fail("Scape did not properly stop");
                }
            }

            public void scapeStarted(ScapeEvent scapeEvent) {
                if (testRestartRequest) {
                    assertTrue("Passed On Start", true);
                }
            }
        });
        testScape.createScape();
        testScape.initialize();
        testScape.setAutoRestart(false);
        testScape.getRunner().start();
    }

    class NumberedCell extends Cell {

        int number;

        public NumberedCell(int number) {
            this.number = number;
        }
    }

    Comparator compareNumbers = new Comparator() {
        public int compare(Object o1, Object o2) {
            return ((NumberedCell) o1).number - ((NumberedCell) o2).number;
        }
    };

    public void testMinMaxAndEquals() {
        Scape testScape = new Scape();

        testScape.add(new NumberedCell(4));
        testScape.add(new NumberedCell(14));
        testScape.add(new NumberedCell(3568));
        testScape.add(new NumberedCell(1));
        testScape.add(new NumberedCell(27));
        testScape.add(new NumberedCell(0));
        testScape.add(new NumberedCell(3567));
        testScape.add(new NumberedCell(1234));

        assertTrue(testScape.search(compareNumbers, new NumberedCell(27)) == testScape.get(4));

        assertTrue(testScape.searchMin(compareNumbers) == testScape.get(5));

        testScape.add(4, new NumberedCell(-14));
        testScape.add(4, new NumberedCell(-10));
        assertTrue(testScape.searchMin(compareNumbers) == testScape.get(5));

        assertTrue(testScape.searchMax(compareNumbers) == testScape.get(2));
    }

    class TestAgent extends Cell {

        boolean testCond;
    }

    class TestConditional implements Conditional {

        public boolean meetsCondition(Object o) {
            return ((TestAgent) o).testCond;
        }
    }

    public void testConditionalIterator() {
        Scape testScape = new Scape();
        testScape.setPrototypeAgent(new TestAgent());
        testScape.setExtent(5);
        testScape.createScape();
        testScape.execute(Scape.INITIALIZE_RULE);

        ((TestAgent) testScape.get(1)).testCond = true;
        ((TestAgent) testScape.get(2)).testCond = true;

        ArrayList a = new ArrayList();

        Iterator iter = CollectionSpace.conditionalIterator(testScape.iterator(), new TestConditional());
        while (iter.hasNext()) {
            a.add(iter.next());
        }

        assertTrue(!a.contains(testScape.get(0)));
        assertTrue(a.contains(testScape.get(1)));
        assertTrue(a.contains(testScape.get(2)));
        assertTrue(!a.contains(testScape.get(3)));
        assertTrue(!a.contains(testScape.get(4)));
    }

    class TestValueAgent extends Cell {

        double value;
    }

    class TestDataPoint implements DataPoint {

        public double getValue(Object o) {
            return ((TestValueAgent) o).value;
        }

        public String getName() {
            return "";
        }
    }

    public void testFindMinMax() {
        Scape testScape = new Scape();
        testScape.setPrototypeAgent(new TestValueAgent());
        testScape.setExtent(5);
        testScape.createScape();
        testScape.execute(Scape.INITIALIZE_RULE);

        ((TestValueAgent) testScape.get(0)).value = 0.12;
        ((TestValueAgent) testScape.get(1)).value = 100.12;
        ((TestValueAgent) testScape.get(2)).value = 101.92;
        ((TestValueAgent) testScape.get(3)).value = -234.12;
        ((TestValueAgent) testScape.get(4)).value = 43.01;

        assertTrue(testScape.findMaximum(testScape.iterator(), new TestDataPoint()) == testScape.get(2));
        assertTrue(testScape.findMinimum(testScape.iterator(), new TestDataPoint()) == testScape.get(3));
    }

    /*
     * Note: this test may fail even when correct at very very low probability.
     */
    public void testFindMinMaxMultiple() {
        Scape testScape = new Scape();
        testScape.setPrototypeAgent(new TestValueAgent());
        testScape.setExtent(9);
        testScape.createScape();
        testScape.execute(Scape.INITIALIZE_RULE);

        ((TestValueAgent) testScape.get(0)).value = 0.12;
        ((TestValueAgent) testScape.get(1)).value = 100.12;
        ((TestValueAgent) testScape.get(2)).value = 101.92;
        ((TestValueAgent) testScape.get(3)).value = -234.12;
        ((TestValueAgent) testScape.get(4)).value = -234.12;
        ((TestValueAgent) testScape.get(5)).value = 43.01;
        ((TestValueAgent) testScape.get(6)).value = 101.92;
        ((TestValueAgent) testScape.get(7)).value = 101.92;
        ((TestValueAgent) testScape.get(8)).value = -234.12;

        HashSet allMins = new HashSet();
        for (int i = 0; i < 100; i++) {
            allMins.add(testScape.findMinimum(testScape.iterator(), new TestDataPoint()));
        }
        assertTrue(!allMins.contains(testScape.get(0)));
        assertTrue(!allMins.contains(testScape.get(1)));
        assertTrue(!allMins.contains(testScape.get(2)));
        assertTrue(allMins.contains(testScape.get(3)));
        assertTrue(allMins.contains(testScape.get(4)));
        assertTrue(!allMins.contains(testScape.get(5)));
        assertTrue(!allMins.contains(testScape.get(6)));
        assertTrue(!allMins.contains(testScape.get(7)));
        assertTrue(allMins.contains(testScape.get(8)));

        HashSet allMaxs = new HashSet();
        for (int i = 0; i < 100; i++) {
            allMaxs.add(testScape.findMaximum(testScape.iterator(), new TestDataPoint()));
        }
        assertTrue(!allMaxs.contains(testScape.get(0)));
        assertTrue(!allMaxs.contains(testScape.get(1)));
        assertTrue(allMaxs.contains(testScape.get(2)));
        assertTrue(!allMaxs.contains(testScape.get(3)));
        assertTrue(!allMaxs.contains(testScape.get(4)));
        assertTrue(!allMaxs.contains(testScape.get(5)));
        assertTrue(allMaxs.contains(testScape.get(6)));
        assertTrue(allMaxs.contains(testScape.get(7)));
        assertTrue(!allMaxs.contains(testScape.get(8)));
    }

    public void testCloneBehavior() {
        Object[] objects1 = new Object[4];
        for (int i = 0; i < objects1.length; i++) {
            objects1[i] = new Double(i);
        }
        Object[] objects2 = objects1;
        Object[] objects3 = new Object[objects1.length];
        System.arraycopy(objects1, 0, objects3, 0, objects1.length);
        objects2[0] = new Double(100);
        assertTrue(((Double) objects1[0]).intValue() == 100);
        objects3[0] = new Double(5);
        assertTrue(((Double) objects1[0]).intValue() == 100);
        assertTrue(((Double) objects3[0]).intValue() == 5);
    }
}
