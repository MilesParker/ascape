/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.engine;

import java.io.Serializable;

import org.ascape.model.Agent;


/**
 * The Class TourAgentSelector.
 */
public class TourAgentSelector implements AgentSelector, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * The strategy.
     */
    IncrementalExecutionStrategy strategy;

    /**
     * Instantiates a new tour agent selector.
     * 
     * @param strategy
     *            the strategy
     */
    public TourAgentSelector(IncrementalExecutionStrategy strategy) {
        this.strategy = strategy;
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.AgentSelector#hasMoreAgents()
     */
    public boolean hasMoreAgents() {
        return strategy.getAgentIterator().hasNext();
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.AgentSelector#nextAgent()
     */
    public Agent nextAgent() {
        return (Agent) strategy.getAgentIterator().next();
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.Selector#reset()
     */
    public void reset() {
        strategy.getAgentIterator().first();
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
