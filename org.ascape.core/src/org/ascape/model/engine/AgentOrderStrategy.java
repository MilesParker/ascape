/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.engine;



/**
 * The Class AgentOrderStrategy.
 */
public class AgentOrderStrategy extends ParallelExecutionStrategy {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new agent order strategy.
     * 
     * @param factory
     *            the factory
     */
    public AgentOrderStrategy(StrategyFactory factory) {
        super(factory);
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.IncrementalExecutionStrategy#hasNext()
     */
    public boolean hasNext() {
        return ((currentAgent == null) && (agentSelector.hasMoreAgents() && ruleSelector.hasMoreRules()) ||
            ((currentAgent != null) && ((ruleSelector.hasMoreRules() || agentSelector.hasMoreAgents()))));
    }

    //Presuming we get to this point, which should only happen for situations like collect stats, etc..
    /* (non-Javadoc)
     * @see org.ascape.model.engine.ParallelExecutionStrategy#hasNextParallel()
     */
    public final boolean hasNextParallel() {
        return hasNext();
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.ParallelExecutionStrategy#nextParallelSequence()
     */
    public void nextParallelSequence() {
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.IncrementalExecutionStrategy#increment()
     */
    public final void increment() {
        if (!ruleSelector.hasMoreRules() || currentAgent == null) {
            ruleSelector.reset();
            currentAgent = agentSelector.nextAgent();
        }
        currentRule = ruleSelector.nextRule();
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.IncrementalExecutionStrategy#reset()
     */
    public void reset() {
        super.reset();
        agentSelector.reset();
        currentAgent = null;
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.ExecutionStrategy#isSupportsParallel()
     */
    public boolean isSupportsParallel() {
        return !factory.isAnyRandom();
    }
}

