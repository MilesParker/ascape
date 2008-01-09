/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.sweep;

import java.util.Enumeration;
import java.util.Vector;

/**
 * A sweep link allows two or more sweep dimensions to be iterated together.
 * When next is called, all memebers of the sweep link are iterated.
 * hasNext returns false when all memebers no longer return true for haveNext.
 * So fo instance, the link composed of members with values {1.0, 1.1, 1.2} and {.1, .2, .3, .4}
 * would set values to {(1.0, .1), (1.1, .2), (1.2, .3), (1.2, .4)}.
 * SweepLinks may be nested, of course.
 *
 * @author Miles Parker
 * @version 1.9
 * @since 1.9
 */
public class SweepLink implements Sweepable {

    private Vector sweeps = new Vector();

    /**
     * Adds the sweepable item to be iterated with the link.
     */
    public void addMember(Sweepable sweep) {
        sweeps.addElement(sweep);
        sweep.reset();
    }

    /**
     * Returns the sweepable at the provided location.
     */
    public Sweepable getMember(int pos) {
        return (Sweepable) sweeps.elementAt(pos);
    }

    /**
     * Resets the memebrs to their initial states.
     */
    public void reset() {
        Enumeration e = sweeps.elements();
        while (e.hasMoreElements()) {
            Sweepable sweep = (Sweepable) e.nextElement();
            sweep.reset();
        }
    }

    /**
     * Returns true if any memebrs still have next states.
     */
    public boolean hasNext() {
        Enumeration e = sweeps.elements();
        while (e.hasMoreElements()) {
            if (((Sweepable) e.nextElement()).hasNext()) {
                //if any sweepables still have more elements, we continue
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the memebers as an enumeration.
     */
    public Enumeration elements() {
        return sweeps.elements();
    }

    /**
     * Sets all of the linked member's next state.
     */
    public Object next() {
        Enumeration e = sweeps.elements();
        while (e.hasMoreElements()) {
            Sweepable sweep = (Sweepable) e.nextElement();
            if (sweep.hasNext()) {
                sweep.next();
            }
        }
        return null;
    }

    /**
     * Returns the size of the links.
     */
    public int getSize() {
        return sweeps.size();
    }
}
