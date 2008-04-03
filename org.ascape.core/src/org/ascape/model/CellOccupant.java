/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model;

import java.util.Iterator;
import java.util.List;

import org.ascape.model.rule.Rule;
import org.ascape.model.space.Coordinate;
import org.ascape.model.space.Discrete;
import org.ascape.model.space.Location;
import org.ascape.model.space.Node;
import org.ascape.util.Conditional;

/**
 * An occupant of a cell within a lattice.
 *
 * @author Miles Parker
 * @version 3.0
 * @history 3.0 9/10/02 significant refactoring of scape model
 * @version 1.9.2 fixed a bug in moveToward and moveAway
 * @history 1.2 10/1/99 Moved some rules here
 * @history 1.5 Many small additions and fixes since 1.0
 * @since 1.0
 */
public class CellOccupant extends Cell {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * An rule causing the target agent to interact with each of its neighbors as
     * specified by the Agent.play() method.
     */
    public static final Rule PLAY_HOST_RULE = new Rule("Play Host") {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * Play each neighbor within one unit of the agent.
         * @param agent the playing agent
         * @see Agent#play
         */
        public void execute(Agent agent) {
            HostCell host = ((CellOccupant) agent).getHostCell();
            agent.play(host);
        }

        /**
         * Returns true. Random execution is required, since other agent's state might be affected.
         */
        public boolean isRandomExecution() {
            return true;
        }

        /**
         * Returns false. Play does not by default cause any agent removal, though it
         * might in some models.
         */
        public boolean isCauseRemoval() {
            return false;
        }
    };

    /**
     * A rule causing the taget agent to take a random walk. The agent takes a step in a random direction
     * out of any open directions, that is, into a random unoccupied neighboring cell. If no neighboring
     * cells are unoccupied, nothing happens. Note the difference between this rule and RANDOM_WALK_RULE.
     */
    public static final Rule RANDOM_WALK_AVAILABLE_RULE = new Rule("Random Walk Available") {
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
            ((CellOccupant) agent).randomWalkAvailable();
        }

        /**
         * Returns false. Movement should not usually cause agent removal.
         */
        public boolean isCauseRemoval() {
            return false;
        }
    };

    /**
     * The cell that this cell occupies on the host cell's lattice.
     */
    private Scape hostScape;

    /**
     * The cell that this cell occupies on the host cell's lattice.
     */
    private HostCell hostCell;

    /**
     * Initializes the cell occupant, moving the cell to a random location in the hostscape.
     * If you want to place the agent in a different location, move after calling this super
     * method, or override, being sure to set initialized to true.
     */
    /*public void initialize() {
		Cell cell = getHostScape().findRandomUnoccupiedCell();
		if (cell != null) {
            moveTo((HostCell) cell);
        }
        else {
            die();
            throw new RuntimeException("Couldn't find unoccupied cell for initializing pdAgent.");
        }
        super.initialize();
    }*/

    /**
     * Removes this cell from the current host cell.
     */
    public void leave() {
        if (this.hostCell != null) {
            this.hostCell.removeOccupant();
        }
        this.hostCell = null;
        this.coordinate = null;
    }

    /**
     * Assigns this cell as the occupant of the supplied host cell.
     * @param hostCell the host to assign this cell to
     */
    public void moveTo(HostCell hostCell) {
        if (hostCell != null) {
            if (hostCell.isAvailable()) {
                if (this.hostCell != null) {
                    this.hostCell.removeOccupant();
                }
                this.hostCell = hostCell;
                hostCell.setOccupant(this);
                this.coordinate = hostCell.getCoordinate();
            } else {
                throw new RuntimeException("HostCell not available: " + hostCell);
            }
        } else {
            throw new RuntimeException("HostCell cannot be set null.");
        }
    }

    /**
     * Move one step toward the occupant of the supplied host cell.
     * Assumes Moore like space for now. i.e. allow diagonal moves.
     * Warning: temporary, only works for cells on 2D lattices now.
     * @param targetCell the host to move towards
     */
    public void moveToward(LocatedAgent targetCell) {
        HostCell dCell = (HostCell) ((Discrete) getHostScape().getSpace()).findCellToward(this.getHostCell(), (HostCell) targetCell);
        if (dCell.isAvailable()) {
            moveTo(dCell);
        }
    }

    /**
     * Move one step away from the occupant of the supplied host cell.
     * Warning: temporary, only works for cells on 2D lattices now.
     * @param targetCell the host to move towards
     */
    public void moveAway(LocatedAgent targetCell) {
        HostCell dCell = (HostCell) ((Discrete) getHostScape().getSpace()).findCellAway(this.getHostCell(), (HostCell) targetCell);
        if (dCell.isAvailable()) {
            moveTo(dCell);
        }
    }
    
    /**
     * Gets a coordinate the location of this cell within the relevant <i>Host Scape</i>.
     */
    public Coordinate getCoordinate() {
        if (hostCell != null) {
            return hostCell.getCoordinate();
        }
        return super.getCoordinate();
    }

    /**
     * Moves this cell to a random unoccupied location on the host scape.
     */
    public void moveToRandomLocation() {
        //mtp 11/5/99
        HostCell cell = (HostCell) ((Discrete) getHostScape().getSpace()).findRandomUnoccupiedCell();
        if (cell != null) {
            moveTo(cell);
        } else {
            System.out.println("Warning: no location to move to. Killing agent.");
            die();
        }
    }

    /**
     * Removes the agent from play, causing it to vacate its host cell.
     */
    public void die() {
        leave();
        if (!scape.remove(this)) {
            throw new RuntimeException("Agent Couldn't be deleted");
        }
    }

    /**
     * Returns the cell that this cell occupies in the host cell's lattice.
     */
    public HostCell getHostCell() {
        return hostCell;
    }

    /**
     * Sets the cell that this cell occupies in the host cell's lattice.
     * JMiller 7/24/01
     */
    public void setHostCell(HostCell hostCell) {
        this.hostCell = hostCell;
    }


    /**
     * Returns the lattice that hosts this cell.
     */
    public Scape getHostScape() {
        return hostScape;
    }

    /**
     * Sets the lattice that hosts this cell.
     */
    public void setHostScape(Scape hostScape) {
        this.hostScape = hostScape;
    }

    /**
     * Returns a random neighbor on host, if any exist
     * neighboring this cell's location on its host cell's lattice.
     */
    public Cell findRandomNeighborOnHost() {
        List nh = findNeighborsOnHost();
        if (nh.size() > 0) {
            return (Cell) nh.get(randomToLimit(nh.size()));
        } else {
            return null;
        }
    }
    
    public Node findRandomAvailableNeighbor() {
        return getHostCell().findRandomAvailableNeighbor();
    }
    
    public Node findRandomNeighbor() {
        return findRandomNeighborOnHost();
    }

    public List findNeighbors() {
        return findNeighborsOnHost();
    }

    /**
     * Overides the getNeighbors method to return the cell's occupants
     * neighboring this cell's location on its host cell's lattice.
     */
    public List findNeighborsOnHost() {
        return hostCell.findNeighboringOccupants();
    }

    /**
     * Returns all agents within the specified distance of the agent on the host scape.
     * @param condition the condition that found agent must meet
     * @param distance the distance agents must be within to be included
     */
    public List findWithin(Conditional condition, double distance) {
        return findWithin(condition, false, distance);
    }

    /**
     * Returns all agents within the specified distance of the agent on the host scape.
     * @param distance the distance agents must be within to be included
     */
    public List findWithin(double distance) {
        return findWithin(null, false, distance);
    }

    /**
     * Returns all agents within the specified distance of the agent on the host scape.
     * @param condition the condition that found agent must meet
     * @param includeSelf   whether or not the starting agent should be included in the search
     * @param distance the distance agents must be within to be included
     */
    public List findWithin(final Conditional condition, boolean includeSelf, double distance) {
        Conditional hostedCondition = hostedCondition(condition);
        return findOccupants(getHostCell().findWithin(hostedCondition, includeSelf, distance));
    }

    /**
     * Returns the closest agent.
     */
    public LocatedAgent findNearest() {
        return findNearest(null, false, Double.MAX_VALUE);
    }

    /**
     * Returns the closest agent within the specified distance from this agent that meet some condition.
     * @param distance the distance agents must be within to be included
     */
    public LocatedAgent findNearest(double distance) {
        return findNearest(null, false, distance);
    }

    /**
     * Returns the closest agent within the specified distance from this agent that meet some condition.
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
        Conditional hostedCondition = hostedCondition(condition);
        final LocatedAgent nearest = getHostCell().findNearest(hostedCondition, includeSelf, distance);
        if (nearest != null) {
            return (LocatedAgent) ((HostCell) nearest).getOccupant();
        } else {
            return null;
        }
    }

    /**
     * Returns unoccupied cells neighboring this cell's location
     * on the host cell's lattice.
     */
    public List findAvailableNeighbors() {
        return hostCell.findAvailableNeighbors();
    }

    /**
     * Interact with each neighbor as specified by the Agent.play() method.
     */
    public void playRandomNeighbor() {
        Cell n = findRandomNeighborOnHost();
        if (n != null) {
            play(n);
        }
    }

    /**
     * Interact with each neighbor as specified by the Agent.play() method.
     */
    public void playNeighbors() {
        List neighbors = findNeighborsOnHost();
        for (Iterator iterator = neighbors.iterator(); iterator.hasNext();) {
            Agent neighbor = (Agent) iterator.next();
            play(neighbor);

        }
    }

    /**
     * Picks a random neighboring location on the host cell's lattice.
     * If that location is unoccupied, moves this agent to it.
     * Note the distinction between this method and <code>randomWalkAvailable</code>.
     * In this case, a random neighboring cell is slected. If, and only if, that cell is
     * unoccupied, does the agent move to it.
     * This means, for instance, that a neighboring cell might be available but
     * the cell occupant migh select an occupied cell and thus not move.
     * @see this.randomWalkAvailable()
     */
    public void randomWalk() {
        if (getHostCell() != null) {
            Cell candidate = (Cell) getHostCell().findRandomNeighbor();
            if ((candidate).isAvailable()) {
                moveTo((HostCell) candidate);
            }
        } else {
            throw new RuntimeException("Called Random Walk on Agent (" + this + ") that has no current location.");
        }
    }

    /**
     * Picks a random available neighboring location on the host cell's lattice.
     * If no locations are available, stays put.
     * Note the distinction between this method and <code>randomWalk</code>.
     * In this case, all available neigbors are first found, and then one of those cells
     * is randomly selected and moved to.
     * This means that if there is even one unoccpied cell available, the cell  occupant
     * will move to it.
     * @see this.randomWalk
     */
    public void randomWalkAvailable() {
        if (getHostCell() != null) {
            HostCell candidate = getHostCell().findRandomAvailableNeighbor();
            if (candidate != null) {
                moveTo(candidate);
            }
        }
    }

    /**
     * Clone this occupant, making host cell and coordinate null,
     * since for a base cell occupant it is illegal for more than one cell to
     * occupy the same location.
     */
    public Object clone() {
        CellOccupant clone = (CellOccupant) super.clone();
        clone.hostCell = null;
        return clone;
    }
}
