/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.vis;

import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;

/**
 * @version   2.9
 * @history   2.9 Moved into main Ascape.
 * @history   1.0 (Class version) 06/05/01 initial definition
 * @since     1.0
 */
public class ERVGlyphFactory {

    /**
     * Method.
     *
     * @param s  parameter
     * @return
     */
    public static GeneralPath square(int s) {
        GeneralPath path = new GeneralPath();

        path.moveTo(0, 0);

        path.lineTo(s, 0);
        path.lineTo(s, s);
        path.lineTo(0, s);
        path.closePath();

        return path;
    }

    /**
     * Method.
     *
     * @param s  parameter
     * @return
     */
    public static GeneralPath triangle1(int s) {
        GeneralPath path = new GeneralPath();

        path.moveTo(s / 2, 0);

        path.lineTo(s, s);
        path.lineTo(0, s);
        path.closePath();

        return path;
    }

    /**
     * Method.
     *
     * @param s  parameter
     * @return
     */
    public static GeneralPath triangle2(int s) {
        GeneralPath path = new GeneralPath();

        path.moveTo(0, 0);

        path.lineTo(s, 0);
        path.lineTo(s / 2, s);
        path.closePath();

        return path;
    }

    /**
     * Method.
     *
     * @param s  parameter
     * @return
     */
    public static GeneralPath triangle3(int s) {

        GeneralPath path = new GeneralPath();

        path.moveTo(0, 0);

        path.lineTo(s, s / 2);
        path.lineTo(0, s);
        path.closePath();

        return path;
    }

    /**
     * Method.
     *
     * @param s  parameter
     * @return
     */
    public static GeneralPath triangle4(int s) {

        GeneralPath path = new GeneralPath();

        path.moveTo(s, 0);

        path.lineTo(0, s / 2);
        path.lineTo(s, s);
        path.closePath();

        return path;
    }

    /**
     * Method.
     *
     * @param s  parameter
     * @return
     */
    public static GeneralPath diamond(int s) {
        GeneralPath path = new GeneralPath();

        path.moveTo(s / 2, 0);

        path.lineTo(s, s / 2);
        path.lineTo(s / 2, s);
        path.lineTo(0, s / 2);
        path.closePath();

        return path;
    }

    /**
     * Method.
     *
     * @param s  parameter
     * @return
     */
    public static GeneralPath circle(int s) {
        Ellipse2D el = new Ellipse2D.Float(0, 0, s, s);
        GeneralPath path = new GeneralPath(el);

        return path;
    }

}
