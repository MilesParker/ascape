/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.test.util.sweep;

import junit.framework.TestCase;

import org.ascape.util.sweep.SweepDimension;


public class SweepDimensionTest extends TestCase {

    public SweepDimensionTest(String name) {
        super(name);
    }

    public class TestObject {

        public int value;
        public long longValue;
        public float floatValue;
        public double doubleValue;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public long getLongValue() {
            return longValue;
        }

        public void setLongValue(long longValue) {
            this.longValue = longValue;
        }

        public float getFloatValue() {
            return floatValue;
        }

        public void setFloatValue(float floatValue) {
            this.floatValue = floatValue;
        }

        public double getDoubleValue() {
            return doubleValue;
        }

        public void setDoubleValue(double doubleValue) {
            this.doubleValue = doubleValue;
        }
    }

    public void testInt() {
        SweepDimension testInt = new SweepDimension(new TestObject(), "Value", 2, 10, 1);
        int count = 0;
        while (testInt.hasNext()) {
            testInt.next();
            count++;
        }
        assertTrue(count == 9);
        testInt.reset();
        count = 0;
        while (testInt.hasNext()) {
            testInt.next();
            count++;
        }
        assertTrue(count == 9);
    }

    public void testInt2() {
        SweepDimension testInt = new SweepDimension(new TestObject(), "Value", 0, 1, 1);
        int count = 0;
        while (testInt.hasNext()) {
            testInt.next();
            count++;
        }
        assertTrue(count == 2);
        testInt.reset();
        count = 0;
        while (testInt.hasNext()) {
            testInt.next();
            count++;
        }
        assertTrue(count == 2);
    }

    public void testLong() {
        SweepDimension testLong = new SweepDimension(new TestObject(), "LongValue", 2l, 10l, 1l);
        int count = 0;
        while (testLong.hasNext()) {
            testLong.next();
            count++;
        }
        assertTrue(count == 9);
        testLong.reset();
        count = 0;
        while (testLong.hasNext()) {
            testLong.next();
            count++;
        }
        assertTrue(count == 9);
    }

    public void testFloat() {
        SweepDimension testFloat = new SweepDimension(new TestObject(), "FloatValue", 2.1f, 5.3f, .23f);
        testFloat.setMinAsText("2.0");
        testFloat.setMaxAsText("5.4");
        int count = 0;
        while (testFloat.hasNext()) {
            testFloat.next();
            count++;
        }
        assertTrue(count == 15);
        testFloat.reset();
        count = 0;
        while (testFloat.hasNext()) {
            testFloat.next();
            count++;
        }
        assertTrue(count == 15);
    }

    public void testDouble() {
        SweepDimension testDouble = new SweepDimension(new TestObject(), "DoubleValue", 2.1, 5.3, .23);
        int count = 0;
        testDouble.reset();
        while (testDouble.hasNext()) {
            testDouble.next();
            count++;
        }
        assertTrue(count == 14);
        testDouble.reset();
        count = 0;
        while (testDouble.hasNext()) {
            testDouble.next();
            count++;
        }
        assertTrue(count == 14);
    }
}
