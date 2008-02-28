/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.engine;

import org.ascape.model.space.Mutable;


/**
 * The Class RuleOrderMutableStrategy.
 */
public class RuleOrderMutableStrategy extends RuleOrderStrategy {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new rule order mutable strategy.
     * 
     * @param factory
     *            the factory
     */
    public RuleOrderMutableStrategy(StrategyFactory factory) {
        super(factory);
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.RuleOrderStrategy#increment()
     */
    public final void increment() {
        currentAgent = agentSelector.nextAgent();
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.RuleOrderStrategy#nextParallelSequence()
     */
    public void nextParallelSequence() {
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.RuleOrderStrategy#hasNext()
     */
    public boolean hasNext() {
        if (agentSelector.hasMoreAgents()) {
            return true;
        } else {
            if (ruleSelector.hasMoreRules()) {
                incrementRule();
                return agentSelector.hasMoreAgents();
            } else {
                return false;
            }
        }
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.RuleOrderStrategy#reset()
     */
    public void reset() {
        super.reset();
        ((Mutable) getScape().getSpace()).deleteSweep();
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.RuleOrderStrategy#isSupportsParallel()
     */
    public boolean isSupportsParallel() {
        return !factory.isAnyRandom() && !factory.isAnyDelete();
    }
}
