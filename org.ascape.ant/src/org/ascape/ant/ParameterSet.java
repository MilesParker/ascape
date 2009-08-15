/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.ant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DynamicConfigurator;


/**
 * The Class ParameterSet.
 */
public class ParameterSet implements Serializable, DynamicConfigurator {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The attributes.
     */
    List attributes;
    
    /**
     * The value for name.
     */
    Map valueForName;

    /**
     * Instantiates a new parameter set.
     */
    public ParameterSet() {
        attributes = new ArrayList();
        valueForName = new HashMap();
    }

    /* (non-Javadoc)
     * @see org.apache.tools.ant.DynamicAttribute#setDynamicAttribute(java.lang.String, java.lang.String)
     */
    public void setDynamicAttribute(String name, String value) throws BuildException {
        attributes.add(name + "=" + value);
        valueForName.put(name, value);
    }

    /* (non-Javadoc)
     * @see org.apache.tools.ant.DynamicElement#createDynamicElement(java.lang.String)
     */
    public Object createDynamicElement(String name) throws BuildException {
        throw new BuildException("Unexpected Element for ParameterSet: " + name);
    }

    /**
     * Value for name.
     * 
     * @param name
     *            the name
     * @return the string
     */
    public String valueForName(String name) {
        return (String) valueForName.get(name);
    }

    /**
     * As args.
     * 
     * @return the string[]
     */
    public String[] asArgs() {
        String[] args = new String[attributes.size()];
        args = (String[]) attributes.toArray(args);
        return args;
    }
}
