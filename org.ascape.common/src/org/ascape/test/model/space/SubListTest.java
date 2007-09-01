/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.test.model.space;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.ascape.model.Agent;
import org.ascape.model.Cell;
import org.ascape.model.LocatedAgent;
import org.ascape.model.Scape;
import org.ascape.model.rule.Rule;
import org.ascape.model.space.Array1D;
import org.ascape.model.space.Coordinate1DDiscrete;
import org.ascape.model.space.CoordinateMutable;
import org.ascape.model.space.Mutable;
import org.ascape.model.space.SubListSpace;

public class SubListTest extends TestCase {

    public SubListTest(String name) {
        super(name);
    }

    public void testSuperBehavior() {
        Scape superScape = new Scape();
        Scape subScape1 = new Scape(new SubListSpace());
        subScape1.setSuperScape(superScape);
        Scape subScape2 = new Scape(new SubListSpace());
        subScape2.setSuperScape(superScape);

        LocatedAgent agent1_0 = new LocatedAgent();
        subScape1.add(agent1_0);
        LocatedAgent agent1_1 = new LocatedAgent();
        subScape1.add(agent1_1);
        LocatedAgent agent1_2 = new LocatedAgent();
        subScape1.add(agent1_2);
        LocatedAgent agent1_3 = new LocatedAgent();
        subScape1.add(agent1_3);
        LocatedAgent agent1_4 = new LocatedAgent();
        subScape1.add(agent1_4);

        subScape2.setExtent(5);
        subScape2.createScape();
        LocatedAgent agent2_0 = (LocatedAgent) subScape2.get(0);
        LocatedAgent agent2_1 = (LocatedAgent) subScape2.get(1);
        LocatedAgent agent2_2 = (LocatedAgent) subScape2.get(2);
        LocatedAgent agent2_3 = (LocatedAgent) subScape2.get(3);
        LocatedAgent agent2_4 = (LocatedAgent) subScape2.get(4);

        assertTrue(subScape1.size() == 5);
        assertTrue(subScape2.size() == 5);
        assertTrue(superScape.size() == 10);

        assertTrue(subScape1.contains(agent1_0));
        assertTrue(subScape1.contains(agent1_1));
        assertTrue(subScape1.contains(agent1_2));
        assertTrue(subScape1.contains(agent1_3));
        assertTrue(subScape1.contains(agent1_4));

        assertTrue(subScape2.contains(agent2_0));
        assertTrue(subScape2.contains(agent2_1));
        assertTrue(subScape2.contains(agent2_2));
        assertTrue(subScape2.contains(agent2_3));
        assertTrue(subScape2.contains(agent2_4));

        assertTrue(superScape.contains(agent1_0));
        assertTrue(superScape.contains(agent1_1));
        assertTrue(superScape.contains(agent1_2));
        assertTrue(superScape.contains(agent1_3));
        assertTrue(superScape.contains(agent1_4));
        assertTrue(superScape.contains(agent2_0));
        assertTrue(superScape.contains(agent2_1));
        assertTrue(superScape.contains(agent2_2));
        assertTrue(superScape.contains(agent2_3));
        assertTrue(superScape.contains(agent2_4));

        subScape1.remove(agent1_0);
        assertTrue(!subScape1.contains(agent1_0));
        assertTrue(!subScape2.contains(agent1_0));
        assertTrue(!superScape.contains(agent1_0));

        LocatedAgent newAgent0 = new LocatedAgent();

        subScape1.add(newAgent0);
        assertTrue(subScape1.contains(newAgent0));
        assertTrue(!subScape2.contains(newAgent0));
        assertTrue(superScape.contains(newAgent0));

        Scape tempScape = new Scape();
        tempScape.addAll(subScape2);
        assertTrue(tempScape.contains(agent2_0));
        assertTrue(tempScape.contains(agent2_1));
        assertTrue(tempScape.contains(agent2_2));
        assertTrue(tempScape.contains(agent2_3));
        assertTrue(tempScape.contains(agent2_4));

        subScape2.clear();
        assertTrue(!subScape1.contains(agent1_0));
        assertTrue(subScape1.contains(agent1_1));
        assertTrue(subScape1.contains(agent1_2));
        assertTrue(subScape1.contains(agent1_3));
        assertTrue(subScape1.contains(agent1_4));

        assertTrue(!subScape2.contains(agent2_0));
        assertTrue(!subScape2.contains(agent2_1));
        assertTrue(!subScape2.contains(agent2_2));
        assertTrue(!subScape2.contains(agent2_3));
        assertTrue(!subScape2.contains(agent2_4));

        assertTrue(!superScape.contains(agent1_0));
        assertTrue(superScape.contains(agent1_1));
        assertTrue(superScape.contains(agent1_2));
        assertTrue(superScape.contains(agent1_3));
        assertTrue(superScape.contains(agent1_4));
        assertTrue(!superScape.contains(agent2_0));
        assertTrue(!superScape.contains(agent2_1));
        assertTrue(!superScape.contains(agent2_2));
        assertTrue(!superScape.contains(agent2_3));
        assertTrue(!superScape.contains(agent2_4));

        subScape1.addAll(tempScape);

        assertTrue(!subScape1.contains(agent1_0));
        assertTrue(subScape1.contains(agent1_1));
        assertTrue(subScape1.contains(agent1_2));
        assertTrue(subScape1.contains(agent1_3));
        assertTrue(subScape1.contains(agent1_4));
        assertTrue(subScape1.contains(agent2_0));
        assertTrue(subScape1.contains(agent2_1));
        assertTrue(subScape1.contains(agent2_2));
        assertTrue(subScape1.contains(agent2_3));
        assertTrue(subScape1.contains(agent2_4));

        assertTrue(!subScape2.contains(agent2_0));
        assertTrue(!subScape2.contains(agent2_1));
        assertTrue(!subScape2.contains(agent2_2));
        assertTrue(!subScape2.contains(agent2_3));
        assertTrue(!subScape2.contains(agent2_4));

        assertTrue(!superScape.contains(agent1_0));
        assertTrue(superScape.contains(agent1_1));
        assertTrue(superScape.contains(agent1_2));
        assertTrue(superScape.contains(agent1_3));
        assertTrue(superScape.contains(agent1_4));
        assertTrue(superScape.contains(agent2_0));
        assertTrue(superScape.contains(agent2_1));
        assertTrue(superScape.contains(agent2_2));
        assertTrue(superScape.contains(agent2_3));
        assertTrue(superScape.contains(agent2_4));

        subScape1.remove(agent2_1);
        subScape2.add(agent2_1);
        assertTrue(subScape2.contains(agent2_1));
        assertTrue(superScape.contains(agent2_1));

        subScape1.add(agent2_1);
        subScape1.add(agent2_2);
        subScape2.add(agent2_2);
        subScape1.retainAll(subScape2);

        assertTrue(!subScape1.contains(agent1_0));
        assertTrue(!subScape1.contains(agent1_1));
        assertTrue(!subScape1.contains(agent1_2));
        assertTrue(!subScape1.contains(agent1_3));
        assertTrue(!subScape1.contains(agent1_4));
        assertTrue(!subScape1.contains(agent2_0));
        assertTrue(subScape1.contains(agent2_1));
        assertTrue(subScape1.contains(agent2_2));
        assertTrue(!subScape1.contains(agent2_3));
        assertTrue(!subScape1.contains(agent2_4));

        assertTrue(!subScape2.contains(agent2_0));
        assertTrue(subScape2.contains(agent2_1));
        assertTrue(subScape2.contains(agent2_2));
        assertTrue(!subScape2.contains(agent2_3));
        assertTrue(!subScape2.contains(agent2_4));

        assertTrue(!superScape.contains(agent1_0));
        assertTrue(!superScape.contains(agent1_1));
        assertTrue(!superScape.contains(agent1_2));
        assertTrue(!superScape.contains(agent1_3));
        assertTrue(!superScape.contains(agent1_4));
        assertTrue(!superScape.contains(agent2_0));
        assertTrue(superScape.contains(agent2_1));
        assertTrue(superScape.contains(agent2_2));
        assertTrue(!superScape.contains(agent2_3));
        assertTrue(!superScape.contains(agent2_4));
    }

    public void testNestedSuperBehavior() {
        Scape superSuperScape = new Scape();
        Scape superScape = new Scape(new SubListSpace());
        Scape subScape1 = new Scape(new SubListSpace());
        subScape1.setSuperScape(superScape);
        Scape subScape2 = new Scape(new SubListSpace());
        subScape2.setSuperScape(superScape);
        superScape.setSuperScape(superSuperScape);

        LocatedAgent agent1_0 = new LocatedAgent();
        subScape1.add(agent1_0);
        LocatedAgent agent1_1 = new LocatedAgent();
        subScape1.add(agent1_1);
        LocatedAgent agent1_2 = new LocatedAgent();
        subScape1.add(agent1_2);
        LocatedAgent agent1_3 = new LocatedAgent();
        subScape1.add(agent1_3);
        LocatedAgent agent1_4 = new LocatedAgent();
        subScape1.add(agent1_4);

        subScape2.setExtent(5);
        subScape2.createScape();
        LocatedAgent agent2_0 = (LocatedAgent) subScape2.get(0);
        LocatedAgent agent2_1 = (LocatedAgent) subScape2.get(1);
        LocatedAgent agent2_2 = (LocatedAgent) subScape2.get(2);
        LocatedAgent agent2_3 = (LocatedAgent) subScape2.get(3);
        LocatedAgent agent2_4 = (LocatedAgent) subScape2.get(4);

        assertTrue(superSuperScape.contains(agent1_0));
        assertTrue(superSuperScape.contains(agent1_1));
        assertTrue(superSuperScape.contains(agent1_2));
        assertTrue(superSuperScape.contains(agent1_3));
        assertTrue(superSuperScape.contains(agent1_4));
        assertTrue(superSuperScape.contains(agent2_0));
        assertTrue(superSuperScape.contains(agent2_1));
        assertTrue(superSuperScape.contains(agent2_2));
        assertTrue(superSuperScape.contains(agent2_3));
        assertTrue(superSuperScape.contains(agent2_4));

        subScape1.remove(agent1_0);
        assertTrue(!subScape1.contains(agent1_0));
        assertTrue(!subScape2.contains(agent1_0));
        assertTrue(!superScape.contains(agent1_0));

        LocatedAgent newAgent0 = new LocatedAgent();

        subScape1.add(newAgent0);

        Scape tempScape = new Scape();
        tempScape.addAll(subScape2);

        subScape2.clear();

        assertTrue(!superSuperScape.contains(agent1_0));
        assertTrue(superSuperScape.contains(agent1_1));
        assertTrue(superSuperScape.contains(agent1_2));
        assertTrue(superSuperScape.contains(agent1_3));
        assertTrue(superSuperScape.contains(agent1_4));
        assertTrue(!superSuperScape.contains(agent2_0));
        assertTrue(!superSuperScape.contains(agent2_1));
        assertTrue(!superSuperScape.contains(agent2_2));
        assertTrue(!superSuperScape.contains(agent2_3));
        assertTrue(!superSuperScape.contains(agent2_4));

        subScape1.addAll(tempScape);

        assertTrue(!subScape1.contains(agent1_0));
        assertTrue(subScape1.contains(agent1_1));
        assertTrue(subScape1.contains(agent1_2));
        assertTrue(subScape1.contains(agent1_3));
        assertTrue(subScape1.contains(agent1_4));
        assertTrue(subScape1.contains(agent2_0));
        assertTrue(subScape1.contains(agent2_1));
        assertTrue(subScape1.contains(agent2_2));
        assertTrue(subScape1.contains(agent2_3));
        assertTrue(subScape1.contains(agent2_4));

        assertTrue(!subScape2.contains(agent2_0));
        assertTrue(!subScape2.contains(agent2_1));
        assertTrue(!subScape2.contains(agent2_2));
        assertTrue(!subScape2.contains(agent2_3));
        assertTrue(!subScape2.contains(agent2_4));

        assertTrue(!superSuperScape.contains(agent1_0));
        assertTrue(superSuperScape.contains(agent1_1));
        assertTrue(superSuperScape.contains(agent1_2));
        assertTrue(superSuperScape.contains(agent1_3));
        assertTrue(superSuperScape.contains(agent1_4));
        assertTrue(superSuperScape.contains(agent2_0));
        assertTrue(superSuperScape.contains(agent2_1));
        assertTrue(superSuperScape.contains(agent2_2));
        assertTrue(superSuperScape.contains(agent2_3));
        assertTrue(superSuperScape.contains(agent2_4));

        subScape1.remove(agent2_1);
        subScape2.add(agent2_1);
        assertTrue(subScape2.contains(agent2_1));
        assertTrue(superSuperScape.contains(agent2_1));

        subScape1.add(agent2_1);
        subScape1.add(agent2_2);
        subScape2.add(agent2_2);
        subScape1.retainAll(subScape2);

        assertTrue(!subScape1.contains(agent1_0));
        assertTrue(!subScape1.contains(agent1_1));
        assertTrue(!subScape1.contains(agent1_2));
        assertTrue(!subScape1.contains(agent1_3));
        assertTrue(!subScape1.contains(agent1_4));
        assertTrue(!subScape1.contains(agent2_0));
        assertTrue(subScape1.contains(agent2_1));
        assertTrue(subScape1.contains(agent2_2));
        assertTrue(!subScape1.contains(agent2_3));
        assertTrue(!subScape1.contains(agent2_4));

        assertTrue(!subScape2.contains(agent2_0));
        assertTrue(subScape2.contains(agent2_1));
        assertTrue(subScape2.contains(agent2_2));
        assertTrue(!subScape2.contains(agent2_3));
        assertTrue(!subScape2.contains(agent2_4));

        assertTrue(!superSuperScape.contains(agent1_0));
        assertTrue(!superSuperScape.contains(agent1_1));
        assertTrue(!superSuperScape.contains(agent1_2));
        assertTrue(!superSuperScape.contains(agent1_3));
        assertTrue(!superSuperScape.contains(agent1_4));
        assertTrue(!superSuperScape.contains(agent2_0));
        assertTrue(superSuperScape.contains(agent2_1));
        assertTrue(superSuperScape.contains(agent2_2));
        assertTrue(!superSuperScape.contains(agent2_3));
        assertTrue(!superSuperScape.contains(agent2_4));
    }

    public void testNestedIteratorDeletion() {
        Scape superScape = new Scape();
        superScape.setName("Super");
        Scape subScape = new Scape(new SubListSpace());
        subScape.setName("Sub");
        subScape.setSuperScape(superScape);
        Scape subSubScape = new Scape(new SubListSpace());
        subSubScape.setName("SubSub");
        subSubScape.setSuperScape(subScape);
        LocatedAgent proto = new LocatedAgent();
        proto.setName("LocatedAgent");
        subSubScape.setPrototypeAgent(proto);

        subSubScape.setExtent(5);
        subSubScape.createScape();
        LocatedAgent agent2_0 = (LocatedAgent) subSubScape.get(0);
        LocatedAgent agent2_1 = (LocatedAgent) subSubScape.get(1);
        LocatedAgent agent2_2 = (LocatedAgent) subSubScape.get(2);
        LocatedAgent agent2_3 = (LocatedAgent) subSubScape.get(3);
        LocatedAgent agent2_4 = (LocatedAgent) subSubScape.get(4);

        subSubScape.remove(agent2_1);

        assertTrue(subSubScape.contains(agent2_0));
        assertTrue(!subSubScape.contains(agent2_1));
        assertTrue(subSubScape.contains(agent2_2));
        assertTrue(subSubScape.contains(agent2_3));
        assertTrue(subSubScape.contains(agent2_4));

        assertTrue(subScape.contains(agent2_0));
        assertTrue(!subScape.contains(agent2_1));
        assertTrue(subScape.contains(agent2_2));
        assertTrue(subScape.contains(agent2_3));
        assertTrue(subScape.contains(agent2_4));

        assertTrue(superScape.contains(agent2_0));
        assertTrue(!superScape.contains(agent2_1));
        assertTrue(superScape.contains(agent2_2));
        assertTrue(superScape.contains(agent2_3));
        assertTrue(superScape.contains(agent2_4));

        assertTrue(subSubScape.size() == 4);
        assertTrue(subScape.size() == 4);
        assertTrue(superScape.size() == 4);

        Iterator testIter3 = subSubScape.iterator();
        int subSubCount = 0;
        while (testIter3.hasNext()) {
            subSubCount++;
            testIter3.next();
        }
        assertTrue(subSubCount == 4);

        Iterator testIter2 = superScape.iterator();
        int superCount = 0;
        while (testIter2.hasNext()) {
            superCount++;
            testIter2.next();
        }
        assertTrue(superCount == 4);

        Iterator testIter = subScape.iterator();
        int subCount = 0;
        while (testIter.hasNext()) {
            subCount++;
            testIter.next();
        }
        assertTrue(subCount == 4);
    }



    /*
     * The following tests simply copy scape list tests
     */

    private boolean sweepExpected;

    public void testSweepRemove() {
        sweepExpected = false;
        Scape testScape = new Scape(new SubListSpace()) {
            @SuppressWarnings("unused")
            public void deleteSweep() {
                //If this is callled and not expected, then fail..
                assertTrue(sweepExpected || !((Mutable) getSpace()).isDeleteSweepNeeded());
                ((Mutable) getSpace()).deleteSweep();
            }
        };
        testScape.setSuperScape(new Scape());
        testScape.setExtent(10);
        testScape.createScape();

        sweepExpected = false;
        assertTrue(testScape.getSize() == 10);
        sweepExpected = false;

        testScape.remove(testScape.get(3));
        assertTrue(testScape.getSize() == 9);
        sweepExpected = true;
        testScape.iterator();
        sweepExpected = false;

        testScape.remove(testScape.get(3));
        sweepExpected = true;
        testScape.get(4);
        sweepExpected = false;
        Cell new3 = (Cell) testScape.get(4);
        testScape.remove(testScape.get(3));
        sweepExpected = true;
        assertTrue(testScape.get(3) == new3);
        sweepExpected = false;
        assertTrue(testScape.getSize() == 7);
        assertTrue(testScape.size() == 7);
    }

    public void testRemovalFromSeperateScape() {
        Scape testScape1 = new Scape(new SubListSpace());
        testScape1.setSuperScape(new Scape());
        Cell cell_1_1 = new Cell();
        testScape1.add(cell_1_1);
        Cell cell_1_2 = new Cell();
        testScape1.add(cell_1_2);
        Cell cell_1_3 = new Cell();
        testScape1.add(cell_1_3);
        Cell cell_1_4 = new Cell();
        testScape1.add(cell_1_4);
        Scape testScape2 = new Scape(new SubListSpace());
        testScape2.setSuperScape(new Scape());
        //testScape2 becomes the new primary scape, but we do not want the cell 1 1 to be removed from scape 1
        testScape2.add(cell_1_1);
        testScape2.add(cell_1_2);
        testScape2.remove(cell_1_1);

        testScape1.remove(cell_1_3);
        testScape1.get(0);
        assertTrue(testScape1.getSize() == 3);
        assertTrue(testScape1.get(0) == cell_1_1);
    }

    public void testSweepCoordinate() {
        sweepExpected = false;
        Scape testScape = new Scape(new SubListSpace()) {
            @SuppressWarnings("unused")
            public void coordinateSweep() {
                //If this is callled and not expected, then fail..
                assertTrue(sweepExpected || !((CoordinateMutable) getSpace()).isCoordinateSweepNeeded());
                ((CoordinateMutable) getSpace()).coordinateSweep();
            }
        };
        testScape.setSuperScape(new Scape());
        testScape.setExtent(10);
        testScape.createScape();

        sweepExpected = true;
        assertTrue(((Coordinate1DDiscrete) ((Cell) testScape.get(3)).getCoordinate()).getXValue() == 3);
        //Sweep should only happen the _first_ time we call getCoordinate..
        sweepExpected = false;
        for (int i = 0; i < testScape.getSize(); i++) {
            assertTrue(((Coordinate1DDiscrete) ((Cell) testScape.get(i)).getCoordinate()).getXValue() == i);
        }

        Cell foo = new Cell();
        testScape.add(foo);
        sweepExpected = true;
        assertTrue(((Coordinate1DDiscrete) ((Cell) testScape.get(3)).getCoordinate()).getXValue() == 3);
        //Sweep should only happen the _first_ time we call getCoordinate..
        sweepExpected = false;
        for (int i = 0; i < testScape.getSize(); i++) {
            assertTrue(((Coordinate1DDiscrete) ((Cell) testScape.get(i)).getCoordinate()).getXValue() == i);
        }

        testScape.remove(foo);
        sweepExpected = true;
        assertTrue(((Coordinate1DDiscrete) ((Cell) testScape.get(3)).getCoordinate()).getXValue() == 3);
        //Sweep should only happen the _first_ time we call getCoordinate..
        sweepExpected = false;
        for (int i = 0; i < testScape.getSize(); i++) {
            assertTrue(((Coordinate1DDiscrete) ((Cell) testScape.get(i)).getCoordinate()).getXValue() == i);
        }

        foo = new Cell();
        testScape.add(8, foo);
        sweepExpected = true;
        assertTrue(((Coordinate1DDiscrete) ((Cell) testScape.get(3)).getCoordinate()).getXValue() == 3);
        //Sweep should only happen the _first_ time we call getCoordinate..
        sweepExpected = false;
        for (int i = 0; i < testScape.getSize(); i++) {
            assertTrue(((Coordinate1DDiscrete) ((Cell) testScape.get(i)).getCoordinate()).getXValue() == i);
        }

        testScape.remove(foo);
        sweepExpected = true;
        assertTrue(((Coordinate1DDiscrete) ((Cell) testScape.get(3)).getCoordinate()).getXValue() == 3);
        //Sweep should only happen the _first_ time we call getCoordinate..
        sweepExpected = false;
        for (int i = 0; i < testScape.getSize(); i++) {
            assertTrue(((Coordinate1DDiscrete) ((Cell) testScape.get(i)).getCoordinate()).getXValue() == i);
        }
    }

    // jmiller 8/6/01
    // changed setCell(cell, boolean)
    public void testAdditions() {
        Scape testScape = new Scape(new SubListSpace());
        testScape.setSuperScape(new Scape());
        Scape testArray = new Scape(new Array1D());
        testScape.setPrototypeAgent(new Cell());
        testScape.setExtent(3);
        testArray.setExtent(3);
        testScape.createScape();
        testArray.createScape();
        Cell testCell = (Cell) testScape.get(0);
        testArray.set(0, testCell);
        assertTrue(testCell.getScape() == testArray);
        Cell testCell2 = (Cell) testScape.get(1);
        testArray.set(1, testCell2, false);
        assertTrue(testCell2.getScape() == testScape);
    }

    public void testAddItemAtCapability() {
        Scape testScape = new Scape(new SubListSpace());
        testScape.setSuperScape(new Scape());
        testScape.setExtent(10);
        testScape.createScape();
        Cell testCell0 = new Cell();
        testScape.add(3, testCell0);
        assertTrue(testScape.getSize() == 11);
        assertTrue(((Coordinate1DDiscrete) testScape.getExtent()).getXValue() == 11);
        assertTrue(testScape.get(3) == testCell0);

        Cell testCell1 = new Cell();
        testScape.add(0, testCell1);
        assertTrue(testScape.getSize() == 12);
        assertTrue(testScape.get(0) == testCell1);

        Cell testCell2 = new Cell();
        testScape.add(12, testCell2);
        assertTrue(testScape.getSize() == 13);
        assertTrue(testScape.get(12) == testCell2);

        Cell testCell3 = new Cell();
        testScape.add(12, testCell3);
        assertTrue(testScape.getSize() == 14);
        assertTrue(testScape.get(12) == testCell3);
    }

    public void testFindRandomCell() {
        Scape testScape = new Scape(new SubListSpace());
        testScape.setSuperScape(new Scape());
        testScape.setExtent(10);
        testScape.createScape();
        LocatedAgent t = (LocatedAgent) testScape.findRandom();
        assertNotNull(t);
        int count = 0;
        while (testScape.getSize() > 0) {
            testScape.remove(testScape.findRandom());
            count++;
        }
        assertTrue(count == 10);
        t = (LocatedAgent) testScape.findRandom();
        assertNull(t);
    }

    class TestCell extends Cell {

        public int number;
    }

    public void testListIterations() {
        Scape testScape = new Scape(new SubListSpace());
        testScape.setSuperScape(new Scape());
        testScape.setExtent(20);
        testScape.setPrototypeAgent(new TestCell());
        testScape.createScape();
        Iterator iter = testScape.iterator();
        while (iter.hasNext()) {
            TestCell cell = (TestCell) iter.next();
            cell.number++;
        }
        iter = testScape.getSpace().safeIterator();
        while (iter.hasNext()) {
            TestCell cell = (TestCell) iter.next();
            cell.number++;
        }
        iter = testScape.getSpace().safeRandomIterator();
        while (iter.hasNext()) {
            TestCell cell = (TestCell) iter.next();
            cell.number++;
        }
        for (int i = 0; i < testScape.getSize(); i++) {
            assertTrue(((TestCell) testScape.get(i)).number == 3);
        }
        Collections.shuffle((List) testScape.getSpace());
        iter = testScape.iterator();
        while (iter.hasNext()) {
            TestCell cell = (TestCell) iter.next();
            cell.number++;
        }
        for (int i = 0; i < testScape.getSize(); i++) {
            assertTrue(((TestCell) testScape.get(i)).number == 4);
        }

        //test remove add behavior for scape iterator..
        TestCell removed = (TestCell) testScape.remove(0);
        testScape.add(new TestCell());
        iter = testScape.getSpace().safeIterator();
        while (iter.hasNext()) {
            TestCell cell = (TestCell) iter.next();
            cell.number++;
        }
        for (int i = 0; i < testScape.getSize() - 1; i++) {
            assertTrue(((TestCell) testScape.get(i)).number == 5);
        }
        assertTrue(((TestCell) testScape.get(19)).number == 1);
        assertTrue(removed.number == 4);

        //test remove add behavior for scape iterator..
        TestCell removed2 = (TestCell) testScape.remove(12);
        testScape.add(new TestCell(), false);
        iter = testScape.getSpace().safeIterator();
        while (iter.hasNext()) {
            TestCell cell = (TestCell) iter.next();
            cell.number++;
        }
        for (int i = 0; i < testScape.getSize() - 2; i++) {
            assertTrue(((TestCell) testScape.get(i)).number == 6);
        }
        assertTrue(((TestCell) testScape.get(18)).number == 2);
        assertTrue(((TestCell) testScape.get(19)).number == 1);
        assertTrue(removed2.number == 5);

        //test add behavior for backing iterator..
        iter = testScape.iterator();
        TestCell removed3 = (TestCell) testScape.remove(0);
        //No add, because that would cause a concurrent modification error
        while (iter.hasNext()) {
            TestCell cell = (TestCell) iter.next();
            cell.number++;
        }
        for (int i = 0; i < testScape.getSize() - 2; i++) {
            assertTrue(((TestCell) testScape.get(i)).number == 7);
        }
        assertTrue(removed.number == 4);
        assertTrue(removed3.number == 7);
    }

    public void testSort() {
        final Scape testScape = new Scape(new SubListSpace());
        testScape.setSuperScape(new Scape());
        testScape.setExtent(20);
        testScape.setPrototypeAgent(new TestCell());
        testScape.createScape();

        Iterator iter = testScape.getSpace().safeIterator();
        int index = 0;
        while (iter.hasNext()) {
            TestCell cell = (TestCell) iter.next();
            cell.number = index++;
        }
        for (int i = 0; i < testScape.getSize(); i++) {
            assertTrue(((TestCell) testScape.get(i)).number == i);
        }
        Collections.shuffle((List) testScape.getSpace());
        Collections.shuffle((List) testScape.getSpace());
        Comparator reverseOrder = new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((TestCell) o2).number - ((TestCell) o1).number;
            }

            public boolean equals(Object o) {
                return false;
            }
        };
        Collections.sort((List) testScape.getSpace(), reverseOrder);
        testScape.executeOnMembers(new Rule("Test Ordering and Rule") {
            int i = 0;

            public void execute(Agent a) {
                assertTrue(((TestCell) testScape.get(i)).number == testScape.getSize() - i - 1);
                i++;
            }

            @SuppressWarnings("unused")
            public boolean isRandom() {
                return false;
            }
        });
    }

    public void testScapeAddAndRemove() {
        Scape testScape = new Scape(new SubListSpace());
        testScape.setSuperScape(new Scape());
        testScape.setExtent(8);
        testScape.createScape();
        LocatedAgent agent0 = (LocatedAgent) testScape.get(0);
        LocatedAgent agent1 = (LocatedAgent) testScape.get(1);
        LocatedAgent agent2 = (LocatedAgent) testScape.get(2);
        LocatedAgent agent3 = (LocatedAgent) testScape.get(3);
        LocatedAgent agent4 = (LocatedAgent) testScape.get(4);
        LocatedAgent agent5 = (LocatedAgent) testScape.get(5);
        LocatedAgent agent6 = (LocatedAgent) testScape.get(6);
        LocatedAgent agent7 = (LocatedAgent) testScape.get(7);

        testScape.remove(agent1);
        assertTrue(testScape.contains(agent0));
        assertTrue(!testScape.contains(agent1));
        assertTrue(testScape.contains(agent2));
        assertTrue(testScape.contains(agent3));
        assertTrue(testScape.contains(agent4));
        assertTrue(testScape.contains(agent5));
        assertTrue(testScape.contains(agent6));
        assertTrue(testScape.contains(agent7));

        Scape testScape2 = new Scape(new SubListSpace());
        testScape2.setSuperScape(new Scape());
        //Now, agent one's parent is another list..
        testScape2.add(agent1);
        testScape2.remove(agent1);

        assertTrue(!testScape2.contains(agent1));

        testScape.add(agent1);
        assertTrue(testScape.contains(agent0));
        assertTrue(testScape.contains(agent1));
        assertTrue(testScape.contains(agent2));
        assertTrue(testScape.contains(agent3));
        assertTrue(testScape.contains(agent4));
        assertTrue(testScape.contains(agent5));
        assertTrue(testScape.contains(agent6));
        assertTrue(testScape.contains(agent7));

        assertTrue(!testScape2.contains(agent1));

        testScape.remove(agent3);
        testScape.add(agent3);

        //add some extraneous duplciate agents
        testScape.add(agent1);

        assertTrue(testScape.contains(agent0));
        assertTrue(testScape.contains(agent1));
        assertTrue(testScape.contains(agent2));
        assertTrue(testScape.contains(agent3));
        assertTrue(testScape.contains(agent4));
        assertTrue(testScape.contains(agent5));
        assertTrue(testScape.contains(agent6));
        assertTrue(testScape.contains(agent7));

        assertTrue(!testScape2.contains(agent1));

        testScape.add(agent3);
        assertTrue(testScape.contains(agent3));
    }

    public void testScapeAddAndRemoveNotParent() {
        Scape testScape = new Scape(new SubListSpace());
        testScape.setSuperScape(new Scape());
        testScape.createScape();
        LocatedAgent agent0 = new LocatedAgent();
        testScape.add(agent0, false);
        LocatedAgent agent1 = new LocatedAgent();
        testScape.add(agent1, false);
        LocatedAgent agent2 = new LocatedAgent();
        testScape.add(agent2, false);
        LocatedAgent agent3 = new LocatedAgent();
        testScape.add(agent3, false);
        LocatedAgent agent4 = new LocatedAgent();
        testScape.add(agent4, false);
        LocatedAgent agent5 = new LocatedAgent();
        testScape.add(agent5, false);
        LocatedAgent agent6 = new LocatedAgent();
        testScape.add(agent6, false);
        LocatedAgent agent7 = new LocatedAgent();
        testScape.add(agent7, false);

        testScape.remove(agent1);
        assertTrue(testScape.contains(agent0));
        assertTrue(!testScape.contains(agent1));
        assertTrue(testScape.contains(agent2));
        assertTrue(testScape.contains(agent3));
        assertTrue(testScape.contains(agent4));
        assertTrue(testScape.contains(agent5));
        assertTrue(testScape.contains(agent6));
        assertTrue(testScape.contains(agent7));

        Scape testScape2 = new Scape(new SubListSpace());
        testScape2.setSuperScape(new Scape());
        //Now, agent one's parent is another list..
        testScape2.add(agent1);
        testScape2.remove(agent1);

        assertTrue(!testScape2.contains(agent1));

        testScape.add(agent1);
        assertTrue(testScape.contains(agent0));
        assertTrue(testScape.contains(agent1));
        assertTrue(testScape.contains(agent2));
        assertTrue(testScape.contains(agent3));
        assertTrue(testScape.contains(agent4));
        assertTrue(testScape.contains(agent5));
        assertTrue(testScape.contains(agent6));
        assertTrue(testScape.contains(agent7));

        assertTrue(!testScape2.contains(agent1));

        testScape.remove(agent3);
        testScape.add(agent3);

        //add some extraneous duplciate agents
        testScape.add(agent1);

        assertTrue(testScape.contains(agent0));
        assertTrue(testScape.contains(agent1));
        assertTrue(testScape.contains(agent2));
        assertTrue(testScape.contains(agent3));
        assertTrue(testScape.contains(agent4));
        assertTrue(testScape.contains(agent5));
        assertTrue(testScape.contains(agent6));
        assertTrue(testScape.contains(agent7));

        assertTrue(!testScape2.contains(agent1));

        testScape.add(agent3);
        assertTrue(testScape.contains(agent3));
    }
}