/*
 * This software and documentation is subject to, and may be only used in accordance with, the terms and conditions of
 * the license agreement described or set forth in the file titled License.txt.  This software and documentation
 * contains information that is confidential and proprietary to NuTech Solutions, Inc.
 *
 *             Copyright (c) 2000-2004
 *             NuTech Solutions, Inc.
 * 
 * Portions of the software are licensed from The Brookings Institution
 *             Copyright (c) 1998-2000
 *            The Brookings Institution
 *           All rights reserved worldwide.
 *
 *
 * U.S. GOVERNMENT END USERS. The software is a "commercial item," as that term is defined in 48 C.F.R. 2.101,
 * consisting of "commercial computer software" and "commercial computer software documentation," as such terms are
 * used in 48 C.F.R. 12.212. Consistent with 48 C.F.R. 12.212 and 48 C.F.R. 227.7202-1 through 227.7202-4, and other
 * relevant sections of the Code of Federal Regulations, as applicable all U.S. Government End Users acquire the
 * software with only those rights set forth in the license agreement. This software includes trade secrets that
 * are exempt from disclosure under the Freedom of Information Act.  
 *
 * 
 * _Sugarscape_
 * See _Growing Artificial Societies: Social Science from the Ground Up_
 * Brookings Institution Press, The MIT Press
 * Joshua Epstein
 * jepstein@brook.edu
 * Robert Axtell
 * raxtell@brook.edu
 *
 * Miles Parker
 * mparker@brook.edu

 * http://www.brook.edu/es/dynamics/models/ascape
 * The Brookings Institution
 * Washington, D.C.
 */

package edu.brook.sugarscape;

import java.awt.Color;

import org.ascape.util.data.StatCollector;
import org.ascape.util.data.StatCollectorCond;
import org.ascape.util.vis.ColorFeatureConcrete;
import org.ascape.view.vis.ChartView;


public class GAS_III_2 extends GAS_SexBase {

    public void createViews() {
        super.createViews();

        StatCollector[] stats = new StatCollector[6];
        stats[0] = new StatCollectorCond("Vision = 1") {
            public boolean meetsCondition(Object object) {
                return (((SugarAgent) object).getVision() == 1);
            }
        };
        stats[1] = new StatCollectorCond("Vision = 2") {
            public boolean meetsCondition(Object object) {
                return (((SugarAgent) object).getVision() == 2);
            }
        };
        stats[2] = new StatCollectorCond("Vision = 3") {
            public boolean meetsCondition(Object object) {
                return (((SugarAgent) object).getVision() == 3);
            }
        };
        stats[3] = new StatCollectorCond("Vision = 4") {
            public boolean meetsCondition(Object object) {
                return (((SugarAgent) object).getVision() == 4);
            }
        };
        stats[4] = new StatCollectorCond("Vision = 5") {
            public boolean meetsCondition(Object object) {
                return (((SugarAgent) object).getVision() == 5);
            }
        };
        stats[5] = new StatCollectorCond("Vision = 6") {
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
