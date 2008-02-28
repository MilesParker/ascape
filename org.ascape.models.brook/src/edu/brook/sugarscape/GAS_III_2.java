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


public class GAS_III_2 extends GAS_SexBase {

    /**
     * 
     */
    private static final long serialVersionUID = -6453293880101053525L;

    public void createViews() {
        super.createViews();

        StatCollector[] stats = new StatCollector[6];
        stats[0] = new StatCollectorCond("Vision = 1") {
            /**
             * 
             */
            private static final long serialVersionUID = 1452135301328798642L;

            public boolean meetsCondition(Object object) {
                return (((SugarAgent) object).getVision() == 1);
            }
        };
        stats[1] = new StatCollectorCond("Vision = 2") {
            /**
             * 
             */
            private static final long serialVersionUID = 8874155818019066315L;

            public boolean meetsCondition(Object object) {
                return (((SugarAgent) object).getVision() == 2);
            }
        };
        stats[2] = new StatCollectorCond("Vision = 3") {
            /**
             * 
             */
            private static final long serialVersionUID = 1696357349659728887L;

            public boolean meetsCondition(Object object) {
                return (((SugarAgent) object).getVision() == 3);
            }
        };
        stats[3] = new StatCollectorCond("Vision = 4") {
            /**
             * 
             */
            private static final long serialVersionUID = 7089372720155589994L;

            public boolean meetsCondition(Object object) {
                return (((SugarAgent) object).getVision() == 4);
            }
        };
        stats[4] = new StatCollectorCond("Vision = 5") {
            /**
             * 
             */
            private static final long serialVersionUID = 2794643255803005425L;

            public boolean meetsCondition(Object object) {
                return (((SugarAgent) object).getVision() == 5);
            }
        };
        stats[5] = new StatCollectorCond("Vision = 6") {
            /**
             * 
             */
            private static final long serialVersionUID = 7646427930023594520L;

            public boolean meetsCondition(Object object) {
                return (((SugarAgent) object).getVision() == 6);
            }
        };
        agents.addStatCollectors(stats);

        //Create some colors to use throughout the views
        //White is just a placeholder, since there is no vision 0
        final Color[] colorForVision = {Color.white, new Color(0.0f, 0.0f, 1.0f), new Color(0.3f, 0.0f, 1.0f), new Color(0.6f, 0.0f, 1.0f),
                                        new Color(1.0f, 0.0f, 0.6f), new Color(1.0f, 0.0f, 0.3f), new Color(1.0f, 0.0f, 0.0f)};

        //Set the method that the view will use to determine agent color
        sugarView.setHostedAgentColorFeature(new ColorFeatureConcrete("Vision") {
            /**
             * 
             */
            private static final long serialVersionUID = -1973227148597116052L;

            public Color getColor(Object object) {
                return colorForVision[((SugarAgent) object).getVision()];
            }
        });

        //Create pie chart
        ChartView chart = new ChartView();
        chart.setChartType(ChartView.PIE);
        agents.addView(chart);
        for (int i = 1; i <= 6; i++) {
            chart.addSeries("Count Vision = " + i, colorForVision[i]);
        }

        //And time series
        ChartView ts = new ChartView();
        ts.setChartType(ChartView.TIME_SERIES);
        agents.addView(ts);
        for (int i = 1; i <= 6; i++) {
            ts.addSeries("Count Vision = " + i, colorForVision[i]);
        }
    }
}
