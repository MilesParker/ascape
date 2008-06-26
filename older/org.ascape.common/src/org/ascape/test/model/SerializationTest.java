/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.test.model;

import java.io.Serializable;

import junit.framework.TestCase;

import org.ascape.model.Cell;
import org.ascape.model.Scape;
import org.ascape.view.vis.Overhead2DView;


/**
 * Tests that the model meets various Ascape design rules.
 *
 * @author    Miles Parker, Matthew Hendrey, and others
 * @created   April 15-August 31, 2001
 */
public class SerializationTest extends TestCase implements Serializable {

    @SuppressWarnings("unused")
    private class SimplerTestModel extends Scape {

        public void createScape() {
            setSerializable(true);
            setAutoRestart(false);
            setExtent(2, 2);
            setPrototypeAgent(new Cell());
            super.createScape();
            setExecutionOrder(RULE_ORDER);
            getRules().clear();
            addRule(ITERATE_AND_UPDATE_RULE);
        }

        public void createViews() {
            super.createViews();
/*
            StatCollectorCond aliveStat = new StatCollectorCond("Alive") {
                public boolean meetsCondition(Object o) {
                    return ((LifeCell) o).isAlive();
                }
            };
            addStatCollector(aliveStat);
*/
            addView(new Overhead2DView());
        }

        /**
         * For testing purposes we override quitFinally() so that the test runner does not exit
         * when we close this model.
         */
/*
        public void quitFinally() {
            closeFinally();
        }
*/
    }

    /**
     * Constructs an instance of SerializationTest.
     *
     * @param name  the name of the test
     */
    public SerializationTest(String name) {
        super(name);
    }

    /**
     * Compares a run from a newly instantiated model with a run from a model
     * that has been restarted once.
     */
//    public void testSerialization() {
//        int stepsToRun = 3;
//        Scape model1 = new SerializationTestModel();
//        model1.setRandomSeed(123);
//        model1.setStartOnOpen(false);
//        model1.open();
//        model1.setAutoRestart(false);
//        model1.setPausePeriod(stepsToRun);
//
//        (new Thread(model1)).start();
//
//        while (model1.getPeriod() < stepsToRun || !model1.isPaused() || !model1.isAllViewsUpdated()) {
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                System.err.println(""+e);
//            }
//        }
//
//        // wait a bit longer just to make sure the pause has propogated throughout the model--this may be unnecessary
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            System.err.println(""+e);
//        }
//
//        assertTrue(model1.getPeriod() == stepsToRun);
//
//        ByteArrayOutputStream os = new ByteArrayOutputStream();
//
//        // test whether model can be serialized
//        try {
//            model1.save(os);
//        } catch (IOException e) {
//            e.printStackTrace();
//            fail();
//        }
//
//        model1.close();
//
//        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
//
//        // now test whether serialized model can be de-serialized correctly
//        Scape model2 = null;
//        try {
//            model2 = Scape.openSavedRun(is);
//        } catch (IOException e) {
//            e.printStackTrace();
//            fail();
//        }
//
//        // model2 could be null if a ClassNotFoundException occurred in openSavedRun()
//        assertTrue(model2 != null);
//
//        assertTrue(model2.getStartPeriod() == model2.getPeriod() + 1);
//
//        stepsToRun += 3;
//
//        model2.setPausePeriod(stepsToRun);
//
//        (new Thread(model2)).start();
//
//        model2.resume();
//
//        while (model2.getPeriod() < stepsToRun || !model2.isPaused() || !model2.isAllViewsUpdated()) {
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                System.err.println(""+e);
//            }
//        }
//
//        // wait a bit longer just to make sure the pause has propogated throughout the model--this may be unnecessary
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            System.err.println(""+e);
//        }
//
//        assertTrue(model2.getPeriod() == stepsToRun);
//        model2.close();
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            System.err.println(""+e);
//        }
//        //We don't want to actually quit, because that will shut the environment down...
//        model2.getDefaultEnvironment().environmentQuiting(new ScapeEvent(model2, ScapeEvent.REQUEST_QUIT));
//
//        // next thing to do would be to deeply compare saved and resumed run with uninterrupted run
//
///*
//        Scape model2 = new Scape();
//        model2.setRandomSeed(123);
//        model2.executeOnRoot(Scape.CREATE_RULE);
//        model2.setAutoRestart(false);
//        try {
//            model2.setStopPeriod(stepsToRun);
//        } catch (SpatialTemporalException e) {
//            throw new RuntimeException("Problem in base model setup");
//        }
//
//        model2.run();
//        assertTrue(model2.getPeriod() == stepsToRun);
//
//        model2.run();
//        assertTrue(model2.getPeriod() == stepsToRun);
//
//        boolean modelsEqual = model1.equalsDeep(model2);
//        if (!modelsEqual) {
//            System.out.println("Model Initialization Test Failed.");
//            System.out.println("Creating InitDiffs3.txt file.");
//
//            try {
//                DataOutputStream diffStream = new DataOutputStream(new FileOutputStream(new File("InitDiffs3.txt")));
//                ArrayList diffResults = model1.diffDeep(model2);
//
//                Iterator diffIter = diffResults.iterator();
//                while (diffIter.hasNext()) {
//                    String diffLine = (String) diffIter.next();
//                    diffStream.writeBytes(diffLine + EOLString);
//                    //System.out.println(diffLine);
//                }
//            } catch (IOException e) {
//                System.out.println("A file io exception happened while saving diff data: " + e);
//            }
//            fail();
//        }
//*/
//    }

    /**
     * Ensure that the model produces exatly the same results, regardless of wether
     * it has been reinitialized from a previous model. All variable should be properly reset
     * in initialize pahse, and this test ensures that that happens.
     * Both this method and the one following prodice a file named InitDiffs detainling any differences
     * on failure.
     */
/*
    public void testModelReintialization1() {

        BaseModel model1 = new BaseModel();
        model1.setRandomSeed(123);
        model1.executeOnRoot(Scape.CREATE_RULE);
        model1.setAutoRestart(false);
        try {
            model1.setStopPeriod(1);
        } catch (SpatialTemporalException e) {
            throw new RuntimeException("Problem in base model setup");
        }
        model1.run();
        assertTrue(model1.getPeriod() == 1);
        model1.stop();
        model1.run();
        assertTrue(model1.getPeriod() == 1);
        model1.stop();
        model1.getData().clear();
        model1.reseed();
        long model1Random = model1.getRandom().nextLong();
        model1.executeOnRoot(Scape.CREATE_RULE);
        model1.executeOnRoot(Scape.CLEAR_STATS_RULE);
        model1.executeOnRoot(Scape.START_RULE);

        BaseModel model2 = new BaseModel();
        model2.setRandomSeed(123);
        model2.executeOnRoot(Scape.CREATE_RULE);
        model2.setAutoRestart(false);
        try {
            model2.setStopPeriod(1);
        } catch (SpatialTemporalException e) {
            throw new RuntimeException("Problem in base model setup");
        }
        model2.run();
        assertTrue(model2.getPeriod() == 1);
        model2.stop();
        model2.run();
        assertTrue(model2.getPeriod() == 1);
        model2.stop();
        model2.run();
        assertTrue(model2.getPeriod() == 1);
        model2.stop();
        model2.getData().clear();
        model2.reseed();
        long model2Random = model2.getRandom().nextLong();
        assertTrue(model1Random == model2Random);
        model2.executeOnRoot(Scape.CREATE_RULE);
        model2.executeOnRoot(Scape.CLEAR_STATS_RULE);
        model2.executeOnRoot(Scape.START_RULE);

        boolean modelsEqual = model1.equalsDeep(model2);
        if (!modelsEqual) {
            System.out.println("Model Initialization Test Failed.");
            System.out.println("Creating InitDiffs.txt file.");

            try {
                DataOutputStream diffStream = new DataOutputStream(new FileOutputStream(new File("InitDiffs.txt")));
                ArrayList diffResults = model1.diffDeep(model2);

                Iterator diffIter = diffResults.iterator();
                while (diffIter.hasNext()) {
                    String diffLine = (String) diffIter.next();
                    diffStream.writeBytes(diffLine + EOLString);
                    //System.out.println(diffLine);
                }
            } catch (IOException e) {
                System.out.println("A file io exception happened while saving diff data: " + e);
            }
            fail();
        }
    }
*/

    /**
     * Ensure that the model produces exatly the same results, regardless of wether
     * it has been reinitialized from a previous model. All variable should be properly reset
     * in initialize pahse, and this test ensures that that happens.
     * (If there is a problem, this method might give more revealing (alothough more extensive)
     * information.
     *
     */
/*
    public void testModelReintialization2() {

        BaseModel model1 = new BaseModel();
        model1.setRandomSeed(123);
        model1.executeOnRoot(Scape.CREATE_RULE);
        model1.setAutoRestart(false);
        try {
            model1.setStopPeriod(10);
        } catch (SpatialTemporalException e) {
            throw new RuntimeException("Problem in base model setup");
        }
        model1.run();
        assertTrue(model1.getPeriod() == 10);
        model1.stop();
        model1.run();
        assertTrue(model1.getPeriod() == 10);
        model1.stop();
        model1.getData().clear();
        model1.reseed();
        long model1Random = model1.getRandom().nextLong();
        model1.executeOnRoot(Scape.CREATE_RULE);
        model1.executeOnRoot(Scape.CLEAR_STATS_RULE);
        model1.executeOnRoot(Scape.START_RULE);

        BaseModel model2 = new BaseModel();
        model2.setRandomSeed(123);
        model2.executeOnRoot(Scape.CREATE_RULE);
        model2.setAutoRestart(false);
        try {
            model2.setStopPeriod(10);
        } catch (SpatialTemporalException e) {
            throw new RuntimeException("Problem in base model setup");
        }
        model2.run();
        assertTrue(model2.getPeriod() == 10);
        model2.stop();
        model2.run();
        assertTrue(model2.getPeriod() == 10);
        model2.stop();
        model2.run();
        assertTrue(model2.getPeriod() == 10);
        model2.stop();
        model2.getData().clear();
        model2.reseed();
        long model2Random = model2.getRandom().nextLong();
        assertTrue(model1Random == model2Random);
        model2.executeOnRoot(Scape.CREATE_RULE);
        model2.executeOnRoot(Scape.CLEAR_STATS_RULE);
        model2.executeOnRoot(Scape.START_RULE);

        boolean modelsEqual = model1.equalsDeep(model2);
        if (!modelsEqual) {
            System.out.println("Model Initialization Test Failed.");
            System.out.println("Creating InitDiffs2.txt file.");

            try {
                DataOutputStream diffStream = new DataOutputStream(new FileOutputStream(new File("InitDiffs2.txt")));
                ArrayList diffResults = model1.diffDeep(model2);

                Iterator diffIter = diffResults.iterator();
                while (diffIter.hasNext()) {
                    String diffLine = (String) diffIter.next();
                    diffStream.writeBytes(diffLine + EOLString);
                    //System.out.println(diffLine);
                }
            } catch (IOException e) {
                System.out.println("A file io exception happened while saving diff data: " + e);
            }
            fail();
        }
    }
*/
}
