/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */


package org.ascape.runtime.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.Serializable;

import javax.swing.BoundedRangeModel;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.ascape.model.event.ControlEvent;
import org.ascape.model.event.ScapeEvent;
import org.ascape.view.vis.ScapeTransitionPanelView;


/**
 * A view providing a slider to control the speed of the model. Requires Swing.
 * 
 * @author Miles Parker
 * @version 3.0
 * @history 3.0 8/13/02 refcatored out of ControlBarView
 * @since 3.0
 */
public class SpeedSliderView extends ScapeTransitionPanelView /* implements Externalizable */ {

    /**
     * The Class SpeedSlider.
     */
    class SpeedSlider extends JSlider {

        /**
         * 
         */
        private static final long serialVersionUID = -4465792600546767334L;

        /**
         * The Constant KNOB_SNAP.
         */
        final static int KNOB_SNAP = 3;

        /**
         * The Constant PAUSE_SPEED.
         */
        final static int PAUSE_SPEED = 0;

        /**
         * The Constant NORMAL_SPEED.
         */
        final static int NORMAL_SPEED = 50;

        /**
         * The Constant FASTEST_SPEED.
         */
        final static int FASTEST_SPEED = 100;

        /**
         * The Constant SLOW_RANGE.
         */
        final static int SLOW_RANGE = NORMAL_SPEED - KNOB_SNAP * 2;

        /**
         * The Constant FAST_RANGE.
         */
        final static int FAST_RANGE = FASTEST_SPEED - NORMAL_SPEED - KNOB_SNAP * 2;

        /**
         * Instantiates a new speed slider.
         */
        public SpeedSlider() {
            super(0, FASTEST_SPEED, NORMAL_SPEED);
        }

        /**
         * In pause.
         * 
         * @param value
         *            the value
         * @return true, if successful
         */
        public boolean inPause(int value) {
            return value < PAUSE_SPEED + KNOB_SNAP;
        }

        /**
         * In slow range.
         * 
         * @param value
         *            the value
         * @return true, if successful
         */
        public boolean inSlowRange(int value) {
            return (value < NORMAL_SPEED - KNOB_SNAP) && !inPause(value);
        }

        /**
         * In normal.
         * 
         * @param value
         *            the value
         * @return true, if successful
         */
        public boolean inNormal(int value) {
            return (value >= NORMAL_SPEED - KNOB_SNAP) && (value < NORMAL_SPEED + KNOB_SNAP);
        }

        /**
         * In fast range.
         * 
         * @param value
         *            the value
         * @return true, if successful
         */
        public boolean inFastRange(int value) {
            return value >= NORMAL_SPEED + KNOB_SNAP;
        }

        /**
         * In fastest.
         * 
         * @param value
         *            the value
         * @return true, if successful
         */
        public boolean inFastest(int value) {
            return value >= FASTEST_SPEED - KNOB_SNAP;
        }

        /**
         * Slow value.
         * 
         * @param value
         *            the value
         * @return the float
         */
        public float slowValue(int value) {
            return ((SLOW_RANGE - (value - PAUSE_SPEED - KNOB_SNAP + 1)) / (float) SLOW_RANGE);
        }

        /**
         * Fast value.
         * 
         * @param value
         *            the value
         * @return the float
         */
        public float fastValue(int value) {
            //Take min to ignore value past knob snap
            return Math.min(((float) (value - (NORMAL_SPEED + KNOB_SNAP)) / (float) FAST_RANGE), 1.0f);
        }

        /**
         * We need a serializable ChangeListener (following how they do it in
         * JSlider).
         */
        private class ModelListener implements ChangeListener, Serializable {

            /**
             * 
             */
            private static final long serialVersionUID = -1045068819492557821L;

            /* (non-Javadoc)
             * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
             */
            public void stateChanged(ChangeEvent e) {
                fireStateChanged();
            }
        }

        /* (non-Javadoc)
         * @see javax.swing.JSlider#createChangeListener()
         */
        public ChangeListener createChangeListener() {
            return new ModelListener() {
                /**
                 * 
                 */
                private static final long serialVersionUID = 1659898244402772719L;

                public void stateChanged(ChangeEvent e) {
                    BoundedRangeModel source = (BoundedRangeModel) e.getSource();

                    if (inPause(source.getValue())) {
                        scape.respondControl(new ControlEvent(this, ControlEvent.REQUEST_PAUSE));
                    } else {
                        if (scape.isPaused()) {
                            scape.respondControl(new ControlEvent(this, ControlEvent.REQUEST_RESUME));
                        }
                    }
                    if (inSlowRange(source.getValue()) && !scape.isPaused()) {
                        delayInMillis = (long) (slowValue(source.getValue()) * maxDelayInMillis);
                    } else {
                        delayInMillis = 0;
                    }
                    if (!inFastRange(source.getValue())) {
                        getScape().setIterationsPerRedraw(1);
                    }
                    if (!source.getValueIsAdjusting() && inNormal(source.getValue())) {
                        //Recalibrate one iteration time
                        oneIterationInMillis = UNKNOWN_TIME;
                        startOneIterationInMillis = UNKNOWN_TIME;
                    }
                    if (inFastRange(source.getValue())) {
                        long normalMillis = oneIterationInMillis;
                        if (normalMillis == UNKNOWN_TIME) {
                            normalMillis = 150;
                        }
                        float waitRange = fastValue(source.getValue());
                        //We want to translate value so that it has a long area of relativly moinor change, and then approaches maximim quickly
                        //So we make the range exponential from linear
                        float waitFactor = ((float) (Math.pow(fastApproachWeight, waitRange)) - 1.0f) / (fastApproachWeight - 1.0f);
                        waitToRedrawInMillis = (long) (waitFactor * (float) (maxWaitToRedrawInMillis - normalMillis)) + normalMillis;
                        //We set wait iterations per redraw to max until we get to a place where wait to redraw is exceeded
                        getScape().setIterationsPerRedraw(Integer.MAX_VALUE);
                        startWaitToRedrawInMillis = UNKNOWN_TIME;
                        determiningRedrawWait = true;
                    } else {
                        waitToRedrawInMillis = 0;
                    }
                }
            };
        }

        /* (non-Javadoc)
         * @see java.awt.Component#getForeground()
         */
        public Color getForeground() {
            if (super.getForeground() != null) {
                if (inPause(getValue()) || inNormal(getValue()) || inFastest(getValue())) {
                    return super.getForeground().darker();
                } else {
                    return super.getForeground();
                }
            } else {
                return null;
            }
        }

        /* (non-Javadoc)
         * @see javax.swing.JSlider#setValue(int)
         */
        public void setValue(int value) {
            if (inFastest(value)) {
                super.setValue(FASTEST_SPEED);
                setSnapToTicks(true);
            } else if (inNormal(value)) {
                super.setValue(NORMAL_SPEED);
                setSnapToTicks(true);
            } else if (inPause(value)) {
                super.setValue(PAUSE_SPEED);
                setSnapToTicks(true);
            } else {
                super.setValue(value);
                setSnapToTicks(false);
            }
        }
    }

    /**
     * The speed slider.
     */
    private JSlider speedSlider;

    /**
     * The speed icon.
     */
    private JLabel speedIcon;

    /**
     * The max delay in millis.
     */
    private long maxDelayInMillis = 1500;

    /**
     * The delay in millis.
     */
    private long delayInMillis = 0;

    /**
     * The max wait to redraw in millis.
     */
    private long maxWaitToRedrawInMillis = 15000;

    /**
     * The determining redraw wait.
     */
    private boolean determiningRedrawWait;
    
    /**
     * The start wait to redraw in millis.
     */
    private long startWaitToRedrawInMillis = UNKNOWN_TIME;
    
    /**
     * The wait to redraw in millis.
     */
    private long waitToRedrawInMillis = 0;
    
    /**
     * The iterations since began wait for redraw.
     */
    private int iterationsSinceBeganWaitForRedraw;

    //A control on how quickly the slider causes the model to approach maximum wait state
    //Any value from just above 1.0 (to avoid d/0 error) will work; higher numbers cause the slope to be lower at first and higher later
    /**
     * The fast approach weight.
     */
    private float fastApproachWeight = 300;

    /**
     * The Constant UNKNOWN_TIME.
     */
    final static long UNKNOWN_TIME = Long.MAX_VALUE;

    //The length of time it takes to do one iterastion in millis
    /**
     * The one iteration in millis.
     */
    private long oneIterationInMillis = UNKNOWN_TIME;

    //The time we started calculating the length of time to do one iteration in millis
    /**
     * The start one iteration in millis.
     */
    private long startOneIterationInMillis = UNKNOWN_TIME;

    /**
     * The prior iterations per redraw.
     */
    private int priorIterationsPerRedraw;

    /**
     * Constructs the control view, creating and laying out its components.
     */
    public SpeedSliderView() {
        this("Speed Slider");
    }

    /**
     * Constructs the speed slider view.
     * 
     * @param name
     *            a user relevant name for this view
     */
    public SpeedSliderView(String name) {
        super(name);
        speedSlider = new SpeedSlider();
        speedSlider.setPaintTrack(true);
        speedSlider.setMajorTickSpacing(SpeedSlider.NORMAL_SPEED);
        //speedSlider.setInverted(true);
        setLayout(new BorderLayout());
        add(speedSlider, BorderLayout.CENTER);
        speedIcon = new JLabel(DesktopEnvironment.getIcon("GoalFlag"));
        add(speedIcon, BorderLayout.EAST);
        setBorder(DesktopEnvironment.getInfoAreaBorder());
        //speedPanel.setBorder(new ShadowBorder(ShadowBorder.LOWERED));
        //speedSlider.putClientProperty("JSlider.isFilled", Boolean.TRUE);
        speedSlider.setToolTipText("Set Model Speed [Rightmost is Fastest]");
    }

    /**
     * Called on interation; delays models return by delay slider setting.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void scapeIterated(ScapeEvent scapeEvent) {
        //We put in the second check just in case we have subsequently moved below threshold
        if (determiningRedrawWait) {
            if (speedSlider.getValue() >= SpeedSlider.NORMAL_SPEED + SpeedSlider.KNOB_SNAP) {
                if (startWaitToRedrawInMillis == UNKNOWN_TIME) {
                    startWaitToRedrawInMillis = System.currentTimeMillis();
                    iterationsSinceBeganWaitForRedraw = 0;
                } else if (getScape().getIterationsPerRedraw() != 1) {
                    iterationsSinceBeganWaitForRedraw++;
                    if (System.currentTimeMillis() - startWaitToRedrawInMillis >= waitToRedrawInMillis) {
                        //force a redraw immediatly
                        getScape().setIterationsPerRedraw(1);
                    }
                }
                //When we return from the refresh immediatly redraww request, the following will be true
                else {
                    getScape().setIterationsPerRedraw(iterationsSinceBeganWaitForRedraw);
                    determiningRedrawWait = false;
                }
            } else { //We're no longer determining wait
                determiningRedrawWait = false;
            }
        }
        if ((oneIterationInMillis == UNKNOWN_TIME) && (startOneIterationInMillis != UNKNOWN_TIME)) {
            oneIterationInMillis = System.currentTimeMillis() - startOneIterationInMillis;
            getScape().setIterationsPerRedraw(priorIterationsPerRedraw);
        }
        super.scapeIterated(scapeEvent);
        if (delayInMillis != 0) {
            try {
                Thread.sleep(delayInMillis);
            } catch (InterruptedException e) {
                //We don't care if we're interrupted
            }
        }
        if (oneIterationInMillis == UNKNOWN_TIME) {
            priorIterationsPerRedraw = getScape().getIterationsPerRedraw();
            getScape().setIterationsPerRedraw(1);
            startOneIterationInMillis = System.currentTimeMillis();
        }
    }

    /* (non-Javadoc)
     * @see javax.swing.JComponent#getPreferredSize()
     */
    public Dimension getPreferredSize() {
        return new Dimension(150, super.getPreferredSize().height);
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.ScapeTransitionView#environmentNowNoScape()
     */
    public void environmentNowNoScape() {
        super.environmentNowNoScape();
        speedSlider.setEnabled(false);
        speedIcon.setEnabled(false);
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.ScapeTransitionView#scapeNowRunning()
     */
    public void scapeNowRunning() {
        super.scapeNowRunning();
        speedSlider.setEnabled(true);
        speedIcon.setEnabled(true);
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.ScapeTransitionView#scapeNowStopped()
     */
    public void scapeNowStopped() {
        super.scapeNowStopped();
        if (speedSlider.getValue() > 0) {
            //We want to retain its old value if the model is unpaused, so we don't want to change its position
            speedSlider.setEnabled(false);
            speedIcon.setEnabled(false);
        } else {
            //Otehrwise, we can leave it enabled since its present value is valid
            speedSlider.setEnabled(true);
            speedIcon.setEnabled(true);
        }
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.ScapeTransitionView#scapeNowPaused()
     */
    public void scapeNowPaused() {
        super.scapeNowPaused();
        if (speedSlider.getValue() > 0) {
            //We want to retain its old value if the model is unpaused, so we don't want to change its position
            speedSlider.setEnabled(false);
            speedIcon.setEnabled(false);
        } else {
            //Otehrwise, we can leave it enabled since its present value is valid
            speedSlider.setEnabled(true);
            speedIcon.setEnabled(true);
        }
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.ScapeTransitionView#scapeNowResumed()
     */
    public void scapeNowResumed() {
        super.scapeNowResumed();
        speedSlider.setEnabled(true);
        speedIcon.setEnabled(true);
        if (speedSlider.getValue() < SpeedSlider.PAUSE_SPEED + SpeedSlider.KNOB_SNAP) {
            //Just return slider value to normal...we might want to review this behavior and set it to 5 (slowest) instead
            //we'll see what looks best
            speedSlider.setValue(SpeedSlider.NORMAL_SPEED);
        }
    }

    /**
     * Gets the max wait to redraw in millis.
     * 
     * @return the max wait to redraw in millis
     */
    public long getMaxWaitToRedrawInMillis() {
        return maxWaitToRedrawInMillis;
    }

    /**
     * Sets the max wait to redraw in millis.
     * 
     * @param maxWaitToRedrawInMillis
     *            the new max wait to redraw in millis
     */
    public void setMaxWaitToRedrawInMillis(long maxWaitToRedrawInMillis) {
        this.maxWaitToRedrawInMillis = maxWaitToRedrawInMillis;
    }

    /**
     * Gets the max delay in millis.
     * 
     * @return the max delay in millis
     */
    public long getMaxDelayInMillis() {
        return maxDelayInMillis;
    }

    /**
     * Sets the max delay in millis.
     * 
     * @param maxDelayInMillis
     *            the new max delay in millis
     */
    public void setMaxDelayInMillis(long maxDelayInMillis) {
        this.maxDelayInMillis = maxDelayInMillis;
    }
}
