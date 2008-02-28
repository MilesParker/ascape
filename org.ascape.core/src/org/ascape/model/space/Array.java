/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.space;


import java.util.List;

import org.ascape.util.Conditional;

/**
 * The Class Array.
 */
public abstract class Array extends Discrete {


    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * The Constant CACHE_RESULTS.
     */
    public static final boolean CACHE_RESULTS = false;

//    private List softRefs;
//    private Map foundQueries = new HashMap();

    /**
 * Constructs an array space.
 */
    public Array() {
    }

    /**
     * Constructs an array space of provided extent.
     * 
     * @param extent
     *            a coordinate describing the size of this space
     */
    public Array(CoordinateDiscrete extent) {
        this();
        setExtent(extent);
    }

//    public void initialize() {
//        clearCache();
//        super.initialize();
//    }

//    protected void clearCache() {
//        softRefs = new ArrayList(Math.max(0, getSize()));
//        foundQueries = new HashMap();
//    }

    /**
 * Returns a random element in the supplied rank that matches the supplied
 * condition, null if no matches.
 * 
 * @param origin
 *            the agent to find cells near
 * @param condition
 *            the condition that found cell must meet
 * @param rank
 *            the rank to return match in
 * @return the location
 */
    public abstract Location findRandomMatchInRank(Coordinate origin, Conditional condition, int rank);

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
     * @param maximumDistance
     *            the maximum distance to search within
     * @return the int
     */
    public abstract int findNearestMatchRank(Coordinate origin, Conditional condition, boolean includeOrigin, double maximumDistance);

    /**
     * Finds the nearest cell that meets some condition.
     * 
     * @param origin
     *            the agent to find cells near
     * @param condition
     *            the condition that found cell must meet
     * @param includeOrigin
     *            should supplied agent be included in the search
     * @param maximumDistance
     *            the maximum distance to search within
     * @return the location
     */
    public Location findNearest(Coordinate origin, Conditional condition, boolean includeOrigin, double maximumDistance) {
        int nearestRank = findNearestMatchRank(origin, condition, includeOrigin, maximumDistance);
        if (nearestRank >= 0) {
            return findRandomMatchInRank(origin, condition, nearestRank);
        } else {
            return null;
        }
    }

    /**
     * The Class FindKey.
     */
    class FindKey {

        /**
         * The origin.
         */
        Coordinate origin;
        
        /**
         * The include self.
         */
        boolean includeSelf;
        
        /**
         * The dist.
         */
        int dist;

        /**
         * Instantiates a new find key.
         * 
         * @param origin
         *            the origin
         * @param includeSelf
         *            the include self
         * @param dist
         *            the dist
         */
        public FindKey(Coordinate origin, boolean includeSelf, int dist) {
            this.dist = dist;
            this.includeSelf = includeSelf;
            this.origin = origin;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(Object o) {
            final FindKey find = (FindKey) o;
            if (o == null) return false;
            if (dist != find.dist) return false;
            if (includeSelf != find.includeSelf) return false;
            if (origin != origin) return false;

            return true;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        public int hashCode() {
            int result;
            result = origin.hashCode();
            result = 29 * result + (includeSelf ? 1 : 0);
            result = 29 * result + dist;
            return result;
        }
    }

//    double misses;
//    double hits;

    /**
 * Returns cells that are near the provided coordinate.
 * 
 * @param origin
 *            the agent to find cells near
 * @param distance
 *            the distance to form centralCells to return cells
 * @param includeOrigin
 *            should supplied agent be included in the return set
 * @param condition
 *            the condition
 * @return the list
 */
    public List findWithin(Coordinate origin, Conditional condition, boolean includeOrigin, double distance) {
        //For arrays it is typically going to be much quicker to copy the arrays and then check for a condition
        List result = null;
//        if (CACHE_RESULTS) {
//            FindKey findKey = new FindKey(origin, includeOrigin, (int) distance);
//            result = (List) foundQueries.get(findKey);
//            if (result == null) {
//                //Don't include conditional here -- it may change
//                result = findWithinImpl(origin, includeOrigin, distance);
//                if (result.size() == getSize()) {
//                    if (allLocations == null) {
//                        allLocations = Arrays.asList(toArray());
//                    }
//                    result = allLocations;
//                }
//                softRefs.add(new SoftReference(findKey));
//                foundQueries.put(findKey, result);
////                    misses++;
////                } else {
////                    hits++;
//            }
//        } else {
            result = findWithinImpl(origin, includeOrigin, distance);
//        }
//                if (Math.random() < 0.0001) {
//                    System.out.println("hit ratio: " + hits / (hits + misses) + "   cahche sie: " + foundQueries.size());
//                    hits = 0;
//                    misses = 0;
//                }
        return filter(result, condition);
    }

    /**
     * Returns cells that are near the provided coordinate.
     * 
     * @param origin
     *            the agent to find cells near
     * @param distance
     *            the distance to form centralCells to return cells
     * @param includeOrigin
     *            should supplied agent be included in the return set
     * @return the list
     */
    protected abstract List findWithinImpl(Coordinate origin, boolean includeOrigin, double distance);

    /**
     * Changes the agent at the foirmer cells location to the supplied cell.
     * 
     * @param currentCell
     *            the current cell
     * @param newCell
     *            the new cell
     */
    public void replace(Node currentCell, Node newCell) {
        set(currentCell.getCoordinate(), newCell);
    }

    /**
     * Swaps two cells in the graph.
     * 
     * @param one
     *            the one
     * @param two
     *            the two
     */
    public void swap(Node one, Node two) {
        Coordinate coordOne = one.getCoordinate();
        Coordinate coordTwo = two.getCoordinate();

        set(coordOne, two);
        set(coordTwo, one);
    }
}
