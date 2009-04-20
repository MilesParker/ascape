/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */


package org.ascape.gis.model;

import org.ascape.model.space.Continuous2D;
import org.ascape.model.space.Coordinate;
import org.ascape.model.space.Location;

import com.bbn.openmap.LatLonPoint;
import com.bbn.openmap.proj.GreatCircle;
import com.bbn.openmap.proj.ProjMath;

/**
 * A scape containing a population of agents that exist within a map of space on
 * the earth.
 *
 * This is designed to work with OpenMap and all its Location and OMRaster stuff. if you don't
 * want to use it, use ScapeContinuous2D.
 *
 * @author    Miles Parker
 * @created   April 15-June 15, 2001
 * @version   2.0
 * @history   2.0 8/31/01 first in
 * @since     2.0
 */
public class Globe extends Continuous2D {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The average radius of the earth
     */
    public final static double AVERAGE_EARTH_RADIUS = 3440.8f;

    private int secondsPerIteration = 60;

    /**
     * Returns the distance an agent can move in one iteration, given its velocity.
     * This assumes 60 seconds per iteration.
     *
     * @param velocity  the agent's velocity
     * @return          the distance
     */
    public double distancePerIteration(double velocity) {
        return velocity * secondsPerIteration / 3600.0 / AVERAGE_EARTH_RADIUS;
    }

    /**
     * Moves an agent from its original coordinate towards its target coordinate. This
     * method uses the GreatCircle eqation.
     *
     * @param origin    the origin coordinate
     * @param target    the target coordinate
     * @param distance  the maximum distance that can be traveled
     */
    public void moveToward(Location origin, Coordinate target, double distance) {
        //todo, provide moveAwy version...
        double originLat = ((MapCoordinate) origin.getCoordinate()).getLatitude();
        double originLon = ((MapCoordinate) origin.getCoordinate()).getLongitude();
        double targetLat = ((MapCoordinate) target).getLatitude();
        double targetLon = ((MapCoordinate) target).getLongitude();
        double az = GreatCircle.spherical_azimuth((float) ProjMath.degToRad(originLat), (float) ProjMath.degToRad(originLon), (float) ProjMath.degToRad(targetLat), (float) ProjMath.degToRad(targetLon));
        double arcDistanceToMove = Math.min(distance, calculateDistance(origin.getCoordinate(), target) / AVERAGE_EARTH_RADIUS);
        LatLonPoint llp = GreatCircle.spherical_between((float) ProjMath.degToRad(originLat), (float) ProjMath.degToRad(originLon), (float) arcDistanceToMove, (float) az);
        ((MapCoordinate) origin.getCoordinate()).setLatitude(llp.getLatitude());
        ((MapCoordinate) origin.getCoordinate()).setLongitude(llp.getLongitude());
    }

    /**
     * Returns the shortest distance between one LocatedAgent and another.
     *
     * @param origin    one LocatedAgent
     * @param target    another LocatedAgent
     * @return          the shortest distance
     */
    public double calculateDistance(Coordinate origin, Coordinate target) {
        double originLat = ((MapCoordinate) origin).getLatitude();
        double originLon = ((MapCoordinate) origin).getLongitude();
        double targetLat = ((MapCoordinate) target).getLatitude();
        double targetLon = ((MapCoordinate) target).getLongitude();
        float arcDistance = GreatCircle.spherical_distance((float) ProjMath.degToRad(originLat),
            (float) ProjMath.degToRad(originLon), (float) ProjMath.degToRad(targetLat), (float) ProjMath.degToRad(targetLon));
        return arcDistance * AVERAGE_EARTH_RADIUS;
    }

    public int getSecondsPerIteration() {
        return secondsPerIteration;
    }

    public void setSecondsPerIteration(int secondsPerIteration) {
        this.secondsPerIteration = secondsPerIteration;
    }
}
