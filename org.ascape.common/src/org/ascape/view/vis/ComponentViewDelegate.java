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
 * @history 2.9.1 7/10/02 Refacotred changed names to better conform to standard usage
 * @history 2.9 5/9/02 updated for new movie refactorings
 * @history 2.9 3/30/02 refactored out of Views.
 * @since 2.9
 */

package org.ascape.view.vis;

import java.awt.Component;
import java.awt.Container;
import java.util.TooManyListenersException;

import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.event.ScapeListenerDelegate;
import org.ascape.movie.MovieRecorder;

/**
 * Manages the relationship between a component view and its scape, including
 * determining when updates occur and managing the graphics recorder.
 */
public class ComponentViewDelegate extends ScapeListenerDelegate {

    /**
     * Are we waiting for a record to occur?.
     */
    private boolean recordNext;

    /**
     * The update currently being drawn.
     */
    protected int currentUpdate = 1;

    /**
     * A recorder for the graphic of this view. Usually null, unless this view
     * is being recorded.
     */
    private MovieRecorder recorder;

    /**
     * A flag that is true if we are potentially calling paint from within
     * another paint call. A little hacky, but other solutions using update
     * method did not work reliably.
     */
    private boolean recursiveCallToPaint;

    /**
     * Has the buffer been modified? Are we waiting for the paint method to
     * update it to the onscreen image?.
     */
    private boolean awaitingUpdate = false;

    /**
     * Constructs the ComponentViewDelegate.
     */
    public ComponentViewDelegate() {
        //We don't want to notify scape automatically, so that repaint can occur
        setNotifyScapeAutomatically(false);
    }


    /**
     * Constructs the ComponentViewDelegate.
     * 
     * @param scapeView
     *            the view this delegate is managing scape relationships for
     */
    public ComponentViewDelegate(ComponentView scapeView) {
        setScapeListener(scapeView);
        setNotifyScapeAutomatically(false);
    }

    /**
     * Notifies the listener that the scape has added it.
     * 
     * @param scapeEvent
     *            the scape added notification event
     * @throws TooManyListenersException
     *             the too many listeners exception
     * @exception java.util.TooManyListenersException
     *                on attempt to add this listener to another scape when one
     *                has already been assigned
     */
    public void scapeAdded(ScapeEvent scapeEvent) throws TooManyListenersException {
        super.scapeAdded(scapeEvent);
        ((ComponentView) getScapeListener()).onChangeIterationsPerRedraw();
    }

    /**
     * Method called as the scape is about to be closed. Allows any final view
     * cleanup.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void scapeClosing(ScapeEvent scapeEvent) {
        currentUpdate = ((ComponentView) getScapeListener()).getIterationsPerRedraw();
        if (recorder != null) {
            recorder.close();
            recorder = null;
        }
        if ((((ComponentView) getScapeListener()).getViewFrame() != null) && getScapeListener().isLifeOfScape()) {
            ((ComponentView) getScapeListener()).getViewFrame().dispose();
        } else {
            ((Scape) scapeEvent.getSource()).removeScapeListener(getScapeListener());
        }
    }

    /**
     * Called before the environment quits. Allows any final view cleanup.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void environmentQuiting(ScapeEvent scapeEvent) {
        //Get rid of any remaining non-scape specific listeners
        if ((((ComponentView) getScapeListener()).getViewFrame() != null) && !getScapeListener().isLifeOfScape()) {
            ((ComponentView) getScapeListener()).getViewFrame().dispose();
        }
    }

    /**
     * Notifies the delegating view that something has happened on the scape.
     * Manages recording and updating of views where appropriate, by calling
     * repaint. When the paint subsequently occurs, the scape is notified.
     * 
     * @param scapeEvent
     *            a scape event update
     */
    public void scapeNotification(ScapeEvent scapeEvent) {
        super.scapeNotification(scapeEvent);

        if (scapeEvent.getID() == ScapeEvent.REPORT_ITERATE) {
            currentUpdate++;
            if (currentUpdate > ((ComponentView) getScapeListener()).getIterationsPerRedraw()) {
                currentUpdate = 1;
            }
            if (recorder != null) {
                recordNext = true;
            }
        }

        if ((scapeEvent.getID() == ScapeEvent.REPORT_START) || (scapeEvent.getID() == ScapeEvent.REPORT_STOP) || scape.isPaused()) {
            currentUpdate = ((ComponentView) getScapeListener()).getIterationsPerRedraw();
        }

        if (scapeEvent.getID() == ScapeEvent.REQUEST_CHANGE_ITERATIONS_PER_REDRAW) {
            ((ComponentView) getScapeListener()).onChangeIterationsPerRedraw();
        }
        boolean allVisible = true;
        Component c = (Component) getScapeListener();
        while (c != null) {
            if (!c.isVisible() || !c.isShowing()) {
                allVisible = false;
                break;
            }
            c = c.getParent();
        }
        if (allVisible && (scapeEvent.getID() != ScapeEvent.REQUEST_CHANGE_ITERATIONS_PER_REDRAW) && (scapeEvent.getID() != ScapeEvent.TICK) && ((Container) getScapeListener()).isVisible() && ((Container) getScapeListener()).isShowing() && ((currentUpdate == ((ComponentView) getScapeListener()).getIterationsPerRedraw()) && (scape.isInitialized()))) {
            awaitingUpdate = true;
            ((ComponentView) getScapeListener()).updateScapeGraphics();
            ((Component) getScapeListener()).repaint();
        } else {
            notifyScapeUpdated();
        }
    }

    /**
     * Notifies the delegating view that something has happened on the scape.
     * Manages recording and updating of views where appropriate, by calling
     * repaint. When the paint subsequently occurs, the scape is notified.
     * 
     * @param scapeEvent
     *            a scape event update
     */
    public void scapeDeserialized(ScapeEvent scapeEvent) {
        ((ComponentView) getScapeListener()).getViewFrame().viewDeserialized();
    }

    /**
     * Handles anything that should occur when a componenent paint happens. If
     * recording, records. Notifes the scape. This method should be called from
     * the component view paint methods.
     */
    public synchronized void viewPainted() {
        if ((awaitingUpdate) && (!recursiveCallToPaint)) {
            if (recordNext && (recorder != null)) {
                recursiveCallToPaint = true;
                if (getScape().getPeriod() % 6 == 0) {
                    recorder.recordFrame();
                }
                recursiveCallToPaint = false;
            }
            recordNext = false;
            awaitingUpdate = false;
            notifyScapeUpdated();
        }
    }

    /**
     * Used to force a notification if the scape .
     */
    protected void forceScapeNotify() {
        if (awaitingUpdate) {
            notifyScapeUpdated();
        }
    }

    /**
     * Sets the recorder that can be used to record the graphics of this view.
     * If null, do not record.
     * 
     * @param recorder
     *            the recorder
     */
    public void setGraphicsRecorder(MovieRecorder recorder) {
        this.recorder = recorder;
    }
}
