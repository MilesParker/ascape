/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.sugarscape;

import java.awt.Color;

import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.space.Array2DVonNeumann;
import org.ascape.model.space.Coordinate1DDiscrete;
import org.ascape.model.space.Coordinate2DDiscrete;
import org.ascape.util.data.StatCollector;
import org.ascape.util.data.StatCollectorCSAMM;
import org.ascape.util.data.UnitIntervalDataPoint;
import org.ascape.util.vis.ColorFeatureFixed;
import org.ascape.util.vis.ColorFeatureGradiated;
import org.ascape.view.vis.Overhead2DView;

public class GAS_Base extends Scape {

    /**
     * 
     */
    private static final long serialVersionUID = 5765121711598985032L;
    //Parameters
    private int minVision = 1;
    private int maxVision = 6;
    private int minSugarMetabolism = 1;
    private int maxSugarMetabolism = 4;
    private int minInitialSugar = 0;
    private int maxInitialSugar = 30;
    private int minDeathAge = 60;
    private int maxDeathAge = 100;
    private float sugarMoundness = .004f;

    protected Scape sugarscape;

    protected Scape agents;

    protected Overhead2DView sugarView;

    /**
     * Returns the topology parameter for sugar peaks
     */
    public float getSugarMoundness() {
        return sugarMoundness;
    }

    /**
     * Sets the topology parameter for sugar peaks
     */
    public void setSugarMoundness(float moundness) {
        sugarMoundness = moundness;
    }

    /**
     * Returns the minimum vision that an agent may be given on initialization.
     * Model parameter.
     */
    public int getMinVision() {
        return minVision;
    }

    /**
     * Sets the minimum vision that an agent may be given on initialization.
     * Model parameter.
     */
    public void setMinVision(int _minVision) {
        minVision = _minVision;
    }

    /**
     * Returns the maximum vision that an agent may be given on initialization.
     * Model parameter.
     */
    public int getMaxVision() {
        return maxVision;
    }

    /**
     * Sets the maximum vision that an agent may be given on initialization.
     * Model parameter.
     */
    public void setMaxVision(int _maxVision) {
        maxVision = _maxVision;
    }

    /**
     * Returns the minimum sugar metabolism that an agent may be given on initialization.
     * Model parameter.
     */
    public int getMinSugarMetabolism() {
        return minSugarMetabolism;
    }

    /**
     * Sets the minimum sugar metabolism that an agent may be given on initialization.
     * Model parameter.
     */
    public void setMinSugarMetabolism(int _minMetabolism) {
        minSugarMetabolism = _minMetabolism;
    }

    /**
     * Returns the maximum sugar metabolism that an agent may be given on initialization.
     * Model parameter.
     */
    public int getMaxSugarMetabolism() {
        return maxSugarMetabolism;
    }

    /**
     * Sets the maximum Sugar metabolism that an agent may be given on initialization.
     * Model parameter.
     */
    public void setMaxSugarMetabolism(int _maxMetabolism) {
        maxSugarMetabolism = _maxMetabolism;
    }

    /**
     * Returns the minimum age that an agent may be given on initialization.
     * Model parameter.
     */
    public int getMinDeathAge() {
        return minDeathAge;
    }

    /**
     * Sets the minimum age that an agent may be given on initialization.
     * Model parameter.
     */
    public void setMinDeathAge(int _minDeathAge) {
        minDeathAge = _minDeathAge;
    }

    /**
     * Returns the maximum age that an agent may be given on initialization.
     * Model parameter.
     */
    public int getMaxDeathAge() {
        return maxDeathAge;
    }

    /**
     * Sets the maximum age that an agent may be given on initialization.
     * Model parameter.
     */
    public void setMaxDeathAge(int _maxDeathAge) {
        maxDeathAge = _maxDeathAge;
    }

    /**
     * Returns the minimum sugar that an agent may be given on initialization.
     * Model parameter.
     */
    public int getMinInitialSugar() {
        return minInitialSugar;
    }

    /**
     * Sets the minimum sugar that an agent may be given on initialization.
     * Model parameter.
     */
    public void setMinInitialSugar(int _minInitialSugar) {
        minInitialSugar = _minInitialSugar;
    }

    /**
     * Returns the maximum sugar that an agent may be given on initialization.
     * Model parameter.
     */
    public int getMaxInitialSugar() {
        return maxInitialSugar;
    }

    /**
     * Sets the maximum sugar that an agent may be given on initialization.
     * Model parameter.
     */
    public void setMaxInitialSugar(int _maxInitialSugar) {
        maxInitialSugar = _maxInitialSugar;
    }

    public void createScape() {
        super.createScape();
        setName("Sugarscape Model");
        setPrototypeAgent(new Scape());
        sugarscape = new Scape(new Array2DVonNeumann());
        sugarscape.setName("Sugar Cells");
        sugarscape.setPrototypeAgent(new SugarCell());
        sugarscape.setExtent(new Coordinate2DDiscrete(50, 50));
        SugarAgent agent = new SugarAgent();
        agent.setHostScape(sugarscape);
        agents = new Scape();
        agents.setName("Sugar Agents");
        agents.setPrototypeAgent(agent);
        sugarscape.setExecutionOrder(Scape.RULE_ORDER);
        sugarscape.setCellsRequestUpdates(true);
        add(sugarscape);
        add(agents);
        
        StatCollector[] stats = { new StatCollectorCSAMM() {
            /**
             * 
             */
            private static final long serialVersionUID = 1026552789605891665L;

            public double getValue(Object object) {
                return ((SugarAgent) object).getSugar().getStock();
            }

            public String getName() {
                return "Sugar";
            }
        }, new StatCollectorCSAMM() {
            /**
                                     * 
                                     */
            private static final long serialVersionUID = -4921601134107850321L;

            public double getValue(Object object) {
                return -((SugarAgent) object).getAge();
            }

            public String getName() {
                return "Age";
            }
        }, new StatCollectorCSAMM() {
            /**
                                     * 
                                     */
            private static final long serialVersionUID = -6328308124120217985L;

            public double getValue(Object object) {
                return ((SugarAgent) object).getVision();
            }

            public String getName() {
                return "Vision";
            }
        }, new StatCollectorCSAMM() {
            /**
                                     * 
                                     */
            private static final long serialVersionUID = -9026524001803218091L;

            public double getValue(Object object) {
                return ((SugarAgent) object).getSugarMetabolism();
            }

            public String getName() {
                return "Sugar Metabolism";
            }
        } };
        agents.addStatCollectors(stats);
    }

    public void scapeSetup(ScapeEvent scapeEvent) {
        agents.setExtent(new Coordinate1DDiscrete(400));
    }

    public void createViews() {
        super.createViews();
        sugarView = new Overhead2DView();
        final ColorFeatureGradiated colorCellForSugar = new ColorFeatureGradiated("Sugar");
        colorCellForSugar.setDataPoint(new UnitIntervalDataPoint() {
            /**
             * 
             */
            private static final long serialVersionUID = 60938578951681284L;

            public double getValue(Object object) {
                return ((double) ((SugarCell) object).getSugar().getQuantity() / (double) SugarCell.MAX_SUGAR);
            }
        });
        colorCellForSugar.setMaximumColor(Color.yellow);
        sugarView.setCellColorFeature(colorCellForSugar);
        sugarView.setHostedAgentColorFeature(ColorFeatureFixed.red);
        sugarView.setCellSize(8);
        sugarscape.addView(sugarView);
    }
}
