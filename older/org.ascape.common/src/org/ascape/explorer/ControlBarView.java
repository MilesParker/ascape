/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.explorer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.TooManyListenersException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.event.InternalFrameAdapter;

import org.ascape.model.ModelRoot;
import org.ascape.model.event.ScapeEvent;

/**
 * A class providing a control bar for controlling a model. Provides start,
 * stop, restart, pause, resume, quit, info, status, and new charts. Control
 * views can be attached to any scape, and controls will typically affect the
 * model (entire collection of scapes) as a whole.s Use SimpleControlView if
 * Swing is not available or if simple buttons are preferred to image buttons
 * with tool-tips. Requires Swing.
 * 
 * @author Miles Parker
 * @version 3.0
 * @history 3.0 9/1/02 Many general improvments
 * @history 2.9 5/9/02 updated for new movie refactorings
 * @history 2.9 2/1/02 major changes to functionality, including quick slider,
 *          factoring out control action view, etc..
 * @history 1.9 9/20/00 Much new functionality, including delay functionality,
 *          and access to quicktime movie creation
 * @history 1.2 7/14/99 Added counter to view, made JToolBar a delegated member
 *          rather than direct superclass
 * @history 1.1.1 5/1/1999 fixes, support for different frame types
 * @history 1.0.2 3/6/1999 numerous update changes, made aware of view frame
 * @history 1.0.1 added support for removing scapes
 * @since 1.0
 */
public class ControlBarView extends ControlActionView /* implements Externalizable */ {

    /**
     * The period label.
     */
    private JLabel periodLabel;

    /**
     * The status label.
     */
    private JLabel statusLabel;

    /**
     * The frame panel.
     */
    private JPanel framePanel;

    /**
     * The frame list.
     */
    private JComboBox frameList;

    /**
     * The start restart button.
     */
    private JButton startRestartButton;

    /**
     * The pause resume button.
     */
    private JButton pauseResumeButton;

    /**
     * The record start stop button.
     */
    private JButton recordStartStopButton;

    /**
     * The gbl.
     */
    private GridBagLayout gbl;

    /**
     * The info button.
     */
    protected JButton infoButton;

    /**
     * The quit button.
     */
    protected JButton quitButton;

    /**
     * The add TS button.
     */
    protected JButton addTSButton;

    /**
     * The add hist button.
     */
    protected JButton addHistButton;

    /**
     * The add pie button.
     */
    protected JButton addPieButton;

    /**
     * The full normal window button.
     */
    private JButton fullNormalWindowButton;

    /**
     * The ignore frame selection.
     */
    private boolean ignoreFrameSelection;

    /**
     * The slider view.
     */
    private SpeedSliderView sliderView;

    /**
     * The options bar.
     */
    protected JToolBar optionsBar;

    /**
     * The file bar.
     */
    protected JToolBar fileBar;

    /**
     * The run control bar.
     */
    protected JToolBar runControlBar;

    /**
     * Constructs the control view, creating and laying out its components.
     */
    public ControlBarView() {
        this("Control Bar");
    }

    /**
     * Constructs the control view, creating and laying out its components.
     * 
     * @param name
     *            a user relevant name for this view
     */
    public ControlBarView(String name) {
        super(name);
        //We have to put this here to guarantee it will be part of user environmnet
        sliderView = new SpeedSliderView();
        if (UserEnvironment.getDefaultEnvironment() != null) {
            UserEnvironment.getDefaultEnvironment().addView(sliderView, false);
        } else {
            ModelRoot.getEnvironment().addView(sliderView, false);
        }

        gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints gbc = gbl.getConstraints(this);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;

        framePanel = new JPanel();
        framePanel.setLayout(new BorderLayout());
        framePanel.setBorder(BorderFactory.createEmptyBorder(3, 2, 3, 2));
        add(framePanel, gbc);

        statusLabel = UserEnvironment.createLabel(60);
        periodLabel = UserEnvironment.createLabel(100);
        gbc.gridx++;
        add(statusLabel, gbc);
        gbc.gridx++;
        add(periodLabel, gbc);

        fileBar = UserEnvironment.createToolbar();

        gbc.gridx++;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(fileBar, gbc);


        UserEnvironment.addToolBarButton(fileBar, getOpenAction());
        UserEnvironment.addToolBarButton(fileBar, getOpenSavedAction());
        UserEnvironment.addToolBarButton(fileBar, getCloseAction());
        UserEnvironment.addToolBarButton(fileBar, getSaveAction());
        UserEnvironment.addToolBarButton(fileBar, getQuitAction());

        runControlBar = UserEnvironment.createToolbar();

        gbc.gridx++;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(runControlBar, gbc);

        startRestartButton = UserEnvironment.addToolBarButton(runControlBar);
        pauseResumeButton = UserEnvironment.addToolBarButton(runControlBar);
        UserEnvironment.addToolBarButton(runControlBar, getStepAction());
        UserEnvironment.addToolBarButton(runControlBar, getStopAction());

        runControlBar.add(sliderView);
        sliderView.build();

        optionsBar = UserEnvironment.createToolbar();

        gbc.gridx++;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(optionsBar, gbc);

        UserEnvironment.addToolBarButton(optionsBar, getSettingsAction());
        UserEnvironment.addToolBarButton(optionsBar, getSearchAction());
        UserEnvironment.addToolBarButton(optionsBar, getCaptureDeskAction());
        recordStartStopButton = UserEnvironment.addToolBarButton(optionsBar, getRecordStartAction());
        UserEnvironment.addToolBarButton(optionsBar, getInfoAction());

        optionsBar.addSeparator();

        UserEnvironment.addToolBarButton(optionsBar, getAddTSAction());
        UserEnvironment.addToolBarButton(optionsBar, getAddHistAction());
        UserEnvironment.addToolBarButton(optionsBar, getAddPieAction());

        if (UserEnvironment.getDefaultEnvironment() != null) {
            fullNormalWindowButton = UserEnvironment.addToolBarButton(optionsBar, getFullWindowAction());
            changeInFullScreen();
        }

        //disallow resizing
        if (getViewFrame() != null) {
            if (getViewFrame().getFrameImp() instanceof JFrame) {
                ((JFrame) getViewFrame().getFrameImp()).setResizable(false);
            } else if (getViewFrame().getFrameImp() instanceof JInternalFrame) {
                ((JInternalFrame) getViewFrame().getFrameImp()).setResizable(false);
            }
        }
        startRestartButton.setAction(getStartRestartAction());
        pauseResumeButton.setAction(getPauseResumeAction());
        statusLabel.setForeground(Color.black);
        periodLabel.setForeground(Color.black);
        buildFrameList();
    }

    /* (non-Javadoc)
     * @see java.awt.Container#add(java.lang.String, java.awt.Component)
     */
    public Component add(String name, Component c) {
        super.add(c, gbl.getConstraints(this));
        return null;
    }

    /**
     * Constructs the tool bar buttons and actions, and places them within the
     * toolbar.
     */
    public void build() {
        super.build();
        //We need to start new thread so that we don't get locked
        if (getViewFrame() != null) {
            if (getViewFrame().getFrameImp() instanceof Window) {
                ((Window) getViewFrame().getFrameImp()).addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        (new Thread("Quit") {
                            public void run() {
                                getScape().getModel().quit();
                            }
                        }).start();
                    }
                });
            } else if (getViewFrame().getFrameImp() instanceof Window) {
                ((JInternalFrame) getViewFrame().getFrameImp()).addInternalFrameListener(new InternalFrameAdapter() {
                    @SuppressWarnings("unused")
                    public void internalFrameClosing(WindowEvent e) {
                        (new Thread("Quit") {
                            public void run() {
                                getScape().getModel().quit();
                            }
                        }).start();
                    }
                });
            }
        }

        //minor hack, we don't want a period where actions are blank, so we force update right away
        updateScapeGraphics();
        repaint();
    }

    /**
     * Builds the frame list.
     */
    protected void buildFrameList() {
        if (UserEnvironment.getDefaultEnvironment() != null) {
            framePanel.removeAll();
            frameList = new JComboBox(UserEnvironment.getDefaultEnvironment().getAllFrames()) {
                public Dimension getPreferredSize() {
                    return new Dimension(100, super.getPreferredSize().height);
                }
            };
            frameList.setEditable(false);
            ItemListener frameSelectAction = new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (!ignoreFrameSelection && getScape() != null && getScape().isRunning()) {
                        if (frameList.getSelectedItem() != null) {
                            ((ViewFrameBridge) e.getItem()).toFront();
                        }
                    }
                }
            };
            frameList.addItemListener(frameSelectAction);
            framePanel.add(frameList);
        }
    }

    /**
     * Builds the on scape.
     */
    private void buildOnScape() {
        startRestartButton.setAction(getStartRestartAction());
        if (scape.isPaused()) {
            statusLabel.setForeground(Color.black);
        } else {
            statusLabel.setForeground(Color.gray);
        }
        buildFrameList();
        if (!scape.isPaused()) {
            periodLabel.setForeground(Color.black);
        } else {
            periodLabel.setForeground(Color.gray);
        }
        if (sliderView.getScape() != scape) {
            scape.addView(sliderView, false);
        }
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
        buildOnScape();
        updateScapeGraphics();
    }

    /**
     * Update the components. Ensures that the state of all buttons matchhes the
     * state of the observed scape.
     */
    public synchronized void updateScapeGraphics() {
        super.updateScapeGraphics();
        if (getScape() != null) {
            periodLabel.setText(getScape().getPeriodDescription());
            if (recorder != null) {
                recorder.statusUpdate();
            }
        }
    }

    /* (non-Javadoc)
     * @see org.ascape.explorer.ControlActionView#scapeNowRunning()
     */
    public void scapeNowRunning() {
        super.scapeNowRunning();
        statusLabel.setText("Running");
    }

    /* (non-Javadoc)
     * @see org.ascape.explorer.ControlActionView#scapeNowStopped()
     */
    public void scapeNowStopped() {
        super.scapeNowStopped();
        periodLabel.setForeground(Color.black);
        statusLabel.setForeground(Color.black);
        statusLabel.setText("Stopped");
    }

    /* (non-Javadoc)
     * @see org.ascape.explorer.ControlActionView#scapeNowPaused()
     */
    public void scapeNowPaused() {
        super.scapeNowPaused();
        periodLabel.setForeground(Color.gray);
        statusLabel.setForeground(Color.gray);
        statusLabel.setText("Paused");
    }

    /* (non-Javadoc)
     * @see org.ascape.explorer.ControlActionView#scapeNowResumed()
     */
    public void scapeNowResumed() {
        super.scapeNowResumed();
        periodLabel.setForeground(Color.black);
        statusLabel.setForeground(Color.black);
        statusLabel.setText("Running");
    }

    /* (non-Javadoc)
     * @see org.ascape.explorer.ControlActionView#scapeNowStepping()
     */
    public void scapeNowStepping() {
        statusLabel.setText("Stepping");
        periodLabel.setForeground(Color.black);
        statusLabel.setForeground(Color.black);
    }

    /* (non-Javadoc)
     * @see org.ascape.explorer.ControlActionView#environmentNowScape()
     */
    public void environmentNowScape() {
        super.environmentNowScape();
    }

    /* (non-Javadoc)
     * @see org.ascape.explorer.ControlActionView#environmentNowNoScape()
     */
    public void environmentNowNoScape() {
        super.environmentNowNoScape();
        periodLabel.setForeground(Color.gray);
        statusLabel.setForeground(Color.gray);
        periodLabel.setText("");
        statusLabel.setText("");
//        frameList.repaint();
    }

    /* (non-Javadoc)
     * @see org.ascape.explorer.ControlActionView#changeInFullScreen()
     */
    public void changeInFullScreen() {
        super.changeInFullScreen();
        if (UserEnvironment.getDefaultEnvironment().isFullScreen()) {
            fullNormalWindowButton.setAction(getNormalWindowAction());
        } else {
            fullNormalWindowButton.setAction(getFullWindowAction());
        }
    }

    /* (non-Javadoc)
     * @see org.ascape.explorer.ControlActionView#recordingStopped()
     */
    public void recordingStopped() {
        super.recordingStopped();
        recordStartStopButton.setAction(getRecordStartAction());
        //recorder.getViewFrame()getViews()[0].setMovieRecorder(null);
    }

    /* (non-Javadoc)
     * @see org.ascape.explorer.ControlActionView#recordingCreated()
     */
    public void recordingCreated() {
        super.recordingCreated();
        recordStartStopButton.setAction(getRecordStopAction());
    }

    /**
     * Sets the frame selection.
     * 
     * @param o
     *            the new frame selection
     */
    public void setFrameSelection(Object o) {
        if ((frameList != null) && (o != null)) {
            if (frameList.getSelectedItem() != o) {
                frameList.setSelectedItem(o);
            }
            if ((recorder != null) && (recorder.getTarget() == null)) {
//We need to delegate to a view because thats where updates occur.
                recordedView = ((ViewFrameBridge) o).getViews()[0];
                recordedView.setMovieRecorder(recorder);
                recorder.setComponent(((ViewFrameBridge) o).getViewPanel());
                recorder.setTargetName(((ViewFrameBridge) o).getViews()[0].getName());
            }
        }
    }

    /**
     * Method called when the scape is closed.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void scapeClosing(ScapeEvent scapeEvent) {
    }

    /**
     * Sets the ignore frame selection.
     * 
     * @param ignore
     *            the new ignore frame selection
     */
    public void setIgnoreFrameSelection(boolean ignore) {
        ignoreFrameSelection = ignore;
    }

    /**
     * Gets the frame list.
     * 
     * @return the frame list
     */
    public JComboBox getFrameList() {
        return frameList;
    }

    /**
     * Gets the options bar.
     * 
     * @return the options bar
     */
    public JToolBar getOptionsBar() {
        return optionsBar;
    }

    /**
     * Gets the file bar.
     * 
     * @return the file bar
     */
    public JToolBar getFileBar() {
        return fileBar;
    }

    /**
     * Gets the run control bar.
     * 
     * @return the run control bar
     */
    public JToolBar getRunControlBar() {
        return runControlBar;
    }

}
