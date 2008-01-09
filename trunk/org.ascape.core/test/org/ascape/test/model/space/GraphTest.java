/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.test.model.space;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.ascape.model.Cell;
import org.ascape.model.LocatedAgent;
import org.ascape.model.Scape;
import org.ascape.model.space.Graph;
import org.ascape.util.Conditional;

public class GraphTest extends TestCase {

    Scape graph = new Scape(new Graph());

    class TestAgent extends Cell {

        boolean testCondition;

        public boolean isTestCondition() {
            return testCondition;
        }

        public void setTestCondition(boolean testCondition) {
            this.testCondition = testCondition;
        }
    }


    Conditional testCondition_true = new Conditional() {
        public boolean meetsCondition(Object o) {
            return ((TestAgent) o).isTestCondition();
        }
    };

    Conditional testCondition_false = new Conditional() {
        public boolean meetsCondition(Object o) {
            return !((TestAgent) o).isTestCondition();
        }
    };

    TestAgent testAgent1 = new TestAgent();
    TestAgent testAgent2 = new TestAgent();
    TestAgent testAgent3 = new TestAgent();
    TestAgent testAgent4 = new TestAgent();
    TestAgent testAgent5 = new TestAgent();

    TestAgent testAgent6 = new TestAgent();
    TestAgent testAgent7 = new TestAgent();
    TestAgent testAgent8 = new TestAgent();
    TestAgent testAgent9 = new TestAgent();
    TestAgent testAgent10 = new TestAgent();
    TestAgent testAgent11 = new TestAgent();


    public GraphTest(String name) {
        super(name);
        testAgent1.setName("1");
        testAgent2.setName("2");
        testAgent3.setName("3");
        testAgent4.setName("4");
        testAgent5.setName("5");
        testAgent6.setName("6");
        testAgent7.setName("7");
        testAgent8.setName("8");
        testAgent9.setName("9");
        testAgent10.setName("10");

        testAgent1.setTestCondition(true);
        testAgent2.setTestCondition(true);
        testAgent3.setTestCondition(true);
        testAgent4.setTestCondition(true);
        testAgent5.setTestCondition(true);
        testAgent6.setTestCondition(false);
        testAgent7.setTestCondition(false);
        testAgent8.setTestCondition(false);
        testAgent9.setTestCondition(false);
        testAgent10.setTestCondition(false);
    }

    public void testInitialize() {
        graph.initialize();
        graph.clear();
        for (Iterator it = graph.iterator(); it.hasNext();) {
            Cell c = (Cell) it.next();
            List neighbors = ((Graph) graph.getSpace()).getNeighborsFor(c);
            assertTrue(neighbors.size() == 0);
        }
    }

    public void testFindNearest() {
        graph.initialize();
        graph.clear();

        graph.add(testAgent1);
        graph.add(testAgent2);
        graph.add(testAgent3);
        graph.add(testAgent4);
        graph.add(testAgent5);
        graph.add(testAgent6);
        graph.add(testAgent7);
        graph.add(testAgent8);
        graph.add(testAgent9);
        graph.add(testAgent10);

        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent2, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent5, false);

        ((Graph) graph.getSpace()).addNeighbor(testAgent2, testAgent3, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent2, testAgent4, false);

        ((Graph) graph.getSpace()).addNeighbor(testAgent3, testAgent6, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent3, testAgent7, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent6, testAgent8, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent5, testAgent9, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent8, testAgent10, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent9, testAgent10, false);

        LocatedAgent nearest = testAgent7.findNearest(testCondition_true, false, 1.0);
        assertTrue(nearest == testAgent3);

        testAgent1.setTestCondition(false);
        testAgent2.setTestCondition(false);
        testAgent6.setTestCondition(false);
        testAgent7.setTestCondition(false);
        testAgent8.setTestCondition(false);
        testAgent4.setTestCondition(true);

        nearest = testAgent3.findNearest(testCondition_true, false, 2.0);
        assertTrue(nearest == testAgent4);
    }

    public void testAddNeighbor_directed() {
        graph.initialize();
        graph.clear();

        graph.add(testAgent1);
        graph.add(testAgent2);
        graph.add(testAgent3);
        graph.add(testAgent4);

        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent2);
        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent3);
        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent4);

        assertTrue(!graph.contains(testAgent5));
        try {
            ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent5);
        } catch (RuntimeException e) {
            assertTrue(e instanceof RuntimeException);
        }
        assertTrue(!graph.contains(testAgent5));

        graph.add(testAgent5);
        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent5);

        HashMap adjacencies = ((Graph) graph.getSpace()).getAdjacencyMap();
        ArrayList neighbors = (ArrayList) adjacencies.get(testAgent1);

        assertTrue(neighbors.contains(testAgent2));
        assertTrue(neighbors.contains(testAgent3));
        assertTrue(neighbors.contains(testAgent4));
        assertTrue(neighbors.contains(testAgent5));

        neighbors = (ArrayList) adjacencies.get(testAgent5);
        assertTrue(neighbors.size() == 0);
        neighbors = (ArrayList) adjacencies.get(testAgent2);
        assertTrue(neighbors.size() == 0);
        neighbors = (ArrayList) adjacencies.get(testAgent3);
        assertTrue(neighbors.size() == 0);
        neighbors = (ArrayList) adjacencies.get(testAgent4);
        assertTrue(neighbors.size() == 0);
    }

    public void testAddNeighbor_undirected() {
        graph.initialize();
        graph.clear();

        graph.add(testAgent1);
        graph.add(testAgent2);
        graph.add(testAgent3);
        graph.add(testAgent4);
        graph.add(testAgent5);

        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent2, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent3, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent4, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent5, false);

        ArrayList neighbors = (ArrayList) ((Graph) graph.getSpace()).getAdjacencyMap().get(testAgent5);
        assertTrue(neighbors.size() == 1);
        neighbors = (ArrayList) ((Graph) graph.getSpace()).getAdjacencyMap().get(testAgent2);
        assertTrue(neighbors.size() == 1);
        neighbors = (ArrayList) ((Graph) graph.getSpace()).getAdjacencyMap().get(testAgent3);
        assertTrue(neighbors.size() == 1);
        neighbors = (ArrayList) ((Graph) graph.getSpace()).getAdjacencyMap().get(testAgent4);
        assertTrue(neighbors.size() == 1);
    }

    public void testSetNeighbors() {
        graph.initialize();
        graph.clear();

        graph.add(testAgent1);
        graph.add(testAgent2);
        graph.add(testAgent3);
        graph.add(testAgent4);
        graph.add(testAgent5);

        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent2);
        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent3);
        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent4);

        assertTrue(((Graph) graph.getSpace()).getNeighborsFor(testAgent1).size() == 3);

        ArrayList newNeighbors = new ArrayList();
        newNeighbors.add(testAgent7);
        newNeighbors.add(testAgent8);
        newNeighbors.add(testAgent9);
        newNeighbors.add(testAgent10);

        ((Graph) graph.getSpace()).setNeighborsFor(testAgent1, newNeighbors);

        List neighbors = ((Graph) graph.getSpace()).getNeighborsFor(testAgent1);

        assertTrue(neighbors.size() == 4);
        assertTrue(neighbors.contains(testAgent7));
        assertTrue(neighbors.contains(testAgent8));
        assertTrue(neighbors.contains(testAgent9));
        assertTrue(neighbors.contains(testAgent10));

        assertTrue(!neighbors.contains(testAgent2));
        assertTrue(!neighbors.contains(testAgent3));
        assertTrue(!neighbors.contains(testAgent4));
    }

    public void testRemoveNeighbor() {
        graph.initialize();
        graph.clear();

        graph.add(testAgent1);
        graph.add(testAgent2);
        graph.add(testAgent3);
        graph.add(testAgent4);
        graph.add(testAgent5);

        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent2);
        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent3);
        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent4);
        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent5);

        assertTrue(((Graph) graph.getSpace()).removeNeighbor(testAgent1, testAgent2) == true);

        ArrayList neighbors = (ArrayList) ((Graph) graph.getSpace()).getAdjacencyMap().get(testAgent1);

        assertTrue(!neighbors.contains(testAgent2));

        assertTrue(neighbors.contains(testAgent3));
        assertTrue(neighbors.contains(testAgent4));
        assertTrue(neighbors.contains(testAgent5));
    }

    public void testRemove() {
        graph.initialize();
        graph.clear();

        graph.add(testAgent1);
        graph.add(testAgent2);
        graph.add(testAgent3);
        graph.add(testAgent4);
        graph.add(testAgent5);

        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent2);
        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent3);
        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent4);
        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent5);

        graph.remove(testAgent4);
        ArrayList neighbors = (ArrayList) ((Graph) graph.getSpace()).getAdjacencyMap().get(testAgent1);

        assertTrue(!graph.contains(testAgent4));
        assertTrue(!neighbors.contains(testAgent4));

        assertTrue(neighbors.contains(testAgent2));
        assertTrue(neighbors.contains(testAgent3));
        assertTrue(neighbors.contains(testAgent5));
    }

    public void testFindWithin_distance_undirected() {
        graph.initialize();
        graph.clear();

        graph.add(testAgent1);
        graph.add(testAgent2);
        graph.add(testAgent3);
        graph.add(testAgent4);
        graph.add(testAgent5);
        graph.add(testAgent6);
        graph.add(testAgent7);
        graph.add(testAgent8);
        graph.add(testAgent9);
        graph.add(testAgent10);

        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent2, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent5, false);

        ((Graph) graph.getSpace()).addNeighbor(testAgent2, testAgent3, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent2, testAgent4, false);

        ((Graph) graph.getSpace()).addNeighbor(testAgent3, testAgent6, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent3, testAgent7, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent6, testAgent8, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent5, testAgent9, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent8, testAgent10, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent9, testAgent10, false);

        List found = testAgent1.findWithin(null, true, 1);
        assertTrue(found.size() == 3);
        assertTrue(found.contains(testAgent1));
        assertTrue(found.contains(testAgent2));
        assertTrue(found.contains(testAgent5));

        found = testAgent7.findWithin(null, true, 2);
        //assertTrue(found.size() == 4);
        assertTrue(found.contains(testAgent7));
        assertTrue(found.contains(testAgent3));
        assertTrue(found.contains(testAgent6));
        assertTrue(found.contains(testAgent2));

        found = testAgent3.findWithin(null, true, 0);
        assertTrue(found.size() == 1);
        assertTrue(found.contains(testAgent3));

        found = testAgent9.findWithin(null, true, 3);
        assertTrue(found.size() == 7);
        assertTrue(found.contains(testAgent9));
        assertTrue(found.contains(testAgent5));
        assertTrue(found.contains(testAgent10));
        assertTrue(found.contains(testAgent1));
        assertTrue(found.contains(testAgent8));
        assertTrue(found.contains(testAgent6));
        assertTrue(found.contains(testAgent2));
    }

    public void testFindWithin_distance_directed() {
        graph.initialize();
        graph.clear();

        graph.add(testAgent1);
        graph.add(testAgent2);
        graph.add(testAgent3);
        graph.add(testAgent4);
        graph.add(testAgent5);
        graph.add(testAgent6);
        graph.add(testAgent7);
        graph.add(testAgent8);
        graph.add(testAgent9);
        graph.add(testAgent10);

        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent2);
        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent5);

        ((Graph) graph.getSpace()).addNeighbor(testAgent2, testAgent3);
        ((Graph) graph.getSpace()).addNeighbor(testAgent2, testAgent4);

        ((Graph) graph.getSpace()).addNeighbor(testAgent3, testAgent6);
        ((Graph) graph.getSpace()).addNeighbor(testAgent3, testAgent7);
        ((Graph) graph.getSpace()).addNeighbor(testAgent6, testAgent8);
        ((Graph) graph.getSpace()).addNeighbor(testAgent5, testAgent9);
        ((Graph) graph.getSpace()).addNeighbor(testAgent8, testAgent10);
        ((Graph) graph.getSpace()).addNeighbor(testAgent9, testAgent10);

        ((Graph) graph.getSpace()).addNeighbor(testAgent9, testAgent5);
        ((Graph) graph.getSpace()).addNeighbor(testAgent10, testAgent9);

        List found = testAgent9.findWithin(null, true, 2);
        assertTrue(found.size() == 3);
        assertTrue(found.contains(testAgent9));
        assertTrue(found.contains(testAgent5));
        assertTrue(found.contains(testAgent10));
        assertTrue(!found.contains(testAgent8));
        assertTrue(!found.contains(testAgent1));
    }

    public void testFindWithin_condition() {
        graph.initialize();
        graph.clear();

        graph.add(testAgent1);
        graph.add(testAgent2);
        graph.add(testAgent3);
        graph.add(testAgent4);
        graph.add(testAgent5);
        graph.add(testAgent6);
        graph.add(testAgent7);
        graph.add(testAgent8);
        graph.add(testAgent9);
        graph.add(testAgent10);

        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent2, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent5, false);

        ((Graph) graph.getSpace()).addNeighbor(testAgent2, testAgent3, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent2, testAgent4, false);

        ((Graph) graph.getSpace()).addNeighbor(testAgent3, testAgent6, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent3, testAgent7, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent6, testAgent8, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent5, testAgent9, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent8, testAgent10, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent9, testAgent10, false);

        List found = testAgent3.findWithin(testCondition_true, true, 1);
        assertTrue(found.size() == 2);
        assertTrue(found.contains(testAgent3));
        assertTrue(found.contains(testAgent2));

        found = testAgent3.findWithin(testCondition_false, true, 1);
        assertTrue(found.size() == 2);
        assertTrue(found.contains(testAgent6));
        assertTrue(found.contains(testAgent7));
    }

    public void testFindWithin_includeSelf() {
        graph.initialize();
        graph.clear();

        graph.add(testAgent1);
        graph.add(testAgent2);
        graph.add(testAgent3);
        graph.add(testAgent4);
        graph.add(testAgent5);
        graph.add(testAgent6);
        graph.add(testAgent7);
        graph.add(testAgent8);
        graph.add(testAgent9);
        graph.add(testAgent10);

        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent2, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent5, false);

        ((Graph) graph.getSpace()).addNeighbor(testAgent2, testAgent3, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent2, testAgent4, false);

        ((Graph) graph.getSpace()).addNeighbor(testAgent3, testAgent6, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent3, testAgent7, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent6, testAgent8, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent5, testAgent9, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent8, testAgent10, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent9, testAgent10, false);

        List found = testAgent3.findWithin(testCondition_true, false, 1);
        assertTrue(found.size() == 1);
        assertTrue(!found.contains(testAgent3));
        assertTrue(found.contains(testAgent2));

        found = testAgent3.findWithin(testCondition_true, true, 1);
        assertTrue(found.size() == 2);
        assertTrue(found.contains(testAgent3));
        assertTrue(found.contains(testAgent2));
    }

    public void testCountWithin() {
        graph.initialize();
        graph.clear();

        graph.add(testAgent1);
        graph.add(testAgent2);
        graph.add(testAgent3);
        graph.add(testAgent4);
        graph.add(testAgent5);

        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent2, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent3, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent2, testAgent4, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent3, testAgent4, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent3, testAgent5, false);

        assertTrue(testAgent3.countWithin(null, true, 1) == 4);
        assertTrue(testAgent3.countWithin(null, true, 0) == 1);
        assertTrue(testAgent2.countWithin(null, true, 1) == 3);
        assertTrue(testAgent5.countWithin(null, true, 1) == 2);
        assertTrue(testAgent5.countWithin(testCondition_true, false, 1) == 1);
    }

    public void testHasWithin() {
        graph.initialize();
        graph.clear();

        graph.add(testAgent1);
        graph.add(testAgent2);
        graph.add(testAgent3);
        graph.add(testAgent4);
        graph.add(testAgent5);

        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent2, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent3, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent2, testAgent4, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent3, testAgent4, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent3, testAgent5, false);

        assertTrue(testAgent3.hasWithin(null, false, 1) == true);
        assertTrue(testAgent3.hasWithin(testCondition_false, false, 1) == false);
    }

    public void testClear() {
        graph = new Scape(new Graph());
        graph.initialize();
        graph.clear();
        assertTrue(graph.size() == 0);
        assertTrue(((Graph) graph.getSpace()).getAdjacencyMap().size() == 0);

        graph.add(testAgent1);
        graph.add(testAgent2);
        graph.add(testAgent3);
        graph.add(testAgent4);
        graph.add(testAgent5);

        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent2);
        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent5);
        ((Graph) graph.getSpace()).addNeighbor(testAgent2, testAgent3);
        ((Graph) graph.getSpace()).addNeighbor(testAgent2, testAgent4);
        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent5);

        graph.clear();

        assertTrue(graph.size() == 0);
        assertTrue(((Graph) graph.getSpace()).getAdjacencyMap().size() == 0);
    }

    public void testNeighbors() {
        graph.initialize();
        graph.clear();

        graph.add(testAgent1);
        graph.add(testAgent2);
        graph.add(testAgent3);
        graph.add(testAgent4);
        graph.add(testAgent5);
        graph.add(testAgent6);
        graph.add(testAgent7);
        graph.add(testAgent8);
        graph.add(testAgent9);
        graph.add(testAgent10);

        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent2, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent5, false);

        ((Graph) graph.getSpace()).addNeighbor(testAgent2, testAgent3, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent2, testAgent4, false);

        ((Graph) graph.getSpace()).addNeighbor(testAgent3, testAgent6, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent3, testAgent7, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent6, testAgent8, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent5, testAgent9, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent8, testAgent10, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent9, testAgent10, false);

        List neighbors = ((Graph) graph.getSpace()).getNeighborsFor(testAgent2);
        assertTrue(neighbors.size() == 3);
        assertTrue(neighbors.contains(testAgent1));
        assertTrue(neighbors.contains(testAgent3));
        assertTrue(neighbors.contains(testAgent4));
    }

    public void testIsNeighbor() {
        graph.initialize();
        graph.clear();

        graph.add(testAgent1);
        graph.add(testAgent2);
        graph.add(testAgent3);
        graph.add(testAgent4);
        graph.add(testAgent5);
        graph.add(testAgent6);
        graph.add(testAgent7);
        graph.add(testAgent8);
        graph.add(testAgent9);
        graph.add(testAgent10);

        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent2);
        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent5);
        ((Graph) graph.getSpace()).addNeighbor(testAgent2, testAgent3);
        ((Graph) graph.getSpace()).addNeighbor(testAgent2, testAgent4);
        ((Graph) graph.getSpace()).addNeighbor(testAgent3, testAgent6);
        ((Graph) graph.getSpace()).addNeighbor(testAgent3, testAgent7);
        ((Graph) graph.getSpace()).addNeighbor(testAgent6, testAgent8);
        ((Graph) graph.getSpace()).addNeighbor(testAgent5, testAgent9);
        ((Graph) graph.getSpace()).addNeighbor(testAgent8, testAgent10, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent9, testAgent10, false);

        assertTrue(((Graph) graph.getSpace()).isNeighbor(testAgent1, testAgent2));
        assertTrue(((Graph) graph.getSpace()).isNeighbor(testAgent1, testAgent5));

        assertTrue(!((Graph) graph.getSpace()).isNeighbor(testAgent2, testAgent1));
        assertTrue(!((Graph) graph.getSpace()).isNeighbor(testAgent5, testAgent1));

        assertTrue(((Graph) graph.getSpace()).isNeighbor(testAgent3, testAgent6));
        assertTrue(((Graph) graph.getSpace()).isNeighbor(testAgent3, testAgent7));

        assertTrue(((Graph) graph.getSpace()).isNeighbor(testAgent8, testAgent10));
        assertTrue(((Graph) graph.getSpace()).isNeighbor(testAgent9, testAgent10));
        assertTrue(((Graph) graph.getSpace()).isNeighbor(testAgent10, testAgent8));
        assertTrue(((Graph) graph.getSpace()).isNeighbor(testAgent10, testAgent9));

    }

    public void testCalculateDistance() {
        graph.initialize();
        graph.clear();

        graph.add(testAgent1);
        graph.add(testAgent2);
        graph.add(testAgent3);
        graph.add(testAgent4);
        graph.add(testAgent5);
        graph.add(testAgent6);
        graph.add(testAgent7);
        graph.add(testAgent8);
        graph.add(testAgent9);
        graph.add(testAgent10);
        graph.add(testAgent11);

        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent2);
        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent5);
        ((Graph) graph.getSpace()).addNeighbor(testAgent2, testAgent3);
        ((Graph) graph.getSpace()).addNeighbor(testAgent2, testAgent4);
        ((Graph) graph.getSpace()).addNeighbor(testAgent3, testAgent6);
        ((Graph) graph.getSpace()).addNeighbor(testAgent3, testAgent7);
        ((Graph) graph.getSpace()).addNeighbor(testAgent6, testAgent8);
        ((Graph) graph.getSpace()).addNeighbor(testAgent5, testAgent9);
        ((Graph) graph.getSpace()).addNeighbor(testAgent8, testAgent10, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent9, testAgent10, false);

        assertTrue(graph.calculateDistance(testAgent1, testAgent1) == 0);
        assertTrue(graph.calculateDistance(testAgent1, testAgent2) == 1);
        assertTrue(graph.calculateDistance(testAgent1, testAgent4) == 2);
        assertTrue(graph.calculateDistance(testAgent1, testAgent10) == 3);
        assertTrue(Double.isNaN(graph.calculateDistance(testAgent10, testAgent1)));
        assertTrue(Double.isNaN(graph.calculateDistance(testAgent1, testAgent11)));
    }

    public void testGetCellToward() {
        graph.initialize();
        graph.clear();

        graph.add(testAgent1);
        graph.add(testAgent2);
        graph.add(testAgent3);
        graph.add(testAgent4);
        graph.add(testAgent5);
        graph.add(testAgent6);
        graph.add(testAgent7);
        graph.add(testAgent8);
        graph.add(testAgent9);
        graph.add(testAgent10);
        graph.add(testAgent11);

        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent2);
        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent5);
        ((Graph) graph.getSpace()).addNeighbor(testAgent2, testAgent3);
        ((Graph) graph.getSpace()).addNeighbor(testAgent2, testAgent4);
        ((Graph) graph.getSpace()).addNeighbor(testAgent3, testAgent6);
        ((Graph) graph.getSpace()).addNeighbor(testAgent4, testAgent6);
        ((Graph) graph.getSpace()).addNeighbor(testAgent3, testAgent7);
        ((Graph) graph.getSpace()).addNeighbor(testAgent6, testAgent8);
        ((Graph) graph.getSpace()).addNeighbor(testAgent5, testAgent9);
        ((Graph) graph.getSpace()).addNeighbor(testAgent8, testAgent10, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent9, testAgent10, false);

        //assertSame(((Graph) graph.getSpace()).findCellToward(testAgent1, testAgent1), testAgent1);
        assertSame(((Graph) graph.getSpace()).findCellToward(testAgent1, testAgent2), testAgent2);
        assertSame(((Graph) graph.getSpace()).findCellToward(testAgent1, testAgent5), testAgent5);
        assertSame(((Graph) graph.getSpace()).findCellToward(testAgent1, testAgent10), testAgent5);
        Cell candidate = (Cell) ((Graph) graph.getSpace()).findCellToward(testAgent2, testAgent6);
        assertTrue((candidate == testAgent3) || (candidate == testAgent4));
        assertSame(((Graph) graph.getSpace()).findCellToward(testAgent10, testAgent1), testAgent10);
    }

    public void testGetCellAway() {
        graph.initialize();
        graph.clear();

        graph.add(testAgent1);
        graph.add(testAgent2);
        graph.add(testAgent3);
        graph.add(testAgent4);
        graph.add(testAgent5);
        graph.add(testAgent6);
        graph.add(testAgent7);
        graph.add(testAgent8);
        graph.add(testAgent9);
        graph.add(testAgent10);
        graph.add(testAgent11);

        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent2);
        ((Graph) graph.getSpace()).addNeighbor(testAgent1, testAgent5);
        ((Graph) graph.getSpace()).addNeighbor(testAgent2, testAgent3);
        ((Graph) graph.getSpace()).addNeighbor(testAgent2, testAgent4);
        ((Graph) graph.getSpace()).addNeighbor(testAgent3, testAgent6);
        ((Graph) graph.getSpace()).addNeighbor(testAgent4, testAgent6);
        ((Graph) graph.getSpace()).addNeighbor(testAgent3, testAgent7);
        ((Graph) graph.getSpace()).addNeighbor(testAgent6, testAgent8);
        ((Graph) graph.getSpace()).addNeighbor(testAgent5, testAgent9);
        ((Graph) graph.getSpace()).addNeighbor(testAgent8, testAgent10, false);
        ((Graph) graph.getSpace()).addNeighbor(testAgent9, testAgent10, false);

        Cell away = (Cell) ((Graph) graph.getSpace()).findCellAway(testAgent1, testAgent1);
        assertTrue((away == testAgent2) || (away == testAgent5));
    }
}
