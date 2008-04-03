/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.space;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.ascape.model.Cell;
import org.ascape.util.Conditional;


/**
 * A space containing a simple, unweighted graph.
 */
public class Graph extends Discrete {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * The adjacency map.
     */
    private HashMap adjacencyMap = new HashMap();

    /**
     * Constructs an arbitrary directed graph.
     */
    public Graph() {
        super();
    }

    /**
     * Constructs an arbitrary directed graph.
     * 
     * @param extent
     *            a coordinate describing the size of this space
     */
    public Graph(CoordinateDiscrete extent) {
        this();
        setExtent(extent);
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.CollectionSpace#initialize()
     */
    public void initialize() {
        super.initialize();

        adjacencyMap.clear();

        // due to the nature of how the graph is populated,
        // need to go through and set up the array lists for those that start in it by default.
        // when agents are added, their array lists will be created automatically
        for (Iterator agentsIt = this.iterator(); agentsIt.hasNext();) {
            Node agent = (Node) agentsIt.next();
            agent.setCoordinate(new CoordinateGraph(agent));
            if (adjacencyMap.get(agent) == null) {
                adjacencyMap.put(agent, new ArrayList());
            }
        }
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.CollectionSpace#clear()
     */
    public void clear() {
        super.clear();
        adjacencyMap.clear();
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.Discrete#calculateNeighbors(org.ascape.model.space.Node)
     */
    public List calculateNeighbors(Node cell) {
        return getNeighborsFor(cell);
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
    public Location findNearest(final Coordinate origin, Conditional condition, boolean includeOrigin, double distance) {
        return findNearestBFS(origin, condition, includeOrigin, distance);
    }

    public List findNeighbors(Node location) {
        return getNeighborsFor(location);
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.CollectionSpace#withinIterator(org.ascape.model.space.Coordinate, org.ascape.util.Conditional, boolean, double)
     */
    public Iterator withinIterator(Coordinate origin, Conditional condition, boolean includeSelf, double distance) {
        return bfsWithinIterator(((CoordinateGraph) origin).getLocation(), condition, includeSelf, distance);
    }

    /**
     * Returns the next cell within immediate neighborhood toward the requested
     * cell. If no path exists between the origin andthe target, returns the
     * origin. If more than one are same distance, properly randomizes results.
     * 
     * @param origin
     *            the current cell
     * @param target
     *            the cell that we are moving toward
     * @return the node
     */
    public Node findCellToward(Node origin, Node target) {
        return findCellTowardBFS(origin, target);
    }

    /**
     * Returns the next cell within immediate neighborhood furthest away from
     * the requested cell. If more than one are same distance, properly
     * randomizes results.
     * 
     * @param origin
     *            the current cell
     * @param target
     *            the cell that we are moving toward
     * @return the node
     */
    public Node findCellAway(Node origin, Node target) {
        return findCellAwayBFS(origin, target);
    }

    /**
     * Adds the supplied object (assumed to be an agent) to this graph.
     * 
     * @param o
     *            the agent to add
     * @param isParent
     *            the is parent
     * @return true, if add
     */
    public boolean add(Object o, boolean isParent) {
        adjacencyMap.put(o, new ArrayList());
        if (o instanceof Cell) {
            ((Node) o).setCoordinate(new CoordinateGraph((Node) o));
        } else {
            // don't set its coordinate if it's not a Cell - it will be a MapAgent, and so its coordinate
            // is a Coordinate2DContinuous
        }
        return super.add(o, isParent);
    }

    /**
     * Adds a neighbor (the target) to the source's list of neighbors. The
     * directed flag determines if the link is one-way.
     * 
     * @param source
     *            the agent to add a new neighbor to
     * @param target
     *            the neighbor to add
     * @param directed
     *            if the neighbor-link is one-way or two-way
     * @return true, if add neighbor
     */

    public boolean addNeighbor(Node source, Node target, boolean directed) {
        if (!this.contains(source)) {
            throw new RuntimeException("Src agent is not in Graph");
        }
        if (!this.contains(target)) {
            throw new RuntimeException("Target agent is not in Graph");
        }
        // add to hash map
        boolean added = ((ArrayList) adjacencyMap.get(source)).add(target);
        // add to neighbors list
        //source.getNeighbors().add(target);

        if (!directed) {
            added |= ((ArrayList) adjacencyMap.get(target)).add(source);
            //target.getNeighbors().add(source);
        }
        return added;
    }

    /**
     * Adds a neighbor (the target) to the source's list of neighbors. The
     * directed flag determines if the link is one-way.
     * 
     * @param source
     *            the agent to add a new neighbor to
     * @param target
     *            the neighbor to add
     * @param directed
     *            if the neighbor-link is one-way or two-way
     * @return true, if add neighbor safe
     */

    public boolean addNeighborSafe(Node source, Node target, boolean directed) {
        if (adjacencyMap.get(source) == null) {
            adjacencyMap.put(source, new ArrayList());
        }
        // add to hash map
        boolean added = ((List) adjacencyMap.get(source)).add(target);
        // add to neighbors list
        //source.getNeighbors().add(target);

        if (!directed) {
            if (adjacencyMap.get(target) == null) {
                adjacencyMap.put(target, new ArrayList());
            }
            added |= ((List) adjacencyMap.get(target)).add(source);
            //target.getNeighbors().add(source);
        }
        return added;
    }
    
    /**
     * Adds a neighbor (the target) to the source's list of neighbors. By
     * default, the connection is one-way.
     * 
     * @param source
     *            the source
     * @param target
     *            the target
     */
    public void addNeighbor(Node source, Node target) {
        addNeighbor(source, target, true);
    }

    /**
     * Remove agent b from a's list of neighbors. If b is found in the list,
     * return true. Else, return false.
     * 
     * @param source
     *            the source
     * @param target
     *            the target
     * @return true, if remove neighbor
     */
    public boolean removeNeighbor(Node source, Node target) {
        ArrayList list = (ArrayList) adjacencyMap.get(source);
        for (Iterator buckets = list.iterator(); buckets.hasNext();) {
            Node neighbor = (Node) buckets.next();
            if (neighbor == target) {
                list.remove(target);
                //source.getNeighbors().remove(target);
                return true;
            }
        }
        return false;
    }

    /**
     * Return an iterator of the agent source's neighbors.
     * 
     * @param source
     *            the agent of which neighbors are being returned
     * @return an iterator of agents
     */
    public Iterator neighborIterator(Node source) {
        return getNeighborsFor(source).iterator();
    }

    /**
     * Clears the source agent's list of neighbors.
     * 
     * @param source
     *            the agent of whose neighbors are being cleared
     */
    public void clearNeighbors(Node source) {
        for (Object neighbor : getNeighborsFor(source)) {
            removeNeighbor((Node) neighbor, source);
        }
        getNeighborsFor(source).clear();
    }

    /**
     * Gets the agent's list of neighbors.
     * 
     * @param agent
     *            the agent whose neighbors are being returned
     * @return list the neighbors
     */
    public List getNeighborsFor(Node agent) {
        if (adjacencyMap == null) {
            return null;
        }
        List result = (List) adjacencyMap.get(agent);
        return (result != null) ? result : Collections.EMPTY_LIST;
    }

    /**
     * Replaces the agent's list of neighbors with new neighbor.
     * 
     * @param agent
     *            the agent whose neighbors are being returned
     * @param newNeighbor
     *            the new neighbor
     * @param directed
     *            the directed
     * @return list the neighbors
     */
    public void replaceNeighbor(Node agent, Node newNeighbor, boolean directed) {
        adjacencyMap.remove(agent);
        addNeighborSafe(agent, newNeighbor, directed);
    }


    /**
     * Sets the agent's list of neighbors.
     * 
     * @param agent
     *            the agentwho neighbors are being set
     * @param neighbors
     *            the neighbors
     */
    public void setNeighborsFor(Node agent, List neighbors) {
        adjacencyMap.put(agent, neighbors);
        //agent.setNeighbors(neighbors);
    }

    /**
     * Removes the supplied object (agent) from this list.
     * 
     * @param o
     *            the agent to be removed
     * @return true, if remove
     */
    public boolean remove(Object o) {
        for (Iterator it = this.iterator(); it.hasNext();) {
            Node agent = (Node) it.next();
            ArrayList list = (ArrayList) adjacencyMap.get(agent);
            list.remove(o);
        }
        return super.remove(o);
    }

    /**
     * Returns whether target is a neighbor of source.
     * 
     * @param source
     *            the agent whose neighbor list is to be cheked
     * @param target
     *            the agent who is being searched for in the neighbor list
     * @return true if they're neighbors, else false
     */
    public boolean isNeighbor(Node source, Node target) {
        return getNeighborsFor(source).contains(target);
    }

    /**
     * Return the adjacenty map.
     * 
     * @return the map
     */
    public HashMap getAdjacencyMap() {
        return adjacencyMap;
    }

    /**
     * Sets the adjancency map.
     * 
     * @param adjacencyMap
     *            the new map object
     */
    public void setAdjacencyMap(HashMap adjacencyMap) {
        this.adjacencyMap = adjacencyMap;
    }
}
