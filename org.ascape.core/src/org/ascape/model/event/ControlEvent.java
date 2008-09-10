/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.event;

import java.util.EventObject;

/**
 * Events which control the execution state of a model or collection of scapes.
 * 
 * @author Miles Parker
 * @version 2.9.1
 * @history 2.9.1 7/10/02 Refactored to .event, changed names to better conform
 *          to standard usage
 * @since 1.0
 */
public class ControlEvent extends EventObject {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * A listener of the target has been updated.
     */
    public final static int REPORT_LISTENER_UPDATED = 1;

    /**
     * The target is requested to start.
     */
    public final static int REQUEST_START = 2;

    /**
     * The target is requested to stop.
     */
    public final static int REQUEST_STOP = 3;

    /**
     * The target is requested to step one iteration.
     */
    public final static int REQUEST_STEP = 4;

    /**
     * The target is requested to restart; that is, stop and start from initial
     * state.
     */
    public final static int REQUEST_RESTART = 5;

    /**
     * The target is requested to pause.
     */
    public final static int REQUEST_PAUSE = -1;

    /**
     * The target is requested to resume.
     */
    public final static int REQUEST_RESUME = -2;

    /**
     * The target is requested to exit.
     */
    public final static int REQUEST_QUIT = -3;

    /**
     * The target is requested to save itself.
     */
    public final static int REQUEST_SAVE = -4;
    
    /**
     * The target is requested to open another model.
     */
    public final static int REQUEST_CLOSE = -5;

    /**
     * The target is requested to open another model, saving itself first.
     */
    public final static int REQUEST_OPEN = -6;

    /**
     * The target is requested to open a saved run, saving itself first.
     */
    public final static int REQUEST_OPEN_SAVED = -7;

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
    public ControlEvent(Object source, int id) {
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
            case REPORT_LISTENER_UPDATED:
                typeStr = "Report listener updated";
                break;
            case REQUEST_START:
                typeStr = "Request start";
                break;
            case REQUEST_STEP:
                typeStr = "Request step";
                break;
            case REQUEST_STOP:
                typeStr = "Request stop";
                break;
            case REQUEST_PAUSE:
                typeStr = "Request pause";
                break;
            case REQUEST_RESUME:
                typeStr = "Request resume";
                break;
            case REQUEST_QUIT:
                typeStr = "Request scape quit";
                break;
            case REQUEST_SAVE:
                typeStr = "Request save";
                break;
            case REQUEST_OPEN:
                typeStr = "Request open";
                break;
            case REQUEST_OPEN_SAVED:
                typeStr = "Request open a saved run";
                break;
            default:
                typeStr = "Unknown request";
        }
        return typeStr;
    }

    /**
     * Returns a string describing this event.
     * 
     * @return the string
     */
    public String toString() {
        return paramString() + " from " + getSource();
    }
}
