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

import org.ascape.model.Agent;
import org.ascape.model.CellOccupant;
import org.ascape.model.HostCell;
import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.rule.Rule;
import org.ascape.model.space.Array2D;
import org.ascape.model.space.Array2DMoore;
import org.ascape.util.Conditional;
import org.ascape.view.vis.Overhead2DView;

public class Model_E extends Scape {

    private Scape territory;

    private Scape redBions;

    private Scape orangeBions;

    private Scape blueBions;

    private double initialDensity = 0.28f;

    private double bionEnergyLeakage = 0.02f;

    private double bionEnergyConsumption = 0.015f;

    private int minVision = 8;

    private int maxVision = 8;

    private class BionTile extends HostCell {

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
    }

    private class Bion extends CellOccupant {

        protected int vision;

        public void initialize() {
            super.initialize();
            vision = randomInRange(minVision, maxVision);
        }

        public void leaveEnergy() {
        }

        public void consumeRedEnergy() {
            double energy = Math.min(((BionTile) getHostCell()).getRedEnergy(), bionEnergyConsumption);
            ((BionTile) getHostCell()).setRedEnergy(((BionTile) getHostCell()).getRedEnergy() - energy);
        }

        public void consumeOrangeEnergy() {
            double energy = Math.min(((BionTile) getHostCell()).getOrangeEnergy(), bionEnergyConsumption);
            ((BionTile) getHostCell()).setOrangeEnergy(((BionTile) getHostCell()).getOrangeEnergy() - energy);
        }

        public void consumeBlueEnergy() {
            double energy = Math.min(((BionTile) getHostCell()).getBlueEnergy(), bionEnergyConsumption);
            ((BionTile) getHostCell()).setBlueEnergy(((BionTile) getHostCell()).getBlueEnergy() - energy);
        }

        public int getVision() {
            return vision;
        }

        public void setVision(int vision) {
            this.vision = vision;
        }
    }

    class RedBion extends Bion {

        public Color getColor() {
            return Color.red;
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

        public void leaveEnergy() {
            super.leaveEnergy();
            ((BionTile) getHostCell()).setOrangeEnergy(Math.min(((BionTile) getHostCell()).getOrangeEnergy() + bionEnergyLeakage, 1.0f));
        }
    }

    class BlueBion extends Bion {

        public Color getColor() {
            return Color.blue;
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
        redBions = new Scape();
        createBions(redBions, new RedBion(), "Red");
        redBions.getRules().setSelected(MOVE_AWAY_BLUE_RULE, true);
        redBions.getRules().setSelected(CONSUME_BLUE_ENERGY_RULE, true);
        redBions.getRules().setSelected(CONSUME_ORANGE_ENERGY_RULE, true);
        orangeBions = new Scape();
        createBions(orangeBions, new OrangeBion(), "Orange");
        orangeBions.getRules().setSelected(MOVE_AWAY_RED_RULE, true);
        orangeBions.getRules().setSelected(CONSUME_RED_ENERGY_RULE, true);
        orangeBions.getRules().setSelected(CONSUME_BLUE_ENERGY_RULE, true);
        blueBions = new Scape();
        createBions(blueBions, new BlueBion(), "Blue");
        blueBions.getRules().setSelected(MOVE_AWAY_ORANGE_RULE, true);
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
        add(bions);
    }

    public void createViews() {
        super.createViews();
        Overhead2DView mapView = new Overhead2DView("Map");
        territory.addView(mapView);
    }

    public double getInitialPopulationDensity() {
        return initialDensity;
    }

    public void setInitialPopulationDensity(double initialDensity) {
        this.initialDensity = initialDensity;
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
