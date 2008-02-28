/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.norms;

import java.awt.Color;
import java.util.List;

import org.ascape.model.Agent;
import org.ascape.model.Cell;
import org.ascape.model.Scape;
import org.ascape.model.rule.CollectStats;
import org.ascape.model.rule.Rule;
import org.ascape.model.space.Coordinate1DDiscrete;
import org.ascape.util.data.StatCollector;
import org.ascape.util.data.StatCollectorCSAMM;
import org.ascape.util.data.StatCollectorCond;
import org.ascape.util.data.StatCollectorCondCSAMMVar;
import org.ascape.util.data.UnitIntervalDataPoint;
import org.ascape.util.vis.ColorFeatureGradiated;
import org.ascape.view.vis.ChartView;
import org.ascape.view.vis.ComponentView;
import org.ascape.view.vis.Scrolling1DView;

public class Norms extends Scape {

    /**
     * 
     */
    private static final long serialVersionUID = -8346760837643636831L;
    private double ambientNoise = .03;//set ambient=crisis in constant-noise cases.
    private double tolerance = .05;
    private int initMaxRadius = 10;//Default is 10
    private int size = 191;
    private int maxRadius = (size - 1) / 2;
    boolean includeSelf = true;		//1 is default->Agent EXCLUDES himself with default tie-break of

    private CollectStats valueCollector;
    private StatCollector[] values;

    public class NormCell extends Cell {

        /**
         * 
         */
        private static final long serialVersionUID = -4201627518794467460L;

        protected int strategy = 0;

        protected int radius = 0;

        public boolean strategyFixed;

        public boolean radiusFixed;

        public NormCell() {
            super();
        }

        public void initialize() {
            super.initialize();
            //Normal case
            setStrategy((getRandom().nextInt() > 0) ? 0 : 1);
//Initial strategy "lock-in" case (Figure 1 in paper)
//setStrategy(0);
            setRadius((randomToLimit(initMaxRadius)));
/*float randomDraw = getRandom().nextFloat();
            if (randomDraw < .10) {
                strategyFixed = true;
                radiusFixed = false;
            }
            else if (randomDraw > 1.0 - .00) {
                radiusFixed = true;
                strategyFixed = false;
                setRadius(20);
            }
            else {
                strategyFixed = false;
                radiusFixed = false;
            }*/
        }

        public void scapeCreated() {
            scape.addRule(new Rule("Update Radius & Strategy") {
                /**
                 * 
                 */
                private static final long serialVersionUID = -7630790252873313917L;

                public void execute(Agent agent) {
                    ((NormCell) agent).updateRadius();
                    ((NormCell) agent).updateStrategy();
                }
            });
            scape.addRule(new Rule("Update Radius Only") {
                /**
                 * 
                 */
                private static final long serialVersionUID = 782487720819697595L;

                public void execute(Agent agent) {
                    ((NormCell) agent).updateRadius();
                }
            }, false);
            scape.addRule(new Rule("Update Strategy Only") {
                /**
                 * 
                 */
                private static final long serialVersionUID = 6448955998139337111L;

                public void execute(Agent agent) {
                    ((NormCell) agent).updateStrategy();
                }
            }, false);
            if (valueCollector == null) {
                valueCollector = new CollectStats();
                values = new StatCollector[1];
                values[0] = new StatCollectorCSAMM() {
                    /**
                     * 
                     */
                    private static final long serialVersionUID = 981681713575230657L;

                    public double getValue(Object object) {
                        return ((NormCell) object).getStrategy();
                    }
                };
            }
            valueCollector.addStatCollectors(values);
        }

        public Color getColor() {
            //if (getAttributeValue("Radius") != CoordinateSeries2D.MAX_RANK - 1) {
            /*if (strategyFixed) {
                if (getStrategy() == 0) {
                    return Color.blue.darker();
                }
                else {
                    return Color.yellow.darker();
                }
            }
            else if (radiusFixed) {*/
            /*if (getStrategy() == 0) {
                return Color.black;
            }
            else {
                return Color.white;
            }*/
            /*}
            else {*/
            if (getStrategy() == 0) {
                return Color.blue;
            } else {
                return Color.yellow;
            }
            //}
        }

        List nearAgents;
        List nearMinusOneAgents;
        List nearPlusOneAgents;

        public void findNearCells() {
            nearAgents = findWithin((double) getRadius(), true);
            nearMinusOneAgents = findWithin(getRadius() - 1, true);
            nearPlusOneAgents = findWithin(getRadius() + 1, true);
        }

        public void updateRadius() {
            valueCollector.clear();
            scape.execute(valueCollector, nearAgents);
            double averageRadius = ((StatCollectorCSAMM) values[0]).getAvg();
            valueCollector.clear();
            scape.execute(valueCollector, nearMinusOneAgents);
            double averageRadiusMinusOne = ((StatCollectorCSAMM) values[0]).getAvg();
            valueCollector.clear();
            scape.execute(valueCollector, nearPlusOneAgents);
            double averageRadiusPlusOne = ((StatCollectorCSAMM) values[0]).getAvg();
            valueCollector.clear();
            if ((averageRadiusPlusOne <= (averageRadius - tolerance)) || (averageRadiusPlusOne >= averageRadius + tolerance)) {
                setRadius(Math.min(getRadius() + 1, maxRadius));
            } else if ((averageRadiusMinusOne >= averageRadius - tolerance) || (averageRadiusMinusOne <= averageRadius + tolerance)) {
                setRadius(getRadius() - 1);
            }
        }

        public void updateStrategy() {
            //Normal Case
            if (getRandom().nextDouble() > ambientNoise) {
                //Noise shock case
                //if (getIteration() < 145 || getIteration() >= 155) {
                //Non-random case
                scape.execute(valueCollector, nearAgents);
                double newAverageRadius = ((StatCollectorCSAMM) values[0]).getAvg();
                if (newAverageRadius < 0.5) {
                    setStrategy(0);
                } else {
                    setStrategy(1);
                }
            } else {
                //Random case
                setStrategy(randomIs() ? 0 : 1);
            }
        }

        public int getStrategy() {
            return strategy;
        }

        public void setStrategy(int strategy) {
            this.strategy = strategy;
        }

        public int getRadius() {
            return radius;
        }

        public void setRadius(int radius) {
            if (radius >= 1) {
                this.radius = radius;
            } else {
                this.radius = 1;
            }
            findNearCells();
        }
    }

    public void createScape() {
        setName("Norm Model");
        setPrototypeAgent(new NormCell());
        setExtent(new Coordinate1DDiscrete(size));
        setExecutionStyle(REPEATED_DRAW);
        super.createScape();
    }

    public void createViews() {

        //Collect the statistics to support views
        StatCollector[] stats = new StatCollector[3];
        stats[0] = new StatCollectorCondCSAMMVar("Radius") {
            /**
             * 
             */
            private static final long serialVersionUID = -4942178570771647277L;

            public boolean meetsCondition(Object object) {
                return (!((NormCell) object).strategyFixed && !((NormCell) object).radiusFixed);
            }

            public double getValue(Object object) {
                return (((NormCell) object).getRadius());
            }
        };
        stats[1] = new StatCollectorCond("Yellow") {
            /**
             * 
             */
            private static final long serialVersionUID = 2321787007979922009L;

            public boolean meetsCondition(Object object) {
                return (((NormCell) object).getStrategy() == 0);
            }
        };
        stats[2] = new StatCollectorCond("Blue") {
            /**
             * 
             */
            private static final long serialVersionUID = -227900715357870804L;

            public boolean meetsCondition(Object object) {
                return (((NormCell) object).getStrategy() == 1);
            }
        };
        addStatCollectors(stats);

        super.createViews();
    }

    public void createGraphicViews() {
        super.createGraphicViews();
        //The radius value at and above which the cell is white
        final double whiteRadius = 5.0;
        final double whiteRadiusMinusOne = whiteRadius - 1.0;
        Scrolling1DView strategyView = new Scrolling1DView();
        strategyView.setName("Strategy");
        Scrolling1DView radiusView = new Scrolling1DView();
        radiusView.setName("Radius");
        ColorFeatureGradiated cellColor =
            new ColorFeatureGradiated("Radius", Color.white, Color.black, new UnitIntervalDataPoint() {
                /**
                 * 
                 */
                private static final long serialVersionUID = -9060315707944996127L;

                public double getValue(Object object) {
                    return ((((NormCell) object).getRadius() - 1.0) / whiteRadiusMinusOne);//2.5
                }
            });
        radiusView.setCellColorFeature(cellColor);
        ComponentView[] views = new ComponentView[2];
        views[0] = strategyView;
        views[1] = radiusView;
        strategyView.setCellSize(1);
        radiusView.setCellSize(1);
        addViews(views);

        //And time series
        if (!(getUIEnvironment().isInApplet())) {
            ChartView ts = new ChartView();
            addView(ts);
            ts.addSeries("Variance Radius", Color.blue);
            ts.addSeries("Standard Deviation Radius", Color.yellow);
            ts.addSeries("Minimum Radius", Color.red);
            ts.addSeries("Average Radius", Color.orange);
            ts.addSeries("Maximum Radius", Color.red);
            ts.setDisplayPoints(100);
        }
    }

    public double getAmbientNoise() {
        return ambientNoise;
    }

    public void setAmbientNoise(double ambientNoise) {
        this.ambientNoise = ambientNoise;
    }

    public double getTolerance() {
        return tolerance;
    }

    public void setTolerance(double tolerance) {
        this.tolerance = tolerance;
    }

    public int getInitMaxRadius() {
        return initMaxRadius;
    }

    public void setInitMaxRadius(int initMaxRadius) {
        this.initMaxRadius = initMaxRadius;
    }
}
