/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.examples.boids;

import java.awt.Color;
import java.awt.Dimension;

import org.ascape.model.Agent;
import org.ascape.model.Scape;
import org.ascape.model.rule.Rule;
import org.ascape.model.space.Continuous2D;
import org.ascape.model.space.Coordinate2DContinuous;
import org.ascape.model.space.SubContinuous2D;
import org.ascape.examples.boids.view.BoidsView;
import org.ascape.view.vis.Overhead2DContinuousView;
public class BaseModel extends Scape {

    /**
     * 
     */
    private static final long serialVersionUID = 720243153187275842L;

    private Scape world;

    private Scape boids;

    private Scape redBoids;

    private Scape blueBoids;

    private Scape obstacles;

    private BoidsView view;

    private int numRedBoids = 25;

    private int numBlueBoids = 25;

    private double initialVelocity = 1.f;

    private double initialHeadingRange = 3.5f;

    private double initialObstacleAvoidanceRange = 2.5f;

    private double initialClumpingRange = 8.f;

    private double initialFlockRange = 15.f;

    private double initialCollisionRange = 1f;

    private int agentSize = 1;

    /**
     * Steer towards the average heading of local neighbors.
     */
    private final static Rule ALIGNMENT_RULE = new Rule("Alignment Rule") {
        /**
         * 
         */
        private static final long serialVersionUID = 6553285310121032975L;

        public void execute(Agent a) {
            ((Boid) a).alignHeading();
        }
    };

    /**
     * Steer to avoid crowding local neighbors.
     */
    private final static Rule COLLISION_RULE = new Rule("Avoid Collisions Rule") {
        /**
         * 
         */
        private static final long serialVersionUID = 8257554942449032768L;

        public void execute(Agent a) {
            ((Boid) a).avoidCollisions();
        }
    };

    private final static Rule AVOID_OBSTACLES_RULE = new Rule("Avoid Obstacles Rule") {
        /**
         * 
         */
        private static final long serialVersionUID = 647255445252501513L;

        public void execute(Agent a) {
            ((Boid) a).avoidObstacles();
        }
    };

    /**
     * Steer to move towards the average position of local neighbors.
     */
    private final static Rule COHESION_RULE = new Rule("Cohesion Rule") {
        /**
         * 
         */
        private static final long serialVersionUID = -5723083430731638115L;

        public void execute(Agent a) {
            ((Boid) a).cohesion();
        }
    };

    public void createScape() {
        super.createScape();
        setName("Craig Reynold's \"Boids\"");

        String boidsDisclaimer = "This is a simple implementation of the \"Boids\" model by Craig Reynolds.";
        boidsDisclaimer += "\nFor more information, please see his paper \"Flocks, Herds and Schools: A Distributed Behavior Model\"";
        boidsDisclaimer += " \nat http:\\\\www.red3d.com\\cwr\\papers\\1987\\boids.html.";
        setDescription(boidsDisclaimer);

        world = new Scape(new Continuous2D());
        world.setExtent(new Coordinate2DContinuous(50, 50));
        Boid protoBoid = new Boid();
        protoBoid.setAgentSize(getAgentSize());
        world.setPrototypeAgent(protoBoid);
        world.setAutoCreate(false);
        world.setPeriodic(true);
        world.setName("World");
        add(world);

        boids = new Scape(new SubContinuous2D());
        boids.setName("Boids");
        boids.setAutoCreate(false);
        Boid proto = new Boid();
        proto.setAgentSize(getAgentSize());
        boids.setPrototypeAgent(proto);
        add(boids);
        boids.setSuperScape(world);
        boids.addInitialRule(MOVE_RANDOM_LOCATION_RULE);
        boids.addRule(ITERATE_RULE);

        redBoids = new Scape(new SubContinuous2D());
        redBoids.setName("Red Boids");
        Boid redProtoBoid = new Boid();
        redProtoBoid.setTeam(Boid.RED_TEAM);
        redBoids.setPrototypeAgent(redProtoBoid);
        add(redBoids);
        redBoids.setSuperScape(boids);
        redBoids.addRule(AVOID_OBSTACLES_RULE);
        redBoids.addRule(COLLISION_RULE, false);
        redBoids.addRule(COHESION_RULE, false);
        redBoids.addRule(ALIGNMENT_RULE, false);
        redBoids.addRule(MOVEMENT_RULE);

        blueBoids = new Scape(new SubContinuous2D());
        blueBoids.setName("Blue Boids");
        Boid blueProtoBoid = new Boid();
        blueProtoBoid.setTeam(Boid.BLUE_TEAM);
        blueBoids.setPrototypeAgent(blueProtoBoid);
        add(blueBoids);
        blueBoids.setSuperScape(boids);
        blueBoids.addRule(AVOID_OBSTACLES_RULE);
        blueBoids.addRule(COLLISION_RULE);
        blueBoids.addRule(COHESION_RULE);
        blueBoids.addRule(ALIGNMENT_RULE);
        blueBoids.addRule(MOVEMENT_RULE);

        obstacles = new Scape(new SubContinuous2D());
        obstacles.setName("Obstacles");
        obstacles.setPrototypeAgent(new Obstacle());
        obstacles.setAutoCreate(false);
        add(obstacles);
        obstacles.setSuperScape(world);
        obstacles.addInitialRule(MOVE_RANDOM_LOCATION_RULE);
    }

    public void createViews() {
        super.createViews();
        view = new BoidsView();
        view.setPreferredSize(new Dimension(600, 600));
        view.setBackground(Color.white);
        view.setName("Continuous Overhead View");
        world.addView(view);
        System.out.println("");
        System.out.println("To place an obstacle, shift-click in the overhead view.");
    }

    public void initialize() {
        super.initialize();
        redBoids.setSize(numRedBoids);
        blueBoids.setSize(numBlueBoids);
        world.clear();
        obstacles.clear(); //<--- this shouldnt be necessary
    }

    public Scape getWorld() {
        return world;
    }

    public int getNumRedBoids() {
        return numRedBoids;
    }

    public void setNumRedBoids(int numRedBoids) {
        this.numRedBoids = numRedBoids;
    }

    public int getNumBlueBoids() {
        return numBlueBoids;
    }

    public void setNumBlueBoids(int numBlueBoids) {
        this.numBlueBoids = numBlueBoids;
    }

    public double getInitialVelocity() {
        return initialVelocity;
    }

    public void setInitialVelocity(double initialVelocity) {
        this.initialVelocity = initialVelocity;
    }

    public double getInitialHeadingRange() {
        return initialHeadingRange;
    }

    public void setInitialHeadingRange(double initialHeadingRange) {
        this.initialHeadingRange = initialHeadingRange;
    }

    public double getInitialClumpingRange() {
        return initialClumpingRange;
    }

    public void setInitialClumpingRange(double initialClumpingRange) {
        this.initialClumpingRange = initialClumpingRange;
    }

    public double getInitialCollisionRange() {
        return initialCollisionRange;
    }

    public double getInitialFlockRange() {
        return initialFlockRange;
    }

    public void setInitialFlockRange(double initialFlockRange) {
        this.initialFlockRange = initialFlockRange;
    }

    public void setInitialCollisionRange(double initialCollisionRange) {
        this.initialCollisionRange = initialCollisionRange;
    }

    public Overhead2DContinuousView getView() {
        return view;
    }

    public Scape getObstacles() {
        return obstacles;
    }

    public double getInitialObstacleAvoidanceRange() {
        return initialObstacleAvoidanceRange;
    }

    public void setInitialObstacleAvoidanceRange(double initialObstacleAvoidanceRange) {
        this.initialObstacleAvoidanceRange = initialObstacleAvoidanceRange;
    }

    public int getAgentSize() {
        return agentSize;
    }

    public void setAgentSize(int agentSize) {
        this.agentSize = agentSize;
    }

    public Scape getBlueBoids() {
        return blueBoids;
    }

    public Scape getRedBoids() {
        return redBoids;
    }
}
