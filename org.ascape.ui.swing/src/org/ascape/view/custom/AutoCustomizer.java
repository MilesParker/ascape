/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.view.custom;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.PopupMenu;
import java.awt.TextArea;
import java.awt.TextField;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;

import org.ascape.model.Scape;
import org.ascape.util.PropertyAccessor;


/**
 * A panel for making changes to model settings. Automatically creates field
 * names and text fields for viewing and changing model settings based on scape
 * bean info.
 * 
 * @author Miles Parker
 * @version 1.1.2
 * @history 1.1.2 Improved Netscape error reporting
 * @history first in 1.0
 * @since 1.0
 */
public class AutoCustomizer extends ModelCustomizer {

    /**
     * Labels for settings components.
     */
    private Label[] settingsLabels;

    /**
     * Components for settings components. (Text fields for now.)
     */
    private Component[] settingsComponents;

    /**
     * The accessors for the values being set.
     */
    private PropertyAccessor[] settingsAccessors;

    /**
     * The current (and last) netscape Java release is flawed.
     */
    private boolean netscapeFailure;

    /**
     * The panel that all settings are displayed within.
     */
    private JPanel settingsPanel;

    /**
     * The panel that all settings are displayed within.
     */
    private JPanel scapePanel;

    /**
     * The netscape msg.
     */
    private static String netscapeMsg = "Sorry, this settings dialog does not work in your environment. " +
        "The most likely reason for this is a flaw in Netscape's implementation " +
        "of Java security and introspection. If you want to use this feature, try using " +
        "the JRE plug-in at http://java.sun.com/products/jdk/1.2/jre, or another java-capable browser like Internet Explorer.";

    /**
     * Create and place the customizer's components. Introspects the model to
     * find setting's accessors, and adds descriptions and text fields to the
     * customizer for them.
     */
    public void build() {
        super.build();
        buildSettingsPanel();
        contentPanel.add(settingsPanel, "Center");
        validate();
    }

    /**
     * Builds the settings panel.
     */
    protected void buildSettingsPanel() {
        GridBagLayout gbl = new GridBagLayout();
        settingsPanel = new JPanel();
        settingsPanel.setLayout(gbl);
        GridBagConstraints gbc = gbl.getConstraints(this);
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.gridy = 1;
        try {
            java.util.List accessors = scape.retrieveAllAccessors();
            PropertyAccessor[] allAccessors = (PropertyAccessor[]) accessors.toArray(new PropertyAccessor[accessors.size()]);
            settingsLabels = new Label[allAccessors.length];
            settingsComponents = new Component[allAccessors.length];
            settingsAccessors = new PropertyAccessor[allAccessors.length];
            for (int i = 0; i < settingsAccessors.length; i++) {
                //Alphabetize
                int lowestPosition = 0;
                for (int j = 1; j < allAccessors.length; j++) {
                    if ((allAccessors[j] != null) && ((allAccessors[lowestPosition] == null) || (allAccessors[lowestPosition].getDescriptor().getName().compareTo(allAccessors[j].getDescriptor().getName()) > 0))) {
                        lowestPosition = j;
                    }
                }
                settingsAccessors[i] = allAccessors[lowestPosition];
                allAccessors[lowestPosition] = null;
                settingsLabels[i] = new Label(settingsAccessors[i].getLongName());
                gbc.gridwidth = 1;
                gbc.gridx = 0;
                gbc.weightx = 1.0;
                settingsPanel.add(settingsLabels[i], gbc);
                PropertyEditor editor = PropertyEditorManager.findEditor(settingsAccessors[i].getDescriptor().getPropertyType());
                if (editor.getCustomEditor() != null) {
                    settingsComponents[i] = editor.getCustomEditor();
                } else {
                    settingsComponents[i] = new TextField();
                }
                //gbc.gridwidth = GridBagConstraints.REMAINDER;
                gbc.gridx = 1;
                gbc.weightx = 10.0;
                settingsPanel.add(settingsComponents[i], gbc);
                gbc.gridy++;
            }
            //Filler so the panel exapnds to fill all available space, leaving white pace below it
            gbc.weighty = 1.0;
            settingsPanel.add(new Label(), gbc);
        } catch (RuntimeException e) {
            netscapeFailure = true;
            System.out.println(netscapeMsg);
            TextArea msg = new TextArea(netscapeMsg, 16, 38, TextArea.SCROLLBARS_NONE);
            msg.setEditable(false);
            setLayout(new BorderLayout());
            removeAll();
            settingsPanel.add("Center", msg);
        }
    }

    /**
     * Builds the scape panel.
     */
    protected void buildScapePanel() {
        GridBagLayout gbl = new GridBagLayout();
        scapePanel = new JPanel();
        scapePanel.setLayout(gbl);
        GridBagConstraints gbc = gbl.getConstraints(this);
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.gridy = 1;
        PopupMenu scapePopup = new PopupMenu("Scapes");
        List scapes = scape.getAllScapes();
        for (Iterator iterator = scapes.iterator(); iterator.hasNext();) {
            scapePopup.add(((Scape) iterator.next()).getName());

        }
        scapePanel.add(scapePopup);
    }

    /**
     * Retrieve the settings from the model, and update the panel's components
     * to reflect them. Takes all the accessor values and assigns them to the
     * fields.
     */
    public void retrieveSettings() {
        if (!netscapeFailure) {
            for (int i = 0; i < settingsAccessors.length; i++) {
                //To do: fix to handle custom editors
                if (settingsComponents[i] instanceof TextField) {
                    ((TextField) settingsComponents[i]).setText(settingsAccessors[i].getAsText());
                }
            }
        }
    }

    /**
     * Assign the changes made in the panel's components back to the model. Gets
     * the field values and changes them through the accessors.
     */
    public void assignSettings() {
        for (int i = 0; i < settingsAccessors.length; i++) {
            //To do: fix to handle custom editors
            if (settingsComponents[i] instanceof TextField) {
                try {
                    settingsAccessors[i].setAsText(((TextField) settingsComponents[i]).getText());
                } catch (IllegalArgumentException e) {
                    ((TextField) settingsComponents[i]).setText(settingsAccessors[i].getAsText());
                } catch (InvocationTargetException e) {
                    ((TextField) settingsComponents[i]).setText(settingsAccessors[i].getAsText());
                    throw new Error("Exception in called method: " + e.getTargetException());
                }
            }
        }
    }
}
