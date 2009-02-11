/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.util.data;


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
 * @see org.ascape.util.data.StatCollector#createDataSeries
 * @see org.ascape.util.data.StatCollectorCSA
 * @author Miles Parker
 * @version 1.0.2
 * @history 1.0.2 3/8/99 renamed from ValueSeries for clarity
 * @history 1.0.1 11/6/98 document, minor changes (changed point from StatCollector back to DataPoint for flexibility)
 * @since 1.0
 */
public abstract class DataSeries extends StatCollectorCSAMM {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * The value point this series is tracking.
     */
    protected DataPoint point;

    /**
     * Constructs a new data series.
     */
    public DataSeries() {
        clear();
    }

    /**
     * Clears the series of all values.
     */
    public void clear() {
        super.clear();
        //series = new Vector();
    }

    /**
     * Adds a new value to the series.
     */
    public void addValue() {
        super.addValue(getValue());
        // moved to DataSeriesStore
        //   series.addElement(new Double(getValue()));
    }

    /**
     * Overridden to return the current value of the measure used for this series.
     * For example, if this series is intended to track minimums, this method
     * might return <code>((StatCollectorCSAMM) point).getMin()</code>
     * @see org.ascape.util.data.StatCollector#createDataSeries
     */
    public abstract double getValue();

    /**
     * Overrides the superclasses abstract method, to call the getValue
     * method of this class. Object is ignored, since this data series
     * is interested in one and only one object; its value point.
     * @param object normally the object we're interested in, ignored here
     */
    public final double getValue(Object object) {
        return getValue();
    }

    /**
     * Overridden to return a (english for now) name for the type of measure
     * being recorded; for example, "Minimum."
     */
    public abstract String getMeasureName();

    /**
     * Returns the name of this data series. By default, the measure name and
     * the value being measured; for example, "Minimum Age."
     */
    public String getName() {
        return getMeasureName() + " " + point.getName();
    }

    /**
     * Returns the data point that this series is recording.
     */
    public DataPoint getDataPoint() {
        return point;
    }

    /**
     * Sets the data point that this series shoudl record.
     */
    public void setDataPoint(DataPoint point) {
        this.point = point;
    }

    /**
     * Returns whether this DataSeries is collecting longitudinal data.
     * Check this to see if a DataSeries needs to be cast into a DataSeriesStore.
     * This method always returns false.
     */
    public boolean isCollecting() {
        return false;
    }
}
