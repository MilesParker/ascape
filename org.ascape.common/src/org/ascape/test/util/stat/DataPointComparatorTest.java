/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.test.util.stat;

import junit.framework.TestCase;

import org.ascape.model.Cell;
import org.ascape.model.Scape;
import org.ascape.model.rule.SearchRule;
import org.ascape.util.data.DataPointComparator;

public class DataPointComparatorTest extends TestCase {

    public DataPointComparatorTest(String name) {
        super(name);
    }

    public class TestCell extends Cell {

        double testValue;

        public TestCell() {
        }

        public TestCell(double testValue) {
            this.testValue = testValue;
        }
    }

    static final DataPointComparator testComp = new DataPointComparator() {
        public double getValue(Object o) {
            return ((TestCell) o).testValue;
        }
    };

    public void testComparison() {
        TestCell o1 = new TestCell();
        TestCell o2 = new TestCell();
        o1.testValue = 0.0000001;
        o2.testValue = 0.00000011;
        assertTrue(testComp.compare(o1, o2) == -1);
        assertTrue(testComp.compare(o2, o1) == 1);
        o1.testValue = 10.0;
        o2.testValue = 100.0 / 10.0;
        assertTrue(testComp.compare(o2, o1) == 0);
        o1.testValue = 1.0 / 3.0;
        o2.testValue = 0.333333333333333333;
        assertTrue(testComp.compare(o2, o1) == 0);
    }


    public void testInSearch() {
        Scape testScape = new Scape();
        SearchRule testRule = new SearchRule("Test");
        testRule.clear();
        testRule.setComparator(testComp);

        testScape.add(new TestCell(123.2323));
        testScape.add(new TestCell(123.1121));
        testScape.add(new TestCell(0.0));
        testScape.add(new TestCell(0.12));
        testScape.add(new TestCell(10.0));
        testScape.add(new TestCell(-2312.232));
        testScape.add(new TestCell(-2312.234));
        testScape.add(new TestCell(1222.09));
        testScape.add(new TestCell(121.22));

        testRule.setKey(new TestCell(100.0 / 10.0));

        testScape.executeOnMembers(testRule);
        assertTrue(testRule.getFoundAgent() == testScape.get(4));
        testRule.clear();

        testRule.setSearchType(SearchRule.SEARCH_MIN);
        testScape.executeOnMembers(testRule);
        assertTrue(testRule.getFoundAgent() == testScape.get(6));
        testRule.clear();

        testRule.setSearchType(SearchRule.SEARCH_MAX);
        testScape.executeOnMembers(testRule);
        assertTrue(testRule.getFoundAgent() == testScape.get(7));
    }
}