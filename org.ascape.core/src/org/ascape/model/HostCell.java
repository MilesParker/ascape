/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model;

import java.awt.Color;
import java.util.List;

import org.ascape.model.space.Discrete;
import org.ascape.model.space.Location;
import org.ascape.model.space.Node;
import org.ascape.util.Conditional;

/**
 * An cell capable fo serving as a 'home' for agents. At the moment,
 * only one agent per cell can be hosted, but note that that cell can obviosuly be an instance of Scape
 * such as a list.
 *
 * @author Miles Parker
 * @version 3.0
 * @history 3.0 9/10/02 significant refactoring of scape model
 * @history 1.5 1/20/2000 many small changes since 1.0
 * @since 1.0
 */
public class HostCell extends Cell {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public final static Conditional IS_AVAILABLE = new Conditional() {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public final boolean meetsCondition(Object o) {
            return ((HostCell) o).isAvailable();
        }
    };

    public final static Conditional IS_OCCUPIED = new Conditional() {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public final boolean meetsCondition(Object o) {
            return !((HostCell) o).isAvailable();
        }
    };

    /**
     * The occupant, if any, of this cell.
     */
    protected CellOccupant occupant = null;

    /**
     * Is this cell available (currently unoccupied?)
     * @return true if available, false if not
     */
    public boolean isAvailable() {
        return (occupant == null);
    }

    /**
     * Return the occupant of this cell, null if no occupant.
     */
    public Node getOccupant() {
        return occupant;
    }

    /*public void setNeighbors(Cell[] neighbors) {
        super.setNeighbors(neighbors);
        neighborOccupants = new CellOccupant[0];
        availableNeighbors = new HostCell[neighbors.size()];
        for (int i = 0; i < neighbors.size(); i++) {
            availableNeighbors[i] = (HostCell) neighbors[i];
        }
    }*/

    private int[] neighborsAvailableIndices = new int[0];

    /**
     * Sets this cell's neighbors. Used by agent scape intialize
     * methods to inform the cell of it's neighbors. When creating new
     * lattice classes, be sure to set all cell's neighbors using this method.
     * Note that this method could also be used for creating dynamic spaces!
     * @param neighbors the array of neighbors to set
     */
    public void setNeighborsList(List neighbors) {
        super.setNeighborsList(neighbors);
        if (neighborsAvailableIndices.length < neighbors.size()) {
            neighborsAvailableIndices = new int[neighbors.size()];
        }
    }

    /**
     * Are their any neighboring cells that are available.
     * return true if adjoing cell is available, false otherwise
     */
    public boolean isNeighborAvailable() {
        for (int i = 0; i < neighbors.size(); i++) {
            if (((HostCell) neighbors.get(i)).isAvailable()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets a random neighboring host cell. If none available, returns null.
     */
    public HostCell findRandomAvailableNeighbor() {
        int count = 0;
        for (int i = 0; i < neighbors.size(); i++) {
            if (((HostCell) neighbors.get(i)).isAvailable()) {
                neighborsAvailableIndices[count] = i;
                count++;
            }
        }
        if (count > 0) {
            return (HostCell) neighbors.get(neighborsAvailableIndices[randomToLimit(count)]);
        } else { //No neighbors available (often better to check first.)
            return null;
        }
    }

    /**
     * Host the supplied occupant. Request a view update.
     */
    public void setOccupant(Node occupant) {
        if (this.occupant == null) {
            if (occupant != null) {
                this.occupant = (CellOccupant) occupant;
                /*for (int i = 0; i < neighbors.size(); i++) {
                    ((HostCell) neighbors[i]).addNeighborOccupant(occupant);
                }*/
            } else {
                throw new RuntimeException("Can't assign a null occupant to a host cell. Use removeOccupant first.");
            }
        } else {
            throw new RuntimeException("Tried to assign an occupant to an allready occupied cell.");
        }
        requestUpdate();
    }

    /**
     * Evict the supplied occupant. Request a view update.
     */
    public void removeOccupant() {
        if (occupant != null) {
            /*for (int i = 0; i < neighbors.size(); i++) {
                 ((HostCell) neighbors[i]).removeNeighborOccupant(occupant);
            }*/
            occupant = null;
        } else {
            throw new RuntimeException("No occupant to remove.");
        }
        requestUpdate();
    }

    /**
     * Returns all occupants of neighboring cells.
     */
    public List findNeighboringOccupants() {
        return Discrete.findOccupants(neighbors);
        //return availableNeighbors;
    }

    public List findOccupantsWithin(Conditional condition, double distance) {
        return findOccupantsWithin(condition, false, distance);
    }

    public List findOccupantsWithin(double distance) {
        return findOccupantsWithin(null, false, distance);
    }

    public List findOccupantsWithin(final Conditional condition, boolean includeSelf, double distance) {
        Conditional hostedCondition = hostedCondition(condition);
        return findOccupants(findWithin(hostedCondition, includeSelf, distance));
    }

    /**
     * Returns the closest agent.
     */
    public LocatedAgent findNearestOccupants() {
        return findNearestOccupants(null, false, Double.MAX_VALUE);
    }

    /**
     * Returns the closest agent within the specified distance from this agent that meet some condition.
     * @param distance the distance agents must be within to be included
     */
    public LocatedAgent findNearestOccupants(double distance) {
        return findNearestOccupants(null, false, distance);
    }

    /**
     * Returns the closest agent within the specified distance from this agent that meet some condition.
     * @param condition the condition the agent must meet to be included
     */
    public LocatedAgent findNearestOccupants(Conditional condition) {
        return findNearestOccupants(condition, false, Double.MAX_VALUE);
    }

    /**
     * Returns the closest agent within the specified distance from this agent that meet some condition.
     * @param condition the condition the agent must meet to be included
     * @param distance the distance agents must be within to be included
     */
    public LocatedAgent findNearestOccupants(Conditional condition, double distance) {
        return findNearestOccupants(condition, false, distance);
    }

    /**
     * Returns the closest agent within the specified distance from this agent that meet some condition.
     * @param condition the condition the agent must meet to be included
     * @param includeSelf if the calling agent should be included in the search
     * @param distance the distance agents must be within to be included
     */
    public LocatedAgent findNearestOccupants(Conditional condition, boolean includeSelf, double distance) {
        Conditional hostedCondition = hostedCondition(condition);
        LocatedAgent nearest = getScape().findNearest(this.getCoordinate(), hostedCondition, includeSelf, distance);
        if (nearest != null) {
            return (LocatedAgent) ((HostCell) nearest).getOccupant();
        } else {
            return null;
        }
    }

    /**
     * Returns all neighboring cells which are available for occupation.
     */
    public List findAvailableNeighbors() {
        return ((Discrete) getScape().getSpace()).findAvailable(neighbors);
        //return neighborOccupants;
    }

    /**
     * Clones the host cell, making occupant and neighbors null.
     */
    public Object clone() {
        Agent clone = (Agent) super.clone();
        occupant = null;
        return clone;
    }

    /**
     * Returns the default color for this cell (green.)
     * Override to provide another color, or provide a different
     * color feature for your views.
     */
    public Color getColor() {
        return Color.green;
        //below was replaced by general agent painting scheme..
        //previously used to have occupant's color automatically used if occupied
        /*if (occupant == null) {
            return Color.green;
        }
        else {
            return occupant.getColor();
        }*/
    }
}
