/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.view.vis.control;


import java.awt.Component;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import org.ascape.model.Scape;
import org.ascape.model.event.ControlEvent;
import org.ascape.model.event.ScapeEvent;
import org.ascape.movie.MovieRecorder;
import org.ascape.movie.RecorderListener;
import org.ascape.movie.qt.QuickTimeRecorder;
import org.ascape.runtime.swing.DesktopEnvironment;
import org.ascape.runtime.swing.SearchView;
import org.ascape.util.swing.AscapeGUIUtil;
import org.ascape.view.custom.BaseCustomizer;
import org.ascape.view.vis.ChartView;
import org.ascape.view.vis.ComponentView;
import org.ascape.view.vis.ScapeTransitionPanelView;

/**
 * A class providing control a model. Provides start, stop, restart, pause,
 * resume, quit, info, status, and new chart acitons. Control views can be
 * attached to any scape, and controls will typically affect the model (entire
 * collection of scapes) as a whole. Use SimpleControlView if Swing is not
 * available or if simple buttons are preferred to image buttons with tool-tips.
 * Requires Swing.
 * 
 * @author Miles Parker
 * @version 3.0
 * @history 3.0 9/1/02 Many general improvments
 * @history 2.9 5/9/02 updated for new movie refactorings
 * @history 2.9 3/1/02 Refactored out of control bar view to provide a basis for
 *          other kinds of control UI
 * @since 2.9
 */
public class ControlActionView extends ScapeTransitionPanelView implements RecorderListener {

    /**
     * Scape control for re-opening the current scape.
     */
    private Action reopenAction;
	
    /**
     * Scape control for opening a new scape.
     */
    private Action openAction;

    /**
     * Scape control for closing an existing scape.
     */
    private Action closeAction;

    /**
     * Scape control for opening a saved scape run.
     */
    private Action openSavedAction;

    /**
     * Scape control for saving a scape.
     */
    private Action saveAction;

    /**
     * Scape control for starting and restarting a scape.
     */
    private Action startRestartAction;

    /**
     * Scape control for stoping a scape.
     */
    private Action stopAction;

    /**
     * Scape control for pausing and resuming a scape.
     */
    private Action pauseResumeAction;

    /**
     * Flag to determine if the model is paused. Used by pauseResumeAction to
     * know if it should pause or resume the model.
     */
    boolean modelIsPaused = false;

    /**
     * Flag to determine if the model has been started. Used by
     * startRestartAction to know if it should start or restart the model.
     */
    boolean startButtonIsRestart = false;

    /**
     * Scape control for stepping a scape one iteration.
     */
    private Action stepAction;

    /**
     * Scape control for examining scape settings.
     */
    private Action settingsAction;

    /**
     * Scape control for searching through scape.
     */
    private Action searchAction;

    /**
     * Scape control for getting information about a scape.
     */
    private Action infoAction;

    /**
     * Scape control for quiting the model.
     */
    private Action quitAction;

    /**
     * Scape control for creating a new time series graph.
     */
    private Action addTSAction;

    /**
     * Scape control for creating a new histogram.
     */
    private Action addHistAction;

    /**
     * Scape control for creating a new pie chart.
     */
    private Action addPieAction;

    /**
     * Scape control for capturing the contents of the desktop, containing the
     * Ascape views and customizers (i.e. the scrollable area within the MDI
     * view).
     */
    private Action captureDeskAction;

    /**
     * Scape control for starting a scape recording session.
     */
    private Action recordStartAction;

    /**
     * Scape control for stopping a scape recording session.
     */
    private Action recordStopAction;

    /**
     * Scape control for making the eser frame a full window.
     */
    Action fullWindowAction;

    /**
     * Scape control for making the eser frame a standard window.
     */
    Action normalWindowAction;

    /**
     * The recorder.
     */
    protected MovieRecorder recorder;

    //todo, fix so this is not static
    /**
     * The recorded view.
     */
    static protected ComponentView recordedView;

    /**
     * The recorder caused pause.
     */
    private boolean recorderCausedPause;

    /**
     * The last customizer location.
     */
    private Point lastCustomizerLocation;


    /**
     * Constructs the control view, creating and laying out its components.
     */
    public ControlActionView() {
        this("Control Action View");
    }

    /**
     * Constructs the control view, creating and laying out its components.
     * 
     * @param name
     *            the name
     */
    public ControlActionView(String name) {
        super(name);
        startRestartAction = new AbstractAction() {
            /**
             * 
             */
            private static final long serialVersionUID = 5557060914978519081L;

            public void actionPerformed(ActionEvent e) {
                if (!getScape().isRunning()) {
                    getScape().respondControl(new ControlEvent(this, ControlEvent.REQUEST_START));
                } else {
                    getScape().respondControl(new ControlEvent(this, ControlEvent.REQUEST_RESTART));
                }
            }
        };
        startRestartAction.putValue(Action.NAME, "Start/Restart");
        startRestartAction.putValue(Action.SHORT_DESCRIPTION, "Start/Restart Model");
        startRestartAction.putValue(Action.SMALL_ICON, DesktopEnvironment.getIcon("ClockGo"));

        pauseResumeAction = new AbstractAction() {
            /**
             * 
             */
            private static final long serialVersionUID = 1092243053425962189L;

            public void actionPerformed(ActionEvent e) {
                if (modelIsPaused) {
                    getScape().respondControl(new ControlEvent(this, ControlEvent.REQUEST_RESUME));
                    modelIsPaused = false;
                    stepAction.setEnabled(false);
                    //                    stepAction.isEnabled();
                } else {
                    getScape().respondControl(new ControlEvent(this, ControlEvent.REQUEST_PAUSE));
                    modelIsPaused = true;
                    stepAction.setEnabled(true);
                    //                    stepAction.isEnabled();
                }
            }
        };
        pauseResumeAction.putValue(Action.NAME, "Pause/Resume");
        pauseResumeAction.putValue(Action.SHORT_DESCRIPTION, "Pause/Resume Model");
        pauseResumeAction.putValue(Action.SMALL_ICON, DesktopEnvironment.getIcon("RedFlag"));

        stepAction = new AbstractAction() {
            /**
             * 
             */
            private static final long serialVersionUID = -4251437686821330596L;

            public void actionPerformed(ActionEvent e) {
                scapeNowStepping();
                //Force change back to pause on next update
                lastScapeAppearsPaused = false;
                getScape().respondControl(new ControlEvent(this, ControlEvent.REQUEST_STEP));
            }
        };
        stepAction.putValue(Action.NAME, "Step");
        stepAction.putValue(Action.SHORT_DESCRIPTION, "Step One Iteration");
        stepAction.putValue(Action.SMALL_ICON, DesktopEnvironment.getIcon("Enter"));
        stepAction.setEnabled(false);
        //        System.err.println("ControlActionView - setting stepAction dis-enabled");
        //        stepAction.isEnabled();

        stopAction = new AbstractAction() {
            /**
             * 
             */
            private static final long serialVersionUID = -4509621518176505478L;

            public void actionPerformed(ActionEvent e) {
                getScape().respondControl(new ControlEvent(this, ControlEvent.REQUEST_STOP));
            }
        };
        stopAction.putValue(Action.NAME, "Stop");
        stopAction.putValue(Action.SHORT_DESCRIPTION, "Stop Model");
        stopAction.putValue(Action.SMALL_ICON, DesktopEnvironment.getIcon("ClockStop"));


        openAction = new AbstractAction() {
            /**
             * 
             */
            private static final long serialVersionUID = 4510642692479060522L;

            public void actionPerformed(ActionEvent e) {
                if (getScape() != null) {
                    getScape().respondControl(new ControlEvent(this, ControlEvent.REQUEST_OPEN));
                } else {
                	AscapeGUIUtil.getDesktopEnvironment().getScape().getRunner().openChoose();
                }
            }
        };
        openAction.putValue(Action.NAME, "Open Model...");
        openAction.putValue(Action.SHORT_DESCRIPTION, "Open New Model");
        openAction.putValue(Action.SMALL_ICON, DesktopEnvironment.getIcon("OpenArrow"));

        reopenAction = new AbstractAction() {

            /**
			 * 
			 */
			private static final long serialVersionUID = -79740733780101978L;

			public void actionPerformed(ActionEvent e) {
				// reload the current model
                final Scape scape = AscapeGUIUtil.getDesktopEnvironment().getScape();
                
				// stop current
                final String modelName = scape.getClass().getName();
                
                
                // Need to do this in a new thread otherwise we'll get an exception
                new Thread(new Runnable() {
                    public void run() {
        				scape.getRunner().close();
        				scape.getRunner().openInstance(modelName);
                    }
                }).start();
            }
        };
        reopenAction.putValue(Action.NAME, "Reload Model");
        reopenAction.putValue(Action.SHORT_DESCRIPTION, "Reload Model");

        
        openSavedAction = new AbstractAction() {
            /**
             * 
             */
            private static final long serialVersionUID = -79740733780101978L;

            public void actionPerformed(ActionEvent e) {
                if (getScape() != null) {
                    getScape().respondControl(new ControlEvent(this, ControlEvent.REQUEST_OPEN_SAVED));
                } else {
                	AscapeGUIUtil.getDesktopEnvironment().getScape().getRunner().openSavedChoose();
                }
            }
        };
        openSavedAction.putValue(Action.NAME, "Open Model Run...");
        openSavedAction.putValue(Action.SHORT_DESCRIPTION, "Open Saved Model Run");
        openSavedAction.putValue(Action.SMALL_ICON, DesktopEnvironment.getIcon("OpenDoc"));

        closeAction = new AbstractAction() {
            /**
             * 
             */
            private static final long serialVersionUID = -5421754824561277042L;

            public void actionPerformed(ActionEvent e) {
                if (scape != null) {
                    getScape().respondControl(new ControlEvent(this, ControlEvent.REQUEST_CLOSE));
                }
            }
        };
        closeAction.putValue(Action.NAME, "Close Model");
        closeAction.putValue(Action.SHORT_DESCRIPTION, "Close Model");
        closeAction.putValue(Action.SMALL_ICON, DesktopEnvironment.getIcon("DeleteSheet"));

        saveAction = new AbstractAction() {
            /**
             * 
             */
            private static final long serialVersionUID = -473518649914694666L;

            public void actionPerformed(ActionEvent e) {
                getScape().respondControl(new ControlEvent(this, ControlEvent.REQUEST_SAVE));
            }
        };
        saveAction.putValue(Action.NAME, "Save Run...");
        saveAction.putValue(Action.SHORT_DESCRIPTION, "Save Model Run");
        saveAction.putValue(Action.SMALL_ICON, DesktopEnvironment.getIcon("Save"));

        quitAction = new AbstractAction() {
            /**
             * 
             */
            private static final long serialVersionUID = -3147220495521509564L;

            public void actionPerformed(ActionEvent e) {
            	//NB: after the simulation has ended, this will have been
            	//removed from the scape so we can't use getScape()
                //getScape().respondControl(new ControlEvent(this, ControlEvent.REQUEST_QUIT));
                //getScape().getRunner().getEnvironment().quit();
              
/*            	Scape scape = AscapeGUIUtil.getDesktopEnvironment().getScape(); 
                scape.respondControl(new ControlEvent(this, ControlEvent.REQUEST_QUIT));
*/                
				// execute quit on new thread instead of AWT
				new Thread("Quit") {
					public void run() {
						AscapeGUIUtil.getDesktopEnvironment().quit();
					}
				}.start();
            }
        };
        quitAction.putValue(Action.NAME, "Quit");
        quitAction.putValue(Action.SHORT_DESCRIPTION, "Quit Ascape");
        quitAction.putValue(Action.SMALL_ICON, DesktopEnvironment.getIcon("Delete"));

        settingsAction = new AbstractAction() {
            /**
             * 
             */
            private static final long serialVersionUID = -2962923969270143073L;

            public void actionPerformed(ActionEvent e) {
                if (!getScape().getScapeListeners().contains(getScape().getUIEnvironment().getCustomizer())) {
                    getScape().addView(getScape().getUIEnvironment().getCustomizer());
                    if (lastCustomizerLocation != null) {
                        final Frame frame = JOptionPane.getFrameForComponent((Component) getScape().getUIEnvironment().getCustomizer());
                        frame.setLocation(lastCustomizerLocation);
                        frame.addWindowListener(new WindowAdapter() {
                            public void windowClosed(WindowEvent e) {
                                lastCustomizerLocation = frame.getLocation();
                            }
                        });
                    }
                    //                    getScape().addView(new SearchView()); // not sure why this is here
                }
                ((BaseCustomizer) getScape().getUIEnvironment().getCustomizer()).selected();
            }
        };
        settingsAction.putValue(Action.NAME, "Settings");
        settingsAction.putValue(Action.SHORT_DESCRIPTION, "Display Model Settings");
        settingsAction.putValue(Action.SMALL_ICON, DesktopEnvironment.getIcon("World"));

        searchAction = new AbstractAction() {
            /**
             * 
             */
            private static final long serialVersionUID = 2248339728714978093L;

            public void actionPerformed(ActionEvent e) {
                getScape().addView(new SearchView());
            }
        };
        searchAction.putValue(Action.NAME, "Search");
        searchAction.putValue(Action.SHORT_DESCRIPTION, "Search for Scape Members");
        searchAction.putValue(Action.SMALL_ICON, DesktopEnvironment.getIcon("Binocular"));

        captureDeskAction = new AbstractAction() {
            /**
             * 
             */
            private static final long serialVersionUID = 608361368433987948L;

            public void actionPerformed(ActionEvent actionEvent) {
                Component desk = DesktopEnvironment.getDefaultDesktop().getUserFrame().getDesk();
                int w = desk.getWidth(), h = desk.getHeight();
                BufferedImage image = new BufferedImage(w, h,
                                                        BufferedImage.TYPE_INT_RGB);
                Graphics2D g2 = image.createGraphics();
                desk.paint(g2);
                g2.dispose();
                try {
                    ImageIO.write(image, "png", new File("ascape.png"));
                } catch (IOException e) {
                    System.err.println(e);
                }
            }
        };
        captureDeskAction.putValue(Action.NAME, "Capture Image");
        captureDeskAction.putValue(Action.SHORT_DESCRIPTION, "Capture Image to ascape.png");
        captureDeskAction.putValue(Action.SMALL_ICON, DesktopEnvironment.getIcon("CameraFlash"));

        recordStartAction = new AbstractAction() {
            /**
             * 
             */
            private static final long serialVersionUID = 1505297333726705163L;

            public void actionPerformed(ActionEvent e) {
                if (recorder == null) {
                    recorder = new QuickTimeRecorder(DesktopEnvironment.getDefaultDesktop().getUserFrame().getDesk());
                    recorder.addListener(ControlActionView.this);
                }
            }
        };
        recordStartAction.putValue(Action.NAME, "Start Recording");
        recordStartAction.putValue(Action.SHORT_DESCRIPTION, "Start Recording");
        recordStartAction.putValue(Action.SMALL_ICON, DesktopEnvironment.getIcon("Camera"));

        recordStopAction = new AbstractAction() {
            /**
             * 
             */
            private static final long serialVersionUID = 1114129941923489755L;

            public void actionPerformed(ActionEvent e) {
                recordingCancelStop();
            }
        };
        recordStopAction.putValue(Action.NAME, "Stop Recording");
        recordStopAction.putValue(Action.SHORT_DESCRIPTION, "Stop Recording");
        recordStopAction.putValue(Action.SMALL_ICON, DesktopEnvironment.getIcon("CameraFlash"));

        infoAction = new AbstractAction() {
            /**
             * 
             */
            private static final long serialVersionUID = -8431813556281975870L;

            public void actionPerformed(ActionEvent e) {
                DesktopEnvironment.displayAboutDialog(scape);
            }
        };
        infoAction.putValue(Action.NAME, "About");
        infoAction.putValue(Action.SHORT_DESCRIPTION, "About Ascape and this Model");
        infoAction.putValue(Action.SMALL_ICON, DesktopEnvironment.getIcon("Inform"));

        addTSAction = new AbstractAction() {
            /**
             * 
             */
            private static final long serialVersionUID = -4300893818212408338L;

            public void actionPerformed(ActionEvent e) {
                newChart(ChartView.TIME_SERIES);
            }
        };
        addTSAction.putValue(Action.NAME, "New TimeSeries");
        addTSAction.putValue(Action.SHORT_DESCRIPTION, "Create New Time Series");
        addTSAction.putValue(Action.SMALL_ICON, DesktopEnvironment.getIcon("LineGraph"));

        addHistAction = new AbstractAction() {
            /**
             * 
             */
            private static final long serialVersionUID = 6063864949471473037L;

            public void actionPerformed(ActionEvent e) {
                newChart(ChartView.HISTOGRAM);
            }
        };
        addHistAction.putValue(Action.NAME, "New Histogram");
        addHistAction.putValue(Action.SHORT_DESCRIPTION, "Create New Histogram");
        addHistAction.putValue(Action.SMALL_ICON, DesktopEnvironment.getIcon("BarGraph"));

        addPieAction = new AbstractAction() {
            /**
             * 
             */
            private static final long serialVersionUID = 8425068395400505182L;

            public void actionPerformed(ActionEvent e) {
                newChart(ChartView.PIE);
            }
        };
        addPieAction.putValue(Action.NAME, "New PieChart");
        addPieAction.putValue(Action.SHORT_DESCRIPTION, "Create New PieChart");
        addPieAction.putValue(Action.SMALL_ICON, DesktopEnvironment.getIcon("PieGraph"));

        fullWindowAction = new AbstractAction() {
            /**
             * 
             */
            private static final long serialVersionUID = 1361316239693434267L;

            public void actionPerformed(ActionEvent e) {
                DesktopEnvironment.getDefaultDesktop().setFullScreen(true);
            }
        };
        if (DesktopEnvironment.getDefaultDesktop() != null) {
            fullWindowAction.putValue(Action.NAME, "Full Screen");
            fullWindowAction.putValue(Action.SHORT_DESCRIPTION, "Display Ascape in Full Screen");
            fullWindowAction.putValue(Action.SMALL_ICON, DesktopEnvironment.getIcon("Computer"));

            normalWindowAction = new AbstractAction() {
                /**
                 * 
                 */
                private static final long serialVersionUID = -4242913283035409066L;

                public void actionPerformed(ActionEvent e) {
                    DesktopEnvironment.getDefaultDesktop().setFullScreen(false);
                }
            };
            normalWindowAction.putValue(Action.NAME, "Normal Screen");
            normalWindowAction.putValue(Action.SHORT_DESCRIPTION, "Display Ascape in Normal Window");
            normalWindowAction.putValue(Action.SMALL_ICON, DesktopEnvironment.getIcon("Frame"));
        }
    }

    /**
     * Method called once a model is deserialized.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void scapeDeserialized(ScapeEvent scapeEvent) {
        super.scapeDeserialized(scapeEvent);    //To change body of overridden methods use File | Settings | File Templates.
        if (getScape().isPaused()) {
            modelIsPaused = true;
        } else {
            modelIsPaused = false;
        }
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.PanelView#scapeStarted(org.ascape.model.event.ScapeEvent)
     */
    public void scapeStarted(ScapeEvent scapeEvent) {
        super.scapeStarted(scapeEvent);    //To change body of overridden methods use File | Settings | File Templates.
        modelIsPaused = false;
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.ScapeTransitionView#scapeNowRunning()
     */
    public void scapeNowRunning() {
        startRestartAction.setEnabled(true);
        stopAction.setEnabled(true);
        stepAction.setEnabled(false);
        //        System.err.println("ControlActionView.scapeNowRunning - setting it dis-enabled");
        //        stepAction.isEnabled();
        pauseResumeAction.setEnabled(true);
        saveAction.setEnabled(getScape().isSerializable());
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.ScapeTransitionView#scapeNowStopped()
     */
    public void scapeNowStopped() {
        startRestartAction.setEnabled(true);
        stopAction.setEnabled(false);
        pauseResumeAction.setEnabled(false);
        stepAction.setEnabled(false);
        //        System.err.println("ControlActionView.scapeNowStopped - setting it to false");
        //        stepAction.isEnabled();
        saveAction.setEnabled(false);
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.ScapeTransitionView#scapeNowPaused()
     */
    public void scapeNowPaused() {
        stepAction.setEnabled(true);
        //        System.err.println("ControlActionView.scapeNowPaused - setting it to true");
        //        stepAction.isEnabled();
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.ScapeTransitionView#scapeNowResumed()
     */
    public void scapeNowResumed() {
        stepAction.setEnabled(false);
        stepAction.isEnabled();
    }

    /**
     * Scape now stepping.
     */
    public void scapeNowStepping() {
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.ScapeTransitionView#scapeNowSteppable()
     */
    public void scapeNowSteppable() {
        stepAction.setEnabled(true);
        //        System.err.println("ControlActionView.scapeNowSteppable - setting it true");
        //        stepAction.isEnabled();
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.ScapeTransitionView#environmentNowScape()
     */
    public void environmentNowScape() {
        //Scape control, etc.. will be handled by approrpriate methods
        openAction.setEnabled(true);
        closeAction.setEnabled(true);
        openSavedAction.setEnabled(true);
        saveAction.setEnabled(true);
        startRestartAction.setEnabled(true);
        settingsAction.setEnabled(true);
        infoAction.setEnabled(true);
        quitAction.setEnabled(true);
        addTSAction.setEnabled(true);
        addHistAction.setEnabled(true);
        addPieAction.setEnabled(true);
        searchAction.setEnabled(true);
        captureDeskAction.setEnabled(true);
        recordStartAction.setEnabled(true);
        recordStopAction.setEnabled(false);
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.ScapeTransitionView#environmentNowNoScape()
     */
    public void environmentNowNoScape() {
        openAction.setEnabled(true);
        closeAction.setEnabled(false);
        openSavedAction.setEnabled(true);
        saveAction.setEnabled(false);
        startRestartAction.setEnabled(false);
        stopAction.setEnabled(false);
        pauseResumeAction.setEnabled(false);
        stepAction.setEnabled(false);
        settingsAction.setEnabled(false);
        infoAction.setEnabled(false);
        quitAction.setEnabled(true);
        addTSAction.setEnabled(false);
        addHistAction.setEnabled(false);
        addPieAction.setEnabled(false);
        searchAction.setEnabled(false);
        captureDeskAction.setEnabled(false);
        recordStartAction.setEnabled(false);
        recordStopAction.setEnabled(false);
    }

    /*
     * A factory method to produce charts from their action buttons
     */
    /**
     * New chart.
     * 
     * @param chartType
     *            the chart type
     */
    protected void newChart(int chartType) {
        ChartView chart = new ChartView(chartType);
        getScape().addView(chart);
        chart.displayCustomizer();
    }

    /* (non-Javadoc)
     * @see org.movie.RecorderListener#recordingStopped()
     */
    public void recordingStopped() {
        getRecordStartAction().setEnabled(true);
        getRecordStopAction().setEnabled(false);
        if (recorderCausedPause && getScape().isPaused()) {
            getScape().respondControl(new ControlEvent(this, ControlEvent.REQUEST_RESUME));
        }
        if (recordedView != null) {
            recordedView.setMovieRecorder(null);
        }
        recorder = null;
    }

    /* (non-Javadoc)
     * @see org.movie.RecorderListener#recordingCreated()
     */
    public void recordingCreated() {
        getRecordStartAction().setEnabled(false);
        getRecordStopAction().setEnabled(true);
        if (!getScape().isPaused()) {
            getScape().respondControl(new ControlEvent(this, ControlEvent.REQUEST_PAUSE));
            recorderCausedPause = true;
        }
    }

    /* (non-Javadoc)
     * @see org.movie.RecorderListener#recordingStarted()
     */
    public void recordingStarted() {
        if (getScape().isPaused()) {
            getScape().respondControl(new ControlEvent(this, ControlEvent.REQUEST_RESUME));
        }
    }

    /**
     * Called to stop or cancel recording.
     */
    protected void recordingCancelStop() {
        recorder.cancelOrStop();
        recordingStopped();
        recorder = null;
    }

    /**
     * Change in full screen.
     */
    public void changeInFullScreen() {
        if (DesktopEnvironment.getDefaultDesktop().isFullScreen()) {
            fullWindowAction.setEnabled(false);
            normalWindowAction.setEnabled(true);
        } else {
            fullWindowAction.setEnabled(true);
            normalWindowAction.setEnabled(false);
        }
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
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the add hist action.
     * 
     * @return the add hist action
     */
    public Action getAddHistAction() {
        return addHistAction;
    }

    /**
     * Gets the add pie action.
     * 
     * @return the add pie action
     */
    public Action getAddPieAction() {
        return addPieAction;
    }

    /**
     * Gets the add TS action.
     * 
     * @return the add TS action
     */
    public Action getAddTSAction() {
        return addTSAction;
    }

    /**
     * Gets the info action.
     * 
     * @return the info action
     */
    public Action getInfoAction() {
        return infoAction;
    }

    /**
     * Gets the close action.
     * 
     * @return the close action
     */
    public Action getCloseAction() {
        return closeAction;
    }

    /**
     * Gets the open action.
     * 
     * @return the open action
     */
    public Action getOpenAction() {
        return openAction;
    }

    /**
     * Gets the reopen action.
     * 
     * @return the reopen action
     */
    public Action getReopenAction() {
        return reopenAction;
    }

    
    /**
     * Gets the open saved action.
     * 
     * @return the open saved action
     */
    public Action getOpenSavedAction() {
        return openSavedAction;
    }

    /**
     * Gets the pause resume action.
     * 
     * @return the pause resume action
     */
    public Action getPauseResumeAction() {
        return pauseResumeAction;
    }

    /**
     * Gets the quit action.
     * 
     * @return the quit action
     */
    public Action getQuitAction() {
        return quitAction;
    }

    /**
     * Gets the capture desk action.
     * 
     * @return the capture desk action
     */
    public Action getCaptureDeskAction() {
        return captureDeskAction;
    }

    /**
     * Gets the record start action.
     * 
     * @return the record start action
     */
    public Action getRecordStartAction() {
        return recordStartAction;
    }

    /**
     * Gets the record stop action.
     * 
     * @return the record stop action
     */
    public Action getRecordStopAction() {
        return recordStopAction;
    }

    /**
     * Gets the save action.
     * 
     * @return the save action
     */
    public Action getSaveAction() {
        return saveAction;
    }

    /**
     * Gets the settings action.
     * 
     * @return the settings action
     */
    public Action getSettingsAction() {
        return settingsAction;
    }

    /**
     * Gets the search action.
     * 
     * @return the search action
     */
    public Action getSearchAction() {
        return searchAction;
    }

    /**
     * Gets the start restart action.
     * 
     * @return the start restart action
     */
    public Action getStartRestartAction() {
        return startRestartAction;
    }

    /**
     * Gets the step action.
     * 
     * @return the step action
     */
    public Action getStepAction() {
        return stepAction;
    }

    /**
     * Gets the stop action.
     * 
     * @return the stop action
     */
    public Action getStopAction() {
        return stopAction;
    }

    /**
     * Gets the full window action.
     * 
     * @return the full window action
     */
    public Action getFullWindowAction() {
        return fullWindowAction;
    }

    /**
     * Gets the normal window action.
     * 
     * @return the normal window action
     */
    public Action getNormalWindowAction() {
        return normalWindowAction;
    }

    /**
     * Returns a short description of this view. Sames as name unless
     * overridden.
     * 
     * @return the string
     */
    public String toString() {
        return name;
    }


    /* (non-Javadoc)
     * @see org.ascape.view.vis.ScapeTransitionView#scapeNotification(org.ascape.model.event.ScapeEvent)
     */
    public void scapeNotification(ScapeEvent scapeEvent) {
        super.scapeNotification(scapeEvent);    //To change body of overridden methods use File | Settings | File Templates.
        if (scapeEvent.getID() == ScapeEvent.REPORT_DESERIALIZED) {
            modelIsPaused = true;
        }
    }

    //    private void writeObject(java.io.ObjectOutputStream out)
    //         throws IOException {
    //      if (SwingUtilities.isEventDispatchThread()) {
    //        // This is all that is necessary if we are already in
    //        // the event dispatch thread, e.g. a user clicked a
    //        // button which caused the object to be serialized
    //        out.defaultWriteObject();
    //      } else {
    //          System.out.println(this+": cannot write because not in Event Dispatch Thread!");
    //      }
    //    }
}
