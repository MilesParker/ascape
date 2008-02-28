/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.aa;

import org.ascape.view.nonvis.DataOutputView;

public class LHVMultiple extends LHV {

    /**
     * 
     */
    private static final long serialVersionUID = -8087699266408541306L;

    public void createViews() {
        super.createViews();
        DataOutputView dataView = new DataOutputView();
        valley.addView(dataView);
        /*dataView.getSweep().setRunsPer(3);
        try {
        valley.setStopPeriod(5);
        }
        catch (SpatialTemporalException e) {
        }
        setMinDeathAge(20);
        setMaxDeathAge(40);
        //We don't need to export historic data, and we want to match current C++ output
        dataView.getDataSelection().setSelected("Count Historic Households", false);
        dataView.getDataSelection().setSelected("Count Historic Households", false);
        dataView.getDataSelection().setSelected("Sum Historic Households", false);
        dataView.getDataSelection().setSelected("Average Historic Households", false);
        setViewSelf(true);*/
    }
}
