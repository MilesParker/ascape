/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.event;

import java.io.Serializable;
import java.util.TooManyListenersException;

import org.ascape.model.Scape;

/**
 * A listener to an agent scape. When a scape is updated, it is the listener's
 * responsility to update itself, and then inform the scape that updating is
 * finished, so that the scape can continue iterating through its rules.
 * DefaultScapeListener provides a good reference implementation of the listener
 * contract. By default, this is assumed to be a nongrphic view. Overide
 * isGraphic to return true if you are implementing a graphic view.
 * 
 * @author Miles Parker
 * @version 2.9.1
 * @history 2.9.1 7/10/02 Refacotred to .event, changed names to better conform
 *          to standard usage
 * @history 2.9 7/2/02 major refactoring to more closely conform to Java usage
 * @history 1.2.6 10/25/99 added support for named listeners
 * @since 1.0
 */
public abstract class DefaultScapeListener implements ScapeListener, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The scape being listened to.
     */
    public Scape scape;

    /**
     * Is this scape view delegate currently listening to a scape? Seperated
     * from wether scape is null, becuase we want to be able to save old scape.
     */
    protected boolean listeningToScape;

    /**
     * The name of the view.
     */
    protected String name = "Default Listener";

    /**
     * Should the scape be notified automatically?.
     */
    private boolean notifyScapeAutomatically = true;

    /**
     * Constructs a default listener.
     */
    public DefaultScapeListener() {
    }

    /**
     * Constructs a default listener.
     * 
     * @param name
     *            a user relevant name for this view
     */
    public DefaultScapeListener(String name) {
        this.name = name;
    }

    /**
     * Called immediatly after the scape is initialized.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void scapeInitialized(ScapeEvent scapeEvent) {
    }

    /**
     * Called immediatly after the scape is started.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void scapeStarted(ScapeEvent scapeEvent) {
    }

    /**
     * Called immediatly after the scape is stopped.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void scapeStopped(ScapeEvent scapeEvent) {
    }

    /**
     * Called immediatly after scape is iterated.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void scapeIterated(ScapeEvent scapeEvent) {
    }

    /**
     * Method called when the scape is ready for setup. That is, the scape has
     * been created (or it has just finished its previous run) but it has not
     * yet been initialized. This is an appropriate place to change model
     * paramters, persent user's with options, etc.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void scapeSetup(ScapeEvent scapeEvent) {
    }

    /**
     * Method called as the scape is about to be closed. Allows any final view
     * cleanup.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void scapeClosing(ScapeEvent scapeEvent) {
        ((Scape) scapeEvent.getSource()).removeScapeListener(this);
    }

    /**
     * Method called as the scape is about to be closed. Allows any final view
     * cleanup.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void environmentQuiting(ScapeEvent scapeEvent) {
    }

    /**
     * Method called immediatly after a model is deserialized.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void scapeDeserialized(ScapeEvent scapeEvent) {
    }

    /**
     * Add the view to the scape, registering it as a listener, and ensuring
     * that it hasn't been added to any other scapes.
     * 
     * @param scapeEvent
     *            the event for this scape to make this view the observer of
     * @throws TooManyListenersException
     *             the too many listeners exception
     * @exception TooManyListenersException
     *                on attempt to add a scape when one is allready added
     */
    public void scapeAdded(ScapeEvent scapeEvent) throws TooManyListenersException {
        if (scapeEvent == null) {
            throw new RuntimeException("Called " + this + "'s addScape with a null argument.");
        }

        if (!listeningToScape) {
            scape = (Scape) scapeEvent.getSource();
            listeningToScape = true;
        } else {
            throw new TooManyListenersException("Tried to add scape " + scapeEvent + " to view " + this +
                "; already listening to scape " + scape + ".");
        }
    }

    /**
     * Notifies the listener that the scape has removed it. The scape event
     * event must be from the scape that this listener is listening to.
     * 
     * @param scapeEvent
     *            the scape removed notification event
     */
    public void scapeRemoved(ScapeEvent scapeEvent) {
        if (this.scape == null) {
            throw new RuntimeException("Tried to remove a scape from a delegate with no scape.");
        }
        listeningToScape = false;
    }

    /**
     * Notifies the delegating view that something has happened on the scape.
     * Calls the scapeIterated, setup, started, stoped, closing, or
     * onDeserialized methods as appropriate. Calls notifyScapeUpdated, so you
     * will need to overide this method if you do not want to notify the scape
     * immediatly upon the scape... methods returning.
     * 
     * @param scapeEvent
     *            a scape event update
     */
    public void scapeNotification(ScapeEvent scapeEvent) {
        if (scapeEvent.getID() == ScapeEvent.REPORT_ITERATE) {
            scapeIterated(scapeEvent);
        } else if (scapeEvent.getID() == ScapeEvent.REQUEST_SETUP) {
            scapeSetup(scapeEvent);
        } else if (scapeEvent.getID() == ScapeEvent.REPORT_INITIALIZED) {
            scapeInitialized(scapeEvent);
        } else if (scapeEvent.getID() == ScapeEvent.REPORT_START) {
            scapeStarted(scapeEvent);
        } else if (scapeEvent.getID() == ScapeEvent.REPORT_STOP) {
            scapeStopped(scapeEvent);
        } else if (scapeEvent.getID() == ScapeEvent.REPORT_DESERIALIZED) {
            scapeDeserialized(scapeEvent);
        } else if (scapeEvent.getID() == ScapeEvent.REQUEST_CLOSE) {
            scapeClosing(scapeEvent);
        }
        if (isNotifyScapeAutomatically()) {
            notifyScapeUpdated();
        }
    }

    /**
     * Calls the observed scape and notifies it that the view has finished
     * updating its state. This method must be called after receiving any
     * scapeNotification event or the model will stall.
     */
    public void notifyScapeUpdated() {
        scape.respondControl(new ControlEvent(this, ControlEvent.REPORT_LISTENER_UPDATED));
    }

    /**
     * Is this a grpahic view? By default, returns false. Overide to return true
     * if this is a graphic view.
     * 
     * @return true, if is graphic
     */
    public boolean isGraphic() {
        return false;
    }

    /**
     * Returns true (default) if the listener is intended to be used only for
     * the current scape; typical of all but control related listeners.
     * 
     * @return true, if is life of scape
     */
    public boolean isLifeOfScape() {
        return true;
    }

    /**
     * Returns the scape the listner is listening to.
     * 
     * @return the scape
     */
    public Scape getScape() {
        return scape;
    }

    /**
     * Should the scape be notified automatically? Default is true.
     * 
     * @return true, if is notify scape automatically
     */
    public boolean isNotifyScapeAutomatically() {
        return notifyScapeAutomatically;
    }

    /**
     * Sets wether the scape is notified automatically. That is, when the
     * listener methods finish, should the scape be notified? Normally this
     * should be true - anytime everything needed to update from the current
     * scape state is known when the methods exit. But in some cases the sacape
     * should not continue iterating until something else happens. The most
     * obvious example is in a graphics view, where we need to be sure that the
     * actual graphics are updated before the scape moves on. But other cases
     * where you might want to wait might also happen; for example, you may want
     * to wait for some other state to change first, you may need to wait for a
     * process (like a database write) to occur in another thread, etc.
     * 
     * @param notifyScapeAutomatically
     *            true to send a respond update event back immediatly, false to
     *            wait
     */
    public void setNotifyScapeAutomatically(boolean notifyScapeAutomatically) {
        this.notifyScapeAutomatically = notifyScapeAutomatically;
    }

    /**
     * Returns a name for the view as defined by set name.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this view.
     * 
     * @param name
     *            a user relevant name for this view
     */
    public final void setName(String name) {
        this.name = name;
    }

    /**
     * Clones this object.
     * 
     * @return the object
     */
    public Object clone() {
        try {
            DefaultScapeListener clone = (DefaultScapeListener) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }

    /**
     * Returns a short description of this view. Sames as name unless
     * overridden.
     * 
     * @return the string
     */
    public String toString() {
        return getName();
    }
}
