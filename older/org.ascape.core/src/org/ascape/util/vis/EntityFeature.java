/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.vis;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;
import java.util.Iterator;

/**
 * An interface for classes which define the Entity rendering hints of an entity
 * relation graphical view.
 *
 * @author    Roger Critchlow
 * @version   2.9
 * @history   1.0 (Internal) 06/05/01 initial definition
 * @since     2.9
 */
public interface EntityFeature extends ColorFeature {

    /**
     * Returns the name of this feature.
     *
     * @return   a <code>String</code> value
     */
    public String getName();

    /**
     * Answers whether this entity should be rendered.
     *
     * @param entity  the object of interest.
     * @return        a boolean
     */
    public boolean includesEntity(Object entity);

    /**
     * Returns an iteration over the entities herein featured.
     *
     * @return   an iterator over the entities.
     */
    public Iterator iterator();

    /**
     * Returns the derived font map used by all entities in this view.
     *
     * @return   the ERVDerivedFontMap
     */
    public ERVDerivedFontMap getDerivedFontMap();

    /**
     * Returns the shape class of the entity
     *
     * @param entity  the object of interest.
     * @return        the shapeClass
     */
    public Class getShapeClass(Object entity);

    /**
     * Describe <code>getShape</code> method here.
     *
     * @param entity  an <code>Object</code> value
     * @return        a <code>RectangularShape</code> value
     */
    public RectangularShape getShape(Object entity);

    /**
     * Returns the normalized position of the entity.
     *
     * @param entity  the object of interest.
     * @return        the position
     */
    public Point2D.Double getPosition(Object entity);

    /**
     * Returns the minimum normalized height of the entity.
     *
     * @param entity  the object of interest.
     * @return        the minNormedHeight
     */
    public double getMinNormedHeight(Object entity);

    /**
     * Returns the maximum normalized height of the entity.
     *
     * @param entity  the object of interest.
     * @return        the maxNormedHeight
     */
    public double getMaxNormedHeight(Object entity);

    /**
     * Returns the double value which should be interpolated into the min and
     * max height to decide the size of this entity.
     *
     * @param entity  the object of interest.
     * @return        the height
     */
    public double getHeight(Object entity);

    /**
     * Gets the color for the EntityFeature object.
     *
     * @param entity  parameter
     * @return        the color
     */
    public Color getColor(Object entity);

    /**
     * Gets the borderColor for the EntityFeature object.
     *
     * @param entity  parameter
     * @return        the borderColor
     */
    public Color getBorderColor(Object entity);

    /**
     * Returns the text string for this entity.
     *
     * @param entity  the object of interest.
     * @return        the text
     */
    public String getText(Object entity);

}
