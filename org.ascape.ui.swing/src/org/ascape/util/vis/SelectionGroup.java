/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.vis;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.ascape.util.VectorSelection;


/**
 * An class to enforce a radio group selection in a View's SelectionVector
 *
 * @author    Roger Critchlow
 * @version   2.9
 * @history   2.9 Moved into main Ascape.
 * @history   1.0 (Class version) 06/05/01 initial definition
 * @since     1.0
 */
public class SelectionGroup implements Observer, java.io.Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Describe variable <code>v</code> here.
     */
    private VectorSelection v;

    /**
     * Describe variable <code>s</code> here.
     */
    private Set s = new HashSet();

    /**
     * The object last selected in the group.
     */
    private Object o = null;

    /**
     * Creates a new <code>SelectionGroup</code> instance.
     *
     * @param v  a <code>VectorSelection</code> value
     */
    public SelectionGroup(VectorSelection v) {
        this.v = v;
        v.addObserver(this);
    }

    /**
     * Add an object to the radio select group.
     *
     * @param o  an <code>Object</code> value
     */
    public void add(Object o) {
        s.add(o);
    }

    /**
     * Update the radio group selection status
     *
     * @param obj  parameter
     * @param arg  parameter
     */
    public void update(Observable obj, Object arg) {
        /* five cases to deal with:
	 * 1) o == null, initial selection state,
	 * 1a) no member of the radio group is selected,
	 *	then o remains null.
	 * 1b) some member of the radio group is now selected,
	 *	then make it the new o.
	 * 2) o != null, there is a current selection
	 * 2a) no member of the radio group is selected,
	 *	then make o selected.
	 * 2b) member == o is selected,
	 *	then keep o selected.
	 * 2c) member != o is selected,
	 *	unselect o, make o == new selection.
	 */
        Object newo = null;
        Object oldo = null;
        for (Iterator i = s.iterator(); i.hasNext();) {
            Object n = i.next();
            if (v.isSelected(n)) {
                if (n != o) {
                    if (newo != null) {
                        System.out.println("More than two selected in SelectionGroup");
                    }
                    newo = n;
                } else {
                    oldo = n;
                }
            }
        }
        if (newo != null) {
            oldo = o;
            o = newo;
            if (oldo != null) {
                v.setSelected(oldo, false);
            }
        } else if (oldo == null && o != null) {
            v.setSelected(o, true);
        }
    }
}
