/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.ascape.model.rule.Rule;
import org.ascape.model.space.Coordinate;
import org.ascape.model.space.Discrete;
import org.ascape.model.space.Node;
import org.ascape.model.space.Relative;
import org.ascape.model.space.Space;
import org.ascape.util.Conditional;

/**
 * The base class for all members of lattices. Currently considered a
 * node, although nodes might be become a superclass of this class as the
 * notion of a coordinate may not be useful in all graphs.
 *
 * @author Miles Parker
 *
 * @version 3.0
 * @history 3.0 9/10/02 significant refactoring of scape model
 * @history 2.0 10/29/01 Changed to subclass Located Agent, moved coordinate stuff there.
 * @history 1.2 Changed to subclass Agent, after removing AgentScapeAware (Ascape objects are all scape aware now.)
 * @since 1.0
 */
public class Cell extends LocatedAgent implements Node {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * A rule causing the target agent to interact with each of its neighbors as
     * specified by the Agent.play() method.
     */
    public static final Rule PLAY_NEIGHBORS_RULE = new Rule("Play Neighbors") {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * Play each neighbor within one unit of the agent.
         * Assumes that ordering doesn't matter.
         * @param agent the playing agent
         * @see Agent#play
         */
        public void execute(Agent agent) {
            ((CellOccupant) agent).playNeighbors();
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
     * A rule causing the target agent to interact with one of its neighbors as
     * specified by the Agent.play() method.
     */
    public static final Rule PLAY_RANDOM_NEIGHBOR_RULE = new Rule("Play Random Neighbor") {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * Play each neighbor within one unit of the agent.
         * Assumes that ordering doesn't matter.
         * @param agent the playing agent
         * @see Agent#play
         */
        public void execute(Agent agent) {
            ((CellOccupant) agent).playRandomNeighbor();
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
     * A rule causing the taget agent to move to a random location.
     */
    public static final Rule CALCULATE_NEIGHBORS_RULE = new Rule("Calculate Neighbors") {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        /**
         * Discover the neighbors for this cell in the primary scape.
         * @param agent the playing agent
         */
        public void execute(Agent agent) {
            ((Cell) agent).calculateNeighbors();
        }

        /**
         * Returns false -- it doens't amtter what order we calculate neighbors in.
         */
        public boolean isRandomExecution() {
            return false;
        }

        /**
         * Returns false. Neighbor calc cannot cause deletion.
         */
        public boolean isCauseRemoval() {
            return false;
        }
    };

    /**
     * For better performance we store neighbors so we only have to calculate them once.
     * Typically only used by scapes with a static strucure.
     */
    protected List neighbors = new ArrayList();

    /**
     * A network of related cells. This is intended to provide a light-weight
     * way to track relationships between cells without resorting to the use
     * of scapes to track each relationship. At some point this implementation
     * may change to simply use 1D vector or array scape. In any case, getNetwork
     * and setNetwork will <i>probably</i> retain the current implementation.
     */
    private List network;

    public void initialize() {
        super.initialize();
        network = new ArrayList();
    }

    /**
     * Computes the cells neighbors in the context of the primary scape and assigns them as this cell's neighbors.
     */
    public void calculateNeighbors() {
        setNeighborsList(((Discrete) getScape().getSpace()).calculateNeighbors(this));
    }

    /**
     * Returns this cells neighbors, that is, the set of cells as an array of cells.
     * (Principally here for backward compatibility.)
     * adjoing this cell as defined by the scape's geometry.
     * @deprecated use findNeighbors instead.
     */
    public Cell[] getNeighbors() {
        List list = findNeighbors();
        Cell[] cells = new Cell[list.size()];
        return (Cell[]) list.toArray(cells);
    }

    /**
     * Returns this cells neighbors, that is, the set of cells
     * adjoining this cell as defined by the scape's geometry.
     * Note: returned as unmodifieable list for perforamnce reasons.
     * If you need to modify this list, make a copy.
     */
    public List findNeighbors() {
        if (!(scape.isMutable())) {
            return Collections.unmodifiableList(neighbors);
        } else {
            //Will be slow, special case if we call it often...
            return ((Discrete) getScape().getSpace()).calculateNeighbors(this);
        }
    }

    public Cell findRelative(Coordinate c) throws UnsupportedOperationException {
        Space space = getScape().getSpace();
        if (space instanceof Relative) {
            return (Cell) ((Relative) space).findRelative(this, c);
        } else {
            throw new UnsupportedOperationException("Cell is not a memeber of a scape space that can handle relative coordiantes: " + space);
        }
    }

    /**
     * Returns the neighbors of the cell that meet the supplied condition.
     * @param condition the condition that found cell must meet
     */
    public List getNeighbors(Conditional condition) {
        ArrayList neighborsMatching = new ArrayList();
        if (scape.isMutable()) {
            //In this case, we will only be using neighbors as a temp value
            //Will be slow, special case if we call it a often...
            neighbors = findWithin(1.0);
        }
        for (int i = 0; i < neighbors.size(); i++) {
            if (condition.meetsCondition(neighbors.get(i))) {
                neighborsMatching.add(neighbors.get(i));
            }
        }
        return neighborsMatching;
    }

    /**
     * Returns the number of cells that are neighbors and that meet the supplied condition.
     * @param condition the condition that found cell must meet
     */
    public int countNeighbors(Conditional condition) {
        if (scape.isMutable()) {
            //In this case, we will only be using neighbors as a temp value
            //Will be slower, special case if we call it a often...
            neighbors = scape.findWithin(1.0);
        }
        int count = 0;
        //This is quicker than using iterator!
        for (int i = 0; i < neighbors.size(); i++) {
            if (condition.meetsCondition(neighbors.get(i))) {
                count++;
            }
        }
        return count;
    }

    /**
     * Interact with each neighbor as specified by the Agent.play() method.
     */
    public void playRandomNeighbor() {
        Cell n = (Cell) findRandomNeighbor();
        if (n != null) {
            play(n);
        }
    }

    /**
     * Interact with each neighbor as specified by the Agent.play() method.
     */
    public void playNeighbors() {
        for (Iterator iterator = findNeighbors().iterator(); iterator.hasNext();) {
            play((Agent) iterator.next());
        }
    }

    public int getDistance(Cell target) {
        return (int) calculateDistance(target);
    }

    /**
     * Sets this cell's neighbors. Used by agent scape intialize
     * methods to inform the cell of it's neighbors. When creating new
     * lattice classes, be sure to set all cell's neighbors using this method.
     * Note that this method could also be used for creating dynamic spaces!
     * @param neighbors the array of neighbors to set
     */
    public void setNeighborsList(List neighbors) {
        this.neighbors = neighbors;
    }

    /**
     * Sets this cell's neighbors. Used by agent scape intialize
     * methods to inform the cell of it's neighbors. When creating new
     * lattice classes, be sure to set all cell's neighbors using this method.
     * Note that this method could also be used for creating dynamic spaces!
     * Preferred method is to use setNeighborsList.
     * @param neighbors the array of neighbors to set
     */
    public void setNeighbors(Cell[] neighbors) {
        setNeighborsList(new ArrayList(Arrays.asList(neighbors)));
    }

    /**
     * Returns a cell randomly selected from among this cell's neighbors.
     */
    public Node findRandomNeighbor() {
        try {
            return (Cell) neighbors.get(randomToLimit(neighbors.size()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("No neighbors when trying to find random neighbor for: " + this);
        }
    }
    
    public Node findRandomAvailableNeighbor() {
        return findRandomNeighbor();
    }
    
    /**
     * Returns any occupants of this cell. Cell occupants are incapable of
     * hosting agents, so this will always be null for instantiations of this class.
     */
    public Node getOccupant() {
        return null;
    }

    /**
     * Returns some network of related cells. The network itself is implementation specific.
     */
    public List getNetwork() {
        return network;
    }

    /**
     * Sets a network of related cells.
     */
    public void setNetwork(List network) {
        this.network = network;
    }

    /**
     * Is this cell available for occupation? Again, this will always return
     * false because base cells can not host agents.
     */
    public boolean isAvailable() {
        return false;
    }

    /**
     * Sets this cell's occupant. Produces an error, because base cells can not
     * host agents.
     */
    public void setOccupant(Node occupant) {
        throw new RuntimeException("Only host cells can accept occupants.");
    }

    /**
     * Removes this cell's occupant. Produces an error, because base cells can not
     * host agents.
     */
    public void removeOccupant() {
        throw new RuntimeException("You cannot remove an occupant from a Cell, which can not support occupants.");
    }

    /**
     * Clones the host cell, making coordinate null.
     */
    public Object clone() {
        Cell clone = (Cell) super.clone();
        clone.coordinate = null;
        return clone;
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
                return "Cell " + coordinate;
            } else {
                return "Cell";
            }
        }
    }

    protected static Conditional hostedCondition(final Conditional condition) {
        if (condition != null) {
            Conditional hostedCondition = new Conditional() {
                /**
                 * 
                 */
                private static final long serialVersionUID = 1L;

                public boolean meetsCondition(Object object) {
                    final CellOccupant occupant = (CellOccupant) ((HostCell) object).getOccupant();
                    return (occupant != null) && (condition.meetsCondition(occupant));
                }
            };
            return hostedCondition;
        } else {
            return HostCell.IS_OCCUPIED;
        }
    }

    /**
     * Returns all cell occupants of the provided cells.
     * @param candidates the cells to return occupants of
     */
    public static List findOccupants(List candidates) {
        List occupants = new ArrayList();
        for (Iterator iterator = candidates.iterator(); iterator.hasNext();) {
            CellOccupant cellOccupant = (CellOccupant) ((Cell) iterator.next()).getOccupant();
            if (cellOccupant != null) {
                occupants.add(cellOccupant);
            }
        }
        return occupants;
    }
}
