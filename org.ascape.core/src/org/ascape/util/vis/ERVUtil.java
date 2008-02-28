/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.vis;

import java.awt.geom.Point2D;

/**
 * Geometric utilities.
 *
 * @version   2.9
 * @history   2.9 Moved into main Ascape.
 * @history   1.0 (Class version) 06/05/01 initial definition
 * @since     1.0
 */
public class ERVUtil {

    // assuming point determines business end of a 2D vector rooted at (0,0)
    // rotate CCW 90 degrees.
    /**
     * Method.
     *
     * @param pt  parameter
     */
    public static void orthogonalize(Point2D pt) {
        double x = pt.getX();
        double y = pt.getY();
        pt.setLocation(y, -x);
    }

    /**
     * Method.
     *
     * @param pt  parameter
     * @return
     */
    public static double mag(Point2D pt) {
        double x = pt.getX();
        double y = pt.getY();
        return java.lang.Math.sqrt((x * x) + (y * y));
    }

    /**
     * Method.
     *
     * @param pt  parameter
     */
    public static void normalize(Point2D pt) {
        double length = mag(pt);
        double x = pt.getX();
        double y = pt.getY();
        if (length != 0.0) {
            x /= length;
            y /= length;
            pt.setLocation(x, y);
        }
    }

    /**
     * Method.
     *
     * @param pt  parameter
     * @param s   parameter
     */
    public static void scaleBy(Point2D pt, double s) {
        double x = pt.getX();
        double y = pt.getY();
        pt.setLocation(x * s, y * s);
    }

    /**
     * Method.
     *
     * @param d1  parameter
     * @param d2  parameter
     * @return
     */
    public static double dot(Point2D d1, Point2D d2) {
        return (d1.getX() * d2.getX()) + (d1.getY() * d2.getY());
    }

    /**
     * Method.
     *
     * @param pt  parameter
     * @param xt  parameter
     * @param yt  parameter
     */
    public static void translate(Point2D pt, double xt, double yt) {
        double x = pt.getX();
        double y = pt.getY();
        pt.setLocation(x + xt, y + yt);
    }
}
