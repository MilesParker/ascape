/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.vis;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

/**
 * A class using a image feature to determine the image to use to draw a feature.
 * @author Miles Parker
 * @version 1.2
 * @history 1.2 7/8/99 added more constructor options (including name) to simplify subclassing
 * @history 1.0.1 3/13/99 renamed from DrawImageSource
 * @since 1.0
 */
public class DrawImageFeature extends DrawFeature implements ImageFeature {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The image feature to use for drawing this feature.
     * By default, black.
     */
    private ImageFeature imageFeature = ImageFeatureFixed.grayBallFeature;

    private Color backgroundColor = Color.lightGray;

    /**
     * Constructs a DrawImageFeature.
     */
    public DrawImageFeature() {
        super();
    }

    /**
     * Constructs the feature with the supplied name.
     * @param name the name of this draw feature
     */
    public DrawImageFeature(String name) {
        super(name);
    }

    /**
     * Constructs the feature with a name and image feature.
     * @param name the name of this draw feature
     * @param imageFeature the feature to nest within this feature
     */
    public DrawImageFeature(String name, ImageFeature imageFeature) {
        super(name);
        this.imageFeature = imageFeature;
    }

    /**
     * Constructs the feature with a name and nested feature.
     * @param name the name of this draw feature
     * @param nestedFeature the feature to nest within this feature
     */
    public DrawImageFeature(String name, DrawFeature nestedFeature) {
        super(name, nestedFeature);
    }

    /**
     * Constructs a DrawImageFeature with a nested feature.
     * @param nestedFeature the feature to nest
     */
    public DrawImageFeature(DrawFeature nestedFeature) {
        super(nestedFeature);
    }

    /**
     * Draws a graphic interpreation of the object into the supplied graphics port,
     * (typically) within the supplied dimensions. Views which use this class are
     * responsible for translating the graphics so that the object is drawn at the
     * approriate location. Please let us know if you think you need a directly
     * addressed alternative.
     * @param g the Graphics context to draw into
     * @param object the object to interpret for drawing
     * @param width the width of the space that should be drawn into
     * @param height the height of the space that should be drawn into
     */
    public void draw(Graphics g, Object object, int width, int height) {
        g.setColor(backgroundColor);
        g.fillRect(0, 0, width, height);
        g.drawImage(imageFeature.getImage(object), 0, 0, null);
    }

    /**
     * Returns the image feature's image interpretation of the object.
     * @param object the object to interpret image for
     */
    public final Image getImage(Object object) {
        return imageFeature.getImage(object);
    }

    /**
     * Returns the image feature this object is using to interpret the object's image.
     */
    public ImageFeature getImageFeature() {
        return imageFeature;
    }

    /**
     * Sets the image feature this object uses to interpret the object's image.
     * @param imageFeature the feature to use for imageing
     */
    public void setImageFeature(ImageFeature imageFeature) {
        this.imageFeature = imageFeature;
    }

    /**
     * Returns the user relevant name of this feature.
     * "Unnamed", the name of the nested feature, or the name of the image feature, by default.
     * Override or set name to provide a name.
     */
    public String getName() {
        if (name != null) {
            return name;
        } else {
            if (nestedFeature != null) {
                String candidateName = nestedFeature.getName();
                if (candidateName != "Unnamed") {
                    return candidateName;
                } else {
                    return imageFeature.getName();
                }
            } else if (imageFeature != null) {
                return imageFeature.getName();
            } else {
                return "Unnamed";
            }
        }
    }
}

