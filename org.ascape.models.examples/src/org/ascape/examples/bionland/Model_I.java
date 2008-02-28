/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */



package org.ascape.examples.bionland;

import java.awt.Color;
import java.awt.Image;

import org.ascape.model.Agent;
import org.ascape.model.CellOccupant;
import org.ascape.model.HostCell;
import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.rule.Rule;
import org.ascape.model.space.Array2D;
import org.ascape.model.space.Array2DMoore;
import org.ascape.util.Conditional;
import org.ascape.util.data.DataPoint;
import org.ascape.util.data.DataPointConcrete;
import org.ascape.util.data.StatCollectorCSAMM;
import org.ascape.util.vis.ImageFeatureFixed;
import org.ascape.view.vis.ChartView;
import org.ascape.view.vis.Overhead2DView;

public class Model_I extends Scape {

    /**
     * 
     */
    private static final long serialVersionUID = 5036597995844290805L;

    private Scape territory;

    private Scape redBions;

    private Scape orangeBions;

    private Scape blueBions;

    private double initialDensity = 0.28f;

    private double initialStoredEnergy = 1.5f;

    private double bionEnergyLeakage = 0.02f;

    private double bionEnergyConsumption = 0.015f;

    private double bionEnergyGain = 1.0f;

    private int minVision = 8;

    private int maxVision = 8;

    private class BionTile extends HostCell {

        /**
         * 
         */
        private static final long serialVersionUID = 1212361935242594813L;
        private double redEnergy;
        private double orangeEnergy;
        private double blueEnergy;

        public Color getColor() {
            return new Color(Math.min(1.0f, (float) (redEnergy + orangeEnergy)), (float) orangeEnergy * .8f, (float) blueEnergy);
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
    }

    public static DataPointConcrete REDNESS = new DataPointConcrete() {
        /**
         * 
         */
        private static final long serialVersionUID = -8284401700413259043L;

        public double getValue(Object o) {
            return ((BionTile) o).getRedEnergy();
        }
    };

    public static DataPointConcrete ORANGENESS = new DataPointConcrete() {
        /**
         * 
         */
        private static final long serialVersionUID = 3670795017436784202L;

        public double getValue(Object o) {
            return ((BionTile) o).getOrangeEnergy();
        }
    };

    public static DataPointConcrete BLUENESS = new DataPointConcrete() {
        /**
         * 
         */
        private static final long serialVersionUID = 3892425058629632695L;

        public double getValue(Object o) {
            return ((BionTile) o).getBlueEnergy();
        }
    };

    private class Bion extends CellOccupant {

        /**
         * 
         */
        private static final long serialVersionUID = 7357989462414666918L;
        protected int vision;
        protected double storedEnergy;

        public void initialize() {
            super.initialize();
            vision = 8;
            storedEnergy = initialStoredEnergy;
        }

        public void leaveEnergy() {
            storedEnergy -= bionEnergyLeakage * (1.0f / bionEnergyGain);
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
        private static final long serialVersionUID = -4317735380759878723L;

        public Color getColor() {
            return Color.red;
        }

        public Image getImage() {
            return ImageFeatureFixed.redBall;
        }

        public void leaveEnergy() {
            super.leaveEnergy();
            ((BionTile) getHostCell()).setRedEnergy(Math.min(((BionTile) getHostCell()).getRedEnergy() + bionEnergyLeakage, 1.0f));
        }
    }

    class OrangeBion extends Bion {

        /**
         * 
         */
        private static final long serialVersionUID = -9057281239554813341L;

        public Color getColor() {
            return Color.orange;
        }

        public Image getImage() {
            return ImageFeatureFixed.orangeBall;
        }

        public void leaveEnergy() {
            super.leaveEnergy();
            ((BionTile) getHostCell()).setOrangeEnergy(Math.min(((BionTile) getHostCell()).getOrangeEnergy() + bionEnergyLeakage, 1.0f));
        }
    }

    class BlueBion extends Bion {

        /**
         * 
         */
        private static final long serialVersionUID = -7366894819257291692L;

        public Color getColor() {
            return Color.blue;
        }

        public Image getImage() {
            return ImageFeatureFixed.blueBall;
        }

        public void leaveEnergy() {
            super.leaveEnergy();
            ((BionTile) getHostCell()).setBlueEnergy(Math.min(((BionTile) getHostCell()).getBlueEnergy() + bionEnergyLeakage, 1.0f));
        }
    }

    public static Conditional CONTAINS_RED = new Conditional() {
        /**
         * 
         */
        private static final long serialVersionUID = -2310921913551016436L;

        public boolean meetsCondition(Object o) {
            return (((HostCell) o).getOccupant() instanceof RedBion);
        }
    };

    public static Conditional CONTAINS_ORANGE = new Conditional() {
        /**
         * 
         */
        private static final long serialVersionUID = 2843119464627039584L;

        public boolean meetsCondition(Object o) {
            return (((HostCell) o).getOccupant() instanceof OrangeBion);
        }
    };

    public static Conditional CONTAINS_BLUE = new Conditional() {
        /**
         * 
         */
        private static final long serialVersionUID = 8523973124341424454L;

        public boolean meetsCondition(Object o) {
            return (((HostCell) o).getOccupant() instanceof BlueBion);
        }
    };

    class MoveTowardConditionRule extends Rule {

        /**
         * 
         */
        private static final long serialVersionUID = -6611707719913930725L;
        String name;
        Conditional condition;

        public MoveTowardConditionRule(String name, Conditional condition) {
            super(name);
            this.name = name;
            this.condition = condition;
        }

        public void execute(Agent a) {
            HostCell target = (HostCell) ((CellOccupant) a).getHostCell().findNearest(condition, false, ((Bion) a).getVision());
            if (target != null) {
                ((Bion) a).moveToward(target);
            }
        }
    };

    class MoveAwayConditionRule extends MoveTowardConditionRule {

        /**
         * 
         */
        private static final long serialVersionUID = 6754947700289724033L;

        public MoveAwayConditionRule(String name, Conditional condition) {
            super(name, condition);
        }

        public void execute(Agent a) {
            HostCell target = (HostCell) ((CellOccupant) a).getHostCell().findNearest(condition, false, ((Bion) a).getVision());
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
        private static final long serialVersionUID = -5706510956729622829L;
        String name;
        DataPoint point;

        public MoveTowardMaximumRule(String name, DataPoint point) {
            super(name);
            this.name = name;
            this.point = point;
        }

        public void execute(Agent a) {
            HostCell target = (HostCell) ((CellOccupant) a).getHostCell().findMaximumWithin(point, true, ((Bion) a).getVision());
            if (target != null) {
                ((Bion) a).moveToward(target);
            }
        }
    }

    class MoveAwayMaximumRule extends MoveTowardMaximumRule {

        /**
         * 
         */
        private static final long serialVersionUID = 6367023420101596506L;

        public MoveAwayMaximumRule(String name, DataPoint point) {
            super(name, point);
        }

        public void execute(Agent a) {
            HostCell target = (HostCell) ((CellOccupant) a).getHostCell().findMaximumWithin(point, true, ((Bion) a).getVision());
            if (target != null) {
                ((Bion) a).moveAway(target);
            }
        }
    }

    public final Rule MOVE_TOWARD_RED_ENERGY_RULE = new MoveTowardMaximumRule("Move Toward Red Energy", REDNESS);

    public final Rule MOVE_TOWARD_ORANGE_ENERGY_RULE = new MoveTowardMaximumRule("Move Toward Orange Energy", ORANGENESS);

    public final Rule MOVE_TOWARD_BLUE_ENERGY_RULE = new MoveTowardMaximumRule("Move Toward Blue Energy", BLUENESS);

    public final Rule MOVE_AWAY_RED_ENERGY_RULE = new MoveAwayMaximumRule("Move Away Red Energy", REDNESS);

    public final Rule MOVE_AWAY_ORANGE_ENERGY_RULE = new MoveAwayMaximumRule("Move Away Orange Energy", ORANGENESS);

    public final Rule MOVE_AWAY_BLUE_ENERGY_RULE = new MoveAwayMaximumRule("Move Away Blue Energy", BLUENESS);

    public final static Rule LEAVE_ENERGY_RULE = new Rule("Leave Energy") {
        /**
         * 
         */
        private static final long serialVersionUID = 6782138340566461812L;

        public void execute(Agent a) {
            ((Bion) a).leaveEnergy();
        }
    };

    public final static Rule CONSUME_RED_ENERGY_RULE = new Rule("Consume Red Energy") {
        /**
         * 
         */
        private static final long serialVersionUID = -3106863683233212775L;

        public void execute(Agent a) {
            ((Bion) a).consumeRedEnergy();
        }
    };

    public final static Rule CONSUME_ORANGE_ENERGY_RULE = new Rule("Consume Orange Energy") {
        /**
         * 
         */
        private static final long serialVersionUID = 2594875494941240478L;

        public void execute(Agent a) {
            ((Bion) a).consumeOrangeEnergy();
        }
    };

    public final static Rule CONSUME_BLUE_ENERGY_RULE = new Rule("Consume Blue Energy") {
        /**
         * 
         */
        private static final long serialVersionUID = 2833399959198318461L;

        public void execute(Agent a) {
            ((Bion) a).consumeBlueEnergy();
        }
    };

    public void createScape() {
        super.createScape();
        territory = new Scape(new Array2DMoore());
        territory.setName("Bionland");
        territory.setPrototypeAgent(new BionTile());
        ((Array2D) territory.getSpace()).setExtent(25, 25);
        add(territory);
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
        int popSize = (int) ((territory.getSize() * initialDensity) / 3.0f);
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
            private static final long serialVersionUID = 6523167126459296325L;

            public double getValue(Object o) {
                return ((Bion) o).getStoredEnergy();
            }
        });
    }

    public void createViews() {
        super.createViews();
        Overhead2DView mapView = new Overhead2DView("Map");
        mapView.setCellSize(15);
        territory.addView(mapView);
        mapView.getDrawSelection().clearSelection();
        mapView.getDrawSelection().setSelected(mapView.agents_image_cells_draw_feature, true);

        ChartView chart = new ChartView("Population");
        addView(chart);
        chart.addSeries("Average Red Stored Energy", Color.red);
        chart.addSeries("Average Orange Stored Energy", Color.orange);
        chart.addSeries("Average Blue Stored Energy", Color.blue);
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
}
