/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.aa;

//Temporary for JDK 1.1 compatibility
//import com.sun.java.util.collections.*;

import org.ascape.util.vis.ColorFeature;
import org.ascape.view.vis.ChartView;

/**
 * This class involves very preliminary exploration.
 */
public class LHVClan extends LHVDisaggregate {

    /**
     * 
     */
    private static final long serialVersionUID = -5102792644057845317L;

    public void createScape() {
        super.createScape();
        people.setPrototypeAgent(new PersonClan());
        minFertility = .33;
        // harvestAdjustment = .75;
        // harvestAdjustment = .80;

        // setRandomSeed(936050508840L);
        try {
            setStopPeriod(1400);
        } catch (org.ascape.model.space.SpatialTemporalException e) {
        }
    }

    public void createViews() {
        ChartView chart = new ChartView();
        valley.addView(chart);
        // And add some of the stat series we've just created to it
        chart.addSeries("Count Orange Clan", ColorFeature.DARK_YELLOW);
        chart.addSeries("Count Red Clan", ColorFeature.RED);
        chart.addSeries("Count Green Clan", ColorFeature.GREEN);
        chart.addSeries("Count Yellow Clan", ColorFeature.YELLOW);
        chart.addSeries("Count Pink Clan", ColorFeature.DARK_RED);
        chart.addSeries("Count Cyan Clan", ColorFeature.CYAN);

        super.createViews();
        view.getDrawSelection().setSelected("Clan", true);
    }
}
