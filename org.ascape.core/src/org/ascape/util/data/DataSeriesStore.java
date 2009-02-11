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
 * A class which stores the results of measurements of data points. Comprehensive statistics are kept
 * for the series as a whole. Ordinarily, you should not have to work with this class
 * unless you are creating custom measurement types or collecting different kinds of
 * data. You do not usually need to subclass this class directly; a stat factory
 * creates the appropriate implementations.
 * Confused? A data series keeps track of the overall count, sum, average, minimum, maximum
 * for every piece of data collected, so it 'is-a' StatCollectorCSAMM. But it keeps the data stat for
 * that data as well, and it records just one measure of that stat, be it count, sum, average, minimum,
 * maximum, etc. so it 'has-a' DataPoint.
 * See StatCollectorCSA for an example.
 *
 */

public abstract class DataSeriesStore extends DataSeries {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * A series of double values.
     */
    private List<Double> series;

    /**
     * Constructs a new data series.
     */
    public DataSeriesStore() {
        clear();
    }

    /**
     * Clears the series of all values.
     */
    public void clear() {
        super.clear();
        series = new ArrayList<Double>();
    }

    /**
     * Returns a list holding the entire series.
     */
    public List<Double> toList() {
        return series;
    }

    /**
     * Adds a new value to the series.
     */
    public void addValue() {
        super.addValue();
        series.add(new Double(getValue()));
    }

    /**
     * Returns whether this DataSeries is collecting longitudinal data.
     * Check this to see if a DataSeries needs to be cast into a DataSeriesStore.
     * This method always returns true.
     */
    public boolean isCollecting() {
        return true;
    }

}

