/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.engine;



/**
 * The Class RuleOrderStrategy.
 */
public class RuleOrderStrategy extends ParallelExecutionStrategy {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new rule order strategy.
     * 
     * @param factory
     *            the factory
     */
    public RuleOrderStrategy(StrategyFactory factory) {
        super(factory);
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.IncrementalExecutionStrategy#hasNext()
     */
    public boolean hasNext() {
        return (ruleSelector.hasMoreRules() || agentSelector.hasMoreAgents());
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.ParallelExecutionStrategy#hasNextParallel()
     */
    public final boolean hasNextParallel() {
        return agentSelector.hasMoreAgents() && (currentRule != null);
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.ParallelExecutionStrategy#nextParallelSequence()
     */
    public void nextParallelSequence() {
        incrementRule();
    }

    /**
     * Increment rule.
     */
    void incrementRule() {
        agentSelector.reset();
        currentRule = ruleSelector.nextRule();
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.IncrementalExecutionStrategy#increment()
     */
    public void increment() {
        if ((ruleSelector.hasMoreRules() && !agentSelector.hasMoreAgents())) {
            incrementRule();
        }
        currentAgent = agentSelector.nextAgent();
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.IncrementalExecutionStrategy#reset()
     */
    public void reset() {
        super.reset();
        currentRule = null;
        if (ruleSelector.hasMoreRules()) {
            currentRule = ruleSelector.nextRule();
        }
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.ExecutionStrategy#isSupportsParallel()
     */
    public boolean isSupportsParallel() {
        return !factory.isAnyRandom();
    }
}
