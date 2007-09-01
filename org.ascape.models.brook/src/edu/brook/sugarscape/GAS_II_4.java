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

import org.ascape.util.data.StatCollectorCSAMM;
import org.ascape.util.data.StatCollectorCond;
import org.ascape.view.vis.ChartView;


public class GAS_II_4 extends GAS_III_2 {

    public void createViews() {
        super.createViews();

        final StatCollectorCSAMM wealth = new StatCollectorCSAMM() {
            public double getValue(Object o) {
                return ((SugarAgent) o).getSugar().getStock();
            }

            public String getName() {
                return "Wealth";
            }
        };
        agents.addStatCollector(wealth);

        class DecileCollector extends StatCollectorCond {

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
