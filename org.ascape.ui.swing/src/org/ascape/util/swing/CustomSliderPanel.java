/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util.swing;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.ascape.util.Utility;


/**
 * A panel containg a slider and appropriate labels that automatically
 * scale an actual value to the range of values expected by the slider.
 * To use, subclass and overide the actual value getters and setters. For example,
 * you might place the following lines in a model's createViews method.
 * <pre>
 * CustomSliderPanel mySlider = new CustomSliderPanel("Important value", 0.0, 1.0, 2) {
 *     public double getActualValue() {
 *         return getMyImportantValue();
 *     }
 *     public void setActualValue(double value) {
 *          setMyImportantValue(value);
 *          [If neccessary, let model know that an update has been made]
 *      }
 * };
 * </pre>
 * If any changes occur to the actual value (that are not caused by the slider itself, of course)
 * you must call updateValue to inform the slider.
 * @author Miles Parker
 * @version 1.5
 * @history 1.5 docuemented
 * @since 1.2.5
 */
public abstract class CustomSliderPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -6626601592336438979L;

    protected JSlider slider;

    private JLabel label;

    protected JLabel valueLabel;

    protected double minimum;

    protected double maximum;

    private int decPlaces;

    private String name;

    /**
     * Constructs a slider panel taht can be set to any value between minimum and maximum,
     * and that allows changes to the given decimal place precision.
     * @param name a name that will appear as the slider's label
     * @param minimum the value of the slider at its minimum position
     * @param maximum the value of the slider at its maximum position
     * @param decPlaces the number of decimal places to have discrete points to
     */
    public CustomSliderPanel(String name, double minimum, double maximum, int decPlaces) {
        this.name = name;
        this.minimum = minimum;
        this.maximum = maximum;
        this.decPlaces = decPlaces;
    }

    /**
     * Constructs the visual components of the slider panel.
     */
    public void build() {
        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints gbc = gbl.getConstraints(this);
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.gridy = 0;
        label = new JLabel(name);
        label.setForeground(Color.black);
        add(label, gbc);
        int sliderminimum = (int) (minimum * getValueMultiplier());
        int slidermaximum = (int) (maximum * getValueMultiplier());
        int sliderCurrent = (int) (getIntermediateValue() * getValueMultiplier());
        slider = new JSlider(sliderminimum, slidermaximum, sliderCurrent);
        int majorSpacing = (slidermaximum - sliderminimum) / 5;
        slider.setMajorTickSpacing(majorSpacing);
        slider.setMinorTickSpacing((slidermaximum - sliderminimum) / 20);
        Hashtable labelTable = new Hashtable();
        for (int i = sliderminimum; i <= slidermaximum; i += majorSpacing) {
            labelTable.put(new Integer(i), new JLabel(Utility.formatToString(i / getValueMultiplier(), getDecPlaces())));
        }
        slider.setLabelTable(labelTable);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        valueLabel = new JLabel("");
        updateLabel();
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        valueLabel.setForeground(Color.black);
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                double newValue = (double) source.getValue() / getValueMultiplier();
                setIntermediateValue(newValue);
                //For some reason, panel size isn't being validated on resize
                //if (!source.getValueIsAdjusting()) {
                //MEI: commented out the next line because it was causing FinInstab to
                //lock up when starting up a large model with iterationsPerRedraw > 1,
                // and commenting it out did not seem to cause any problems in FinInstab,
                // bionland Model L, M, or P, CVModel, or WaterWars
                //CustomSliderPanel.this.validate();
                //}
            }
        });
        gbc.fill = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx++;
        gbc.weightx = 1.0;
        add(valueLabel, gbc);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        add(slider, gbc);
    }

    /**
     * Updates the label with the current value.
     */
    protected void updateLabel() {
        valueLabel.setText(Utility.formatToString(getIntermediateValue(), getDecPlaces()));
    }

    /**
     * Informs the slider that the current actual value has been updated, causing the slider and label
     * to be updated with the current actual value.
     * Call this method whenever a change has been made in the actual value, except
     * of course if that change has been made by the slider itself.
     */
    public void valueUpdated() {
        if (slider != null) {
            slider.setValue((int) (Math.round(getIntermediateValue() * getValueMultiplier())));
            updateLabel();
        }
    }

    /**
     * Returns the value multiplier used in translating the slider value to the actual value and back.
     */
    protected double getValueMultiplier() {
        return Math.pow(10, getDecPlaces());
    }

    /**
     * Returns the number of decimal places selectable in the slider.
     */
    public int getDecPlaces() {
        return decPlaces;
    }

    /**
     * Returns the intermediate value, that is the scaled value prior to
     * being multiplied by the value multiplier.
     */
    protected double getIntermediateValue() {
        return getActualValue();
    }

    /**
     * Sets the intermediate value, updating the label in the process.
     * @param value the intermediate value
     */
    public void setIntermediateValue(double value) {
        //System.out.println("+++ "+value +", "+ getActualValue());
        int roundedTestValue = (int) Math.round(getActualValue() * Math.pow(10, getDecPlaces()));
        if (roundedTestValue != (int) Math.round(value * Math.pow(10, getDecPlaces()))) {
            setActualValue(value);
        }
        updateLabel();
    }

    /**
     * Returns the actual value the slider is representing.
     * Override this moethod to return the actual value you want the slider to portray.
     */
    public abstract double getActualValue();

    /**
     * Upon any slider change this method will be called with the new actual value
     * the slider is representing. Override this method to set the value in your model
     * taht the slider is responsible for.
     * @param value the new value that the slider has been changed to
     */
    public abstract void setActualValue(double value);

    /**
     * Returns the slider component for direct manipulation.
     */
    public JSlider getSlider() {
        return slider;
    }

    /**
     * Returns the slider name label for direct manipulation.
     */
    public JLabel getLabel() {
        return label;
    }

    /**
     * Returns the value label (the label that displays the sliders actual current value)
     * for direct manipulation.
     */
    public JLabel getValueLabel() {
        return valueLabel;
    }
}
