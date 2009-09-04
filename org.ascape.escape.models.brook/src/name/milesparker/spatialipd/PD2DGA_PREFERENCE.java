/*
 * Copyright 2000 Miles T. Parker.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package name.milesparker.spatialipd;

import org.ascape.util.vis.ColorFeature;
import org.ascape.view.vis.ChartView;


public class PD2DGA_PREFERENCE extends PD2DGA {

    /**
     * 
     */
    private static final long serialVersionUID = -6127637096300287201L;

    public void createScape() {
        super.createScape();
        getAgents().getRules().setSelected(PLAY_RANDOM_NEIGHBOR_RULE, false);
        getAgents().getRules().setSelected("Play Preference", true);
    }

    public void createViews() {
        super.createViews();
        ChartView closenessChart = new ChartView();
        getAgents().addView(closenessChart);
        closenessChart.addSeries("Sum Cooperate Closest", ColorFeature.BLUE);
        closenessChart.addSeries("Sum Cooperate Farthest", ColorFeature.GREEN);
        closenessChart.addSeries("Sum Defect Closest", ColorFeature.RED);
        closenessChart.addSeries("Sum Defect Farthest", ColorFeature.DARK_YELLOW);
    }
}
