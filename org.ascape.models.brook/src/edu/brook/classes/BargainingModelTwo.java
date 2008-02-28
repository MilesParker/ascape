/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.classes;

import org.ascape.util.vis.SimplexFeature;
import org.ascape.view.vis.ComponentView;
import org.ascape.view.vis.SimplexView;

public class BargainingModelTwo extends BargainingModelBase {

    /**
     * 
     */
    private static final long serialVersionUID = -4439696528099816913L;

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
