/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.test.rule;

import java.util.Comparator;

import junit.framework.TestCase;

import org.ascape.model.Cell;
import org.ascape.model.Scape;
import org.ascape.model.rule.SearchRule;
import org.ascape.model.space.Coordinate1DDiscrete;

public class SearchRuleTest extends TestCase {

    public SearchRuleTest(String name) {
        super(name);
    }

    public void testEquals() {
        Scape testScape = new Scape();
        testScape.add(new Cell());
        testScape.add(new Cell());
        final Cell agentToFind = new Cell();
        testScape.add(agentToFind);
        testScape.initialize();
        Comparator searcher = new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Coordinate1DDiscrete) ((Cell) o1).getCoordinate()).getXValue() - ((Coordinate1DDiscrete) ((Cell) o2).getCoordinate()).getXValue();
            }
        };
        testScape.add(new Cell());
        testScape.add(new Cell());
        testScape.add(new Cell());
        SearchRule testRule = new SearchRule("Test");
        testRule.clear();
        testRule.setComparator(searcher);
        testRule.setKey(agentToFind);
        testScape.executeOnMembers(testRule);
        assertTrue(testRule.getFoundAgent() == agentToFind);
    }

    class NumberedCell extends Cell {

        int number;

        public NumberedCell(int number) {
            this.number = number;
        }
    }

    Comparator compareNumbers = new Comparator() {
        public int compare(Object o1, Object o2) {
            return ((NumberedCell) o1).number - ((NumberedCell) o2).number;
        }
    };

    public void testMinMaxAndEquals() {
        Scape testScape = new Scape();
        SearchRule testRule = new SearchRule("Test");
        testRule.clear();
        testRule.setComparator(compareNumbers);

        testScape.add(new NumberedCell(4));
        testScape.add(new NumberedCell(14));
        testScape.add(new NumberedCell(3568));
        testScape.add(new NumberedCell(1));
        testScape.add(new NumberedCell(27));
        testScape.add(new NumberedCell(0));
        testScape.add(new NumberedCell(3567));
        testScape.add(new NumberedCell(1234));

        testRule.setKey(new NumberedCell(27));

        testScape.executeOnMembers(testRule);
        assertTrue(testRule.getFoundAgent() == testScape.get(4));
        testRule.clear();

        testRule.setSearchType(SearchRule.SEARCH_MIN);
        testScape.executeOnMembers(testRule);
        assertTrue(testRule.getFoundAgent() == testScape.get(5));
        testRule.clear();

        testScape.add(4, new NumberedCell(-14));
        testScape.add(4, new NumberedCell(-10));
        testScape.executeOnMembers(testRule);
        assertTrue(testRule.getFoundAgent() == testScape.get(5));
        testRule.clear();

        testRule.setSearchType(SearchRule.SEARCH_MAX);
        testScape.executeOnMembers(testRule);
        assertTrue(testRule.getFoundAgent() == testScape.get(2));
    }
}
