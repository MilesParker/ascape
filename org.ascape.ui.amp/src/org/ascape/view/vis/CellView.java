/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc.,
 * Metascape LLC, and contributors. All rights reserved. This program and the
 * accompanying materials are made available solely under of the BSD license
 * "ascape-license.txt". Any referenced or included libraries carry licenses of
 * their respective copyright holders.
 */

package org.ascape.view.vis;

import org.ascape.util.vis.ColorFeature;
import org.ascape.util.vis.DrawFeature;
import org.eclipse.draw2d.Graphics;



/**
 * A generic base class for views that draw some kind of spatial view of a group of cells. Cell views have a default
 * draw feature that draws a background for the cell, using the cell color feature.
 * 
 * @author Miles Parker
 * @version 3.0
 * @history 3.0 Renamed from BaseCellView, extensive refactorings
 * @history 1.9 5/1/00 added support for cell customizer
 * @history 1.2.6 10/26/99 updated with better support for draw feature names
 * @history 1.1.2 5/17/99 added border support (replacing border cell draw method)
 * @history 1.0.1 1/13/99 added support for multiple draw features
 * @since 1.0
 */
public abstract class CellView extends AgentView {

    private static final long serialVersionUID = 4897290361291185128L;

    /**
     * Draws the provided object, assumed to be a cell, by filling it using the cell color feature.
     */
    public final DrawFeature cells_fill_draw_feature = new DrawFeature("Cell Fill") {
        /**
         * 
         */
        private static final long serialVersionUID = 7839214134595858090L;

        public final void draw(Graphics g, Object object, int width, int height) {
            g.setBackgroundColor(agentColorFeature.getColor(object));
            g.fillRectangle(0, 0, width, height);
        }
    };

    /**
     * Draws the provided object, assumed to be a cell, by filling it using the cell color feature.
     */
    public final DrawFeature cells_fill_draw_inset_feature = new DrawFeature("Border Cell") {
        /**
         * 
         */
        private static final long serialVersionUID = 7121529492206023085L;

        public final void draw(Graphics g, Object object, int width, int height) {
            g.setBackgroundColor(agentColorFeature.getColor(object));
            g.fillRectangle(1, 1, width - 2, height - 2);
        }
    };

    /**
     * Size of border around each cell. 0 by default.
     */
    protected int borderSize = 0;

    /**
     * Constructs a CellView, adding a listener to present a settings frame when the user double-clicks on the view.
     */
    public CellView(String name) {
        super(name);
    }

    @Override
    public void createFeatures() {
        super.createFeatures();
        addDrawFeature(cells_fill_draw_feature);
        cells_fill_draw_feature.setName(getName() + " Cells Fill");
        addDrawFeature(cells_fill_draw_inset_feature);
        cells_fill_draw_inset_feature.setName(getName() + " Cells Fill Inset");
        getDrawSelection().setSelected(cells_fill_draw_feature, true);
    }

    /**
     * Returns the color feature that will be used for determining cell color. The default color feature is simply the
     * getForegroundColor() method of the cell.
     */
    public ColorFeature getPrimaryAgentColorFeature() {
        return agentColorFeature;
    }

    /**
     * Set the color feature that will be used for determining cell color. The default color feature is simply the
     * getForegroundColor() method of the cell.
     * 
     * @param cellColorFeature
     *        the color feature, whose object is assumed to be a cell populating this lattice
     */
    public void setPrimaryAgentColorFeature(ColorFeature cellColorFeature) {
        this.agentColorFeature = cellColorFeature;
    }

    /**
     * Returns the color feature that will be used for determining cell color. Mirrors agent color feature. Essentially
     * here for backward compatibility. The default color feature is simply the getForegroundColor() method of the cell.
     * 
     * @return the cell color feature
     */
    public ColorFeature getCellColorFeature() {
        return getAgentColorFeature();
    }

    /**
     * Set the color feature that will be used for determining cell color. Mirrors agent color feature. Essentially here
     * for backward compatibility. The default color feature is simply the getForegroundColor() method of the cell.
     * 
     * @param agentColorFeature
     *        the color feature, whose object is assumed to be a cell populating this lattice
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
     *        number of pixels
     */
    public void setBorderSize(int borderSize) {
        this.borderSize = borderSize;
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
     *        number of pixels per edge
     */
    public void setCellSize(int cellSize) {
        setAgentSize(cellSize);
    }
}
