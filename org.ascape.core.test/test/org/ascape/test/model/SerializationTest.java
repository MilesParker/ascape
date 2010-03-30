/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.test.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.space.SpatialTemporalException;

import junit.framework.TestCase;


/**
 * Tests that the model meets various Ascape design rules.
 *
 * @author    Miles Parker, Matthew Hendrey, and others
 * @created   April 15-August 31, 2001
 */
public class SerializationTest extends TestCase implements Serializable {

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
    public void testSerialization() {
        int stepsToRun = 3;
        Scape model1 = new SerializationTestModel();
        model1.setRandomSeed(123);
        model1.setStartOnOpen(false);
        model1.createScape();
        model1.getRunner().open();
        model1.setAutoRestart(false);
        model1.setPausePeriod(stepsToRun);

        model1.getRunner().start();

        while (model1.getPeriod() < stepsToRun || !model1.isPaused() || !model1.isAllViewsUpdated()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.err.println("" + e);
            }
        }

        // wait a bit longer just to make sure the pause has propogated throughout the model--this may be unnecessary
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            System.err.println("" + e);
        }

        assertTrue(model1.getPeriod() == stepsToRun);

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        // test whether model can be serialized
        try {
            model1.save(os);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        model1.getRunner().close();

        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());

        // now test whether serialized model can be de-serialized correctly
        Scape model2 = null;
        try {
            model2 = model1.getRunner().openSavedRun(is);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }

        // model2 could be null if a ClassNotFoundException occurred in openSavedRun()
        assertTrue(model2 != null);

        assertTrue(model2.getStartPeriod() == model2.getPeriod() + 1);

        stepsToRun += 3;

        model2.setPausePeriod(stepsToRun);

        model2.getRunner().start();

        model2.getRunner().resume();

        while (model2.getPeriod() < stepsToRun || !model2.isPaused() || !model2.isAllViewsUpdated()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.err.println("" + e);
            }
        }

        // wait a bit longer just to make sure the pause has propogated throughout the model--this may be unnecessary
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            System.err.println("" + e);
        }

        assertTrue(model2.getPeriod() == stepsToRun);
        model2.getRunner().close();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.err.println("" + e);
        }
        // We don't want to actually quit, because that will shut the environment down...
        model2.getRunner().getEnvironment().environmentQuiting(new ScapeEvent(model2, ScapeEvent.REQUEST_QUIT));

        // next thing to do would be to deeply compare saved and resumed run with uninterrupted run

        /*
         * Scape model2 = new Scape(); model2.setRandomSeed(123); model2.executeOnRoot(Scape.CREATE_RULE);
         * model2.setAutoRestart(false); try { model2.setStopPeriod(stepsToRun); } catch (SpatialTemporalException e) {
         * throw new RuntimeException("Problem in base model setup"); }
         * 
         * model2.run(); assertTrue(model2.getPeriod() == stepsToRun);
         * 
         * model2.run(); assertTrue(model2.getPeriod() == stepsToRun);
         * 
         * boolean modelsEqual = model1.equalsDeep(model2); if (!modelsEqual) {
         * System.out.println("Model Initialization Test Failed."); System.out.println("Creating InitDiffs3.txt file.");
         * 
         * try { DataOutputStream diffStream = new DataOutputStream(new FileOutputStream(new File("InitDiffs3.txt")));
         * ArrayList diffResults = model1.diffDeep(model2);
         * 
         * Iterator diffIter = diffResults.iterator(); while (diffIter.hasNext()) { String diffLine = (String)
         * diffIter.next(); diffStream.writeBytes(diffLine + EOLString); //System.out.println(diffLine); } } catch
         * (IOException e) { System.out.println("A file io exception happened while saving diff data: " + e); } fail();
         * }
         */
    }

    /**
     * Ensure that the model produces exactly the same results, regardless of whether it has been reinitialized from a
     * previous model. All variable should be properly reset in initialize phase, and this test ensures that that
     * happens. Both this method and the one following produce a file named InitDiffs detailing any differences on
     * failure.
     */
    public void testModelReintialization1() {

        SerializationTestModel model1 = new SerializationTestModel();
        runModel(model1);
        model1.getData().clear();
        model1.reseed();
        model1.getRunner().run();
        long model1Random = model1.getRandom().nextLong();

        SerializationTestModel model2 = new SerializationTestModel();
        runModel(model2);
        model2.getData().clear();
        model2.reseed();
        model2.getRunner().run();
        long model2Random = model2.getRandom().nextLong();
        assertTrue(model1Random == model2Random);
        model2.reseed();
        model2.getRunner().run();

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
                    diffStream.writeBytes(diffLine + "\r\n");
                    //System.out.println(diffLine);
                }
            } catch (IOException e) {
                System.out.println("A file io exception happened while saving diff data: " + e);
            }
            fail("see InitDiffs.txt");
        }
    }

    private void runModel(SerializationTestModel model2) {
        model2.setRandomSeed(123);
        model2.createScape();
        model2.setAutoRestart(false);
        try {
            model2.setStopPeriod(1);
        } catch (SpatialTemporalException e) {
            throw new RuntimeException("Problem in base model setup");
        }
        model2.getRunner().run();
        assertTrue(model2.getPeriod() == 1);
        model2.getRunner().run();
        assertTrue(model2.getPeriod() == 1);
    }

}
