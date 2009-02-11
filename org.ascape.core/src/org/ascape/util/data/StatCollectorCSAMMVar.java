/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.util.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores aggregate data for values obtained for a statistic, including
 * statistical variance. See StatCollectorCSA for an example.
 *
 * @see org.ascape.util.data.StatCollector
 * @see org.ascape.util.data.StatCollectorCond
 * @author Miles Parker
 * @version 1.2
 * @history 1.2 7/7/99 (and before) changes to this and other stat collector classes to support manual stat collection and ease of use
 * @history 1.0.1 11/6/98 renamed all stats from form Count... and ConditionCount...
 * @since 1.0
 */
public class StatCollectorCSAMMVar extends StatCollectorCSAMM {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * Vector storing all values so variance can be calculated.
     */
    private List<Double> data;

    /**
     * Constructs a new StatCollectorCSAMMVar.
     */
    public StatCollectorCSAMMVar() {
    }

    /**
     * Constructs a new StatCollectorCSAMMVar.
     * @param name the name of the stat collector.
     * @param autoCollect should the stat be collected automatically?
     */
    public StatCollectorCSAMMVar(String name, boolean autoCollect) {
        this.name = name;
        this.autoCollect = autoCollect;
    }

    /**
     * Constructs a new StatCollectorCSAMMVar. (Automatic by default.)
     * @param name the name of the stat collector.
     */
    public StatCollectorCSAMMVar(String name) {
        this(name, true);
    }

    /**
     * Sets all values statistics to base values (0 or max.)
     * Clears variance vector.
     */
    public void clear() {
        super.clear();
        data = new ArrayList<Double>();
    }

    /**
     * Add the value, incrementing count, adding sum, checking for minimum and maximum,
     * and adding to a record of data used for calculating variance and standard deviation.
     */
    public void addValue(double value) {
        super.addValue(value);
        data.add(new Double(value));
    }

    /**
     * Returns the statistical variance of the current data.
     */
    public double getVar() {
        double avg = getAvg();
        double variance = 0.0;
        for (Double value : data) {
            final double deviation = avg - value;
            variance += deviation * deviation;
        }
        variance /= getCount() - 1.0;
        return variance;
    }

    /**
     * Returns the standard deviation of the current data.
     */
    public double getStDev() {
        return Math.sqrt(getVar());
    }
}
