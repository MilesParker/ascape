/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.space;

import org.ascape.util.Conditional;


/**
 * A two-dimensional, fixed-size collection of agents providing services
 * described for space. Important: at moment, does not support von Neumann or
 * Moore space for find near, uses Euclidian distances only.
 * 
 * @author Miles Parker
 * @version 3.0
 * @history 3.0 9/10/02 significant refactoring of space model hierarchy
 * @history 2.0 10/29/01 changes to support new continuous space functionality
 * @history 1.9.3 3-4/2001 Many QA fixes and functional improvements, changed
 *          getCells ordering
 * @history 1.9.2 10/2/2000 fix to getCellsNearVonNeumann from user report (Tim
 *          Norris)
 * @history 1.9 9/20/2000 many additions, including test code, support for
 *          distances, etc..
 * @history 1.5 12/99 added support for iterations, and so was able to move
 *          executeMembers functionality to ScapeDiscrete
 * @history 1.1.1 5/3/99 Support random execution, fixed findRandomCell,
 *          findRandomUnoccupiedCell, etc..
 * @history 1.2 6/22/99 added support for periodic searches, and todo fix
 *          findNearest for periodic case
 * @since 1.0
 */
public abstract class Array2D extends Array2DBase {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a 2-dimensional array space of provided extent.
     */
    public Array2D() {
        super();
        for (int i = 0; i < relativeCoordinatesTemplate.length; i++) {
            relativeCoordinatesRankDistance[i] = Math.sqrt(Math.pow(relativeCoordinatesTemplate[i][0][0], 2) + Math.pow(relativeCoordinatesTemplate[i][0][1], 2));
        }
        //Need to be careful becuase if we miss any methods we will get infinite recusrions.
        collection = this;
//        if ((geometry != null) && ((geometry.getDimensionCount() != 2) || !geometry.isDiscrete())) {
//            throw new RuntimeException("Tried to assign an inappropriate geometry: " + geometry.toString() +
//                " to a 1- dimensional lattice.");
//        }
    }

    /**
     * Constructs a 2-dimensional array space of provided extent.
     * 
     * @param geometry
     *            geometry describing this space
     * @param extent
     *            a coordinate describing the size of this space
     */
    public Array2D(Geometry geometry, CoordinateDiscrete extent) {
        this();
        setGeometry(geometry);
        setExtent(extent);
    }

    /**
     * Initializes the space, copying a set of relative coordinates for use, and
     * ensuring that the ordering used for random draws starts consistently.
     */
    public void initialize() {
        super.initialize();
        //We need to make sure that relative coordinates are always in the same
        //state at the begining of each run to ensure reproducibility
        //Deep copy relativeCoordinates = relativeCoordinatesTemplate
        for (int i = 0; i < Array2D.relativeCoordinatesTemplate.length; i++) {
            for (int j = 0; j < Array2D.relativeCoordinatesTemplate[i].length; j++) {
                System.arraycopy(Array2D.relativeCoordinatesTemplate[i][j], 0, relativeCoordinates[i][j], 0, Array2D.relativeCoordinatesTemplate[i][j].length);
            }
        }
    }

    /**
     * The MA x_ RANK.
     */
    public static int MAX_RANK = 135;

    /**
     * Returns the number of relative coordiantes that exist in the given
     * distance rank.
     * 
     * @param rank
     *            the rank to return coordinate count for
     * @return the num of coordinates within rank
     */
    protected static int getNumOfCoordinatesWithinRank(int rank) {
        return sumOfCoordinatesWithinRank[rank];
    }

    /**
     * Calculate distance moore.
     * 
     * @param origin
     *            the origin
     * @param target
     *            the target
     * @return the double
     */
    public final double calculateDistanceMoore(Coordinate origin, Coordinate target) {
        return Math.max(getXSpan(origin, target), getYSpan(origin, target));
    }

    /**
     * Gets the x span.
     * 
     * @param origin
     *            the origin
     * @param target
     *            the target
     * @return the x span
     */
    protected final int getXSpan(Coordinate origin, Coordinate target) {
        if (geometry.isPeriodic()) {
            return calculateDistance(((Coordinate2DDiscrete) origin).getXValue(), ((Coordinate2DDiscrete) target).getXValue(), ((Coordinate2DDiscrete) getExtent()).getXValue());
        } else {
            return Math.abs(((Coordinate2DDiscrete) origin).getXValue() - ((Coordinate2DDiscrete) target).getXValue());
        }
    }

    /**
     * Gets the y span.
     * 
     * @param origin
     *            the origin
     * @param target
     *            the target
     * @return the y span
     */
    protected final int getYSpan(Coordinate origin, Coordinate target) {
        if (geometry.isPeriodic()) {
            return calculateDistance(((Coordinate2DDiscrete) origin).getYValue(), ((Coordinate2DDiscrete) target).getYValue(), ((Coordinate2DDiscrete) getExtent()).getYValue());
        } else {
            return Math.abs(((Coordinate2DDiscrete) origin).getYValue() - ((Coordinate2DDiscrete) target).getYValue());
        }
    }

    /**
     * Returns the first element in the supplied rank that matches the supplied
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
    public Location findFirstMatchInRank(Coordinate origin, Conditional condition, int rank) {
        int xO = ((Coordinate2DDiscrete) origin).getXValue();
        int yO = ((Coordinate2DDiscrete) origin).getYValue();
        if (geometry.isPeriodic()) {
            for (int place = 0; place < Array2D.relativeCoordinatesRankLengths[rank]; place++) {
                //basically, calculate mod of x and y plus the relative coordinates...
                int x = xO + relativeCoordinates[rank][place][0];
                while (x < 0) {
                    x += cells.length;
                }
                while (x >= cells.length) {
                    x -= cells.length;
                }
                int y = yO + relativeCoordinates[rank][place][1];
                while (y < 0) {
                    y += cells[0].length;
                }
                while (y >= cells[0].length) {
                    y -= cells[0].length;
                }
                if ((condition.meetsCondition(cells[x][y]))) {
                    return cells[x][y];
                }
            }
        } else {
            for (int place = 0; place < Array2D.relativeCoordinatesRankLengths[rank]; place++) {
                int x = xO + relativeCoordinates[rank][place][0];
                if ((x >= 0) && (x < cells.length)) {
                    int y = yO + relativeCoordinates[rank][place][1];
                    if ((y >= 0) && (y < cells[0].length)) {
                        if ((condition.meetsCondition(cells[x][y]))) {
                            return cells[x][y];
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Finds the nearest cell that meets some condition.
     * 
     * @param origin
     *            the agent to find cells near
     * @param condition
     *            the condition that found cell must meet
     * @param includeOrigin
     *            should supplied agent be included in the search
     * @param distance
     *            the maximum distance to search within
     * @return the location
     */
    public Location findNearest(Coordinate origin, Conditional condition, boolean includeOrigin, double distance) {
        int nearestRank = findNearestMatchRank(origin, condition, includeOrigin, distance);
        if (nearestRank >= 0) {
            return findRandomMatchInRank(origin, condition, nearestRank);
        } else {
            if (distance > Array2D.MAX_RANK) {
                return findMinimum(withinIterator(origin, condition, includeOrigin, distance), new ClosestDataPoint(origin));
            } else {
                return null;
            }
        }
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
     * @param maximumDistance
     *            the maximum distance to search within
     * @return the int
     */
    public int findNearestMatchRank(Coordinate origin, Conditional condition, boolean includeOrigin, double maximumDistance) {
        for (int i = (includeOrigin ? 0 : 1); (i < relativeCoordinates.length) && (Array2D.relativeCoordinatesRankDistance[i] < maximumDistance); i++) {
            if (findFirstMatchInRank(origin, condition, i) != null) {
                return i;
            }
        }
        //No matching coordinates found
        return -1;
    }

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
    public Location findRandomMatchInRank(Coordinate origin, Conditional condition, int rank) {
        randomizeRank(rank);
        return findFirstMatchInRank(origin, condition, rank);
    }

    /**
     * Returns true if there is a cell within the supplied distance that meets
     * the supplied condition.
     * 
     * @param origin
     *            the agent to find cells near
     * @param condition
     *            the condition that found cell must meet
     * @param includeOrigin
     *            should supplied agent be included in the search
     * @param maximumDistance
     *            the distance to search within
     * @return true, if has within
     */
    public boolean hasWithin(Coordinate origin, Conditional condition, boolean includeOrigin, double maximumDistance) {
        return (findNearestMatchRank(origin, condition, includeOrigin, maximumDistance) >= 0);
    }

    /**
     * Returns the number of cells within the supplied distance that meet the
     * supplied condition.
     * 
     * @param origin
     *            the agent to find cells near
     * @param condition
     *            the condition that found cell must meet
     * @param includeOrigin
     *            should supplied agent be included in the search
     * @param maximumDistance
     *            the distance to search within
     * @return the int
     */
    public int countWithin(Coordinate origin, Conditional condition, boolean includeOrigin, double maximumDistance) {
        int xO = ((Coordinate2DDiscrete) origin).getXValue();
        int yO = ((Coordinate2DDiscrete) origin).getYValue();
        int count = 0;
        if (geometry.isPeriodic()) {
            for (int rank = (includeOrigin ? 0 : 1); rank < relativeCoordinates.length; rank++) {
                if (Array2D.relativeCoordinatesRankDistance[rank] <= maximumDistance) {
                    for (int place = 0; place < Array2D.relativeCoordinatesRankLengths[rank]; place++) {
                        int x = xO + relativeCoordinates[rank][place][0];
                        if (x < 0) {
                            x += cells.length;
                        } else if (x >= cells.length) {
                            x -= cells.length;
                        }
                        int y = yO + relativeCoordinates[rank][place][1];
                        if (y < 0) {
                            y += cells[0].length;
                        } else if (y >= cells[0].length) {
                            y -= cells[0].length;
                        }
                        if ((condition.meetsCondition(cells[x][y]))) {
                            count++;
                        }
                    }
                } else {
                    //Searched to maximum distance
                    return count;
                }
            }
        } else {
            for (int rank = (includeOrigin ? 0 : 1); rank < relativeCoordinates.length; rank++) {
                if (Array2D.relativeCoordinatesRankDistance[rank] <= maximumDistance) {
                    for (int place = 0; place < Array2D.relativeCoordinatesRankLengths[place]; place++) {
                        int x = xO + relativeCoordinates[rank][place][0];
                        if ((x >= 0) && (x < cells.length)) {
                            int y = yO + relativeCoordinates[rank][place][1];
                            if ((y >= 0) && (y < cells[0].length)) {
                                if ((condition.meetsCondition(cells[x][y]))) {
                                    count++;
                                }
                            }
                        }
                    }
                } else {
                    //Searched to maximum distance
                    return count;
                }
            }
        }
        return count;
    }
}
