/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.test.model.event;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.ascape.model.Cell;
import org.ascape.model.Scape;
import org.ascape.model.space.GraphSmallWorld;

public class ScapeGraphSmallWorldTest extends TestCase {

    Scape graph;

    class TestAgent extends Cell {

        boolean testCondition;

        public boolean isTestCondition() {
            return testCondition;
        }

        public void setTestCondition(boolean testCondition) {
            this.testCondition = testCondition;
        }
    }

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

    public ScapeGraphSmallWorldTest(String name) {
        super(name);
//
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
    }

    public void testInitialize() {
        graph = new Scape(new GraphSmallWorld());
        ((GraphSmallWorld) graph.getSpace()).setAdjacencyMap(new HashMap());

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

        for (Iterator iterator = graph.iterator(); iterator.hasNext();) {
            TestAgent agent = (TestAgent) iterator.next();
            List neighbors = agent.findNeighbors();
            assertTrue(neighbors.size() == 0);
        }

        ((GraphSmallWorld) graph.getSpace()).setRandomEdgeRatio(0.0);

        graph.initialize();

        // given no random edges, all agents should have exactly 2 neighbors
        for (Iterator iterator = graph.iterator(); iterator.hasNext();) {
            TestAgent agent = (TestAgent) iterator.next();
            List neighbors = agent.findNeighbors();
            assertTrue(neighbors.size() == 2);
        }

        ((GraphSmallWorld) graph.getSpace()).setRandomEdgeRatio(1.0);

        // clear all neighbors
        for (Iterator iterator = graph.iterator(); iterator.hasNext();) {
            TestAgent agent = (TestAgent) iterator.next();
            ((GraphSmallWorld) graph.getSpace()).clearNeighbors(agent);
        }
        graph.initialize();

        // given all random edges created, all agents should have the number of neighbors
        // equal to total number of agents minus one.
        for (Iterator iterator = graph.iterator(); iterator.hasNext();) {
            TestAgent agent = (TestAgent) iterator.next();
            List neighbors = agent.findNeighbors();
            assertTrue(neighbors.size() == 3);
        }
    }

}
