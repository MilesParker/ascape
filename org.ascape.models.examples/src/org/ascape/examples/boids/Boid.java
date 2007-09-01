/*
 * This software and documentation is subject to, and may be only used in accordance with, the terms and conditions of
 * the license agreement described or set forth in the file titled License.txt.  This software and documentation
 * contains information that is confidential and proprietary to NuTech Solutions, Inc.
 *
 *             Copyright (c) 2000-2004
 *             NuTech Solutions, Inc.
 *
 * Portions of the software are licensed from The Brookings Institution
 *             Copyright (c) 1998-2000
 *            The Brookings Institution
 *          All rights reserved worldwide.
 *
 * U.S. GOVERNMENT END USERS. The software is a "commercial item," as that term is defined in 48 C.F.R. 2.101,
 * consisting of "commercial computer software" and "commercial computer software documentation," as such terms are
 * used in 48 C.F.R. 12.212. Consistent with 48 C.F.R. 12.212 and 48 C.F.R. 227.7202-1 through 227.7202-4, and other
 * relevant sections of the Code of Federal Regulations, as applicable all U.S. Government End Users acquire the
 * software with only those rights set forth in the license agreement. This software includes trade secrets that
 * are exempt from disclosure under the Freedom of Information Act.
 *
 *
 * Boids Model
 *
 * June - July 2002
 *
 * An implementation of Craig Reynold's Boids model in Ascape.
 *             Copyright (c) 2002-2004-2004
 *                NuTech Solutions,Inc.
 *
 */


package org.ascape.examples.boids;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;

import org.ascape.model.LocatedAgent;
import org.ascape.model.MomentumAgent;
import org.ascape.model.Scape;
import org.ascape.model.space.Continuous2D;
import org.ascape.model.space.Coordinate;
import org.ascape.model.space.Coordinate2DContinuous;
import org.ascape.util.Conditional;

public class Boid extends MomentumAgent {

    protected Color color;

    public static final int RED_TEAM = 1;

    public static final int BLUE_TEAM = 2;

    private int team;

    private double headingRange;

    private double obstacleRange;

    private double flockRange;

    private double clumpingRange;

    private double collisionRange;

    private Conditional sameTeam;

    @SuppressWarnings("unused")
    private Conditional oppositeTeam;

    @SuppressWarnings("unused")
    private Conditional obstacles;

    private boolean prioritySet;

    public void initialize() {
        // only randomly set team if it's not already set
        if (team == 0) {
            if (getRandom().nextDouble() > 0.5) {
                team = RED_TEAM;
            } else {
                team = BLUE_TEAM;
            }
        }
        BaseModel model = (BaseModel) getRoot();
        velocity = model.getInitialVelocity();
        clumpingRange = model.getInitialClumpingRange();
        flockRange = model.getInitialFlockRange();
        headingRange = model.getInitialHeadingRange();
        obstacleRange = model.getInitialObstacleAvoidanceRange();
        collisionRange = model.getInitialCollisionRange();
        heading = randomInRange(0.0, 2.0 * Math.PI);

        prioritySet = false;

        sameTeam = new Conditional() {
            public boolean meetsCondition(Object o) {
                return ((team == ((Boid) o).getTeam()) && (((Boid) o) != Boid.this));
            }
        };

        oppositeTeam = new Conditional() {
            public boolean meetsCondition(Object o) {
                return ((team != ((Boid) o).getTeam()) && (((Boid) o) != Boid.this));
            }
        };

        obstacles = new Conditional() {
            public boolean meetsCondition(Object o) {
                return (o instanceof Obstacle);
            }
        };
    }

    public void iterate() {
        super.iterate();
        prioritySet = false;
    }

    /**
     * Find all obstacles within range. Set heading diametrically opposite to the closest one.
     * This is the highest priority.
     */
    public void avoidObstacles() {
        Obstacle ob = (Obstacle) ((BaseModel) getRoot()).getObstacles().findNearest(this.getCoordinate(), null, false, getObstacleRange());
        if (ob != null) {
            // find the angle from the origin of the obstacles (assume this Boid is at the origin)
            headAway(ob);
            prioritySet = true;
        }
    }

    /**
     * Steer to avoid running into flock members.
     * Find closest flock mate. If it is too close to you (as defined by collision range),
     * then steer away from that agent.
     * Only do this if the one higher priority (avoid obstacles) is off.
     * If there is a collision to be avoided, set priority marker.
     */
    public void avoidCollisions() {
        if (!prioritySet) {

            Boid closest = (Boid) findNearest(sameTeam, false, collisionRange);
            if (closest != null) {
                headAway(closest);
                prioritySet = true;
            }
        }
    }

    /**
     * Group together with local boids.
     * Only do this if the two higher priorities (avoid collisions and obstacles) are not relevant.
     * If clumping needs to be done, set priority marker.
     */
    public void cohesion() {
        if (!prioritySet) {
            // calculate the average coordinate of local agents
            Coordinate centerOfMass = calcCenterOfMass(this, flockRange);
            if (calculateDistance(centerOfMass) > clumpingRange) {
                // only steer towards the flock, if its center of mass is within clumping range
                headToward(centerOfMass);
                prioritySet = true;
            }
        }
    }

    public Scape getTeamBoids() {
        if (this.team == RED_TEAM) {
            return ((BaseModel) getRoot()).getRedBoids();
        } else if (this.team == BLUE_TEAM) {
            return ((BaseModel) getRoot()).getBlueBoids();
        } else {
            throw new RuntimeException("Bad team.");
        }
    }

    /**
     * Set new heading to weighted average of current heading and local boids.
     * Only do this if neither of the three higher priorities (avoid collisions and obstacles, and cohesion) are off.
     * If heading needs to be aligned, set priority marker.
     */
    public void alignHeading() {
        if (!prioritySet) {
            // calculate new location based on current heading
            java.util.List flock = getTeamBoids().findWithin(this.getCoordinate(), null, false, headingRange);
            double aveHeading = calcAverageHeading(flock);
            // no one around, so just keep on current heading
            if (aveHeading == -1.0f) {
                ;
            } else {
                // set new heading to be average between flock's heading and your own heading
                setHeading((heading + aveHeading) / 2.0f);
                // set priority marker
            }
            prioritySet = true;
        }
    }

    double calcAverageHeading(List flock) {
        if (flock.size() == 0) {
            return -1.f;
        } else {
            double xSum = 0.;
            double ySum = 0.;
            for (Iterator flockIt = flock.iterator(); flockIt.hasNext();) {
                Boid boid = (Boid) flockIt.next();
                xSum += Math.cos(boid.getHeading());
                ySum += Math.sin(boid.getHeading());
            }
            return Math.atan2(ySum, xSum);
        }
    }

    Coordinate calcCenterOfMass(LocatedAgent fromAgent, double clumpingRange) {
        Iterator team = getTeamBoids().findWithin(fromAgent.getCoordinate(), null, false, clumpingRange).iterator();
        if (team.hasNext()) {
            double xSum = 0.f;
            double ySum = 0.f;
            int count = 0;
            while (team.hasNext()) {
                LocatedAgent flockMember = (LocatedAgent) team.next();
                Coordinate2DContinuous rel = (Coordinate2DContinuous) ((Continuous2D) ((BaseModel) getRoot()).getWorld().getSpace()).calculateRelativePosition(fromAgent, flockMember);
                xSum += rel.getXValue();
                ySum += rel.getYValue();
                count++;
            }
            double xAve = xSum / ((double) count);
            double yAve = ySum / ((double) count);
            return (Coordinate2DContinuous) (new Coordinate2DContinuous(xAve, yAve)).add(fromAgent.getCoordinate());
        } else {
            return fromAgent.getCoordinate();
        }
    }

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public Color getColor() {
        if (team == RED_TEAM) {
            return Color.red;
        } else if (team == BLUE_TEAM) {
            return Color.blue;
        } else {
            return Color.white;
        }
    }

    public int getNumWithinHeadingRange() {
        return findWithin(sameTeam, headingRange).size();
    }

    public double getHeadingRange() {
        return headingRange;
    }

    public void setHeadingRange(double headingRange) {
        this.headingRange = headingRange;
    }

    public double getObstacleRange() {
        return obstacleRange;
    }

    public void setObstacleRange(double obstacleRange) {
        this.obstacleRange = obstacleRange;
    }

    public double getClumpingRange() {
        return clumpingRange;
    }

    public void setClumpingRange(double clumpingRange) {
        this.clumpingRange = clumpingRange;
    }

    public double getCollisionRange() {
        return collisionRange;
    }

    public void setCollisionRange(double collisionRange) {
        this.collisionRange = collisionRange;
    }
}
