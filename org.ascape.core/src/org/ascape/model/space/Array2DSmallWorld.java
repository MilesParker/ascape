/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.space;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ascape.util.Conditional;



/**
 * A two-dimensional space providing effecient implementations for von Neumann
 * neighbors.
 * 
 * @author Miles Parker
 * @version 2.0
 * @history 2.0 10/29/01 changes to support new continuous space functionality,
 *          (int) getDistance is now (double) calcualteDistance
 * @history 1.9.3 3-4/2001 QA fixes
 * @history 1.9.2 10/6/99 first in
 * @since 1.9.2
 */
//public class Array2DSmallWorld extends Array2DBase {
public class Array2DSmallWorld extends Array2D {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The radius.
     */
    protected int radius = 1;

    /**
     * The random edge ratio.
     */
    protected double randomEdgeRatio;

    /**
     * The distance for pair.
     */
    private Map distanceForPair;

    /**
     * The small world cells.
     */
    Set smallWorldCells;

    /**
     * Constructs a 2-dimensional small world space.
     */
    public Array2DSmallWorld() {
        super();
    }

    /**
     * Constructs a 2-dimensional small world space of provided extent.
     * 
     * @param extent
     *            a coordinate describing the size of this space
     */
    public Array2DSmallWorld(CoordinateDiscrete extent) {
        this();
        setGeometry(new Geometry(2, true, Geometry.MOORE));
        setExtent(extent);
    }

    /**
     * Initializes the space. For small worlds, we want to calculate the
     * neighborhood each time.
     */
    public void initialize() {
        //Put call to require calculate neighbors here..
        super.initialize();
//        distanceForPair = new HashMap();
        smallWorldCells = new HashSet();
        //calculateAllDistances();
    }

    /**
     * The Class CellPair.
     */
    class CellPair {

        /**
         * The v1.
         */
        Coordinate2DDiscrete v1;
        
        /**
         * The v2.
         */
        Coordinate2DDiscrete v2;

        /**
         * Instantiates a new cell pair.
         * 
         * @param v1
         *            the v1
         * @param v2
         *            the v2
         */
        public CellPair(Coordinate v1, Coordinate v2) {
            this.v1 = (Coordinate2DDiscrete) v1;
            this.v2 = (Coordinate2DDiscrete) v2;
        }

        /**
         * Instantiates a new cell pair.
         * 
         * @param v1
         *            the v1
         * @param v2
         *            the v2
         */
        public CellPair(Node v1, Node v2) {
            this(v1.getCoordinate(), v2.getCoordinate());
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public final boolean equals(Object o) {
            final CellPair other = (CellPair) o;
            return (((v1.equals(other.v1)) && (v2.equals(other.v2))) ||
                ((v1.equals(other.v2)) && (v2.equals(other.v1))));
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        public final int hashCode() {
            int result;
            result = v1.hashCode() + v2.hashCode();
            return result;
        }
    }

    /**
     * The Class CalcThread.
     */
    class CalcThread extends Thread {

        /**
         * The running.
         */
        boolean running = true;
        
        /**
         * The i.
         */
        int i;

        /* (non-Javadoc)
         * @see java.lang.Thread#run()
         */
        public void run() {
            while (running) {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(((double) i / (double) getSize()) + "% complete");
            }
        }
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.Discrete#calculateNeighbors(org.ascape.model.space.Node)
     */
    public List calculateNeighbors(Node cell) {
        List neighbors = new ArrayList(findWithinMoore(cell.getCoordinate(), false, radius));
        if (randomEdgeRatio > 0.0) {
            for (int i = 0; i < neighbors.size(); i++) {
                if (getRandom().nextDouble() < randomEdgeRatio) {
                    smallWorldCells.add(cell);
                    boolean tryAgain;
                    do {
                        tryAgain = false;
                        neighbors.remove(i);
                        neighbors.add(i, findRandom());
                        for (int j = 0; j < neighbors.size(); j++) {
                            if ((j != i) && (neighbors.get(j) == neighbors.get(i))) {
                                tryAgain = true;
                            }
                        }
                    } while (tryAgain);
                }
            }
        }
        return neighbors;
    }

    /**
     * Calculate all distances.
     */
    void calculateAllDistances() {
        System.out.println("Calculating distances..");
        CalcThread monitor = new CalcThread();
        monitor.start();
        //We are using array instead of iterator for direct access for inner pair-wise loop
        Object[] all = toArray();
        for (; monitor.i < all.length; monitor.i++) {
            Node origin = (Node) all[monitor.i];
            for (int j = monitor.i + 1; j < all.length; j++) {
                Node target = (Node) all[j];
                double result = calculateDistance(origin, target);
                distanceForPair.put(new CellPair(origin, target), new Double(result));
            }
        }
        monitor.running = false;
        System.out.println("Finished calcualting distances..");
    }

    /**
     * Finds the nearest agent that meets some condition. Spaces without
     * coordinate meaning should override this method.
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

    /* (non-Javadoc)
     * @see org.ascape.model.space.Array2D#findNearestMatchRank(org.ascape.model.space.Coordinate, org.ascape.util.Conditional, boolean, double)
     */
    public int findNearestMatchRank(Coordinate origin, Conditional condition, boolean includeOrigin, double maximumDistance) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Method not appropriate for this space.");
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.Array2D#findRandomMatchInRank(org.ascape.model.space.Coordinate, org.ascape.util.Conditional, int)
     */
    public Location findRandomMatchInRank(Coordinate origin, Conditional condition, int rank) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Method not appropriate for this space.");
    }

//    protected List findWithinArrayCopy(Coordinate origin, boolean includeOrigin, double distance) {
//        //Not appropriate to use array copy method here so use the method below
//        //This method should not be called directly in any case.
//        return findWithin(coordinate, null, includeOrigin, distance);
//    }

    /*
     * Returns all agents within the specified distance of the agent.
     * @param originCoor the coordinate at the center of the search
     * @param includeSelf   whether or not the starting agent should be included in the search
     * @param distance the distance agents must be within to be included
     */
/*    public List findWithin(Coordinate originCoor, Conditional condition, boolean includeSelf, double distance) {
        int dist = (int) distance;
        List result = null;
        Node origin = getCell((CoordinateDiscrete) originCoor);
        if (dist == 1) {
            if (!includeSelf) {
                result = origin.findNeighbors();
            } else {
                result = new ArrayList(origin.findNeighbors());
                result.add(origin);
            }
        } else {
            FindKey findKey = new FindKey(origin, includeSelf, dist);
            result = (List) foundQueries.get(findKey);
            if (result == null) {
                //Don't include conditional here -- it may change
                result = iteratorToList(withinIterator(originCoor, null, includeSelf, distance));
                if (result.size() == getSize()) {
                    result = allLocations;
                }
                softRefs.add(new SoftReference(findKey));
                foundQueries.put(findKey, result);
//                misses++;
//            } else {
//                hits++;
            }
//            if (Math.random() < 0.0001) {
//                System.out.println("hit ratio: " + hits / (hits + misses) + "   cahche sie: " + foundQueries.size());
//                hits = 0;
//                misses = 0;
//            }
        }
        result = filter(result, condition);
        return result;

    }*/

    /* (non-Javadoc)
 * @see org.ascape.model.space.Array#findWithinImpl(org.ascape.model.space.Coordinate, boolean, double)
 */
public List findWithinImpl(Coordinate origin, boolean includeSelf, double distance) {
        //old implementation appears to be significantly faster..
        return iteratorToList(new BFSWithinIterator(this, get(origin), null, includeSelf, distance));
//        return iteratorToList(new BFSWithinIterator2DSmallWorld(this, get((CoordinateDiscrete) origin), null, includeSelf, distance));
//        return findWithinMoore(origin, includeSelf, distance);
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.Discrete#bfsWithinIterator(org.ascape.model.space.Location, org.ascape.util.Conditional, boolean, double)
     */
    protected Iterator bfsWithinIterator(Location origin, Conditional condition, boolean includeSelf, double distance) {
        //old implementation appears to be significantly faster..
       return new BFSWithinIterator(this, origin, condition, includeSelf, distance);
//        return new BFSWithinIterator2DSmallWorld(this, origin, condition, includeSelf, distance);
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.CollectionSpace#withinIterator(org.ascape.model.space.Coordinate, org.ascape.util.Conditional, boolean, double)
     */
    public Iterator withinIterator(Coordinate origin, Conditional condition, boolean includeSelf, double distance) {
        return bfsWithinIterator(get((CoordinateDiscrete) origin), condition, includeSelf, distance);
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.Discrete#calculateDistance(org.ascape.model.space.Coordinate, org.ascape.model.space.Coordinate)
     */
    public double calculateDistance(Coordinate origin, Coordinate target) {
        BFSWithinIterator within = (BFSWithinIterator) withinIterator(origin, new FindCoordinateCondition(target), true, Double.MAX_VALUE);
        //may be null
        Location result = (Location) within.next();
        if (result != null) {
            return within.getDepth();
        } else {
            return Double.NaN;
        }
    }

    /**
     * Returns the next cell within immediate neighborhood toward the requested
     * cell. If no path exists between the origin andthe target, returns the
     * origin. If more than one are same distance, properly randomizes results.
     * 
     * @return the radius
     */
//    public Node findCellToward(Node origin, Node target) {
//        return findCellTowardBFS(origin, target);
//    }

    /**
     * Returns the next cell within immediate neighborhood furthest away from the requested cell.
     * If more than one are same distance, properly randomizes results.
     * @param origin the current cell
     * @param target the cell that we are moving toward
     */
//    public Node findCellAway(Node origin, Node target) {
//        return findCellAwayBFS(origin, target);
//    }

    /**
     * Returns the radius.
     */
    public int getRadius() {
        return radius;
    }

    /**
     * Set the diffusion temp value.
     * 
     * @param radius
     *            the radius
     */
    public void setRadius(int radius) {
        this.radius = radius;
    }

    /**
     * Returns the radius.
     * 
     * @return the random edge ratio
     */
    public double getRandomEdgeRatio() {
        return randomEdgeRatio;
    }

    /**
     * Set the diffusion temp value.
     * 
     * @param randomEdgeRatio
     *            the random edge ratio
     */
    public void setRandomEdgeRatio(double randomEdgeRatio) {
        this.randomEdgeRatio = randomEdgeRatio;
    }

    /**
     * Gets the small world cells.
     * 
     * @return the small world cells
     */
    public Set getSmallWorldCells() {
        return smallWorldCells;
    }
}
