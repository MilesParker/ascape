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
 * _Emergence of classes_
 * See _The Emergence of Economic Classes in an Agent-Based Bargaining Model_
 * Joshua Epstein
 * jepstein@brook.edu
 * Robert Axtell
 * raxtell@brook.edu
 * Peyton Young
 * pyoung@brook.edu
 *
 * Implementation:
 * Miles Parker
 * mparker@brook.edu
 * The Brookings Institution
 * Washington, D.C.
 */

package edu.brook.classes;

import org.ascape.util.vis.SimplexFeature;
import org.ascape.view.vis.ComponentView;
import org.ascape.view.vis.SimplexView;

public class BargainingModelTwo extends BargainingModelBase {

    public void createScape() {
        super.createScape();
        agents.setPrototypeAgent(new BargainerTagged());
    }

    private SimplexView interMemoryView;

    public void createViews() {
        super.createViews();
        interMemoryView = new SimplexView("Intergroup Memory", Strategy.LOW_STRATEGY.getDemand());
        interMemoryView.setSimplexFeature(new SimplexFeature("Memory") {
            public float getAxis1Value(Object object) {
                return (float) ((BargainerTagged) object).countInterHigh() / (float) ((BargainerTagged) object).countInter();
            }

            public String getAxis1Name() {
                return "high";
            }

            public float getAxis2Value(Object object) {
                return (float) ((BargainerTagged) object).countInterMedium() / (float) ((BargainerTagged) object).countInter();
            }

            public String getAxis2Name() {
                return "medium";
            }

            public float getAxis3Value(Object object) {
                return (float) ((BargainerTagged) object).countInterLow() / (float) ((BargainerTagged) object).countInter();
            }

            public String getAxis3Name() {
                return "low";
            }
        });

        ComponentView[] views = new ComponentView[2];
        //ViewFrameBridge.setMultiViewMode(ViewFrameBridge.TABBED_MULTIVIEW_MODE);
        views[0] = intraMemoryView;
        views[1] = interMemoryView;
        //views[2] = riskView;
        agents.addViews(views);
        /*final StatCollector[] stats = {
            new StatCollector("All"),
            new StatCollectorCond("Low") {
public boolean meetsCondition(Object object) {
return (((Bargainer) object).getLastStrategy() == Strategy.LOW_STRATEGY);
}
},
            new StatCollectorCond("Medium") {
public boolean meetsCondition(Object object) {
return (((Bargainer) object).getLastStrategy() == Strategy.MEDIUM_STRATEGY);
}
},
            new StatCollectorCond("Hi") {
public boolean meetsCondition(Object object) {
return (((Bargainer) object).getLastStrategy() == Strategy.HIGH_STRATEGY);
}
}
};
        agents.addStatCollectors(stats);

        //Create a new chart
        ChartView chart = new ChartView();
        //Add the chart view
        agents.addView(chart);
        chart.addSeries("Count Low", Color.blue);
        chart.addSeries("Count Medium", Color.green);
        chart.addSeries("Count Hi", Color.red);*/
    }

    public float getRandomStrategyProbability() {
        return randomStrategyProbability;
    }

    public void setRandomStrategyProbability(float randomStrategyProbability) {
        this.randomStrategyProbability = randomStrategyProbability;
    }

    public int getMinimumMemorySize() {
        return minMemorySize;
    }

    public void setMinimumMemorySize(int minMemorySize) {
        this.minMemorySize = minMemorySize;
    }

    public int getMaximumMemorySize() {
        return maxMemorySize;
    }

    public void setMaximumMemorySize(int maxMemorySize) {
        this.maxMemorySize = maxMemorySize;
    }
}
