/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.vis;

import java.awt.Image;
import java.io.Serializable;


/**
 * A Image feature returns a image based on the state of an object of known type provided to it.
 * This class provides a concrete version of image feature for convenience in implementing subclasses.
 * Used to provide an appropriate image for some feature or aspect of an object.
 *
 * @author Miles Parker
 * @version 1.2.5
 * @history 1.2.5 9/1/1999 first in
 * @since 1.2.5
 */
public abstract class ImageFeatureConcrete implements Cloneable, Serializable, ImageFeature {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * The name of the feature.
     */
    private String name;

    /**
     * Constructs a concrete instantiation of a image feature.
     */
    public ImageFeatureConcrete() {
    }

    /**
     * Constructs a concrete instantiation of a image feature with the supplied name.
     * @param name the user relevant name of the feature
     */
    public ImageFeatureConcrete(String name) {
        this.name = name;
    }

    /**
     * Returns a name for the object as defined by set name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this feature.
     * @param name a user relevant name for this feature
     */
    public final void setName(String name) {
        this.name = name;
    }

    /**
     * Returns a image for the object as defined in implementions of this class.
     * @param object the object to get a image from.
     */
    public abstract Image getImage(Object object);

    /**
     * Utility method to return an image resource as specified according to the rules of Class.getResource.
     * @param string the resource reference of the image to load
     */
    public static Image createImage(String string) {
        return ImageRegistry.INSTANCE.getImage("images/" + string);
    }
}
