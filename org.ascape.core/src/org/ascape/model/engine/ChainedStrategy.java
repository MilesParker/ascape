/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.engine;


/*
 * User: Miles Parker  
 * Date: Sep 23, 2003
 * Time: 1:07:54 PM
 * To change this template use Options | File Templates.
 */

/**
 * The Class ChainedStrategy.
 */
public class ChainedStrategy extends ExecutionStrategy {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The strategy1.
     */
    private ExecutionStrategy strategy1;

    /**
     * The strategy2.
     */
    private ExecutionStrategy strategy2;

    /**
     * Instantiates a new chained strategy.
     * 
     * @param strategy1
     *            the strategy1
     * @param strategy2
     *            the strategy2
     */
    public ChainedStrategy(ExecutionStrategy strategy1, ExecutionStrategy strategy2) {
        super();
        this.strategy1 = strategy1;
        this.strategy2 = strategy2;
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.ExecutionStrategy#execute()
     */
    public void execute() {
        strategy1.execute();
        strategy2.execute();
    }

    /* (non-Javadoc)
     * @see org.ascape.model.engine.ExecutionStrategy#reset()
     */
    public void reset() {
        strategy1.reset();
        strategy2.reset();
    }
}

