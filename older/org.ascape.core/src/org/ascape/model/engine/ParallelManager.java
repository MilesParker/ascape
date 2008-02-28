/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.engine;

import org.ascape.util.ResetableIterator;

/*
 * User: Miles Parker
 * Date: Sep 23, 2003
 * Time: 1:06:38 PM
 * To change this template use Options | File Templates.
 */

/**
 * The Class ParallelManager.
 */
public class ParallelManager {

    /**
     * The factory.
     */
    private StrategyFactory factory;
    
    /**
     * The main strategy.
     */
    private ParallelExecutionStrategy mainStrategy;

    /**
     * The active count.
     */
    private int activeCount;

    /**
     * The total call count.
     */
    int totalCallCount;

    /**
     * The Class Barrier.
     */
    class Barrier {
        
        /**
         * The threads finished.
         */
        int threadsFinished;
        
        /**
         * Wait for all.
         * 
         * @throws InterruptedException
         *             the interrupted exception
         */
        public synchronized void waitForAll() throws InterruptedException {
            threadsFinished++;
            if (threadsFinished == executors.length) {
                threadsFinished = 0;
                notifyAll();
            } else {
                wait();
            }
        }
    }

    /**
     * The execute barrier.
     */
    Barrier executeBarrier = new Barrier();

    /**
     * The Class ExecutionThread.
     */
    class ExecutionThread extends Thread {

        /**
         * The strategy.
         */
        ParallelExecutionStrategy strategy;

        /**
         * Instantiates a new execution thread.
         * 
         * @param name
         *            the name
         * @param strategy
         *            the strategy
         */
        public ExecutionThread(String name, ParallelExecutionStrategy strategy) {
            super(name);
            this.strategy = (ParallelExecutionStrategy) strategy.clone();
        }

        /* (non-Javadoc)
         * @see java.lang.Thread#start()
         */
        public void start() {
            super.start();
            activeCount++;
            setName(activeCount + " Execution Thread");
            //System.out.println(strategy + ", " + strategy.getScape().getPeriod() + ": "+ getName() + " --" + activeCount);
        }

        /**
         * The prime.
         */
        boolean prime;

        /* (non-Javadoc)
         * @see java.lang.Thread#run()
         */
        public synchronized void run() {
            strategy.reset();
            while (strategy.hasNext()) {
                strategy.executeParallel();
                if (strategy.hasNext()) {
                    strategy.nextParallelSequence();
                }
                try {
                    executeBarrier.waitForAll();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            synchronized (ParallelManager.this) {
                ParallelManager.this.notify();
            }
        }

        /**
         * Gets the strategy.
         * 
         * @return the strategy
         */
        public ParallelExecutionStrategy getStrategy() {
            return strategy;
        }

        /**
         * Next.
         */
        public void next() {
            strategy.nextParallelSequence();
        }
    }

    /**
     * The executors.
     */
    private ExecutionThread[] executors = new ExecutionThread[0];

    /**
     * The i.
     */
    int i = 0;

    /**
     * The post.
     */
    Object post = new Object();

    /**
     * Instantiates a new parallel manager.
     * 
     * @param factory
     *            the factory
     * @param mainStrategy
     *            the main strategy
     */
    public ParallelManager(StrategyFactory factory, ParallelExecutionStrategy mainStrategy) {
        this.factory = factory;
        this.mainStrategy = mainStrategy;
    }

    /**
     * Execute.
     */
    public synchronized void execute() {
        createThreads();
        assignIterators();
        threadStart();
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Thread start.
     */
    private void threadStart() {
        for (int i = 0; i < executors.length; i++) {
            executors[i].start();
        }
    }

    /**
     * Assign iterators.
     */
    private void assignIterators() {
        ResetableIterator iterators[] = mainStrategy.getScape().scapeIterators(factory.getThreads());
        for (int i = 0; i < executors.length; i++) {
            executors[i].getStrategy().setAgentIterator(iterators[i]);
        }
    }

    /**
     * Creates the threads.
     */
    private void createThreads() {
        if (factory.getThreads() != executors.length) {
            executors = new ExecutionThread[factory.getThreads()];
            for (int i = 0; i < executors.length; i++) {
                executors[i] = new ExecutionThread(mainStrategy + " thread " + i, mainStrategy);
            }
        }
    }

    /**
     * Sets the main strategy.
     * 
     * @param mainStrategy
     *            the new main strategy
     */
    public void setMainStrategy(ParallelExecutionStrategy mainStrategy) {
        this.mainStrategy = mainStrategy;
    }

    /**
     * Sets the factory.
     * 
     * @param factory
     *            the new factory
     */
    public void setFactory(StrategyFactory factory) {
        this.factory = factory;
    }
}

