/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */
package org.ascape.view.vis.erv;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;
import java.util.ArrayList;
import java.util.Iterator;

import org.ascape.model.Scape;
import org.ascape.util.vis.ERVDerivedFontMap;
import org.ascape.util.vis.EntityFeature;


/**
 * A default implementation of the EntityFeature interface.
 * 
 * @author Roger Critchlow
 * @version 2.9
 * @history 2.9 06/05/01 initial definition
 * @since 2.9
 */
public class DefaultEntityFeature implements EntityFeature, java.io.Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 473788354164046131L;

    /**
     * The scape.
     */
    private Scape scape;
    
    /**
     * The name.
     */
    private String name;
    
    /**
     * The derived font map.
     */
    private ERVDerivedFontMap derivedFontMap = new ERVDerivedFontMap(new Font("Serif", Font.PLAIN, 1));

    /**
     * Constructs an instance of DefaultEntityFeature.
     * 
     * @param scape
     *            parameter
     * @param name
     *            parameter
     */
    public DefaultEntityFeature(Scape scape, String name) {
        this.scape = scape;
        this.name = name;
    }

    /**
     * Method.
     * 
     * @param entity
     *            parameter
     * @return true, if includes entity
     */
    public boolean includesEntity(Object entity) {
        for (Iterator i = iterator(); i.hasNext();) {
            if (entity.equals(i.next())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method.
     * 
     * @return the iterator
     */
    public Iterator iterator() {
        return (new ArrayList(scape)).iterator();
    }

    /**
     * Describe <code>getName</code> method here.
     * 
     * @return a <code>String</code> value
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the derivedFontMap for the DefaultEntityFeature object.
     * 
     * @return the derivedFontMap
     */
    public ERVDerivedFontMap getDerivedFontMap() {
        return derivedFontMap;
    }

    /**
     * Gets the shapeClass for the DefaultEntityFeature object.
     * 
     * @param entity
     *            parameter
     * @return the shapeClass
     */
    public Class getShapeClass(Object entity) {
        return Ellipse2D.Float.class;
    }

    /**
     * <code>getShape</code> returns a new instance of the entity's shape
     * class.
     * 
     * @param entity
     *            an <code>Object</code> value
     * @return a <code>RectangularShape</code> value
     */
    public RectangularShape getShape(Object entity) {
        Class shapeClass = getShapeClass(entity);
        try {
            return (RectangularShape) shapeClass.newInstance();
        } catch (IllegalAccessException iae) {
        } catch (InstantiationException ie) {
        } catch (ExceptionInInitializerError eiie) {
        } catch (SecurityException se) {
        }
        return new Ellipse2D.Float();
    }

    /**
     * Gets the position for the DefaultEntityFeature object.
     * 
     * @param entity
     *            parameter
     * @return the position
     */
    public Point2D.Double getPosition(Object entity) {
        int i = indexOf(entity);
        // NB - would be better if scape implemented indexOf
        int n = scape.getSize();
        double phi = 2 * Math.PI * i / n;
        if (i == -1) {
            // throw new RuntimeException("cannot find index of scape member: "+entity.toString());
            System.err.println("cannot find index of scape member: " + entity.toString());
            return new Point2D.Double(0.5, 0.5);
        } else {
            //return new Point2D.Double((1 + Math.cos(phi))/3, (1 + Math.sin(phi))/3);
            return new Point2D.Double(0.5 + (Math.cos(phi) * 0.33), 0.5 + (Math.sin(phi) * 0.33));
        }
    }

    /**
     * Gets the minNormedHeight for the DefaultEntityFeature object.
     * 
     * @param entity
     *            parameter
     * @return the minNormedHeight
     */
    public double getMinNormedHeight(Object entity) {
        /* generate a minimum diameter appropriate to circular display */
        return getMaxNormedHeight(entity) / 10;
        //return 0.1;
    }

    /**
     * Gets the maxNormedHeight for the DefaultEntityFeature object.
     * 
     * @param entity
     *            parameter
     * @return the maxNormedHeight
     */
    public double getMaxNormedHeight(Object entity) {
        /* generate a maximum diameter appropriate to circular display */
        return 2 * Math.PI * 0.5 / scape.getSize();
        //return 0.2;
    }

    /**
     * Gets the height for the DefaultEntityFeature object.
     * 
     * @param entity
     *            parameter
     * @return the height
     */
    public double getHeight(Object entity) {
        return 0.5;
    }

    /**
     * Gets the color for the DefaultEntityFeature object.
     * 
     * @param entity
     *            parameter
     * @return the color
     */
    public Color getColor(Object entity) {
        return Color.magenta;
    }

    /**
     * Gets the borderColor for the DefaultEntityFeature object.
     * 
     * @param entity
     *            parameter
     * @return the borderColor
     */
    public Color getBorderColor(Object entity) {
        return Color.cyan;
    }

    /**
     * Gets the text for the DefaultEntityFeature object.
     * 
     * @param entity
     *            parameter
     * @return the text
     */
    public String getText(Object entity) {
        return entity.toString();
    }

    /**
     * Method.
     * 
     * @param entity
     *            parameter
     * @return the int
     */
    int indexOf(Object entity) {
        int j = 0;
        for (Iterator i = scape.iterator(); i.hasNext();) {
            if (entity == i.next()) {
                return j;
            } else {
                j += 1;
            }
        }
        return -1;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return getName() + " Entity Feature";
    }
}
