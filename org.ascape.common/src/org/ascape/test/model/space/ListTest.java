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
import org.ascape.model.space.ListSpace;
import org.ascape.util.Conditional;

public class ListTest extends TestCase {

    public ListTest(String name) {
        super(name);
    }

    private boolean sweepExpected;


    public void testAddAll() {
        Scape testScape1 = new Scape();
        testScape1.setExtent(5);
        testScape1.createScape();
        Scape testScape2 = new Scape();
        testScape2.setExtent(5);
        testScape2.createScape();
        assertTrue(testScape1.size() == 5);
        assertTrue(testScape2.size() == 5);
        testScape2.addAll(testScape1);
        for (int i = 0; i < testScape1.size(); i++) {
            assertTrue(testScape2.contains(testScape1.get(i)));
        }
        assertTrue(testScape1.size() == 5);
        assertTrue(testScape2.size() == 10);
    }

    public void testSweepRemove() {
        sweepExpected = false;
        Scape testScape = new Scape();
        testScape.setSpace(new ListSpace() {
            public void deleteSweep() {
                //If this is callled and not expected, then fail..
                assertTrue(sweepExpected || !isDeleteSweepNeeded());
                super.deleteSweep();
            }
        });
        testScape.setExtent(10);
        testScape.createScape();

        sweepExpected = false;
        assertTrue(testScape.getSize() == 10);
        sweepExpected = false;

        testScape.remove(((ListSpace) testScape.getSpace()).get(3));
        assertTrue(testScape.getSize() == 9);
        sweepExpected = true;
        testScape.iterator();
        sweepExpected = false;

        testScape.remove(((ListSpace) testScape.getSpace()).get(3));
        sweepExpected = true;
        ((ListSpace) testScape.getSpace()).get(4);
        sweepExpected = false;
        Cell new3 = (Cell) ((ListSpace) testScape.getSpace()).get(4);
        testScape.remove(((ListSpace) testScape.getSpace()).get(3));
        sweepExpected = true;
        assertTrue(((ListSpace) testScape.getSpace()).get(3) == new3);
        sweepExpected = false;
        assertTrue(testScape.getSize() == 7);
        assertTrue(testScape.size() == 7);
    }

    public void testRemovalFromSeperateScape() {
        Scape testScape1 = new Scape();
        Cell cell_1_1 = new Cell();
        testScape1.add(cell_1_1);
        Cell cell_1_2 = new Cell();
        testScape1.add(cell_1_2);
        Cell cell_1_3 = new Cell();
        testScape1.add(cell_1_3);
        Cell cell_1_4 = new Cell();
        testScape1.add(cell_1_4);
        Scape testScape2 = new Scape();
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
        Scape testScape = new Scape();
        testScape.setSpace(new ListSpace() {
            public void coordinateSweep() {
                //If this is callled and not expected, then fail..
                assertTrue(sweepExpected || !isCoordinateSweepNeeded());
                super.coordinateSweep();
            }
        });
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
        Scape testScape = new Scape();
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
        Scape testScape = new Scape();
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
        Scape testScape = new Scape();
        testScape.setExtent(10);
        testScape.createScape();
        Agent t = testScape.findRandom();
        assertNotNull(t);
        int count = 0;
        while (testScape.getSize() > 0) {
            testScape.remove(testScape.findRandom());
            count++;
        }
        assertTrue(count == 10);
        t = testScape.findRandom();
        assertNull(t);
    }

    class TestAgent extends LocatedAgent {

        int number;

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }
    }

    public void testFindRandomWithConditional() {
        Scape testScape = new Scape();
        testScape.setPrototypeAgent(new TestAgent());
        testScape.setExtent(10);
        testScape.createScape();
        Conditional condition = new Conditional() {
            public boolean meetsCondition(Object o) {
                return ((TestAgent) o).getNumber() == 5;
            }
        };
        // set all agents numbers to 9, and one to 5
        for (Iterator it = testScape.iterator(); it.hasNext();) {
            ((TestAgent) it.next()).setNumber(9);
        }
        TestAgent five = (TestAgent) testScape.get(testScape.randomToLimit(testScape.size()));
        five.setNumber(5);

        assertTrue(testScape.findRandom(condition) == five);

        for (Iterator it = testScape.iterator(); it.hasNext();) {
            ((TestAgent) it.next()).setNumber(9);
        }
        assertTrue(testScape.findRandom(condition) == null);
    }

    class TestCell extends Cell {

        public int number;
    }

    public void testScapeIterations() {
        Scape testScape = new Scape();
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
        final Scape testScape = new Scape();
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
        Scape testScape = new Scape();
        testScape.setExtent(8);
        testScape.createScape();
        Agent agent0 = (Agent) testScape.get(0);
        Agent agent1 = (Agent) testScape.get(1);
        Agent agent2 = (Agent) testScape.get(2);
        Agent agent3 = (Agent) testScape.get(3);
        Agent agent4 = (Agent) testScape.get(4);
        Agent agent5 = (Agent) testScape.get(5);
        Agent agent6 = (Agent) testScape.get(6);
        Agent agent7 = (Agent) testScape.get(7);

        testScape.remove(agent1);
        assertTrue(testScape.contains(agent0));
        assertTrue(!testScape.contains(agent1));
        assertTrue(testScape.contains(agent2));
        assertTrue(testScape.contains(agent3));
        assertTrue(testScape.contains(agent4));
        assertTrue(testScape.contains(agent5));
        assertTrue(testScape.contains(agent6));
        assertTrue(testScape.contains(agent7));

        Scape testScape2 = new Scape();
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
        Scape testScape = new Scape();
        testScape.createScape();
        Cell agent0 = new Cell();
        testScape.add(agent0, false);
        Cell agent1 = new Cell();
        testScape.add(agent1, false);
        Cell agent2 = new Cell();
        testScape.add(agent2, false);
        Cell agent3 = new Cell();
        testScape.add(agent3, false);
        Cell agent4 = new Cell();
        testScape.add(agent4, false);
        Cell agent5 = new Cell();
        testScape.add(agent5, false);
        Cell agent6 = new Cell();
        testScape.add(agent6, false);
        Cell agent7 = new Cell();
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

        Scape testScape2 = new Scape();
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

    /**
     * This is just a simple test by inspection to see that the random itererator _appears_ to be working correctly.
     * We are not attempting to verify conclusivly that a random order exists but will just test by examing list of numbers.
     * Since this requires someone to actually look at the list generated, it will not be included in automated tests,
     * but will be commented out.
     * If someone has a suggestion for properly automating this, please propse it.
     */
    /*public void testRandomOrder() {
        final Scape testScape = new Scape();
        testScape.setExtent(20);
        testScape.setPrototypeAgent(new TestCell());
        testScape.createScape();

        Iterator iter = testScape.scapeRandomIterator();
        int index = 0;
        while(iter.hasNext()) {
            TestCell cell = (TestCell) iter.next();
            cell.number = index++;
        }
        for (int i = 0; i < testScape.getSize(); i++) {
            System.out.println(((TestCell) testScape.get(i)).number);
        }
    }*/
}