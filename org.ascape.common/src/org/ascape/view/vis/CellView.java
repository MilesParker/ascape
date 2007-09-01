/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.view.vis;

import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.swing.ImageIcon;

import org.ascape.explorer.UserEnvironment;
import org.ascape.model.Agent;
import org.ascape.model.event.ScapeEvent;
import org.ascape.util.vis.ColorFeature;
import org.ascape.util.vis.ColorFeatureConcrete;
import org.ascape.util.vis.DrawFeature;

/**
 * A generic base class for views that draw some kind of spatial view of a group
 * of cells. Cell views have a default draw feature that draws a background for
 * the cell, using the cell color feature.
 * 
 * @author Miles Parker
 * @version 3.0
 * @history 3.0 Renamed from BaseCellView, extensive refactorings
 * @history 1.9 5/1/00 added support for cell customizer
 * @history 1.2.6 10/26/99 updated with better support for draw feature names
 * @history 1.1.2 5/17/99 added border support (replacing border cell draw
 *          method)
 * @history 1.0.1 1/13/99 added support for multiple draw features
 * @since 1.0
 */
public abstract class CellView extends AgentSizedView {

    /**
     * Draws the provided object, assumed to be a cell, by filling it using the
     * cell color feature.
     */
    public final DrawFeature cells_fill_draw_feature = new DrawFeature("Cell Fill") {
        public final void draw(Graphics g, Object object, int width, int height) {
            g.setColor(agentColorFeature.getColor(object));
            g.fillRect(0, 0, width, height);
        }
    };

    /**
     * Draws the provided object, assumed to be a cell, by filling it using the
     * cell color feature.
     */
    public final DrawFeature cells_fill_draw_inset_feature = new DrawFeature("Border Cell") {
        public final void draw(Graphics g, Object object, int width, int height) {
            g.setColor(agentColorFeature.getColor(object));
            g.fillRect(1, 1, width - 2, height - 2);
        }
    };

    /**
     * Size of border around each cell. 0 by default.
     */
    protected int borderSize = 0;

    /**
     * Constructs a CellView, adding a listener to present a settings frame when
     * the user double-clicks on the view.
     */
    public CellView() {
        clearBackgroundAutomatically = false;
    }

    /**
     * Builds the view. Sets the color feature defaults, adds the default oval
     * and fill draw features, and selects the draw agents as ovals feature.
     */
    public void build() {
        super.build();
        if (agentColorFeature == null) {
            agentColorFeature = new ColorFeatureConcrete("Default Cell Color") {
                public Color getColor(Object object) {
                    return ((Agent) object).getColor();
                }
            };
        }
        addDrawFeature(cells_fill_draw_feature);
//		cells_fill_draw_feature.setName(getName() + " Cells Fill");
        addDrawFeature(cells_fill_draw_inset_feature);
//		cells_fill_draw_inset_feature.setName(getName() + " Cells Fill Inset");
        getDrawSelection().setSelected(cells_fill_draw_feature, true);
    }

    /**
     * On notification of a scape update, draws the actual overhead view.
     * 
     * @return the primary agent color feature
     */
/*public void updateScapeGraphics() {
		drawSelectedAgent(bufferedGraphics);
        super.updateScapeGraphics();
	}*/

    /**
     * Returns the color feature that will be used for determining cell color.
     * The default color feature is simply the getColor() method of the cell.
     */
    public ColorFeature getPrimaryAgentColorFeature() {
        return agentColorFeature;
    }

    /**
     * Set the color feature that will be used for determining cell color. The
     * default color feature is simply the getColor() method of the cell.
     * 
     * @param cellColorFeature
     *            the color feature, whose object is assumed to be a cell
     *            populating this lattice
     */
    public void setPrimaryAgentColorFeature(ColorFeature cellColorFeature) {
        this.agentColorFeature = cellColorFeature;
    }

    /**
     * Returns the color feature that will be used for determining cell color.
     * Mirrors agent color feature. Essentially here for backward compatibility.
     * The default color feature is simply the getColor() method of the cell.
     * 
     * @return the cell color feature
     */
    public ColorFeature getCellColorFeature() {
        return getAgentColorFeature();
    }

    /**
     * Set the color feature that will be used for determining cell color.
     * Mirrors agent color feature. Essentially here for backward compatibility.
     * The default color feature is simply the getColor() method of the cell.
     * 
     * @param agentColorFeature
     *            the color feature, whose object is assumed to be a cell
     *            populating this lattice
     */
    public void setCellColorFeature(ColorFeature agentColorFeature) {
        this.agentColorFeature = agentColorFeature;
    }

    /**
     * Returns a one-dimension size in pixels of the cell border.
     * 
     * @return the border size
     */
    public int getBorderSize() {
        return borderSize;
    }

    /**
     * Sets the border size in pixels.
     * 
     * @param borderSize
     *            number of pixels
     */
    public void setBorderSize(int borderSize) {
        this.borderSize = borderSize;
    }

    /**
     * Notifies the view that the scape has removed it. Removes the customizer.
     * 
     * @param scapeEvent
     *            the scape removed notification event
     */
    public synchronized void scapeRemoved(ScapeEvent scapeEvent) {
        super.scapeRemoved(scapeEvent);
        removeCustomizer();
    }

    /**
     * Return an icon that can be used to represent this frame. Returns null in
     * this case, use default. Implementors should specify an icon that makes
     * sense for the view.
     * 
     * @return the icon
     */
    public ImageIcon getIcon() {
        return UserEnvironment.getIcon("Sheet");
    }

    /**
     * Returns a one-dimension size of pixels used to represent each cell.
     * 
     * @return the cell size
     */
    public int getCellSize() {
        return getAgentSize();
    }

    /**
     * Sets the number of pixels used to represent each cell.
     * 
     * @param cellSize
     *            number of pixels per edge
     */
    public void setCellSize(int cellSize) {
        setAgentSize(cellSize);
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
     * @exception java.io.IOException
     *                Includes any I/O exceptions that may occur
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(new Integer(borderSize));
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
     * @exception java.io.IOException
     *                if I/O errors occur
     * @exception java.lang.ClassNotFoundException
     *                If the class for an object being restored cannot be found.
     */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        borderSize = ((Integer) in.readObject()).intValue();
    }
}
