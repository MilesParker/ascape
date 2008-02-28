/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.sugarscape;

import org.ascape.view.nonvis.DataOutputView;

public class MultipleGAS_II_2 extends GAS_II_2 {

    /**
     * 
     */
    private static final long serialVersionUID = -7719974817115633105L;

    public void createViews() {
        DataOutputView dataView = new DataOutputView();
        sugarscape.addView(dataView);
        try {
            setStopPeriod(100);
        } catch (org.ascape.model.space.SpatialTemporalException e) {
            throw new RuntimeException("Internal Logic Error");
        }
        ////dataView.getSweep().setRunsPer(2);
        setViewSelf(true);
    }
}
