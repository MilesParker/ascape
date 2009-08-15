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
import java.util.TooManyListenersException;

import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;
import org.ascape.view.nonvis.SweepControlView;


/**
 * The Class SweepViewElement.
 */
public class SweepViewElement implements Serializable, ViewElement {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The view.
     */
    SweepControlView view;

    /**
     * The dimension elements.
     */
    List dimensionElements;

    /**
     * Instantiates a new sweep view element.
     */
    public SweepViewElement() {
        view = new SweepControlView("SweepControlView (Generated)") {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            public void scapeAdded(ScapeEvent scapeEvent) throws TooManyListenersException {
                super.scapeAdded(scapeEvent);
                for (Iterator iterator = dimensionElements.iterator(); iterator.hasNext();) {
                    SweepDimensionElement element = (SweepDimensionElement) iterator.next();
                    getSweepGroup().addMember(element.asDimension(scape));
                }
            }
        };
        dimensionElements = new ArrayList();
    }

    /**
     * Adds the sweep dimension.
     * 
     * @param dimension
     *            the dimension
     */
    public void addSweepDimension(SweepDimensionElement dimension) {
        dimensionElements.add(dimension);
    }

    /* (non-Javadoc)
     * @see org.ascape.ant.ViewElement#addToScape(org.ascape.model.Scape)
     */
    public void addToScape(Scape scape) {
        scape.addView(view);
    }

    /**
     * Sets the runs per setting.
     * 
     * @param runs
     *            the new runs per setting
     */
    public void setRunsPerSetting(int runs) {
        view.getSweepGroup().setRunsPer(runs);
    }
}
