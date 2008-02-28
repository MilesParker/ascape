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
public class StatCollectorCSA extends StatCollector {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    protected double sum;

    /**
     * Constructs a new StatCollectorCSA.
     */
    public StatCollectorCSA() {
        clear();
    }

    /**
     * Constructs a new StatCollectorCSA.
     * @param name the name of the stat collector.
     * @param autoCollect should the stat be collected automatically?
     */
    public StatCollectorCSA(String name, boolean autoCollect) {
        this();
        this.name = name;
        this.autoCollect = autoCollect;
    }

    /**
     * Constructs a new StatCollectorCSA. (Automatic by default.)
     * @param name the name of the stat collector.
     */
    public StatCollectorCSA(String name) {
        this(name, true);
    }

    /**
     * Sets all values statistics to base values (0 or max.)
     */
    public void clear() {
        super.clear();
        sum = 0.0;
    }

    /**
     *  Add the value, incrementing count and adding sum.
     */
    public void addValue(double value) {
        //We don't call superclass simply for performace reasons
        count++;
        sum += value;
    }

    /**
     * Returns the current sum of all values added.
     * @depreacted use "getSum()"
     */
    public double getTotal() {
        return sum;
    }

    /**
     * Returns the current sum of all values added.
     */
    public double getSum() {
        return sum;
    }

    /**
     * Returns the current average of all values added.
     */
    public double getAvg() {
        if (count != 0) {
            return sum / (double) count;
        } else {
            return 0.0;
        }
    }
}
