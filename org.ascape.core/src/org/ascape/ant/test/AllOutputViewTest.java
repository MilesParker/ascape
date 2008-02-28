package org.ascape.ant.test;


import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.ascape.ant.AllOutputView;
import org.ascape.model.Cell;
import org.ascape.model.Scape;
import org.ascape.model.space.Array2DMoore;
import org.ascape.model.space.SpatialTemporalException;

/**

 * User: jmiller
 * Date: Nov 2, 2005
 * Time: 2:22:52 PM
 * To change this template use Options | File Templates.
 */
public class AllOutputViewTest extends TestCase{

    public AllOutputViewTest(String s) {
        super(s);
    }

    class TestScape extends Scape {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

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

    public class TestAgent extends Cell {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;
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

    public void testView() {
        Scape testScape = new Scape(new Array2DMoore());
        testScape.setExtent(3, 3);
        testScape.setAutoRestart(false);
        testScape.setPrototypeAgent(new TestAgent());
        testScape.createScape();
        try {
            testScape.setStopPeriod(0);
        } catch (SpatialTemporalException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        AllOutputView view = new AllOutputView();
        try {
            view.setRunFile(new File("AllOutputTest.xml"));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        testScape.addView(view);
        testScape.getRunner().run();
    }
}
