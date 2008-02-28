/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.data;

import org.ascape.util.Conditional;

/**
 * Collects and stores aggregate statistics on some data source for every specified measurement type
 * of that source, and for each succesive measurement of that source, while that source meets some condition.
 * See StatCollector documentation for a discussion of how to use stat collectors.
 * Conditional stat collectors allow you to specifiy a condition that must be met for a statistic to be measured for
 * an object. In Ascape, they are useful in cases where you want to collect statistics automatically, but you want to
 * restrict the statistic to some subpopulation of a scape.
 * The following example creates an anonymous class that gathers age statistics for agents with wealth over 50,000, and adds
 * it to the parent scape so it can be collected.
 * <pre>
 * scape.addStatCollector(new StatCollectorCSA("Age") {
 *    public boolean meetsCondition(Object object) {
 *         return ((AnObjectWithWealth) object).getWealth() >= 50000;
 *    }
 *    public double getValue(Object object) {
 *         return ((AnObjectWithAge) object).getAge();
 *    }
 * }
 * </pre>
 *
 * @see org.ascape.util.data.StatCollector
 * @author Miles Parker
 * @version 1.2
 * @history 1.2 7/7/99 (and before) changes to this and other stat collector classes to support manual stat collection and ease of use
 * @history 1.0.1 11/6/98 renamed all stats from form Count... and ConditionCount...
 * @since 1.0
 */
public abstract class StatCollectorCond extends StatCollector implements Conditional {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new StatCollectorCond.
     */
    public StatCollectorCond() {
    }

    /**
     * Constructs a new StatCollectorCond.
     * @param name the name of the stat collector.
     * @param autoCollect should the stat be collected automatically?
     */
    public StatCollectorCond(String name, boolean autoCollect) {
        this.name = name;
        this.autoCollect = autoCollect;
    }

    /**
     * Constructs a new StatCollectorCond. (Automatic by default.)
     * @param name the name of the stat collector.
     */
    public StatCollectorCond(String name) {
        this(name, true);
    }

    /**
     * Add value to stat calculation for this object if condition met.
     */
    public void addValueFor(Object object) {
        if (meetsCondition(object)) {
            addValue(getValue(object));
        }
    }
}
