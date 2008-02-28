/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model;

import java.util.List;

import org.ascape.model.rule.Rule;
import org.ascape.model.space.Coordinate;
import org.ascape.model.space.CoordinateMutable;
import org.ascape.model.space.Location;
import org.ascape.util.Conditional;
import org.ascape.util.data.DataPoint;

/**
 * An agent that has a location in continuous space.
 *
 * @author Miles Parker
 * @version 3.0
 * @history 3.0 9/10/02 significant refactoring of scape model
 * @history 2.0 10/29/01 Many changes to support new continuous space functionality
 * @history 2.0 9/11/01 first in
 * @since 2.0
 */
public class LocatedAgent extends Agent implements Location {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * A rule causing the taget agent to move to a random location.
     * Move random location rules can only be applied to agents in continuous space or to cell occupants.
     */
    public static final Rule MOVE_RANDOM_LOCATION_RULE = new Rule("Move To Random Location") {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * Move to a random location in the lattice.
         * @param agent the playing agent
         * @see LocatedAgent#moveToRandomLocation
         */
        public void execute(Agent agent) {
            ((LocatedAgent) agent).moveToRandomLocation();
        }

        /**
         * Returns false. Movement should not usually cause agent removal.
         */
        public boolean isCauseRemoval() {
            return false;
        }
    };

    /**
     * A rule causing the taget agent to take a random walk. The agent attempts to take a random step
     * in a random direction. If the cell at the random location already has an occupant, nothing happens.
     * Note the difference between this rule and RANDOM_WALK_AVAILABLE_RULE.
     * Random walk rules can only be applied to agents in continuous space or to cell occupants.
     */
    public static final Rule RANDOM_WALK_RULE = new Rule("Random Walk") {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * Attempt to move one step in a random direction.
         * @param agent the playing agent
         * @see CellOccupant#randomWalk
         */
        public void execute(Agent agent) {
            ((LocatedAgent) agent).randomWalk();
        }

        /**
         * Returns false. Movement should not usually cause agent removal.
         */
        public boolean isCauseRemoval() {
            return false;
        }
    };

    /**
     * The coordinate location of this cell within the relevant scape.
     */
    protected Coordinate coordinate;

    protected int thisUpdate;

    /**
     * Default agent size. For now, agent image will simply by a circle, with radius of agentSize.
     */
    protected int agentSize = 5;

    public void initialize() {
        super.initialize();
        thisUpdate = 0;
    }

    /**
     * Indicate to all views of this cell that an update is needed. Should be called whenever
     * the cell state has changed in a way that might affect how it is drawn.
     */
    public void requestUpdate() {
        thisUpdate = getIteration();
    }

    /**
     * Indicate to all views of this cell that an update is needed next iteration. Called when
     * a paint action in the current iteration might need cleanup in the next cycle.
     */
    public void requestUpdateNext() {
        thisUpdate = getIteration() + 1;
    }

    /**
     * Has a view update been requested for this cell?
     */
    public boolean isUpdateNeeded(int within) {
        if (thisUpdate > getIteration() - within) {
            return true;
        }
        return false;
    }

    /**
     * Gets a coordinate the location of this cell within the relevant scape.
     * Warning, may be null for members of graphs.
     */
    public Coordinate getCoordinate() {
        if (getScape() != null && getScape().getSpace() instanceof CoordinateMutable) {
            ((CoordinateMutable) getScape().getSpace()).coordinateSweep();
        }
        return coordinate;
    }

    /**
     * Returns the extent of the nth dimension.
     */
    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    /**
     * Returns the closest agent.
     */
    public LocatedAgent findNearest() {
        return findNearest(null, false, Double.MAX_VALUE);
    }

    /**
     * Returns the closest agent within the specified distance from this agent.
     * @param distance the distance agents must be within to be included
     */
    public LocatedAgent findNearest(double distance) {
        return findNearest(null, false, distance);
    }

    /**
     * Returns the closest agent that meet some condition.
     * @param condition the condition the agent must meet to be included
     */
    public LocatedAgent findNearest(Conditional condition) {
        return findNearest(condition, false, Double.MAX_VALUE);
    }

    /**
     * Returns the closest agent within the specified distance from this agent that meet some condition.
     * @param condition the condition the agent must meet to be included
     * @param distance the distance agents must be within to be included
     */
    public LocatedAgent findNearest(Conditional condition, double distance) {
        return findNearest(condition, false, distance);
    }

    /**
     * Returns the closest agent within the specified distance from this agent that meet some condition.
     * @param condition the condition the agent must meet to be included
     * @param includeSelf if the calling agent should be included in the search
     * @param distance the distance agents must be within to be included
     */
    public LocatedAgent findNearest(Conditional condition, boolean includeSelf, double distance) {
        return getScape().findNearest(this.getCoordinate(), condition, includeSelf, distance);
    }

    /**
     * Returns all agents within the specified distance of the agent,
     * excluding this agent.
     * @param distance the distance agents must be within to be included
     */
    public List findWithin(double distance) {
        return findWithin(null, distance);
    }

    /**
     * Returns all agents within the specified distance of the agent,
     * excluding this agent.
     * @param distance the distance agents must be within to be included
     */
    public List findWithin(double distance, boolean includeSelf) {
        return findWithin(null, includeSelf, distance);
    }

    /**
     * Returns all agents within the specified distance of the agent that meet some condition,
     * excluding this agent.
     * @param condition the condition the agent must meet to be included
     * @param distance the distance agents must be within to be included
     */
    public List findWithin(Conditional condition, double distance) {
        return findWithin(condition, false, distance);
    }

    /**
     * Returns all agents within the specified distance of the agent that meet some condition.
     * @param includeSelf if this agent should be included in the search
     * @param condition the condition the agent must meet to be included
     * @param distance the distance agents must be within to be included
     */
    public List findWithin(Conditional condition, boolean includeSelf, double distance) {
        return getScape().findWithin(this.getCoordinate(), condition, includeSelf, distance);
    }

    public LocatedAgent findMaximumWithin(DataPoint data, boolean includeSelf, double distance) {
        return getScape().findMaximumWithin(this.getCoordinate(), data, null, includeSelf, distance);
    }

    /**
     * Returns the number of agents within the specified distance of the agent.
     * @param distance the distance agents must be within to be included
     */
    public int countWithin(double distance) {
        return countWithin(null, distance);
    }

    /**
     * Returns the number of agents within the specified distance of the agent that meet some condition.
     * @param condition the condition the agent must meet to be included
     * @param distance the distance agents must be within to be included
     */
    public int countWithin(Conditional condition, double distance) {
        return countWithin(condition, false, distance);
    }

    /**
     * Returns the number of agents within the specified distance of the agent that meet some condition.
     * @param includeSelf whether or not this agent should be included in the count
     * @param condition the condition the agent must meet to be included
     * @param distance the distance agents must be within to be included
     */
    public int countWithin(Conditional condition, boolean includeSelf, double distance) {
        return getScape().countWithin(this.getCoordinate(), condition, includeSelf, distance);
    }

    /**
     * Returns the number of agents within the specified distance of the agent.
     * @param distance the distance agents must be within to be included
     */
    public boolean hasWithin(double distance) {
        return hasWithin(null, distance);
    }

    /**
     * Returns the number of agents within the specified distance of the agent that meet some condition.
     * @param condition the condition the agent must meet to be included
     * @param distance the distance agents must be within to be included
     */
    public boolean hasWithin(Conditional condition, double distance) {
        return getScape().hasWithin(condition, false, distance);
    }


    /**
     * Returns the number of agents within the specified distance of the agent that meet some condition.
     * @param includeSelf if this agent should be included in the search
     * @param condition the condition the agent must meet to be included
     * @param distance the distance agents must be within to be included
     */
    public boolean hasWithin(Conditional condition, boolean includeSelf, double distance) {
        return getScape().hasWithin(this.getCoordinate(), condition, includeSelf, distance);
    }

    /**
     * Moves distance 1.0 toward the specified agent.
     * @param target the agent to move toward
     */
    public void moveToward(LocatedAgent target) {
        moveToward(target.getCoordinate());
    }

    /**
     * Moves distance 1.0 toward the specified coordinate.
     * @param target the agent to move toward
     */
    public void moveToward(Coordinate target) {
        moveToward(target, 1.0);
    }

    /**
     * Moves distance 1.0 toward the specified coordinate.
     * @param target the agent to move toward
     * @param distance the distance to move
     */
    public void moveToward(Coordinate target, double distance) {
        getScape().moveToward(this, target, distance);
    }

    /**
     * Moves distance 1.0 toward the specified agent.
     * @param target the agent to move toward
     */
    public void moveAway(LocatedAgent target) {
        moveAway(target.getCoordinate());
    }

    /**
     * Moves distance 1.0 toward the specified coordinate.
     * @param target the agent to move toward
     */
    public void moveAway(Coordinate target) {
        moveAway(target, 1.0);
    }

    /**
     * Moves distance 1.0 toward the specified coordinate.
     * @param target the agent to move toward
     * @param distance the distance to move
     */
    public void moveAway(Coordinate target, double distance) {
        getScape().moveAway(this, target, distance);
    }

    /**
     * Moves this agent to a random unoccupied location on the host scape.
     * It is an error to call this method on a cell in a non-continous scape unless the cell is a cell occupant.
     */
    public void moveToRandomLocation() {
        Coordinate coord = getScape().findRandomCoordinate();
        if (coord != null) {
            moveTo(coord);
        } else {
            System.err.println("Warning: no location to move to. Killing agent.");
            die();
        }
    }

    /**
     * Moves a set distance in a random direction.
     * It is an error to call this method on a cell in a non-continuous space unless the cell is a cell occupant.
     * (distance 1 may be replaced with a veolcity.
     */
    public void randomWalk() {
        throw new RuntimeException("Tried to move an agent in a scape that does not allow movement.");
    }

    /**
     * Moves to the coordiante specified.
     * It is an error to call this method on a cell in a non-continuous space unless the cell is a cell occupant.
     * In other words, the default behavior of this method is to throw an exception.
     * @param coordinate
     */
    public void moveTo(Coordinate coordinate) {
        setCoordinate(coordinate);
    }

    /**
     * Return the distance between this agent and the supplied agent.
     */
    public double calculateDistance(LocatedAgent target) {
        return getScape().calculateDistance(this, target);
    }

    /**
     * Return the distance between this agent and the supplied coordinate.
     */
    public double calculateDistance(Coordinate target) {
        return getScape().calculateDistance(this.getCoordinate(), target);
    }

    public int getAgentSize() {
        return agentSize;
    }

    public void setAgentSize(int agentSize) {
        this.agentSize = agentSize;
    }

    /**
     * A string representation of this cell.
     */
    public String toString() {
        if (getName() != null) {
            if (coordinate != null) {
                return getName() + " " + coordinate;
            } else {
                return getName();
            }
        } else {
            if (coordinate != null) {
                return "Agent at " + coordinate;
            } else {
                return "Agent";
            }
        }
    }
}
