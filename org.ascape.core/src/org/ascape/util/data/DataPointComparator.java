/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.data;

import java.io.Serializable;
import java.util.Comparator;

/**
 * An interface for a class comparing objects using a data point.
 * The datapoint can be set, or this getValue method may be overridden and the class used as the DataPoint itself.
 *
 * @see org.ascape.util.data.DataPoint
 * @author Miles Parker
 * @version 2.0
 * @history 2.0 6/6/2001 first in
 * @since 2.0
 */
public class DataPointComparator implements Comparator, DataPoint, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private DataPoint dataPoint;

    /**
     * Constructs a new data point comparator.
     */
    public DataPointComparator() {
        this.dataPoint = this;
    }

    /**
     * Constructs a new data point using the supplied data point as the interpretor of any supplied object's value.
     */
    public DataPointComparator(DataPoint dataPoint) {
        this.dataPoint = dataPoint;
    }

    /**
     * Compares two object using the data point to interpret their values.
     * Note that DataPoint.equals is used to determine the 0 (equivalence) case -- _not_ the default doubles compare method -- so it is
     * possible that doubles that are very close in value will not maintain consistent order.
     * We do this so that searches for specific double values will produce expected results, but if you want to ensure
     * a guranteed sorting order, override this method using the Double.compare method.
     */
    public int compare(Object o1, Object o2) {
        if (DataPointConcrete.equals(dataPoint, o1, o2)) {
            return 0;
        } else {
            //We want to avoid construction cost for Double in the common case
            if (dataPoint.getValue(o1) < dataPoint.getValue(o2)) {
                return -1;
            } else if (dataPoint.getValue(o1) > dataPoint.getValue(o2)) {
                return 1;
            } else {
                //In uncommon cases, we'll use the Double class interpreation for NAN, etc..
                return (new Double(dataPoint.getValue(o1))).compareTo(new Double(dataPoint.getValue(o1)));
            }
        }
    }

    /**
     * Returns true if the compared comparator uses the same data point.
     */
    public boolean equals(Object o) {
        return ((o instanceof DataPointComparator) && (((DataPointComparator) o).dataPoint == this.dataPoint));
    }

    /**
     * Returns the value of a given data point from a given object.
     * @param object the object to extract the value from.
     */
    public double getValue(Object object) {
        return dataPoint.getValue(object);
    }

    /**
     * Sets the data point to be sued by this comparator.
     * (By default, the data point is the comparator itself.)
     */
    public void setDataPoint(DataPoint dataPoint) {
        this.dataPoint = dataPoint;
    }

    /**
     * Returns the name of this data point comparator.
     */
    public String getName() {
        return dataPoint.getName() + " Comparator";
    }

    /**
     * Returns the name of this data point comparator.
     */
    public String toString() {
        return getName();
    }
}
