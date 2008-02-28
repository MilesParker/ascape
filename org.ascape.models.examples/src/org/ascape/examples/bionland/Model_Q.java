/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */


package org.ascape.examples.bionland;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;

import org.ascape.model.Agent;
import org.ascape.model.LocatedAgent;
import org.ascape.model.Scape;
import org.ascape.model.rule.Rule;
import org.ascape.model.space.Continuous2D;
import org.ascape.model.space.Coordinate2DContinuous;
import org.ascape.model.space.SubContinuous2D;
import org.ascape.util.Conditional;
import org.ascape.util.vis.ImageFeatureFixed;
import org.ascape.view.vis.Overhead2DContinuousView;

public class Model_Q extends Scape {

    /**
     * 
     */
    private static final long serialVersionUID = 6908142220214361373L;

    private Scape territory;

    private Scape redBions;

    private Scape orangeBions;

    private Scape blueBions;

    private int initialSize = 40;

    private int minVision = 60;

    private int maxVision = 100;

    private int minVelocity = 7;

    private int maxVelocity = 13;

    private class Bion extends LocatedAgent {

        /**
         * 
         */
        private static final long serialVersionUID = 1465257738623963424L;
        protected double vision;
        protected double velocity;

        public void initialize() {
            super.initialize();
            vision = randomInRange(minVision, maxVision);
            velocity = randomInRange(minVelocity, maxVelocity);
            setAgentSize(10);
        }

        public double getVision() {
            return vision;
        }

        public void setVision(double vision) {
            this.vision = vision;
        }

        public double getVelocity() {
            return velocity;
        }

        public void setVelocity(double velocity) {
            this.velocity = velocity;
        }
    }

    class RedBion extends Bion {

        /**
         * 
         */
        private static final long serialVersionUID = -8087225336656891911L;

        public Color getColor() {
            return Color.red;
        }

        public Image getImage() {
            return ImageFeatureFixed.redBall;
        }

        public String getName() {
            return "Red Bion at " + RedBion.this.getCoordinate();
        }
    }

    class OrangeBion extends Bion {

        /**
         * 
         */
        private static final long serialVersionUID = -6419258005253043572L;

        public Color getColor() {
            return Color.orange;
        }

        public Image getImage() {
            return ImageFeatureFixed.orangeBall;
        }

        public String getName() {
            return "Orange Bion at " + OrangeBion.this.getCoordinate();
        }
    }

    class BlueBion extends Bion {

        /**
         * 
         */
        private static final long serialVersionUID = 7255175846367767254L;

        public Color getColor() {
            return Color.blue;
        }

        public Image getImage() {
            return ImageFeatureFixed.blueBall;
        }

        public String getName() {
            return "Blue Bion at " + BlueBion.this.getCoordinate();
        }
    }

    class MoveTowardConditionRule extends Rule {

        /**
         * 
         */
        private static final long serialVersionUID = 7232297596488751244L;
        String name;
        Conditional condition;

        public MoveTowardConditionRule(String name, Conditional condition) {
            super(name);
            this.name = name;
            this.condition = condition;
        }

        public void execute(Agent a) {
            LocatedAgent target = territory.findNearest(((LocatedAgent) a).getCoordinate(), condition, false, ((Bion) a).getVision());
            if (target != null) {
                ((Bion) a).moveToward(target.getCoordinate(), ((Bion) a).getVelocity());
            }
        }
    };

    class MoveAwayConditionRule extends MoveTowardConditionRule {

        /**
         * 
         */
        private static final long serialVersionUID = -9171389514833109396L;

        public MoveAwayConditionRule(String name, Conditional condition) {
            super(name, condition);
        }

        public void execute(Agent a) {
            LocatedAgent target = territory.findNearest(((LocatedAgent) a).getCoordinate(), condition, false, ((Bion) a).getVision());
            if (target != null) {
                ((Bion) a).moveAway(target.getCoordinate(), ((Bion) a).getVelocity());
            }
        }
    };

    public static Conditional CONTAINS_RED = new Conditional() {
        /**
         * 
         */
        private static final long serialVersionUID = -3845517710729322797L;

        public boolean meetsCondition(Object o) {
            return (o instanceof RedBion);
        }
    };

    public static Conditional CONTAINS_ORANGE = new Conditional() {
        /**
         * 
         */
        private static final long serialVersionUID = 8377185056087828144L;

        public boolean meetsCondition(Object o) {
            return (o instanceof OrangeBion);
        }
    };

    public static Conditional CONTAINS_BLUE = new Conditional() {
        /**
         * 
         */
        private static final long serialVersionUID = 1513704421006648535L;

        public boolean meetsCondition(Object o) {
            return (o instanceof BlueBion);
        }
    };

    public final Rule MOVE_TOWARD_RED_RULE = new MoveTowardConditionRule("Move Toward Red", CONTAINS_RED);

    public final Rule MOVE_TOWARD_ORANGE_RULE = new MoveTowardConditionRule("Move Toward Orange", CONTAINS_ORANGE);

    public final Rule MOVE_TOWARD_BLUE_RULE = new MoveTowardConditionRule("Move Toward Blue", CONTAINS_BLUE);

    public final Rule MOVE_AWAY_RED_RULE = new MoveAwayConditionRule("Move Away Red", CONTAINS_RED);

    public final Rule MOVE_AWAY_ORANGE_RULE = new MoveAwayConditionRule("Move Away Orange", CONTAINS_ORANGE);

    public final Rule MOVE_AWAY_BLUE_RULE = new MoveAwayConditionRule("Move Away Blue", CONTAINS_BLUE);

    public void createScape() {
        super.createScape();
        territory = new Scape(new Continuous2D());
        territory.setName("Bionland");
        territory.setExtent(new Coordinate2DContinuous(600.0, 600.0));
        Bion protoBion = new Bion();
        protoBion.initialize();
        territory.setPrototypeAgent(protoBion);
        territory.setAutoCreate(false);
        territory.setPeriodic(true);
        add(territory);
        territory.setExecutionOrder(RULE_ORDER);
        redBions = new Scape(new SubContinuous2D());
        createBions(redBions, new RedBion(), "Red");
        redBions.getRules().setSelected(MOVE_TOWARD_ORANGE_RULE, true);
        redBions.getRules().setSelected(MOVE_AWAY_BLUE_RULE, true);
        orangeBions = new Scape(new SubContinuous2D());
        createBions(orangeBions, new OrangeBion(), "Orange");
        orangeBions.getRules().setSelected(MOVE_TOWARD_BLUE_RULE, true);
        orangeBions.getRules().setSelected(MOVE_AWAY_RED_RULE, true);
        blueBions = new Scape(new SubContinuous2D());
        createBions(blueBions, new BlueBion(), "Blue");
        blueBions.getRules().setSelected(MOVE_TOWARD_RED_RULE, true);
        blueBions.getRules().setSelected(MOVE_AWAY_ORANGE_RULE, true);
    }

    public void initialize() {
        super.initialize();
        territory.clear();
        redBions.setSize(initialSize);
        orangeBions.setSize(initialSize);
        blueBions.setSize(initialSize);
    }

    protected void createBions(Scape bions, LocatedAgent protoAgent, String name) {
        bions.setSuperScape(territory);
        bions.setName(name);
        bions.setPrototypeAgent(protoAgent);
        bions.addInitialRule(MOVE_RANDOM_LOCATION_RULE);
        //bions.addRule(RANDOM_WALK_RULE);
        bions.addRule(MOVE_TOWARD_RED_RULE, false);
        bions.addRule(MOVE_TOWARD_ORANGE_RULE, false);
        bions.addRule(MOVE_TOWARD_BLUE_RULE, false);
        bions.addRule(MOVE_AWAY_RED_RULE, false);
        bions.addRule(MOVE_AWAY_ORANGE_RULE, false);
        bions.addRule(MOVE_AWAY_BLUE_RULE, false);
        add(bions);
    }

    public void createViews() {
        super.createViews();

        Overhead2DContinuousView mapView = new Overhead2DContinuousView();
        mapView.setPreferredSize(new Dimension(600, 600));
        territory.addView(mapView);
        mapView.getDrawSelection().clearSelection();
        mapView.getDrawSelection().setSelected(mapView.agents_image_cells_draw_feature, true);
    }

    public int getInitialPopulationSize() {
        return initialSize;
    }

    public void setInitialPopulationSize(int initialSize) {
        this.initialSize = initialSize;
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

    public int getMinVelocity() {
        return minVelocity;
    }

    public void setMinVelocity(int minVelocity) {
        this.minVelocity = minVelocity;
    }

    public int getMaxVelocity() {
        return maxVelocity;
    }

    public void setMaxVelocity(int maxVelocity) {
        this.maxVelocity = maxVelocity;
    }

    public boolean isPeriodic() {
        return territory.isPeriodic();
    }

    public void setPeriodic(boolean periodic) {
        territory.setPeriodic(periodic);
    }
}
