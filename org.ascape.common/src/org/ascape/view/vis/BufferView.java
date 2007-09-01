/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.view.vis;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;

/**
 * A base class for any poanel that wants to have a manually updateable
 * background buffer.
 * 
 * @author Miles Parker
 * @version 3.0
 */
public abstract class BufferView extends PanelView {

    /**
     * The image for double-buffering.
     */
    protected transient Image bufferedImage;

    /**
     * The garphics for double-buffering.
     */
    protected transient Graphics bufferedGraphics;

    /**
     * Constructs a canvas view.
     */
    public BufferView() {
        this("Canvas View");
    }

    /**
     * Constructs a canvas view.
     * 
     * @param name
     *            a user relevant name for this view
     */
    public BufferView(String name) {
        super(name);
        this.name = name;
    }

    /**
     * Override addNotify to build buffer.
     */
    public void addNotify() {
        super.addNotify();
        //setDoubleBuffered(false);
        buildGraphicsBuffer();
    }

    /* (non-Javadoc)
     * @see java.awt.Component#setBounds(int, int, int, int)
     */
    public synchronized void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        //Ignore intial sizing
        if ((getScape() != null) && (scape.isInitialized()) && (getParent() != null)) {
            buildGraphicsBuffer();
            updateScapeGraphics();
            repaint();
        }
    }

    /**
     * Builds the graphics buffer.
     */
    public synchronized void buildGraphicsBuffer() {
        //For some reason. size is changing between the first call and the second!
        int width = (int) getSize().getWidth();
        int height = (int) getSize().getHeight();
        if (width * height > 0) {
//            setBufferedImage(createVolatileImage(width, height));
            setBufferedImage(createImage(width, height));
        } else {
//            setBufferedImage(createVolatileImage(getPreferredSize().width, getPreferredSize().height));
            setBufferedImage(createImage(getPreferredSize().width, getPreferredSize().height));
        }
    }

    /**
     * Paints the canvas. Ordinarily, you should not do painting here, but into
     * the buffer in the scape updated method. Paint draws the buffer into the
     * canvas. By default, we just copy the buffer in. Override to provide some
     * other kind of behavior, making sure to call this super method, so that
     * the view can report that it has been updated. Otherwise, the whole model
     * will stop, waiting in vain for this view to update.
     * 
     * @param g
     *            the g
     */
    public synchronized void paintComponent(Graphics g) {
        if (bufferedImage != null) {
            if (g.drawImage(bufferedImage, 0, 0, this)) {
//This always returns in any environment we've tested, but needs a fix if not..
            } else {
                throw new RuntimeException("Internal Error in BufferView.paintComponent()");
            }
        }
    }

    /* (non-Javadoc)
     * @see java.awt.Component#imageUpdate(java.awt.Image, int, int, int, int, int)
     */
    public boolean imageUpdate(Image image, int i, int i1, int i2, int i3, int i4) {
        if (i == ImageObserver.ALLBITS) {
            getDelegate().viewPainted();
        }
        return super.imageUpdate(image, i, i1, i2, i3, i4);    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * Attempts to free buffer memory as soon as possible upon canvas
     * destruction.
     */
    public void finalize() {
        if (bufferedGraphics != null) {
            bufferedGraphics.dispose();
        }
    }

    /**
     * Gets the buffered image.
     * 
     * @return the buffered image
     */
    public Image getBufferedImage() {
        return bufferedImage;
    }

    /**
     * Sets the buffered image.
     * 
     * @param bufferedImage
     *            the new buffered image
     */
    public void setBufferedImage(Image bufferedImage) {
        this.bufferedImage = bufferedImage;
        bufferedGraphics = bufferedImage.getGraphics();
        bufferedGraphics.setColor(getBackground());
        bufferedGraphics.fillRect(0, 0, bufferedImage.getWidth(null), bufferedImage.getHeight(null));
    }
}
