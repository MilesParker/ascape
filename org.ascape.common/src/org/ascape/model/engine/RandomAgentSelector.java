/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.engine;

import org.ascape.model.Agent;
import org.ascape.model.Scape;


/**
 * The Class RandomAgentSelector.
 */
public class RandomAgentSelector implements AgentSelector {

    /**
     * The strategy.
     */
    private IncrementalExecutionStrategy strategy;
    
    /**
     * The scape.
     */
    private Scape scape;
    
    /**
     * The iteration count.
     */
    private int iterationCount;
    
    /**
     * The iteration.
     */
    private int iteration;

    /**
     * Instantiates a new random agent selector.
     * 
     * @param strategy
     *            the strategy
     * @param iterationCount
     *            the iteration count
     */
    public RandomAgentSelector(IncrementalExecutionStrategy strategy, int iterationCount) {
        setStrategy(strategy);
        this.iterationCount = iterationCount;
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.AgentSelector#hasMoreAgents()
     */
    public boolean hasMoreAgents() {
        return (iteration < iterationCount);
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.AgentSelector#nextAgent()
     */
    public Agent nextAgent() {
        iteration++;
        return scape.findRandom();
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.Selector#reset()
     */
    public void reset() {
        iteration = 0;
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.AgentSelector#getStrategy()
     */
    public IncrementalExecutionStrategy getStrategy() {
        return strategy;
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.AgentSelector#setStrategy(org.ascape.model.engine.IncrementalExecutionStrategy)
     */
    public void setStrategy(IncrementalExecutionStrategy strategy) {
        this.strategy = strategy;
        scape = strategy.getScape();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        try {
            AgentSelector clone = (AgentSelector) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e.toString());  //To change body of catch statement use Options | File Templates.
        }
    }
}
