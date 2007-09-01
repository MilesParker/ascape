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
 *          All rights reserved worldwide.
 *
 * U.S. GOVERNMENT END USERS. The software is a "commercial item," as that term is defined in 48 C.F.R. 2.101,
 * consisting of "commercial computer software" and "commercial computer software documentation," as such terms are
 * used in 48 C.F.R. 12.212. Consistent with 48 C.F.R. 12.212 and 48 C.F.R. 227.7202-1 through 227.7202-4, and other
 * relevant sections of the Code of Federal Regulations, as applicable all U.S. Government End Users acquire the
 * software with only those rights set forth in the license agreement. This software includes trade secrets that
 * are exempt from disclosure under the Freedom of Information Act.
 *
 */



package org.ascape.examples.bionland;

import java.awt.Color;
import java.awt.Image;

import org.ascape.model.Agent;
import org.ascape.model.CellOccupant;
import org.ascape.model.HostCell;
import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.rule.Diffusable;
import org.ascape.model.rule.ParameterizedDiffusion;
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

public class Model_K extends Scape {

    private Scape territory;

    private Scape redBions;

    protected Scape orangeBions;

    private Scape blueBions;

    private double initialDensity = 0.28f;

    private double initialStoredEnergy = 1.5f;

    private double bionEnergyLeakage = 0.02f;

    private double bionEnergyConsumption = 0.015f;

    private double bionEnergyGain = 1.0f;

    private int minVision = 8;

    private int maxVision = 8;

    private class BionTile extends HostCell implements Diffusable {

        public double redEnergy;
        public double orangeEnergy;
        public double blueEnergy;

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

        private double diffusionTemp;

        public double getDiffusionTemp() {
            return diffusionTemp;
        }

        public void setDiffusionTemp(double diffusionTemp) {
            this.diffusionTemp = diffusionTemp;
        }
    }

    public static DataPointConcrete REDNESS = new DataPointConcrete() {
        public double getValue(Object o) {
            return ((BionTile) o).getRedEnergy();
        }
    };

    public static DataPointConcrete ORANGENESS = new DataPointConcrete() {
        public double getValue(Object o) {
            return ((BionTile) o).getOrangeEnergy();
        }
    };

    public static DataPointConcrete BLUENESS = new DataPointConcrete() {
        public double getValue(Object o) {
            return ((BionTile) o).getBlueEnergy();
        }
    };

    private final ParameterizedDiffusion diffuse_red_energy = new ParameterizedDiffusion("Diffuse Red Energy") {
        public double getDiffusionValue(Agent agent) {
            return ((BionTile) agent).getRedEnergy();
        }

        public void setDiffusionValue(Agent agent, double value) {
            ((BionTile) agent).setRedEnergy(value);
        }
    };

    private final ParameterizedDiffusion diffuse_orange_energy = new ParameterizedDiffusion("Diffuse Orange Energy") {
        public double getDiffusionValue(Agent agent) {
            return ((BionTile) agent).getOrangeEnergy();
        }

        public void setDiffusionValue(Agent agent, double value) {
            ((BionTile) agent).setOrangeEnergy(value);
        }
    };

    private final ParameterizedDiffusion diffuse_blue_energy = new ParameterizedDiffusion("Diffuse Blue Energy") {
        public double getDiffusionValue(Agent agent) {
            return ((BionTile) agent).getBlueEnergy();
        }

        public void setDiffusionValue(Agent agent, double value) {
            ((BionTile) agent).setBlueEnergy(value);
        }
    };

    private class Bion extends CellOccupant {

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
        public boolean meetsCondition(Object o) {
            return (((HostCell) o).getOccupant() instanceof RedBion);
        }
    };

    public static Conditional CONTAINS_ORANGE = new Conditional() {
        public boolean meetsCondition(Object o) {
            return (((HostCell) o).getOccupant() instanceof OrangeBion);
        }
    };

    public static Conditional CONTAINS_BLUE = new Conditional() {
        public boolean meetsCondition(Object o) {
            return (((HostCell) o).getOccupant() instanceof BlueBion);
        }
    };

    class MoveTowardConditionRule extends Rule {

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
        public void execute(Agent a) {
            ((Bion) a).leaveEnergy();
        }
    };

    public final static Rule CONSUME_RED_ENERGY_RULE = new Rule("Consume Red Energy") {
        public void execute(Agent a) {
            ((Bion) a).consumeRedEnergy();
        }
    };

    public final static Rule CONSUME_ORANGE_ENERGY_RULE = new Rule("Consume Orange Energy") {
        public void execute(Agent a) {
            ((Bion) a).consumeOrangeEnergy();
        }
    };

    public final static Rule CONSUME_BLUE_ENERGY_RULE = new Rule("Consume Blue Energy") {
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
        chart.addSeries("Count Red Stored Energy", Color.red);
        chart.addSeries("Count Orange Stored Energy", Color.orange);
        chart.addSeries("Count Blue Stored Energy", Color.blue);
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
        //red stands in for all diffusion..
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
