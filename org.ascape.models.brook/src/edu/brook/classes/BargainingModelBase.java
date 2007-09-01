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

import org.ascape.model.Agent;
import org.ascape.model.Scape;
import org.ascape.model.rule.Rule;
import org.ascape.model.space.Array1D;
import org.ascape.model.space.Coordinate1DDiscrete;
import org.ascape.util.vis.SimplexFeature;
import org.ascape.view.vis.SimplexView;

public class BargainingModelBase extends Scape {

    private int population = 100;

    protected int minMemorySize = 20;

    protected int maxMemorySize = 100;

    protected float randomStrategyProbability = .1f;

    protected Scape agents;

    public void createScape() {
        super.createScape();
        agents = new Scape(new Array1D());
        agents.setExtent(new Coordinate1DDiscrete(population));
        agents.setPrototypeAgent(new Bargainer());
        agents.addRule(PLAY_OTHER);
        agents.setExecutionStyle(REPEATED_DRAW);
        agents.setAgentsPerIteration(population / 2);
        add(agents);
    }

    @SuppressWarnings("unused")
    private boolean allMedium;

    Rule checkAllMedium = new Rule("Check All Medium") {
        public void execute(Agent a) {
            if (((Bargainer) a).lastStrategy != Strategy.MEDIUM_STRATEGY) {
                allMedium = false;
            }
        }
        @SuppressWarnings("unused")
        public boolean isRandom() {
            return false;
        }
    };

    SimplexView intraMemoryView;

    private static float lowPayoff = 0.30f;

    private static float mediumPayoff = 0.50f;

    private static float highPayoff = 1.0f - lowPayoff;

    public void createViews() {
/*
 * agents.addView(new StandardOutView() { public void updateScapeGraphics() {
 * int iter = scape.getIteration(); if (((float) ( iter) / 1000) == (iter /
 * 1000)) { System.out.println(iter); } allMedium = true;
 * agents.executeOnMembers(checkAllMedium); if (allMedium) {
 * System.out.println("Medium at iteration "+iter); stop(); } } });
 */
        super.createViews();
        intraMemoryView = new SimplexView("Intragroup Memory", Strategy.LOW_STRATEGY.getDemand());
        intraMemoryView.setSimplexFeature(new SimplexFeature("Memory") {
            public float getAxis1Value(Object object) {
                return (float) ((Bargainer) object).countIntraHigh() / (float) ((Bargainer) object).countIntra();
            }

            public String getAxis1Name() {
                return "high";
            }

            public float getAxis2Value(Object object) {
                return (float) ((Bargainer) object).countIntraMedium() / (float) ((Bargainer) object).countIntra();
            }

            public String getAxis2Name() {
                return "medium";
            }

            public float getAxis3Value(Object object) {
                return (float) ((Bargainer) object).countIntraLow() / (float) ((Bargainer) object).countIntra();
            }

            public String getAxis3Name() {
                return "low";
            }
        });

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

    public static float getLowPayoffStatic() {
        return lowPayoff;
    }

    public float getLowPayoff() {
        return lowPayoff;
    }

    public void setLowPayoff(float lowPayoff) {
        this.lowPayoff = lowPayoff;
    }

    public static float getMediumPayoffStatic() {
        return mediumPayoff;
    }

    public float getMediumPayoff() {
        return mediumPayoff;
    }

    public void setMediumPayoff(float mediumPayoff) {
        this.mediumPayoff = mediumPayoff;
    }

    public static float getHighPayoffStatic() {
        return highPayoff;
    }

    public float getHighPayoff() {
        return highPayoff;
    }

    public void setHighPayoff(float highPayoff) {
        this.highPayoff = highPayoff;
    }
}
