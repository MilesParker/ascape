/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */


package org.ascape.gis.model;

import java.awt.Image;
import java.awt.Toolkit;

import org.ascape.model.MomentumAgent;
import org.ascape.model.space.Continuous;
import org.ascape.model.space.Coordinate;
import org.ascape.model.space.Coordinate2DContinuous;

import com.bbn.openmap.layer.location.Location;
import com.bbn.openmap.omGraphics.OMRaster;

public class MapAgent extends MomentumAgent {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private double offsetX;

    private double offsetY;

    /**
     * The wrapper class that OpenMap needs to place the actual image within.
     */
    protected OMRaster graphic;

    /**
     * The location of the asset in continuous space.
     */
    protected Location location;

    /**
     * The asset's velocity in nautical miles per hour.
     */
    protected double velocity;

    /**
     * The actual graphic representation of the asset on the map.
     */
    protected Image mapImage;

    /**
     * Moves this MapAgent object closer to its destination
     *
     * @param coordinate  the new location as a Coordinate
     */
    public void moveToward(Coordinate coordinate) {
        double d = ((Continuous) getScape().getSpace()).distancePerIteration(getVelocity());
        getScape().moveToward(this, coordinate, d);
    }

    /**
     * Returns true if the MapAgent is at the location defined by
     * the MapCoordinate passed in.
     *
     * @param coordinate  The MapCoordinate where the MapAgent is being tested to be.
     * @return            True if the MapAgent is within a small distance of the MapCoordinate
     */
    public boolean isAt(MapCoordinate coordinate) {
        double lat1 = ((MapCoordinate) getCoordinate()).getLatitude();
        double lon1 = ((MapCoordinate) getCoordinate()).getLongitude();
        double lat2 = coordinate.getLatitude();
        double lon2 = coordinate.getLongitude();
        return Math.abs(lat1 - lat2) < 0.0001 && Math.abs(lon1 - lon2) < 0.0001;
    }

    /**
     * Gets the location for the MapAgent object.
     *
     * @return   the location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Gets the MapAgent's velocity
     *
     * @return   the velocity
     */
    public double getVelocity() {
        return velocity;
    }

    /**
     * Sets graphic for the MapAgent object.
     *
     * @param graphic  the graphic
     */
    public void setGraphic(OMRaster graphic) {
        this.graphic = graphic;
    }

    /**
     * Returns the extent of the nth dimension.
     *
     * @param coordinate  the coordinate
     */
    public void setCoordinate(Coordinate coordinate) {
        double newX = ((Coordinate2DContinuous) coordinate).getXValue();
        double newY = ((Coordinate2DContinuous) coordinate).getYValue();
        graphic = new OMRaster((float) newX, (float) newY, getImage());
        location = new MapLocation((float) newX, (float) newY, "", graphic);
        this.coordinate =
            new MapCoordinate() {
                /**
                 * 
                 */
                private static final long serialVersionUID = 1L;

                public void setXValue(double x) {
                    super.setXValue(x);
                    location.setLocation((float) getYValue(), (float) getXValue());
                    graphic.setLat((float) getYValue());
                    graphic.setLon((float) getXValue());
                }

                public void setYValue(double y) {
                    super.setYValue(y);
                    location.setLocation((float) getYValue(), (float) getXValue());
                    graphic.setLat((float) getYValue());
                    graphic.setLon((float) getXValue());
                }
            };
        ((MapCoordinate) this.coordinate).setXValue(newX);
        ((MapCoordinate) this.coordinate).setYValue(newY);
    }

    /**
     * Sets the vehicle's velocity
     *
     * @param vel  the velocity
     */
    public void setVelocity(double vel) {
        velocity = vel;
    }

    /**
     * Internal class. Contains the continuous space Location information for the MapAgent object.
     *
     * @author    Miles Parker, Josh Miller, and others
     * @created   September-November, 2001
     */
//    class MapLocation extends Location {
//
//        /**
//         * Constructs an instance of MapLocation.
//         *
//         * @param lat             the new MapLocation's latitude
//         * @param lon             the new MapLocation's longitude
//         * @param name            the new MapLocation's name
//         * @param locationMarker  the new MapLocation's locationMarker
//         */
//        public MapLocation(float lat, float lon, String name, OMGraphic locationMarker) {
//            super(lat, lon, name, locationMarker);
//        }
//
//
//        /**
//         * Sets the graphicLocations attribute of the MapLocation object
//         *
//         * @param lat  the latitude
//         * @param lon  the longitude
//         */
//        public void setGraphicLocations(float lat, float lon) {
//            MapAgent.MapLocation.this.lat = lat;
//            MapAgent.MapLocation.this.lon = lon;
//        }
//
//
//        /**
//         * Sets the graphicLocations attribute of the MapLocation object
//         *
//         * @param f
//         * @param f2
//         * @param i   the latitude as x
//         * @param j   the longitude as y
//         */
//        public void setGraphicLocations(float f, float f2, int i, int j) {
//            throw new RuntimeException("Unintended call to mapLocation.setLocation.");
//        }
//
//
//        /**
//         * Sets the graphicLocations attribute of the MapLocation object
//         *
//         * @param i   The new graphicLocations value
//         * @param i2  The new graphicLocations value
//         */
//        public void setGraphicLocations(int i, int i2) {
//            throw new RuntimeException("Unintended call to mapLocation.setLocation.");
//        }
//    }

    /**
     * Gets an image for the graphic from a file.
     * By convention, assumed to be in lib hierarchy where the class file is, in the dat/ directory.
     *
     * @param imageName  parameter
     * @return        the image
     */
    public Image getImage(String imageName) {
        return getImage(imageName, "dat");
    }

    /**
     * Gets an image for the graphic from a file.
     * By convention, assumed to be in lib hierarchy where the class file is, in the dat/ directory.
     *
     * @param imageName  parameter
     * @return        the image
     */
    public Image getImage(String imageName, String subDirectory) {
        Image image = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource(subDirectory + "/" + imageName));
        setOffsetX(-image.getWidth(null) / 2);
        setOffsetY(-image.getHeight(null) / 2);
        return image;
    }

    /**
     * Returns the offset that the image should carry from the left. Normally half of the image width.
     */
    public double getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(double offsetX) {
        this.offsetX = offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
    }

    public OMRaster getGraphic() {
        return graphic;
    }

    /**
     * Gets the image for the MapAgent class.
     *
     * @return        the image
     */
    public Image getImage() {
        return mapImage;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Sets the image to be used when drawing.
     *
     * @param mapImage the image to draw
     */
    public void setImage(Image mapImage) {
        this.mapImage = mapImage;
        //Neccessary to force updating in OpenMap
        setCoordinate(getCoordinate());
    }

    /**
     * Sets the image to be used when drawing.
     *
     * @param imageName the image to draw
     */
    public void setImage(String imageName) {
        this.mapImage = getImage(imageName);
    }
}
