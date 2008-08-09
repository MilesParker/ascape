/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.vis;

import java.awt.Image;

/**
 * An interface for a class providing an image that doesn't change in
 * relation to the supplied object. A number of basic sphere images are provided.
 *
 * @author Miles Parker
 * @version 1.2.5
 * @history 1.2.5 9/1/1999 first in
 * @since 1.2.5
 */
public class ImageFeatureFixed extends ImageFeatureConcrete {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * A image of a gray ball.
     */
    public final static Image grayBall = createImage("bullets_balls_gray_mid.gif");

    /**
     * A image feature of a gray ball.
     */
    public final static ImageFeatureFixed grayBallFeature = new ImageFeatureFixed("Gray Ball", grayBall);

    /**
     * A image of a red ball.
     */
    public final static Image redBall = createImage("bullets_balls_red_mid.gif");

    /**
     * A image feature of a red ball.
     */
    public final static ImageFeatureFixed redBallFeature = new ImageFeatureFixed("Red Ball", redBall);

    /**
     * A image of an orange ball.
     */
    public final static Image orangeBall = createImage("bullets_balls_orange_mid.gif");

    /**
     * A image feature of am orange ball.
     */
    public final static ImageFeatureFixed orangeBallFeature = new ImageFeatureFixed("Orange Ball", orangeBall);

    /**
     * A image of a green ball.
     */
    public final static Image greenBall = createImage("bullets_balls_light_green_mid.gif");

    /**
     * A image feature of a green ball.
     */
    public final static ImageFeatureFixed greenBallFeature = new ImageFeatureFixed("Green Ball", redBall);

    /**
     * A image of a blue ball.
     */
    public final static Image blueBall = createImage("bullets_balls_blue_mid.gif");

    /**
     * A image feature of a blue ball.
     */
    public final static ImageFeatureFixed blueBallFeature = new ImageFeatureFixed("Blue Ball", blueBall);

    /**
     * A image of a black ball.
     */
    public final static Image blackBall = createImage("bullets_balls_black_mid.gif");

    /**
     * A image feature of a black ball.
     */
    public final static ImageFeatureFixed blackBallFeature = new ImageFeatureFixed("Black Ball", blackBall);

    /**
     * The image of the feature.
     */
    private Image image;

    /**
     * Constructs a ImageFeatureFixed.
     */
    public ImageFeatureFixed() {
    }

    /**
     * Constructs a ImageFeatureFixed.
     * @param name the image of the feature
     * @param image the user relevant name of the feature
     */
    public ImageFeatureFixed(String name, Image image) {
        super(name);
        this.image = image;
    }

    /**
     * Returns a image for the object as defined irrespective of object by setImage.
     * @param object Ignored; normally, the object the object to get a image from.
     */
    public final Image getImage(Object object) {
        return image;
    }

    /**
     * Sets the image of this feature.
     * @param image the image this feature should always return
     */
    public final void setImage(Image image) {
        this.image = image;
    }

    /**
     * Clones this feature.
     */
    public Object clone() {
        try {
            ImageFeatureFixed clone = (ImageFeatureFixed) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }
}
