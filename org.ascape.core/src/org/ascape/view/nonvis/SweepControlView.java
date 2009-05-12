/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.view.nonvis;

import java.util.TooManyListenersException;

import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;
import org.ascape.util.sweep.SweepGroup;

/**
 * A view that is used in conjunction with a sweep group to control model
 * parameters. While sweep group continues to have a next parameter setting
 * state, the sweep control view will restart the scape with the next sweep
 * group settings.
 * 
 * @see org.ascape.util.sweep.SweepGroup
 * @see org.ascape.view.nonvis.DataOutputView
 * @see org.ascape.model.Scape
 * @author Miles Parker
 * @version 1.9.2 8/1/00
 * @history 1.9.2 2/10/01 added name constructor
 * @history 1.9 8/1/00 first in
 * @since 1.9
 */
public class SweepControlView extends NonGraphicView {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * The sweep.
     */
    private SweepGroup sweep;

    /**
     * Consturcts a new sweep view.
     */
    public SweepControlView() {
        sweep = new SweepGroup();
    }

    /**
     * Consturcts a new sweep view.
     * 
     * @param name
     *            the sweep group name
     */
    public SweepControlView(String name) {
        this();
        setName(name);
    }

    /**
     * Consturcts a new sweep view.
     * 
     * @param sweep
     *            the sweep group that will be used for this sweep view.
     */
    public SweepControlView(SweepGroup sweep) {
        setSweepGroup(sweep);
    }

    /**
     * On scape add, set scape auto restart to false, as sweep view will be
     * handling scape control.
     * 
     * @param scapeEvent
     *            the scape event
     * @throws TooManyListenersException
     *             the too many listeners exception
     */
    public void scapeAdded(ScapeEvent scapeEvent) throws TooManyListenersException {
        super.scapeAdded(scapeEvent);
        //as scape will be controlled by
        ((Scape) scapeEvent.getSource()).setAutoRestart(false);
    }

    /**
     * On scape setup, create a new file and set the output stream to write to
     * it.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void scapeSetup(ScapeEvent scapeEvent) {
        if (sweep.hasNext()) {
            sweep.next();
        }
    }

    /**
     * On scape stop, close the data stream and files. If you don't want the
     * file closed on stop, override this method. Similarily, you could override
     * onStart to open a new data stream.
     * 
     * @param scapeEvent
     *            the scape event
     * @see org.ascape.view.nonvis.DataOutputView#scapeStarted
     * @see org.ascape.view.nonvis.DataOutputView#scapeStopped
     */
    public void scapeStopped(ScapeEvent scapeEvent) {
        if (sweep.hasNext()) {
            scape.getRunner().requestRestart();
        } else {
            scape.getRunner().close();
            //Close file streams
        }
    }

    /**
     * Sets the runs per, or number of runs per each sweep setting.
     * 
     * @return the sweep group
     */
    public SweepGroup getSweepGroup() {
        return sweep;
    }

    /**
     * Returns the runs per, or number of runs per each sweep setting.
     * 
     * @param sweep
     *            the sweep
     */
    public void setSweepGroup(SweepGroup sweep) {
        this.sweep = sweep;
    }

    /**
     * Returns a string describing this as a "Sweep Control View".
     * 
     * @return the string
     */
    public String toString() {
        return "Sweep Control View";
    }
}
