/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.test.model.space;

import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;

import org.ascape.model.Agent;
import org.ascape.model.Cell;
import org.ascape.model.HostCell;
import org.ascape.model.Scape;
import org.ascape.model.rule.Rule;
import org.ascape.model.space.Array1D;
import org.ascape.model.space.Array2D;
import org.ascape.model.space.Array2DEuclidian;
import org.ascape.model.space.Array2DMoore;
import org.ascape.model.space.Array2DVonNeumann;
import org.ascape.model.space.Coordinate2DDiscrete;
import org.ascape.model.space.Node;
import org.ascape.util.Conditional;
import org.ascape.util.ResetableIterator;

public class Array2DTest extends TestCase {

    Scape ts;

    public Array2DTest(String name) {
        super(name);
    }

    class TestCell extends Cell {

        public boolean testState;

        public void initialize() {
            testState = false;
        }
    }

    // jmiller 8/6/01
    // changed setCell(cell, boolean)
    public void testAdditions() {
        Scape testScape2D = new Scape(new Array2DVonNeumann());
        Scape testScape1D = new Scape(new Array1D());
        testScape2D.setPrototypeAgent(new Cell());
        testScape2D.setExtent(3, 3);
        testScape1D.setExtent(3);
        testScape2D.createScape();
        testScape1D.createScape();
        Cell testCell = (Cell) testScape2D.get(new Coordinate2DDiscrete(0, 0));
        testScape1D.set(0, testCell);
        assertTrue(testCell.getScape() == testScape1D);
        Cell testCell2 = (Cell) ((Array2D) testScape2D.getSpace()).get(1, 1);
        testScape1D.set(1, testCell2, false);
        assertTrue(testCell2.getScape() == testScape2D);
    }

    public void testIterators() {
        Scape testScape = new Scape(new Array2DVonNeumann());
        testScape.setExtent(3, 3);
        testScape.createScape();
        ResetableIterator iter = testScape.scapeIterator();
        Vector targets = new Vector();
        targets.addElement(new Coordinate2DDiscrete(0, 0));
        targets.addElement(new Coordinate2DDiscrete(0, 1));
        targets.addElement(new Coordinate2DDiscrete(0, 2));
        targets.addElement(new Coordinate2DDiscrete(1, 0));
        targets.addElement(new Coordinate2DDiscrete(1, 1));
        targets.addElement(new Coordinate2DDiscrete(1, 2));
        targets.addElement(new Coordinate2DDiscrete(2, 0));
        targets.addElement(new Coordinate2DDiscrete(2, 1));
        targets.addElement(new Coordinate2DDiscrete(2, 2));
        int index = 0;
        while (iter.hasNext()) {
            Coordinate2DDiscrete comp = ((Coordinate2DDiscrete) ((Cell) iter.next()).getCoordinate());
            assertTrue(comp.equals(targets.elementAt(index)));
            index++;
        }
        assertTrue(index == 9);
        ResetableIterator randomIter = testScape.scapeRandomIterator();
        index = 0;
        while (randomIter.hasNext()) {
            Coordinate2DDiscrete comp = ((Coordinate2DDiscrete) ((Cell) randomIter.next()).getCoordinate());
            assertTrue(targets.remove(comp));
            index++;
        }
        assertTrue(index == 9);
        assertTrue(targets.size() == 0);

        testScape = new Scape(new Array2DVonNeumann());
        testScape.setExtent(3, 2);
        testScape.createScape();
        iter = testScape.scapeIterator();
        targets = new Vector();
        targets.addElement(new Coordinate2DDiscrete(0, 0));
        targets.addElement(new Coordinate2DDiscrete(0, 1));
        targets.addElement(new Coordinate2DDiscrete(1, 0));
        targets.addElement(new Coordinate2DDiscrete(1, 1));
        targets.addElement(new Coordinate2DDiscrete(2, 0));
        targets.addElement(new Coordinate2DDiscrete(2, 1));
        index = 0;
        while (iter.hasNext()) {
            Coordinate2DDiscrete comp = ((Coordinate2DDiscrete) ((Cell) iter.next()).getCoordinate());
            assertTrue(comp.equals(targets.elementAt(index)));
            index++;
        }
        assertTrue(index == 6);
        randomIter = testScape.scapeRandomIterator();
        index = 0;
        while (randomIter.hasNext()) {
            Coordinate2DDiscrete comp = ((Coordinate2DDiscrete) ((Cell) randomIter.next()).getCoordinate());
            assertTrue(targets.remove(comp));
            index++;
        }
        assertTrue(index == 6);
        assertTrue(targets.size() == 0);

        testScape = new Scape(new Array2DVonNeumann());
        testScape.setExtent(2, 3);
        testScape.createScape();
        iter = testScape.scapeIterator();
        targets = new Vector();
        targets.addElement(new Coordinate2DDiscrete(0, 0));
        targets.addElement(new Coordinate2DDiscrete(0, 1));
        targets.addElement(new Coordinate2DDiscrete(0, 2));
        targets.addElement(new Coordinate2DDiscrete(1, 0));
        targets.addElement(new Coordinate2DDiscrete(1, 1));
        targets.addElement(new Coordinate2DDiscrete(1, 2));
        index = 0;
        while (iter.hasNext()) {
            Coordinate2DDiscrete comp = ((Coordinate2DDiscrete) ((Cell) iter.next()).getCoordinate());
            assertTrue(comp.equals(targets.elementAt(index)));
            index++;
        }
        assertTrue(index == 6);
        randomIter = testScape.scapeRandomIterator();
        index = 0;
        while (randomIter.hasNext()) {
            Coordinate2DDiscrete comp = ((Coordinate2DDiscrete) ((Cell) randomIter.next()).getCoordinate());
            assertTrue(targets.remove(comp));
            index++;
        }
        assertTrue(index == 6);
        assertTrue(targets.size() == 0);

        testScape = new Scape(new Array2DVonNeumann());
        testScape.setExtent(1, 3);
        testScape.createScape();
        iter = testScape.scapeIterator();
        targets = new Vector();
        targets.addElement(new Coordinate2DDiscrete(0, 0));
        targets.addElement(new Coordinate2DDiscrete(0, 1));
        targets.addElement(new Coordinate2DDiscrete(0, 2));
        index = 0;
        while (iter.hasNext()) {
            Coordinate2DDiscrete comp = ((Coordinate2DDiscrete) ((Cell) iter.next()).getCoordinate());
            assertTrue(comp.equals(targets.elementAt(index)));
            index++;
        }
        assertTrue(index == 3);
        randomIter = testScape.scapeRandomIterator();
        index = 0;
        while (randomIter.hasNext()) {
            Coordinate2DDiscrete comp = ((Coordinate2DDiscrete) ((Cell) randomIter.next()).getCoordinate());
            assertTrue(targets.remove(comp));
            index++;
        }
        assertTrue(index == 3);
        assertTrue(targets.size() == 0);

        testScape = new Scape(new Array2DVonNeumann());
        testScape.setExtent(3, 1);
        testScape.createScape();
        iter = testScape.scapeIterator();
        targets = new Vector();
        targets.addElement(new Coordinate2DDiscrete(0, 0));
        targets.addElement(new Coordinate2DDiscrete(1, 0));
        targets.addElement(new Coordinate2DDiscrete(2, 0));
        index = 0;
        while (iter.hasNext()) {
            Coordinate2DDiscrete comp = ((Coordinate2DDiscrete) ((Cell) iter.next()).getCoordinate());
            assertTrue(comp.equals(targets.elementAt(index)));
            index++;
        }
        assertTrue(index == 3);
        randomIter = testScape.scapeRandomIterator();
        index = 0;
        while (randomIter.hasNext()) {
            Coordinate2DDiscrete comp = ((Coordinate2DDiscrete) ((Cell) randomIter.next()).getCoordinate());
            assertTrue(targets.remove(comp));
            index++;
        }
        assertTrue(index == 3);
        assertTrue(targets.size() == 0);
    }

    public void testGetCells() {
        setUpVonNeumann(100, 100, true);
        ts.createScape();
        ts.execute(Scape.INITIALIZE_RULE);
        Node[] testCells = ((Array2D) ts.getSpace()).getCells();
        for (int i = 0; i < testCells.length; i++) {
            ((TestCell) testCells[i]).testState = true;
        }
        ts.executeOnMembers(new Rule("Test") {
            public void execute(Agent a) {
                assertTrue(((TestCell) a).testState);
            }
        });
    }

    protected void setUpVonNeumann(int x, int y, boolean periodic) {
        ts = new Scape(new Array2DVonNeumann());
        ts.setExtent(x, y);
        ts.setPrototypeAgent(new TestCell());
        ts.getSpace().setPeriodic(periodic);
        ts.createScape();
        ts.execute(Scape.INITIALIZE_RULE);
    }

    public void testDistanceVonNeumann() {
        setUpVonNeumann(100, 100, true);
        ((Array2D) ts.getSpace()).setNearnessLineOfSight(false);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(10, 12), ((Array2D) ts.getSpace()).get(13, 10)) == 5);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(11, 11), ((Array2D) ts.getSpace()).get(12, 15)) == 5);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(13, 10), ((Array2D) ts.getSpace()).get(10, 12)) == 5);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(98, 10), ((Array2D) ts.getSpace()).get(1, 12)) == 5);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(10, 98), ((Array2D) ts.getSpace()).get(12, 1)) == 5);

        ts.getSpace().setPeriodic(false);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(98, 10), ((Array2D) ts.getSpace()).get(10, 1)) == 97);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(10, 50), ((Array2D) ts.getSpace()).get(50, 90)) == 80);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(10, 98), ((Array2D) ts.getSpace()).get(15, 10)) == 93);

        ((Array2D) ts.getSpace()).setNearnessLineOfSight(true);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(98, 10), ((Array2D) ts.getSpace()).get(10, 1)) == Integer.MAX_VALUE);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(1, 13), ((Array2D) ts.getSpace()).get(2, 14)) == Integer.MAX_VALUE);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(10, 50), ((Array2D) ts.getSpace()).get(10, 90)) == 40);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(67, 13), ((Array2D) ts.getSpace()).get(47, 13)) == 20);
    }

    public void testDistanceVonNeumannWidth1() {
        boolean exceptionCaught;

        setUpVonNeumann(1, 100, true);
        ((Array2D) ts.getSpace()).setNearnessLineOfSight(false);

        exceptionCaught = false;
        try {
            ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(1, 15), ((Array2D) ts.getSpace()).get(0, 15));
        } catch (ArrayIndexOutOfBoundsException e) {
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);

        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(0, 15), ((Array2D) ts.getSpace()).get(0, 15)) == 0);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(0, 15), ((Array2D) ts.getSpace()).get(0, 10)) == 5);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(0, 17), ((Array2D) ts.getSpace()).get(0, 22)) == 5);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(0, 98), ((Array2D) ts.getSpace()).get(0, 3)) == 5);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(0, 4), ((Array2D) ts.getSpace()).get(0, 99)) == 5);

        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(0, 50), ((Array2D) ts.getSpace()).get(0, 99)) == 49);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(0, 49), ((Array2D) ts.getSpace()).get(0, 99)) == 50);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(0, 48), ((Array2D) ts.getSpace()).get(0, 99)) == 49);

        ts.getSpace().setPeriodic(false);

        exceptionCaught = false;
        try {
            ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(1, 15), ((Array2D) ts.getSpace()).get(0, 15));
        } catch (ArrayIndexOutOfBoundsException e) {
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);

        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(0, 15), ((Array2D) ts.getSpace()).get(0, 15)) == 0);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(0, 98), ((Array2D) ts.getSpace()).get(0, 3)) == 95);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(0, 3), ((Array2D) ts.getSpace()).get(0, 98)) == 95);

        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(0, 50), ((Array2D) ts.getSpace()).get(0, 99)) == 49);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(0, 49), ((Array2D) ts.getSpace()).get(0, 99)) == 50);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(0, 48), ((Array2D) ts.getSpace()).get(0, 99)) == 51);

        ((Array2D) ts.getSpace()).setNearnessLineOfSight(true);

        exceptionCaught = false;
        try {
            ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(1, 15), ((Array2D) ts.getSpace()).get(0, 15));
        } catch (ArrayIndexOutOfBoundsException e) {
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);

        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(0, 15), ((Array2D) ts.getSpace()).get(0, 15)) == 0);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(0, 50), ((Array2D) ts.getSpace()).get(0, 90)) == 40);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(0, 13), ((Array2D) ts.getSpace()).get(0, 13)) == 0);
    }

    public void testDistanceVonNeumannHeight1() {
        boolean exceptionCaught;

        setUpVonNeumann(100, 1, true);
        ((Array2D) ts.getSpace()).setNearnessLineOfSight(false);

        exceptionCaught = false;
        try {
            ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(15, 1), ((Array2D) ts.getSpace()).get(15, 0));
        } catch (ArrayIndexOutOfBoundsException e) {
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);

        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(15, 0), ((Array2D) ts.getSpace()).get(15, 0)) == 0);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(15, 0), ((Array2D) ts.getSpace()).get(10, 0)) == 5);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(17, 0), ((Array2D) ts.getSpace()).get(22, 0)) == 5);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(98, 0), ((Array2D) ts.getSpace()).get(3, 0)) == 5);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(4, 0), ((Array2D) ts.getSpace()).get(99, 0)) == 5);

        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(50, 0), ((Array2D) ts.getSpace()).get(99, 0)) == 49);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(49, 0), ((Array2D) ts.getSpace()).get(99, 0)) == 50);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(48, 0), ((Array2D) ts.getSpace()).get(99, 0)) == 49);

        ts.getSpace().setPeriodic(false);

        exceptionCaught = false;
        try {
            ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(15, 1), ((Array2D) ts.getSpace()).get(15, 0));
        } catch (ArrayIndexOutOfBoundsException e) {
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);

        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(15, 0), ((Array2D) ts.getSpace()).get(15, 0)) == 0);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(98, 0), ((Array2D) ts.getSpace()).get(3, 0)) == 95);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(3, 0), ((Array2D) ts.getSpace()).get(98, 0)) == 95);

        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(50, 0), ((Array2D) ts.getSpace()).get(99, 0)) == 49);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(49, 0), ((Array2D) ts.getSpace()).get(99, 0)) == 50);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(48, 0), ((Array2D) ts.getSpace()).get(99, 0)) == 51);

        ((Array2D) ts.getSpace()).setNearnessLineOfSight(true);

        exceptionCaught = false;
        try {
            ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(15, 1), ((Array2D) ts.getSpace()).get(15, 0));
        } catch (ArrayIndexOutOfBoundsException e) {
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);

        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(15, 0), ((Array2D) ts.getSpace()).get(15, 0)) == 0);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(50, 0), ((Array2D) ts.getSpace()).get(90, 0)) == 40);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(13, 0), ((Array2D) ts.getSpace()).get(13, 0)) == 0);
    }

    public void testfindWithinVonNeumann() {
        setUpVonNeumann(100, 100, true);
        ts.execute(Scape.INITIALIZE_RULE);
        List testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 10).getCoordinate(), null, false, 0);
        for (int i = 0; i < testCells.size(); i++) {
            ((TestCell) testCells.get(i)).testState = true;
        }
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(0, 10)).testState);
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 10).getCoordinate(), null, true, 0);
        for (int i = 0; i < testCells.size(); i++) {
            ((TestCell) testCells.get(i)).testState = true;
        }
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(0, 10)).testState);
        ts.execute(Scape.INITIALIZE_RULE);

        ((Array2D) ts.getSpace()).setNearnessLineOfSight(true);
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 10).getCoordinate(), null, false, 2);
        for (int i = 0; i < testCells.size(); i++) {
            ((TestCell) testCells.get(i)).testState = true;
        }
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(99, 10)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(98, 10)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(0, 12)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(0, 8)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(0, 7)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(0, 13)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(1, 11)).testState);
        ts.execute(Scape.INITIALIZE_RULE);
//
//		ts.setNearnessLineOfSight(false);
//		testCells =  ts.findWithin(((Array2D) ts.getSpace()).get(0, 10).getCoordinate(), null, false, 2);
//		for (int i = 0; i < testCells.size(); i++) {
//			((TestCell) testCells.get(i)).testState = true;
//		}
//		assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(99, 10)).testState);
//		assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(98, 10)).testState);
//		assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(0, 12)).testState);
//		assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(0, 8)).testState);
//		assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(0, 7)).testState);
//		assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(0, 13)).testState);
//		assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(1, 11)).testState);
//		assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(99, 11)).testState);
//		assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(1, 9)).testState);
//		assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(99, 9)).testState);
//        ts.execute(Scape.INITIALIZE_RULE);

        ts.getSpace().setPeriodic(false);

        ((Array2D) ts.getSpace()).setNearnessLineOfSight(true);
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 10).getCoordinate(), null, false, 2);
        for (int i = 0; i < testCells.size(); i++) {
            ((TestCell) testCells.get(i)).testState = true;
        }
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(99, 10)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(98, 10)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(0, 8)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(0, 9)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(0, 11)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(0, 12)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(0, 7)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(0, 13)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(1, 11)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(1, 9)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(99, 11)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(99, 9)).testState);
        ts.execute(Scape.INITIALIZE_RULE);
//
//        ts.setNearnessLineOfSight(false);
//        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 10).getCoordinate(), null, false, 2);
//        for (int i = 0; i < testCells.size(); i++) {
//            ((TestCell) testCells.get(i)).testState = true;
//        }
//        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(99, 10)).testState);
//        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(98, 10)).testState);
//        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(0, 12)).testState);
//        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(0, 8)).testState);
//        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(0, 7)).testState);
//        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(0, 13)).testState);
//        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(1, 11)).testState);
//        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(99, 11)).testState);
//        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(1, 9)).testState);
//        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(99, 9)).testState);
    }

    public void testfindWithinVonNeumannWidth4Height2() {
        setUpVonNeumann(4, 2, true);
        List testCells = ts.findWithin(((Array2D) ts.getSpace()).get(2, 1).getCoordinate(), null, false, 1);
        //Should be thrre because 1 is shared
        assertTrue(testCells.size() == 3);
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(1, 1).getCoordinate(), null, false, 3);
        assertTrue(testCells.size() == 4);
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(1, 1).getCoordinate(), null, false, 2);
        assertTrue(testCells.size() == 4);
    }

    public void testfindWithinVonNeumannWidth2Height4() {
        setUpVonNeumann(2, 4, true);
        List testCells = ts.findWithin(((Array2D) ts.getSpace()).get(1, 1).getCoordinate(), null, false, 1);
        assertTrue(testCells.size() == 3);
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(1, 1).getCoordinate(), null, false, 3);
        assertTrue(testCells.size() == 4);
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(1, 1).getCoordinate(), null, false, 2);
        assertTrue(testCells.size() == 4);
    }

    public void testfindWithinVonNeumannWidth1() {
        setUpVonNeumann(1, 100, true);
        List testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 30).getCoordinate(), null, false, 1);
        assertTrue(testCells.size() == 2);
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 99).getCoordinate(), null, false, 1);
        assertTrue(testCells.size() == 2);
    }

    public void testfindWithinVonNeumannRadiusSmaller() {
        setUpVonNeumann(1, 3, true);
        List testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 1).getCoordinate(), null, false, 1);
        assertTrue(testCells.size() == 2);
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 1).getCoordinate(), null, false, 2);
        assertTrue(testCells.size() == 2);
    }


    public void setUpMoore(int x, int y) {
        ts = new Scape(new Array2DMoore());
        ts.setExtent(x, y);
        ts.setPrototypeAgent(new TestCell());
        ts.getSpace().setPeriodic(true);
        ts.createScape();
        ts.execute(Scape.INITIALIZE_RULE);
    }

    public void testDistanceMoore() {
        setUpMoore(100, 100);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(10, 12), ((Array2D) ts.getSpace()).get(13, 10)) == 3);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(11, 11), ((Array2D) ts.getSpace()).get(12, 15)) == 4);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(13, 10), ((Array2D) ts.getSpace()).get(10, 12)) == 3);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(98, 10), ((Array2D) ts.getSpace()).get(1, 12)) == 3);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(10, 98), ((Array2D) ts.getSpace()).get(12, 1)) == 3);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(99, 98), ((Array2D) ts.getSpace()).get(10, 10)) == 12);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(99, 98), ((Array2D) ts.getSpace()).get(80, 90)) == 19);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(10, 10), ((Array2D) ts.getSpace()).get(10, 15)) == 5);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(15, 10), ((Array2D) ts.getSpace()).get(10, 10)) == 5);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(10, 15), ((Array2D) ts.getSpace()).get(10, 10)) == 5);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(10, 10), ((Array2D) ts.getSpace()).get(15, 10)) == 5);

        ts.getSpace().setPeriodic(false);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(98, 10), ((Array2D) ts.getSpace()).get(10, 1)) == 88);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(10, 50), ((Array2D) ts.getSpace()).get(50, 90)) == 40);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(10, 98), ((Array2D) ts.getSpace()).get(15, 10)) == 88);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(1, 1), ((Array2D) ts.getSpace()).get(99, 99)) == 98);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(3, 1), ((Array2D) ts.getSpace()).get(1, 1)) == 2);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(0, 0), ((Array2D) ts.getSpace()).get(99, 99)) == 99);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(10, 10), ((Array2D) ts.getSpace()).get(10, 15)) == 5);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(15, 10), ((Array2D) ts.getSpace()).get(10, 10)) == 5);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(10, 15), ((Array2D) ts.getSpace()).get(10, 10)) == 5);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(10, 10), ((Array2D) ts.getSpace()).get(15, 10)) == 5);
    }

    public void testDistanceMooreWidth1() {
        boolean exceptionCaught;

        setUpMoore(1, 100);
        ((Array2D) ts.getSpace()).setNearnessLineOfSight(false);

        exceptionCaught = false;
        try {
            ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(1, 15), ((Array2D) ts.getSpace()).get(0, 15));
        } catch (ArrayIndexOutOfBoundsException e) {
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);

        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(0, 15), ((Array2D) ts.getSpace()).get(0, 15)) == 0);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(0, 15), ((Array2D) ts.getSpace()).get(0, 10)) == 5);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(0, 17), ((Array2D) ts.getSpace()).get(0, 22)) == 5);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(0, 98), ((Array2D) ts.getSpace()).get(0, 3)) == 5);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(0, 4), ((Array2D) ts.getSpace()).get(0, 99)) == 5);

        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(0, 50), ((Array2D) ts.getSpace()).get(0, 99)) == 49);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(0, 49), ((Array2D) ts.getSpace()).get(0, 99)) == 50);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(0, 48), ((Array2D) ts.getSpace()).get(0, 99)) == 49);

        ts.getSpace().setPeriodic(false);

        exceptionCaught = false;
        try {
            ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(1, 15), ((Array2D) ts.getSpace()).get(0, 15));
        } catch (ArrayIndexOutOfBoundsException e) {
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);

        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(0, 15), ((Array2D) ts.getSpace()).get(0, 15)) == 0);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(0, 98), ((Array2D) ts.getSpace()).get(0, 3)) == 95);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(0, 3), ((Array2D) ts.getSpace()).get(0, 98)) == 95);

        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(0, 50), ((Array2D) ts.getSpace()).get(0, 99)) == 49);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(0, 49), ((Array2D) ts.getSpace()).get(0, 99)) == 50);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(0, 48), ((Array2D) ts.getSpace()).get(0, 99)) == 51);

        ((Array2D) ts.getSpace()).setNearnessLineOfSight(true);

        exceptionCaught = false;
        try {
            ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(1, 15), ((Array2D) ts.getSpace()).get(0, 15));
        } catch (ArrayIndexOutOfBoundsException e) {
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);

        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(0, 15), ((Array2D) ts.getSpace()).get(0, 15)) == 0);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(0, 50), ((Array2D) ts.getSpace()).get(0, 90)) == 40);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(0, 13), ((Array2D) ts.getSpace()).get(0, 13)) == 0);
    }

    public void testDistanceMooreHeight1() {
        boolean exceptionCaught;
        setUpMoore(100, 1);
        ((Array2D) ts.getSpace()).setNearnessLineOfSight(false);

        exceptionCaught = false;
        try {
            ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(15, 1), ((Array2D) ts.getSpace()).get(15, 0));
        } catch (ArrayIndexOutOfBoundsException e) {
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);

        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(15, 0), ((Array2D) ts.getSpace()).get(15, 0)) == 0);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(15, 0), ((Array2D) ts.getSpace()).get(10, 0)) == 5);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(17, 0), ((Array2D) ts.getSpace()).get(22, 0)) == 5);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(98, 0), ((Array2D) ts.getSpace()).get(3, 0)) == 5);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(4, 0), ((Array2D) ts.getSpace()).get(99, 0)) == 5);

        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(50, 0), ((Array2D) ts.getSpace()).get(99, 0)) == 49);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(49, 0), ((Array2D) ts.getSpace()).get(99, 0)) == 50);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(48, 0), ((Array2D) ts.getSpace()).get(99, 0)) == 49);

        ts.getSpace().setPeriodic(false);

        exceptionCaught = false;
        try {
            ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(15, 1), ((Array2D) ts.getSpace()).get(15, 0));
        } catch (ArrayIndexOutOfBoundsException e) {
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);

        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(15, 0), ((Array2D) ts.getSpace()).get(15, 0)) == 0);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(98, 0), ((Array2D) ts.getSpace()).get(3, 0)) == 95);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(3, 0), ((Array2D) ts.getSpace()).get(98, 0)) == 95);

        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(50, 0), ((Array2D) ts.getSpace()).get(99, 0)) == 49);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(49, 0), ((Array2D) ts.getSpace()).get(99, 0)) == 50);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(48, 0), ((Array2D) ts.getSpace()).get(99, 0)) == 51);

        ((Array2D) ts.getSpace()).setNearnessLineOfSight(true);

        exceptionCaught = false;
        try {
            ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(15, 1), ((Array2D) ts.getSpace()).get(15, 0));
        } catch (ArrayIndexOutOfBoundsException e) {
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);

        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(15, 0), ((Array2D) ts.getSpace()).get(15, 0)) == 0);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(50, 0), ((Array2D) ts.getSpace()).get(90, 0)) == 40);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(13, 0), ((Array2D) ts.getSpace()).get(13, 0)) == 0);
    }

    public void testFindWithinMoore() {
        setUpMoore(100, 100);

        List testCells = ts.findWithin(((Array2D) ts.getSpace()).get(10, 99).getCoordinate(), null, false, 1);
        assertTrue(testCells.size() == 8);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(99, 0).getCoordinate(), null, false, 1);
        assertTrue(testCells.size() == 8);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 13).getCoordinate(), null, false, 1);
        assertTrue(testCells.size() == 8);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(13, 0).getCoordinate(), null, false, 40);
        assertTrue(testCells.size() == 6560);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(99, 99).getCoordinate(), null, false, 40);
        assertTrue(testCells.size() == 6560);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(13, 99).getCoordinate(), null, false, 40);
        assertTrue(testCells.size() == 6560);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(13, 99).getCoordinate(), null, false, 55);
        assertTrue(testCells.size() == 9999);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(98, 10).getCoordinate(), null, false, 55);
        assertTrue(testCells.size() == 9999);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(99, 99).getCoordinate(), null, false, 55);
        assertTrue(testCells.size() == 9999);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 1).getCoordinate(), null, false, 55);
        assertTrue(testCells.size() == 9999);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 10).getCoordinate(), null, true, 29);
        assertTrue(testCells.size() == 3481);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(99, 32).getCoordinate(), null, true, 101);
        assertTrue(testCells.size() == 10000);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 0).getCoordinate(), null, true, 101);
        assertTrue(testCells.size() == 10000);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(10, 99).getCoordinate(), null, true, 101);
        assertTrue(testCells.size() == 10000);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 10).getCoordinate(), null, false, 2);
        assertTrue(testCells.size() == 24);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(10, 99).getCoordinate(), null, false, 2);
        assertTrue(testCells.size() == 24);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }

        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 10).getCoordinate(), null, false, 0);
        for (int i = 0; i < testCells.size(); i++) {
            ((TestCell) testCells.get(i)).testState = true;
        }
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(0, 10)).testState);
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 10).getCoordinate(), null, true, 0);
        for (int i = 0; i < testCells.size(); i++) {
            ((TestCell) testCells.get(i)).testState = true;
        }
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(0, 10)).testState);

        ts.getSpace().setPeriodic(true);
        ts.execute(Scape.INITIALIZE_RULE);

        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(0, 10)).testState);

        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 10).getCoordinate(), null, false, 2);
        for (int i = 0; i < testCells.size(); i++) {
            ((TestCell) testCells.get(i)).testState = true;
        }
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(98, 8)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(98, 9)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(98, 10)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(98, 11)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(98, 12)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(99, 8)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(99, 9)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(99, 10)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(99, 11)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(99, 12)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(0, 12)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(1, 12)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(2, 12)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(1, 8)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(1, 9)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(1, 10)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(1, 11)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(1, 12)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(2, 12)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(2, 8)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(2, 9)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(2, 10)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(2, 11)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(2, 12)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(0, 10)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(0, 8)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(0, 7)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(0, 13)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(3, 10)).testState);

        ts.getSpace().setPeriodic(false);
        ts.createScape();
        ts.execute(Scape.INITIALIZE_RULE);

        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 10).getCoordinate(), null, false, 2);
        for (int i = 0; i < testCells.size(); i++) {
            ((TestCell) testCells.get(i)).testState = true;
        }
        ts.initialize();

        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(10, 99).getCoordinate(), null, false, 1);
        assertTrue(testCells.size() == 5);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(99, 0).getCoordinate(), null, false, 1);
        assertTrue(testCells.size() == 3);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 13).getCoordinate(), null, false, 1);
        assertTrue(testCells.size() == 5);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(13, 0).getCoordinate(), null, false, 40);
        assertTrue(testCells.size() == 2213);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 10).getCoordinate(), null, true, 29);
        assertTrue(testCells.size() == 1200);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(99, 32).getCoordinate(), null, true, 101);
        assertTrue(testCells.size() == 10000);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 0).getCoordinate(), null, true, 101);
        assertTrue(testCells.size() == 10000);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(10, 99).getCoordinate(), null, true, 101);
        assertTrue(testCells.size() == 10000);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 10).getCoordinate(), null, false, 2);
        assertTrue(testCells.size() == 14);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(10, 99).getCoordinate(), null, false, 2);
        assertTrue(testCells.size() == 14);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }

        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(98, 8)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(98, 9)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(98, 10)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(98, 11)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(98, 12)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(99, 8)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(99, 9)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(99, 10)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(99, 11)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(99, 12)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(0, 8)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(0, 9)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(0, 10)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(0, 11)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(0, 12)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(1, 8)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(1, 9)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(1, 10)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(1, 11)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(2, 12)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(2, 8)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(2, 9)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(2, 10)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(2, 11)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(2, 12)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(0, 7)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(0, 13)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(3, 10)).testState);

        assertTrue(((Array2D) ts.getSpace()).get(0, 0).findNeighbors().size() == 3);
        assertTrue(((Array2D) ts.getSpace()).get(0, 23).findNeighbors().size() == 5);
        assertTrue(((Array2D) ts.getSpace()).get(0, 99).findNeighbors().size() == 3);
        assertTrue(((Array2D) ts.getSpace()).get(99, 0).findNeighbors().size() == 3);
        assertTrue(((Array2D) ts.getSpace()).get(50, 0).findNeighbors().size() == 5);
        assertTrue(((Array2D) ts.getSpace()).get(99, 99).findNeighbors().size() == 3);
        assertTrue(((Array2D) ts.getSpace()).get(98, 99).findNeighbors().size() == 5);
        assertTrue(((Array2D) ts.getSpace()).get(50, 99).findNeighbors().size() == 5);
        assertTrue(((Array2D) ts.getSpace()).get(99, 50).findNeighbors().size() == 5);
        assertTrue(((Array2D) ts.getSpace()).get(50, 50).findNeighbors().size() == 8);
    }

    public void testfindWithinMooreWidth4Height2() {
        setUpMoore(4, 2);
        List testCells = ts.findWithin(((Array2D) ts.getSpace()).get(2, 1).getCoordinate(), null, false, 1);
        assertTrue(testCells.size() == 5);
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(1, 1).getCoordinate(), null, false, 3);
        assertTrue(testCells.size() == 7);
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(1, 1).getCoordinate(), null, false, 2);
        assertTrue(testCells.size() == 7);
    }

    public void testfindWithinMooreWidth2Height4() {
        setUpMoore(2, 4);
        List testCells = ts.findWithin(((Array2D) ts.getSpace()).get(1, 1).getCoordinate(), null, false, 1);
        assertTrue(testCells.size() == 5);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(1, 1).getCoordinate(), null, false, 3);
        assertTrue(testCells.size() == 7);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(1, 1).getCoordinate(), null, false, 2);
        assertTrue(testCells.size() == 7);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
    }

    public void testfindWithinMooreWidth1() {
        setUpMoore(1, 100);
        List testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 99).getCoordinate(), null, false, 1);
        assertTrue(testCells.size() == 2);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
    }

    public void testfindWithinMooreRadiusSmaller() {
        setUpMoore(1, 3);
        List testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 1).getCoordinate(), null, false, 1);
        assertTrue(testCells.size() == 2);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 1).getCoordinate(), null, false, 2);
        assertTrue(testCells.size() == 2);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
    }

    public void testfindWithinMooreMidProblem() {
        setUpMoore(1, 7);
        List testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 3).getCoordinate(), null, false, 3);
        assertTrue(testCells.size() == 6);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
    }

    public void testfindWithinMooreRadiusSmaller2() {
        setUpMoore(3, 1);
        //List testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 0).getCoordinate(), null, false, 1);
        List testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 0).getCoordinate(), null, false, 1);
        assertTrue(testCells.size() == 2);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) instanceof TestCell);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(1, 0).getCoordinate(), null, false, 1);
        assertTrue(testCells.size() == 2);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) instanceof TestCell);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(2, 0).getCoordinate(), null, false, 1);
        assertTrue(testCells.size() == 2);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) instanceof TestCell);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(1, 0).getCoordinate(), null, false, 2);
        assertTrue(testCells.size() == 2);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
    }

    public void testfindWithinMoore() {
        setUpMoore(100, 100);

        //List testCells = ts.findWithin(((Array2D) ts.getSpace()).get(10, 99).getCoordinate(), null, false, 1);
        List testCells = ts.findWithin(((Array2D) ts.getSpace()).get(10, 99).getCoordinate(), null, false, 1);
        assertTrue(testCells.size() == 8);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(99, 0).getCoordinate(), null, false, 1);
        assertTrue(testCells.size() == 8);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 13).getCoordinate(), null, false, 1);
        assertTrue(testCells.size() == 8);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(13, 0).getCoordinate(), null, false, 40);
        assertTrue(testCells.size() == 6560);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(99, 99).getCoordinate(), null, false, 40);
        assertTrue(testCells.size() == 6560);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(13, 99).getCoordinate(), null, false, 40);
        assertTrue(testCells.size() == 6560);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(13, 99).getCoordinate(), null, false, 55);
        assertTrue(testCells.size() == 9999);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(98, 10).getCoordinate(), null, false, 55);
        assertTrue(testCells.size() == 9999);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(99, 99).getCoordinate(), null, false, 55);
        assertTrue(testCells.size() == 9999);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 1).getCoordinate(), null, false, 55);
        assertTrue(testCells.size() == 9999);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 10).getCoordinate(), null, true, 29);
        assertTrue(testCells.size() == 3481);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(99, 32).getCoordinate(), null, true, 101);
        assertTrue(testCells.size() == 10000);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 0).getCoordinate(), null, true, 101);
        assertTrue(testCells.size() == 10000);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(10, 99).getCoordinate(), null, true, 101);
        assertTrue(testCells.size() == 10000);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 10).getCoordinate(), null, false, 2);
        assertTrue(testCells.size() == 24);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(10, 99).getCoordinate(), null, false, 2);
        assertTrue(testCells.size() == 24);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }

        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 10).getCoordinate(), null, false, 0);
        for (int i = 0; i < testCells.size(); i++) {
            ((TestCell) testCells.get(i)).testState = true;
        }
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(0, 10)).testState);
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 10).getCoordinate(), null, true, 0);
        for (int i = 0; i < testCells.size(); i++) {
            ((TestCell) testCells.get(i)).testState = true;
        }
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(0, 10)).testState);

        ts.getSpace().setPeriodic(true);
        ts.execute(Scape.INITIALIZE_RULE);

        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(0, 10)).testState);

        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 10).getCoordinate(), null, false, 2);
        for (int i = 0; i < testCells.size(); i++) {
            ((TestCell) testCells.get(i)).testState = true;
        }
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(98, 8)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(98, 9)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(98, 10)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(98, 11)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(98, 12)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(99, 8)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(99, 9)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(99, 10)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(99, 11)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(99, 12)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(0, 12)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(1, 12)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(2, 12)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(1, 8)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(1, 9)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(1, 10)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(1, 11)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(1, 12)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(2, 12)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(2, 8)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(2, 9)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(2, 10)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(2, 11)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(2, 12)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(0, 10)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(0, 8)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(0, 7)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(0, 13)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(3, 10)).testState);

        ts.getSpace().setPeriodic(false);
        ts.createScape();
        ts.execute(Scape.INITIALIZE_RULE);

        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 10).getCoordinate(), null, false, 2);
        for (int i = 0; i < testCells.size(); i++) {
            ((TestCell) testCells.get(i)).testState = true;
        }
        ts.initialize();

        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(10, 99).getCoordinate(), null, false, 1);
        assertTrue(testCells.size() == 5);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(99, 0).getCoordinate(), null, false, 1);
        assertTrue(testCells.size() == 3);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 13).getCoordinate(), null, false, 1);
        assertTrue(testCells.size() == 5);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(13, 0).getCoordinate(), null, false, 40);
        assertTrue(testCells.size() == 2213);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 10).getCoordinate(), null, true, 29);
        assertTrue(testCells.size() == 1200);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(99, 32).getCoordinate(), null, true, 101);
        assertTrue(testCells.size() == 10000);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 0).getCoordinate(), null, true, 101);
        assertTrue(testCells.size() == 10000);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(10, 99).getCoordinate(), null, true, 101);
        assertTrue(testCells.size() == 10000);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(0, 10).getCoordinate(), null, false, 2);
        assertTrue(testCells.size() == 14);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }
        testCells = ts.findWithin(((Array2D) ts.getSpace()).get(10, 99).getCoordinate(), null, false, 2);
        assertTrue(testCells.size() == 14);
        for (int i = 0; i < testCells.size(); i++) {
            assertTrue(testCells.get(i) != null);
        }

        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(98, 8)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(98, 9)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(98, 10)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(98, 11)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(98, 12)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(99, 8)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(99, 9)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(99, 10)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(99, 11)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(99, 12)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(0, 8)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(0, 9)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(0, 10)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(0, 11)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(0, 12)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(1, 8)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(1, 9)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(1, 10)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(1, 11)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(2, 12)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(2, 8)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(2, 9)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(2, 10)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(2, 11)).testState);
        assertTrue(((TestCell) ((Array2D) ts.getSpace()).get(2, 12)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(0, 7)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(0, 13)).testState);
        assertTrue(!((TestCell) ((Array2D) ts.getSpace()).get(3, 10)).testState);

        assertTrue(((Array2D) ts.getSpace()).get(0, 0).findNeighbors().size() == 3);
        assertTrue(((Array2D) ts.getSpace()).get(0, 23).findNeighbors().size() == 5);
        assertTrue(((Array2D) ts.getSpace()).get(0, 99).findNeighbors().size() == 3);
        assertTrue(((Array2D) ts.getSpace()).get(99, 0).findNeighbors().size() == 3);
        assertTrue(((Array2D) ts.getSpace()).get(50, 0).findNeighbors().size() == 5);
        assertTrue(((Array2D) ts.getSpace()).get(99, 99).findNeighbors().size() == 3);
        assertTrue(((Array2D) ts.getSpace()).get(98, 99).findNeighbors().size() == 5);
        assertTrue(((Array2D) ts.getSpace()).get(50, 99).findNeighbors().size() == 5);
        assertTrue(((Array2D) ts.getSpace()).get(99, 50).findNeighbors().size() == 5);
        assertTrue(((Array2D) ts.getSpace()).get(50, 50).findNeighbors().size() == 8);
    }

    public void setUpEuclidian() {
        ts = new Scape(new Array2DEuclidian());
        ts.setExtent(100, 100);
        ts.setPrototypeAgent(new TestCell());
        ts.createScape();
        ts.execute(Scape.INITIALIZE_RULE);
        ts.getSpace().setPeriodic(true);
    }

    public void testDistanceEuclidian() {
        setUpEuclidian();
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(10, 12), ((Array2D) ts.getSpace()).get(12, 10)) == 3);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(12, 10), ((Array2D) ts.getSpace()).get(10, 12)) == 3);

        ts.getSpace().setPeriodic(false);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(98, 10), ((Array2D) ts.getSpace()).get(0, 12)) == 98);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(10, 97), ((Array2D) ts.getSpace()).get(14, 3)) == 94);

        ts.getSpace().setPeriodic(true);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(98, 10), ((Array2D) ts.getSpace()).get(0, 12)) == 3);
        assertTrue(ts.getSpace().calculateDistance(((Array2D) ts.getSpace()).get(10, 98), ((Array2D) ts.getSpace()).get(13, 2)) == 5);
    }

    Conditional TEST_STATE_TRUE = new Conditional() {
        public boolean meetsCondition(Object o) {
            return ((TestCell) o).testState;
        }
    };

    public void testFindNearestMoore() {
        setUpMoore(30, 30);
        //ts.initialize();
        ((TestCell) ((Array2D) ts.getSpace()).get(10, 12)).testState = true;
        ((TestCell) ((Array2D) ts.getSpace()).get(10, 20)).testState = true;
        assertTrue(((Cell) (((Array2D) ts.getSpace()).get(10, 10))).findNearest(TEST_STATE_TRUE) == ((TestCell) ((Array2D) ts.getSpace()).get(10, 12)));
    }

    public void testFindCellAwayMoore() {
        ts = new Scape(new Array2DMoore());
        ts.setExtent(30, 30);
        ts.setPrototypeAgent(new HostCell());
        ts.getSpace().setPeriodic(true);
        ts.createScape();
        ts.execute(Scape.INITIALIZE_RULE);

        Node originCell = ((Array2D) ts.getSpace()).get(5, 5);
        Node targetCell = ((Array2D) ts.getSpace()).get(25, 25);

        Node awayCell = ((Array2D) ts.getSpace()).findCellAway(originCell, targetCell);
        assertSame(((Array2D) ts.getSpace()).get(6, 6), awayCell);

        ts.getSpace().setPeriodic(false);
        awayCell = ((Array2D) ts.getSpace()).findCellAway(originCell, targetCell);
        assertSame(((Array2D) ts.getSpace()).get(4, 4), awayCell);
    }

    class TestCell2 extends Cell {

        int tag = 0;
    }

    class TestThread extends Thread {

        ResetableIterator iterator;
        int id;

        public TestThread(ResetableIterator iterator, int id) {
            super("Test Thread " + id);
            this.iterator = iterator;
            this.id = id;
        }

        public void run() {
            super.run();
            for (; iterator.hasNext();) {
                TestCell2 cell = (TestCell2) iterator.next();
                //We use + to guard against false negatives from write-overs.
                cell.tag += id;
            }
        }
    };

    public void testScapeMultiIterators10101() {
        Scape scape = new Scape(new Array2DMoore());
        scape.setExtent(10, 10);
        scape.setPrototypeAgent(new TestCell2());
        scape.createScape();
        ResetableIterator[] scapeIterators = scape.scapeIterators(2);
        Thread[] threads = new Thread[scapeIterators.length];
        for (int i = 0; i < scapeIterators.length; i++) {
            ResetableIterator scapeIterator = scapeIterators[i];
            threads[i] = new TestThread(scapeIterator, i);
        }
        for (int i = 0; i < scapeIterators.length; i++) {
            threads[i].start();
        }
        for (int i = 0; i < scapeIterators.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            }
        }
        int expTag = 0;
        Coordinate2DDiscrete extent = (Coordinate2DDiscrete) scape.getExtent();
        for (int x = 0; x < extent.getXValue(); x++) {
            for (int y = 0; y < extent.getYValue(); y++) {
                if ((x == 5) && (y == 0)) {
                    expTag++;
                }
                TestCell2 testCell = (TestCell2) ((Array2D) scape.getSpace()).get(x, y);
                assertTrue(testCell.tag == expTag);
            }
        }
    }

    public void testScapeMultiIterators10104() {
        Scape scape = new Scape(new Array2DMoore());
        scape.setExtent(10, 10);
        scape.setPrototypeAgent(new TestCell2());
        scape.createScape();
        ResetableIterator[] scapeIterators = scape.scapeIterators(4);
        Thread[] threads = new Thread[scapeIterators.length];
        for (int i = 0; i < scapeIterators.length; i++) {
            ResetableIterator scapeIterator = scapeIterators[i];
            threads[i] = new TestThread(scapeIterator, i);
        }
        for (int i = 0; i < scapeIterators.length; i++) {
            threads[i].start();
        }
        for (int i = 0; i < scapeIterators.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            }
        }
        int expTag = 0;
        Coordinate2DDiscrete extent = (Coordinate2DDiscrete) scape.getExtent();
        for (int x = 0; x < extent.getXValue(); x++) {
            for (int y = 0; y < extent.getYValue(); y++) {
                if (((x == 2) && (y == 5)) || ((x == 5) && (y == 0)) || ((x == 7) && (y == 5))) {
                    expTag++;
                }
                TestCell2 testCell = (TestCell2) ((Array2D) scape.getSpace()).get(x, y);
                assertTrue(testCell.tag == expTag);
            }
        }
    }

    public void testScapeMultiIterators10102() {
        Scape scape = new Scape(new Array2DMoore());
        scape.setExtent(10, 10);
        scape.setPrototypeAgent(new TestCell2());
        scape.createScape();
        ResetableIterator[] scapeIterators = scape.scapeIterators(2);
        Thread[] threads = new Thread[scapeIterators.length];
        for (int i = 0; i < scapeIterators.length; i++) {
            ResetableIterator scapeIterator = scapeIterators[i];
            threads[i] = new TestThread(scapeIterator, i);
        }
        for (int i = 0; i < scapeIterators.length; i++) {
            threads[i].start();
        }
        for (int i = 0; i < scapeIterators.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            }
        }
        int expTag = 0;
        Coordinate2DDiscrete extent = (Coordinate2DDiscrete) scape.getExtent();
        for (int x = 0; x < extent.getXValue(); x++) {
            for (int y = 0; y < extent.getYValue(); y++) {
                if ((x == 5) && (y == 0)) {
                    expTag++;
                }
                TestCell2 testCell = (TestCell2) ((Array2D) scape.getSpace()).get(x, y);
                assertTrue(testCell.tag == expTag);
            }
        }
    }

    public void testScapeMultiIterators794() {
        Scape scape = new Scape(new Array2DMoore());
        scape.setExtent(7, 9);
        scape.setPrototypeAgent(new TestCell2());
        scape.createScape();
        ResetableIterator[] scapeIterators = scape.scapeIterators(4);
        Thread[] threads = new Thread[scapeIterators.length];
        for (int i = 0; i < scapeIterators.length; i++) {
            ResetableIterator scapeIterator = scapeIterators[i];
            threads[i] = new TestThread(scapeIterator, i);
        }
        for (int i = 0; i < scapeIterators.length; i++) {
            threads[i].start();
        }
        for (int i = 0; i < scapeIterators.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            }
        }
        int expTag = 0;
        Coordinate2DDiscrete extent = (Coordinate2DDiscrete) scape.getExtent();
        for (int x = 0; x < extent.getXValue(); x++) {
            for (int y = 0; y < extent.getYValue(); y++) {
                if (((x == 1) && (y == 6)) || ((x == 3) && (y == 3)) || ((x == 5) && (y == 0))) {
                    expTag++;
                }
                TestCell2 testCell = (TestCell2) ((Array2D) scape.getSpace()).get(x, y);
                assertTrue(testCell.tag == expTag);
            }
        }
    }
}