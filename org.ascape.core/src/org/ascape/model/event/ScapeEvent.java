/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.event;

import java.util.EventObject;

/**
 * An event describing some change in scape state. todo change to enumeration
 * 
 * @author Miles Parker
 * @version 2.9.1
 * @history 2.9.1 7/10/02 Refacotred to .event, changed names to better conform
 *          to standard usage
 * @history 1.5 1/15/00 a few small changes since 1.0
 * @since 1.0
 */
public class ScapeEvent extends EventObject {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The scape is in a pre-initialization state, and can be modified freely.
     * Warning; the internal state of the scape is not consistent at this point;
     * this event should only be used by objects which set scape state, not by
     * objects that attempt to use it.
     */
    public final static int REQUEST_SETUP = -1;

    /**
     * The scape is closing, and requests scape specific views to finish their
     * business and destroy themselves.
     */
    public final static int REQUEST_CLOSE = -2;

    /**
     * The entire environment is quitting, and requests all views to finish
     * their business and destroy itself.
     */
    public final static int REQUEST_QUIT = -3;

    /**
     * The scape has been initialized.
     */
    public final static int REPORT_INITIALIZED = -4;

    /**
     * The scape requests the listener to take notice that it has been added to
     * the scape.
     */
    public final static int REPORT_ADDED = -5;

    /**
     * The scape requests the listener to take notice that it has been removed
     * from the scape.
     */
    public final static int REPORT_REMOVED = -6;

    /**
     * The scape requests the views to change their iterationsPerRedraw.
     */
    public final static int REQUEST_CHANGE_ITERATIONS_PER_REDRAW = -7;

    /**
     * The scape has been initialized, had stats collected, and started.
     */
    public final static int REPORT_START = 1;

    /**
     * The scape has stopped.
     */
    public final static int REPORT_STOP = 2;

    /**
     * The scape has been updated (iterated.)
     */
    public final static int REPORT_ITERATE = 3;

    /**
     * A 'tick' event gerated while a scape is paused.
     */
    public final static int TICK = 4;

    /**
     * The scape has been deserialized.
     */
    public final static int REPORT_DESERIALIZED = 5;

    /**
     * The id.
     */
    private int id;

    /**
     * Constructs a control event, used to control a scape.
     * 
     * @param source
     *            the object firing this alert event.
     * @param id
     *            the id
     */
    public ScapeEvent(Object source, int id) {
        super(source);
        this.id = id;
    }

    /**
     * Gets the id decribing the control event.
     * 
     * @return the ID
     */
    public int getID() {
        return id;
    }

    /**
     * Returns a paramter string describing this event.
     * 
     * @return the string
     */
    public String paramString() {
        String typeStr;
        switch (id) {
            case REPORT_START:
                typeStr = "Scape started";
                break;
            case REPORT_STOP:
                typeStr = "Scape stopped";
                break;
            case REPORT_ITERATE:
                typeStr = "Scape iterated";
                break;
            case TICK:
                typeStr = "Scape idle tick";
                break;
            case REPORT_INITIALIZED:
                typeStr = "Scape initialized";
                break;
            case REQUEST_SETUP:
                typeStr = "Setup requested";
                break;
            case REQUEST_CLOSE:
                typeStr = "Close requested";
                break;
            case REQUEST_QUIT:
                typeStr = "Quit requested";
                break;
            case REQUEST_CHANGE_ITERATIONS_PER_REDRAW:
                typeStr = "Change in iterationsPerRedraw requested";
                break;
            case REPORT_ADDED:
                typeStr = "Scape added";
                break;
            case REPORT_DESERIALIZED:
                typeStr = "Scape deserialized";
                break;
            default:
                typeStr = "Unknown event";
        }
        return typeStr;
    }

    /**
     * Reutrns a descriptive string for this event in the form <Event> for
     * <Scape>.
     * 
     * @return the string
     */
    public String toString() {
        return paramString() + " for " + source;
    }
}
