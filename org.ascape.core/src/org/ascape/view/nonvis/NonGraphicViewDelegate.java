/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

/**
 * An interface for any view of a scape. More simple views may want to simply use ScapeListener, but
 * this view is appropriate for any other views. Generally, this view is used with a ScapeListenerDelegate
 * which takes care of all of the basic event handling.
 *
 * @author Miles Parker
 * @version 2.9
 * @history 2.9 3/30/02 refactored out of Views.
 * @since 2.9
 */

package org.ascape.view.nonvis;

import org.ascape.model.event.ScapeEvent;
import org.ascape.model.event.ScapeListener;
import org.ascape.model.event.ScapeListenerDelegate;

/**
 * Manages the relationship between a nongraphic view and its scape, including
 * listener registeration and managing calling appopriate view methods for scape
 * events.
 */
public class NonGraphicViewDelegate extends ScapeListenerDelegate {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs the NonGraphicViewDelegate.
     */
    public NonGraphicViewDelegate() {
    }

    /**
     * Constructs the NonGraphicViewDelegate.
     * 
     * @param scapeListener
     *            the view this delegate is managing scape relationships for
     */
    public NonGraphicViewDelegate(ScapeListener scapeListener) {
        setScapeListener(scapeListener);
    }

    /**
     * Super method notifies this view that something has happened on the scape.
     * by calling updateScapeGraphics and then notifies the scape that this view
     * has been updated.
     * 
     * @param scapeEvent
     *            a scape event update
     */
    public void scapeNotification(ScapeEvent scapeEvent) {
        super.scapeNotification(scapeEvent);
        //notifyScapeUpdated();
    }
}
