/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */
package org.ascape.test.view;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.ascape.model.Cell;
import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.space.Array2D;
import org.ascape.model.space.Array2DMoore;
import org.ascape.model.space.Coordinate1DDiscrete;
import org.ascape.model.space.Coordinate2DDiscrete;
import org.ascape.model.space.SpatialTemporalException;
import org.ascape.view.nonvis.ScapeFromXMLView;

/**
 User: jmiller Date: Jan 10, 2006 Time: 2:50:32 PM To change this template use Options |
 * File Templates.
 */

// This was taken from the ScapeFromFileViewTest.
public class ScapeFromXMLViewTest extends TestCase {

    public ScapeFromXMLViewTest(String s) {
        super(s);
    }

    public void testEquals() {
        ScapeFromXMLViewTest.TestAgent comp1 = new ScapeFromXMLViewTest.TestAgent(1, "One", 1.1);
        ScapeFromXMLViewTest.TestAgent comp2 = new ScapeFromXMLViewTest.TestAgent(1, "One", 1.1);
        assertTrue(comp1.equals(comp2));
        ScapeFromXMLViewTest.TestAgent comp3 = new ScapeFromXMLViewTest.TestAgent(1, "One", 1.2);
        assertTrue(!comp1.equals(comp3));
        ScapeFromXMLViewTest.TestAgent comp4 = new ScapeFromXMLViewTest.TestAgent(1, "Two", 1.1);
        assertTrue(!comp1.equals(comp4));
        ScapeFromXMLViewTest.TestAgent comp5 = new ScapeFromXMLViewTest.TestAgent(2, "One", 1.1);
        assertTrue(!comp1.equals(comp5));
    }

    public void testPopulationDefinedByFile() {
        Scape testScape = new Scape();
        testScape.setAutoCreate(false);
        testScape.setAutoRestart(false);
        ScapeFromXMLView fileView = new ScapeFromXMLView();
        fileView.parseStream(ScapeFromXMLViewTest.class.getResourceAsStream("ScapeFileMutableTestData.xml"));
        testScape.addView(fileView);
        fileView.setMode(ScapeFromXMLView.SIZE_BY_FILE);
        fileView.setAssignmentOrder(ScapeFromXMLView.SEQUENTIAL_ORDER);
        ScapeFromXMLViewTest.TestAgent testAgent = new ScapeFromXMLViewTest.TestAgent();
        testScape.setPrototypeAgent(testAgent);
        testScape.createScape();
        try {
            testScape.setStopPeriod(0);
        } catch (SpatialTemporalException e) {
        }
        testScape.getModel().run();

        assertTrue(testScape.size() == 4);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(0)).getIntAttr() == 10);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(1)).getIntAttr() == 20);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(2)).getIntAttr() == 30);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(3)).getIntAttr() == 40);

        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(0)).getDoubleAttr() == 10.1);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(1)).getDoubleAttr() == 20.2);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(2)).getDoubleAttr() == 30.3);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(3)).getDoubleAttr() == 40.4);

        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(0)).getStringAttr().equals("Ten"));
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(1)).getStringAttr().equals("Twenty"));
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(2)).getStringAttr().equals("Thirty"));
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(3)).getStringAttr().equals("Forty"));
    }

    public void testPopulationDefinedByFileMutable() {
        Scape testScape = new Scape();
        testScape.createSelfView();
        testScape.setAutoRestart(false);
        ScapeFromXMLView fileView = new ScapeFromXMLView();
        fileView.parseStream(ScapeFromXMLViewTest.class.getResourceAsStream("ScapeFileMutableTestData.xml"));
        testScape.addView(fileView);
        fileView.setMode(ScapeFromXMLView.SIZE_BY_FILE);
        testScape.setPrototypeAgent(new ScapeFromXMLViewTest.TestAgent());
        try {
            testScape.setStopPeriod(0);
        } catch (SpatialTemporalException e) {
            throw new RuntimeException("Bad stop period: " + e);
        }
        testScape.createScape();
        testScape.getModel().run();
        assertTrue(testScape.size() == 4);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(0)).getIntAttr() == 10);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(1)).getIntAttr() == 20);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(2)).getIntAttr() == 30);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(3)).getIntAttr() == 40);

        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(0)).getDoubleAttr() == 10.1);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(1)).getDoubleAttr() == 20.2);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(2)).getDoubleAttr() == 30.3);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(3)).getDoubleAttr() == 40.4);

        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(0)).getStringAttr().equals("Ten"));
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(1)).getStringAttr().equals("Twenty"));
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(2)).getStringAttr().equals("Thirty"));
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(3)).getStringAttr().equals("Forty"));

        testScape.createScape();
        try {
            testScape.setStopPeriod(0);
        } catch (SpatialTemporalException e) {
            throw new RuntimeException("Bad stop period: " + e);
        }
        testScape.getModel().run();
        assertTrue(testScape.size() == 4);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(0)).getIntAttr() == 10);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(1)).getIntAttr() == 20);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(2)).getIntAttr() == 30);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(3)).getIntAttr() == 40);

        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(0)).getDoubleAttr() == 10.1);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(1)).getDoubleAttr() == 20.2);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(2)).getDoubleAttr() == 30.3);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(3)).getDoubleAttr() == 40.4);

        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(0)).getStringAttr().equals("Ten"));
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(1)).getStringAttr().equals("Twenty"));
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(2)).getStringAttr().equals("Thirty"));
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(3)).getStringAttr().equals("Forty"));

        testScape.createScape();
        try {
            testScape.setStopPeriod(0);
        } catch (SpatialTemporalException e) {
        }
        testScape.getModel().run();
        assertTrue(testScape.size() == 4);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(0)).getIntAttr() == 10);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(1)).getIntAttr() == 20);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(2)).getIntAttr() == 30);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(3)).getIntAttr() == 40);

        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(0)).getDoubleAttr() == 10.1);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(1)).getDoubleAttr() == 20.2);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(2)).getDoubleAttr() == 30.3);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(3)).getDoubleAttr() == 40.4);

        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(0)).getStringAttr().equals("Ten"));
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(1)).getStringAttr().equals("Twenty"));
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(2)).getStringAttr().equals("Thirty"));
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(3)).getStringAttr().equals("Forty"));
    }

    public void testPopulationNotDefinedByFileMutable() {
        Scape testScape = new Scape() {
            public void scapeSetup(ScapeEvent scapeEvent) {
                super.scapeSetup(scapeEvent);
                setSize(3);
            }

            public boolean isAutoCreate() {
                return true;
            }
        };
        testScape.createSelfView();
        testScape.setAutoRestart(false);
        ScapeFromXMLView fileView = new ScapeFromXMLView();
        // fileView.setMode(ScapeFromXMLView.SIZE_BY_SCAPE_SEQUENTIAL_MODE);
        fileView.setMode(ScapeFromXMLView.SIZE_BY_SCAPE);
        fileView.setReadOrder(ScapeFromXMLView.SEQUENTIAL_ORDER);
        fileView.setAssignmentOrder(ScapeFromXMLView.SEQUENTIAL_ORDER);
        fileView.parseStream(ScapeFromXMLViewTest.class.getResourceAsStream("ScapeFileMutableTestData.xml"));
        testScape.addView(fileView);
        testScape.setPrototypeAgent(new ScapeFromXMLViewTest.TestAgent());
        try {
            testScape.setStopPeriod(0);
        } catch (SpatialTemporalException e) {
            throw new RuntimeException("Bad stop period: " + e);
        }
        System.out.println("More file elements left warnings (3) expected below:");
        testScape.getModel().run();
        assertTrue(testScape.size() == 3);
        assertTrue(testScape.get(0) instanceof ScapeFromXMLViewTest.TestAgent);
        assertTrue(testScape.get(1) instanceof ScapeFromXMLViewTest.TestAgent);
        assertTrue(testScape.get(2) instanceof ScapeFromXMLViewTest.TestAgent);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(0)).getIntAttr() == 10);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(1)).getIntAttr() == 20);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(2)).getIntAttr() == 30);

        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(0)).getDoubleAttr() == 10.1);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(1)).getDoubleAttr() == 20.2);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(2)).getDoubleAttr() == 30.3);

        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(0)).getStringAttr().equals("Ten"));
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(1)).getStringAttr().equals("Twenty"));
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(2)).getStringAttr().equals("Thirty"));

        testScape.createScape();
        try {
            testScape.setStopPeriod(0);
        } catch (SpatialTemporalException e) {
        }
        testScape.getModel().run();
        assertTrue(testScape.size() == 3);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(0)).getIntAttr() == 10);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(1)).getIntAttr() == 20);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(2)).getIntAttr() == 30);

        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(0)).getDoubleAttr() == 10.1);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(1)).getDoubleAttr() == 20.2);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(2)).getDoubleAttr() == 30.3);

        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(0)).getStringAttr().equals("Ten"));
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(1)).getStringAttr().equals("Twenty"));
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(2)).getStringAttr().equals("Thirty"));

        testScape.createScape();
        try {
            testScape.setStopPeriod(0);
        } catch (SpatialTemporalException e) {
        }
        testScape.getModel().run();
        assertTrue(testScape.size() == 3);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(0)).getIntAttr() == 10);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(1)).getIntAttr() == 20);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(2)).getIntAttr() == 30);

        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(0)).getDoubleAttr() == 10.1);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(1)).getDoubleAttr() == 20.2);
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(2)).getDoubleAttr() == 30.3);

        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(0)).getStringAttr().equals("Ten"));
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(1)).getStringAttr().equals("Twenty"));
        assertTrue(((ScapeFromXMLViewTest.TestAgent) testScape.get(2)).getStringAttr().equals("Thirty"));
    }

    public void testImmutable() {
        Scape testScape = new Scape(new Array2DMoore());
        testScape.setExtent(3, 3);
        testScape.setAutoRestart(false);
        ScapeFromXMLView fileView = new ScapeFromXMLView();
        fileView.parseStream(ScapeFromXMLViewTest.class.getResourceAsStream("ScapeFileImutableTestData.xml"));
        testScape.addView(fileView);
        testScape.setPrototypeAgent(new ScapeFromXMLViewTest.TestAgent());
        testScape.createScape();
        try {
            testScape.setStopPeriod(0);
        } catch (SpatialTemporalException e) {
        }
        testScape.getModel().run();

        // Order is undefined, so we need to search for matches
        ArrayList toMatch = new ArrayList();
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(10, "Ten", 10.1));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(20, "Twenty", 20.2));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(30, "Thirty", 30.3));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(40, "Forty", 40.4));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(50, "Fifty", 50.5));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(60, "Sixty", 60.6));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(70, "Seventy", 70.7));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(80, "Eighty", 80.8));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(90, "Ninety", 90.9));

        // We use for loop and not iterator just in case there is something funky going on w/ the iterator
        int matchCount = 0;
        for (int x = 0; x < ((Coordinate2DDiscrete) testScape.getExtent()).getXValue(); x++) {
            for (int y = 0; y < ((Coordinate2DDiscrete) testScape.getExtent()).getYValue(); y++) {
                ScapeFromXMLViewTest.TestAgent candidate = (ScapeFromXMLViewTest.TestAgent) ((Array2D) testScape
                        .getSpace()).get(x, y);
                toMatch.remove(candidate);
            }
        }

        assertTrue(toMatch.size() == 0);
    }

    public void testImmutableMultiple() {
        Scape testScape = new Scape(new Array2DMoore());
        testScape.setExtent(3, 3);
        testScape.setAutoRestart(false);
        ScapeFromXMLView fileView = new ScapeFromXMLView();
        fileView.parseStream(ScapeFromXMLViewTest.class.getResourceAsStream("ScapeFileImutableTestData.xml"));
        testScape.addView(fileView);
        testScape.setPrototypeAgent(new ScapeFromXMLViewTest.TestAgent());
        try {
            testScape.setStopPeriod(0);
        } catch (SpatialTemporalException e) {
        }
        testScape.createScape();
        testScape.getModel().run();

        // Order is undefined, so we need to search for matches
        ArrayList toMatch = new ArrayList();
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(10, "Ten", 10.1));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(20, "Twenty", 20.2));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(30, "Thirty", 30.3));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(40, "Forty", 40.4));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(50, "Fifty", 50.5));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(60, "Sixty", 60.6));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(70, "Seventy", 70.7));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(80, "Eighty", 80.8));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(90, "Ninety", 90.9));

        // We use for loop and not iterator just in case there is something funky going on w/ the iterator
        for (int x = 0; x < ((Coordinate2DDiscrete) testScape.getExtent()).getXValue(); x++) {
            for (int y = 0; y < ((Coordinate2DDiscrete) testScape.getExtent()).getYValue(); y++) {
                ScapeFromXMLViewTest.TestAgent candidate = (ScapeFromXMLViewTest.TestAgent) ((Array2D) testScape
                        .getSpace()).get(x, y);
                boolean success = toMatch.remove(candidate);
                assertTrue(success);
            }
        }

        assertTrue(toMatch.size() == 0);

        testScape.createScape();
        testScape.getModel().run();

        // Order is undefined, so we need to search for matches
        toMatch = new ArrayList();
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(10, "Ten", 10.1));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(20, "Twenty", 20.2));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(30, "Thirty", 30.3));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(40, "Forty", 40.4));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(50, "Fifty", 50.5));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(60, "Sixty", 60.6));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(70, "Seventy", 70.7));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(80, "Eighty", 80.8));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(90, "Ninety", 90.9));

        // We use for loop and not iterator just in case there is something funky going on w/ the iterator
        for (int x = 0; x < ((Coordinate2DDiscrete) testScape.getExtent()).getXValue(); x++) {
            for (int y = 0; y < ((Coordinate2DDiscrete) testScape.getExtent()).getYValue(); y++) {
                ScapeFromXMLViewTest.TestAgent candidate = (ScapeFromXMLViewTest.TestAgent) ((Array2D) testScape
                        .getSpace()).get(x, y);
                boolean success = toMatch.remove(candidate);
                assertTrue(success);
            }
        }

        assertTrue(toMatch.size() == 0);

        testScape.createScape();
        testScape.getModel().run();

        // Order is undefined, so we need to search for matches
        toMatch = new ArrayList();
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(10, "Ten", 10.1));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(20, "Twenty", 20.2));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(30, "Thirty", 30.3));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(40, "Forty", 40.4));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(50, "Fifty", 50.5));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(60, "Sixty", 60.6));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(70, "Seventy", 70.7));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(80, "Eighty", 80.8));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(90, "Ninety", 90.9));

        // We use for loop and not iterator just in case there is something funky going on w/ the iterator
        for (int x = 0; x < ((Coordinate2DDiscrete) testScape.getExtent()).getXValue(); x++) {
            for (int y = 0; y < ((Coordinate2DDiscrete) testScape.getExtent()).getYValue(); y++) {
                ScapeFromXMLViewTest.TestAgent candidate = (ScapeFromXMLViewTest.TestAgent) ((Array2D) testScape
                        .getSpace()).get(x, y);
                boolean success = toMatch.remove(candidate);
                assertTrue(success);
            }
        }

        assertTrue(toMatch.size() == 0);
    }

    public void testImmutableTooSmallWarning() {
        Scape testScape = new Scape(new Array2DMoore());
        testScape.setExtent(2, 2);
        testScape.setAutoRestart(false);
        ScapeFromXMLView fileView = new ScapeFromXMLView();
        fileView.parseStream(ScapeFromXMLViewTest.class.getResourceAsStream("ScapeFileImutableTestData.xml"));
        testScape.addView(fileView);
        testScape.setPrototypeAgent(new ScapeFromXMLViewTest.TestAgent());
        testScape.createScape();
        try {
            testScape.setStopPeriod(0);
        } catch (SpatialTemporalException e) {
        }
        System.out.println("More file elements left warning expected below:");
        testScape.getModel().run();

        // Order is undefined, so we need to search for matches
        ArrayList toMatch = new ArrayList();
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(10, "Ten", 10.1));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(20, "Twenty", 20.2));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(30, "Thirty", 30.3));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(40, "Forty", 40.4));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(50, "Fifty", 50.5));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(60, "Sixty", 60.6));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(70, "Seventy", 70.7));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(80, "Eighty", 80.8));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(90, "Ninety", 90.9));

        // We use for loop and not iterator just in case there is something funky going on w/ the iterator
        for (int x = 0; x < ((Coordinate2DDiscrete) testScape.getExtent()).getXValue(); x++) {
            for (int y = 0; y < ((Coordinate2DDiscrete) testScape.getExtent()).getYValue(); y++) {
                ScapeFromXMLViewTest.TestAgent candidate = (ScapeFromXMLViewTest.TestAgent) ((Array2D) testScape
                        .getSpace()).get(x, y);
                boolean success = toMatch.remove(candidate);
                assertTrue(success);
            }
        }

        assertTrue(toMatch.size() == 9 - 4);
    }

    public void testImmutableTooLargeWarning() {
        Scape testScape = new Scape(new Array2DMoore());
        testScape.setExtent(4, 4);
        testScape.setAutoRestart(false);
        ScapeFromXMLView fileView = new ScapeFromXMLView();
        fileView.parseStream(ScapeFromXMLViewTest.class.getResourceAsStream("ScapeFileImutableTestData.xml"));
        testScape.addView(fileView);
        testScape.setPrototypeAgent(new ScapeFromXMLViewTest.TestAgent());
        testScape.createScape();
        try {
            testScape.setStopPeriod(0);
        } catch (SpatialTemporalException e) {
        }
        System.out.println("Not enough file elements warning expected below:");
        testScape.getModel().run();

        // Order is undefined, so we need to search for matches
        ArrayList toMatch = new ArrayList();
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(10, "Ten", 10.1));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(20, "Twenty", 20.2));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(30, "Thirty", 30.3));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(40, "Forty", 40.4));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(50, "Fifty", 50.5));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(60, "Sixty", 60.6));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(70, "Seventy", 70.7));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(80, "Eighty", 80.8));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(90, "Ninety", 90.9));

        int misses = 0;

        // We use for loop and not iterator just in case there is something funky going on w/ the iterator
        for (int x = 0; x < ((Coordinate2DDiscrete) testScape.getExtent()).getXValue(); x++) {
            for (int y = 0; y < ((Coordinate2DDiscrete) testScape.getExtent()).getYValue(); y++) {
                ScapeFromXMLViewTest.TestAgent candidate = (ScapeFromXMLViewTest.TestAgent) ((Array2D) testScape
                        .getSpace()).get(x, y);
                boolean success = toMatch.remove(candidate);
                if (!success) {
                    misses++;
                }
            }
        }

        assertTrue(toMatch.size() == 0);
        assertTrue(misses == 16 - 9);
    }

    // public void testImmutableRandom() {
    // Scape testScape = new TestScape();
    // testScape.setExtent(3, 3);
    // testScape.setAutoRestart(false);
    // ScapeFromXMLView fileView = new ScapeFromXMLView();
    // // fileView.setMode(ScapeFromXMLView.SIZE_BY_SCAPE_RANDOM_SAMPLE_MODE);
    // fileView.setMode(ScapeFromXMLView.SIZE_BY_SCAPE);
    // fileView.setAssignmentOrder(ScapeFromXMLView.RANDOM_ORDER);
    // fileView.setReadOrder(ScapeFromXMLView.RANDOM_ORDER);
    // fileView.parseFile("..\\lib\\com\\nutech\\ascape\\test\\view\\ScapeFileImutableTestData.xml");
    // testScape.addView(fileView);
    // testScape.setPrototypeAgent(new ScapeFromXMLViewTest.TestAgent());
    // testScape.createScape();
    // try {
    // testScape.setStopPeriod(0);
    // } catch (SpatialTemporalException e) {
    // }
    // testScape.getModel().run();
    //
    // //Order is undefined, so we need to search for matches
    // ArrayList toMatch = new ArrayList();
    // for (int i = 0; i < 9; i++) {
    // toMatch.add(new ScapeFromXMLViewTest.TestAgent(90, "Ninety", 90.9));
    // }
    //
    // //We use for loop and not iterator just in case there is something funky going on w/ the iterator
    // for (int x = 0; x < ((Coordinate2DDiscrete) testScape.getExtent()).getXValue(); x++) {
    // for (int y = 0; y < ((Coordinate2DDiscrete) testScape.getExtent()).getYValue(); y++) {
    // ScapeFromXMLViewTest.TestAgent candidate = (ScapeFromXMLViewTest.TestAgent) ((Array2D)
    // testScape.getSpace()).get(x, y);
    // //xxx2
    // System.out.println("candidate = " + candidate);
    // boolean success = toMatch.remove(candidate);
    // System.out.println("success = " + success);
    // assertTrue(success);
    // }
    // }
    //
    // assertTrue(toMatch.size() == 0);
    // }
    //
    // public void testImmutableRandomLow() {
    // Scape testScape = new TestScapeLow();
    // testScape.setExtent(3, 3);
    // testScape.setAutoRestart(false);
    // ScapeFromXMLView fileView = new ScapeFromXMLView();
    // // SIZE_BY_SCAPE_RANDOM_SAMPLE_MODE);
    // fileView.setMode(ScapeFromXMLView.SIZE_BY_SCAPE);
    // fileView.setReadOrder(ScapeFromXMLView.RANDOM_ORDER);
    // fileView.setAssignmentOrder(ScapeFromXMLView.RANDOM_ORDER);
    // fileView.parseFile("..\\lib\\com\\nutech\\ascape\\test\\view\\ScapeFileImutableTestData.xml");
    // testScape.addView(fileView);
    // testScape.setPrototypeAgent(new ScapeFromXMLViewTest.TestAgent());
    // testScape.createScape();
    // try {
    // testScape.setStopPeriod(0);
    // } catch (SpatialTemporalException e) {
    // }
    // testScape.getModel().run();
    //
    // //Order is undefined, so we need to search for matches
    // ArrayList toMatch = new ArrayList();
    // for (int i = 0; i < 9; i++) {
    // toMatch.add(new TestAgent(10, "Ten", 10.1));
    // }
    //
    // //We use for loop and not iterator just in case there is something funky going on w/ the iterator
    // for (int x = 0; x < ((Coordinate2DDiscrete) testScape.getExtent()).getXValue(); x++) {
    // for (int y = 0; y < ((Coordinate2DDiscrete) testScape.getExtent()).getYValue(); y++) {
    // TestAgent candidate = (TestAgent) ((Array2D) testScape.getSpace()).get(x, y);
    // boolean success = toMatch.remove(candidate);
    // System.out.println("candidate = " + candidate);
    // System.out.println("success = " + success);
    // assertTrue(success);
    // }
    // }
    //
    // assertTrue(toMatch.size() == 0);
    // }

    public void testImmutableRandomSequence() {
        Scape testScape = new TestScapeSequence();
        testScape.setExtent(3, 3);
        testScape.setAutoRestart(false);
        ScapeFromXMLView fileView = new ScapeFromXMLView();
        // fileView.setMode(ScapeFromXMLView.SIZE_BY_SCAPE_RANDOM_SAMPLE_MODE);
        fileView.setMode(ScapeFromXMLView.SIZE_BY_SCAPE);
        fileView.setAssignmentOrder(ScapeFromXMLView.RANDOM_ORDER);
        fileView.setReadOrder(ScapeFromXMLView.RANDOM_ORDER);
        fileView.parseStream(ScapeFromXMLViewTest.class.getResourceAsStream("ScapeFileImutableTestData.xml"));
        testScape.addView(fileView);
        testScape.setPrototypeAgent(new ScapeFromXMLViewTest.TestAgent());
        testScape.createScape();
        try {
            testScape.setStopPeriod(0);
        } catch (SpatialTemporalException e) {
        }
        testScape.getModel().run();

        // Order is undefined, so we need to search for matches
        ArrayList toMatch = new ArrayList();
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(10, "Ten", 10.1));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(20, "Twenty", 20.2));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(30, "Thirty", 30.3));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(40, "Forty", 40.4));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(50, "Fifty", 50.5));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(60, "Sixty", 60.6));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(70, "Seventy", 70.7));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(80, "Eighty", 80.8));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(90, "Ninety", 90.9));

        assertTrue(testScape.size() == 9);

        // We use for loop and not iterator just in case there is something funky going on w/ the iterator
        for (int x = 0; x < ((Coordinate2DDiscrete) testScape.getExtent()).getXValue(); x++) {
            for (int y = 0; y < ((Coordinate2DDiscrete) testScape.getExtent()).getYValue(); y++) {
                ScapeFromXMLViewTest.TestAgent candidate = (ScapeFromXMLViewTest.TestAgent) ((Array2D) testScape
                        .getSpace()).get(x, y);
                toMatch.remove(candidate);
            }
        }

        assertTrue(toMatch.size() == 0);
    }

    public void testImmutableRandomSequenceBig() {
        Scape testScape = new TestScapeSequence();
        testScape.setExtent(4, 4);
        testScape.setAutoRestart(false);
        ScapeFromXMLView fileView = new ScapeFromXMLView();
        // fileView.setMode(ScapeFromXMLView.SIZE_BY_SCAPE_RANDOM_SAMPLE_MODE);
        fileView.setMode(ScapeFromXMLView.SIZE_BY_SCAPE);
        fileView.setAssignmentOrder(ScapeFromXMLView.RANDOM_ORDER);
        fileView.setReadOrder(ScapeFromXMLView.RANDOM_ORDER);
        fileView.parseStream(ScapeFromXMLViewTest.class.getResourceAsStream("ScapeFileImutableTestData.xml"));
        testScape.addView(fileView);
        testScape.setPrototypeAgent(new ScapeFromXMLViewTest.TestAgent());
        testScape.createScape();
        try {
            testScape.setStopPeriod(0);
        } catch (SpatialTemporalException e) {
        }
        System.out.println("Not enough file elements warning expected below:");
        testScape.getModel().run();

        // Order is undefined, so we need to search for matches
        ArrayList toMatch = new ArrayList();
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(10, "Ten", 10.1));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(20, "Twenty", 20.2));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(30, "Thirty", 30.3));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(40, "Forty", 40.4));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(50, "Fifty", 50.5));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(60, "Sixty", 60.6));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(70, "Seventy", 70.7));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(80, "Eighty", 80.8));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(90, "Ninety", 90.9));

        int misses = 0;

        // We use for loop and not iterator just in case there is something funky going on w/ the iterator
        for (int x = 0; x < ((Coordinate2DDiscrete) testScape.getExtent()).getXValue(); x++) {
            for (int y = 0; y < ((Coordinate2DDiscrete) testScape.getExtent()).getYValue(); y++) {
                ScapeFromXMLViewTest.TestAgent candidate = (ScapeFromXMLViewTest.TestAgent) ((Array2D) testScape
                        .getSpace()).get(x, y);
                boolean success = toMatch.remove(candidate);
                if (!success) {
                    misses++;
                }
            }
        }

        assertTrue(toMatch.size() == 0);
        assertTrue(misses == 16 - 9);
    }

    public void testImmutableRandomSequenceSmall() {
        Scape testScape = new TestScapeSequence();
        testScape.setExtent(2, 2);
        testScape.setAutoRestart(false);
        ScapeFromXMLView fileView = new ScapeFromXMLView();
        // fileView.setMode(ScapeFromXMLView.SIZE_BY_SCAPE_RANDOM_SAMPLE_MODE);
        fileView.setMode(ScapeFromXMLView.SIZE_BY_SCAPE);
        fileView.setAssignmentOrder(ScapeFromXMLView.RANDOM_ORDER);
        fileView.setReadOrder(ScapeFromXMLView.RANDOM_ORDER);
        fileView.parseStream(ScapeFromXMLViewTest.class.getResourceAsStream("ScapeFileImutableTestData.xml"));
        testScape.addView(fileView);
        testScape.setPrototypeAgent(new ScapeFromXMLViewTest.TestAgent());
        testScape.createScape();
        try {
            testScape.setStopPeriod(0);
        } catch (SpatialTemporalException e) {
        }
        testScape.getModel().run();

        // Order is undefined, so we need to search for matches
        ArrayList toMatch = new ArrayList();
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(10, "Ten", 10.1));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(20, "Twenty", 20.2));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(30, "Thirty", 30.3));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(40, "Forty", 40.4));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(50, "Fifty", 50.5));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(60, "Sixty", 60.6));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(70, "Seventy", 70.7));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(80, "Eighty", 80.8));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(90, "Ninety", 90.9));

        // We use for loop and not iterator just in case there is something funky going on w/ the iterator
        for (int x = 0; x < ((Coordinate2DDiscrete) testScape.getExtent()).getXValue(); x++) {
            for (int y = 0; y < ((Coordinate2DDiscrete) testScape.getExtent()).getYValue(); y++) {
                ScapeFromXMLViewTest.TestAgent candidate = (ScapeFromXMLViewTest.TestAgent) ((Array2D) testScape
                        .getSpace()).get(x, y);
                boolean success = toMatch.remove(candidate);
                assertTrue(success);
            }
        }

        assertTrue(toMatch.size() == 9 - 4);
    }

    public void testImutableRandomSequence() {
        TestScapeListSequence testScape = new TestScapeListSequence();
        testScape.setExtent(9);
        testScape.setAutoRestart(false);
        ScapeFromXMLView fileView = new ScapeFromXMLView();
        // fileView.setMode(ScapeFromXMLView.SIZE_BY_SCAPE_RANDOM_SAMPLE_MODE);
        fileView.setMode(ScapeFromXMLView.SIZE_BY_SCAPE);
        fileView.setAssignmentOrder(ScapeFromXMLView.RANDOM_ORDER);
        fileView.setReadOrder(ScapeFromXMLView.RANDOM_ORDER);
        fileView.parseStream(ScapeFromXMLViewTest.class.getResourceAsStream("ScapeFileImutableTestData.xml"));
        testScape.addView(fileView);
        testScape.setPrototypeAgent(new ScapeFromXMLViewTest.TestAgent());
        testScape.createScape();
        try {
            testScape.setStopPeriod(0);
        } catch (SpatialTemporalException e) {
        }
        testScape.getModel().run();

        // Order is undefined, so we need to search for matches
        ArrayList toMatch = new ArrayList();
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(10, "Ten", 10.1));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(20, "Twenty", 20.2));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(30, "Thirty", 30.3));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(40, "Forty", 40.4));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(50, "Fifty", 50.5));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(60, "Sixty", 60.6));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(70, "Seventy", 70.7));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(80, "Eighty", 80.8));
        toMatch.add(new ScapeFromXMLViewTest.TestAgent(90, "Ninety", 90.9));

        // We use for loop and not iterator just in case there is something funky going on w/ the iterator
        for (int x = 0; x < ((Coordinate1DDiscrete) testScape.getExtent()).getXValue(); x++) {
            ScapeFromXMLViewTest.TestAgent candidate = (ScapeFromXMLViewTest.TestAgent) testScape.get(x);
            boolean success = toMatch.remove(candidate);
            assertTrue(success);
        }

        assertTrue(toMatch.size() == 0);
    }

    public void testImportCoordinate() {

        Scape testScape = new TestScape();
        testScape.setExtent(2, 2);
        testScape.setAutoRestart(false);
        ScapeFromXMLView fileView = new ScapeFromXMLView();
        fileView.setMode(ScapeFromXMLView.SIZE_BY_SCAPE);
        fileView.setAssignmentOrder(ScapeFromXMLView.RANDOM_ORDER);
        fileView.setReadOrder(ScapeFromXMLView.RANDOM_ORDER);
        fileView.parseStream(ScapeFromXMLViewTest.class.getResourceAsStream("ScapeFileMutableTestData.xml"));
        testScape.addView(fileView);
        testScape.setPrototypeAgent(new ScapeFromXMLViewTest.TestAgent());
        testScape.createScape();
        try {
            testScape.setStopPeriod(0);
        } catch (SpatialTemporalException e) {
        }
        testScape.getModel().run();

        // Order is undefined, so we need to search for matches
        ArrayList toMatch = new ArrayList();
        TestAgent ta = new TestAgent(10, "Ten", 10.1);
        ta.setCoordinate(new Coordinate2DDiscrete(2, 2));
        toMatch.add(ta);
        ta = new TestAgent(20, "Twenty", 20.2);
        ta.setCoordinate(new Coordinate2DDiscrete(3, 3));
        toMatch.add(ta);
        ta = new TestAgent(30, "Thirty", 30.3);
        ta.setCoordinate(new Coordinate2DDiscrete(4, 4));
        toMatch.add(ta);
        ta = new TestAgent(40, "Forty", 40.4);
        ta.setCoordinate(new Coordinate2DDiscrete(5, 5));
        toMatch.add(ta);

        // We use for loop and not iterator just in case there is something funky going on w/ the iterator
        for (int x = 0; x < ((Coordinate2DDiscrete) testScape.getExtent()).getXValue(); x++) {
            for (int y = 0; y < ((Coordinate2DDiscrete) testScape.getExtent()).getYValue(); y++) {
                ScapeFromXMLViewTest.TestAgent candidate = (ScapeFromXMLViewTest.TestAgent) ((Array2D) testScape
                        .getSpace()).get(x, y);
                if (toMatch.contains(candidate)) {
                    toMatch.remove(candidate);
                }
            }
        }
        assertTrue(toMatch.size() == 0);
    }

    public class TestAgent extends Cell {

        int testInt;

        public TestAgent() {}

        public TestAgent(int testInt, String testString, double testDouble) {
            this.testInt = testInt;
            this.testString = testString;
            this.testDouble = testDouble;
        }

        public int getIntAttr() {
            return testInt;
        }

        public void setIntAttr(int testInt) {
            this.testInt = testInt;
        }

        String testString = "";

        public String getStringAttr() {
            return testString;
        }

        public void setStringAttr(String testString) {
            this.testString = testString;
        }

        double testDouble;

        public double getDoubleAttr() {
            return testDouble;
        }

        public void setDoubleAttr(double testDouble) {
            this.testDouble = testDouble;
        }

        public boolean equals(Object o) {
            return (((ScapeFromXMLViewTest.TestAgent) o).testInt == testInt)
                    && (((ScapeFromXMLViewTest.TestAgent) o).testString.equals(testString))
                    && (((ScapeFromXMLViewTest.TestAgent) o).testDouble == testDouble);
        }

        public String toString() {
            return testInt + ", " + testString + ", " + testDouble;
        }
    }

    class TestScape extends Scape {

        public TestScape() {
            super(new Array2DMoore());
        }

        // An interesting little test hack; override randomToLimit to provide a predictable number for testing..
        public int randomToLimit(int limit) {
            // Here we're testing all tail end...
            return limit - 1;
        }

        public String toString() {
            return "Test Scape";
        }
    }

    class TestScapeLow extends Scape {

        public TestScapeLow() {
            super(new Array2DMoore());
        }

        public int randomToLimit(int limit) {
            // Here we're testing all head end...
            return 0;
        }

        public String toString() {
            return "Test Scape";
        }
    }

    class TestScapeSequence extends Scape {

        public TestScapeSequence() {
            super(new Array2DMoore());
        }

        int i = -1;

        public int randomToLimit(int limit) {
            // Here we're testing sequential...
            i++;
            return i;
        }

        public String toString() {
            return "Test Scape";
        }
    }

    class TestScapeListSequence extends Scape {

        int i = -1;

        public int randomToLimit(int limit) {
            // Here we're testing sequential...
            i++;
            return i;
        }

        public String toString() {
            return "Test Scape";
        }
    }
}
