/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */


package org.ascape.movie;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.ascape.util.swing.NumberOnlyField;



/**
 * A Base class for recording views.
 *
 * @author Miles Parker
 * @version 3.0
 * @history 5/7/2002 made into class, moved stuff from ControlActionView, moved to ne package, other refactorings
 * @history 9/1/2000 first in
 * @since 1.9
 * todo return support for non-ionternal frame usage
 */
public abstract class MovieRecorder {

    /**
     * The movie file.
     */
    protected File file;

    /**
     * The target we are taking a movie of.
     */
    protected RecorderTarget target;

    /**
     * A recorder listenening for changes.
     */
    protected RecorderListener listener;

    /**
     * THe frame number currently being recorded
     */
    protected int recordFrameNum;

    private String targetName;

    /**
     * How many frames per second are we recording?
     */
    private int framesPerSecond = 12;

    private boolean waitingForSelection;

    private JInternalFrame recordFrame;

    private Container recordContainer;

    private Thread blinkThread;

    private JButton cancelOrStopButton;

    private JLabel statusMsg;

    private JPanel recordInternalPanel;

    private JComboBox qualityList;

    private JTextField framesField;

    /**
     * Called to create recording session and setup control dialog.
     */
    public MovieRecorder() {
        this(null);
    }

    /**
     * Called to create recording session and setup control dialog.
     */
    public MovieRecorder(JDesktopPane desktop) {
        recordFrameNum = 0;
        try {
            //OK, the (Frame) null is weird, but it stopped vc from complaining..
//            recordDialog = new JDialog(parent, "Record QuickTime Movie");
            recordFrame = new JInternalFrame();
            desktop.add(recordFrame, JLayeredPane.PALETTE_LAYER);
            waitingForSelection = true;
            recordContainer = recordFrame.getContentPane();
            recordContainer.setLayout(new BorderLayout());
            cancelOrStopButton = new JButton("Cancel") {
                /**
                 * 
                 */
                private static final long serialVersionUID = 2636768903600459051L;

                public Dimension getPreferredSize() {
                    return new Dimension(100, super.getPreferredSize().height);
                }
            };
            cancelOrStopButton.addActionListener(new AbstractAction() {
                /**
                 * 
                 */
                private static final long serialVersionUID = 8873376030622446171L;

                public void actionPerformed(ActionEvent e) {
                    cancelOrStop();
                }
            });
            recordContainer.setBackground(Color.black);
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout());
            JPanel buttonPanelContainer = new JPanel();
            buttonPanelContainer.setBackground(Color.black);
            buttonPanel.setBackground(Color.black);
            cancelOrStopButton.setBackground(Color.black);
            cancelOrStopButton.setForeground(Color.green);
            buttonPanelContainer.setLayout(new BorderLayout());
            buttonPanelContainer.add(buttonPanel, "East");
            recordContainer.add(BorderLayout.SOUTH, buttonPanelContainer);
            buttonPanel.add(cancelOrStopButton);
            recordInternalPanel = new JPanel();
            recordInternalPanel.setLayout(new GridLayout(3, 1));
            statusMsg = new JLabel("Select a window to record.");
            recordInternalPanel.add(statusMsg);
            JPanel qualityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            qualityPanel.setBackground(Color.black);
            JLabel qualityLabel = new JLabel("Quality:");
            qualityLabel.setBackground(Color.black);
            qualityLabel.setForeground(Color.green);
            qualityPanel.add(qualityLabel);
            qualityList = createQualityComboBox();
            qualityPanel.add(qualityList);
            recordInternalPanel.add(qualityPanel);
            JPanel framesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            framesPanel.setBackground(Color.black);
            JLabel framesLabel = new JLabel("Iterations Per Second:");
            framesLabel.setBackground(Color.black);
            framesLabel.setForeground(Color.green);
            framesPanel.add(framesLabel);
            framesField = new NumberOnlyField(Integer.toString(getFramesPerSecond()), 5);
            framesField.getDocument().addDocumentListener(new DocumentListener() {
                public void updated() {
                    try {
                        setFramesPerSecond(Integer.parseInt(framesField.getText()));
                    } catch (NumberFormatException e) {
                    }
                }

                public void changedUpdate(DocumentEvent e) {
                    updated();
                };
                public void insertUpdate(DocumentEvent e) {
                    updated();
                };
                public void removeUpdate(DocumentEvent e) {
                    updated();
                };
            });
            framesPanel.add(framesField);
            recordInternalPanel.add(framesPanel);
            recordInternalPanel.setBackground(Color.black);
            recordContainer.add(BorderLayout.CENTER, recordInternalPanel);
            recordInternalPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
            recordInternalPanel.setPreferredSize(new Dimension(300, 130));
            blinkThread = new Thread() {
                int greenness;

                boolean toGreen;

                public void run() {
                    while (waitingForSelection) {
                        if (toGreen) {
                            greenness += 8;
                            if (greenness > 255) {
                                greenness = 255;
                                toGreen = false;
                            }
                        } else {
                            greenness -= 8;
                            if (greenness < 0) {
                                greenness = 0;
                                toGreen = true;
                            }
                        }
                        statusMsg.setForeground(new Color(0, greenness, 0));
                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException ignore) {
                        }
                    }
                }
            };
            blinkThread.start();
            //todo investigate; the following call should not be neccesary, the code isn't clean just a stop gap
            int recordWidth = 300;
            int recordHeight = 200;
            recordFrame.setBounds((int) (desktop.getSize().getWidth() - recordWidth) / 2, (int) (desktop.getSize().getHeight() - recordHeight) / 2, recordWidth, recordHeight);
            recordFrame.setVisible(true);
        } catch (UnsatisfiedLinkError e) {
            JOptionPane.showMessageDialog(target.getComponent(), "To use this feature, you need to install Quicktime for Java.\nThe installer is available at http://www.apple.com/quicktime/qtjava.", "Quicktime Needed", JOptionPane.INFORMATION_MESSAGE);
        } catch (NoClassDefFoundError e) {
            JOptionPane.showMessageDialog(target.getComponent(), "To use this feature, you need to install Quicktime for Java.\nThe installer is available at http://www.apple.com/quicktime/qtjava.", "Quicktime Needed", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Called to stop or cancel recording.
     */
    public void cancelOrStop() {
        //target.setRecorder(null);
        close();
        if (listener != null) { // if the recording is canceled before it's started (e.g. before a window is selected)
            listener.recordingStopped();
        }

        if (recordFrame != null) {
            recordFrame.dispose();
            recordFrame = null;
        }
        if (blinkThread != null) {
            blinkThread = null;
        }
        waitingForSelection = false;
    }

    /**
     * Called to start recording.
     */
    protected void requestFile() {
        if (blinkThread != null) {
            blinkThread = null;
        }
        waitingForSelection = false;
        statusMsg.setForeground(Color.green);
        statusMsg.setText("Save Movie As...");

        JFileChooser fileChooser = new JFileChooser();
        //Will use parent file
        if (fileChooser.showSaveDialog(target.getComponent()) == JFileChooser.APPROVE_OPTION) {
            setFile(fileChooser.getSelectedFile());
            cancelOrStopButton.setText("Stop");
            start();
            statusUpdate();
            recordFrame.toFront();
            qualityList.setEnabled(false);
            framesField.setEnabled(false);
        } else {
            cancelOrStop();
        }
    }

    /**
     * Creates a list of qualities to select. In the basic version, there are none.
     */
    protected JComboBox createQualityComboBox() {
        JComboBox qualityComboBox = new JComboBox();
        return qualityComboBox;
    }

    /**
     * Called the listener or target to record the next frame.
     */
    public void recordFrame() {
        statusUpdate();
    }

    /**
     * Creates the movie, preparing it for recording of frames.
     */
    public void start() {
        recordFrame();
        listener.recordingStarted();
    };

    /**
     * Closes the movie and the file it is being saved to.
     */
    public abstract void close();

    /**
     * Updates the status display.
     */
    public void statusUpdate() {
        if ((file != null) && (target != null)) {
            statusMsg.setText(getStatusMsg());
        }
    }

    /**
     * Returns the number of frames to be displayed per second.
     */
    public int getFramesPerSecond() {
        return framesPerSecond;
    }

    /**
     * Sets the number of frames to be displayed per second.
     */
    public void setFramesPerSecond(int framesPerSecond) {
        this.framesPerSecond = framesPerSecond;
    }

    /**
     * Sets the file to save the movie to.
     */
    public void setFile(File file) {
    }

    /**
     * Retuns the target we are recording.
     */
    public RecorderTarget getTarget() {
        return target;
    }

    /**
     * Sets the target to record.
     */
    public void setTarget(RecorderTarget recorderTarget) {
        this.target = recorderTarget;
        if ((listener != null) && (target != null)) {
            requestFile();
        }
    }

    /**
     * Sets the component to record.
     */
    public abstract void setComponent(Component component);

    /**
     * Returns the listener responsible for this recording.
     */
    public RecorderListener getListener() {
        return listener;
    }

    /**
     * Sets the listener responsible for this recording.
     * <strong>Warning: for now, does not support multiple listeners.</strong>
     */
    public void addListener(RecorderListener listener) {
        this.listener = listener;
        if (listener != null) {
            listener.recordingCreated();
        }
    }

    /**
     * Returns a message indicating target and frame number.
     */
    public String getStatusMsg() {
        return "Recording " + targetName + ": " + recordFrameNum;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }
}
