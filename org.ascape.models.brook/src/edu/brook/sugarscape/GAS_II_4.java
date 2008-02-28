/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.sugarscape;

import java.awt.Color;

import org.ascape.util.data.StatCollectorCSAMM;
import org.ascape.util.data.StatCollectorCond;
import org.ascape.view.vis.ChartView;


public class GAS_II_4 extends GAS_III_2 {

    /**
     * 
     */
    private static final long serialVersionUID = 8164788204620699378L;

    public void createViews() {
        super.createViews();

        final StatCollectorCSAMM wealth = new StatCollectorCSAMM() {
            /**
             * 
             */
            private static final long serialVersionUID = 4322388161358953355L;

            public double getValue(Object o) {
                return ((SugarAgent) o).getSugar().getStock();
            }

            public String getName() {
                return "Wealth";
            }
        };
        agents.addStatCollector(wealth);

        class DecileCollector extends StatCollectorCond {

            /**
             * 
             */
            private static final long serialVersionUID = -8146962501703395280L;
            int decile;

            public DecileCollector(int decile) {
                this.decile = decile;
            }

            public boolean meetsCondition(Object o) {
                return ((((SugarAgent) o).getSugar().getStock() > ((decile - 1) * 0.1 * wealth.getMax())) && (((SugarAgent) o).getSugar().getStock() < (decile * 0.1 * wealth.getMax())));
            }

            public final boolean isPhase2() {
                return true;
            }

            public String getName() {
                return "Decile " + Integer.toString(decile);
            }
        }

        StatCollectorCond[] decileStats = new StatCollectorCond[10];
        for (int n = 1; n <= 10; n++) {
            decileStats[n - 1] = new DecileCollector(n);
        }
        agents.addStatCollectors(decileStats);

        //Create pie chart
        ChartView chart = new ChartView();
        chart.setChartType(ChartView.HISTOGRAM);
        agents.addView(chart);
        for (int i = 1; i <= 10; i++) {
            chart.addSeries("Count Decile " + i, Color.red);
        }
    }
}
