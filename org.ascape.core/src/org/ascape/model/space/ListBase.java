/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.space;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.ascape.util.Conditional;



/**
 * A one-dimensional, fixed-size, collection of agents providing services
 * described for space.
 * 
 * @author Miles Parker
 * @version 3.0
 * @history 3.0 9/10/02 significant refactoring of space model hierarchy
 * @history 2.0 10/29/01 changes to support new continuous space functionality
 * @history 1.9.2 3/5/01 first in, to absorb common functionality of Scape and
 *          ScapeArray1D
 * @since 1.0
 */
public abstract class ListBase extends Array implements List {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * Have there been operations on the list that require a coordinate setting
     * operations?.
     */
    private boolean coordinateSweepNeeded;

    /**
     * Constructs a space array.
     */
    public ListBase() {
        super();
        setExtent(new Coordinate1DDiscrete(0));
    }

    /**
     * Constructs a space array of provided extent, populated with clones of
     * provided agent.
     * 
     * @param extent
     *            a coordinate describing the size of this space
     */
    public ListBase(CoordinateDiscrete extent) {
        this();
        setExtent(extent);
    }

    /**
     * Populates the space with clones of the prototype agent. Prototype agent
     * should be set before calling this method.
     */
    public void populate() {
        super.populate();
        if (extent != null) {
            coordinateSweepNeeded = true;
        }
    }

    /**
     * Adds the supplied object (assumed to be an agent) to this list.
     * 
     * @param index
     *            location to insert the new agent
     * @param o
     *            the agent to add
     */
    public void add(int index, Object o) {
        add(index, o, true);
    }

    /**
     * Adds the supplied object (agent) to this list. The object is assumed to
     * be an agent, though that behavior may be loosened at some point.
     * 
     * @param index
     *            location to insert the new agent
     * @param o
     *            the agent to add
     * @param isParent
     *            should this space be made the parent space of the agent?
     * @throws ClassCastException
     *             if the object is not an instance of agent
     */
    public void add(int index, Object o, boolean isParent) {
        if (!(o instanceof Location)) {
            //This may change at some point..
            throw new ClassCastException("Spaces expect Locations only.");
        }
        //If we decide to allow non-agents, we may change to check for agent here..
        if (isParent) {
            if (!((Location) o).isDelete()) {
                if (index < size()) {
                    //We must have specified an index, so we need to do a delete sweep
                    deleteSweep();
                    ((List) collection).add(index, o);
                } else {
                    //Otherwise, must be attempting to add the agent to the end of the list, the backing list of which may be larger
                    collection.add(o);
                }
            } else {
                //we do not need to be added, because we are allready in list
                //But we do need a coordinate sweep
                coordinateSweepNeeded = true;
            }
            if ((o instanceof Node) && (index == size())) {
                //We do not have to do a coordiante sweep if adding at the end..
                ((Node) o).setCoordinate(new Coordinate1DDiscrete(getSize()));
            } else {
                //We have to do a complete coordinate sweep in this case, as its likely we've added the agent somewhere in the middle of the list
                coordinateSweepNeeded = true;
            }
        } else {
            //We don't have to worry about parenthood, so just add the agent
            if (index < size()) {
                //We must have specified an index, so we need to do a delete sweep
                deleteSweep();
                ((List) collection).add(index, o);
            } else {
                //Otherwise, must be attempting to add the agent to the end of the list, the backing list of which may be larger
                collection.add(o);
            }
        }
        //setSize(getSize() + 1);
        setSize(getSize() + 1);
    }

    /**
     * Adds the supplied object (agent) to this list. The object is assumed to
     * be an agent, though that behavior may be loosened at some point.
     * 
     * @param o
     *            the agent to add
     * @param isParent
     *            should this space be made the parent space of the agent?
     * @return true, if add
     * @throws ClassCastException
     *             if the object is not an instance of agent
     */
    public boolean add(Object o, boolean isParent) {
        if (isParent) {
            //We do not have to do a coordiante sweep if adding at the end..
            //warning, this code assumes that add will occur at end, which is the case with aarray list but might no be w/ other lists
            //todo check code so that is works w/ an lists
            if (o instanceof Node) {
                ((Node) o).setCoordinate(new Coordinate1DDiscrete(getSize()));
            }
        }
        return super.add(o, isParent);
    }

    /**
     * Adds the supplied object (assumed to be an agent) to this list.
     * 
     * @param coordinate
     *            the coordinate to insert the new agent at
     * @param o
     *            the agent to add
     */
    public void add(CoordinateDiscrete coordinate, Object o) {
        add(coordinate.getValueAtDimension(1), o, true);
    }

    /**
     * Inserts all of the agents in the specified collection at the location
     * specified. Assumes (but does not check) that all of the elements are
     * instances of agent.
     * 
     * @param index
     *            the location at which to place the agents
     * @param c
     *            collection whose agents are to be added to the space
     * @return true if the space had new agents added
     */
    public boolean addAll(int index, Collection c) {
        boolean addedAll = ((List) collection).addAll(index, c);
        setSize(getSize() + c.size());
        return addedAll;
    }

    /**
     * Removes the agent at the specified coordinate from this list.
     * 
     * @param coordinate
     *            coordinate of the agent to delete
     * @return the agent removed
     */
    public Object remove(CoordinateDiscrete coordinate) {
        return remove(coordinate.getValueAtDimension(1));
    }

    /**
     * Removes the agent at the specified position from this list.
     * 
     * @param index
     *            location of the agent to delete
     * @return the agent removed
     */
    public Object remove(int index) {
        deleteSweep();
        //May be inefficient, but we want to use the same general method..
        //Will evaluate
        Object removed = ((List) collection).get(index);
        remove(removed);
        return removed;
    }

    /**
     * Removes the supplied object (agent) from this list.
     * 
     * @param o
     *            the agent to be removed
     * @return true if the agent was deleted, false otherwise
     */
    public boolean remove(Object o) {
        if ((o instanceof Location) && getContext().isHome((Location) o)) {
            coordinateSweepNeeded = true;
        }
        return super.remove(o);
    }

    /**
     * Sets the agent at the specified coordinate to the supplied agent. The
     * object is expected to be an agent, though this requirement may be
     * loosened at some point.
     * 
     * @param index
     *            the lcoation to add the agent at
     * @param o
     *            the agent to add
     * @return the agent (if any) replaced at the location
     */
    public Object set(int index, Object o) {
        return set(index, (Node) o, true);
    }

    /**
     * Sets the agent at the specified coordinate to the supplied agent. The
     * object is expected to be an agent, though this requirement may be
     * loosened at some point.
     * 
     * @param index
     *            the location to add the agent at
     * @param o
     *            the agent to add
     * @param isParent
     *            should this space be made the parent space of the agent?
     * @return the node
     */
    public Node set(int index, Location o, boolean isParent) {
        if (!(o instanceof Location)) {
            //This may change at some point..
            throw new ClassCastException("Spaces expect Locations only.");
        }
        if (isParent) {
            coordinateSweepNeeded = true;
        }
        deleteSweep();
        return (Node) ((List) collection).set(index, o);
    }

    /**
     * Sets the geometry of this space. Must of course be one-dimensional.
     * 
     * @param geometry
     *            the basic geometry of this space
     */
    public void setGeometry(Geometry geometry) {
        super.setGeometry(geometry);
        if (geometry.getDimensionCount() != 1) {
            throw new RuntimeException("Tried to assign an inappropriate geometry.");
        }
    }

    /**
     * Returns the index in this list of the first occurrence of the agent.
     * 
     * @param o
     *            the agent to search for.
     * @return the index in this list of the first matching agent
     */
    public int indexOf(Object o) {
        deleteSweep();
        return ((List) collection).indexOf(o);
    }

    /**
     * Returns the index in this list of the last occurrence of the agent.
     * (Obviously, this will typically be the same as indexOf, as agents do not
     * typically appear in the same space twice.)
     * 
     * @param o
     *            the agent to search for.
     * @return the index in this list of the first matching agent
     */
    public int lastIndexOf(Object o) {
        deleteSweep();
        return ((List) collection).lastIndexOf(o);
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.Discrete#calculateDistance(org.ascape.model.space.Coordinate, org.ascape.model.space.Coordinate)
     */
    public double calculateDistance(Coordinate origin, Coordinate target) {
        int o = ((Coordinate1DDiscrete) origin).getXValue();
        int t = ((Coordinate1DDiscrete) target).getXValue();
        if (getGeometry().isPeriodic()) {
            return calculateDistance(o, t, getSize());
        } else {
            if (o > t) {
                return o - t;
            } else { //t >= o
                return t - o;
            }
        }
    }

    /**
     * Returns cells that are near the provided cell. Need to fix for
     * non-periodic space!
     * 
     * @param origin
     *            the coordinate to find cells near
     * @param distance
     *            the distance to form centralCells to return cells
     * @param includeSelf
     *            should supplied agent be included in the return set
     * @return the list
     */
    public List findWithinImpl(Coordinate origin, boolean includeSelf, double distance) {
        int dist = (int) distance;
        deleteSweep();
        Node[] agentsNear;
//        try {
        int rank = ((Coordinate1DDiscrete) origin).getValue();
        int index = 0;
        int listSize = collection.size();
        if (listSize > dist * 2 + 1) {
            agentsNear = new Node[dist * 2 + (includeSelf ? 1 : 0)];
            for (int x = Math.min(listSize, listSize + (rank - dist)); x < listSize; x++) {
                agentsNear[index] = (Node) ((List) collection).get(x);
                index++;
            }
            for (int x = Math.max(0, rank - dist); x < Math.min(listSize, rank + dist + 1); x++) {
                if (includeSelf || (x != rank)) {
                    agentsNear[index] = (Node) ((List) collection).get(x);
                    index++;
                }
            }
            for (int x = 0; x < Math.max(0, ((rank + dist) - listSize) + 1); x++) {
                agentsNear[index] = (Node) ((List) collection).get(x);
                index++;
            }
        } else {
            agentsNear = new Node[listSize - (includeSelf ? 0 : 1)];
            for (int x = 0; x < listSize; x++) {
                if (includeSelf || (x != rank)) {
                    agentsNear[index] = (Node) ((List) collection).get(x);
                    index++;
                }
            }
        }
//    	    return agentsNear;
        ArrayList found = new ArrayList();
        for (int i = 0; i < agentsNear.length; i++) {
            found.add(agentsNear[i]);
        }
        return found;
//        } catch (ArrayIndexOutOfBoundsException e) {
//            throw new RuntimeException("Central Location not in collection.");
//        }
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
    public Node findCellToward(Node origin, Node target) {
        if (origin == target) {
            return origin;
        }
        int ox = ((Coordinate1DDiscrete) origin.getCoordinate()).getValue();
        int tx = ((Coordinate1DDiscrete) target.getCoordinate()).getValue();
        if (!getGeometry().isPeriodic()) {
            if (ox > tx) {
                ox--;
            } else if (ox < tx) {
                ox++;
            }
        } else {
            int ex = getSize();
            if (ox > tx) {
                //is inside distance less than outside distance?
                if ((ox - tx) < (tx + (ex - ox))) {
                    ox--;
                } else {
                    ox++;
                }
            } else if (ox < tx) {
                if ((tx - ox) < (ox + (ex - tx))) {
                    ox++;
                } else {
                    ox--;
                }
            }
            if (ox >= ex) {
                ox = 0;
            } else if (ox < 0) {
                ox = ex - 1;
            }
        }
        return (Node) get(ox);
    }

    /**
     * Returns the cell within immediate neighborhood furthest away from the
     * requested cell.
     * 
     * @param origin
     *            the current cell
     * @param target
     *            the cell that we are moving toward
     * @return the node
     */
    public Node findCellAway(Node origin, Node target) {
        if (origin == target) {
            return origin.findRandomNeighbor();
        }
        int ox = ((Coordinate1DDiscrete) origin.getCoordinate()).getXValue();
        int tx = ((Coordinate1DDiscrete) target.getCoordinate()).getXValue();
        int cx = getSize();
        if (!getGeometry().isPeriodic()) {
            if (ox < tx) {
                ox--;
                if (ox < 0) {
                    ox = 0;
                }
            } else {
                ox++;
                if (ox >= cx) {
                    ox = cx - 1;
                }
            }
        } else {
            int rx = cx / 2;
            int dx = tx - ox;
            if (dx > 0) {
                if (dx < rx - 1) {
                    if (ox > 0) {
                        ox--;
                    } else {
                        ox = cx - 1;
                    }
                } else {
                    if (ox < cx - 1) {
                        ox++;
                    } else {
                        ox = 0;
                    }
                }
            } else if (dx < 0) {
                if (dx > -rx - 1) {
                    if (ox < cx - 1) {
                        ox++;
                    } else {
                        ox = 0;
                    }
                } else {
                    if (ox > 0) {
                        ox--;
                    } else {
                        ox = cx - 1;
                    }
                }
            }
        }
        return (Node) get(ox);
    }

    /**
     * Returns the nearest rank that includes a cell which matches the supplied
     * condition, -1 if no matches.
     * 
     * @param origin
     *            the agent to find cells near
     * @param condition
     *            the condition that found cell must meet
     * @param includeOrigin
     *            should supplied agent (rank 0) be included in the search
     * @return the int
     */
    public int findNearestMatchRank(Coordinate origin, Conditional condition, boolean includeOrigin) {
        if (getGeometry().isPeriodic()) {
            return findNearestMatchRank(origin, condition, includeOrigin, getSize() / 2);
        } else {
            int pos = ((Coordinate1DDiscrete) origin).getXValue();
            //go out to the furthest possible rank...
            return findNearestMatchRank(origin, condition, includeOrigin, Math.max(pos - 1, getSize() - pos - 1));
        }
    }

    /**
     * Returns the nearest rank that includes a cell which matches the supplied
     * condition, -1 if no matches. Special Note: uses euclidian, _not_ geometry
     * specific distance; this will be fixed soon.
     * 
     * @param origin
     *            the agent to find cells near
     * @param condition
     *            the condition that found cell must meet
     * @param includeOrigin
     *            should supplied agent (rank 0) be included in the search
     * @param maximumDistance
     *            the maximum distance to search within
     * @return the int
     */
    public int findNearestMatchRank(Coordinate origin, Conditional condition, boolean includeOrigin, double maximumDistance) {
        for (int i = (includeOrigin ? 0 : 1); i <= maximumDistance; i++) {
            if (findFirstMatchInRank(origin, condition, i) != null) {
                return i;
            }
        }
        //No matching coordinates found
        return -1;
    }

    /**
     * Check down.
     * 
     * @param pos
     *            the pos
     * @param condition
     *            the condition
     * @param rank
     *            the rank
     * @return the int
     */
    private int checkDown(int pos, Conditional condition, int rank) {
        pos -= rank;
        if (pos < 0) {
            if (getGeometry().isPeriodic()) {
                pos += getSize();
            } else {
                return -1;
            }
        }
        if (condition.meetsCondition(get(pos))) {
            return pos;
        } else {
            return -1;
        }
    }

    /**
     * Check up.
     * 
     * @param pos
     *            the pos
     * @param condition
     *            the condition
     * @param rank
     *            the rank
     * @return the int
     */
    private int checkUp(int pos, Conditional condition, int rank) {
        pos += rank;
        if (pos >= getSize()) {
            if (getGeometry().isPeriodic()) {
                pos -= getSize();
            } else {
                return -1;
            }
        }
        if (condition.meetsCondition(get(pos))) {
            return pos;
        } else {
            return -1;
        }
    }

    /**
     * Returns a random element in the supplied rank that matches the supplied
     * condition, null if no matches. Special Note: uses euclidian, _not_
     * geometry specific distance; this will be fixed soon.
     * 
     * @param origin
     *            the agent to find cells near
     * @param condition
     *            the condition that found cell must meet
     * @param rank
     *            the rank to return match in
     * @return the node
     */
    public Node findFirstMatchInRank(Coordinate origin, Conditional condition, int rank) {
        int pos = ((Coordinate1DDiscrete) origin).getXValue();
        //Don't bother with random selection
        pos = checkUp(pos, condition, rank);
        if (pos == -1) {
            pos = checkDown(((Coordinate1DDiscrete) origin).getXValue(), condition, rank);
        }
        if (pos != -1) {
            return (Node) get(pos);
        } else {
            return null;
        }
    }

    /**
     * Returns a random element in the supplied rank that matches the supplied
     * condition, null if no matches. Special Note: uses euclidian, _not_
     * geometry specific distance; this will be fixed soon.
     * 
     * @param origin
     *            the agent to find cells near
     * @param condition
     *            the condition that found cell must meet
     * @param rank
     *            the rank to return match in
     * @return the location
     */
    public Location findRandomMatchInRank(Coordinate origin, Conditional condition, int rank) {
        int pos = ((Coordinate1DDiscrete) origin).getXValue();
        if (randomIs()) {
            pos = checkUp(pos, condition, rank);
            if (pos == -1) {
                pos = checkDown(((Coordinate1DDiscrete) origin).getXValue(), condition, rank);
            }
        } else {
            pos = checkDown(pos, condition, rank);
            if (pos == -1) {
                pos = checkUp(((Coordinate1DDiscrete) origin).getXValue(), condition, rank);
            }
        }
        if (pos != -1) {
            return (Node) get(pos);
        } else {
            return null;
        }
    }

    /**
     * Returns a list iterator across all agents in this space. The notes for
     * iterator apply equally to the list iterators.
     * 
     * @return an iterator over the agents in space order
     */
    public ListIterator listIterator() {
        deleteSweep();
        return ((List) collection).listIterator();
    }

    /**
     * Returns a list iterator across all agents in this space, beginning at the
     * specified index. The notes for iterator apply equally to the list
     * iterators.
     * 
     * @param index
     *            index of first agent to be returned
     * @return an iterator over the agents in space order
     */
    public ListIterator listIterator(int index) {
        deleteSweep();
        return ((List) collection).listIterator(index);
    }

    /**
     * Returns a view of a sublist of this list between the specified indices.
     * See important Java List comments.
     * 
     * @param fromIndex
     *            the low index (inclusive)
     * @param toIndex
     *            the high index (exclusive)
     * @return a view of the specified range within this list.
     * @throws IndexOutOfBoundsException
     * for an illegal endpoint index value @ see java.util.List
     */
    public List subList(int fromIndex, int toIndex) {
        deleteSweep();
        return ((List) collection).subList(fromIndex, toIndex);
    }

    /**
     * Walks through each agent, setting the agent's coordinates. Warning --
     * should only be called on space's of which all agents have the space as a
     * primary space.
     */
    public void coordinateSweep() {
        if ((coordinateSweepNeeded) && !((getContext().getPrototype() instanceof NodeOccupant) && (((NodeOccupant) getContext().getPrototype()).getHostSpace() != null))) {
            //Here we use an iterator becuase we are sure that we the list will not be modified
            Iterator i = iterator();
            int index = 0;
            while (i.hasNext()) {
                Node candidate = (Node) i.next();
                candidate.setCoordinate(new Coordinate1DDiscrete(index));
                index++;
            }
        }
        coordinateSweepNeeded = false;
    }

    /**
     * Is a coordinate location sweep needed for this space? Intended for
     * internal purposes.
     * 
     * @return true, if is coordinate sweep needed
     */
    public boolean isCoordinateSweepNeeded() {
        return coordinateSweepNeeded;
    }

    /**
     * Returns the agents at the specified index.
     * 
     * @param index
     *            index of the agent to return.
     * @return the agent at the specified place in the space
     * @throws IndexOutOfBoundsException
     *             if the index is out of range
     */
    public Object get(int index) {
        deleteSweep();
        return ((List) collection).get(index);
    }

    /**
     * Returns the agents at the specified index.
     * 
     * @param coordinate
     *            location of the agent to return.
     * @return the agent at the specified place in the space
     * @throws IndexOutOfBoundsException
     *             if the index is out of range
     */
    public Node get(CoordinateDiscrete coordinate) {
        deleteSweep();
        return (Node) ((List) collection).get(((Coordinate1DDiscrete) coordinate).getValue());
    }
}
