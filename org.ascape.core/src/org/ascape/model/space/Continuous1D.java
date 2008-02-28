/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.space;

import java.util.Iterator;


/**
 * A space containing a population of agents that exist within some continuous
 * space. Note that this space is also an instance of CollectionSpace so it will
 * not work to check for continuous scapes by doing something like !instanceof
 * Discrete.
 * 
 * @author Miles Parker
 * @version 3.0
 * @history 3.0 9/10/02 significant refactoring of space model hierarchy
 * @history 2.0 8/31/01 first in
 * @since 2.0
 */
public class Continuous1D extends CollectionSpace implements Continuous {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * The size.
     */
    private int size;

    /**
     * Constructs a continuous one-dimensional space (line or circle).
     */
    public Continuous1D() {
        this(new Coordinate1DContinuous(0.0));
    }

    /**
     * Constructs a continuous one-dimensional space (line or circle).
     * 
     * @param extent
     *            a coordinate describing the size of this space
     */
    public Continuous1D(Coordinate1DContinuous extent) {
        super();
        setExtent(extent);
    }

    /**
     * A no-op; overrides the base collection's behavior so that agents do not
     * have their coorinates changed.
     */
    public void coordinateSweep() {
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.CollectionSpace#populate()
     */
    public void populate() {
        super.populate();
        Iterator iter = iterator();
        while (iter.hasNext()) {
            ((Location) iter.next()).setCoordinate(new Coordinate1DContinuous(0.0f));
        }
    }

    /**
     * Returns the shortest distance between one Location and another.
     * 
     * @param origin
     *            one Location
     * @param target
     *            another Location
     * @return the double
     */
    public double calculateDistance(Coordinate origin, Coordinate target) {
        if (geometry.isPeriodic()) {
            throw new RuntimeException("Periodic continuous geometry not yet implemented for this space.");
        }
        return super.calculateDistance(origin, target);
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.Continuous#distancePerIteration(double)
     */
    public double distancePerIteration(double velocity) {
        return velocity;
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.CollectionSpace#findRandomCoordinate()
     */
    public Coordinate findRandomCoordinate() {
        double max = ((Coordinate1DContinuous) extent).getXValue();
        return new Coordinate1DContinuous(randomInRange(0.f, max));
    }

    /**
     * Moves an agent from its original coordinate towards its target
     * coordinate. This method uses the GreatCircle eqation.
     * 
     * @param origin
     *            the origin coordinate
     * @param target
     *            the target coordinate
     * @param distance
     *            the maximum distance that can be traveled
     */
    public void moveToward(Location origin, Coordinate target, double distance) {
        throw new UnsupportedOperationException("Not yet supported.");
//        double deltaX = ((Coordinate2DContinuous) target).getXValue() - ((Coordinate2DContinuous) origin).getXValue();
//        double deltaY = ((Coordinate2DContinuous) target).getYValue() - ((Coordinate2DContinuous) origin).getYValue();
//        double theta = (float) Math.atan2(deltaY, deltaX);
//        double y = distance * (float) Math.sin(theta);
//        double x = distance * (float) Math.cos(theta);
//        origin = new Coordinate2DContinuous(x,y);
    }

    /**
     * Converts the coordiante into the boundaries of the space. If the
     * cooridnate is out of bounds, adds or substracts the bounds as appropriate
     * to bring the coordinate into a common sapce mod boundary.
     * 
     * @param coor
     *            the Coordinate to normalize
     */
    public void normalize(Coordinate coor) {
        if (geometry.isPeriodic()) {
            if (((Coordinate1DContinuous) coor).getXValue() < 0.0) {
                ((Coordinate1DContinuous) coor).setXValue(((Coordinate1DContinuous) coor).getXValue() + ((Coordinate1DContinuous) getExtent()).getXValue());
            } else if (((Coordinate1DContinuous) coor).getXValue() > ((Coordinate1DContinuous) getExtent()).getXValue()) {
                ((Coordinate1DContinuous) coor).setXValue(((Coordinate1DContinuous) coor).getXValue() - ((Coordinate1DContinuous) getExtent()).getXValue());
            }
        } else {
            if (((Coordinate1DContinuous) coor).getXValue() < 0.0) {
                ((Coordinate1DContinuous) coor).setXValue(0.0);
            } else if (((Coordinate1DContinuous) coor).getXValue() > ((Coordinate1DContinuous) getExtent()).getXValue()) {
                ((Coordinate1DContinuous) coor).setXValue(((Coordinate1DContinuous) getExtent()).getXValue());
            }
        }
    }

    /**
     * Sets the number of agents in the space.
     * 
     * @param size
     *            the size
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Return the number of agents in the space.
     * 
     * @return the size
     */
    public int getSize() {
        return size;
    }
}
