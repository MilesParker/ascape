/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.space;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.ascape.util.RandomIterator;
import org.ascape.util.ResetableIterator;



/**
 * A space with treates itself as its only member. This provides agents with a
 * way to have single rules implemented upon them and to support views.
 * 
 * @author Miles Parker
 * @version 3.0
 * @history 3.0 9/10/02 significant refactoring of space model hierarchy
 * @history 1.5 1/10/2000 Fixed some rule propogation related issues
 * @since 1.0
 */
public class Singleton extends CollectionSpace {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construct a new Singleton.
     */
    public Singleton() {
        super();
        //membersActive = false;
        geometry = new Geometry(1, true);
        collection.add(this);
        //lock the backing collection down.
        collection = Collections.unmodifiableList((List) collection);
    }

    /**
     * Returns the number of members of this space.
     * 
     * @return the size
     */
    public int getSize() {
        return 1;
    }

    /**
     * Normally, create the basic strucutre of the space. Here, a noop, since
     * the space member is the space itself.
     */
    public void construct() {
    }

    /**
     * Normally, populates the space with instances of its protoype agent. Here,
     * a noop, since the space member is the space itself.
     */
    public void populate() {
    }

    /**
     * The Class LocationRandomIterator.
     */
    private class LocationRandomIterator implements RandomIterator, Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        /**
         * The i.
         */
        private int i;

        /* (non-Javadoc)
         * @see org.ascape.util.ResetableIterator#first()
         */
        public void first() {
            i = 0;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            return i != 1;
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        public Object next() {
            if (i != 1) {
                i++;
                return getContext();
            } else {
                throw new RuntimeException("No objects available in iterator.");
            }
        }

        /* (non-Javadoc)
         * @see org.ascape.util.Randomizable#randomize()
         */
        public void randomize() {
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
         */
        public void remove() {
            throw new UnsupportedOperationException("Can't remove an object from a immutable space.");
        }
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.CollectionSpace#iterator()
     */
    public Iterator iterator() {
        return new LocationRandomIterator();
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.CollectionSpace#safeIterator(int, int)
     */
    public ResetableIterator safeIterator(int start, int limit) {
        return new LocationRandomIterator();
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.CollectionSpace#safeIterator()
     */
    public ResetableIterator safeIterator() {
        return new LocationRandomIterator();
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.CollectionSpace#safeRandomIterator()
     */
    public RandomIterator safeRandomIterator() {
        return new LocationRandomIterator();
    }

    /**
     * Returns all agents in the space as an array.
     * 
     * @return the locations
     */
    public Location[] getLocations() {
        Location[] agents = new Location[1];
        agents[0] = (Location) getContext();
        return agents;
    }

    /**
     * Moves an agent toward the specified agent.
     * 
     * @param origin
     *            the agent moving
     * @param target
     *            the agent's target
     * @param distance
     *            the distance to move
     */
    public void moveToward(Location origin, Coordinate target, double distance) {
        if (!origin.equals(target)) {
            throw new RuntimeException("Tried to move toward a different coordinate within an agent space!");
        }
    }

    /**
     * Moves an agent toward the specified agent.
     * 
     * @param origin
     *            the agent moving
     * @param target
     *            the agent's target
     * @param distance
     *            the distance to move
     */
    public void moveAway(Location origin, Coordinate target, double distance) {
        if (!origin.equals(target)) {
            throw new RuntimeException("Tried to move away from a different coordinate within an agent space!");
        }
    }

    /**
     * Returns the shortest distance between one agent and another. In this
     * case, must be 0.
     * 
     * @param origin
     *            the starting cell
     * @param target
     *            the ending cell
     * @return the double
     */
    public double calculateDistance(Coordinate origin, Coordinate target) {
        if (contains(origin) && (origin == target)) {
            return 0;
        } else {
            throw new RuntimeException("Tried to calcualte distance for non-space members.");
        }
    }

    /**
     * Returns a string representing this space.
     * 
     * @return the string
     */
    public String toString() {
        if (getContext().getName() != null) {
            return getContext().getName();
        } else {
            return "A Singleton";
        }
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.CollectionSpace#findRandom()
     */
    public Location findRandom() {
        return (Location) getContext();
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.CollectionSpace#findRandomCoordinate()
     */
    public Coordinate findRandomCoordinate() {
        return ((Location) getContext()).getCoordinate();
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.CollectionSpace#setExtent(org.ascape.model.space.Coordinate)
     */
    public void setExtent(Coordinate extent) {
        throw new RuntimeException("Tried to set the extent of a Singleton, which can have one and nly one member.");
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.CollectionSpace#isMutable()
     */
    public final boolean isMutable() {
        return false;
    }
}
