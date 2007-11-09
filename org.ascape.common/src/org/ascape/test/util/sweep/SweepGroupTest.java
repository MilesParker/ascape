/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.test.util.sweep;

import junit.framework.TestCase;

import org.ascape.util.sweep.SweepDimension;
import org.ascape.util.sweep.SweepGroup;


public class SweepGroupTest extends TestCase {

    public SweepGroupTest(String name) {
        super(name);
    }

    public static class TestObject {

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

        public String toString() {
            return value + "," + floatValue + "," + doubleValue + ",";
        }
    }

    public void testGroups() {
        SweepGroup testGroup = new SweepGroup();
        TestObject testObject = new TestObject();
        testGroup.setRunsPer(3);
        testGroup.addMember(new SweepDimension(testObject, "Value", 2, 4, 1));
        testGroup.addMember(new SweepDimension(testObject, "FloatValue", 3.0f, 6.1f, 1.1f));
        testGroup.addMember(new SweepDimension(testObject, "DoubleValue", 2.0, 8.0, 2.0));
        int count = 0;
        while (testGroup.hasNext()) {
            testGroup.next();
            //System.out.print(testObject+" ");
            count++;
        }
        assertTrue(count == 36 * 3);
    }
}
