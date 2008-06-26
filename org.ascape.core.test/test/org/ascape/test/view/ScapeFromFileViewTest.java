/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.test.view;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

import junit.framework.TestCase;

import org.ascape.model.CellOccupant;
import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.space.Array2D;
import org.ascape.model.space.Array2DMoore;
import org.ascape.model.space.Coordinate1DDiscrete;
import org.ascape.model.space.Coordinate2DDiscrete;
import org.ascape.model.space.SpatialTemporalException;
import org.ascape.view.nonvis.ScapeFromFileView;

class TestScape extends Scape {

    public TestScape() {
        super(new Array2DMoore());
    }

    //An interesting little test hack; override randomToLimit to provide a predictable number for testing..
    public int randomToLimit(int limit) {
        //Here we're testing all tail end...
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
        //Here we're testing all head end...
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
        //Here we're testing sequential...
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
        //Here we're testing sequential...
        i++;
        return i;
    }

    public String toString() {
        return "Test Scape";
    }
}

public class ScapeFromFileViewTest extends TestCase {

    public ScapeFromFileViewTest(String name) {
        super(name);
    }

    public class TestAgent extends CellOccupant {

        int testInt;

        public TestAgent() {
        }

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
            return (((TestAgent) o).testInt == testInt) && (((TestAgent) o).testString.equals(testString)) && (((TestAgent) o).testDouble == testDouble);
        }

        public String toString() {
            return testInt + ", " + testString + ", " + testDouble;
        }
    }

    public void testBackwardCompare() {
        ArrayList testList = new ArrayList();
        testList.add(new Integer(1));
        testList.add(new Integer(2));
        testList.add(new Integer(3));
        testList.add(new Integer(4));
        Collections.sort(testList, ScapeFromFileView.BACKWARD_COMPARE);
        assertTrue(testList.get(0).equals(new Integer(4)));
        assertTrue(testList.get(1).equals(new Integer(3)));
        assertTrue(testList.get(2).equals(new Integer(2)));
        assertTrue(testList.get(3).equals(new Integer(1)));
    }

    public void testEquals() {
        TestAgent comp1 = new TestAgent(1, "One", 1.1);
        TestAgent comp2 = new TestAgent(1, "One", 1.1);
        assertTrue(comp1.equals(comp2));
        TestAgent comp3 = new TestAgent(1, "One", 1.2);
        assertTrue(!comp1.equals(comp3));
        TestAgent comp4 = new TestAgent(1, "Two", 1.1);
        assertTrue(!comp1.equals(comp4));
        TestAgent comp5 = new TestAgent(2, "One", 1.1);
        assertTrue(!comp1.equals(comp5));
    }

    public void testPopulationDefinedByFile() {
        Scape testScape = new Scape();
        ScapeFromFileView fileView = new ScapeFromFileView();
        try {
            fileView.setInputStream(ScapeFromFileViewTest.class.getResourceAsStream("ScapeFileMutableTestData.txt"));
        } catch (IOException e) {
            throw new RuntimeException("File Problem in Scape File Control Test" + e);
        }
        testScape.addView(fileView);
        testScape.setPrototypeAgent(new TestAgent());
        testScape.createScape();
        testScape.setAutoRestart(false);
        try {
            testScape.setStopPeriod(0);
        } catch (SpatialTemporalException e) {
            throw new RuntimeException("Bad stop period: " + e);
        }
        testScape.getRunner().run();
        assertTrue(testScape.size() == 4);
        assertTrue(((TestAgent) testScape.get(0)).getIntAttr() == 10);
        assertTrue(((TestAgent) testScape.get(1)).getIntAttr() == 20);
        assertTrue(((TestAgent) testScape.get(2)).getIntAttr() == 30);
        assertTrue(((TestAgent) testScape.get(3)).getIntAttr() == 40);

        assertTrue(((TestAgent) testScape.get(0)).getDoubleAttr() == 10.1);
        assertTrue(((TestAgent) testScape.get(1)).getDoubleAttr() == 20.2);
        assertTrue(((TestAgent) testScape.get(2)).getDoubleAttr() == 30.3);
        assertTrue(((TestAgent) testScape.get(3)).getDoubleAttr() == 40.4);

        assertTrue(((TestAgent) testScape.get(0)).getStringAttr().equals("Ten"));
        assertTrue(((TestAgent) testScape.get(1)).getStringAttr().equals("Twenty"));
        assertTrue(((TestAgent) testScape.get(2)).getStringAttr().equals("Thirty"));
        assertTrue(((TestAgent) testScape.get(3)).getStringAttr().equals("Fourty"));
    }

    public void testPopulationDefinedByFileMutable() {
        Scape testScape = new Scape();
        testScape.createSelfView();
        ScapeFromFileView fileView = new ScapeFromFileView();
        try {
            fileView.setInputStream(ScapeFromFileViewTest.class.getResourceAsStream("ScapeFileMutableTestData.txt"));
        } catch (IOException e) {
            throw new RuntimeException("File Problem in Scape File Control Test" + e);
        }
        testScape.addView(fileView);
        testScape.setPrototypeAgent(new TestAgent());
        testScape.createScape();
        try {
            testScape.setStopPeriod(0);
        } catch (SpatialTemporalException e) {
            throw new RuntimeException("Bad stop period: " + e);
        }
        testScape.setAutoRestart(false);
        testScape.getRunner().run();
        assertTrue(testScape.size() == 4);
        assertTrue(((TestAgent) testScape.get(0)).getIntAttr() == 10);
        assertTrue(((TestAgent) testScape.get(1)).getIntAttr() == 20);
        assertTrue(((TestAgent) testScape.get(2)).getIntAttr() == 30);
        assertTrue(((TestAgent) testScape.get(3)).getIntAttr() == 40);

        assertTrue(((TestAgent) testScape.get(0)).getDoubleAttr() == 10.1);
        assertTrue(((TestAgent) testScape.get(1)).getDoubleAttr() == 20.2);
        assertTrue(((TestAgent) testScape.get(2)).getDoubleAttr() == 30.3);
        assertTrue(((TestAgent) testScape.get(3)).getDoubleAttr() == 40.4);

        assertTrue(((TestAgent) testScape.get(0)).getStringAttr().equals("Ten"));
        assertTrue(((TestAgent) testScape.get(1)).getStringAttr().equals("Twenty"));
        assertTrue(((TestAgent) testScape.get(2)).getStringAttr().equals("Thirty"));
        assertTrue(((TestAgent) testScape.get(3)).getStringAttr().equals("Fourty"));

        testScape.createScape();
        try {
            testScape.setStopPeriod(0);
        } catch (SpatialTemporalException e) {
            throw new RuntimeException("Bad stop period: " + e);
        }
        testScape.getRunner().run();
        assertTrue(testScape.size() == 4);
        assertTrue(((TestAgent) testScape.get(0)).getIntAttr() == 10);
        assertTrue(((TestAgent) testScape.get(1)).getIntAttr() == 20);
        assertTrue(((TestAgent) testScape.get(2)).getIntAttr() == 30);
        assertTrue(((TestAgent) testScape.get(3)).getIntAttr() == 40);

        assertTrue(((TestAgent) testScape.get(0)).getDoubleAttr() == 10.1);
        assertTrue(((TestAgent) testScape.get(1)).getDoubleAttr() == 20.2);
        assertTrue(((TestAgent) testScape.get(2)).getDoubleAttr() == 30.3);
        assertTrue(((TestAgent) testScape.get(3)).getDoubleAttr() == 40.4);

        assertTrue(((TestAgent) testScape.get(0)).getStringAttr().equals("Ten"));
        assertTrue(((TestAgent) testScape.get(1)).getStringAttr().equals("Twenty"));
        assertTrue(((TestAgent) testScape.get(2)).getStringAttr().equals("Thirty"));
        assertTrue(((TestAgent) testScape.get(3)).getStringAttr().equals("Fourty"));

        testScape.createScape();
        try {
            testScape.setStopPeriod(0);
        } catch (SpatialTemporalException e) {
        }
        testScape.getRunner().run();
        assertTrue(testScape.size() == 4);
        assertTrue(((TestAgent) testScape.get(0)).getIntAttr() == 10);
        assertTrue(((TestAgent) testScape.get(1)).getIntAttr() == 20);
        assertTrue(((TestAgent) testScape.get(2)).getIntAttr() == 30);
        assertTrue(((TestAgent) testScape.get(3)).getIntAttr() == 40);

        assertTrue(((TestAgent) testScape.get(0)).getDoubleAttr() == 10.1);
        assertTrue(((TestAgent) testScape.get(1)).getDoubleAttr() == 20.2);
        assertTrue(((TestAgent) testScape.get(2)).getDoubleAttr() == 30.3);
        assertTrue(((TestAgent) testScape.get(3)).getDoubleAttr() == 40.4);

        assertTrue(((TestAgent) testScape.get(0)).getStringAttr().equals("Ten"));
        assertTrue(((TestAgent) testScape.get(1)).getStringAttr().equals("Twenty"));
        assertTrue(((TestAgent) testScape.get(2)).getStringAttr().equals("Thirty"));
        assertTrue(((TestAgent) testScape.get(3)).getStringAttr().equals("Fourty"));
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
        ScapeFromFileView fileView = new ScapeFromFileView();
        fileView.setMode(ScapeFromFileView.SIZE_BY_SCAPE_SEQUENTIAL_MODE);
        try {
            fileView.setInputStream(ScapeFromFileViewTest.class.getResourceAsStream("ScapeFileMutableTestData.txt"));
        } catch (IOException e) {
            throw new RuntimeException("File Problem in Scape File Control Test" + e);
        }
        testScape.addView(fileView);
        testScape.setPrototypeAgent(new TestAgent());
        testScape.createScape();
        testScape.setAutoRestart(false);
        try {
            testScape.setStopPeriod(0);
        } catch (SpatialTemporalException e) {
            throw new RuntimeException("Bad stop period: " + e);
        }
        System.out.println("More file elements left warnings (3) expected below:");
        testScape.getRunner().run();
        assertTrue(testScape.size() == 3);
        assertTrue(testScape.get(0) instanceof TestAgent);
        assertTrue(testScape.get(1) instanceof TestAgent);
        assertTrue(testScape.get(2) instanceof TestAgent);
        assertTrue(((TestAgent) testScape.get(0)).getIntAttr() == 10);
        assertTrue(((TestAgent) testScape.get(1)).getIntAttr() == 20);
        assertTrue(((TestAgent) testScape.get(2)).getIntAttr() == 30);

        assertTrue(((TestAgent) testScape.get(0)).getDoubleAttr() == 10.1);
        assertTrue(((TestAgent) testScape.get(1)).getDoubleAttr() == 20.2);
        assertTrue(((TestAgent) testScape.get(2)).getDoubleAttr() == 30.3);

        assertTrue(((TestAgent) testScape.get(0)).getStringAttr().equals("Ten"));
        assertTrue(((TestAgent) testScape.get(1)).getStringAttr().equals("Twenty"));
        assertTrue(((TestAgent) testScape.get(2)).getStringAttr().equals("Thirty"));

        testScape.createScape();
        try {
            testScape.setStopPeriod(0);
        } catch (SpatialTemporalException e) {
        }
        testScape.getRunner().run();
        assertTrue(testScape.size() == 3);
        assertTrue(((TestAgent) testScape.get(0)).getIntAttr() == 10);
        assertTrue(((TestAgent) testScape.get(1)).getIntAttr() == 20);
        assertTrue(((TestAgent) testScape.get(2)).getIntAttr() == 30);

        assertTrue(((TestAgent) testScape.get(0)).getDoubleAttr() == 10.1);
        assertTrue(((TestAgent) testScape.get(1)).getDoubleAttr() == 20.2);
        assertTrue(((TestAgent) testScape.get(2)).getDoubleAttr() == 30.3);

        assertTrue(((TestAgent) testScape.get(0)).getStringAttr().equals("Ten"));
        assertTrue(((TestAgent) testScape.get(1)).getStringAttr().equals("Twenty"));
        assertTrue(((TestAgent) testScape.get(2)).getStringAttr().equals("Thirty"));

        testScape.createScape();
        try {
            testScape.setStopPeriod(0);
        } catch (SpatialTemporalException e) {
        }
        testScape.getRunner().run();
        assertTrue(testScape.size() == 3);
        assertTrue(((TestAgent) testScape.get(0)).getIntAttr() == 10);
        assertTrue(((TestAgent) testScape.get(1)).getIntAttr() == 20);
        assertTrue(((TestAgent) testScape.get(2)).getIntAttr() == 30);

        assertTrue(((TestAgent) testScape.get(0)).getDoubleAttr() == 10.1);
        assertTrue(((TestAgent) testScape.get(1)).getDoubleAttr() == 20.2);
        assertTrue(((TestAgent) testScape.get(2)).getDoubleAttr() == 30.3);

        assertTrue(((TestAgent) testScape.get(0)).getStringAttr().equals("Ten"));
        assertTrue(((TestAgent) testScape.get(1)).getStringAttr().equals("Twenty"));
        assertTrue(((TestAgent) testScape.get(2)).getStringAttr().equals("Thirty"));
    }

    public void testImmutable() {
        Scape testScape = new Scape(new Array2DMoore());
        testScape.setExtent(3, 3);
        ScapeFromFileView fileView = new ScapeFromFileView();
        try {
            InputStream resourceAsStream = ScapeFromFileViewTest.class.getResourceAsStream("ScapeFileImutableTestData.txt");
            assertNotNull(resourceAsStream);
            fileView.setInputStream(resourceAsStream);
        } catch (IOException e) {
            throw new RuntimeException("File Problem in Scape File Control Test" + e);
        }
        testScape.addView(fileView);
        testScape.setPrototypeAgent(new TestAgent());
        testScape.createScape();
        testScape.setAutoRestart(false);
        try {
            testScape.setStopPeriod(0);
        } catch (SpatialTemporalException e) {
        }
        testScape.getRunner().run();

        //Order is undefined, so we need to search for matches
        ArrayList toMatch = new ArrayList();
        toMatch.add(new TestAgent(10, "Ten", 10.1));
        toMatch.add(new TestAgent(20, "Twenty", 20.2));
        toMatch.add(new TestAgent(30, "Thirty", 30.3));
        toMatch.add(new TestAgent(40, "Fourty", 40.4));
        toMatch.add(new TestAgent(50, "Fifty", 50.5));
        toMatch.add(new TestAgent(60, "Sixty", 60.6));
        toMatch.add(new TestAgent(70, "Seventy", 70.7));
        toMatch.add(new TestAgent(80, "Eighty", 80.8));
        toMatch.add(new TestAgent(90, "Ninety", 90.9));

        //We use for loop and not iterator just in case there is something funky going on w/ the iterator
        for (int x = 0; x < ((Coordinate2DDiscrete) testScape.getExtent()).getXValue(); x++) {
            for (int y = 0; y < ((Coordinate2DDiscrete) testScape.getExtent()).getYValue(); y++) {
                TestAgent candidate = (TestAgent) ((Array2D) testScape.getSpace()).get(x, y);
                boolean success = toMatch.remove(candidate);
                assertTrue(success);
            }
        }

        assertTrue(toMatch.size() == 0);
    }

    public void testImmutableMultiple() {
        Scape testScape = new Scape(new Array2DMoore());
        testScape.setExtent(3, 3);
        ScapeFromFileView fileView = new ScapeFromFileView();
        try {
            fileView.setInputStream(ScapeFromFileViewTest.class.getResourceAsStream("ScapeFileImutableTestData.txt"));
        } catch (IOException e) {
            throw new RuntimeException("File Problem in Scape File Control Test" + e);
        }
        testScape.addView(fileView);
        testScape.setPrototypeAgent(new TestAgent());
        testScape.createScape();
        try {
            testScape.setStopPeriod(0);
        } catch (SpatialTemporalException e) {
        }
        testScape.setAutoRestart(false);
        testScape.getRunner().run();

        //Order is undefined, so we need to search for matches
        ArrayList toMatch = new ArrayList();
        toMatch.add(new TestAgent(10, "Ten", 10.1));
        toMatch.add(new TestAgent(20, "Twenty", 20.2));
        toMatch.add(new TestAgent(30, "Thirty", 30.3));
        toMatch.add(new TestAgent(40, "Fourty", 40.4));
        toMatch.add(new TestAgent(50, "Fifty", 50.5));
        toMatch.add(new TestAgent(60, "Sixty", 60.6));
        toMatch.add(new TestAgent(70, "Seventy", 70.7));
        toMatch.add(new TestAgent(80, "Eighty", 80.8));
        toMatch.add(new TestAgent(90, "Ninety", 90.9));

        //We use for loop and not iterator just in case there is something funky going on w/ the iterator
        for (int x = 0; x < ((Coordinate2DDiscrete) testScape.getExtent()).getXValue(); x++) {
            for (int y = 0; y < ((Coordinate2DDiscrete) testScape.getExtent()).getYValue(); y++) {
                TestAgent candidate = (TestAgent) ((Array2D) testScape.getSpace()).get(x, y);
                boolean success = toMatch.remove(candidate);
                assertTrue(success);
            }
        }

        assertTrue(toMatch.size() == 0);


        testScape.createScape();
        testScape.getRunner().run();

        //Order is undefined, so we need to search for matches
        toMatch = new ArrayList();
        toMatch.add(new TestAgent(10, "Ten", 10.1));
        toMatch.add(new TestAgent(20, "Twenty", 20.2));
        toMatch.add(new TestAgent(30, "Thirty", 30.3));
        toMatch.add(new TestAgent(40, "Fourty", 40.4));
        toMatch.add(new TestAgent(50, "Fifty", 50.5));
        toMatch.add(new TestAgent(60, "Sixty", 60.6));
        toMatch.add(new TestAgent(70, "Seventy", 70.7));
        toMatch.add(new TestAgent(80, "Eighty", 80.8));
        toMatch.add(new TestAgent(90, "Ninety", 90.9));

        //We use for loop and not iterator just in case there is something funky going on w/ the iterator
        for (int x = 0; x < ((Coordinate2DDiscrete) testScape.getExtent()).getXValue(); x++) {
            for (int y = 0; y < ((Coordinate2DDiscrete) testScape.getExtent()).getYValue(); y++) {
                TestAgent candidate = (TestAgent) ((Array2D) testScape.getSpace()).get(x, y);
                boolean success = toMatch.remove(candidate);
                assertTrue(success);
            }
        }

        assertTrue(toMatch.size() == 0);


        testScape.createScape();
        testScape.getRunner().run();

        //Order is undefined, so we need to search for matches
        toMatch = new ArrayList();
        toMatch.add(new TestAgent(10, "Ten", 10.1));
        toMatch.add(new TestAgent(20, "Twenty", 20.2));
        toMatch.add(new TestAgent(30, "Thirty", 30.3));
        toMatch.add(new TestAgent(40, "Fourty", 40.4));
        toMatch.add(new TestAgent(50, "Fifty", 50.5));
        toMatch.add(new TestAgent(60, "Sixty", 60.6));
        toMatch.add(new TestAgent(70, "Seventy", 70.7));
        toMatch.add(new TestAgent(80, "Eighty", 80.8));
        toMatch.add(new TestAgent(90, "Ninety", 90.9));

        //We use for loop and not iterator just in case there is something funky going on w/ the iterator
        for (int x = 0; x < ((Coordinate2DDiscrete) testScape.getExtent()).getXValue(); x++) {
            for (int y = 0; y < ((Coordinate2DDiscrete) testScape.getExtent()).getYValue(); y++) {
                TestAgent candidate = (TestAgent) ((Array2D) testScape.getSpace()).get(x, y);
                boolean success = toMatch.remove(candidate);
                assertTrue(success);
            }
        }

        assertTrue(toMatch.size() == 0);
    }

    public void testImmutableTooSmallWarning() {
        Scape testScape = new Scape(new Array2DMoore());
        testScape.setExtent(2, 2);
        ScapeFromFileView fileView = new ScapeFromFileView();
        try {
            fileView.setInputStream(ScapeFromFileViewTest.class.getResourceAsStream("ScapeFileImutableTestData.txt"));
        } catch (IOException e) {
            throw new RuntimeException("File Problem in Scape File Control Test" + e);
        }
        testScape.addView(fileView);
        testScape.setPrototypeAgent(new TestAgent());
        testScape.createScape();
        testScape.setAutoRestart(false);
        try {
            testScape.setStopPeriod(0);
        } catch (SpatialTemporalException e) {
        }
        System.out.println("More file elements left warning expected below:");
        testScape.getRunner().run();

        //Order is undefined, so we need to search for matches
        ArrayList toMatch = new ArrayList();
        toMatch.add(new TestAgent(10, "Ten", 10.1));
        toMatch.add(new TestAgent(20, "Twenty", 20.2));
        toMatch.add(new TestAgent(30, "Thirty", 30.3));
        toMatch.add(new TestAgent(40, "Fourty", 40.4));
        toMatch.add(new TestAgent(50, "Fifty", 50.5));
        toMatch.add(new TestAgent(60, "Sixty", 60.6));
        toMatch.add(new TestAgent(70, "Seventy", 70.7));
        toMatch.add(new TestAgent(80, "Eighty", 80.8));
        toMatch.add(new TestAgent(90, "Ninety", 90.9));

        //We use for loop and not iterator just in case there is something funky going on w/ the iterator
        for (int x = 0; x < ((Coordinate2DDiscrete) testScape.getExtent()).getXValue(); x++) {
            for (int y = 0; y < ((Coordinate2DDiscrete) testScape.getExtent()).getYValue(); y++) {
                TestAgent candidate = (TestAgent) ((Array2D) testScape.getSpace()).get(x, y);
                boolean success = toMatch.remove(candidate);
                assertTrue(success);
            }
        }

        assertTrue(toMatch.size() == 9 - 4);
    }

    public void testImmutableTooLargeWarning() {
        Scape testScape = new Scape(new Array2DMoore());
        testScape.setExtent(4, 4);
        ScapeFromFileView fileView = new ScapeFromFileView();
        try {
            fileView.setInputStream(ScapeFromFileViewTest.class.getResourceAsStream("ScapeFileImutableTestData.txt"));
        } catch (IOException e) {
            throw new RuntimeException("File Problem in Scape File Control Test" + e);
        }
        testScape.addView(fileView);
        testScape.setPrototypeAgent(new TestAgent());
        testScape.createScape();
        testScape.setAutoRestart(false);
        try {
            testScape.setStopPeriod(0);
        } catch (SpatialTemporalException e) {
        }
        System.out.println("Not enough file elements warning expected below:");
        testScape.getRunner().run();

        //Order is undefined, so we need to search for matches
        ArrayList toMatch = new ArrayList();
        toMatch.add(new TestAgent(10, "Ten", 10.1));
        toMatch.add(new TestAgent(20, "Twenty", 20.2));
        toMatch.add(new TestAgent(30, "Thirty", 30.3));
        toMatch.add(new TestAgent(40, "Fourty", 40.4));
        toMatch.add(new TestAgent(50, "Fifty", 50.5));
        toMatch.add(new TestAgent(60, "Sixty", 60.6));
        toMatch.add(new TestAgent(70, "Seventy", 70.7));
        toMatch.add(new TestAgent(80, "Eighty", 80.8));
        toMatch.add(new TestAgent(90, "Ninety", 90.9));

        int misses = 0;

        //We use for loop and not iterator just in case there is something funky going on w/ the iterator
        for (int x = 0; x < ((Coordinate2DDiscrete) testScape.getExtent()).getXValue(); x++) {
            for (int y = 0; y < ((Coordinate2DDiscrete) testScape.getExtent()).getYValue(); y++) {
                TestAgent candidate = (TestAgent) ((Array2D) testScape.getSpace()).get(x, y);
                boolean success = toMatch.remove(candidate);
                if (!success) {
                    misses++;
                }
            }
        }

        assertTrue(toMatch.size() == 0);
        assertTrue(misses == 16 - 9);
    }

    public void testImmutableRandom() {
        Scape testScape = new TestScape();
        testScape.setExtent(3, 3);
        ScapeFromFileView fileView = new ScapeFromFileView();
        fileView.setMode(ScapeFromFileView.SIZE_BY_SCAPE_RANDOM_SAMPLE_MODE);
        try {
            fileView.setInputStream(ScapeFromFileViewTest.class.getResourceAsStream("ScapeFileImutableTestData.txt"));
        } catch (IOException e) {
            throw new RuntimeException("File Problem in Scape File Control Test" + e);
        }
        testScape.addView(fileView);
        testScape.setPrototypeAgent(new TestAgent());
        testScape.createScape();
        testScape.setAutoRestart(false);
        try {
            testScape.setStopPeriod(0);
        } catch (SpatialTemporalException e) {
        }
        testScape.getRunner().run();

        //Order is undefined, so we need to search for matches
        ArrayList toMatch = new ArrayList();
        for (int i = 0; i < 9; i++) {
            toMatch.add(new TestAgent(90, "Ninety", 90.9));
        }

        //We use for loop and not iterator just in case there is something funky going on w/ the iterator
        for (int x = 0; x < ((Coordinate2DDiscrete) testScape.getExtent()).getXValue(); x++) {
            for (int y = 0; y < ((Coordinate2DDiscrete) testScape.getExtent()).getYValue(); y++) {
                TestAgent candidate = (TestAgent) ((Array2D) testScape.getSpace()).get(x, y);
                boolean success = toMatch.remove(candidate);
                assertTrue(success);
            }
        }

        assertTrue(toMatch.size() == 0);
    }

    public void testImmutableRandomLow() {
        Scape testScape = new TestScapeLow();
        testScape.setExtent(3, 3);
        ScapeFromFileView fileView = new ScapeFromFileView();
        fileView.setMode(ScapeFromFileView.SIZE_BY_SCAPE_RANDOM_SAMPLE_MODE);
        try {
            fileView.setInputStream(ScapeFromFileViewTest.class.getResourceAsStream("ScapeFileImutableTestData.txt"));
        } catch (IOException e) {
            throw new RuntimeException("File Problem in Scape File Control Test" + e);
        }
        testScape.addView(fileView);
        testScape.setPrototypeAgent(new TestAgent());
        testScape.createScape();
        testScape.setAutoRestart(false);
        try {
            testScape.setStopPeriod(0);
        } catch (SpatialTemporalException e) {
        }
        testScape.getRunner().run();

        //Order is undefined, so we need to search for matches
        ArrayList toMatch = new ArrayList();
        for (int i = 0; i < 9; i++) {
            toMatch.add(new TestAgent(10, "Ten", 10.1));
        }

        //We use for loop and not iterator just in case there is something funky going on w/ the iterator
        for (int x = 0; x < ((Coordinate2DDiscrete) testScape.getExtent()).getXValue(); x++) {
            for (int y = 0; y < ((Coordinate2DDiscrete) testScape.getExtent()).getYValue(); y++) {
                TestAgent candidate = (TestAgent) ((Array2D) testScape.getSpace()).get(x, y);
                boolean success = toMatch.remove(candidate);
                assertTrue(success);
            }
        }

        assertTrue(toMatch.size() == 0);
    }

    public void testImmutableRandomSequence() {
        Scape testScape = new TestScapeSequence();
        testScape.setExtent(3, 3);
        ScapeFromFileView fileView = new ScapeFromFileView();
        fileView.setMode(ScapeFromFileView.SIZE_BY_SCAPE_RANDOM_SAMPLE_MODE);
        try {
            fileView.setInputStream(ScapeFromFileViewTest.class.getResourceAsStream("ScapeFileImutableTestData.txt"));
        } catch (IOException e) {
            throw new RuntimeException("File Problem in Scape File Control Test" + e);
        }
        testScape.addView(fileView);
        testScape.setPrototypeAgent(new TestAgent());
        testScape.createScape();
        testScape.setAutoRestart(false);
        try {
            testScape.setStopPeriod(0);
        } catch (SpatialTemporalException e) {
        }
        testScape.getRunner().run();

        //Order is undefined, so we need to search for matches
        ArrayList toMatch = new ArrayList();
        toMatch.add(new TestAgent(10, "Ten", 10.1));
        toMatch.add(new TestAgent(20, "Twenty", 20.2));
        toMatch.add(new TestAgent(30, "Thirty", 30.3));
        toMatch.add(new TestAgent(40, "Fourty", 40.4));
        toMatch.add(new TestAgent(50, "Fifty", 50.5));
        toMatch.add(new TestAgent(60, "Sixty", 60.6));
        toMatch.add(new TestAgent(70, "Seventy", 70.7));
        toMatch.add(new TestAgent(80, "Eighty", 80.8));
        toMatch.add(new TestAgent(90, "Ninety", 90.9));

        //We use for loop and not iterator just in case there is something funky going on w/ the iterator
        for (int x = 0; x < ((Coordinate2DDiscrete) testScape.getExtent()).getXValue(); x++) {
            for (int y = 0; y < ((Coordinate2DDiscrete) testScape.getExtent()).getYValue(); y++) {
                TestAgent candidate = (TestAgent) ((Array2D) testScape.getSpace()).get(x, y);
                boolean success = toMatch.remove(candidate);
                assertTrue(success);
            }
        }

        assertTrue(toMatch.size() == 0);
    }

    public void testImmutableRandomSequenceBig() {
        Scape testScape = new TestScapeSequence();
        testScape.setExtent(4, 4);
        ScapeFromFileView fileView = new ScapeFromFileView();
        fileView.setMode(ScapeFromFileView.SIZE_BY_SCAPE_RANDOM_SAMPLE_MODE);
        try {
            fileView.setInputStream(ScapeFromFileViewTest.class.getResourceAsStream("ScapeFileImutableTestData.txt"));
        } catch (IOException e) {
            throw new RuntimeException("File Problem in Scape File Control Test" + e);
        }
        testScape.addView(fileView);
        testScape.setPrototypeAgent(new TestAgent());
        testScape.createScape();
        testScape.setAutoRestart(false);
        try {
            testScape.setStopPeriod(0);
        } catch (SpatialTemporalException e) {
        }
        System.out.println("Not enough file elements warning expected below:");
        testScape.getRunner().run();

        //Order is undefined, so we need to search for matches
        ArrayList toMatch = new ArrayList();
        toMatch.add(new TestAgent(10, "Ten", 10.1));
        toMatch.add(new TestAgent(20, "Twenty", 20.2));
        toMatch.add(new TestAgent(30, "Thirty", 30.3));
        toMatch.add(new TestAgent(40, "Fourty", 40.4));
        toMatch.add(new TestAgent(50, "Fifty", 50.5));
        toMatch.add(new TestAgent(60, "Sixty", 60.6));
        toMatch.add(new TestAgent(70, "Seventy", 70.7));
        toMatch.add(new TestAgent(80, "Eighty", 80.8));
        toMatch.add(new TestAgent(90, "Ninety", 90.9));

        int misses = 0;

        //We use for loop and not iterator just in case there is something funky going on w/ the iterator
        for (int x = 0; x < ((Coordinate2DDiscrete) testScape.getExtent()).getXValue(); x++) {
            for (int y = 0; y < ((Coordinate2DDiscrete) testScape.getExtent()).getYValue(); y++) {
                TestAgent candidate = (TestAgent) ((Array2D) testScape.getSpace()).get(x, y);
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
        ScapeFromFileView fileView = new ScapeFromFileView();
        fileView.setMode(ScapeFromFileView.SIZE_BY_SCAPE_RANDOM_SAMPLE_MODE);
        try {
            fileView.setInputStream(ScapeFromFileViewTest.class.getResourceAsStream("ScapeFileImutableTestData.txt"));
        } catch (IOException e) {
            throw new RuntimeException("File Problem in Scape File Control Test" + e);
        }
        testScape.addView(fileView);
        testScape.setPrototypeAgent(new TestAgent());
        testScape.createScape();
        testScape.setAutoRestart(false);
        try {
            testScape.setStopPeriod(0);
        } catch (SpatialTemporalException e) {
        }
        testScape.getRunner().run();

        //Order is undefined, so we need to search for matches
        ArrayList toMatch = new ArrayList();
        toMatch.add(new TestAgent(10, "Ten", 10.1));
        toMatch.add(new TestAgent(20, "Twenty", 20.2));
        toMatch.add(new TestAgent(30, "Thirty", 30.3));
        toMatch.add(new TestAgent(40, "Fourty", 40.4));
        toMatch.add(new TestAgent(50, "Fifty", 50.5));
        toMatch.add(new TestAgent(60, "Sixty", 60.6));
        toMatch.add(new TestAgent(70, "Seventy", 70.7));
        toMatch.add(new TestAgent(80, "Eighty", 80.8));
        toMatch.add(new TestAgent(90, "Ninety", 90.9));

        //We use for loop and not iterator just in case there is something funky going on w/ the iterator
        for (int x = 0; x < ((Coordinate2DDiscrete) testScape.getExtent()).getXValue(); x++) {
            for (int y = 0; y < ((Coordinate2DDiscrete) testScape.getExtent()).getYValue(); y++) {
                TestAgent candidate = (TestAgent) ((Array2D) testScape.getSpace()).get(x, y);
                boolean success = toMatch.remove(candidate);
                assertTrue(success);
            }
        }

        assertTrue(toMatch.size() == 9 - 4);
    }

    public void testMutableRandomSequence() {
        TestScapeListSequence testScape = new TestScapeListSequence();
        testScape.setExtent(9);
        ScapeFromFileView fileView = new ScapeFromFileView();
        fileView.setMode(ScapeFromFileView.SIZE_BY_SCAPE_RANDOM_SAMPLE_MODE);
        try {
            fileView.setInputStream(ScapeFromFileViewTest.class.getResourceAsStream("ScapeFileImutableTestData.txt"));
        } catch (IOException e) {
            throw new RuntimeException("File Problem in Scape File Control Test" + e);
        }
        testScape.addView(fileView);
        testScape.setPrototypeAgent(new TestAgent());
        testScape.createScape();
        testScape.setAutoRestart(false);
        try {
            testScape.setStopPeriod(0);
        } catch (SpatialTemporalException e) {
        }
        testScape.getRunner().run();

        //Order is undefined, so we need to search for matches
        ArrayList toMatch = new ArrayList();
        toMatch.add(new TestAgent(10, "Ten", 10.1));
        toMatch.add(new TestAgent(20, "Twenty", 20.2));
        toMatch.add(new TestAgent(30, "Thirty", 30.3));
        toMatch.add(new TestAgent(40, "Fourty", 40.4));
        toMatch.add(new TestAgent(50, "Fifty", 50.5));
        toMatch.add(new TestAgent(60, "Sixty", 60.6));
        toMatch.add(new TestAgent(70, "Seventy", 70.7));
        toMatch.add(new TestAgent(80, "Eighty", 80.8));
        toMatch.add(new TestAgent(90, "Ninety", 90.9));

        //We use for loop and not iterator just in case there is something funky going on w/ the iterator
        for (int x = 0; x < ((Coordinate1DDiscrete) testScape.getExtent()).getXValue(); x++) {
            TestAgent candidate = (TestAgent) testScape.get(x);
            boolean success = toMatch.remove(candidate);
            assertTrue(success);
        }

        assertTrue(toMatch.size() == 0);
    }
}