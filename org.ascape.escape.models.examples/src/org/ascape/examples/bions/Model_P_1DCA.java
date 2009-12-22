/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.examples.bions;

import org.ascape.model.Agent;
import org.ascape.model.Cell;
import org.ascape.model.CellOccupant;
import org.ascape.model.HostCell;
import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.rule.Diffusable;
import org.ascape.model.rule.ParameterizedDiffusion;
import org.ascape.model.rule.Rule;
import org.ascape.model.space.Array1D;
import org.ascape.util.Conditional;
import org.ascape.util.data.DataPoint;
import org.ascape.util.data.DataPointConcrete;
import org.ascape.util.data.StatCollectorCSAMM;
import org.ascape.util.vis.ColorFeature;
import org.ascape.util.vis.ColorFeatureGradiatedIndex;
import org.ascape.util.vis.DrawFeature;
import org.ascape.view.vis.Scrolling1DView;
import org.eclipse.draw2d.Graphics;
import org.eclipse.swt.graphics.Color;

public class Model_P_1DCA extends Scape {

    /**
     * 
     */
    private static final long serialVersionUID = -7862364348103737663L;

    private Scape territory;

    private Scape redBions;

    private Scape orangeBions;

    private Scape blueBions;

    private double initialDensity = 0.10f;

    private double initialStoredEnergy = 1.5f;

    private double bionEnergyLeakage = 0.02f;

    private double bionEnergyConsumption = 0.015f;

    private double bionEnergyGain = 1.0f;

    private int minVision = 8;

    private int maxVision = 8;

    public class BionTile extends HostCell implements Diffusable {

        /**
         * 
         */
        private static final long serialVersionUID = -8587243092093572271L;
        public double redEnergy;
        public double orangeEnergy;
        public double blueEnergy;

        public Object getPlatformColor() {
            // return new Color(Math.min(1.0f, (float) (redEnergy + orangeEnergy)), (float) orangeEnergy * .8f, (float)
            // blueEnergy);
            return ColorFeatureGradiatedIndex.create((float) redEnergy, (float) orangeEnergy, (float) blueEnergy);
        }

        public double getRedEnergy() {
            return redEnergy;
        }

        public void setRedEnergy(double redEnergy) {
            this.redEnergy = redEnergy;
        }

        public double getOrangeEnergy() {
            return orangeEnergy;
        }

        public void setOrangeEnergy(double orangeEnergy) {
            this.orangeEnergy = orangeEnergy;
        }

        public double getBlueEnergy() {
            return blueEnergy;
        }

        public void setBlueEnergy(double blueEnergy) {
            this.blueEnergy = blueEnergy;
        }

        private double diffusionTemp;

        public double getDiffusionTemp() {
            return diffusionTemp;
        }

        public void setDiffusionTemp(double diffusionTemp) {
            this.diffusionTemp = diffusionTemp;
        }
    }

    public static DataPointConcrete REDNESS = new DataPointConcrete() {
        /**
         * 
         */
        private static final long serialVersionUID = 3300468346744661676L;

        public double getValue(Object o) {
            return ((BionTile) o).getRedEnergy();
        }
    };

    public static DataPointConcrete ORANGENESS = new DataPointConcrete() {
        /**
         * 
         */
        private static final long serialVersionUID = 4864029754192227609L;

        public double getValue(Object o) {
            return ((BionTile) o).getOrangeEnergy();
        }
    };

    public static DataPointConcrete BLUENESS = new DataPointConcrete() {
        /**
         * 
         */
        private static final long serialVersionUID = -8558306130437067331L;

        public double getValue(Object o) {
            return ((BionTile) o).getBlueEnergy();
        }
    };

    private final ParameterizedDiffusion diffuse_red_energy = new ParameterizedDiffusion("Diffuse Red Energy") {
        /**
         * 
         */
        private static final long serialVersionUID = -2255130517720569749L;

        public double getDiffusionValue(Agent agent) {
            return ((BionTile) agent).getRedEnergy();
        }

        public void setDiffusionValue(Agent agent, double value) {
            ((BionTile) agent).setRedEnergy(value);
        }
    };

    private final ParameterizedDiffusion diffuse_orange_energy = new ParameterizedDiffusion("Diffuse Orange Energy") {
        /**
         * 
         */
        private static final long serialVersionUID = -7560426227187021096L;

        public double getDiffusionValue(Agent agent) {
            return ((BionTile) agent).getOrangeEnergy();
        }

        public void setDiffusionValue(Agent agent, double value) {
            ((BionTile) agent).setOrangeEnergy(value);
        }
    };

    private final ParameterizedDiffusion diffuse_blue_energy = new ParameterizedDiffusion("Diffuse Blue Energy") {
        /**
         * 
         */
        private static final long serialVersionUID = -1120074961879517899L;

        public double getDiffusionValue(Agent agent) {
            return ((BionTile) agent).getBlueEnergy();
        }

        public void setDiffusionValue(Agent agent, double value) {
            ((BionTile) agent).setBlueEnergy(value);
        }
    };

    public class Bion extends CellOccupant {

        /**
         * 
         */
        private static final long serialVersionUID = -6921707422985190313L;
        protected int vision;
        protected double storedEnergy;

        public void initialize() {
            super.initialize();
            vision = 8;
            storedEnergy = initialStoredEnergy;
        }

        public void leaveEnergy() {
            storedEnergy -= bionEnergyLeakage * 1.0f / bionEnergyGain;
        }

        public void consumeRedEnergy() {
            double energy = Math.min(((BionTile) getHostCell()).getRedEnergy(), bionEnergyConsumption);
            ((BionTile) getHostCell()).setRedEnergy(((BionTile) getHostCell()).getRedEnergy() - energy);
            storedEnergy += energy;
        }

        public void consumeOrangeEnergy() {
            double energy = Math.min(((BionTile) getHostCell()).getOrangeEnergy(), bionEnergyConsumption);
            ((BionTile) getHostCell()).setOrangeEnergy(((BionTile) getHostCell()).getOrangeEnergy() - energy);
            storedEnergy += energy;
        }

        public void consumeBlueEnergy() {
            double energy = Math.min(((BionTile) getHostCell()).getBlueEnergy(), bionEnergyConsumption);
            ((BionTile) getHostCell()).setBlueEnergy(((BionTile) getHostCell()).getBlueEnergy() - energy);
            storedEnergy += energy;
        }

        public boolean deathCondition() {
            return storedEnergy <= 0.0f;
        }

        public boolean fissionCondition() {
            return storedEnergy > 2.0f;
        }

        public void fission() {
            if (getHostCell().isNeighborAvailable()) {
                Bion child = (Bion) this.clone();
                getScape().add(child);
                child.moveTo(getHostCell().findRandomAvailableNeighbor());
                child.storedEnergy = storedEnergy / 2.0f;
                storedEnergy = storedEnergy / 2.0f;
            }
        }

        public int getVision() {
            return vision;
        }

        public void setVision(int vision) {
            this.vision = vision;
        }

        public double getStoredEnergy() {
            return storedEnergy;
        }

        public void setStoredEnergy(double storedEnergy) {
            this.storedEnergy = storedEnergy;
        }
    }

    class RedBion extends Bion {

        /**
         * 
         */
        private static final long serialVersionUID = -4537375121821536720L;

        public Object getPlatformColor() {
            return ColorFeature.RED;
        }

        public void leaveEnergy() {
            super.leaveEnergy();
            ((BionTile) getHostCell()).setRedEnergy(Math.min(((BionTile) getHostCell()).getRedEnergy()
                + bionEnergyLeakage, 1.0f));
        }
    }

    class OrangeBion extends Bion {

        /**
         * 
         */
        private static final long serialVersionUID = -4188875989538103126L;

        public Object getPlatformColor() {
            return ColorFeature.ORANGE;
        }

        public void leaveEnergy() {
            super.leaveEnergy();
            ((BionTile) getHostCell()).setOrangeEnergy(Math.min(((BionTile) getHostCell()).getOrangeEnergy()
                + bionEnergyLeakage, 1.0f));
        }
    }

    class BlueBion extends Bion {

        /**
         * 
         */
        private static final long serialVersionUID = -7140527644467476380L;

        public Object getPlatformColor() {
            return ColorFeature.BLUE;
        }

        public void leaveEnergy() {
            super.leaveEnergy();
            ((BionTile) getHostCell()).setBlueEnergy(Math.min(((BionTile) getHostCell()).getBlueEnergy()
                + bionEnergyLeakage, 1.0f));
        }
    }

    public static Conditional CONTAINS_RED = new Conditional() {
        /**
         * 
         */
        private static final long serialVersionUID = -7737057090540585896L;

        public boolean meetsCondition(Object o) {
            return ((HostCell) o).getOccupant() instanceof RedBion;
        }
    };

    public static Conditional CONTAINS_ORANGE = new Conditional() {
        /**
         * 
         */
        private static final long serialVersionUID = -57790021033902337L;

        public boolean meetsCondition(Object o) {
            return ((HostCell) o).getOccupant() instanceof OrangeBion;
        }
    };

    public static Conditional CONTAINS_BLUE = new Conditional() {
        /**
         * 
         */
        private static final long serialVersionUID = 9165030660226500735L;

        public boolean meetsCondition(Object o) {
            return ((HostCell) o).getOccupant() instanceof BlueBion;
        }
    };

    class MoveTowardConditionRule extends Rule {

        /**
         * 
         */
        private static final long serialVersionUID = 4664276237410056710L;
        String name;
        Conditional condition;

        public MoveTowardConditionRule(String name, Conditional condition) {
            super(name);
            this.name = name;
            this.condition = condition;
        }

        public void execute(Agent a) {
            HostCell target = (HostCell) ((CellOccupant) a).getHostCell().findNearest(condition, false,
                                                                                      ((Bion) a).getVision());
            if (target != null) {
                ((Bion) a).moveToward(target);
            }
        }
    };

    class MoveAwayConditionRule extends MoveTowardConditionRule {

        /**
         * 
         */
        private static final long serialVersionUID = 5279957207759639982L;

        public MoveAwayConditionRule(String name, Conditional condition) {
            super(name, condition);
        }

        public void execute(Agent a) {
            HostCell target = (HostCell) ((CellOccupant) a).getHostCell().findNearest(condition, false,
                                                                                      ((Bion) a).getVision());
            if (target != null) {
                ((Bion) a).moveAway(target);
            }
        }
    };

    public final Rule MOVE_TOWARD_RED_RULE = new MoveTowardConditionRule("Move Toward Red", CONTAINS_RED);

    public final Rule MOVE_TOWARD_ORANGE_RULE = new MoveTowardConditionRule("Move Toward Orange", CONTAINS_ORANGE);

    public final Rule MOVE_TOWARD_BLUE_RULE = new MoveTowardConditionRule("Move Toward Blue", CONTAINS_BLUE);

    public final Rule MOVE_AWAY_RED_RULE = new MoveAwayConditionRule("Move Away Red", CONTAINS_RED);

    public final Rule MOVE_AWAY_ORANGE_RULE = new MoveAwayConditionRule("Move Away Orange", CONTAINS_ORANGE);

    public final Rule MOVE_AWAY_BLUE_RULE = new MoveAwayConditionRule("Move Away Blue", CONTAINS_BLUE);

    class MoveTowardMaximumRule extends Rule {

        /**
         * 
         */
        private static final long serialVersionUID = -7237478658149274133L;
        String name;
        DataPoint point;

        public MoveTowardMaximumRule(String name, DataPoint point) {
            super(name);
            this.name = name;
            this.point = point;
        }

        public void execute(Agent a) {
            HostCell target = (HostCell) ((CellOccupant) a).getHostCell().findMaximumWithin(point, true,
                                                                                            ((Bion) a).getVision());
            if (target != null) {
                ((Bion) a).moveToward(target);
            }
        }
    }

    class MoveAwayMaximumRule extends MoveTowardMaximumRule {

        /**
         * 
         */
        private static final long serialVersionUID = 7652965726269308093L;

        public MoveAwayMaximumRule(String name, DataPoint point) {
            super(name, point);
        }

        public void execute(Agent a) {
            HostCell target = (HostCell) ((CellOccupant) a).getHostCell().findMaximumWithin(point, true,
                                                                                            ((Bion) a).getVision());
            if (target != null) {
                ((Bion) a).moveAway(target);
            }
        }
    }

    public final Rule MOVE_TOWARD_RED_ENERGY_RULE = new MoveTowardMaximumRule("Move Toward Red Energy", REDNESS);

    public final Rule MOVE_TOWARD_ORANGE_ENERGY_RULE = new MoveTowardMaximumRule("Move Toward Orange Energy",
        ORANGENESS);

    public final Rule MOVE_TOWARD_BLUE_ENERGY_RULE = new MoveTowardMaximumRule("Move Toward Blue Energy", BLUENESS);

    public final Rule MOVE_AWAY_RED_ENERGY_RULE = new MoveAwayMaximumRule("Move Away Red Energy", REDNESS);

    public final Rule MOVE_AWAY_ORANGE_ENERGY_RULE = new MoveAwayMaximumRule("Move Away Orange Energy", ORANGENESS);

    public final Rule MOVE_AWAY_BLUE_ENERGY_RULE = new MoveAwayMaximumRule("Move Away Blue Energy", BLUENESS);

    public final static Rule LEAVE_ENERGY_RULE = new Rule("Leave Energy") {
        /**
         * 
         */
        private static final long serialVersionUID = 8572363159193421865L;

        public void execute(Agent a) {
            ((Bion) a).leaveEnergy();
        }
    };

    public final static Rule CONSUME_RED_ENERGY_RULE = new Rule("Consume Red Energy") {
        /**
         * 
         */
        private static final long serialVersionUID = 2793318812633726743L;

        public void execute(Agent a) {
            ((Bion) a).consumeRedEnergy();
        }
    };

    public final static Rule CONSUME_ORANGE_ENERGY_RULE = new Rule("Consume Orange Energy") {
        /**
         * 
         */
        private static final long serialVersionUID = -3809309622348973994L;

        public void execute(Agent a) {
            ((Bion) a).consumeOrangeEnergy();
        }
    };

    public final static Rule CONSUME_BLUE_ENERGY_RULE = new Rule("Consume Blue Energy") {
        /**
         * 
         */
        private static final long serialVersionUID = 8265594512800326856L;

        public void execute(Agent a) {
            ((Bion) a).consumeBlueEnergy();
        }
    };

    public void createScape() {
        super.createScape();
        territory = new Scape(new Array1D());
        territory.setName("Bionland");
        territory.setPrototypeAgent(new BionTile());
        territory.setExtent(190);
        add(territory);
        territory.setExecutionOrder(RULE_ORDER);
        territory.addRule(diffuse_red_energy);
        territory.addRule(diffuse_orange_energy);
        territory.addRule(diffuse_blue_energy);
        redBions = new Scape();
        createBions(redBions, new RedBion(), "Red");
        redBions.getRules().setSelected(MOVE_AWAY_BLUE_ENERGY_RULE, true);
        redBions.getRules().setSelected(MOVE_TOWARD_ORANGE_ENERGY_RULE, true);
        redBions.getRules().setSelected(CONSUME_BLUE_ENERGY_RULE, true);
        redBions.getRules().setSelected(CONSUME_ORANGE_ENERGY_RULE, true);
        orangeBions = new Scape();
        createBions(orangeBions, new OrangeBion(), "Orange");
        orangeBions.getRules().setSelected(MOVE_AWAY_RED_ENERGY_RULE, true);
        orangeBions.getRules().setSelected(MOVE_TOWARD_BLUE_ENERGY_RULE, true);
        orangeBions.getRules().setSelected(CONSUME_RED_ENERGY_RULE, true);
        orangeBions.getRules().setSelected(CONSUME_BLUE_ENERGY_RULE, true);
        blueBions = new Scape();
        createBions(blueBions, new BlueBion(), "Blue");
        blueBions.getRules().setSelected(MOVE_AWAY_ORANGE_ENERGY_RULE, true);
        blueBions.getRules().setSelected(MOVE_TOWARD_RED_ENERGY_RULE, true);
        blueBions.getRules().setSelected(CONSUME_RED_ENERGY_RULE, true);
        blueBions.getRules().setSelected(CONSUME_ORANGE_ENERGY_RULE, true);
    }

    public void scapeSetup(ScapeEvent scapeEvent) {
        int popSize = (int) (territory.getSize() * initialDensity / 3.0f);
        redBions.setExtent(popSize);
        orangeBions.setExtent(popSize);
        blueBions.setExtent(popSize);
    }

    protected void createBions(Scape bions, CellOccupant protoCell, String name) {
        bions.setName(name);
        protoCell.setHostScape(territory);
        bions.setPrototypeAgent(protoCell);
        bions.addInitialRule(MOVE_RANDOM_LOCATION_RULE);
        bions.addRule(RANDOM_WALK_RULE);
        bions.addRule(MOVE_TOWARD_RED_ENERGY_RULE, false);
        bions.addRule(MOVE_TOWARD_ORANGE_ENERGY_RULE, false);
        bions.addRule(MOVE_TOWARD_BLUE_ENERGY_RULE, false);
        bions.addRule(MOVE_AWAY_RED_ENERGY_RULE, false);
        bions.addRule(MOVE_AWAY_ORANGE_ENERGY_RULE, false);
        bions.addRule(MOVE_AWAY_BLUE_ENERGY_RULE, false);
        bions.addRule(MOVE_TOWARD_RED_RULE, false);
        bions.addRule(MOVE_TOWARD_ORANGE_RULE, false);
        bions.addRule(MOVE_TOWARD_BLUE_RULE, false);
        bions.addRule(MOVE_AWAY_RED_RULE, false);
        bions.addRule(MOVE_AWAY_ORANGE_RULE, false);
        bions.addRule(MOVE_AWAY_BLUE_RULE, false);
        bions.addRule(CONSUME_RED_ENERGY_RULE, false);
        bions.addRule(CONSUME_ORANGE_ENERGY_RULE, false);
        bions.addRule(CONSUME_BLUE_ENERGY_RULE, false);
        bions.addRule(LEAVE_ENERGY_RULE);
        bions.addRule(FISSIONING_RULE);
        bions.addRule(DEATH_RULE);
        add(bions);
        bions.addStatCollector(new StatCollectorCSAMM(name + " Stored Energy") {
            /**
             * 
             */
            private static final long serialVersionUID = 4809855147408093109L;

            public double getValue(Object o) {
                return ((Bion) o).getStoredEnergy();
            }
        });
    }

    public void createViews() {
        super.createViews();
        DrawFeature drawTest = new DrawFeature("Draw Test") {
            /**
             * 
             */
            private static final long serialVersionUID = 7238566941830375794L;

            public void draw(Graphics g, Object object, int width, int height) {
                g.setBackgroundColor((org.eclipse.swt.graphics.Color) ((Agent) object).getPlatformColor());
                g.fillRectangle(0, 0, width - 1, height - 1);
                Bion b = (Bion) ((HostCell) object).getOccupant();
                if (b != null) {
                    g.setBackgroundColor((Color) ((Cell) ((HostCell) object).getOccupant()).getPlatformColor());
                    if (b.getStoredEnergy() > 1.7) {
                        g.drawOval(0, 0, width - 2, height - 2);
                    } else if (b.getStoredEnergy() > 1.2) {
                        g.drawOval(0, 0, width - 3, height - 3);
                    } else if (b.getStoredEnergy() > .7) {
                        g.drawOval(0, 0, width - 4, height - 4);
                    } else if (b.getStoredEnergy() > .4) {
                        g.drawOval(0, 0, width - 5, height - 5);
                    } else {
                        g.drawOval(0, 0, width - 6, height - 6);
                    }
                }
            }
        };
        territory.addDrawFeature(drawTest);

        Scrolling1DView mapView = new Scrolling1DView("Map");
        territory.addView(mapView);
        mapView.setCellSize(2);

        /*
         * ChartView chart = new ChartView(); chart.setName("Population"); addView(chart);
         * chart.addSeries("Count Red Stored Energy", ColorFeature.RED); chart.addSeries("Count Orange Stored Energy",
         * ColorFeature.ORANGE); chart.addSeries("Count Blue Stored Energy", ColorFeature.BLUE);
         */
    }

    public double getInitialPopulationDensity() {
        return initialDensity;
    }

    public void setInitialPopulationDensity(double initialDensity) {
        this.initialDensity = initialDensity;
    }

    public void setBionEnergyGain(double bionEnergyGain) {
        this.bionEnergyGain = bionEnergyGain;
    }

    public double getBionEnergyGain() {
        return bionEnergyGain;
    }

    public double getBionEnergyLeakage() {
        return bionEnergyLeakage;
    }

    public void setBionEnergyLeakage(double bionEnergyLeakage) {
        this.bionEnergyLeakage = bionEnergyLeakage;
    }

    public double getBionEnergyConsumption() {
        return bionEnergyConsumption;
    }

    public void setBionEnergyConsumption(double bionEnergyConsumption) {
        this.bionEnergyConsumption = bionEnergyConsumption;
    }

    public double getInitialStoredEnergy() {
        return initialStoredEnergy;
    }

    public void setInitialStoredEnergy(double initialStoredEnergy) {
        this.initialStoredEnergy = initialStoredEnergy;
    }

    public int getMinimumVision() {
        return minVision;
    }

    public void setMinimumVision(int minVision) {
        this.minVision = minVision;
    }

    public int getMaximumVision() {
        return maxVision;
    }

    public void setMaximumVision(int maxVision) {
        this.maxVision = maxVision;
    }

    public double getDiffusionConstant() {
        // red stands in for all diffusion..
        return diffuse_red_energy.getDiffusionConstant();
    }

    public void setDiffusionConstant(double diffuseConstant) {
        diffuse_red_energy.setDiffusionConstant(diffuseConstant);
        diffuse_orange_energy.setDiffusionConstant(diffuseConstant);
        diffuse_blue_energy.setDiffusionConstant(diffuseConstant);
    }

    public double getEvaporationRate() {
        return diffuse_red_energy.getEvaporationRate();
    }

    public void setEvaporationRate(double evaporationRate) {
        diffuse_red_energy.setEvaporationRate(evaporationRate);
        diffuse_orange_energy.setEvaporationRate(evaporationRate);
        diffuse_blue_energy.setEvaporationRate(evaporationRate);
    }
}
