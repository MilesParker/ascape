/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.sugarscape;

import org.ascape.view.nonvis.DataOutputView;

// Requires the directory C:\ResultData to exist

public class MultipleGAS_IV_h extends GAS_IV_h {

    /**
     * 
     */
    private static final long serialVersionUID = -6873656300781556732L;

    public void createViews() {
        DataOutputView dataView = new DataOutputView();
        sugarscape.addView(dataView);
        try {
            setStopPeriod(100);
        } catch (org.ascape.model.space.SpatialTemporalException e) {
            throw new RuntimeException("Internal Logic Error");
        }
        //dataView.getSweep().setRunsPer(2);
//        dataView.getDataSelection().setSelected("Count Age", false);
//        dataView.getDataSelection().setSelected("Minimun Age", false);
//        dataView.getDataSelection().setSelected("Maximum Age", false);
//        dataView.getDataSelection().setSelected("Average Age", false);
//        dataView.getDataSelection().setSelected("Count Vision", false);
//        dataView.getDataSelection().setSelected("Minimun Vision", false);
//        dataView.getDataSelection().setSelected("Maximum Vision", false);
//        dataView.getDataSelection().setSelected("Average Vision", false);
//        dataView.getDataSelection().setSelected("Count Sugar Metabolism", false);
//        dataView.getDataSelection().setSelected("Minimun Sugar Metabolism", false);
//        dataView.getDataSelection().setSelected("Maximum Sugar Metabolism", false);
//        dataView.getDataSelection().setSelected("Average Sugar Metabolism", false);
//        dataView.getDataSelection().setSelected("Count Spice Metabolism", false);
//        dataView.getDataSelection().setSelected("Minimun Spice Metabolism", false);
//        dataView.getDataSelection().setSelected("Maximum Spice Metabolism", false);
//        dataView.getDataSelection().setSelected("Average Spice Metabolism", false);
        setViewSelf(true);
    }
}
