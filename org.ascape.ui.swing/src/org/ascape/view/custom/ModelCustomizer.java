/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.view.custom;

import java.awt.Button;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TooManyListenersException;

import org.ascape.model.event.ScapeEvent;


/**
 * A panel for making changes to a model's settings.
 * 
 * @author Miles Parker
 * @version 1.2
 * @history 1.2 7/9/99 redesigned all customizers, various updates to reflect
 *          new base customizer
 * @history 1.1.1 first in
 * @since 1.1.1
 */
public abstract class ModelCustomizer extends BaseCustomizer {

    /**
     * The button for dismissing this dialog.
     */
    private Button okButton;

    /**
     * The button for dismissing this dialog.
     */
    private Button cancelButton;

    /**
     * The button for dismissing this dialog.
     */
    private Button applyButton;

    /**
     * Constructs the dialog.
     */
    public ModelCustomizer() {
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
     * On start, build accessor panel.
     * 
     * @return true, if is live
     */
    /*public void onStart() {
        build();
    }*/

    /**
     * Returns true if the customizer is 'live' that is, changes to controls are reflected
     * immeadiatly in the model, and false if changes are meant to take place only when the
     * OK or Apply buttons are selected.
     * False by default.
     */
    public boolean isLive() {
        return false;
    }

    /**
     * Contructs the memebers of the customizer, a main panel to hold the
     * customizer components, and ok, apply and cancel buttons to manage the
     * interaction of the customizer with its target scape.
     */
    public void build() {
        super.build();

        /*
        * Button Panel
        */
        if (!isLive()) {
            //okButton = new JButton("Done", new ImageIcon(Scape.getAscapeHome() + getScape().getBasePath() + "lib/images/Check.gif"));
            //Let's make consistent w/ Swing dialogs instead
            okButton = new Button("OK") {
                /**
                 * 
                 */
                private static final long serialVersionUID = -4941867529746520446L;

                public Dimension getPreferredSize() {
                    return new Dimension(60, super.getPreferredSize().height);
                }
            };
            buttonPanel.add(okButton);
            okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    assignSettings();
                    getViewFrame().setVisible(false);
                    //We need to update once because the current update will be missed
                    notifyScapeUpdated();
                }
            });
            cancelButton = new Button("Cancel") {
                /**
                 * 
                 */
                private static final long serialVersionUID = -4012850953280950300L;

                public Dimension getPreferredSize() {
                    return new Dimension(60, super.getPreferredSize().height);
                }
            };
            buttonPanel.add(cancelButton);
            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    getViewFrame().setVisible(false);
                    //We need to update once because the current update will be missed
                    notifyScapeUpdated();
                }
            });
            applyButton = new Button("Apply") {
                /**
                 * 
                 */
                private static final long serialVersionUID = 891536700468630158L;

                public Dimension getPreferredSize() {
                    return new Dimension(60, super.getPreferredSize().height);
                }
            };
            buttonPanel.add(applyButton);
            applyButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    assignSettings();
                }
            });
        }
        getViewFrame().setTitle("Model Settings");
    }

    /**
     * Retrieve the settings from the model, and update the panel's components
     * to reflect them.
     */
    protected abstract void retrieveSettings();

    /**
     * Assign the changes made in the panel's components back to the model.
     */
    protected abstract void assignSettings();
}
