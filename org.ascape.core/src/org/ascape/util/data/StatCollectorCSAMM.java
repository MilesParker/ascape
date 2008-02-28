/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.data;


/**
 * Stores aggregate data for values obtained for a statistic, including
 * running sum, average, minimum and maximum. See StatCollectorCSA for an example.
 *
 * @see org.ascape.util.data.StatCollector
 * @see org.ascape.util.data.StatCollectorCond
 * @author Miles Parker
 * @version 1.2
 * @history 1.2 7/7/99 (and before) changes to this and other stat collector classes to support manual stat collection and ease of use
 * @history 1.2 6/18/99 fixed min implementation so that min returns 0.0 (not max integer!) when no agents are counted
 * @history 1.0.2 5/3/99 fixed bug in min implementation
 * @history 1.0.1 11/6/98 renamed all stats from form Count... and ConditionCount...
 * @since 1.0
 */
public class StatCollectorCSAMM extends StatCollectorCSA {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The current minimum for the stat.
     */
    private double min;

    /**
     * The current maximum for the stat.
     */
    private double max;

    /**
     * Constructs a new StatCollectorCSAMM.
     */
    public StatCollectorCSAMM() {
    }

    /**
     * Constructs a new StatCollectorCSAMM.
     * @param name the name of the stat collector.
     * @param autoCollect should the stat be collected automatically?
     */
    public StatCollectorCSAMM(String name, boolean autoCollect) {
        this.name = name;
        this.autoCollect = autoCollect;
        clear();
    }

    /**
     * Constructs a new StatCollectorCSAMM. (Automatic by default.)
     * @param name the name of the stat collector.
     */
    public StatCollectorCSAMM(String name) {
        this(name, true);
    }

    /**
     * Sets all values statistics to base values (0 or max.)
     */
    public void clear() {
        super.clear();
        min = Double.MAX_VALUE;
        max = -Double.MAX_VALUE;
    }

    /**
     *  Add the value, incrementing count, adding sum, and checking for minimum and maximum.
     */
    public void addValue(double value) {
        //don't call superclass for better performance
        count++;
        sum += value;
        if (value > max) {
            max = value;
        }
        if (value < min) {
            min = value;
        }
    }

    /**
     * Returns the minimum value added.
     * Returns 0.0 if no value has been added yet.
     */
    public double getMin() {
        //As soon as we have a value, we will use it.
        return (count > 0 ? min : 0.0);
    }

    /**
     * Returns the maximum value added.
     * Returns 0.0 if no value has been added yet.
     */
    public double getMax() {
        return (count > 0 ? max : 0.0);
    }
}
