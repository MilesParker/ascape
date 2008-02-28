/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.engine;



/**
 * The Class FlaggedTourAgentSelector.
 */
public class FlaggedTourAgentSelector extends PartialTourAgentSelector {

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
     * Instantiates a new flagged tour agent selector.
     * 
     * @param strategy
     *            the strategy
     * @param iterationCount
     *            the iteration count
     */
    public FlaggedTourAgentSelector(IncrementalExecutionStrategy strategy, int iterationCount) {
        super(strategy, iterationCount);
        this.iterationCount = iterationCount;
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.PartialTourAgentSelector#hasMoreAgents()
     */
    public boolean hasMoreAgents() {
        return strategy.getAgentIterator().hasNext();
    }

    /**
     * Checks for more partial agents.
     * 
     * @return true, if successful
     */
    public boolean hasMorePartialAgents() {
        return ((strategy.getAgentIterator().hasNext()) && (iteration < iterationCount));
    }
}
