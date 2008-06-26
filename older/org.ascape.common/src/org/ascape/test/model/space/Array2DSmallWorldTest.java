/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.test.model.space;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.ascape.model.Cell;
import org.ascape.model.LocatedAgent;
import org.ascape.model.Scape;
import org.ascape.model.space.Array2DSmallWorld;
import org.ascape.model.space.Coordinate2DDiscrete;
import org.ascape.model.space.Space;
import org.ascape.util.Conditional;

public class Array2DSmallWorldTest extends TestCase {

    Scape ts;

    public Array2DSmallWorldTest(String name) {
        super(name);
    }

    class TestCell extends Cell {
        boolean condition;
    }

    Conditional TEST_CONDITION = new Conditional() {
        public boolean meetsCondition(Object object) {
            return ((TestCell) object).condition;
        }
    };

    public void setUpSmallWorld(int width, int height, int radius) {
        ts = new Scape(new Array2DSmallWorld());
        ts.setPeriodic(true);
        ((Array2DSmallWorld) ts.getSpace()).setRadius(radius);
        ((Array2DSmallWorld) ts.getSpace()).setRandomEdgeRatio(0.0);
        ts.setExtent(width, height);
        ts.setPrototypeAgent(new TestCell());
        ts.createScape();
        ts.execute(Scape.INITIALIZE_RULE);
    }

    public void addEdge(Cell c1, Cell c2) {
        List n1 = new ArrayList(c1.findNeighbors());
        n1.add(c2);
        c1.setNeighborsList(n1);
        ((Array2DSmallWorld) ts.getSpace()).getSmallWorldCells().add(c1);
    }

    public void testFindWithin() {
        setUpSmallWorld(20, 20, 1);
        TestCell l1 = (TestCell) ts.get(new Coordinate2DDiscrete(13, 13));
        l1.condition = true;
        TestCell l2 = (TestCell) ts.get(new Coordinate2DDiscrete(19, 19));
        l2.condition = true;

        List results = ts.findWithin(l1.getCoordinate(), null, true, 0.0);
        assertEquals(results.size(), 1);
        assertTrue(results.contains(l1));

        results = ts.findWithin(l1.getCoordinate(), null, true, 1.0);
        assertEquals(results.size(), 9);

        results = ts.findWithin(new Coordinate2DDiscrete(10, 10), null, false, 3.0);
        assertTrue(results.contains(l1));
        assertTrue(!results.contains(l2));

        results = ts.findWithin(new Coordinate2DDiscrete(10, 10), TEST_CONDITION, false, 3.0);

        assertTrue(results.contains(l1));
        assertTrue(!results.contains(l2));
        assertTrue(results.size() == 1);


        ts.initialize();

        TestCell swl1 = (TestCell) ts.get(new Coordinate2DDiscrete(11, 11));
        TestCell swl2 = (TestCell) ts.get(new Coordinate2DDiscrete(18, 18));
        addEdge(swl1, swl2);
        assertTrue(swl1.findNeighbors().contains(swl2));

        //Test below are not valid with current configuration

        results = ts.findWithin(new Coordinate2DDiscrete(10, 10), TEST_CONDITION, false, 3.0);

        assertTrue(results.contains(l2));
        assertTrue(results.contains(l1));
        assertTrue(results.size() == 2);
    }


    public void testFindNearest() {
        setUpSmallWorld(20, 20, 1);

        TestCell l1 = (TestCell) ts.get(new Coordinate2DDiscrete(15, 15));
        l1.condition = true;
        TestCell l2 = (TestCell) ts.get(new Coordinate2DDiscrete(19, 19));
        l2.condition = true;
        LocatedAgent result = ts.findNearest(new Coordinate2DDiscrete(10, 10), TEST_CONDITION, false, Double.MAX_VALUE);
        assertTrue(result == l1);

        ts.initialize();

        TestCell swl1 = (TestCell) ts.get(new Coordinate2DDiscrete(11, 11));
        TestCell swl2 = (TestCell) ts.get(new Coordinate2DDiscrete(18, 18));
        addEdge(swl1, swl2);

        l1 = (TestCell) ts.get(new Coordinate2DDiscrete(15, 15));
        l1.condition = true;
        l2 = (TestCell) ts.get(new Coordinate2DDiscrete(19, 19));
        l2.condition = true;

        result = ts.findNearest(new Coordinate2DDiscrete(10, 10), TEST_CONDITION, false, Double.MAX_VALUE);
        assertTrue(result == l2);

        TestCell l3 = (TestCell) ts.get(new Coordinate2DDiscrete(17, 17));
        l3.condition = true;
        TestCell l4 = (TestCell) ts.get(new Coordinate2DDiscrete(19, 17));
        l4.condition = true;
        TestCell l5 = (TestCell) ts.get(new Coordinate2DDiscrete(18, 19));
        l5.condition = true;
        TestCell l6 = (TestCell) ts.get(new Coordinate2DDiscrete(17, 16));
        l6.condition = true;
        TestCell l7 = (TestCell) ts.get(new Coordinate2DDiscrete(7, 7));
        l7.condition = true;
        TestCell l8 = (TestCell) ts.get(new Coordinate2DDiscrete(13, 13));
        l8.condition = true;

        Set results = new HashSet();
        //if getting random errors, simply increase the number of loops to ensure good random coverage
        for (int i = 0; i < 50; i++) {
            result = ts.findNearest(new Coordinate2DDiscrete(10, 10), TEST_CONDITION, false, Double.MAX_VALUE);
            assertTrue((result == l2) || (result == l3) || (result == l4) || (result == l5) || (result == l7) || (result == l8));
            results.add(result);
        }
        assertTrue(results.contains(l2));
        assertTrue(results.contains(l3));
        assertTrue(results.contains(l4));
        assertTrue(results.contains(l5));
        assertTrue(!results.contains(l6));
        assertTrue(results.contains(l7));
        assertTrue(results.contains(l8));
    }

    public static void printSmallWorld(Space space, Conditional condition) {
//        (new Throwable()).printStackTrace();
        System.out.print(" ");
        for (int y = 0; y < ((Coordinate2DDiscrete) space.getExtent()).getYValue(); y++) {
            System.out.print(y % 10);
        }
        System.out.println("");
        for (int x = 0; x < ((Coordinate2DDiscrete) space.getExtent()).getXValue(); x++) {
            System.out.print(x % 10);
            for (int y = 0; y < ((Coordinate2DDiscrete) space.getExtent()).getYValue(); y++) {
                if (condition.meetsCondition(space.get(new Coordinate2DDiscrete(x, y)))) {
                    System.out.print("X");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println("");
        }
    }

    public void testCalculateDistance() {
        setUpSmallWorld(20, 20, 1);

        assertTrue(ts.calculateDistance(new Coordinate2DDiscrete(10, 10), new Coordinate2DDiscrete(10, 10)) == 0);

        assertTrue(ts.calculateDistance(new Coordinate2DDiscrete(10, 10), new Coordinate2DDiscrete(19, 19)) == 9);

        ts.initialize();

        TestCell swl1 = (TestCell) ts.get(new Coordinate2DDiscrete(11, 11));
        TestCell swl2 = (TestCell) ts.get(new Coordinate2DDiscrete(18, 18));
        addEdge(swl1, swl2);

        assertTrue(ts.calculateDistance(new Coordinate2DDiscrete(10, 10), new Coordinate2DDiscrete(19, 19)) == 3);

        addEdge(swl2, swl1);
        assertTrue(ts.calculateDistance(new Coordinate2DDiscrete(19, 19), new Coordinate2DDiscrete(10, 10)) == 3);
    }

    public void testFindNearest2() {
        setUpSmallWorld(20, 20, 1);

        TestCell origin = (TestCell) ts.get(new Coordinate2DDiscrete(10, 10));

        assertTrue((origin.findNearest(null, true, Double.MAX_VALUE) == origin));

        for (Iterator iterator = origin.findNeighbors().iterator(); iterator.hasNext();) {
            TestCell cell = (TestCell) iterator.next();
            cell.condition = true;
        }
        Set results = new HashSet();
        //Note: there is a small but reachable chance that the following sampling will not discover every neighboring cell.
        //If the test below fails, increase the max value for i further.
        for (int i = 0; i < 100; i++) {
            LocatedAgent result = ts.findNearest(new Coordinate2DDiscrete(10, 10), TEST_CONDITION, false, Double.MAX_VALUE);
            results.add(result);
        }
        for (Iterator iterator = origin.findNeighbors().iterator(); iterator.hasNext();) {
            TestCell cell = (TestCell) iterator.next();
            if (!results.contains(cell)) {
                System.out.println(cell);
            }
            assertTrue(results.contains(cell));
        }
        for (Iterator iterator = results.iterator(); iterator.hasNext();) {
            TestCell cell = (TestCell) iterator.next();
            assertTrue(origin.findNeighbors().contains(cell));
        }
    }
}