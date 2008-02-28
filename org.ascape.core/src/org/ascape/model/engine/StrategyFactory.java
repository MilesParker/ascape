/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.engine;

import java.io.Serializable;

import org.ascape.model.Scape;
import org.ascape.model.rule.ExecuteThenUpdate;
import org.ascape.model.rule.Rule;
import org.ascape.util.ResetableIterator;

/*
 * User: Miles Parker
 * Date: Sep 23, 2003
 * Time: 3:13:36 PM
 * To change this template use Options | File Templates.
 */

/**
 * A factory for creating Strategy objects.
 */
public class StrategyFactory implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The any random.
     */
    private boolean anyRandom;
    
    /**
     * The any iterate all.
     */
    private boolean anyIterateAll;
    
    /**
     * The any delete.
     */
    private boolean anyDelete;
    
    /**
     * The any update.
     */
    private boolean anyUpdate;

    /**
     * The scape.
     */
    private Scape scape;
    
    /**
     * The rules.
     */
    private Object[] rules;
    
    /**
     * The threads.
     */
    private int threads;
    
    /**
     * The iteration count.
     */
    private int iterationCount;

    /**
     * The strategy.
     */
    private ExecutionStrategy strategy;

    /**
     * Instantiates a new strategy factory.
     * 
     * @param scape
     *            the scape
     * @param rules
     *            the rules
     * @param threads
     *            the threads
     */
    public StrategyFactory(Scape scape, Object[] rules, int threads) {
        this.rules = rules;
        this.scape = scape;
        this.threads = threads;
    }

    /**
     * Gets the strategy.
     * 
     * @return the strategy
     */
    public final ExecutionStrategy getStrategy() {
        if (strategy == null) {
            strategy = createStrategy();
        }
        return strategy;
    }

    /**
     * Creates a new Strategy object.
     * 
     * @return the execution strategy
     */
    public ExecutionStrategy createStrategy() {
        ExecutionStrategy strategy = null;

        iterationCount = scape.getSize();
        if (scape.getAgentsPerIteration() != Scape.ALL_AGENTS) {
            iterationCount = Math.min(iterationCount, scape.getAgentsPerIteration());
        }
        analyzeRules();

        IncrementalExecutionStrategy mainStrategy = selectMainStrategy();
        mainStrategy.setAgentIterator(selectAgentIterator());
        assignAgentSelector(mainStrategy);

        //All of the below is only to handle special cases where updates are needed or if there iterate all rules mixed in underpartial execution
        mainStrategy.setRuleSelector(new DefaultRuleSelector(rules));
        ParallelExecutionStrategy updateStrategy = null;
        if ((scape.getExecutionOrder() == Scape.RULE_ORDER) && (anyUpdate)) {
            //todo; this code currently assumes that there is no case where there are update rules that are flagged iterateAll.
            if (!scape.isMutable()) {
                updateStrategy = new RuleOrderUpdateStrategy(this);
            } else {
                updateStrategy = new RuleOrderUpdateMutableStrategy(this);
            }
            updateStrategy.setAgentSelector(mainStrategy.getAgentSelector());
            updateStrategy.setRuleSelector(new UpdateRuleSelector(rules));
        }
        if (((iterationCount < scape.getSize()) || (scape.getExecutionStyle() == Scape.REPEATED_DRAW)) && (anyIterateAll)) {
            if ((scape.getExecutionStyle() == Scape.COMPLETE_TOUR) && (scape.getExecutionOrder() == Scape.AGENT_ORDER)) {
                mainStrategy.setAgentSelector(new FlaggedTourAgentSelector(mainStrategy, iterationCount));
                mainStrategy.setRuleSelector(new PartialRuleSelector(rules, (FlaggedTourAgentSelector) mainStrategy.getAgentSelector()));
            } else {
                IncrementalExecutionStrategy iterateAllStrategy = (ParallelExecutionStrategy) mainStrategy.clone();
                mainStrategy.setRuleSelector(new NoIterateAllRuleSelector(rules));
                iterateAllStrategy.setAgentSelector(new TourAgentSelector(mainStrategy));
                iterateAllStrategy.setRuleSelector(new IterateAllRuleSelector(rules));
                strategy = mainStrategy.chain(iterateAllStrategy);
            }
        }
        if ((scape.getExecutionOrder() == Scape.RULE_ORDER) && (anyUpdate)) {
            strategy = mainStrategy.chain(updateStrategy);
        }
        if (strategy == null) {
            strategy = mainStrategy;
        }
        //todo, we need to determine more about what strategies should not be parralellizable -- for example, multiple rules agent order?
        return strategy;
    }

    /**
     * Assign agent selector.
     * 
     * @param strategy
     *            the strategy
     */
    private void assignAgentSelector(IncrementalExecutionStrategy strategy) {
        if (scape.getExecutionStyle() == Scape.COMPLETE_TOUR) {
            if (iterationCount >= scape.getSize()) {
                strategy.agentSelector = new TourAgentSelector(strategy);
            } else {
                strategy.agentSelector = new PartialTourAgentSelector(strategy, iterationCount);
            }
        } else {
            strategy.agentSelector = new RandomAgentSelector(strategy, iterationCount);
        }
    }

    /**
     * Select main strategy.
     * 
     * @return the incremental execution strategy
     */
    private IncrementalExecutionStrategy selectMainStrategy() {
        IncrementalExecutionStrategy strategy;
        if (scape.getExecutionOrder() == Scape.AGENT_ORDER) {
            if (!scape.isMutable() || !anyDelete) {
                strategy = new AgentOrderStrategy(this);
            } else {
                strategy = new AgentOrderMutableStrategy(this);
            }
        } else {
            if (!scape.isMutable()) {
                strategy = new RuleOrderStrategy(this);
            } else {
                strategy = new RuleOrderMutableStrategy(this);
            }
        }
        return strategy;
    }

    /**
     * Select agent iterator.
     * 
     * @return the resetable iterator
     */
    private ResetableIterator selectAgentIterator() {
        ResetableIterator agentIterator;
        if (anyRandom || (iterationCount < scape.getSize())) {
            agentIterator = scape.getSpace().safeRandomIterator();
        } else {
            agentIterator = scape.getSpace().safeIterator();
        }
        return agentIterator;
    }
//
//    private ResetableIterator[] selectAgentIterators(int count) {
//        ResetableIterator[] iterators;
//        if (anyRandom || (iterationCount < scape.getSize())) {
//            iterators = scape.scapeIterators(count);
//        } else {
//            iterators = scape.scapeIterators(count);
//        }
//        return iterators;
//    }

    /**
 * Analyze rules.
 */
private void analyzeRules() {
        anyRandom = false;
        anyIterateAll = false;
        anyDelete = false;
        anyUpdate = false;
        for (int i = 0; i < rules.length; i++) {
            if (((Rule) rules[i]).isRandomExecution()) {
                anyRandom = true;
            }
            if (((Rule) rules[i]).isIterateAll()) {
                anyIterateAll = true;
            }
            if (((Rule) rules[i]).isCauseRemoval()) {
                anyDelete = true;
            }
            if (((Rule) rules[i]) instanceof ExecuteThenUpdate) {
                anyUpdate = true;
            }
        }
    }

    /**
     * Checks if is any delete.
     * 
     * @return true, if is any delete
     */
    public final boolean isAnyDelete() {
        return anyDelete;
    }

    /**
     * Checks if is any iterate all.
     * 
     * @return true, if is any iterate all
     */
    public final boolean isAnyIterateAll() {
        return anyIterateAll;
    }

    /**
     * Checks if is any random.
     * 
     * @return true, if is any random
     */
    public final boolean isAnyRandom() {
        return anyRandom;
    }

    /**
     * Gets the scape.
     * 
     * @return the scape
     */
    public final Scape getScape() {
        return scape;
    }

    /**
     * Gets the rules.
     * 
     * @return the rules
     */
    public final Object[] getRules() {
        return rules;
    }

    /**
     * Gets the threads.
     * 
     * @return the threads
     */
    public final int getThreads() {
        return threads;
    }
}

