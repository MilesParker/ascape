/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.space;

import java.util.Collection;

/**
 * A space list that is part of another space list. Any agents added to this
 * list will also be added to the super list, and any agents removed from this
 * list will be removed from the super list. Note that agents removed directly
 * from the super list will not be removed from this list.
 * 
 * @author Miles Parker
 * @version 3.0
 * @history 3.0 9/10/02 significant refactoring of space model hierarchy
 * @history 2.0 9/27/01 first in
 * @since 2.0
 */
public class SubListSpace extends ListSpace implements SubSpace {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * The backing collection.
     */
    private Space superSpace;

    /**
     * Constructs a sub-list list.
     */
    public SubListSpace() {
        super();
    }

    /**
     * Constructs a sub-list list.
     * 
     * @param extent
     *            a coordinate describing the size of this space
     */
    public SubListSpace(CoordinateDiscrete extent) {
        this();
        setExtent(extent);
    }

    /**
     * Populates the space and its super list with clones of the prototype
     * agent. First removes any currently existing members from the super list
     * to prevent orphans. Prototype agent should be set before calling this
     * method. (By default, the prototpye agent is a Node.)
     */
    public void populate() {
        superSpace.removeAll(this);
        super.populate();
        superSpace.addAll(this);
    }

    /**
     * Removes the supplied object (agent) from this list and its superlist. If
     * the agent is _not_ a member of this space, but is a member of the super
     * space, it is not removed from the super space.
     * 
     * @param o
     *            the agent to be removed
     * @return true if the agent was deleted, false otherwise
     */
    public boolean remove(Object o) {
        try {
            return super.remove(o) && superSpace.remove(o);
        } catch (NullPointerException e) {
            throw new RuntimeException("No superlist specified for " + getContext().getName());
        }
    }

    /**
     * Inserts all of the agents in the specified collection at the location
     * specified in this list, and at the end of the super list. Assumes (but
     * does not check) that all of the elements are instances of agent.
     * 
     * @param index
     *            the location at which to place the agents
     * @param c
     *            collection whose agents are to be added to the space
     * @return true if the space had new agents added
     */
    public boolean addAll(int index, Collection c) {
        try {
            boolean listAdded = super.addAll(index, c);
            boolean superSpaceAdded = superSpace.addAll(c);
            return listAdded && superSpaceAdded;
        } catch (NullPointerException e) {
            throw new RuntimeException("No superlist specified for " + getContext().getName());
        }
    }

    /**
     * Inserts all of the agents in the specified collection at the location
     * specified in this list, and at the end of the super list. Assumes (but
     * does not check) that all of the elements are instances of agent.
     * 
     * @param c
     *            collection whose agents are to be added to the space
     * @return true if the space had new agents added
     */
    public boolean addAll(Collection c) {
        try {
            boolean listAdded = super.addAll(c);
            boolean superSpaceAdded = superSpace.addAll(c);
            return listAdded && superSpaceAdded;
        } catch (NullPointerException e) {
            throw new RuntimeException("No superlist specified for " + getContext().getName());
        }
    }

    /**
     * Removes all of the agnets contained in the collection from this space
     * list and its super space list. No attempt is made to cache the removal;
     * the agents are all removed at once.
     * 
     * @param c
     *            collection whose agents are to be added to the space
     * @return true if the space had agents (but not neccessarily all?) removed
     */
    public boolean removeAll(Collection c) {
        try {
            return super.removeAll(c) && superSpace.removeAll(c);
        } catch (NullPointerException e) {
            throw new RuntimeException("No superlist specified for " + getContext().getName());
        }
    }

    /**
     * Retains only the elements in the space that are in the specified
     * collection. Removes from the super space list those agents that are in
     * this list but not in the specified reatined collection.
     * 
     * @param c
     *            collection whose agents are to be retained in the space
     * @return true if this space had agents removed
     */
    public boolean retainAll(Collection c) {
        boolean removedAllSub = superSpace.removeAll(this);
        boolean retainedAll = super.retainAll(c);
        boolean addedAllSub = superSpace.addAll(this);
        return removedAllSub && retainedAll && addedAllSub;
    }

    /**
     * Removes all agents from the space, and all members of this space from the
     * super list.
     */
    public void clear() {
        superSpace.removeAll(this);
        super.clear();
    }

    /**
     * Adds the to super.
     * 
     * @param index
     *            the index
     * @param o
     *            the o
     * @param isParent
     *            the is parent
     */
    protected void addToSuper(int index, Object o, boolean isParent) {
        try {
            superSpace.add(o, false);
            super.add(index, o, isParent);
        } catch (NullPointerException e) {
            throw new RuntimeException("No superlist specified for " + getContext().getName());
        }
    }

    /**
     * Adds the supplied object (agent) to this list at the specified location
     * and to end of the super list. The object is assumed to be an agent,
     * though that behavior may be loosened at some point.
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
        try {
            superSpace.add(o, false);
            return super.add(o, isParent);
        } catch (NullPointerException e) {
            throw new RuntimeException("No superlist specified for " + getContext().getName());
        }
    }

    /**
     * Adds the supplied object (agent) to this list at the specified location
     * and to end of the super list. The object is assumed to be an agent,
     * though that behavior may be loosened at some point.
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
        try {
            superSpace.add(o, false);
            super.add(index, o, isParent);
        } catch (NullPointerException e) {
            throw new RuntimeException("No superlist specified for " + getContext().getName());
        }
    }

    /**
     * Returns the super list for this sub list, that is, the list defined to
     * contain a superset of this space's agents.
     * 
     * @return superSpace the list used as super set
     */
    public Space getSuperSpace() {
        return superSpace;
    }

    /**
     * Sets the super list for this sub list, that is, the list defined to
     * contain a superset of this space's agents. If a superlist had allready
     * been assigned to this list, this list's elements are _not_ removed from
     * the previous super list. Any agents allready members of this list would
     * be added to the new superlist, however.
     * 
     * @param superSpace
     *            the list to assign as super set
     */
    public void setSuperSpace(Space superSpace) {
        this.superSpace = superSpace;
        if ((collection != null) && (collection.size() > 0)) {
            superSpace.addAll(this);
        }
    }

    /**
     * Moves an agent toward the specified agent in the context of the
     * superscape.
     * 
     * @param origin
     *            the agent moving
     * @param target
     *            the agent's target
     * @param distance
     *            the distance to move
     */
    public void moveToward(Location origin, Coordinate target, double distance) {
        getSuperSpace().moveToward(origin, target, distance);
    }

    /**
     * Moves an agent toward the specified agent in the context of the
     * superscape.
     * 
     * @param origin
     *            the agent moving
     * @param target
     *            the agent's target
     * @param distance
     *            the distance to move
     */
    public void moveAway(Location origin, Coordinate target, double distance) {
        getSuperSpace().moveAway(origin, target, distance);
    }


    /**
     * Returns the shortest distance between one agent and the other, in the
     * context of the superscape.
     * 
     * @param origin
     *            the starting cell
     * @param target
     *            the ending cell
     * @return the double
     */
    public double calculateDistance(Coordinate origin, Coordinate target) {
        return getSuperSpace().calculateDistance(origin, target);
    }

}
