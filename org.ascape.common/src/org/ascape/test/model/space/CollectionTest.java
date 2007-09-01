/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.test.model.space;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.ascape.model.Agent;
import org.ascape.model.Cell;
import org.ascape.model.Scape;
import org.ascape.model.rule.Rule;
import org.ascape.model.space.ListSpace;
import org.ascape.model.space.Location;
import org.ascape.util.RandomIterator;
import org.ascape.util.ResetableIterator;

/*
\ * User: Miles Parker
 * Date: Sep 3, 2003
 * Time: 3:29:58 PM
 * To change this template use Options | File Templates.
 */

public class CollectionTest extends TestCase {

    public CollectionTest(String name) {
        super(name);
    }

    class TestAgent extends Cell {

        int id;
        int allID;
    }

    Scape testScape;
    int seqID = 0;
    int allSeqID = 0;
    static boolean randomRuleExecution;

    public Rule INCREMENT_SEQUENTIALLY = new Rule("") {
        public void execute(Agent agent) {
            ((TestAgent) agent).id = seqID;
            seqID++;
        }

        public boolean isRandomExecution() {
            return randomRuleExecution;
        }
    };

    public Rule INCREMENT_SEQUENTIALLY_ALL = new Rule("") {
        public void execute(Agent agent) {
            ((TestAgent) agent).allID = allSeqID;
            allSeqID++;
        }

        public boolean isRandomExecution() {
            return randomRuleExecution;
        }

        public boolean isIterateAll() {
            return true;
        }
    };

    static int testIndex = 3;

    int callID;

    public Rule INCREMENT_SEQUENTIALLY_DELETE = new Rule("") {
        boolean firstTime;

        public void execute(Agent agent) {
            if (seqID == 0) {
                firstTime = true;
                callID = 0;
            }
            ((TestAgent) agent).id = seqID;
            if (firstTime && (callID == testIndex)) {
                testScape.remove(agent);
                firstTime = false;
            } else {
                seqID++;
                callID++;
            }
        }

        public boolean isRandomExecution() {
            return randomRuleExecution;
        }
    };

    public Rule INCREMENT_SEQUENTIALLY_ADD = new Rule("") {
        boolean firstTime;

        public void execute(Agent agent) {
            if (seqID == 0) {
                firstTime = true;
                callID = 0;
            }
            ((TestAgent) agent).id = seqID;
            if (firstTime && (callID == testIndex)) {
                testScape.add(new TestAgent());
                firstTime = false;
            } else {
                callID++;
            }
            seqID++;
        }

        public boolean isRandomExecution() {
            return randomRuleExecution;
        }
    };

    private void performRuleTest(Rule[] rules, int order, boolean random, int iterations, int startID, int incrementID, int skip) {
        seqID = 0;
        allSeqID = 0;
//        if ((testScape.getExecutionStyle() == Scape.REPEATED_DRAW) && (iterations < testScape.size())) {
//            Rule[] replaceRules = new Rule[rules.length - 1];
//            System.arraycopy(rules, 0, replaceRules, 0, replaceRules.length);
//            rules = replaceRules;
//        }
        randomRuleExecution = random;
        ((TestSpaceCollection) testScape.getSpace()).resetFindRandom();
        testScape.setExecutionOrder(order);
        testScape.setAgentsPerIteration(iterations);
        testScape.executeOnMembers(rules);
        if (iterations == Scape.ALL_AGENTS) {
            int i = 0;
            for (Iterator iterator = testScape.iterator(); iterator.hasNext();) {
                TestAgent agent = (TestAgent) iterator.next();
                assertTrue(agent.id == startID);
                startID += incrementID;
                i++;
                if (i == testScape.size() - 1) {
                    if (skip != -1) {
                        agent = (TestAgent) iterator.next();
                        assertTrue(agent.id == 0);
                    }
                }
            }
        } else {
            if (testScape.getExecutionStyle() != Scape.REPEATED_DRAW) {
                int allMatchID = 0;
                for (int i = testScape.size() - 1; i >= 0; i -= 1) {
                    if (((rules[0] == INCREMENT_SEQUENTIALLY_ADD) && (i == testScape.size() - 1))) {
                        i -= 1;
                    }
                    TestAgent agent = (TestAgent) testScape.toArray()[i];
                    if (i >= testScape.size() - iterations) {
                        assertTrue(agent.id == startID);
                    }
                    assertTrue(agent.allID == allMatchID);
                    startID += incrementID;
                    if (i == testScape.size() - 1) {
                        if (skip != -1) {
                            agent = (TestAgent) testScape.toArray()[0];
                            assertTrue(agent.id == 0);
                        }
                    }
                    allMatchID++;
                }
            } else {
                int i = 0;
                int allMatchID = testScape.size() - 1;
                for (Iterator iterator = testScape.iterator(); iterator.hasNext();) {
                    TestAgent agent = (TestAgent) iterator.next();
                    if (i < iterations - 1) {
                        assertTrue(agent.id == startID);
                    }
                    assertTrue(agent.allID == allMatchID);
                    startID += incrementID;
                    i++;
                    if (i == testScape.size() - 1) {
                        if (skip != -1) {
                            agent = (TestAgent) testScape.toArray()[0];
                            //assert(agent.id == 0);
                        }
                    }
                    allMatchID--;
                }
            }
        }
    }

    private void performRuleTest(Rule[] rules, int order, boolean random, int iterations, int startID, int incrementID) {
        performRuleTest(rules, order, random, iterations, startID, incrementID, -1);
    }

    private void performRuleTest(Rule[] rules, int order, boolean random, int startID, int incrementID) {
        performRuleTest(rules, order, random, Scape.ALL_AGENTS, startID, incrementID);
    }

    private void performRuleTestWithRestorAndTest(Rule[] rules, int order, boolean random, int startID, int incrementID) {
        performRuleTestWithRestorAndTest(rules, order, random, Scape.ALL_AGENTS, startID, incrementID);
    }

    private void performRuleTestWithRestorAndTest(Rule[] rules, int order, boolean random, int iterations, int startID, int incrementID) {
        ((ListSpace) testScape.getSpace()).add(0, new TestAgent());
        performRuleTest(rules, order, random, iterations, startID, incrementID);
        assertTrue(testScape.size() == 9);
    }

    private void performRuleTestWithDeleteAndTest(Rule[] rules, int order, boolean random, int startID, int incrementID, int skip) {
        performRuleTestWithDeleteAndTest(rules, order, random, Scape.ALL_AGENTS, startID, incrementID, skip);
    }

    private void performRuleTestWithDeleteAndTest(Rule[] rules, int order, boolean random, int iterations, int startID, int incrementID, int skip) {
        assertTrue(testScape.size() == 10);
        performRuleTest(rules, order, random, iterations, startID, incrementID, skip);
        ((ListSpace) testScape.getSpace()).remove(3);
        assertTrue(testScape.size() == 10);
    }

    private void performTestForStyle() {
        Rule[] incrementOnce = new Rule[2];
        incrementOnce[0] = INCREMENT_SEQUENTIALLY;
        incrementOnce[1] = INCREMENT_SEQUENTIALLY_ALL;

        Rule[] incrementTwice = new Rule[3];
        incrementTwice[0] = INCREMENT_SEQUENTIALLY;
        incrementTwice[1] = INCREMENT_SEQUENTIALLY;
        incrementTwice[2] = INCREMENT_SEQUENTIALLY_ALL;

        Rule[] incrementDeleteOnce = new Rule[2];
        incrementDeleteOnce[0] = INCREMENT_SEQUENTIALLY_DELETE;
        incrementDeleteOnce[1] = INCREMENT_SEQUENTIALLY_ALL;

        Rule[] incrementDeleteThenIncrement = new Rule[3];
        incrementDeleteThenIncrement[0] = INCREMENT_SEQUENTIALLY_DELETE;
        incrementDeleteThenIncrement[1] = INCREMENT_SEQUENTIALLY;
        incrementDeleteThenIncrement[2] = INCREMENT_SEQUENTIALLY_ALL;

        Rule[] incrementAddOnce = new Rule[2];
        incrementAddOnce[0] = INCREMENT_SEQUENTIALLY_ADD;
        incrementAddOnce[1] = INCREMENT_SEQUENTIALLY_ALL;

        Rule[] incrementAddThenIncrement = new Rule[3];
        incrementAddThenIncrement[0] = INCREMENT_SEQUENTIALLY_ADD;
        incrementAddThenIncrement[1] = INCREMENT_SEQUENTIALLY;
        incrementAddThenIncrement[2] = INCREMENT_SEQUENTIALLY_ALL;

        //increment times * order * random
        performRuleTest(incrementOnce, Scape.AGENT_ORDER, false, 0, 1);
        performRuleTest(incrementOnce, Scape.AGENT_ORDER, true, 9, -1);
        performRuleTest(incrementOnce, Scape.RULE_ORDER, false, 0, 1);
        performRuleTest(incrementOnce, Scape.RULE_ORDER, true, 9, -1);
        performRuleTest(incrementTwice, Scape.AGENT_ORDER, false, 1, 2);
        performRuleTest(incrementTwice, Scape.RULE_ORDER, false, 10, 1);
        performRuleTest(incrementTwice, Scape.AGENT_ORDER, true, 19, -2);
        performRuleTest(incrementTwice, Scape.RULE_ORDER, true, 19, -1);

        List previousList = new ArrayList(testScape);
        TestAgent removedAgent = (TestAgent) previousList.get(4);
        testScape.remove(removedAgent);
        removedAgent.id = 1000;
        assertTrue(previousList.contains(removedAgent));
        performRuleTest(incrementOnce, Scape.AGENT_ORDER, false, 0, 1);
        performRuleTest(incrementOnce, Scape.AGENT_ORDER, true, 8, -1);
        performRuleTest(incrementOnce, Scape.RULE_ORDER, false, 0, 1);
        performRuleTest(incrementOnce, Scape.RULE_ORDER, true, 8, -1);
        performRuleTest(incrementTwice, Scape.AGENT_ORDER, false, 1, 2);
        performRuleTest(incrementTwice, Scape.RULE_ORDER, false, 9, 1);
        performRuleTest(incrementTwice, Scape.AGENT_ORDER, true, 17, -2);
        performRuleTest(incrementTwice, Scape.RULE_ORDER, true, 17, -1);
        assertTrue(removedAgent.id == 1000);

        assertTrue(testScape.size() == 9);

        performRuleTestWithRestorAndTest(incrementDeleteOnce, Scape.AGENT_ORDER, false, 0, 1);
        performRuleTestWithRestorAndTest(incrementDeleteOnce, Scape.AGENT_ORDER, true, 8, -1);
        performRuleTestWithRestorAndTest(incrementDeleteOnce, Scape.RULE_ORDER, false, 0, 1);
        performRuleTestWithRestorAndTest(incrementDeleteOnce, Scape.RULE_ORDER, true, 8, -1);
        performRuleTestWithRestorAndTest(incrementDeleteThenIncrement, Scape.AGENT_ORDER, false, 1, 2);
        //todo we need to look at these for random draw case
        if (testScape.getExecutionStyle() != Scape.REPEATED_DRAW) {
            performRuleTestWithRestorAndTest(incrementDeleteThenIncrement, Scape.RULE_ORDER, false, 9, 1);
        }
        performRuleTestWithRestorAndTest(incrementDeleteThenIncrement, Scape.AGENT_ORDER, true, 17, -2);
        //todo we need to look at these for random draw case
        if (testScape.getExecutionStyle() != Scape.REPEATED_DRAW) {
            performRuleTestWithRestorAndTest(incrementDeleteThenIncrement, Scape.RULE_ORDER, true, 17, -1);
        }
        ((ListSpace) testScape.getSpace()).add(0, new TestAgent());

        performRuleTestWithDeleteAndTest(incrementAddOnce, Scape.AGENT_ORDER, false, 0, 1, testIndex);
        performRuleTestWithDeleteAndTest(incrementAddOnce, Scape.AGENT_ORDER, true, 9, -1, testIndex);
        performRuleTestWithDeleteAndTest(incrementAddOnce, Scape.RULE_ORDER, false, 0, 1, testIndex);
        performRuleTestWithDeleteAndTest(incrementAddOnce, Scape.RULE_ORDER, true, 9, -1, testIndex);
        performRuleTestWithDeleteAndTest(incrementAddThenIncrement, Scape.AGENT_ORDER, false, 1, 2, testIndex);
        performRuleTestWithDeleteAndTest(incrementAddThenIncrement, Scape.RULE_ORDER, false, 10, 1, testIndex);
        performRuleTestWithDeleteAndTest(incrementAddThenIncrement, Scape.AGENT_ORDER, true, 19, -2, testIndex);
        //todo we need to look at these for random draw case
        if (testScape.getExecutionStyle() != Scape.REPEATED_DRAW) {
            performRuleTestWithDeleteAndTest(incrementAddThenIncrement, Scape.RULE_ORDER, true, 19, -1, testIndex);
        }

        //Add with agents per iteration

        //only 5 agents per iteration
        //This will force random execution, so both results should return the same
        //These test won't run under draw random because it requires a unique ordering
        performRuleTest(incrementOnce, Scape.AGENT_ORDER, false, 5, 0, 1);
        performRuleTest(incrementOnce, Scape.AGENT_ORDER, true, 5, 0, 1);
        performRuleTest(incrementOnce, Scape.RULE_ORDER, false, 5, 0, 1);
        performRuleTest(incrementOnce, Scape.RULE_ORDER, true, 5, 0, 1);
        performRuleTest(incrementTwice, Scape.AGENT_ORDER, false, 5, 1, 2);
        //todo we need to look at these for random draw case
        if (testScape.getExecutionStyle() != Scape.REPEATED_DRAW) {
            performRuleTest(incrementTwice, Scape.RULE_ORDER, false, 5, 5, 1);
        }
        performRuleTest(incrementTwice, Scape.AGENT_ORDER, true, 5, 1, 2);
        //todo we need to look at these for random draw case
        if (testScape.getExecutionStyle() != Scape.REPEATED_DRAW) {
            performRuleTest(incrementTwice, Scape.RULE_ORDER, true, 5, 5, 1);
        }
        if (testScape.getExecutionStyle() != Scape.REPEATED_DRAW) {
            //These will fail because under repeated draw, it _is_ currently possible to draw an agent that has just been created
            performRuleTestWithDeleteAndTest(incrementAddOnce, Scape.AGENT_ORDER, false, 5, 0, 1, testIndex);
            performRuleTestWithDeleteAndTest(incrementAddOnce, Scape.AGENT_ORDER, true, 5, 0, 1, testIndex);
            performRuleTestWithDeleteAndTest(incrementAddOnce, Scape.RULE_ORDER, false, 5, 0, 1, testIndex);
            performRuleTestWithDeleteAndTest(incrementAddOnce, Scape.RULE_ORDER, true, 5, 0, 1, testIndex);
            performRuleTestWithDeleteAndTest(incrementAddThenIncrement, Scape.AGENT_ORDER, false, 5, 1, 2, testIndex);
            performRuleTestWithDeleteAndTest(incrementAddThenIncrement, Scape.RULE_ORDER, false, 5, 5, 1, testIndex);
            performRuleTestWithDeleteAndTest(incrementAddThenIncrement, Scape.AGENT_ORDER, true, 5, 1, 2, testIndex);
            performRuleTestWithDeleteAndTest(incrementAddThenIncrement, Scape.RULE_ORDER, true, 5, 5, 1, testIndex);
        }

        testScape.setIterationsPerRedraw(Scape.ALL_AGENTS);
    }

    public void performTestExecuteOnMembers(int threads) {
        testScape = new Scape(new TestSpaceCollection());
        testScape.setPrototypeAgent(new TestAgent());
        testScape.setSize(10);
        testScape.createScape();
        testScape.initialize();

        testScape.setExecutionStyle(Scape.COMPLETE_TOUR);
        performTestForStyle();

        testScape.setExecutionStyle(Scape.REPEATED_DRAW);
        performTestForStyle();
    }

    public void testExecuteOnMembers1() {
        performTestExecuteOnMembers(1);
    }

    public void testExecuteOnMembers2() {
        performTestExecuteOnMembers(2);
    }

    public void testExecuteOnMembers3() {
        performTestExecuteOnMembers(3);
    }

    public void testExecuteOnMembers4() {
        performTestExecuteOnMembers(4);
    }

    public void testExecuteOnMembers5() {
        performTestExecuteOnMembers(5);
    }

    private static class TestSpaceCollection extends ListSpace {

        public TestSpaceCollection() {
            super();
            collection = new ArrayList();
        }

        public Iterator iterator() {
            return super.iterator();
        }

        public class ScapeListBackwardIterator extends CSMutableIterator implements RandomIterator {

            public ScapeListBackwardIterator() {
                super();
                randomize();
            }

            public void randomize() {
                //fake randomization -- we're just reversing the order
                for (int s = 0; s < collection.size() / 2; s++) {
                    Collections.swap(copy, s, collection.size() - 1 - s);
                }
            }
        }

        public RandomIterator safeRandomIterator() {
            return new ScapeListBackwardIterator();
        }

        Iterator fakeRandomIter;

        public void resetFindRandom() {
            if (!randomRuleExecution) {
                fakeRandomIter = (new ArrayList(collection)).iterator();
            } else {
                //fake randomization -- we're just reversing the order
                List fakeArray = new ArrayList(collection);
                for (int s = 0; s < collection.size() / 2; s++) {
                    Collections.swap(fakeArray, s, collection.size() - 1 - s);
                }
                fakeRandomIter = fakeArray.iterator();
            }
        }

        public Location findRandom() {
            Location a = null;
            while (fakeRandomIter.hasNext() && ((a == null) || (a.isDelete()))) {
                a = (Location) fakeRandomIter.next();
            }
            //We need to start over; kind of a hack...
            if (a == null) {
                resetFindRandom();
                a = findRandom();
            }
            return a;
        }
    }

    class TestCell extends Cell {

        int tag;
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
                TestCell cell = (TestCell) iterator.next();
                cell.tag = id;
            }
        }
    };

    public void testScapeMultiIterators() {
        Scape scape = new Scape();
        scape.setSize(100);
        scape.setPrototypeAgent(new TestCell());
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
        int count = 0;
        for (Iterator iterator = scape.iterator(); iterator.hasNext();) {
            TestCell testCell = (TestCell) iterator.next();
            if (count < 25) {
                assertTrue(testCell.tag == 0);
            } else if (count < 50) {
                assertTrue(testCell.tag == 1);
            } else if (count < 75) {
                assertTrue(testCell.tag == 2);
            } else if (count < 100) {
                assertTrue(testCell.tag == 3);
            } else {
                fail();
            }
            count++;
        }
    }

    //We need some way to check for deletes during rule processing
    //If any agent is deleted, we should do that many fewer iterations for the all agents case
    //wheas we should do exactly the number specified for the agents per iteration case.
//                    if (executionOrder == AGENT_ORDER) {
//                        for (int i = 0; i < ((agentsPerIteration == Scape.ALL_AGENTS) ? iterationCount : agentsPerIteration); i++) {
//                            Agent randomAgent = findRandom();
//                            for (int ruleIndex = 0; ruleIndex < rules.length; ruleIndex++) {
//                                ((Rule) rules[ruleIndex]).execute(randomAgent);
//                            }
//                        }
//                    } else { //executionOrder == RULE_ORDER
//                        for (int ruleIndex = 0; ruleIndex < rules.length; ruleIndex++) {
//                            for (int i = 0; i < ((agentsPerIteration == Scape.ALL_AGENTS) ? iterationCount : agentsPerIteration); i++) {
//                                Agent randomAgent = findRandom();
//                                ((Rule) rules[ruleIndex]).execute(randomAgent);
//                            }
//                        }
//                    }
}
