/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.view.custom;

import java.util.TooManyListenersException;

import javax.swing.JButton;

import org.ascape.model.event.ScapeEvent;


/**
 * A panel for making changes to a model's settings.
 * 
 * @author Miles Parker
 * @version 1.5
 * @history 1.2 7/9/99 redesigned all customizers, various updates to reflect
 *          new base customizer
 * @history 1.1.1 first in
 * @since 1.1.1
 */
public abstract class ModelCustomizerSwing extends ModelCustomizer {

    /**
     * The button for dismissing this dialog.
     */
    protected JButton okButton;

    /**
     * Constructs the dialog.
     */
    public ModelCustomizerSwing() {
        super();
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
        setObject(scapeEvent);
    }

    /**
     * Returns true if the customizer is 'live' that is, changes to controls are
     * reflected immeadiatly in the model, and false if changes are meant to
     * take place only when the OK or Apply buttons are selected. False by
     * default.
     * 
     * @return true, if is live
     */
    public boolean isLive() {
        return true;
    }

    /**
     * Contructs the memebers of the customizer, a main panel to hold the
     * customizer components, and ok, apply and cancel buttons to manage the
     * interaction of the customizer with its target scape.
     */
    public void build() {
        super.build();

        /*
         * JButton Panel
         */
        if (!isLive()) {
            buttonPanel.removeAll();
            //okButton = new JButton("Done", new ImageIcon(Scape.getAscapeHome() + getScape().getBasePath() + "lib/images/Check.gif"));
            //Let's make consistent w/ Swing dialogs instead
            //            okButton = new JButton("Done", UserEnvironment.getIcon("Check")) {
            //                // I override JButton's createActionPropertyChangeListener
            //                // to avoid the creation of a AbstractButton$ButtonActionPropertyChangeListener,
            //                // which is not serializable!
            //                protected PropertyChangeListener createActionPropertyChangeListener(Action a) {
            //                    return null;
            //                }
            //            };
            //            buttonPanel.add(okButton);
            //            okButton.addActionListener(new ActionListener() {
            //                public void actionPerformed(ActionEvent e) {
            //                    assignSettings();
            //                    getViewFrame().dispose();
            //                    //We need to update once because the current update will be missed
            //		            //notifyScapeUpdated();
            //                }
            //            });
        }
        if (getViewFrame() != null) {
            getViewFrame().setTitle("Model Settings");
        }
    }

    /**
     * Retrieve the settings from the model, and update the panel's components
     * to reflect them.
     */
    protected void retrieveSettings() {
    };

    /**
     * Assign the changes made in the panel's components back to the model.
     */
    protected void assignSettings() {
    };
}
