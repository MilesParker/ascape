/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.vis;

import java.awt.Color;

/**
 * A default implementation of the EntityFeature interface.
 *
 * @author    Roger Critchlow
 * @version   2.9
 * @history   2.9 06/05/01 initial definition
 * @since     2.9
 */
public class DefaultRelationFeature implements RelationFeature, java.io.Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String name;

    /**
     * Constructs an instance of DefaultRelationFeature.
     */
    public DefaultRelationFeature() {
    }

    /**
     * Constructs an instance of DefaultRelationFeature.
     */
    public DefaultRelationFeature(String name) {
        setName(name);
    }

    /**
     * Method.
     *
     * @param source       parameter
     * @param destination  parameter
     * @return
     */
    public boolean includesRelation(Object source, Object destination) {
        return true;
    }

    /**
     * Gets the lineWidth for the DefaultRelationFeature object.
     *
     * @param source       parameter
     * @param destination  parameter
     * @return             the lineWidth
     */
    public double getLineWidth(Object source, Object destination) {
        return 0.5;
    }

    /**
     * Gets the lineColor for the DefaultRelationFeature object.
     *
     * @param source       parameter
     * @param destination  parameter
     * @return             the lineColor
     */
    public Color getLineColor(Object source, Object destination) {
        return Color.magenta;
    }

    /**
     * Gets the glyphPosition for the DefaultRelationFeature object.
     *
     * @param source       parameter
     * @param destination  parameter
     * @return             the glyphPosition
     */
    public double getGlyphPosition(Object source, Object destination) {
        return 0.9;
    }

    /**
     * Gets the glyphColor for the DefaultRelationFeature object.
     *
     * @param source       parameter
     * @param destination  parameter
     * @return             the glyphColor
     */
    public Color getGlyphColor(Object source, Object destination) {
        return Color.cyan;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return getName() + " Relation Feature";
    }
}
