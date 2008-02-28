/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.ant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DynamicConfigurator;
import org.ascape.util.VectorSelection;



/**
 * The Class SelectionSet.
 */
public class SelectionSet implements Serializable, DynamicConfigurator {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The clear all.
     */
    boolean clearAll = false;

    /**
     * The select all.
     */
    boolean selectAll = false;

    /**
     * The active rules.
     */
    List activeRules;

    /**
     * The inactive rules.
     */
    List inactiveRules;

    /**
     * Instantiates a new selection set.
     */
    public SelectionSet() {
        activeRules = new ArrayList();
        inactiveRules = new ArrayList();
    }

    /* (non-Javadoc)
     * @see org.apache.tools.ant.DynamicAttribute#setDynamicAttribute(java.lang.String, java.lang.String)
     */
    public void setDynamicAttribute(String s, String s1) throws BuildException {
        if (Boolean.valueOf(s1).booleanValue()) {
            activeRules.add(s);
        } else {
            inactiveRules.add(s);
        }
    }

    /* (non-Javadoc)
     * @see org.apache.tools.ant.DynamicElement#createDynamicElement(java.lang.String)
     */
    public Object createDynamicElement(String s) throws BuildException {
        throw new BuildException("Unexpected Element for ParameterSet: " + s);
    }

    /**
     * Checks if is clear all.
     * 
     * @return true, if is clear all
     */
    public boolean isClearAll() {
        return clearAll;
    }

    /**
     * Sets the clear all.
     * 
     * @param clearAll
     *            the new clear all
     */
    public void setClearAll(boolean clearAll) {
        this.clearAll = clearAll;
    }

    /**
     * Checks if is select all.
     * 
     * @return true, if is select all
     */
    public boolean isSelectAll() {
        return selectAll;
    }

    /**
     * Sets the select all.
     * 
     * @param selectAll
     *            the new select all
     */
    public void setSelectAll(boolean selectAll) {
        this.selectAll = selectAll;
    }

    /**
     * Gets the active rules.
     * 
     * @return the active rules
     */
    public List getActiveRules() {
        return activeRules;
    }

    /**
     * Gets the inactive rules.
     * 
     * @return the inactive rules
     */
    public List getInactiveRules() {
        return inactiveRules;
    }

    /**
     * Apply.
     * 
     * @param selection
     *            the selection
     */
    public void apply(VectorSelection selection) {
        if (isClearAll()) {
            selection.clearSelection();
        }
        if (isSelectAll()) {
            selection.selectAll();
        }
        for (Iterator iterator = getActiveRules().iterator(); iterator.hasNext();) {
            selection.setSelected(((String) iterator.next()).replaceAll("_", " "), true);
        }
        for (Iterator iterator = getInactiveRules().iterator(); iterator.hasNext();) {
            selection.setSelected(((String) iterator.next()).replaceAll("_", " "), false);
        }
    }
}
