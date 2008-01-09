/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.view.vis;

import java.awt.Button;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.ascape.model.event.ControlEvent;
import org.ascape.model.event.ScapeCustomizer;
import org.ascape.model.event.ScapeListener;


/**
 * A class providing a simple control panel for a running model that does not
 * require Swing. Control views can be attached to any scape, and controls will
 * typically affect the model (entire collection of scapes) as a whole. This
 * class should be used when swing isn't available, but otherwise use
 * ControlBarView.
 * 
 * @author Miles Parker
 * @version 1.1.2
 * @history 1.1.2 5/17/99 Fixed over updating buttons, which was causing
 *          blinkiness in some browsers
 * @history First in 1.0
 * @since 1.0
 */
public class SimpleControlView extends PanelView implements ActionListener {

    /**
     * The allow quit.
     */
    protected boolean allowQuit = true;

    /**
     * The show iterations.
     */
    protected boolean showIterations = true;

    /**
     * The start button.
     */
    protected Button startButton;

    /**
     * The stop button.
     */
    protected Button stopButton;

    /**
     * The pause toggle button.
     */
    protected Button pauseToggleButton;

    /**
     * The step button.
     */
    protected Button stepButton;

    /**
     * The settings button.
     */
    protected Button settingsButton;

    /**
     * The quit button.
     */
    protected Button quitButton;

    /**
     * The iteration text.
     */
    protected Label iterationText;

    /**
     * The scape appears running.
     */
    protected boolean scapeAppearsRunning;

    /**
     * The scape appears paused.
     */
    protected boolean scapeAppearsPaused;

    /**
     * Constructs the conrtol view, creating any laying out its components.
     */
    public SimpleControlView() {
    }

    /**
     * Constructs the control view, creating any laying out its components.
     * 
     * @param allowQuit
     *            should the quit button be included?
     * @param showIterations
     *            include a label showing the current iteration?
     */
    public SimpleControlView(boolean allowQuit, boolean showIterations) {
        this.allowQuit = allowQuit;
        this.showIterations = showIterations;
    }

    /**
     * Create components and lay them out.
     */
    public void build() {
        setLayout(new GridLayout(1, 5, 6, 6));
        startButton = new Button();
        add(startButton);
        startButton.addActionListener(this);
        scapeAppearsRunning = false;
        startButton.setLabel("Start");
        stopButton = new Button();
        add(stopButton);
        stopButton.addActionListener(this);
        stopButton.setLabel("Stop");
        pauseToggleButton = new Button();
        add(pauseToggleButton);
        pauseToggleButton.addActionListener(this);
        pauseToggleButton.setLabel("Pause");
        stepButton = new Button();
        add(stepButton);
        stepButton.addActionListener(this);
        stepButton.setLabel("Step");
        settingsButton = new Button();
        add(settingsButton);
        settingsButton.addActionListener(this);
        settingsButton.setLabel("Settings");
        if (allowQuit) {
            quitButton = new Button();
            add(quitButton);
            quitButton.addActionListener(this);
            quitButton.setLabel("Quit");
        }
        if (showIterations) {
            iterationText = new Label();
            add(iterationText);
        }
        //setBackground(Color.lightGray);
    }

    /**
     * Responds to actions perfromed by components upon this view. For the
     * control view, these include, start, top, step, pause, resume, and restart
     * actions.
     * 
     * @param event
     *            the component event beign handled
     */
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == startButton) {
            if (!scapeAppearsRunning) {
                scape.respondControl(new ControlEvent(this, ControlEvent.REQUEST_START));
            } else {
                //Start restart sequence by stopping and setting restart flag
                scape.respondControl(new ControlEvent(this, ControlEvent.REQUEST_RESTART));
            }
        } else if (event.getSource() == stopButton) {
            scape.respondControl(new ControlEvent(this, ControlEvent.REQUEST_STOP));
        } else if (event.getSource() == stepButton) {
            scape.respondControl(new ControlEvent(this, ControlEvent.REQUEST_STEP));
        } else if (event.getSource() == pauseToggleButton) {
            if (!scapeAppearsPaused) {
                scape.respondControl(new ControlEvent(this, ControlEvent.REQUEST_PAUSE));
            } else {
                scape.respondControl(new ControlEvent(this, ControlEvent.REQUEST_RESUME));
            }
        } else if (event.getSource() == quitButton) {
            scape.respondControl(new ControlEvent(this, ControlEvent.REQUEST_QUIT));
        } else if (event.getSource() == settingsButton) {
            ScapeCustomizer customizer = getScape().getUIEnvironment().getCustomizer();
			if (!(getScape().getScapeListeners().contains(customizer))) {
                getScape().addView((ScapeListener) customizer);
            }
        }
    }

    /**
     * Update the components. Ensures that the state of all buttons matchhes the
     * state of the observed scape.
     */
    public synchronized void updateScapeGraphics() {
        if ((!scapeAppearsRunning) && (scape.isRunning())) {
            startButton.setLabel("Restart");
            stopButton.setEnabled(true);
            pauseToggleButton.setEnabled(true);
            scapeAppearsRunning = true;
        } else if ((scapeAppearsRunning) && (!scape.isRunning())) { //scape doesn't appear to be running
            startButton.setLabel("Start");
            //startButton.setEnabled(true);
            stopButton.setEnabled(false);
            pauseToggleButton.setEnabled(false);
            scapeAppearsRunning = false;
        }
        if ((!scapeAppearsPaused) && (scape.isPaused())) {
            pauseToggleButton.setLabel("Resume");
            scapeAppearsPaused = true;
        } else if ((scapeAppearsPaused) && (!scape.isPaused())) {
            pauseToggleButton.setLabel("Pause");
            scapeAppearsPaused = false;
        }
        if (scapeAppearsRunning && scapeAppearsPaused) {
            stepButton.setEnabled(true);
        } else {
            stepButton.setEnabled(false);
        }
        iterationText.setText(scape.getPeriodDescription());
        super.updateScapeGraphics();
    }

    /**
     * Does the view display a quit button, allowing the user to quit? Typically
     * false for applets, true otherwise.
     * 
     * @return true, if is allow quit
     */
    public boolean isAllowQuit() {
        return allowQuit;
    }

    /**
     * Should the view display a quit button, allowing the user to quit?
     * Typically false for applets, true otherwise.
     * 
     * @param allowQuit
     *            true to allow user to quit
     */
    public void setAllowQuit(boolean allowQuit) {
        this.allowQuit = allowQuit;
        build();
    }

    /**
     * Does the view display the current iteration? Typically false for applets,
     * true otherwise.
     * 
     * @return true, if is show iterations
     */
    public boolean isShowIterations() {
        return showIterations;
    }

    /**
     * Should the view display the current iteration?.
     * 
     * @param showIterations
     *            true to include a label showing the current iterations
     */
    public void setShowIterations(boolean showIterations) {
        this.showIterations = showIterations;
        build();
    }

    /**
     * Returns the preferred size of the control view, whcih is width 280 and
     * height 30.
     * 
     * @return the preferred size
     */
    public Dimension getPreferredSize() {
        return new Dimension(480, 30);
    }

    /**
     * Retruns a short description of this view.
     * 
     * @return the string
     */
    public String toString() {
        return "Controller view";
    }
}
