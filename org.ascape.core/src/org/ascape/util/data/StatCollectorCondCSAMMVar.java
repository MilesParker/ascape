/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.data;

import org.ascape.util.Conditional;

/**
 * Stores conditional aggregate data for values conditionally obtained for a statistic,
 * including running sum, average, minimum and maximum. See StatCollectorCSA for an example.
 *
 * @see org.ascape.util.data.StatCollector
 * @see org.ascape.util.data.StatCollectorCond
 * @author Miles Parker
 * @version 1.2
 * @history 1.2 7/7/99 (and before) changes to this and other stat collector classes to support manual stat collection and ease of use
 * @history 1.0.1 11/6/98 renamed all stats from form Count... and ConditionCount...
 * @since 1.0
 */
public abstract class StatCollectorCondCSAMMVar extends StatCollectorCSAMMVar implements Conditional {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new StatCollectorCondCSAMMVar.
     */
    public StatCollectorCondCSAMMVar() {
    }

    /**
     * Constructs a new StatCollectorCondCSAMMVar.
     * @param name the name of the stat collector.
     * @param autoCollect should the stat be collected automatically?
     */
    public StatCollectorCondCSAMMVar(String name, boolean autoCollect) {
        this.name = name;
        this.autoCollect = autoCollect;
    }

    /**
     * Constructs a new StatCollectorCondCSAMMVar. (Automatic by default.)
     * @param name the name of the stat collector.
     */
    public StatCollectorCondCSAMMVar(String name) {
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
