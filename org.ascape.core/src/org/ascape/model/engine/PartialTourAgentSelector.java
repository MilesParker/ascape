/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.engine;

import org.ascape.model.Agent;


/**
 * The Class PartialTourAgentSelector.
 */
public class PartialTourAgentSelector extends TourAgentSelector {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The iteration count.
     */
    private int iterationCount;
    
    /**
     * The iteration.
     */
    private int iteration;

    /**
     * Instantiates a new partial tour agent selector.
     * 
     * @param strategy
     *            the strategy
     * @param iterationCount
     *            the iteration count
     */
    public PartialTourAgentSelector(IncrementalExecutionStrategy strategy, int iterationCount) {
        super(strategy);
        this.iterationCount = iterationCount;
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.TourAgentSelector#nextAgent()
     */
    public Agent nextAgent() {
        iteration++;
        return (Agent) strategy.getAgentIterator().next();
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.TourAgentSelector#hasMoreAgents()
     */
    public boolean hasMoreAgents() {
        return ((strategy.getAgentIterator().hasNext()) && (iteration < iterationCount));
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.TourAgentSelector#reset()
     */
    public void reset() {
        super.reset();
        iteration = 0;
    }
}

