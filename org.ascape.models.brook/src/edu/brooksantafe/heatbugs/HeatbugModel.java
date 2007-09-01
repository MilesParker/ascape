/*
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
 * _Heatbugs_
 * This is an ascape implementation of the canonical swarm model 'heatbugs'.
 * Hopefully, it will give Swarm users some insight into how ascape is organized.
 * I have tried to mirror swarm names and usage where consistent with ascape.
 * For the orignal Swarm implementation and more about swarm, see http://www.santafe.edu/projects/swarm
 *
 * _Ascape Contact_
 * Miles Parker
 * mparker@brook.edu
 * The Brookings Institution
 * Washington, D.C.
 */

package edu.brooksantafe.heatbugs;

import java.awt.Color;

import org.ascape.model.Agent;
import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.rule.ParameterizedDiffusion;
import org.ascape.model.rule.Rule;
import org.ascape.model.space.Array1D;
import org.ascape.model.space.Array2DMoore;
import org.ascape.model.space.Coordinate1DDiscrete;
import org.ascape.model.space.Coordinate2DDiscrete;
import org.ascape.util.data.StatCollector;
import org.ascape.util.data.StatCollectorCSAMM;
import org.ascape.util.data.UnitIntervalDataPoint;
import org.ascape.util.vis.ColorFeatureFixed;
import org.ascape.util.vis.ColorFeatureGradiated;
import org.ascape.util.vis.ColorFeatureGradiatedIndex;
import org.ascape.view.vis.Overhead2DView;

/**
 * The heatbugs model, containing the heatbug world and the heatbugs who live on it.
 */
public class HeatbugModel extends Scape {

    /**
     * The rule for heat diffusion for the model's heat cells.
     */
    private ParameterizedDiffusion heatDiffusionRule = new ParameterizedDiffusion("Parameterized", 1.0f, .99f) {
        public final double getDiffusionValue(Agent agent) {
            return ((HeatCell) agent).getHeat();
        }

        public final void setDiffusionValue(Agent agent, double value) {
            ((HeatCell) agent).setHeat((float) value);
        }
    };

    /**
     * The number of agents in the model.
     */
    private int numBugs = 100;

    /**
     * The extent of the heatbug world.
     */
    private int worldXSize = 80;
    private int worldYSize = 80;

    /**
     * The ranges for the heatbug ideal temperatures. By default, 17000-31000.
     */
    private int minIdealTemp = 17000;
    private int maxIdealTemp = 31000;

    /**
     * The ranges for the heatbug output heat. By default, 3000-10000.
     */
    private int minOutputHeat = 3000;
    private int maxOutputHeat = 10000;

    /**
     * The chance each turn that a heatbug will make a random move.
     */
    private double randomMoveProbability = 0.0;

    /**
     * The collection of heatbugs.
     */
    private Scape heatbugs;

    /**
     * The lattice that the heatbugs live upon.
     */
    private Scape world;

    /**
     * Create the heatbug collection and the heatbug world.
     * Add rules to the heatbugs and world.
     * (These rules could also have been added within the Heatbug and HeatCell code.)
     */
    public void createScape() {
        super.createScape();
        setName("Heatbug Model");
        //Create a 2D lattice scape using Moore geometry for heat cells and add it to the model
        world = new Scape(new Array2DMoore());
        world.setName("World");
        world.setPrototypeAgent(new HeatCell());
        world.setExtent(new Coordinate2DDiscrete(worldXSize, worldYSize));
        world.getRules().clear();
        world.setExecutionOrder(Scape.RULE_ORDER);
        world.addRule(heatDiffusionRule);
        add(world);
        //Create an array scape for heatbugs with world as the host scape and add it to the model
        Heatbug protoBug = new Heatbug();
        protoBug.setHostScape(world);
        heatbugs = new Scape(new Array1D(), "Heatbugs", protoBug);
        heatbugs.setExtent(new Coordinate1DDiscrete(numBugs));
        add(heatbugs);
        //Add rules to the scapes
        Rule happinessRule = new Rule("Calculate Happiness") {
            public void execute(Agent a) {
                ((Heatbug) a).calculateHappiness();
            }
        };
        Rule classicRule = new Rule("Classic Movement & Heat") {
            public void execute(Agent a) {
                ((Heatbug) a).movementAndHeat();
            }
        };
        Rule heatRule = new Rule("Generate Heat") {
            public void execute(Agent a) {
                ((Heatbug) a).generateHeat();
            }
        };
        heatbugs.addInitialRule(MOVE_RANDOM_LOCATION_RULE);
        //We add this initial rule only so that the heatbugs will have consistent state
        //at the beginning of the run, looks better on the graph
        heatbugs.addInitialRule(classicRule);
        heatbugs.addRule(happinessRule);
        heatbugs.addRule(classicRule);
        heatbugs.addRule(heatRule, false);
        heatbugs.addRule(MOVEMENT_RULE, false);
    }

    /**
     * On setup request, set the sizes of the model memeber scapes.
     */
    public void scapeSetup(ScapeEvent scapeEvent) {
//Set the size by setting the coordinate at the lattices largest extent.
        world.setExtent(new Coordinate2DDiscrete(worldXSize, worldYSize));
        //Set the extent of the scape, which is simply the number of agents we want.
        heatbugs.setExtent(new Coordinate1DDiscrete(numBugs));
    }

    /**
     * Create the user interface for the model.
     */
    public void createViews() {
        super.createViews();
//Now, add a simple overhead view so that we can view the lattice:
        Overhead2DView heatView = new Overhead2DView();
        final ColorFeatureGradiated colorCellForHeat = new ColorFeatureGradiatedIndex("Heat");
        colorCellForHeat.setDataPoint(new UnitIntervalDataPoint() {
            public double getValue(Object object) {
                return ((double) ((HeatCell) object).getHeat() / HeatCell.MAX_HEAT);
            }
        });
        colorCellForHeat.setMaximumColor(Color.red);
        colorCellForHeat.setMinimumColor(Color.black);
        heatView.setPrimaryAgentColorFeature(colorCellForHeat);
        heatView.setHostedAgentColorFeature(ColorFeatureFixed.green);
        heatView.setCellSize(4);
        world.addView(heatView);
        heatView.getDrawSelection().clearSelection();
        heatView.getDrawSelection().setSelected(heatView.agents_fill_cells_draw_feature, true);

        final StatCollector[] stats = {
            new StatCollectorCSAMM("Unhappiness") {
                public double getValue(Object object) {
                    return ((Heatbug) object).getUnhappiness();
                }
            }
        };
//Add the new values stats to agents
        heatbugs.addStatCollectors(stats);
        if (!(getUserEnvironment().isInApplet())) {
            /*ChartView happinessChart = new ChartView();
            heatbugs.addView(happinessChart);
            happinessChart.addSeries("Average Unhappiness", Color.blue);*/
        }
    }

    /* The following methods are just getters and setters for all of the model paramaters.
     * It is an important Java idiom to provide these getters and setters for values that a
* class exposes. As long as these methods are provided, it is possible to provide
* automatic features like parameter setting dialogs without writing any code to support
* them.
*/

    public int getNumberOfBugs() {
        return numBugs;
    }

    public void setNumberOfBugs(int numBugs) {
        this.numBugs = numBugs;
    }

    public int getMinIdealTemp() {
        return minIdealTemp;
    }

    public void setMinIdealTemp(int minIdealTemp) {
        this.minIdealTemp = minIdealTemp;
    }

    public int getMaxIdealTemp() {
        return maxIdealTemp;
    }

    public void setMaxIdealTemp(int maxIdealTemp) {
        this.maxIdealTemp = maxIdealTemp;
    }

    public int getMinOutputHeat() {
        return minOutputHeat;
    }

    public void setMinOutputHeat(int minOutputHeat) {
        this.minOutputHeat = minOutputHeat;
    }

    public int getMaxOutputHeat() {
        return maxOutputHeat;
    }

    public void setMaxOutputHeat(int maxOutputHeat) {
        this.maxOutputHeat = maxOutputHeat;
    }

    public double getDiffusionConstant() {
        return heatDiffusionRule.getDiffusionConstant();
    }

    public void setDiffusionConstant(double diffusionConstant) {
        heatDiffusionRule.setDiffusionConstant(diffusionConstant);
    }

    public double getEvaporationRate() {
        return heatDiffusionRule.getEvaporationRate();
    }

    public void setEvaporationRate(double evaporationRate) {
        heatDiffusionRule.setEvaporationRate(evaporationRate);
    }

    public double getRandomMoveProbability() {
        return randomMoveProbability;
    }

    public void setRandomMoveProbability(double randomMoveProbability) {
        this.randomMoveProbability = randomMoveProbability;
    }
}

