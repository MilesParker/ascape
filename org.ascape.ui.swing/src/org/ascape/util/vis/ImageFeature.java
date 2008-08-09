/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.vis;

import java.awt.Image;

import org.ascape.util.HasName;


/**
 * An image feature returns an image based on the state of an object of known type provided to it.
 * Used to provide an appropriate color for some feature or aspect of an object.
 *
 * @author Miles Parker
 * @version 1.2.5
 * @history 1.2.5 9/1/1999 first in
 * @since 1.2.5
 */
public interface ImageFeature extends HasName {

    /**
     * Returns a color for the object as defined in implementions of this class.
     * @param object the object to get a color from.
     */
    public Image getImage(Object object);
}
