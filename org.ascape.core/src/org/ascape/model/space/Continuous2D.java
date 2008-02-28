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
public class Continuous2D extends CollectionSpace implements Continuous {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * The size.
     */
    private int size;

    /**
     * Constructs a continuous two-dimensional space (plane or torous).
     */
    public Continuous2D() {
        this(new Coordinate2DContinuous(0.0, 0.0));
    }

    /**
     * Constructs a continuous two-dimensional space (plane or torous).
     * 
     * @param extent
     *            a coordinate describing the size of this space
     */
    public Continuous2D(Coordinate2DContinuous extent) {
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
            ((Location) iter.next()).setCoordinate(new Coordinate2DContinuous(0.0f, 0.0f));
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
            double originX = ((Coordinate2DContinuous) origin).getXValue();
            double originY = ((Coordinate2DContinuous) origin).getYValue();
            double targetX = ((Coordinate2DContinuous) target).getXValue();
            double targetY = ((Coordinate2DContinuous) target).getYValue();
            double boundsX = ((Coordinate2DContinuous) getExtent()).getXValue();
            double boundsY = ((Coordinate2DContinuous) getExtent()).getYValue();
            double distX;
            double distY;
            if (originX < targetX) {
                distX = Math.min((targetX - originX), (originX + (boundsX - targetX)));
            } else { //targetX <= originX
                distX = Math.min((originX - targetX), (targetX + (boundsX - originX)));
            }
            if (originY < targetY) {
                distY = Math.min((targetY - originY), (originY + (boundsY - targetY)));
            } else { //targetY <= originY
                distY = Math.min((originY - targetY), (targetY + (boundsY - originY)));
            }
            return Math.sqrt((distX * distX) + (distY * distY));
        } else {
            double minXDistance = Math.abs(((Coordinate2DContinuous) origin).getXValue() - ((Coordinate2DContinuous) target).getXValue());
            double minYDistance = Math.abs(((Coordinate2DContinuous) origin).getYValue() - ((Coordinate2DContinuous) target).getYValue());
            return Math.sqrt(minXDistance * minXDistance + minYDistance * minYDistance);
        }
    }

    /**
     * Returns the shortest relative position of two agents.
     * 
     * @param origin
     *            one Location
     * @param target
     *            another Location
     * @return the coordinate
     */
    public Coordinate calculateRelativePosition(Location origin, Location target) {
        return calculateRelativePosition(origin.getCoordinate(), target.getCoordinate());
    }

    /**
     * Returns the shortest relative position of two agents.
     * 
     * @param origin
     *            one Location
     * @param target
     *            another Location
     * @return the coordinate
     */
    public Coordinate calculateRelativePosition(Coordinate origin, Coordinate target) {
        if (geometry.isPeriodic()) {
            double originX = ((Coordinate2DContinuous) origin).getXValue();
            double originY = ((Coordinate2DContinuous) origin).getYValue();
            double targetX = ((Coordinate2DContinuous) target).getXValue();
            double targetY = ((Coordinate2DContinuous) target).getYValue();
            double boundsX = ((Coordinate2DContinuous) getExtent()).getXValue();
            double boundsY = ((Coordinate2DContinuous) getExtent()).getYValue();
            double distX;
            double distY;
            if (originX < targetX) {
                double insideSpanX = targetX - originX;
                double outsideSpanX = originX + (boundsX - targetX);
                if (insideSpanX < outsideSpanX) {
                    distX = insideSpanX;
                } else {
                    distX = -outsideSpanX;
                }
            } else { //targetX <= originX
                double insideSpanX = originX - targetX;
                double outsideSpanX = targetX + (boundsX - originX);
                if (insideSpanX < outsideSpanX) {
                    distX = -insideSpanX;
                } else {
                    distX = outsideSpanX;
                }
            }
            if (originY < targetY) {
                double insideSpanY = targetY - originY;
                double outsideSpanY = originY + (boundsY - targetY);
                if (insideSpanY < outsideSpanY) {
                    distY = insideSpanY;
                } else {
                    distY = -outsideSpanY;
                }
            } else { //targetY <= originY
                double insideSpanY = originY - targetY;
                double outsideSpanY = targetY + (boundsY - originY);
                if (insideSpanY < outsideSpanY) {
                    distY = -insideSpanY;
                } else {
                    distY = outsideSpanY;
                }
            }
            return new Coordinate2DContinuous(distX, distY);
        } else {
            double distX = ((Coordinate2DContinuous) target).getXValue() - ((Coordinate2DContinuous) origin).getXValue();
            double distY = ((Coordinate2DContinuous) target).getYValue() - ((Coordinate2DContinuous) origin).getYValue();
            return new Coordinate2DContinuous(distX, distY);
        }
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
        double maxX = ((Coordinate2DContinuous) getExtent()).getXValue();
        double maxY = ((Coordinate2DContinuous) getExtent()).getYValue();
        double randX = randomInRange(0.0, maxX);
        double randY = randomInRange(0.0, maxY);
        return new Coordinate2DContinuous(randX, randY);
    }

    /**
     * Moves an agent from its original coordinate towards its target
     * coordinate.
     * 
     * @param origin
     *            the origin coordinate
     * @param target
     *            the target coordinate
     * @param distance
     *            the maximum distance that can be traveled
     */
    public void moveToward(Location origin, Coordinate target, double distance) {
        double originX = ((Coordinate2DContinuous) origin.getCoordinate()).getXValue();
        double originY = ((Coordinate2DContinuous) origin.getCoordinate()).getYValue();
        double targetX = ((Coordinate2DContinuous) target).getXValue();
        double targetY = ((Coordinate2DContinuous) target).getYValue();
        if (getGeometry().isPeriodic()) {
            double boundsX = ((Coordinate2DContinuous) getExtent()).getXValue();
            double boundsY = ((Coordinate2DContinuous) getExtent()).getYValue();
            if (originX < targetX) {
                if ((targetX - originX) > (originX + (boundsX - targetX))) {
                    //outside edge is closer
                    //So move outside of bounds to allow for it (orgin will move back toward bounds)
                    originX += boundsX;
                }
            } else { //targetX <= originX
                if ((originX - targetX) > (targetX + (boundsX - originX))) {
                    //outside edge is closer
                    //So move outside of bounds to allow for it (orgin will move out toward bounds)
                    targetX += boundsX;
                }
            }
            if (originY < targetY) {
                if ((targetY - originY) > (originY + (boundsY - targetY))) {
                    //outside edge is closer
                    //So move outside of bounds to allow for it (orgin will move back toward bounds)
                    originY += boundsY;
                }
            } else { //targetY <= originY
                if ((originY - targetY) > (targetY + (boundsY - originY))) {
                    //outside edge is closer
                    //So move outside of bounds to allow for it (orgin will move out toward bounds)
                    targetY += boundsY;
                }
            }
        }
        double distanceX = targetX - originX;
        double distanceY = targetY - originY;
        double theta = Math.atan2(distanceY, distanceX);
        double deltaX = distance * Math.cos(theta);
        double deltaY = distance * Math.sin(theta);
        deltaX += originX;
        deltaY += originY;
        //Check for overruning target
        //probably a more efficent/elegant way to do this..but it works
        if (originX < targetX) {
            if (deltaX > targetX) {
                deltaX = targetX;
            }
        } else { //originX >= targetX
            if (deltaX < targetX) {
                deltaX = targetX;
            }
        }
        if (originY < targetY) {
            if (deltaY > targetY) {
                deltaY = targetY;
            }
        } else { //originY >= targetY
            if (deltaY < targetY) {
                deltaY = targetY;
            }
        }
        ((Coordinate2DContinuous) origin.getCoordinate()).setXValue(deltaX);
        ((Coordinate2DContinuous) origin.getCoordinate()).setYValue(deltaY);
        normalize(origin.getCoordinate());
    }

    /**
     * Moves an agent from its original coordinate away from the target
     * coordinate.
     * 
     * @param origin
     *            the origin coordinate
     * @param target
     *            the target coordinate
     * @param distance
     *            the maximum distance that can be traveled
     */
    public void moveAway(Location origin, Coordinate target, double distance) {
        double originX = ((Coordinate2DContinuous) origin.getCoordinate()).getXValue();
        double originY = ((Coordinate2DContinuous) origin.getCoordinate()).getYValue();
        double targetX = ((Coordinate2DContinuous) target).getXValue();
        double targetY = ((Coordinate2DContinuous) target).getYValue();
        double distanceX = targetX - originX;
        double distanceY = targetY - originY;
        double theta = Math.atan2(distanceY, distanceX);
        double deltaX = distance * Math.cos(theta);
        double deltaY = distance * Math.sin(theta);
        deltaX = originX - deltaX;
        deltaY = originY - deltaY;
        ((Coordinate2DContinuous) origin.getCoordinate()).setXValue(deltaX);
        ((Coordinate2DContinuous) origin.getCoordinate()).setYValue(deltaY);
        //Check that in bounds
        normalize(origin.getCoordinate());
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
            if (((Coordinate2DContinuous) coor).getXValue() < 0.0) {
                ((Coordinate2DContinuous) coor).setXValue(((Coordinate2DContinuous) coor).getXValue() + ((Coordinate2DContinuous) getExtent()).getXValue());
            } else if (((Coordinate2DContinuous) coor).getXValue() > ((Coordinate2DContinuous) getExtent()).getXValue()) {
                ((Coordinate2DContinuous) coor).setXValue(((Coordinate2DContinuous) coor).getXValue() - ((Coordinate2DContinuous) getExtent()).getXValue());
            }
            if (((Coordinate2DContinuous) coor).getYValue() < 0.0) {
                ((Coordinate2DContinuous) coor).setYValue(((Coordinate2DContinuous) coor).getYValue() + ((Coordinate2DContinuous) getExtent()).getYValue());
            } else if (((Coordinate2DContinuous) coor).getYValue() > ((Coordinate2DContinuous) getExtent()).getYValue()) {
                ((Coordinate2DContinuous) coor).setYValue(((Coordinate2DContinuous) coor).getYValue() - ((Coordinate2DContinuous) getExtent()).getYValue());
            }
        } else {
            if (((Coordinate2DContinuous) coor).getXValue() < 0.0) {
                ((Coordinate2DContinuous) coor).setXValue(0.0);
            } else if (((Coordinate2DContinuous) coor).getXValue() > ((Coordinate2DContinuous) getExtent()).getXValue()) {
                ((Coordinate2DContinuous) coor).setXValue(((Coordinate2DContinuous) getExtent()).getXValue());
            }
            if (((Coordinate2DContinuous) coor).getYValue() < 0.0) {
                ((Coordinate2DContinuous) coor).setYValue(0.0);
            } else if (((Coordinate2DContinuous) coor).getYValue() > ((Coordinate2DContinuous) getExtent()).getYValue()) {
                ((Coordinate2DContinuous) coor).setYValue(((Coordinate2DContinuous) getExtent()).getYValue());
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

