/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.event;

import java.util.EventListener;
import java.util.TooManyListenersException;

import org.ascape.model.Scape;
import org.ascape.util.HasName;

/**
 * A listener to an agent scape. When a scape is updated, it is the listener's
 * responsility to update itself, and then inform the scape that updating is
 * finished, so that the scape can continue iterating through its rules.
 * 
 * @see DefaultScapeListener
 * @see ScapeListenerDelegate
 * @author Miles Parker
 * @version 2.9.1
 * @history 2.9.1 7/10/02 Refacotred to .event, changed names to better conform
 *          to standard usage
 * @history 2.9 7/2/02 major refacotring to more closely conform to Java usage
 * @history 1.2.6 10/25/99 added support for named listeners
 * @since 1.0
 */
public interface ScapeListener extends EventListener, HasName, Cloneable {

    /**
     * Called immediatly after the scape is initialized.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void scapeInitialized(ScapeEvent scapeEvent);

    /**
     * Called immediatly after the scape is started.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void scapeStarted(ScapeEvent scapeEvent);

    /**
     * Called immediatly after the scape is stopped.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void scapeStopped(ScapeEvent scapeEvent);

    /**
     * Called immediatly after scape is iterated.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void scapeIterated(ScapeEvent scapeEvent);

    /**
     * Method called when the scape is ready for setup. That is, the scape has
     * been created (or it has just finished its previous run) but it has not
     * yet been initialized. This is an appropriate place to change model
     * paramters, persent user's with options, etc.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void scapeSetup(ScapeEvent scapeEvent);

    /**
     * Method called as the scape is about to be closed. Allows any final view
     * cleanup.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void scapeClosing(ScapeEvent scapeEvent);

    /**
     * Method called as the environment is about to quit. Note that this method
     * may not actually be called by a scape at all. Allows any final view
     * cleanup.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void environmentQuiting(ScapeEvent scapeEvent);

    /**
     * Method called immediatly after a model is deserialized.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void scapeDeserialized(ScapeEvent scapeEvent);

    /**
     * Informs the listener that the agent scape has some kind of notification
     * for the listener,.
     * 
     * @param scapeEvent
     *            the scape setup event
     */
    public void scapeNotification(ScapeEvent scapeEvent);

    /**
     * Notifies the listener that the scape has added it. This is in affect a
     * call back from the scape after adding the listener. At this point, the
     * listener is responsible for responding back to the scape upon any
     * notifications. A listener <i>typically</i> has one and only one scape,
     * see below. A "good citizen" listener will typically make certain that it
     * isn't added to more than one scape at a time, but one can also imagine
     * cases where a listener might be interested in the activities of many
     * scapes and this would be a perfectly legitimate usage pattern.
     * 
     * @param scapeEvent
     *            the scape add event
     * @throws TooManyListenersException
     *             the too many listeners exception
     */
    public void scapeAdded(ScapeEvent scapeEvent) throws TooManyListenersException;

    /**
     * Removes the scape from this view. Typically there should ony be one scape
     * to remove. This method must be called whenever a listener is destroyed or
     * becomes unresponsive; otherwise the model will wait indefinetly for the
     * listener's updated response.
     * 
     * @param scapeEvent
     *            the scape removed event
     * @see scapeAdded
     */
    public void scapeRemoved(ScapeEvent scapeEvent);

    /**
     * Returns true if the listener is a graphical user interface component.
     * 
     * @return true, if is graphic
     */
    public boolean isGraphic();

    /**
     * Returns true if the listener is intended to be used only for the current
     * scape; typical of all but control related listeners.
     * 
     * @return true, if is life of scape
     */
    public boolean isLifeOfScape();

    /**
     * Returns the Scape being viewed.
     * 
     * @return the scape
     */
    public Scape getScape();

    /**
     * Require public access for clone.
     * 
     * @return the object
     */
    public Object clone();
}
