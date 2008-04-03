/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.space;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.ascape.util.Conditional;
import org.ascape.util.RandomFunctions;
import org.ascape.util.RandomIterator;
import org.ascape.util.ResetableIterator;
import org.ascape.util.data.DataPoint;

/**
 * The Interface Space.
 */
public interface Space extends Collection, RandomFunctions {

    /**
     * Move away.
     * 
     * @param origin
     *            the origin
     * @param target
     *            the target
     * @param distance
     *            the distance
     */
    void moveAway(Location origin, Coordinate target, double distance);

    /**
     * Move toward.
     * 
     * @param origin
     *            the origin
     * @param target
     *            the target
     * @param distance
     *            the distance
     */
    void moveToward(Location origin, Coordinate target, double distance);

    /**
     * Calculate distance.
     * 
     * @param origin
     *            the origin
     * @param target
     *            the target
     * @return the double
     */
    double calculateDistance(Coordinate origin, Coordinate target);

    /**
     * Calculate distance.
     * 
     * @param origin
     *            the origin
     * @param target
     *            the target
     * @return the double
     */
    double calculateDistance(Location origin, Location target);

    /**
     * Find.
     * 
     * @param condition
     *            the condition
     * @return the list
     */
    List find(Conditional condition);

    /**
     * Find nearest.
     * 
     * @param origin
     *            the origin
     * @param condition
     *            the condition
     * @param includeOrigin
     *            the include origin
     * @param distance
     *            the distance
     * @return the location
     */
    Location findNearest(Coordinate origin, Conditional condition, boolean includeOrigin, double distance);

    Location findNearest(Location origin, Conditional condition, boolean includeOrigin, double distance);

    /**
     * Find within.
     * 
     * @param origin
     *            the origin
     * @param condition
     *            the condition
     * @param includeSelf
     *            the include self
     * @param distance
     *            the distance
     * @return the list
     */
    List findWithin(Coordinate origin, Conditional condition, boolean includeSelf, double distance);

    /**
     * Count within.
     * 
     * @param origin
     *            the origin
     * @param condition
     *            the condition
     * @param includeSelf
     *            the include self
     * @param distance
     *            the distance
     * @return the int
     */
    int countWithin(Coordinate origin, Conditional condition, boolean includeSelf, double distance);

    /**
     * Checks for within.
     * 
     * @param origin
     *            the origin
     * @param condition
     *            the condition
     * @param includeSelf
     *            the include self
     * @param distance
     *            the distance
     * @return true, if successful
     */
    boolean hasWithin(Coordinate origin, Conditional condition, boolean includeSelf, double distance);

    /**
     * Find minimum within.
     * 
     * @param coordinate
     *            the coordinate
     * @param dataPoint
     *            the data point
     * @param condition
     *            the condition
     * @param includeSelf
     *            the include self
     * @param distance
     *            the distance
     * @return the location
     */
    Location findMinimumWithin(Coordinate coordinate, DataPoint dataPoint, Conditional condition, boolean includeSelf, double distance);

    /**
     * Find maximum within.
     * 
     * @param coordinate
     *            the coordinate
     * @param dataPoint
     *            the data point
     * @param condition
     *            the condition
     * @param includeSelf
     *            the include self
     * @param distance
     *            the distance
     * @return the location
     */
    Location findMaximumWithin(Coordinate coordinate, DataPoint dataPoint, Conditional condition, boolean includeSelf, double distance);

    /**
     * Within iterator.
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
    Iterator withinIterator(Coordinate origin, Conditional condition, boolean includeSelf, double distance);

    /**
     * Find random coordinate.
     * 
     * @return the coordinate
     */
    Coordinate findRandomCoordinate();

    /**
     * Find random.
     * 
     * @return the location
     */
    Location findRandom();

    /**
     * Find random.
     * 
     * @param excludeLocation
     *            the exclude location
     * @return the location
     */
    Location findRandom(Location excludeLocation);

    /**
     * Find random.
     * 
     * @param condition
     *            the condition
     * @return the location
     */
    Location findRandom(Conditional condition);

    Location findRandom(Location exclude, Conditional condition);

    Location findRandomWithin(Location origin, Conditional condition, boolean includeSelf, double distance);

    /**
     * Find minimum.
     * 
     * @param point
     *            the point
     * @return the location
     */
    Location findMinimum(DataPoint point);

    /**
     * Find maximum.
     * 
     * @param point
     *            the point
     * @return the location
     */
    Location findMaximum(DataPoint point);

    /**
     * Get.
     * 
     * @param coordinate
     *            the coordinate
     * @return the location
     */
    Location get(Coordinate coordinate);

    /**
     * Set.
     * 
     * @param coordinate
     *            the coordinate
     * @param agent
     *            the agent
     */
    void set(Coordinate coordinate, Location agent);

    /**
     * New location.
     * 
     * @param randomLocation
     *            the random location
     * @return the location
     */
    Location newLocation(boolean randomLocation);

    /**
     * Sets the extent.
     * 
     * @param extent
     *            the new extent
     */
    void setExtent(Coordinate extent);

    /**
     * Gets the extent.
     * 
     * @return the extent
     */
    Coordinate getExtent();

    /**
     * Gets the size.
     * 
     * @return the size
     */
    int getSize();

    /**
     * Sets the size.
     * 
     * @param size
     *            the new size
     */
    void setSize(int size);

    /**
     * Construct.
     */
    void construct();

    /**
     * Populate.
     */
    void populate();

    /**
     * Initialize.
     */
    void initialize();

    /**
     * Gets the context.
     * 
     * @return the context
     */
    SpaceContext getContext();

    /**
     * Sets the context.
     * 
     * @param space
     *            the new context
     */
    void setContext(SpaceContext space);

    /**
     * Gets the geometry.
     * 
     * @return the geometry
     */
    Geometry getGeometry();

    /**
     * Checks if is periodic.
     * 
     * @return true, if is periodic
     */
    boolean isPeriodic();

    /**
     * Checks if is mutable.
     * 
     * @return true, if is mutable
     */
    boolean isMutable();

    /**
     * Sets the periodic.
     * 
     * @param periodic
     *            the new periodic
     */
    void setPeriodic(boolean periodic);

    /**
     * Add.
     * 
     * @param o
     *            the o
     * @param isParent
     *            the is parent
     * @return true, if successful
     */
    boolean add(Object o, boolean isParent);

    /**
     * Clone.
     * 
     * @return the object
     */
    Object clone();

    /**
     * Safe iterator.
     * 
     * @param start
     *            the start
     * @param limit
     *            the limit
     * @return the resetable iterator
     */
    ResetableIterator safeIterator(int start, int limit);

    /**
     * Safe iterators.
     * 
     * @param count
     *            the count
     * @return the resetable iterator[]
     */
    ResetableIterator[] safeIterators(int count);

    /**
     * Safe iterator.
     * 
     * @return the resetable iterator
     */
    ResetableIterator safeIterator();

    /**
     * Safe random iterator.
     * 
     * @return the random iterator
     */
    RandomIterator safeRandomIterator();

    /**
     * Conditional iterator.
     * 
     * @param condition
     *            the condition
     * @return the iterator
     */
    Iterator conditionalIterator(final Conditional condition);
}
