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
 */



package org.ascape.examples.traffic;

import java.awt.Color;
import java.awt.Graphics;

import org.ascape.model.CellOccupant;
import org.ascape.model.HostCell;
import org.ascape.util.vis.DrawFeature;


public class Car extends CellOccupant {

    public final static int ACCELERATING = 1;

    public final static int DECELERATING = 2;

    public final static int COASTING = 3;

    public final static int COOPERATOR = 1;

    public final static int DEFECTOR = 2;

    public final static DrawFeature DRAW_SIMPLE_CAR = new DrawFeature("Simple Car") {
        public final void draw(Graphics g, Object object, int width, int height) {
            Car car = (Car) ((HostCell) object).getOccupant();
            if (car != null) {
                g.setColor(car.getColor());
                int startpos = (int) ((float) car.getLocation() / 120.0 * (float) width);
                g.fill3DRect(startpos, 2, width - 2, height - 4, true);
                if (car.getMode() == DECELERATING) {
                    g.setColor(Color.red);
                    g.fillRect(startpos, 2, 2, 2);
                    g.fillRect(startpos, width - 4, 2, 2);
                }
            }
        }
    };

    private Color color;

    private double acceleration;

    private double location;

    private double deceleration;

    private double desiredSpeed;

    private double safeDistance;

    private int mode = COASTING;

    private double actualSpeed;

    private int behavior;

    public void initialize() {
        desiredSpeed = randomInRange(((TrafficModel) getRoot()).getMinSpeed(), ((TrafficModel) getRoot()).getMaxSpeed());
        safeDistance = randomInRange(((TrafficModel) getRoot()).getMinSafeDistance(), ((TrafficModel) getRoot()).getMaxSafeDistance());
        ;
        acceleration = randomInRange(((TrafficModel) getRoot()).getMinAcceleration(), ((TrafficModel) getRoot()).getMaxAcceleration());
        ;
        deceleration = randomInRange(((TrafficModel) getRoot()).getMinDeceleration(), ((TrafficModel) getRoot()).getMaxDeceleration());
        ;
        actualSpeed = desiredSpeed;
        location = 0.0;
        color = new Color(getRandom().nextFloat(), getRandom().nextFloat(), getRandom().nextFloat());
        if (getRandom().nextFloat() < ((TrafficModel) getRoot()).getPortionCooperator()) {
            behavior = COOPERATOR;
        } else {
            behavior = DEFECTOR;
        }
    }

    public Car carAhead(RoadTile roadTile, double distance) {
        Car nearestCar = null;
        int cellsAhead;
        for (cellsAhead = 1; cellsAhead <= distance; cellsAhead++) {
            HostCell candidate = roadTile.cellAhead(cellsAhead);
            if (!candidate.isAvailable()) {
                nearestCar = (Car) candidate.getOccupant();
            }
        }
        if (nearestCar != null) {
            if (getLocation() - nearestCar.getLocation() + cellsAhead - 1.0 <= distance) {
                return nearestCar;
            }
        }
        return null;
    }

    public Car carBehind(RoadTile roadTile, double distance) {
        Car nearestCar = null;
        int cellsBehind;
        for (cellsBehind = 1; cellsBehind <= distance; cellsBehind++) {
            HostCell candidate = roadTile.cellBehind(cellsBehind);
            if (!candidate.isAvailable()) {
                nearestCar = (Car) candidate.getOccupant();
            }
        }
        if (nearestCar != null) {
            if (getLocation() - nearestCar.getLocation() + cellsBehind - 1.0 <= distance) {
                return nearestCar;
            }
        }
        return null;
    }

    public Car carAhead(double distance) {
        return carAhead((RoadTile) getHostCell(), distance);
    }

    public Car carBehind(double distance) {
        return carBehind((RoadTile) getHostCell(), distance);
    }

    public boolean isOpen(RoadTile roadTile) {
        if ((roadTile.isAvailable()) && (carBehind(roadTile, safeDistance) == null) && (carAhead(roadTile, safeDistance) == null)) {
            return true;
        } else {
            return false;
        }
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getCarMode() {
        switch (mode) {
            case COASTING:
                return "Coasting";
            case ACCELERATING:
                return "Accelerating";
            case DECELERATING:
                return "Decelerating";
            default:
                throw new RuntimeException("Unspecified mode.");
        }
    }

    public String getCarBehavior() {
        switch (behavior) {
            case COOPERATOR:
                return "Cooperator";
            case DEFECTOR:
                return "Defecting";
            default:
                throw new RuntimeException("Unspecified behavior.");
        }
    }

    public void changeLanes() {
        if (behavior == COOPERATOR) {
            if (carAhead(safeDistance) != null) { //can't go as fast as I want, so I'll try to move left
                RoadTile leftCell = ((RoadTile) getHostCell()).cellLeft();
                if ((leftCell != null) && (isOpen(leftCell)) && (carAhead(leftCell, safeDistance) == null)) {
                    moveTo(leftCell);
                }
            } else if (carBehind(safeDistance * 2.0) != null) { //Oh someone is coming up behind me, better move over to let them pass
                RoadTile rightCell = ((RoadTile) getHostCell()).cellRight();
                if ((rightCell != null) && (isOpen(rightCell)) && (carAhead(rightCell, safeDistance) == null)) {
                    moveTo(rightCell);
                }
            }
        } else if (behavior == DEFECTOR) {
            if (carAhead(safeDistance) != null) { //can't go as fast as I want, so I'll try to move left
                //So I'm going to hop to a random other lane that has an opening
                if (randomIs()) {
                    RoadTile leftCell = ((RoadTile) getHostCell()).cellLeft();
                    if ((leftCell != null) && (isOpen(leftCell)) && (carAhead(leftCell, safeDistance) == null)) {
                        moveTo(leftCell);
                    } else {
                        RoadTile rightCell = ((RoadTile) getHostCell()).cellRight();
                        if ((rightCell != null) && (isOpen(rightCell))) {
                            moveTo(rightCell);
                        }
                    }
                } else {
                    RoadTile rightCell = ((RoadTile) getHostCell()).cellRight();
                    if ((rightCell != null) && (isOpen(rightCell)) && (carAhead(rightCell, safeDistance) == null)) {
                        moveTo(rightCell);
                    } else {
                        RoadTile leftCell = ((RoadTile) getHostCell()).cellLeft();
                        if ((leftCell != null) && (isOpen(leftCell))) {
                            moveTo(leftCell);
                        }
                    }
                }
            } else { //Do nothing, because I'm fine, screw everyone else
            }
        } else {
            throw new RuntimeException("Unspecified behvior in car.");
        }
    }

    public void movement() {
        HostCell candidate = ((RoadTile) getHostCell()).cellAhead();
        if (carAhead(safeDistance) == null) {
            if (actualSpeed < desiredSpeed) {
                mode = ACCELERATING;
                actualSpeed += acceleration;
            } else {
                mode = COASTING;
            }
        } else { //someone is there
            mode = DECELERATING;
            actualSpeed -= deceleration;
            if (actualSpeed < 0.0) {
                actualSpeed = 0.0;
            }
        }
        location += actualSpeed;
        if (location >= 160.0) {
            if (candidate.isAvailable()) {
                location = 0.0;
                moveTo(candidate);
            } else {
                location = 160.0;
            }
        }
    }

    public double getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }

    public double getDeceleration() {
        return deceleration;
    }

    public void setDeceleration(double deceleration) {
        this.deceleration = deceleration;
    }

    public double getSafeDistance() {
        return safeDistance;
    }

    public void setSafeDistance(double safeDistance) {
        this.safeDistance = safeDistance;
    }

    public double getDesiredSpeed() {
        return desiredSpeed;
    }

    public void setDesiredSpeed(double desiredSpeed) {
        this.desiredSpeed = desiredSpeed;
    }

    public double getActualSpeed() {
        return actualSpeed;
    }

    public void setActualSpeed(double actualSpeed) {
        this.actualSpeed = actualSpeed;
    }

    public double getLocation() {
        return location;
    }

    public void setLocation(double location) {
        this.location = location;
    }

    public Color getColor() {
        return color;
    }
}
