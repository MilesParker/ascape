/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.test.util.stat;

import org.ascape.util.data.StatCollectorCondCSAMM;

public class StatCollectorCondCSAMMTest extends StatCollectorCSAMMTest {

    public StatCollectorCondCSAMMTest(String name) {
        super(name);
    }

    public void testConstructorsAndClear() {
        stat = new StatCollectorCondCSAMM() {
            public boolean meetsCondition(Object o) {
                return true;
            }
        };
        subTestBasic();
        stat = new StatCollectorCondCSAMM() {
            public boolean meetsCondition(Object o) {
                return true;
            }
        };
        subTestNegative();
        stat = new StatCollectorCondCSAMM() {
            public boolean meetsCondition(Object o) {
                return true;
            }
        };
        subTestPositive();

        stat = new StatCollectorCondCSAMM("Test Stat") {
            public boolean meetsCondition(Object o) {
                return true;
            }
        };
        subTestBasic();
        stat = new StatCollectorCondCSAMM("Test Stat") {
            public boolean meetsCondition(Object o) {
                return true;
            }
        };
        subTestNegative();
        stat = new StatCollectorCondCSAMM("Test Stat") {
            public boolean meetsCondition(Object o) {
                return true;
            }
        };
        subTestPositive();

        stat = new StatCollectorCondCSAMM("Test Stat", true) {
            public boolean meetsCondition(Object o) {
                return true;
            }
        };
        subTestBasic();
        stat = new StatCollectorCondCSAMM("Test Stat", true) {
            public boolean meetsCondition(Object o) {
                return true;
            }
        };
        subTestNegative();
        stat = new StatCollectorCondCSAMM("Test Stat", true) {
            public boolean meetsCondition(Object o) {
                return true;
            }
        };
        subTestPositive();

        stat.clear();
        subTestBasic();
        stat.clear();
        subTestNegative();
        stat.clear();
        subTestPositive();
    }
}