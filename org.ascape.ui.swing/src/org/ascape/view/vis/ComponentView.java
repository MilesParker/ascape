/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.view.vis;

import java.awt.Dimension;

import javax.swing.ImageIcon;

import org.ascape.model.Scape;
import org.ascape.model.event.ScapeListener;
import org.ascape.movie.MovieRecorder;
import org.ascape.runtime.swing.ViewFrameBridge;

/**
 * A view which listens to (is an observer of) an agent scape. The view must be
 * a sublclass of java.awt.component. May be depreated, as Canvas can be
 * replaced with Panel in Swing.
 * 
 * @author Miles Parker
 * @version 2.9
 * @history 2.9.1 7/10/02 Refacotred, changed names to better conform to
 *          standard usage
 * @history 2.9 5/9/02 updated for new movie refactorings
 * @history 1.2.6 10/25/99 added support for named listeners
 * @history 1.0.1 3/6/1999 made aware of view frame
 * @since 1.0
 */
public interface ComponentView extends ScapeListener {

    /**
     * Prepare the component view, once the view's scape has been created.
     */
    public void build();

    /**
     * Return the view frame this component is being displayed within.
     * 
     * @return the view frame
     */
    public ViewFrameBridge getViewFrame();

    /**
     * Sets the view frame this component is being displayed within.
     * 
     * @param frame
     *            the frame
     */
    public void setViewFrame(ViewFrameBridge frame);

    /**
     * Sets the recorder that can be used to record this view. If null, do not
     * record.
     * 
     * @param recorder
     *            the recorder
     */
    public void setMovieRecorder(MovieRecorder recorder);

    /**
     * Called when scape has been updated and requires a redraw. Depending on
     * the setting for iterations per redraw, this may not be called for every
     * scape update event.
     */
    public void updateScapeGraphics();

    /**
     * Returns the number of iterations this compenent will wait before
     * redrawing, that is, calling updateScapeGraphics.
     * 
     * @return the iterations per redraw
     */
    public int getIterationsPerRedraw();

    /**
     * Sets the number of iterations this compenent will wait before redrawing,
     * that is, calling updateScapeGraphics.
     * 
     * @param iterations
     *            the iterations
     */
    public void setIterationsPerRedraw(int iterations);

    /**
     * Called when the scape requests a change to iterations per redraw. He
     * component may choose to ignore the request.
     */
    public void onChangeIterationsPerRedraw();

    /**
     * Returns the Scape being viewed.
     * 
     * @return the scape
     */
    public Scape getScape();

    /**
     * Forces the view to notify its scape. Neccessary if the view suddenly (say
     * by being iconfied) becomes unable to repaint itself.
     */
    public void forceScapeNotify();

    /**
     * Sets the size this component would be if it had to fit within the allowed
     * dimensions. For example, a component that wanted to be square and was
     * given a dimension of 30 x 40 would return 30 x 30.
     * 
     * @param d
     *            the d
     * @return the preferred size within
     */
    public Dimension getPreferredSizeWithin(Dimension d);

    /**
     * Return an icon that can be used to represent this frame. May return null,
     * in which case a default icon will be used.
     * 
     * @return the icon
     */
    public ImageIcon getIcon();

    /**
     * Should be called when the view has updated itself in a way that changes
     * icon. Implementations need to inform the frame of the update as
     * approriate. Normally this will not need to be overriden as panel view and
     * canvas view implement properly. Any other implementations should simply
     * call getViewFrame().iconUpdated().
     */
    public void iconUpdated();
}
