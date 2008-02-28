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
 * @version 2.9.1
 * @history 2.9.1 7/10/02 Refacotred to .event, changed names to better conform to standard usage
 * @history 2.9 3/30/02 refactored out of Views.
 * @since 2.9
 */

package org.ascape.model.event;

import java.util.TooManyListenersException;


/**
 * Manages the relationship between a listener and its scape, including listener
 * registeration and managing calling appopriate view methods for scape events.
 */
public class ScapeListenerDelegate extends DefaultScapeListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * The scape listener using this delegate.
     */
    private ScapeListener scapeListener;

    /**
     * Constructs the ScapeListenerDelegate.
     */
    public ScapeListenerDelegate() {
    }

    /**
     * Constructs the ScapeListenerDelegate.
     * 
     * @param scapeListener
     *            the scape listener
     */
    public ScapeListenerDelegate(ScapeListener scapeListener) {
        this.scapeListener = scapeListener;
    }

    /**
     * Notifies the listener that the scape has removed it. The scape event must
     * be from the scape that this listener is listening to.
     * 
     * @param scapeEvent
     *            the scape removed notification event
     */
    public void scapeRemoved(ScapeEvent scapeEvent) {
        if (this.scape == null) {
            throw new RuntimeException("Tried to remove a scape from a delegate with no scape.");
        }
        if (scapeEvent == null) {
            throw new RuntimeException("Tried to remove a null scape.");
        }
        listeningToScape = false;
    }

    /**
     * Notifies the delegating view that something has happened on the scape.
     * Calls the onIterate, onSetup, onStart, onStop, onClose, or onDeserialized
     * method as appropriate. If isNotifyScapeAutomatically property is true
     * (default), notifies scape when finished.
     * 
     * @param scapeEvent
     *            a scape event update
     */
    public void scapeNotification(ScapeEvent scapeEvent) {
        super.scapeNotification(scapeEvent);
        if (scapeEvent.getID() == ScapeEvent.REPORT_ITERATE) {
            scapeListener.scapeIterated(scapeEvent);
        } else if (scapeEvent.getID() == ScapeEvent.REQUEST_SETUP) {
            scapeListener.scapeSetup(scapeEvent);
        } else if (scapeEvent.getID() == ScapeEvent.REPORT_INITIALIZED) {
            scapeListener.scapeInitialized(scapeEvent);
        } else if (scapeEvent.getID() == ScapeEvent.REPORT_ADDED) {
            try {
                scapeListener.scapeAdded(scapeEvent);
            } catch (TooManyListenersException e) {
                throw new RuntimeException(e);
            }
        } else if (scapeEvent.getID() == ScapeEvent.REPORT_START) {
            scapeListener.scapeStarted(scapeEvent);
        } else if (scapeEvent.getID() == ScapeEvent.REPORT_STOP) {
            scapeListener.scapeStopped(scapeEvent);
        } else if (scapeEvent.getID() == ScapeEvent.REPORT_DESERIALIZED) {
            scapeListener.scapeDeserialized(scapeEvent);
        } else if (scapeEvent.getID() == ScapeEvent.REQUEST_CLOSE) {
            scapeListener.scapeClosing(scapeEvent);
        }
    }

    /**
     * Calls the observed scape and notifies it that the view has finished
     * updating its state. This method must be called after receiving any
     * scapeNotification event or the model will stall.
     */
    public void notifyScapeUpdated() {
        scape.respondControl(new ControlEvent(scapeListener, ControlEvent.REPORT_LISTENER_UPDATED));
    }

    /**
     * Returns the scape view that this delegate is responsible for.
     * 
     * @return the scape listener
     */
    public ScapeListener getScapeListener() {
        return scapeListener;
    }

    /**
     * Sets the scape view that this delegate is responsible for.
     * 
     * @param scapeListener
     *            the scape listener
     */
    public void setScapeListener(ScapeListener scapeListener) {
        this.scapeListener = scapeListener;
    }

    /**
     * Returns a description of the deleaget and is delegating view.
     * 
     * @return the string
     */
    public String toString() {
        return "Delegate Listener for " + scapeListener;
    }
}
