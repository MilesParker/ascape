/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.pd;

import java.awt.Color;
import java.awt.Image;

import org.ascape.model.Agent;
import org.ascape.model.CellOccupant;
import org.ascape.model.Scape;
import org.ascape.util.data.StatCollectorCondCSA;
import org.ascape.util.vis.ImageFeatureFixed;


/**
 * A player in the prisoner's dilemma game.
 *
 * @author Miles Parker
 * @version 1.0
 **/
public class Player extends CellOccupant {

    /**
     * 
     */
    private static final long serialVersionUID = 2683265117149321229L;

    class AgentWealthStatCollector extends StatCollectorCondCSA {

        /**
         * 
         */
        private static final long serialVersionUID = -5551487009721295781L;
        private Agent agent;
        private int number;

        //private static int currentAgentNumber = 0;
        public AgentWealthStatCollector(Agent agent) {
            this.agent = agent;
            //number = currentAgentNumber;
            //currentAgentNumber++;
        }

        public boolean meetsCondition(Object object) {
            return (agent == object);
        }

        public double getValue(Object object) {
            System.out.println(((Player) object).wealth);
            return ((Player) object).wealth;
        }

        public String getName() {
            return "Agent " + number + " Wealth";
        }
    }

    /**
     * The agent's current (fixed for now) strategy.
     */
    protected int strategy;

    /**
     * The agent's current age.
     */
    protected int age;

    /**
     * The agent's death age.
     */
    //protected int deathAge;

    /**
     * The agent's current wealth.
     */
    protected int wealth = 6;

    /**
     * The agent's last period wealth.
     */
    @SuppressWarnings("unused")
    private int lpWealth = 0;

    /**
     * Begining population values. Cooperation is random draw, coordinate placement is random
     * in scape. Age is random to maximum age.
     */
    public void initialize() {
        super.initialize();
        if (randomIs()) {
            strategy = PD2D.COOPERATE;
        } else {
            strategy = PD2D.DEFECT;
        }
        age = randomInRange(0, ((PD2D) scape.getRoot()).getDeathAge());
        wealth = ((PD2D) scape.getRoot()).getInitialWealth();
    }

    /**
     */
    public void scapeCreated() {
        getScape().addInitialRule(MOVE_RANDOM_LOCATION_RULE);
        getScape().addRule(RANDOM_WALK_RULE);
        getScape().addRule(PLAY_RANDOM_NEIGHBOR_RULE);
        getScape().addRule(PLAY_NEIGHBORS_RULE, false);
        getScape().addRule(PLAY_OTHER, false);
        getScape().addRule(FISSIONING_RULE);
        getScape().addRule(UPDATE_RULE);
        getScape().addRule(DEATH_RULE);
    }

    public void update() {
        //wealth--;
        lpWealth = wealth;
        age++;
    }

    public boolean deathCondition() {
        return ((getWealth() < 0) || (getAge() > ((PD2D) getScape().getRoot()).getDeathAge()));
    }

    public boolean fissionCondition() {
        return (wealth > ((PD2D) scape.getRoot()).getFissionWealth());
    }

    public void fission() {
        if (getHostCell().isNeighborAvailable()) {
            Player child = (Player) this.clone();
            getScape().add(child);
            child.moveTo(getHostCell().findRandomAvailableNeighbor());
            child.wealth = ((PD2D) scape.getRoot()).getInheiritedWealth();
            child.age = 0;
            wealth -= ((PD2D) scape.getRoot()).getInheiritedWealth();
            if (getRandom().nextFloat() < ((PD2D) scape.getRoot()).getMutationRate()) {
                if (randomIs()) {
                    child.strategy = PD2D.COOPERATE;
                } else {
                    child.strategy = PD2D.DEFECT;
                }
            } else {
                child.strategy = this.strategy;
            }
        }
    }

    /**
     * Play one round of the game with another agent.
     */
    public void play(Agent partner) {
        if (this.strategy == PD2D.COOPERATE) {
            if (((Player) partner).strategy == PD2D.COOPERATE) {
                this.wealth += ((PD2D) scape.getRoot()).getPayoff_C_C();
                ((Player) partner).wealth += ((PD2D) scape.getRoot()).getPayoff_C_C();
            } else { //partner defects
                this.wealth += ((PD2D) scape.getRoot()).getPayoff_C_D();
                ((Player) partner).wealth += ((PD2D) scape.getRoot()).getPayoff_D_C();
            }
        } else { //this defects
            if (((Player) partner).strategy == PD2D.COOPERATE) {
                this.wealth += ((PD2D) scape.getRoot()).getPayoff_D_C();
                ((Player) partner).wealth += ((PD2D) scape.getRoot()).getPayoff_C_D();
            } else { //partner defects
                this.wealth += ((PD2D) scape.getRoot()).getPayoff_D_D();
                ((Player) partner).wealth += ((PD2D) scape.getRoot()).getPayoff_D_D();
            }
        }
    }

    /**
     * Sets the agent scape that all scape of this class will belong to.
     **/
    public void setScape(Scape scape) {
        this.scape = scape;
    }

    /**
     * The color to paint this agent; blue if cooperate, red if defect.
     */
    public Color getColor() {
        if (strategy == PD2D.COOPERATE) {
            return Color.blue;
        } else {
            return Color.red;
        }
    }

    /**
     * The image to draw this agent with; blue if cooperate, red if defect.
     */
    public Image getImage() {
        if (strategy == PD2D.COOPERATE) {
            return ImageFeatureFixed.blueBall;
        } else {
            return ImageFeatureFixed.redBall;
        }
    }

    public int getWealth() {
        return wealth;
    }

    public void setWealth(int wealth) {
        this.wealth = wealth;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getStrategy() {
        return strategy;
    }

    /**
     * A small string representation of this agent.
     */
    public String getName() {
        return (strategy == PD2D.COOPERATE ? "Cooperator " : "Defector ");
    }
}
