/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.test.model.strategy;

import junit.framework.TestCase;

import org.ascape.model.engine.ChainedStrategy;
import org.ascape.model.engine.ParallelExecutionStrategy;
import org.ascape.model.engine.StrategyFactory;
import org.ascape.model.rule.Rule;


/*
 * User: Miles Parker  
 * Date: Sep 24, 2003
 * Time: 3:07:29 PM
 * To change this template use Options | File Templates.
 */

public class ChainedStrategyTest extends TestCase {

    public ChainedStrategyTest(String s) {
        super(s);
    }

    class TestExecutionStrategy extends ParallelExecutionStrategy {

        int size = 10;

        int index;

        public TestExecutionStrategy(StrategyFactory factory) {
            super(factory);
        }

        public boolean hasNext() {
            return index < size;
        }

        public final void increment() {
            index++;
        }
    }

    public void testChained() {
        TestExecutionStrategy strat1 = new TestExecutionStrategy(new StrategyFactory(null, new Rule[0], 1)) {
            public void reset() {
            }

            public void fire() {
            }
        };
        TestExecutionStrategy strat2 = new TestExecutionStrategy(new StrategyFactory(null, new Rule[0], 1)) {
            public void reset() {
            }

            public void fire() {
            }
        };
        ChainedStrategy chained = new ChainedStrategy(strat1, strat2);
        chained.execute();
        assertTrue(strat1.index == strat1.size);
        assertTrue(strat2.index == strat2.size);
    }
}
