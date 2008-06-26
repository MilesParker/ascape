/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.test.model.space;

import java.util.List;

import junit.framework.TestCase;

import org.ascape.model.Agent;
import org.ascape.model.Cell;
import org.ascape.model.Scape;
import org.ascape.model.rule.Rule;
import org.ascape.model.space.Array1D;
import org.ascape.util.Conditional;

public class Array1DTest extends TestCase {

    public Array1DTest(String name) {
        super(name);
    }

    class TestCell extends Cell {

        public boolean testState;

        public void initialize() {
            testState = false;
        }
    }

    Scape vn;

    protected void setUp() {
        vn = new Scape(new Array1D());
        vn.setExtent(100);
        vn.getSpace().setPeriodic(true);
        vn.setPrototypeAgent(new TestCell());
        vn.createScape();
        vn.initialize();
    }

    static final Conditional TEST_STATE = new Conditional() {
        public boolean meetsCondition(Object o) {
            return ((TestCell) o).testState;
        }
    };

    // jmiller 8/6/01
    // changed setCell(cell, boolean)
    public void testAdditions() {
        Scape testArray1 = new Scape(new Array1D());
        Scape testArray2 = new Scape(new Array1D());
        testArray1.setPrototypeAgent(new Cell());
        testArray1.setExtent(3);
        testArray2.setExtent(3);
        testArray1.createScape();
        testArray2.createScape();
        Cell testCell = (Cell) testArray1.get(0);
        testArray2.set(0, testCell);
        assertTrue(testCell.getScape() == testArray2);
        Cell testCell2 = (Cell) testArray1.get(1);
        testArray2.set(1, testCell2, false);
        assertTrue(testCell2.getScape() == testArray1);
    }

    public void testSize2() {
        vn = new Scape(new Array1D());
        vn.setExtent(2);
        vn.setPrototypeAgent(new TestCell());
        vn.createScape();
        vn.initialize();
        List testCells = ((Array1D) vn.getSpace()).findWithinImpl(((Cell) vn.get(0)).getCoordinate(), false, 0);
        for (int i = 0; i < testCells.size(); i++) {
            ((TestCell) testCells.get(i)).testState = true;
        }

        assertTrue(testCells.size() == 0);
        assertTrue(!((TestCell) vn.get(0)).testState);
        assertTrue(!((TestCell) vn.get(1)).testState);
        vn.initialize();
        testCells = ((Array1D) vn.getSpace()).findWithinImpl(((Cell) vn.get(0)).getCoordinate(), false, 1);
        for (int i = 0; i < testCells.size(); i++) {
            ((TestCell) testCells.get(i)).testState = true;
        }
        assertTrue(testCells.size() == 1);
        assertTrue(!((TestCell) vn.get(0)).testState);
        assertTrue(((TestCell) vn.get(1)).testState);
        vn.initialize();
        testCells = ((Array1D) vn.getSpace()).findWithinImpl(((Cell) vn.get(0)).getCoordinate(), true, 1);
        for (int i = 0; i < testCells.size(); i++) {
            ((TestCell) testCells.get(i)).testState = true;
        }
        assertTrue(testCells.size() == 2);
        assertTrue(((TestCell) vn.get(0)).testState);
        assertTrue(((TestCell) vn.get(1)).testState);
    }

    public void testSize1() {
        vn = new Scape(new Array1D());
        vn.setExtent(1);
        vn.setPrototypeAgent(new TestCell());
        vn.createScape();
        vn.initialize();
        List testCells = ((Array1D) vn.getSpace()).findWithinImpl(((Cell) vn.get(0)).getCoordinate(), true, 0);
        for (int i = 0; i < testCells.size(); i++) {
            ((TestCell) testCells.get(i)).testState = true;
        }
        assertTrue(((TestCell) vn.get(0)).testState);
    }

    public void testSize1NoOrigin() {
        vn = new Scape(new Array1D());
        vn.setExtent(1);
        vn.setPrototypeAgent(new TestCell());
        vn.createScape();
        vn.initialize();
        List testCells = ((Array1D) vn.getSpace()).findWithinImpl(((Cell) vn.get(0)).getCoordinate(), false, 0);
        for (int i = 0; i < testCells.size(); i++) {
            ((TestCell) testCells.get(i)).testState = true;
        }
        assertTrue(!((TestCell) vn.get(0)).testState);
    }

    public void testTooBig() {
        List testCells = ((Array1D) vn.getSpace()).findWithinImpl(((Cell) vn.get(23)).getCoordinate(), true, 50);
        for (int i = 0; i < testCells.size(); i++) {
            ((TestCell) testCells.get(i)).testState = true;
        }
        vn.executeOnMembers(new Rule("Test") {
            public void execute(Agent a) {
                assertTrue(((TestCell) a).testState);
            }
        });
    }

    public void testTooBigByFar() {
        List testCells = ((Array1D) vn.getSpace()).findWithinImpl(((Cell) vn.get(23)).getCoordinate(), true, 1000);
        for (int i = 0; i < testCells.size(); i++) {
            ((TestCell) testCells.get(i)).testState = true;
        }
        vn.executeOnMembers(new Rule("Test") {
            public void execute(Agent a) {
                assertTrue(((TestCell) a).testState);
            }
        });
    }

    public void testInside() {
        List testCells = ((Array1D) vn.getSpace()).findWithinImpl(((Cell) vn.get(50)).getCoordinate(), true, 2);
        for (int i = 0; i < testCells.size(); i++) {
            ((TestCell) testCells.get(i)).testState = true;
        }
        assertTrue(!((TestCell) vn.get(47)).testState);
        assertTrue(((TestCell) vn.get(48)).testState);
        assertTrue(((TestCell) vn.get(49)).testState);
        assertTrue(((TestCell) vn.get(50)).testState);
        assertTrue(((TestCell) vn.get(51)).testState);
        assertTrue(((TestCell) vn.get(52)).testState);
        assertTrue(!((TestCell) vn.get(53)).testState);
    }

    public void testInsideNoBoundary() {
        List testCells = ((Array1D) vn.getSpace()).findWithinImpl(((Cell) vn.get(50)).getCoordinate(), false, 2);
        for (int i = 0; i < testCells.size(); i++) {
            ((TestCell) testCells.get(i)).testState = true;
        }
        assertTrue(!((TestCell) vn.get(47)).testState);
        assertTrue(((TestCell) vn.get(48)).testState);
        assertTrue(((TestCell) vn.get(49)).testState);
        assertTrue(!((TestCell) vn.get(50)).testState);
        assertTrue(((TestCell) vn.get(51)).testState);
        assertTrue(((TestCell) vn.get(52)).testState);
        assertTrue(!((TestCell) vn.get(53)).testState);
    }

    public void testFromTopBoundary() {
        List testCells = ((Array1D) vn.getSpace()).findWithinImpl(((Cell) vn.get(0)).getCoordinate(), true, 2);
        for (int i = 0; i < testCells.size(); i++) {
            ((TestCell) testCells.get(i)).testState = true;
        }

        assertTrue(((TestCell) vn.get(98)).testState);
        assertTrue(((TestCell) vn.get(99)).testState);
        assertTrue(((TestCell) vn.get(0)).testState);
        assertTrue(((TestCell) vn.get(1)).testState);
        assertTrue(((TestCell) vn.get(2)).testState);
        assertTrue(!((TestCell) vn.get(3)).testState);
        assertTrue(!((TestCell) vn.get(97)).testState);
    }

    public void testFromTopBoundaryNoOrigin() {
        List testCells = ((Array1D) vn.getSpace()).findWithinImpl(((Cell) vn.get(0)).getCoordinate(), false, 2);
        for (int i = 0; i < testCells.size(); i++) {
            ((TestCell) testCells.get(i)).testState = true;
        }

        assertTrue(((TestCell) vn.get(98)).testState);
        assertTrue(((TestCell) vn.get(99)).testState);
        assertTrue(!((TestCell) vn.get(0)).testState);
        assertTrue(((TestCell) vn.get(1)).testState);
        assertTrue(((TestCell) vn.get(2)).testState);
        assertTrue(!((TestCell) vn.get(3)).testState);
        assertTrue(!((TestCell) vn.get(97)).testState);
    }

    public void testFromBottom() {
        List testCells = ((Array1D) vn.getSpace()).findWithinImpl(((Cell) vn.get(98)).getCoordinate(), true, 2);
        for (int i = 0; i < testCells.size(); i++) {
            ((TestCell) testCells.get(i)).testState = true;
        }
        assertTrue(((TestCell) vn.get(96)).testState);
        assertTrue(((TestCell) vn.get(97)).testState);
        assertTrue(((TestCell) vn.get(99)).testState);
        assertTrue(((TestCell) vn.get(0)).testState);
        assertTrue(!((TestCell) vn.get(95)).testState);
        assertTrue(!((TestCell) vn.get(1)).testState);
        assertTrue(((TestCell) vn.get(98)).testState);
    }

    public void testFromBottomNoOrigin() {
        List testCells = ((Array1D) vn.getSpace()).findWithinImpl(((Cell) vn.get(98)).getCoordinate(), false, 2);
        for (int i = 0; i < testCells.size(); i++) {
            ((TestCell) testCells.get(i)).testState = true;
        }
        assertTrue(((TestCell) vn.get(96)).testState);
        assertTrue(((TestCell) vn.get(97)).testState);
        assertTrue(((TestCell) vn.get(99)).testState);
        assertTrue(((TestCell) vn.get(0)).testState);
        assertTrue(!((TestCell) vn.get(95)).testState);
        assertTrue(!((TestCell) vn.get(1)).testState);
        assertTrue(!((TestCell) vn.get(98)).testState);
    }

    public void testDistance() {
        assertTrue(vn.calculateDistance(((Cell) vn.get(60)), ((Cell) vn.get(98))) == 38);
        assertTrue(vn.calculateDistance(((Cell) vn.get(98)), ((Cell) vn.get(60))) == 38);
        assertTrue(vn.calculateDistance(((Cell) vn.get(98)), ((Cell) vn.get(10))) == 12);
        assertTrue(vn.calculateDistance(((Cell) vn.get(10)), ((Cell) vn.get(98))) == 12);
    }
}