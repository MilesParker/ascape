/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model;

import org.ascape.model.space.Continuous;
import org.ascape.model.space.Coordinate;
import org.ascape.model.space.Coordinate2DContinuous;

/**
 * An agent that has momentum and heading in space.
 * We will soon replace velocity and heading with a single velocity vector coordinate.
 *
 * @author Miles Parker
 * @version 3.0
 * @history 3.0 9/10/02 changed name from ContinuousAgent, removed reliaince of continuous space scapes on this class
 * @history 2.0 10/29/01 Many changes to support new continuous space functionality
 * @history 2.0 9/11/01 first in
 * @since 2.0
 */
public class MomentumAgent extends LocatedAgent {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Default agent size. For now, agent image will simply by a circle, with radius of nominalAgentSize.
     */
    private int agentSize = 5;

    protected double velocity;

    /**
     * The heading is an angle, b/w 0 and 360 degrees (or 2 pi)
     */
    protected double heading;

    /**
     * Moves this agent to a random unoccupied location on the host scape.
     * It is an error to call this method on a cell in a non-continous scape unless the cell is a cell occupant.
     */
    public void moveToRandomLocation() {
        Coordinate coord = getScape().findRandomCoordinate();
        if (coord != null) {
            moveTo(coord);
        } else {
            System.err.println("Warning: no location to move to. Killing agent.");
            die();
        }
    }

    /**
     * Moves a set distance in a random direction.
     * In the continuous space version, moves agent distance 1 in that direction.
     * (distance 1 may be replaced with a veolcity.
     */
    public void randomWalk() {
        heading = randomInRange(0.0, 2.0 * Math.PI);
        movement();
    }

    /**
     * Moves to the coordiante specified.
     * It is an error to call this method on a cell in a non-continuous space unless the cell is a cell occupant.
     * In other words, the default behavior of this method is to throw an exception.
     * @param coordinate
     */
    public void moveTo(Coordinate coordinate) {
        moveTo(coordinate, true);
    }

    /**
     * Sets the heading to point toward a particular agent.
     *
     * @param a the coordinate to point toward
     */
    public void headToward(LocatedAgent a) {
        headToward(a.getCoordinate());
    }

    /**
     * Finds the heading between current location and the one being passed in.
     *
     * @param target    the coordinate to point towards
     */
    public double findHeadingToward(LocatedAgent target) {
        return findHeadingToward(target.getCoordinate());
    }


    /**
     * Sets the heading to point away from a particular agent.
     *
     * @param a the coordinate to point toward
     */
    public void headAway(LocatedAgent a) {
        headAway(a.getCoordinate());
    }

    /**
     * Sets the heading to point toward a particular direction.
     *
     * @param target the coordinate to point toward
     */
    public void headToward(Coordinate target) {
        setHeading(findHeadingToward(target));
    }

    /**
     * Finds the heading between current location and the one being passed in.
     *
     * @param target    the coordinate to point towards
     */
    public double findHeadingToward(Coordinate target) {
        double x1 = ((Coordinate2DContinuous) getCoordinate()).getXValue();
        double y1 = ((Coordinate2DContinuous) getCoordinate()).getYValue();
        double x2 = ((Coordinate2DContinuous) target).getXValue();
        double y2 = ((Coordinate2DContinuous) target).getYValue();
        return Math.atan2(y2 - y1, x2 - x1);
    }

    /**
     * Sets the heading to point away from a particular direction.
     *
     * @param target the coordinate to point toward
     */
    public void headAway(Coordinate target) {
        setHeading(findHeadingAway(target));
    }

    /**
     * Finds the heading to point away from a particular direction.
     *
     * @param target the coordinate to point toward
     */
    public double findHeadingAway(LocatedAgent target) {
        return findHeadingAway(target.getCoordinate());
    }

    /**
     * Finds the heading to point away from a particular direction.
     *
     * @param target the coordinate to point toward
     */
    public double findHeadingAway(Coordinate target) {
//        return (-1 * findHeadingToward(target));
        return (Math.PI + findHeadingToward(target));
    }

    /**
     * Moves to the coordiante specified.
     * It is an error to call this method on a cell in a non-continuous space unless the cell is a cell occupant.
     * In other words, the default behavior of this method is to throw an exception.
     * @param coordinate
     */
    public void moveTo(Coordinate coordinate, boolean adjustHeading) {
        if (adjustHeading) {
            //headToward(coordinate);
            setCoordinate(coordinate);
        } else {
            setCoordinate(coordinate);
        }
    }

    /**
     * Returns the size of the agent.
     *
     * @return the agent size
     */
    public int getAgentSize() {
        return agentSize;
    }

    /**
     * Sets the size of the agent.
     *
     * @param agentSize the desired size for this agent.
     */
    public void setAgentSize(int agentSize) {
        this.agentSize = agentSize;
    }

    /**
     * move to new location.
     * Note: it's probably possible to mess up the visuals by setting velocity too high, and
     * emphasizing the fact that they're jumping around, and not moving through all points between current location
     * and new location.
     */
    public void movement() {
        double distance = ((Continuous) getScape().getSpace()).distancePerIteration(velocity);
        double x1 = ((Coordinate2DContinuous) getCoordinate()).getXValue();
        double y1 = ((Coordinate2DContinuous) getCoordinate()).getYValue();
        double x2 = x1 + distance * Math.cos(heading);
        double y2 = y1 + distance * Math.sin(heading);

        // check to be sure new location isn't off the map. if it is, fix it
        //todo...this code assumes that space is 2D
        // now place boid at new location..
        Coordinate2DContinuous coor = new Coordinate2DContinuous(x2, y2);
        ((Continuous) getScape().getSpace()).normalize(coor);
        this.moveTo(coor, false);
    }

    /**
     * A string representation of this cell.
     */
    public String toString() {
        if (getName() != null) {
            if (coordinate != null) {
                return getName() + " " + coordinate;
            } else {
                return getName();
            }
        } else {
            if (coordinate != null) {
                return "Continuous Agent at " + coordinate;
            } else {
                return "Continuous Agent";
            }
        }
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }
}
