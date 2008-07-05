/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.space;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;

import org.ascape.model.HostCell;
import org.ascape.util.Conditional;
import org.ascape.util.Conditionals;



/**
 * A space with discrete nodes. Any space that has some kind of underyling
 * discrete trucutre should implement this class.
 * 
 * @author Miles Parker
 * @version 3.0
 * @history 3.0 9/10/02 significant refactoring of space model hierarchy
 * @history 2.9.1 7/17/02 changed name to ScapeDiscrete
 * @history 2.0 10/29/01 changes to support new continuous space functionality,
 *          (int) getDistance is now (double) calcualteDistance
 * @history 1.9.3 3-4/2001 Many QA fixes and functional improvements, added
 *          "findNearestMember" method, especially to findCells and getCells
 *          methods, changed getCells ordering
 * @history 1.9.2 11/3/2000 fixed small problem with deleted record in rule
 *          order
 * @history 1.5 12/99 modfied to support new iterations approach, moved
 *          executeMembers functionality to Scape from all subclasses
 * @history 1.2.5 10/6/99 changed scape constructors to include name and not
 *          include geometry where appropriate
 * @history 1.2 7/20/99 added accessors for iterations per cycle
 * @history 1.1.1 05/8/99 added find functionality, changed many names from get*
 *          to find*, other fixes and optimizations
 * @history 1.0.2 03/08/99 assumed role of ScapeLatticeDiscrete and
 *          ScapeLattice1DDiscrete
 * @history 1.0.1 11/23/98 renamed (reconceived) from ScapeLattice to
 *          ScapeDiscrete, moved a lot of methods from old Scape (new
 *          ScapeLattice) here
 * @since 1.0.1
 */
public abstract class Discrete extends CollectionSpace {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a discrete space.
     */
    public Discrete() {
        super();
    }

    /**
     * Constructs a discrete space of provided extent.
     * 
     * @param extent
     *            a coordinate describing the size of this space
     */
    public Discrete(CoordinateDiscrete extent) {
        this();
        setExtent(extent);
    }

    /**
     * Calculate neighbors.
     * 
     * @param cell
     *            the cell
     * @return the list
     */
    public List calculateNeighbors(Node cell) {
        return findWithin(cell.getCoordinate(), null, false, 1.0);
    }

    /**
     * Returns the size, or number of cells, (the product of all extents) of
     * this graph.
     * 
     * @return the size
     */
    public int getSize() {
        return ((CoordinateDiscrete) extent).getProduct();
    }

    /**
     * Returns cells that are available, that is, uncoccupied.
     * 
     * @return the list
     */
    public List findOccupants() {
        return findOccupants(this);
    }

    public Location findRandomNeighbor(Node location) {
        List neighbors = findNeighbors(location);
        if (neighbors.size() > 0) {
            return (Location) neighbors.get(randomToLimit(neighbors.size()));
        } else {
            return null;
        }
    }

    public Location findRandomNeighbor(Node location, Conditional condition) {
        List neighbors = findNeighbors(location);
        neighbors = filter(neighbors, condition);
        if (neighbors.size() > 0) {
            return (Location) neighbors.get(randomToLimit(neighbors.size()));
        } else {
            return null;
        }
    }

    public List findNeighbors(Node location) {
        return location.findNeighbors();
    }

    public Location findRandomAvailableNeighbor(Node location) {
        return location.findRandomAvailableNeighbor();
    }

    public Location findRandomAvailable(Node origin, Conditional condition, boolean includeSelf, double distance) {
        List available = findWithin(origin.getCoordinate(), Conditionals.and(condition, HostCell.IS_AVAILABLE), includeSelf, distance);
        if (available.size() > 0) {
            return (Location) available.get(randomToLimit(available.size()));
        } else {
            return null;
        }

    }

    /**
     * Returns all cell occupants of the provided cells.
     * 
     * @param candidates
     *            the cells to return occupants of
     * @return the list
     */
    public static List findOccupants(Collection candidates) {
        List occupants = new ArrayList();
        for (Iterator iterator = candidates.iterator(); iterator.hasNext();) {
            Node occupant = ((Node) iterator.next()).getOccupant();
            if (occupant != null) {
                occupants.add(occupant);
            }
        }
        return occupants;
    }

    /**
     * Returns cells that are available, that is, uncoccupied.
     * 
     * @return the list
     */
    public List findAvailable() {
        return findAvailable(this);
    }

    /**
     * Returns cells that are available, that is, uncoccupied.
     * 
     * @param candidates
     *            the cells to return available cells from
     * @return the list
     */
    public List findAvailable(Collection candidates) {
        List available = new ArrayList();
        for (Iterator iterator = candidates.iterator(); iterator.hasNext();) {
            Node candidate = (Node) iterator.next();
            if (candidate.isAvailable()) {
                available.add(candidate);
            }
        }
        return available;
    }

    /**
     * Returns all agents in the space as an array of cells (use this method to
     * avoid coercion of members to Node.)
     * 
     * @return the cells
     */
    public Node[] getCells() {
        deleteSweep();
        Node[] agents = new Node[collection.size()];
        agents = (Node[]) collection.toArray(agents);
        return agents;
    }

    /**
     * Returns a random unoccupied discrete location in the space. Returns null
     * if no random locations are available, but an unoccupied location, even if
     * only one exists. This method first tries testing random locations within
     * the grid, and the first n (10) are found to be unnoccupied, the number of
     * random locations is assumed to be sparse, and a search of all random
     * locations is done.
     * 
     * @return the node
     * @deprecated
     */
    public Node findRandomUnoccupiedCell() {
        return findRandomAvailable();
    }

    public Node findRandomAvailable() {
        for (int i = 0; i < 10; i++) {
            Node cell = (Node) findRandom();
            if (cell.isAvailable()) {
                return cell;
            }
        }
        List available = findAvailable();
        if (available.size() > 0) {
            return (Node) available.get(randomToLimit(available.size()));
        } else {
            return null;
        }
    }

    public Node findRandomAvailable(Conditional condition) {
        return (Node) findRandom(Conditionals.and(condition, HostCell.IS_AVAILABLE));
    }

    public Location findNearestAvailable(final Location origin, Conditional condition, boolean includeOrigin, double distance) {
        return findNearest(origin, Conditionals.and(condition, HostCell.IS_AVAILABLE), includeOrigin, distance);
    }

    /**
     * Returns a random unoccupied discrete location in the space. Returns null
     * if no random locations are available, but an unoccupied location, even if
     * only one exists. This method first tries testing random locations within
     * the grid, and the first n (10) are found to be unnoccupied, the number of
     * random locations is assumed to be sparse, and a search of all random
     * locations is done.
     * 
     * @param excludeCell
     *            the exclude cell
     * @return the node
     */
    public Node findRandomUnoccupiedCell(Node excludeCell) {
        Node randomCell;
        do {
            randomCell = findRandomUnoccupiedCell();
        } while (excludeCell == randomCell);
        return randomCell;
    }

    /**
     * The performance warning.
     */
    public static boolean performanceWarning = true;

    /**
     * This method returns cells near using a default method that simply scans
     * through every cell in the lattice, checking that it is within 'distance'
     * of central cell. This method should work with any space graph
     * implementation that implements geteDistance properly. Obviously very poor
     * performance in all but extremely large distances. This method is really
     * only intended to support conditions where other more specific methods do
     * not apply. This method works for any geometry, hence it is n ot designed
     * for any specific geometry. Therefore, any calling methods should first
     * check to make sure that the distance doesn't cover the whole space -- if
     * it does, the calling method should simply use the getLocations method. An
     * calling methods should first check to see that
     * 
     * @param origin
     *            the agent to find cells near
     * @param includeSelf
     *            should supplied agent be included in the return set
     * @param distance
     *            the distance to form centralCells to return cells
     * @return the cells near default
     * @deprecated
     */
    public final Node[] getCellsNearDefault(Node origin, boolean includeSelf, int distance) {
        List list = findWithinDefault(origin.getCoordinate(), includeSelf, distance);
        Node[] cells = new Node[list.size()];
        return (Node[]) list.toArray(cells);
    }

    /**
     * Find within default.
     * 
     * @param origin
     *            the origin
     * @param includeSelf
     *            the include self
     * @param dist
     *            the dist
     * @return the list
     */
    public List findWithinDefault(Coordinate origin, boolean includeSelf, double dist) {
        int distance = (int) dist;
//        if (performanceWarning) {
//            System.out.println("Warning: code is using getCellsNearDefault method, which has significant performance issues.");
//            System.out.println("An update should address this issue soon.");
//            performanceWarning = false;
////            throw new RuntimeException();
//        }
        Node[] candidates = getCells();
        //Performance issue -- may be better to move this to permanent scratch memory
        //at cost of memory space.
        int foundIndex = 0;
        Node[] foundCells = new Node[candidates.length];
        if (includeSelf) {
            for (int i = 0; i < candidates.length; i++) {
                if (calculateDistance(origin, candidates[i].getCoordinate()) <= distance) {
                    foundCells[foundIndex] = candidates[i];
                    foundIndex++;
                }
            }
        } else {
            int targetDistance;
            for (int i = 0; i < candidates.length; i++) {
                targetDistance = (int) calculateDistance(origin, candidates[i].getCoordinate());
                //If target distance is 0, the candidate is the centralLocation and we should not include it
                //Performance issue -- any way to move the second check out of the loop?
                if ((targetDistance <= distance) && (targetDistance != 0)) {
                    foundCells[foundIndex] = candidates[i];
                    foundIndex++;
                }
            }
        }
        Node[] returnCells = new Node[foundIndex];
        System.arraycopy(foundCells, 0, returnCells, 0, returnCells.length);
        //return returnCells;
        ArrayList found = new ArrayList();
        for (int i = 0; i < returnCells.length; i++) {
            found.add(returnCells[i]);
        }
        return found;
    }

    /**
     * Returns the next cell within immediate neighborhood toward the requested
     * cell.
     * 
     * @param origin
     *            the current cell
     * @param target
     *            the cell that we are moving toward
     * @return the node
     */
    public abstract Node findCellToward(Node origin, Node target);

    /**
     * Returns the cell within immediate neighborhood furthest away from the
     * requestd cell.
     * 
     * @param origin
     *            the current cell
     * @param target
     *            the cell that we are moving toward
     * @return the node
     */
    public abstract Node findCellAway(Node origin, Node target);

    /**
     * Gets the maximum rank.
     * 
     * @return the maximum rank
     */
    public int getMaximumRank() {
        return Integer.MAX_VALUE;
    }

    /**
     * Returns the shortest distance between one cell and the other.
     * 
     * @param origin
     *            the starting cell
     * @param target
     *            the ending cell
     * @return the distance
     * @deprecated use calculateDistance instead.
     */
    public int getDistance(Node origin, Node target) {
        return (int) calculateDistance(origin.getCoordinate(), target.getCoordinate());
    }

    /**
     * Finds the nearest agent that meets some condition. Spaces without
     * coordinate meaing should override this method.
     * 
     * @param origin
     *            the coordinate to find agents near
     * @param condition
     *            the condition that found agent must meet
     * @param includeOrigin
     *            if the origin should be included
     * @param distance
     *            the maximum distance around the origin to look
     * @return the location
     */
    public Location findNearestBFS(final Coordinate origin, Conditional condition, boolean includeOrigin, double distance) {
        BFSWithinIterator within = (BFSWithinIterator) withinIterator(origin, condition, includeOrigin, distance);
        //may be null
        Location result = (Location) within.next();
        int searchDistance = within.getDepth();
        //Leave null until we know we need it.
        List candidates = null;
        while (within.hasNext() && (searchDistance == within.getDepth())) {
            Location nextResult = (Location) within.next();
            if (candidates == null) {
                candidates = new ArrayList();
                candidates.add(result);
            }
            candidates.add(nextResult);
        }
        if (candidates != null) {
            result = (Location) candidates.get(randomToLimit(candidates.size()));
        }
        return result;
    }
    
    /**
     * The Class InList.
     */
    class InList implements Conditional {
        
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        /**
         * The list.
         */
        List list;
        
        /**
         * Instantiates a new in list.
         * 
         * @param list
         *            the list
         */
        public InList(List list) {
            this.list = list;
        }
        
        /* (non-Javadoc)
         * @see org.ascape.util.Conditional#meetsCondition(java.lang.Object)
         */
        public boolean meetsCondition(Object object) {
            return list.contains(object);  //To change body of implemented methods use File | Settings | File Templates.
        }
    }


    /**
     * This is the findWithin code from Graph. This can be used by this class's
     * findWithin, when distance is greater than 1. dist = 1 is the same as
     * findNeighbors.
     * 
     * @param origin
     *            the origin
     * @param condition
     *            the condition
     * @param includeSelf
     *            the include self
     * @param distance
     *            the distance
     * @return the iterator
     */
    protected Iterator bfsWithinIterator(Location origin, Conditional condition, boolean includeSelf, double distance) {
        return new BFSWithinIterator(this, origin, condition, includeSelf, distance);
    }

    /**
     * The Class FindCoordinateCondition.
     */
    class FindCoordinateCondition implements Conditional {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        /**
         * The agent.
         */
        Coordinate agent;

        /**
         * Instantiates a new find coordinate condition.
         * 
         * @param agent
         *            the agent
         */
        public FindCoordinateCondition(Coordinate agent) {
            this.agent = agent;
        }

        /* (non-Javadoc)
         * @see org.ascape.util.Conditional#meetsCondition(java.lang.Object)
         */
        public boolean meetsCondition(Object object) {
            return ((Location) object).getCoordinate().equals(agent);
        }
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.CollectionSpace#calculateDistance(org.ascape.model.space.Coordinate, org.ascape.model.space.Coordinate)
     */
    public double calculateDistance(Coordinate origin, Coordinate target) {
        BFSWithinIterator within = (BFSWithinIterator) withinIterator(origin, new FindCoordinateCondition(target), true, Double.MAX_VALUE);
        if (within.hasNext()) {
            return within.getDepth();
        } else {
            return Double.NaN;
        }
    }

    /**
     * The Class InCollectionConditional.
     */
    class InCollectionConditional implements Conditional {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        /**
         * The collection.
         */
        Collection collection;

        /**
         * Instantiates a new in collection conditional.
         * 
         * @param collection
         *            the collection
         */
        public InCollectionConditional(Collection collection) {
            this.collection = collection;
        }

        /* (non-Javadoc)
         * @see org.ascape.util.Conditional#meetsCondition(java.lang.Object)
         */
        public boolean meetsCondition(Object object) {
            return collection.contains(object);
        }
    }

    /**
     * Find cell toward BFS.
     * 
     * @param origin
     *            the origin
     * @param target
     *            the target
     * @return the node
     */
    protected Node findCellTowardBFS(Node origin, Node target) {
        List closest = new ArrayList();
        double min = Double.MAX_VALUE;
        for (Iterator iterator = origin.findNeighbors().iterator(); iterator.hasNext();) {
            Node originNeighbor = (Node) iterator.next();
            double distance = calculateDistance(originNeighbor.getCoordinate(), target.getCoordinate());
            if (distance < min) {
                min = distance;
                closest = new ArrayList();
                closest.add(originNeighbor);
            } else if (distance == min) {
                closest.add(originNeighbor);
            }
        }
        if (closest.size() > 0) {
            return (Node) closest.get(randomToLimit(closest.size()));
        } else {
            return origin;

        }
    }

    /**
     * Find cell away BFS.
     * 
     * @param origin
     *            the origin
     * @param target
     *            the target
     * @return the node
     */
    protected Node findCellAwayBFS(Node origin, Node target) {
        double distance = 0;
        LinkedList furthest = new LinkedList();
        for (Iterator iterator = origin.findNeighbors().iterator(); iterator.hasNext();) {
            Node currentNeighbor = (Node) iterator.next();
            double currentDistance = (int) calculateDistance(currentNeighbor, target);
            if (currentDistance > distance) {
                distance = currentDistance;
                furthest.clear();
            }
            if (currentDistance == distance) {
                furthest.add(currentNeighbor);
            }
        }
        if (furthest.size() > 0) {
            return (Node) furthest.get(randomToLimit(furthest.size()));
        } else {
            return origin;
        }
    }

    /**
     * Calculate distance.
     * 
     * @param o
     *            the o
     * @param t
     *            the t
     * @param bound
     *            the bound
     * @return the int
     */
    public static int calculateDistance(int o, int t, int bound) {
        //Check one way
        if (o > t) {
            if ((o - t) < (t + bound - o)) {
                return o - t;
            } else {
                return t + bound - o;
            }
        } else { //t >= o
            if ((t - o) < (o + bound - t)) {
                return t - o;
            } else {
                return o + bound - t;
            }
        }
    }
}
