/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.data;


/**
 * Stores aggregate data for values obtained for a statistic, including
 * running sum and average.
 *
 * @see org.ascape.util.data.StatCollector
 * @see org.ascape.util.data.StatCollectorCond
 * @author Miles Parker
 * @version 1.2
 * @history 1.2 7/7/99 (and before) changes to this and other stat collector classes to support manual stat collection and ease of use
 * @history 1.0.1 11/6/98 renamed all stats from form Count... and ConditionCount...
 * @since 1.0
 */
public class StatCollectorCalculated extends StatCollectorCSA {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new StatCollectorCSA.
     */
    public StatCollectorCalculated() {
    }

    /**
     * Constructs a new StatCollectorCSA. (Automatic by default.)
     * @param name the name of the stat collector.
     */
    public StatCollectorCalculated(String name) {
        super(name);
    }

    /**
     * Returns the current calculate value. This is just an alias for Sum, which is the same thing, as
     * a calculated value is only added once.
     */
    public double getCalculatedValue() {
        return sum;
    }

    /**
     * Returns true, values are collected automatically for this class by definition.
     */
    public final boolean isCalculated() {
        return true;
    }
}
