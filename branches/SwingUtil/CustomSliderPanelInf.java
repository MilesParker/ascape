/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.swing;

import java.util.Hashtable;

import javax.swing.JLabel;

import org.ascape.util.Utility;

/**
 * A panel containg a slider and appropriate labels that automatically
 * scale an actual value to the range of values expected by the slider, with the
 * rightmost edge of the slider represting infinity, as represented by Interger.MAX_VALUE.
 * See CustomSliderPanel for details on usage.
 * @author Miles Parker
 * @version 1.5
 * @history 1.5 docuemented
 * @since 1.2.5
 * @see org.ascape.util.swing.CustomSliderPanel
 */
public abstract class CustomSliderPanelInf extends CustomSliderPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -2903784979153258556L;

    /**
     * Constructs a slider panel that can be set to any value between minimum and infinity,
     * and that allows changes to the given decimal place precision. Note that value specified
     * by maximum will be replaced with infinity, so if you want that value to be selectable,
     * choose a slightly higher value for maximum.
     * @param name a name that will appear as the slider's label
     * @param minimum the value of the slider at its minimum position
     * @param maximum the value of the slider at its maximum position
     * @param decPlaces the number of decimal places to have discrete points to
     */
    public CustomSliderPanelInf(String name, double minimum, double maximum, int decPlaces) {
        super(name, minimum, maximum, decPlaces);
    }

    /**
     * Constructs the visual components of the slider panel.
     */
    public void build() {
        super.build();
        int sliderminimum = (int) (minimum * getValueMultiplier());
        int slidermaximum = (int) (maximum * getValueMultiplier());
        //int sliderCurrent = (int) (getIntermediateValue() * getValueMultiplier());
        int majorSpacing = (slidermaximum - sliderminimum) / 5;
        slider.setMajorTickSpacing(majorSpacing);
        slider.setMinorTickSpacing((slidermaximum - sliderminimum) / 20);
        Hashtable labelTable = new Hashtable();
        for (int i = sliderminimum; i < slidermaximum; i += majorSpacing) {
            labelTable.put(new Integer(i), new JLabel(Utility.formatToString(i / getValueMultiplier(), getDecPlaces())));
        }
        labelTable.put(new Integer(slidermaximum), new JLabel("INF"));
        slider.setLabelTable(labelTable);
    }

    /**
     * Updates the label with the current value. ("INF" if appropriate.)
     */
    protected void updateLabel() {
        if (getIntermediateValue() != maximum) {
            valueLabel.setText(Utility.formatToString(getIntermediateValue(), getDecPlaces()));
        } else {
            valueLabel.setText("INF");
        }
    }

    /**
     * Informs the slider that the current actual value has been updated, causing the slider and label
     * to be updated with the current actual value.
     * Call this method whenever a change has been made in the actual value, except
     * of course if that change has been made by the slider itself.
     */
    public void valueUpdated() {
        if (getActualValue() != Integer.MAX_VALUE) {
            super.valueUpdated();
        } else {
            slider.setValue((int) (maximum * getValueMultiplier()));
            valueLabel.setText("INF");
        }
    }

    /**
     * Returns the intermediate value, that is the scaled value prior to
     * being multiplied by the value multiplier.
     */
    public double getIntermediateValue() {
        if (getActualValue() != Integer.MAX_VALUE) {
            return getActualValue();
        } else {
            return maximum;
        }
    }

    /**
     * Sets the intermediate value, updating the label in the process.
     * @param value the intermediate value
     */
    public void setIntermediateValue(double value) {
        if (value != maximum) {
            setActualValue(value);
        } else {
            setActualValue(Integer.MAX_VALUE);
        }
        updateLabel();
    }
}
