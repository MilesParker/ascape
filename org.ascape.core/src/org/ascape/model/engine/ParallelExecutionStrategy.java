/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.engine;


/*
 * User: Miles Parker  
 * Date: Sep 10, 2003
 * Time: 4:12:29 PM
 * To change this template use Options | File Templates.
 */

/**
 * The Class ParallelExecutionStrategy.
 */
public abstract class ParallelExecutionStrategy extends IncrementalExecutionStrategy {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * The parallel manager.
     */
    private ParallelManager parallelManager;

    /**
     * Instantiates a new parallel execution strategy.
     * 
     * @param factory
     *            the factory
     */
    public ParallelExecutionStrategy(StrategyFactory factory) {
        super(factory);
    }

    /**
     * The x.
     */
    int x;

    /* (non-Javadoc)
     * @see org.ascape.model.engine.IncrementalExecutionStrategy#execute()
     */
    public void execute() {
        if (isSupportsParallel() && (factory.getThreads() > 1) && (factory.getScape().size() > 10)) {
            if (parallelManager == null) {
                parallelManager = new ParallelManager(factory, this);
            } else {
                parallelManager.setFactory(factory);
                parallelManager.setMainStrategy(this);
            }
            parallelManager.execute();
            reset();
        } else {
            super.execute();
        }
    }

    /**
     * Execute parallel.
     */
    public final void executeParallel() {
        while (hasNextParallel()) {
            increment();
            fire();
        }
    }

    /**
     * Checks for next parallel.
     * 
     * @return true, if successful
     */
    public boolean hasNextParallel() {
        throw new UnsupportedOperationException("Parallel operation not supported for this strategy.");
    }

    /**
     * Increment parallel.
     */
    public void incrementParallel() {
        increment();
    }

    /**
     * Next parallel sequence.
     */
    public void nextParallelSequence() {
        throw new UnsupportedOperationException("Parallel operation not supported for this strategy.");
    }

}
