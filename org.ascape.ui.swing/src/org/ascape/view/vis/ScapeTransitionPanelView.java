/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.view.vis;

import java.util.TooManyListenersException;

import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;

/**
 * A view providing notification when a scape transitions from one state to
 * another. For example, when a scape was paused and is now resumed, an
 * apporiate method will be called. Additionally, this view also guarantees that
 * updateScapeGraphics of the view at least 6 times a second regardless of the
 * nature of the scape notification. This will allow pauses and resumes to be
 * noticed in time to immediatly alert the user, regardless of what is happening
 * with the model.
 * 
 * Note: instead of subclassing this class consider using the cleaner delgate pattern
 * with the ScapeTransitionListener.
 * 
 * @author Miles Parker
 * @version 3.0
 * @history 3.0 8/13/02 refcatored out of ControlActionView
 * @since 3.0
 */
public class ScapeTransitionPanelView extends PanelView {

    /**
     * The last update in millis.
     */
    private long lastUpdateInMillis = 0;

    /**
     * The max millis between updates.
     */
    private long maxMillisBetweenUpdates = 1000 / 6;
    
    /**
     * Did the scape appear to be running last iteration?.
     */
    private boolean lastScapeAppearsRunning = false;
    
    /**
     * Did the scape appear to be paused last iteration?.
     */
    protected boolean lastScapeAppearsPaused = false;

    /**
     * Constructs the control view, creating and laying out its components.
     */
    public ScapeTransitionPanelView() {
        this("Scape Transition View");
    }

    /**
     * Constructs the speed slider view.
     * 
     * @param name
     *            a user relevant name for this view
     */
    public ScapeTransitionPanelView(String name) {
        super(name);
        delegate = new ComponentViewDelegate(this) {
            /**
             * 
             */
            private static final long serialVersionUID = -5981783677753234533L;

            public void scapeAdded(ScapeEvent scapeEvent) throws TooManyListenersException {
                scape = (Scape) scapeEvent.getSource();
            }
        };
    }

    /* (non-Javadoc)
     * @see javax.swing.JComponent#addNotify()
     */
    public void addNotify() {
        super.addNotify();
        if (getScape() == null) {
            environmentNowNoScape();
        } else {
            environmentNowScape();
        }
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.PanelView#build()
     */
    public void build() {
        super.build();
        //Force an update no matter what the state
        lastUpdateInMillis = 0;
    }

    /**
     * Scape now running.
     */
    public void scapeNowRunning() {
    }

    /**
     * Scape now stopped.
     */
    public void scapeNowStopped() {
    }

    /**
     * Scape now paused.
     */
    public void scapeNowPaused() {
    }

    /**
     * Scape now resumed.
     */
    public void scapeNowResumed() {
    }

    /**
     * Scape now steppable.
     */
    public void scapeNowSteppable() {
    }

    /**
     * Environment now scape.
     */
    public void environmentNowScape() {
    }

    /**
     * Environment now no scape.
     */
    public void environmentNowNoScape() {
    }

    /**
     * Called on interation; delays models return by delay slider setting.
     * 
     * @param scapeEvent
     *            the scape event
     * @throws TooManyListenersException
     *             the too many listeners exception
     */
    public void scapeAdded(ScapeEvent scapeEvent) throws TooManyListenersException {
        super.scapeAdded(scapeEvent);
        //Force updates for running and pause...
        lastScapeAppearsPaused = !getScape().isPaused();
        lastScapeAppearsRunning = !getScape().isRunning();
        environmentNowScape();
    }

    /**
     * Called on interation; delays models return by delay slider setting.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void scapeRemoved(ScapeEvent scapeEvent) {
        super.scapeRemoved(scapeEvent);
        scape = null;
        environmentNowNoScape();
    }

    /**
     * Update the components. Ensures that the state of all buttons matchhes the
     * state of the observed scape.
     */
    public synchronized void updateScapeGraphics() {
        if (scape != null) {
            boolean scapeAppearsRunning = getScape().isRunning();
            boolean scapeAppearsPaused = getScape().isPaused();
            if (scapeAppearsRunning && !lastScapeAppearsRunning) {
                scapeNowRunning();
                if (scapeAppearsPaused) {
                    scapeNowPaused();
                }
            } else if (!scapeAppearsRunning && lastScapeAppearsRunning) {
                //Todo figure out why this fix below solves the problem where the menubar is not always properly updated on stop
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                }
                scapeNowStopped();
            }

            if (scapeAppearsPaused && !lastScapeAppearsPaused) {
                scapeNowPaused();
            }
            //We don't resume if the scape is not now running...
            if (!scapeAppearsPaused && lastScapeAppearsPaused && scapeAppearsRunning) {
                scapeNowResumed();
            }
            if (((scapeAppearsPaused && !lastScapeAppearsPaused) && scapeAppearsRunning) || ((scapeAppearsRunning && !lastScapeAppearsRunning) && scapeAppearsPaused)) {
                scapeNowSteppable();
            }

            lastScapeAppearsRunning = scapeAppearsRunning;
            lastScapeAppearsPaused = scapeAppearsPaused;
            lastUpdateInMillis = System.currentTimeMillis();
        }
    }

    /**
     * Returns true if the listener is intended to be used only for the current
     * scape; in this case returns false because control views typically will
     * exist for multiple scapes.
     * 
     * @return true, if is life of scape
     */
    public boolean isLifeOfScape() {
        return false;
    }

    /**
     * Notifies this view that something has happened on the scape. This view
     * then has a chance to update itself, and this super method then notifies
     * the scape that the view itself has been updated. By default, calls the
     * onStart, updateScapeGraphics, or onStop method as appropriate, and then
     * notifies scape.
     * 
     * @param scapeEvent
     *            a scape event update
     */
    public void scapeNotification(ScapeEvent scapeEvent) {
        super.scapeNotification(scapeEvent);
        //Unlike other views want to update every 1/6 a second no matter what..
        //We also want to continue updating if this is a pause tick so that we properly register pause state changes
        if (System.currentTimeMillis() - lastUpdateInMillis > maxMillisBetweenUpdates) {
            updateScapeGraphics();
        }
    }
}
