/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.sweep;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

/**
 * A class faciliating iteration across several parameter dimensions,
 * supporting multiple runs per parameter set.
 * This class assumes that the total group size will almost always
 * be quite small (10 dimensions or so at most), so the implementation
 * uses a number of large O methods.
 *
 * @author Miles Parker
 * @version 1.9
 * @since 1.9
 */
public class SweepGroup implements Sweepable, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Vector sweeps = new Vector();

    private int depth;

    private boolean hasNext;

    private int runsPer = 1;

    private int runCount;

    /**
     * Adds the 'sweepable' (sweep iterator) to the group.
     */
    public void addMember(Sweepable sweep) {
        sweeps.addElement(sweep);
        if (!hasNext && sweep.hasNext()) {
            hasNext = true;
        }
        depth = 0;
    }

    /**
     * Returns the 'sweepable' (sweep iterator) at the specified position.
     */
    public Sweepable getMember(int pos) {
        return (Sweepable) sweeps.elementAt(pos);
    }

    /**
     * Sweepables can be SweepLinks, which means that more than one sweep dimension
     * might exist for a given member. This method allows you to access the 'unrolled'
     * memebers.
     */
    public Sweepable getUnlinkedMember(int pos) {
        int current = -1;
        Enumeration e = sweeps.elements();
        while (e.hasMoreElements()) {
            Sweepable sweep = (Sweepable) e.nextElement();
            if (!(sweep instanceof SweepLink)) {
                current++;
                if (current == pos) {
                    return sweep;
                }
            } else {
                Enumeration eLink = ((SweepLink) sweep).elements();
                while (eLink.hasMoreElements()) {
                    Sweepable linkElement = (Sweepable) eLink.nextElement();
                    current++;
                    if (current == pos) {
                        return linkElement;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Sets the entire swep group back to its initial state. The next
     * call to next will set the first sweep item.
     */
    public void reset() {
        Enumeration e = sweeps.elements();
        while (e.hasMoreElements()) {
            Sweepable sweep = (Sweepable) e.nextElement();
            sweep.reset();
            if (!hasNext && sweep.hasNext()) {
                hasNext = true;
            }
        }
        depth = 0;
    }

    /**
     * Sets the runs per, or number of runs per each sweep setting.
     */
    public int getRunsPer() {
        return runsPer;
    }

    /**
     * Sets the runs per, or number of runs per each sweep setting.
     */
    public int getRunCount() {
        return runCount;
    }

    /**
     * Returns the runs per, or number of runs per each sweep setting.
     */
    public void setRunsPer(int runsPer) {
        this.runsPer = runsPer;
    }

    /**
     * Are there more sweep settings in this group?
     */
    public boolean hasNext() {
        if (sweeps.size() == 0) {
            return false;
        }
        if (runCount < runsPer) {
            return true;
        }
        Enumeration e = sweeps.elements();
        while (e.hasMoreElements()) {
            if (((Sweepable) e.nextElement()).hasNext()) {
                return true;
            }
        }
        return false;
    }

    private Object nextInternal() {
        Sweepable currentSweep = (Sweepable) sweeps.elementAt(depth);
        if (currentSweep.hasNext()) {
            if (depth < sweeps.size() - 1) {
                depth++;
                nextInternal();
            }
            return currentSweep.next();
        } else {
            currentSweep.reset();
            depth--;
            return nextInternal();
        }
    }

    /**
     * Iterates to the next sweep setting in this group.
     */
    public Object next() {
        if (runCount == runsPer) {
            runCount = 0;
        }
        runCount++;
        if (runCount == 1) {
            return nextInternal();
        } else {
            return "-";
        }
    }

    /**
     * Returns the 'unrolled' size, that is counting all of the dimensions in each link.
     */
    public int getUnlinkedSize() {
        int size = 0;
        Enumeration e = sweeps.elements();
        while (e.hasMoreElements()) {
            Sweepable sweep = (Sweepable) e.nextElement();
            if (!(sweep instanceof SweepLink)) {
                size++;
            } else {
                size += ((SweepLink) sweep).getSize();
            }
        }
        return size;
    }

    /**
     * Counts the number of members; memebers may be dimensions, links, or other
     * implementoprs of the sweepable interface.
     */
    public int getSize() {
        return sweeps.size();
    }
}
