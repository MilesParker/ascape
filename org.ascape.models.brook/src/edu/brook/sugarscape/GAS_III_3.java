/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.sugarscape;

import java.awt.Color;

import org.ascape.util.data.StatCollector;
import org.ascape.util.data.StatCollectorCond;
import org.ascape.util.vis.ColorFeatureConcrete;
import org.ascape.view.vis.ChartView;


public class GAS_III_3 extends GAS_SexBase {

    /**
     * 
     */
    private static final long serialVersionUID = -1108945364026864952L;

    public void createViews() {
        super.createViews();

        StatCollector[] stats = new StatCollector[4];
        stats[0] = new StatCollectorCond() {
            /**
             * 
             */
            private static final long serialVersionUID = -4160771888469617582L;

            public boolean meetsCondition(Object object) {
                return (((SugarAgent) object).getSugarMetabolism() == 1);
            }

            public String getName() {
                return "Sugar Metabolism = 1";
            }
        };
        stats[1] = new StatCollectorCond() {
            /**
             * 
             */
            private static final long serialVersionUID = 6193160368787158151L;

            public boolean meetsCondition(Object object) {
                return (((SugarAgent) object).getSugarMetabolism() == 2);
            }

            public String getName() {
                return "Sugar Metabolism = 2";
            }
        };
        stats[2] = new StatCollectorCond() {
            /**
             * 
             */
            private static final long serialVersionUID = 7332637310050895866L;

            public boolean meetsCondition(Object object) {
                return (((SugarAgent) object).getSugarMetabolism() == 3);
            }

            public String getName() {
                return "Sugar Metabolism = 3";
            }
        };
        stats[3] = new StatCollectorCond() {
            /**
             * 
             */
            private static final long serialVersionUID = 4954887989494659855L;

            public boolean meetsCondition(Object object) {
                return (((SugarAgent) object).getSugarMetabolism() == 4);
            }

            public String getName() {
                return "Sugar Metabolism = 4";
            }
        };
        agents.addStatCollectors(stats);

        //Create some colors to use throughout the views
        //White is just a placeholder, since there is no metabolism 0
        final Color[] colorForMetabolism = {Color.white, new Color(0.0f, 0.0f, 1.0f), new Color(0.6f, 0.0f, 1.0f),
                                            new Color(1.0f, 0.0f, 0.6f), new Color(1.0f, 0.0f, 0.0f)};

        //Set the method that the view will use to determine agent color
        sugarView.setHostedAgentColorFeature(new ColorFeatureConcrete("Sugar Metabolism") {
            /**
             * 
             */
            private static final long serialVersionUID = -4800113817922755765L;

            public Color getColor(Object object) {
                return colorForMetabolism[((SugarAgent) object).getSugarMetabolism()];
            }
        });

        //Create pie chart
        ChartView chart = new ChartView();
        chart.setChartType(ChartView.PIE);
        agents.addView(chart);
        for (int i = 1; i <= 4; i++) {
            chart.addSeries("Count Sugar Metabolism = " + i, colorForMetabolism[i]);
        }

        //And time series
        ChartView ts = new ChartView();
        ts.setChartType(ChartView.TIME_SERIES);
        agents.addView(ts);
        for (int i = 1; i <= 4; i++) {
            ts.addSeries("Count Sugar Metabolism = " + i, colorForMetabolism[i]);
        }
    }
}
