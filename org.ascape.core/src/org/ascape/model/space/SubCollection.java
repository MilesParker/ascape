/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.space;

import java.util.Collection;

/**
 * A space collection that is part of another space collection. Any agents added
 * to this collection will also be added to the super collection, and any agents
 * removed from this collection will be removed from the super collection. Note
 * that agents removed directly from the super collection will not be removed
 * from this collection.
 * 
 * @author Miles Parker
 * @version 3.0
 * @history 3.0 9/10/02 significant refactoring of space model hierarchy
 * @history 2.0 9/27/01 first in
 * @since 2.0
 */
public abstract class SubCollection extends CollectionSpace implements SubSpace {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * The backing collection.
     */
    private Space superSpace;

    /**
     * Constructs a sub-collection.
     */
    public SubCollection() {
        super();
    }

    /**
     * Constructs a sub-collection.
     * 
     * @param extent
     *            a coordinate describing the size of this space
     */
    public SubCollection(CoordinateDiscrete extent) {
        this();
        setExtent(extent);
    }

    /**
     * Populates the space and its super collection with clones of the prototype
     * agent. First removes any currently existing members from the super
     * collection to prevent orphans. Prototype agent should be set before
     * calling this method. (By default, the prototpye agent is a Node.)
     */
    public void populate() {
        superSpace.removeAll(this);
        super.populate();
        superSpace.addAll(this);
    }

    /**
     * Adds the supplied object (agent) to this list at the specified location
     * and to end of the super list. The object is assumed to be an agent,
     * though that behavior may be loosened at some point.
     * 
     * @param isParent
     *            should this space be made the parent space of the agent?
     * @param o
     *            the o
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
     * Removes the supplied object (agent) from this collection and its
     * superlist. If the agent is _not_ a member of this space, but is a member
     * of the super space, it is not removed from the super space.
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
     * collection and its super space collection. No attempt is made to cache
     * the removal; the agents are all removed at once.
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
     * collection. Removes from the super space collection those agents that are
     * in this collection but not in the specified reatined collection.
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
     * super collection.
     */
    public void clear() {
        superSpace.removeAll(this);
        super.clear();
    }

    /**
     * Returns the super collection for this sub collection, that is, the
     * collection defined to contain a superset of this space's agents.
     * 
     * @return superSpace the collection used as super set
     */
    public Space getSuperSpace() {
        return superSpace;
    }

    /**
     * Sets the super collection for this sub collection, that is, the
     * collection defined to contain a superset of this space's agents. If a
     * superlist had allready been assigned to this collection, this
     * collection's elements are _not_ removed from the previous super
     * collection. Any agents allready members of this collection would be added
     * to the new superlist, however.
     * 
     * @param superSpace
     *            the collection to assign as super set
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
