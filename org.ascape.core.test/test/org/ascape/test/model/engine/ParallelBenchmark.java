/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */
package org.ascape.test.model.engine;

import org.ascape.model.Agent;
import org.ascape.model.Cell;
import org.ascape.model.Scape;
import org.ascape.model.rule.ExecuteThenUpdate;
import org.ascape.model.rule.Rule;
import org.ascape.model.space.Array2DMoore;
import org.ascape.model.space.Coordinate2DDiscrete;
import org.ascape.model.space.Space;
import org.ascape.model.space.SpatialTemporalException;

/*
 * User: Miles Parker
 * Date: Feb 11, 2005
 * Time: 2:17:53 PM
 */

public class ParallelBenchmark {

    private int periods = 500;
    private double baseline;

    public ParallelBenchmark() {
    }

    public void execute() {
//        Space space = new Array1D();
//        space.setExtent(new Coordinate1DDiscrete(160000));

//        Space space = new ListSpace(new Coordinate1DDiscrete(160000));
        Space space = new Array2DMoore(new Coordinate2DDiscrete(400, 400));
        experimentSpace(space);
    }

    class IntAgent extends Cell implements ParallelAgent {
        int state;
        int calcState;

        public void initialize() {
            super.initialize();
            state = 0;
            calcState = 0;
        }

        public void incrState() {
            state = function();
        }

        private int function() {
            return state + 3 - 14 / 6 * 8 ^ 2 ^12220 | 423432424;
        }

        public void calcState() {
            calcState = function();
        }

        public void updateState() {
            state = calcState;
        }
    }

    class DoubleAgent extends Cell implements ParallelAgent {
        double state;
        double calcState;
        public void initialize() {
            super.initialize();
            state = 0;
            calcState = 0;
        }

        public void incrState() {
            state = function();
        }

        private double function() {
//            return state + 1;
            return Math.pow(2.3, Math.random()) + 12.32 * Math.sin(23.);// / Math.sqrt(Math.random());
        }

        public void calcState() {
            calcState = function();
        }

        public void updateState() {
            state = calcState;
        }
    }

    public static Rule CALC_STATE = new Rule("Calc State") {
        public void execute(Agent agent) {
            ((ParallelAgent) agent).calcState();
        }
        public boolean isRandomExecution() {
            return false;
        }
        public boolean isCauseRemoval() {
            return false;
        }
    };

    public static Rule UPDATE_STATE = new Rule("Update State") {
        public void execute(Agent agent) {
            ((ParallelAgent) agent).updateState();
        }
        public boolean isRandomExecution() {
            return false;
        }
        public boolean isCauseRemoval() {
            return false;
        }
    };

    public static Rule EXEC_UPDATE_STATE = new ExecuteThenUpdate("Exec and Update State") {
        public void execute(Agent agent) {
            ((ParallelAgent) agent).calcState();
        }

        public void update(Agent agent) {
            ((ParallelAgent) agent).updateState();
        }
        public boolean isRandomExecution() {
            return false;
        }
        public boolean isCauseRemoval() {
            return false;
        }
    };

    public static Rule INCR_STATE = new Rule("Increment State") {
        public void execute(Agent agent) {
            ((ParallelAgent) agent).incrState();
        }

        public boolean isRandomExecution() {
            return false;
        }
        public boolean isCauseRemoval() {
            return false;
        }
    };

    public void experimentSpace(Space space) {
        Scape root = new Scape();
        root.setAutoRestart(false);
        root.setAutoCreate(false);
        root.setPopulateOnCreate(false);
        root.setPrototypeAgent(new Scape());
        Scape testScape = new Scape();
        root.add(testScape);
//        testScape.setPrototypeAgent(new IntAgent());
        testScape.setPrototypeAgent(new DoubleAgent());
//        testScape.setPrototypeAgent(new LifeCell());
        testScape.setSpace(space);
        testScape.setExecutionOrder(Scape.RULE_ORDER);
        testScape.getRules().clear();
//        testScape.addRule(CALC_STATE);
//        testScape.addRule(UPDATE_STATE);
//        testScape.addRule(INCR_STATE);
        testScape.addRule(EXEC_UPDATE_STATE);

//        testScape.addRule(ConwayLife.NEXT_STATE_SYNCHRONOUS);
        try {
            root.setStopPeriod(periods);
        } catch (SpatialTemporalException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }

        experiment(root, 1);
        experiment(root, 2);
        //experiment(testScape, 4);
        System.exit(0);
    }

    public static final void main(String[] args) {
        (new ParallelBenchmark()).execute();
    }

    private void experiment(Scape scape, int threadCount) {
        System.out.println("Threads: " + threadCount);
        scape.setThreadCount(threadCount);
        long startTime = System.currentTimeMillis();
        scape.getRunner().testRun();
        long endTime = System.currentTimeMillis();
        long runTimeMillis = endTime - startTime;
        double runTimeSecs = runTimeMillis / 1000.;
        if (threadCount == 1) {
            System.out.println("Run Time: " + runTimeSecs);
            baseline = runTimeSecs;
        } else {
            System.out.println("Run Time: " + runTimeSecs);
            System.out.println("Speedup: " + (baseline / runTimeSecs - 1) * 100);
        }
//        ((Scape) scape.get(0)).executeOnMembers(new Rule("Test Results") {
//            public void execute(Agent agent) {
//                if(((DoubleAgent) agent).state != periods) {
//                    System.out.println("agent = " + agent);
//                    System.out.println("((DoubleAgent) agent).state = " + ((DoubleAgent) agent).state);
//                    System.exit(0);
//                    throw new RuntimeException("Bad execution.");
//                }
//            }
//
//            public boolean isRandomExecution() {
//                return false;
//            }
//        });
    }
}
