/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.rule;

import java.util.Comparator;

import org.ascape.model.Agent;

/**
 * A rule that can be used to search through a collection of agents.
 * 
 * @author Miles Parker
 * @version 2.0
 * @history 2.0 first in 6/2/2001
 * @since 2.0
 */
public class SearchRule extends Rule {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a search rule with the providied name. It is strongly
     * encouraged to provide a name for all rules. This name will be used for
     * run time rule selection and provides important information fopr analyzing
     * and debugging models.
     * 
     * @param name
     *            the name of this object
     */
    public SearchRule(String name) {
        super(name);
    }

    /**
     * The found agent.
     */
    private Agent foundAgent;

    /**
     * The comparator.
     */
    private Comparator comparator;

    /**
     * The key.
     */
    private Object key;

    /**
     * The symbol for an 'equals' search.
     */
    public final static int SEARCH_EQUAL = 1;

    /**
     * The symbol for a 'minimum' search.
     */
    public final static int SEARCH_MIN = 2;

    /**
     * The symbol for a 'maximum' search.
     */
    public final static int SEARCH_MAX = 3;

    /**
     * The Class Comparison.
     */
    private abstract class Comparison {

        /**
         * Execute.
         * 
         * @param a
         *            the a
         */
        public abstract void execute(Agent a);
    }

    /**
     * The COMPAR e_ EQUALS.
     */
    private final Comparison COMPARE_EQUALS = new Comparison() {
        public void execute(Agent a) {
            if (comparator.compare(a, key) == 0) {
                foundAgent = a;
            }
        }
    };

    /**
     * The COMPAR e_ MIN.
     */
    private final Comparison COMPARE_MIN = new Comparison() {
        public void execute(Agent a) {
            if ((foundAgent == null) || (comparator.compare(a, foundAgent) < 0)) {
                foundAgent = a;
            }
        }
    };

    /**
     * The COMPAR e_ MAX.
     */
    private final Comparison COMPARE_MAX = new Comparison() {
        public void execute(Agent a) {
            if ((foundAgent == null) || (comparator.compare(a, foundAgent) > 0)) {
                foundAgent = a;
            }
        }
    };

    /**
     * The comparison.
     */
    private Comparison comparison = COMPARE_EQUALS;

    /**
     * Perform the search rule for the specified agent.
     * 
     * @param agent
     *            the target agent.
     */
    public void execute(Agent agent) {
        comparison.execute(agent);
    }

    /**
     * Clears the found result. Must be called before executing the rule.
     */
    public void clear() {
        foundAgent = null;
    }

    /**
     * In geneeral, the search in general does not need to be random.
     * 
     * @return true, if is random
     */
    public boolean isRandom() {
        //later will be true when we modify to allow only n firms to be searched an iteration..
        return false;
    }

    /**
     * Returns the comparator used for this search.
     * 
     * @return the comparator
     */
    public Comparator getComparator() {
        return comparator;
    }

    /**
     * Sets the comparator for the search.
     * 
     * @param comparator
     *            the compartor to use for the search.
     */
    public void setComparator(Comparator comparator) {
        this.comparator = comparator;
    }

    /**
     * Returns the key used for this search.
     * 
     * @return the key
     */
    public Object getKey() {
        return key;
    }

    /**
     * Sets the key for the search.
     * 
     * @param key
     *            the compartor to use for the search.
     */
    public void setKey(Object key) {
        this.key = key;
    }

    /**
     * Gets the found agent.
     * 
     * @return the found agent
     */
    public Agent getFoundAgent() {
        return foundAgent;
    }

    /**
     * Sets the search type to be used for this search. Default is
     * SEARCH_EQUALS.
     * 
     * @param type
     *            one of the "SEARCH" symbols specified above.
     */
    public void setSearchType(int type) {
        switch (type) {
            case SEARCH_EQUAL:
                comparison = COMPARE_EQUALS;
                break;
            case SEARCH_MIN:
                comparison = COMPARE_MIN;
                break;
            case SEARCH_MAX:
                comparison = COMPARE_MAX;
                break;
            default:
                throw new RuntimeException("Tried to set a bad search type rule: " + type);
        }
    }

    /**
     * Returns the search type used in any searches using this rule.
     * 
     * @return the type
     */
    public int getType() {
        if (comparison == COMPARE_EQUALS) {
            return SEARCH_EQUAL;
        } else if (comparison == COMPARE_MIN) {
            return SEARCH_MIN;
        } else if (comparison == COMPARE_MAX) {
            return SEARCH_MAX;
        } else {
            //Shouldn't really happen since we can't set a bad value..
            throw new RuntimeException("Bad search type rule: " + comparison);
        }
    }
}
