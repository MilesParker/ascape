/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.view.vis;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.ascape.model.Agent;
import org.ascape.model.Cell;
import org.ascape.model.CellOccupant;
import org.ascape.util.vis.ColorFeature;
import org.ascape.util.vis.ColorFeatureConcrete;
import org.ascape.util.vis.DrawFeature;
import org.ascape.util.vis.ImageFeature;
import org.ascape.util.vis.ImageFeatureConcrete;


/**
 * A base class for drawing a collection of agents upon a collection of cells.
 * By default, a base agent cell view draws agents as ovals, but any other draw
 * behavior can be used by adding and selecting it.
 * 
 * @author Miles Parker
 * @version 1.2.6
 * @history 1.2.6 10/26/99 updated with better support for draw feature names
 * @history 1.2.5 9/1/1999 added support for drawing images for cells
 * @history 1.0.1 provide support for draw features
 * @since 1.0
 */
public abstract class HostedAgentView extends CellView {

    /**
     * The color feature used to set the agent draw color.
     */
    private ColorFeature hostedAgentColorFeature;

    /**
     * The image feature used to draw the agent draw color.
     */
    private ImageFeature hostedAgentImageFeature;

    /**
     * Draws the provided object, assumed to be a cell with an occupant, by
     * filling the cell using the cell color feature interpretation, and filling
     * an oval using the agent color feature on the occupant.
     */
    public final DrawFeature agents_oval_cells_draw_feature = new DrawFeature("Default Oval Agent") {
        /**
         * 
         */
        private static final long serialVersionUID = -6272510346583316008L;

        public final void draw(Graphics g, Object object, int width, int height) {
            if (object != null) {
                g.setColor(agentColorFeature.getColor(object));
                g.fillRect(0, 0, width, height);
                CellOccupant occupant = (CellOccupant) ((Cell) object).getOccupant();
                if (occupant != null) {
                    if (hostedAgentColorFeature == null) {
                        System.out.println("hosted agent color feature is null");
                    }
                    //                    else {
                    //                    System.out.println("not null");
                    //                }
                    g.setColor(hostedAgentColorFeature.getColor(occupant));
                    g.fillOval(1, 1, width - 2, height - 2);
                }
            }
        }
    };

    /**
     * Draws the provided object, assumed to be a cell with an occupant, by
     * filling the cell using the cell color feature interpretation, and filling
     * an oval using the agent color feature on the occupant.
     */
    public final DrawFeature agents_oval_cells_boundary_draw_feature = new DrawFeature("Default Oval Agent") {
        /**
         * 
         */
        private static final long serialVersionUID = 4911219526716031691L;

        public final void draw(Graphics g, Object object, int width, int height) {
            g.setColor(agentColorFeature.getColor(object));
            g.fillRect(0, 0, width, height);
            CellOccupant occupant = (CellOccupant) ((Cell) object).getOccupant();
            if (occupant != null) {
                g.setColor(Color.black);
                g.drawOval(1, 1, width - 2, height - 2);
                g.setColor(hostedAgentColorFeature.getColor(occupant));
                g.fillOval(1, 1, width - 2, height - 2);
            }
        }
    };

    /**
     * Draws the provided object, assumed to be a cell with an occupant, by
     * filling the cell using the cell color feature interpretation, filling an
     * oval using the agent color feature on the occupant, and printing a
     * descriptive string next to any agents.
     */
    public final DrawFeature agents_oval_cells_desc_draw_feature = new DrawFeature("Default Oval Agent") {
        /**
         * 
         */
        private static final long serialVersionUID = -4498969872571450812L;

        public final void draw(Graphics g, Object object, int width, int height) {
            CellOccupant occupant = (CellOccupant) ((Cell) object).getOccupant();
            if (occupant != null) {
                g.setColor(Color.black);
                g.drawString(occupant.toString(), width, height);
            }
        }
    };

    /**
     * Draws the provided object, assumed to be a cell with an occupant, by
     * filling any unoccupied cells using the cell color feature interpretation,
     * and filling any occupied cells using the agent color feature on the
     * occupant.
     */
    public final DrawFeature agents_fill_cells_draw_feature = new DrawFeature("Default Fill Agent") {
        /**
         * 
         */
        private static final long serialVersionUID = -2515862414367192654L;

        public final void draw(Graphics g, Object object, int width, int height) {
            CellOccupant occupant = (CellOccupant) ((Cell) object).getOccupant();
            if (occupant != null) {
                g.setColor(hostedAgentColorFeature.getColor(occupant));
                g.fillRect(0, 0, width, height);
            } else {
                g.setColor(agentColorFeature.getColor(object));
                g.fillRect(0, 0, width, height);
            }
        }
    };

    /**
     * Draws the provided object, assumed to be a cell with an occupant, by
     * filling the cell using the cell color feature interpretation, and filling
     * an oval using the agent color feature on the occupant.
     */
    public final DrawFeature agents_image_cells_draw_feature = new DrawFeature("Default Image Agent") {
        /**
         * 
         */
        private static final long serialVersionUID = -100190730941724219L;

        public final void draw(Graphics g, Object object, int width, int height) {
            g.setColor(agentColorFeature.getColor(object));
            g.fillRect(0, 0, width, height);
            CellOccupant occupant = (CellOccupant) ((Cell) object).getOccupant();
            // default agent.getImage is null..
            if (occupant != null && hostedAgentImageFeature.getImage(occupant) != null) {
                g.drawImage(hostedAgentImageFeature.getImage(occupant), 0, 0, null);
            }
        }
    };

    /**
     * Builds the view. Sets the color feature defaults, adds the default oval
     * and fill draw features, and selects the draw agents as ovals feature.
     */
    public void build() {
        super.build();
        //System.out.println("build "+this);
        if (hostedAgentColorFeature == null) {
            hostedAgentColorFeature = new ColorFeatureConcrete("Default Agent Color") {
                /**
                 * 
                 */
                private static final long serialVersionUID = -197439982190788937L;

                public Color getColor(Object object) {
                    return ((Agent) object).getColor(object);
                }
            };
        }
        if (hostedAgentImageFeature == null) {
            hostedAgentImageFeature = new ImageFeatureConcrete("Default Agent Image") {
                /**
                 * 
                 */
                private static final long serialVersionUID = 8347260892400147273L;

                public Image getImage(Object object) {
                    return ((Agent) object).getImage();
                }
            };
        }
        addDrawFeature(agents_oval_cells_draw_feature);
        //		agents_oval_cells_draw_feature.setName(getName() + " Agents");
        addDrawFeature(agents_oval_cells_boundary_draw_feature);
        //		agents_oval_cells_boundary_draw_feature.setName(getName() + " Agents Boundary");
        addDrawFeature(agents_fill_cells_draw_feature);
        //		agents_fill_cells_draw_feature.setName(getName() + " Agents Fill");
        addDrawFeature(agents_image_cells_draw_feature);
        //		agents_image_cells_draw_feature.setName(getName() + " Agents Image");
        addDrawFeature(agents_oval_cells_desc_draw_feature);
        //		agents_oval_cells_desc_draw_feature.setName(getName() + " Agents Description");
        //getDrawSelection().update();
        getDrawSelection().clearSelection();
        //To try images, uncomment these lines and comment out the following line
        //setCellSize(15);
        //getDrawSelection().setSelected(agents_image_cells_draw_feature, true);
        getDrawSelection().setSelected(agents_oval_cells_draw_feature, true);
    }

    /**
     * Returns the color feature that will be used for determining agent color.
     * The default color feature is simply the getColor() method of the agent.
     * 
     * @return the primary agent color feature
     */
    public ColorFeature getPrimaryAgentColorFeature() {
        return agentColorFeature;
    }

    /**
     * Set the color feature that will be used for determining agent color. The
     * default color feature is simply the getColor() method of the agent.
     * 
     * @param cellColorFeature
     *            the color feature, whose object is assumed to be a cell living
     *            on this lattice
     */
    public void setPrimaryAgentColorFeature(ColorFeature cellColorFeature) {
        this.agentColorFeature = cellColorFeature;
    }

    /**
     * Returns the color feature that will be used for determining agent color.
     * The default color feature is simply the getColor() method of the agent.
     * 
     * @return the hosted agent color feature
     */
    public ColorFeature getHostedAgentColorFeature() {
        return this.hostedAgentColorFeature;
    }

    /**
     * Set the color feature that will be used for determining agent color. The
     * default color feature is simply the getColor() method of the agent.
     * 
     * @param agentColorFeature
     *            the color feature, whose object is assumed to be a cell living
     *            on this lattice
     */
    public void setHostedAgentColorFeature(ColorFeature agentColorFeature) {
        this.hostedAgentColorFeature = agentColorFeature;
    }

    /**
     * Set the image feature that will be used for determining agent image. The
     * default image feature is simply the getImage() method of the agent.
     * 
     * @param agentImageFeature
     *            the image feature, whose object is assumed to be a cell living
     *            on this lattice
     */
    public void setHostedAgentImageFeature(ImageFeature agentImageFeature) {
        this.hostedAgentImageFeature = agentImageFeature;
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.CellView#writeExternal(java.io.ObjectOutput)
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(hostedAgentColorFeature);
        out.writeObject(hostedAgentImageFeature);
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.CellView#readExternal(java.io.ObjectInput)
     */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        hostedAgentColorFeature = (ColorFeature) in.readObject();
        hostedAgentImageFeature = (ImageFeature) in.readObject();
    }
}

