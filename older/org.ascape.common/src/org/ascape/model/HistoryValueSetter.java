/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model;

import org.ascape.util.ValueSetter;

/**
 * A data point that serves as a setter for a historical value.
 * The history calue setter stores a history of values from a defined
 * period to a defined period.
 *
 * @author Miles Parker
 * @version 1.0
 * @history 1.0 first in ascape 1.0.3
 * @since 1.0.3
 */
public abstract class HistoryValueSetter extends ValueSetter {

    private double[] values = new double[0];

    private int periodBegin;

    public void setPeriodRange(int periodBegin, int periodEnd) {
        this.periodBegin = periodBegin;
        values = new double[periodEnd - periodBegin + 1];
    }

    public void setInitialPeriods(int size) {
        values = new double[size];
    }

    public double getValueFor(int period) {
        return values[period - periodBegin];
    }

    public synchronized void setValueFor(int period, double value) {
        if ((period - periodBegin) < values.length) {
            values[period - periodBegin] = value;
        } else {
            double[] newValues = new double[period - periodBegin + 1];
            for (int i = 0; i < values.length; i++) {
                newValues[i] = values[i];
            }
            newValues[period - periodBegin] = value;
            values = newValues;
        }
    }

    public double getValue(Object object) {
        return values[((Agent) object).getRoot().getPeriod() - periodBegin];
    }
}

