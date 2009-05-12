/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.view.custom;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.swing.JPanel;

import org.ascape.model.event.ScapeCustomizer;
import org.ascape.view.vis.PanelView;


/**
 * A panel for making live changes to a model's settings.
 * 
 * @author Miles Parker
 * @version 1.2
 * @history 1.2 7/9/99 made changes to support other customizers
 * @history 1.2 6/2/99 first in
 * @since 1.2
 */
public abstract class BaseCustomizer extends PanelView implements ScapeCustomizer, Externalizable {

    /**
     * The object this customizer is editing.
     */
    private Object target;

    /**
     * Property change support for the customizer.
     */
    private PropertyChangeSupport propertySupport;

    /**
     * The panel that all settings are displayed within.
     */
    protected JPanel contentPanel;

    /**
     * The panel that standard (OK, Cancel, etc..) buttons can be displayed
     * within.
     */
    protected JPanel buttonPanel;

    /**
     * The last bounds.
     */
    private Rectangle lastBounds;

    /**
     * Called to create and layout the components of the customizer. Override to
     * provide a customizer implementation.
     */
    public void build() {
        removeAll();
        setLayout(new BorderLayout());
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout(0, 0));
        add(contentPanel, "Center");
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        JPanel buttonPanelContainer = new JPanel();
        buttonPanelContainer.setLayout(new BorderLayout());
        buttonPanelContainer.add(buttonPanel, "East");
        add(buttonPanelContainer, "South");
        if (getViewFrame() != null && lastBounds != null) {
            getViewFrame().getFrameImp().setBounds(lastBounds);
        }
    }

    /**
     * Selected.
     */
    public void selected() {
        if (getViewFrame() != null) {
            getViewFrame().toFront();
            requestFocus();
        }
    }

    /**
     * Sets the panel where editing components (as opposed to window related
     * buttons) are kept.
     * 
     * @param contentPanel
     *            the object being customized
     */
    public void setContentPanel(JPanel contentPanel) {
        this.contentPanel = contentPanel;
    }

    /**
     * Returns the panel where custom editing components are kept.
     * 
     * @return the content panel
     */
    public JPanel getContentPanel() {
        return contentPanel;
    }

    /**
     * Sets the panel where editing components (as opposed to window related
     * buttons) are kept.
     * 
     * @param buttonPanel
     *            the object being customized
     */
    public void setButtonPanel(JPanel buttonPanel) {
        this.buttonPanel = buttonPanel;
    }

    /**
     * Returns the panel where custom editing components are kept.
     * 
     * @return the button panel
     */
    public JPanel getButtonPanel() {
        return buttonPanel;
    }

    /**
     * Sets the object this customizer is modifying.
     * 
     * @param target
     *            the object being customized
     */
    public void setObject(Object target) {
        this.target = target;
        if (target != null) {
            propertySupport = new PropertyChangeSupport(target);
        } else {
            propertySupport = null;
        }
    }

    /**
     * Returns the object this customizer is modifying.
     * 
     * @return the object
     */
    public Object getObject() {
        return target;
    }

    /**
     * Returns the bounds of this customizer the last time it was moved.
     * 
     * @return the last bounds for this component
     */
    public Rectangle getLastBounds() {
        return lastBounds;
    }

    /**
     * Set the last bounds for this component. Though public, shoudl not be used
     * except by the user environment.
     * 
     * @param lastBounds
     *            the last bounds
     */
    public void setLastBounds(Rectangle lastBounds) {
        this.lastBounds = lastBounds;
    }

    /**
     * Add a property change event listener.
     * 
     * @param p
     *            the p
     */
    public void addPropertyChangeListener(PropertyChangeListener p) {
        propertySupport.addPropertyChangeListener(p);
    }

    /**
     * Removes a property change listener.
     * 
     * @param p
     *            the p
     */
    public void removePropertyChangeListener(PropertyChangeListener p) {
        propertySupport.removePropertyChangeListener(p);
    }

    /**
     * The object implements the writeExternal method to save its contents by
     * calling the methods of DataOutput for its primitive values or calling the
     * writeObject method of ObjectOutput for objects, strings, and arrays.
     * 
     * @param out
     *            the stream to write the object to
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @serialData Overriding methods should use this tag to describe the data
     *             layout of this Externalizable object. List the sequence of
     *             element types and, if possible, relate the element to a
     *             public/protected field and/or method of this Externalizable
     *             class.
     * @exception IOException
     *                Includes any I/O exceptions that may occur
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(target);
    }

    /**
     * The object implements the readExternal method to restore its contents by
     * calling the methods of DataInput for primitive types and readObject for
     * objects, strings and arrays. The readExternal method must read the values
     * in the same sequence and with the same types as were written by
     * writeExternal.
     * 
     * @param in
     *            the stream to read data from in order to restore the object
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ClassNotFoundException
     *             the class not found exception
     * @exception IOException
     *                if I/O errors occur
     * @exception ClassNotFoundException
     *                If the class for an object being restored cannot be found.
     */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        target = in.readObject();
    }
}
