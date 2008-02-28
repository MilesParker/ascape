/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.ant;


import java.io.Serializable;

import org.ascape.model.Scape;
import org.ascape.model.event.ScapeListener;


/**
 * The Class AntViewElement.
 */
public class AntViewElement implements Serializable, ViewElement {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * The view.
     */
    private ScapeListener view;

    /**
     * Sets the view name.
     * 
     * @param name
     *            the new view name
     */
    public void setViewName(String name) {
        Class c;
        try {
            c = Class.forName(name);
        } catch (ClassNotFoundException e) {
            try {
                c = Class.forName("org.ascape.view.vis." + name);
            } catch (ClassNotFoundException e2) {
                try {
                    c = Class.forName("org.ascape.view.nonvis." + name);
                } catch (ClassNotFoundException e3) {
                    throw new RuntimeException("The class \"" + name + "\" could not be found.");
                }
            }
        }
        try {
            view = (ScapeListener) c.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException("Error instantiating class for view name: " + name, e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error accessing class for view name: " + name, e);
        }
    }

    /* (non-Javadoc)
     * @see org.ascape.ant.ViewElement#addToScape(org.ascape.model.Scape)
     */
    public void addToScape(Scape scape) {
        scape.addView(view);
    }

    /**
     * Gets the view.
     * 
     * @return the view
     */
    public ScapeListener getView() {
        return view;
    }
}
