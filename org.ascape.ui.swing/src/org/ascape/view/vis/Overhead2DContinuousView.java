/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.view.vis;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.Serializable;
import java.util.Iterator;
import java.util.TooManyListenersException;

import org.ascape.model.Agent;
import org.ascape.model.LocatedAgent;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.space.Coordinate;
import org.ascape.model.space.Coordinate2DContinuous;
import org.ascape.util.vis.ColorFeature;
import org.ascape.util.vis.ColorFeatureConcrete;
import org.ascape.util.vis.DrawFeature;
import org.ascape.util.vis.DrawSymbol;
import org.ascape.util.vis.ImageFeature;
import org.ascape.util.vis.ImageFeatureConcrete;

/**
 * The Class Overhead2DContinuousView.
 */
public class Overhead2DContinuousView extends AgentView implements Serializable {

    /**
     * Size of each individual agent, typically in pixels. 6 by default.
     */
    private int nominalAgentSize = 5;

    /**
     * The default size of the view, in pixels.
     */
    private Dimension preferredSize;

    /**
     * The draw features.
     */
    private Object[] drawFeatures;

    /**
     * The color feature used to set the agent draw color.
     */
    private ColorFeature agentColorFeature;

    /**
     * The image feature used to set the agent image color.
     */
    private ImageFeature agentImageFeature;

    /**
     * The draw selected neighbors.
     */
    private boolean drawSelectedNeighbors;

    /**
     * Draws the provided object, assumed to be a agent, by filling it using the
     * agent color feature.
     */
    public final DrawFeature agents_fill_draw_feature = new DrawFeature("Agent Fill") {
        /**
         * 
         */
        private static final long serialVersionUID = -6961184796443185786L;

        public void draw(Graphics g, Object object, int x, int y) {
            g.setColor(agentColorFeature.getColor(object));
            LocatedAgent a = (LocatedAgent) object;
            g.setColor(a.getColor());
            //g.fillOval(0, 0, x, y);
            g.fillOval(-x / 2, -y / 2, x, y);
        }
    };

    /**
     * Draws the provided object, assumed to be a agent, by filling it using the
     * agent color feature.
     */
    public final DrawFeature agents_fill_draw_inset_feature = new DrawFeature("Border Agent") {
        /**
         * 
         */
        private static final long serialVersionUID = 3566236494167356035L;

        public void draw(Graphics g, Object object, int x, int y) {
            g.setColor(agentColorFeature.getColor(object));
            g.fillOval(-x / 2 + 2, -y / 2 + 2, x - 2, y - 2);
            //g.fillRect(2, 2, x-2, y-2);
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
        private static final long serialVersionUID = -5986344579765431238L;

        public final void draw(Graphics g, Object object, int width, int height) {
            g.drawImage(agentImageFeature.getImage(object), 0, 0, null);
        }
    };

    /**
     * Constructs a BaseAgentView, adding a listener to present a settings frame
     * when the user double-clicks on the view.
     */
    public Overhead2DContinuousView() {
        this("Continuous Overhead 2D View");
    }

    /**
     * Instantiates a new overhead2 D continuous view.
     * 
     * @param name
     *            the name
     */
    public Overhead2DContinuousView(String name) {
        super(name);
        this.name = name;
        preferredSize = new Dimension(400, 400);
    }

    /**
     * Builds the view. Sets the color feature defaults, adds the default oval
     * and fill draw features, and selects the draw agents as ovals feature.
     */
    public void build() {
        super.build();
        if (agentColorFeature == null) {
            agentColorFeature = new ColorFeatureConcrete("Default Agent Color") {
                /**
                 * 
                 */
                private static final long serialVersionUID = 5727313277431144451L;

                public Color getColor(Object object) {
                    return ((Agent) object).getColor();
                }
            };
        }
        if (agentImageFeature == null) {
            agentImageFeature = new ImageFeatureConcrete("Default Agent Image") {
                /**
                 * 
                 */
                private static final long serialVersionUID = -7509323116837685796L;

                public Image getImage(Object object) {
                    return ((Agent) object).getImage();
                }
            };
        }
        addDrawFeature(agents_fill_draw_feature);
//        agents_fill_draw_feature.setName(getName() + " Agents Fill");
        getDrawSelection().addElement(agents_fill_draw_feature);
        addDrawFeature(agents_image_cells_draw_feature);
        getDrawSelection().addElement(agents_image_cells_draw_feature, false);
    }

    /**
     * On notification of a scape update, draws the actual overhead view.
     */
    public void updateScapeGraphics() {
        super.updateScapeGraphics();
        drawFeatures = getDrawSelection().getSelection();
        //scale for x and y should always be the same
        for (Iterator agents = getScape().iterator(); agents.hasNext();) {
            drawAgent((LocatedAgent) agents.next(), scale());
        }
    }

    /**
     * The ratio of the view width to the actual scape width.
     * 
     * @return a scaling factor
     */
    private double scale() {
        //Should always be the same for x and y dimension so just check one dimension.
        //(View enforces this through getDimensionWithin.
        double viewWidth = getSize().getWidth();
        double scapeWidth = ((Coordinate2DContinuous) getScape().getExtent()).getXValue();
        return viewWidth / scapeWidth;
    }

    /**
     * Draw agent.
     * 
     * @param a
     *            the a
     * @param scale
     *            the scale
     */
    private void drawAgent(LocatedAgent a, double scale) {
        Coordinate2DContinuous origin = (Coordinate2DContinuous) a.getCoordinate();
        int pixelX = (int) (scale * origin.getXValue());
        int pixelY = (int) (scale * origin.getYValue());
        // translate to new temporary origin
        bufferedGraphics.translate(pixelX, pixelY);
        drawAgentAt(a, nominalAgentSize, nominalAgentSize);
        // translate back to normal origin
        bufferedGraphics.translate(-pixelX, -pixelY);
    }

    /**
     * Draw agent at.
     * 
     * @param agent
     *            the agent
     * @param x
     *            the x
     * @param y
     *            the y
     */
    private void drawAgentAt(LocatedAgent agent, int x, int y) {
        for (int i = 0; i < drawFeatures.length; i++) {
            ((DrawFeature) drawFeatures[i]).draw(bufferedGraphics, agent, x, y);
        }
    }

    /**
     * Draw agent at.
     * 
     * @param agent
     *            the agent
     * @param x
     *            the x
     * @param y
     *            the y
     * @param feature
     *            the feature
     */
    public void drawAgentAt(LocatedAgent agent, int x, int y, DrawFeature feature) {
        feature.draw(bufferedGraphics, agent, x, y);
    }

    /**
     * Draw agent at if update.
     * 
     * @param agent
     *            the agent
     * @param x
     *            the x
     * @param y
     *            the y
     */
    public void drawAgentAtIfUpdate(LocatedAgent agent, int x, int y) {
        if (agent.isUpdateNeeded(iterationsPerRedraw)) {
            for (int i = 0; i < drawFeatures.length; i++) {
                ((DrawFeature) drawFeatures[i]).draw(bufferedGraphics, agent, x, y);
            }
        }
    }

    /**
     * Returns the agent at the given pixel in this view.
     * 
     * @param pixelX
     *            the horizontal pixel location
     * @param pixelY
     *            the vertical pixel location
     * @return the coordinate at pixel
     */
    public Coordinate getCoordinateAtPixel(int pixelX, int pixelY) {
        // find pixel coord in terms of scape coord
        double scale = scale();
        double transX = ((double) pixelX / scale);
        double transY = ((double) pixelY / scale);
        return new Coordinate2DContinuous(transX, transY);
    }

    /**
     * Returns the agent at the given pixel in this view.
     * 
     * @param pixelX
     *            the horizontal pixel location
     * @param pixelY
     *            the vertical pixel location
     * @return the agent at pixel
     */
    public Agent getAgentAtPixel(int pixelX, int pixelY) {
        return getScape().findNearest(getCoordinateAtPixel(pixelX, pixelY), null, true, Double.MAX_VALUE);
    }

    /**
     * Override addNotify to build buffer.
     */
    public void addNotify() {
        super.addNotify();
        while (bufferedGraphics == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.PanelView#getPreferredSizeWithin(java.awt.Dimension)
     */
    public Dimension getPreferredSizeWithin(Dimension d) {
        int protoSize = ((LocatedAgent) getScape().getPrototypeAgent()).getAgentSize();
        int xAgentsPer = (int) ((Coordinate2DContinuous) getScape().getExtent()).getXValue() / protoSize;
        int xAgentSize = (int) (d.getWidth() / xAgentsPer);
        int yAgentsPer = (int) ((Coordinate2DContinuous) getScape().getExtent()).getYValue() / protoSize;
        int yAgentSize = (int) (d.getHeight() / yAgentsPer);
        nominalAgentSize = Math.max(Math.min(xAgentSize, yAgentSize), 1);
        return new Dimension(nominalAgentSize * xAgentsPer, nominalAgentSize * yAgentsPer);
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.BufferView#setBounds(int, int, int, int)
     */
    public synchronized void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        //Ignore intial sizing
        if ((getScape() != null) && (scape.isInitialized())) {
            int protoSize = ((LocatedAgent) getScape().getPrototypeAgent()).getAgentSize();
            int xAgentsPer = (int) ((Coordinate2DContinuous) getScape().getExtent()).getXValue() / protoSize;
            int xAgentSize = (width / xAgentsPer);
            int yAgentsPer = (int) ((Coordinate2DContinuous) getScape().getExtent()).getYValue() / protoSize;
            int yAgentSize = (height / yAgentsPer);
            nominalAgentSize = Math.min(xAgentSize, yAgentSize);
            buildGraphicsBuffer();
            updateScapeGraphics();
            repaint();
        }
    }

    /**
     * Notifies the view that the scape has removed it. Removes the customizer.
     * 
     * @param scapeEvent
     *            the scape removed notification event
     * @throws TooManyListenersException
     *             the too many listeners exception
     */
    public void scapeAdded(ScapeEvent scapeEvent) throws TooManyListenersException {
        super.scapeAdded(scapeEvent);
        //setPreferredSize(getPreferredSizeWithin(new Dimension(700, 700)));
        // this assumes build() is called before the view is added to the scape
        setPreferredSize(getPreferredSizeWithin(getPreferredSize()));
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

    /* (non-Javadoc)
     * @see javax.swing.JComponent#getPreferredSize()
     */
    public Dimension getPreferredSize() {
        return preferredSize;
    }

    /* (non-Javadoc)
     * @see javax.swing.JComponent#setPreferredSize(java.awt.Dimension)
     */
    public void setPreferredSize(Dimension preferredSize) {
        this.preferredSize = preferredSize;
    }

    /**
     * Gets the nominal agent size.
     * 
     * @return the nominal agent size
     */
    public int getNominalAgentSize() {
        return nominalAgentSize;
    }

    /**
     * Sets the nominal agent size.
     * 
     * @param nominalAgentSize
     *            the new nominal agent size
     */
    public void setNominalAgentSize(int nominalAgentSize) {
        this.nominalAgentSize = nominalAgentSize;
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
     * @param agentColorFeature
     *            the color feature, whose object is assumed to be a agent
     *            populating this lattice
     */
    public void setPrimaryAgentColorFeature(ColorFeature agentColorFeature) {
        this.agentColorFeature = agentColorFeature;
    }

    /**
     * Returns the color feature that will be used for determining agent color.
     * The default color feature is simply the getColor() method of the agent.
     * 
     * @return the agent color feature
     */
    public ColorFeature getAgentColorFeature() {
        return agentColorFeature;
    }

    /**
     * Set the color feature that will be used for determining agent color. The
     * default color feature is simply the getColor() method of the agent.
     * 
     * @param agentColorFeature
     *            the color feature, whose object is assumed to be a agent
     *            populating this lattice
     */
    public void setAgentColorFeature(ColorFeature agentColorFeature) {
        this.agentColorFeature = agentColorFeature;
    }

    /**
     * Returns the image feature that will be used for determining agent color.
     * 
     * @return the agent image feature
     */
    public ImageFeature getAgentImageFeature() {
        return agentImageFeature;
    }

    /**
     * Set the image feature that will be used for determining agent color. The
     * default image feature is simply the getColor() method of the agent.
     * 
     * @param agentImageFeature
     *            the color feature, whose object is assumed to be a agent
     *            populating this lattice
     */
    public void setAgentImageFeature(ImageFeature agentImageFeature) {
        this.agentImageFeature = agentImageFeature;
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.AgentView#drawSelectedAgent(java.awt.Graphics, org.ascape.model.LocatedAgent)
     */
    public synchronized void drawSelectedAgent(Graphics g, LocatedAgent a) {
        Coordinate2DContinuous origin = (Coordinate2DContinuous) a.getCoordinate();
        double scale = scale();
        int pixelX = (int) (scale * origin.getXValue());
        int pixelY = (int) (scale * origin.getYValue());
        // translate to new temporary origin
        g.translate(pixelX - ((a.getAgentSize() / 2) + 8), pixelY - ((a.getAgentSize() / 2) + 8));
        g.setColor(Color.black);
        DrawSymbol.DRAW_OVAL_2.draw(g, null, a.getAgentSize() + 16, a.getAgentSize() + 16);
        g.setColor(Color.yellow);
        g.translate(2, 2);
        DrawSymbol.DRAW_OVAL_2.draw(g, null, a.getAgentSize() + 12, a.getAgentSize() + 12);
        // translate back to normal origin
        g.translate(-pixelX + (a.getAgentSize() / 2) + 6, -pixelY + (a.getAgentSize() / 2) + 6);
//        if ((drawSelectedNeighbors) && ((getAgentCustomizer() != null) && ((MomentumAgent) getAgentCustomizer().getAgent() != null))) {
//            drawNeighborsFor(g, a);
//        }
    }

//    protected void drawNeighborsFor(Graphics g, Agent agent) {
//        System.out.println("trying to draw neighbors");
//        //bufferedGraphics.drawString(Integer.toString(((SugarAgent) occupant).getVision()), x * agentSize, y * agentSize);
////        int td = agentSize + borderSize;
//        MomentumAgent momentumAgent = (MomentumAgent) agent;
//        int td = momentumAgent.getAgentSize();
//        g.setColor(Color.black);
//        List neighbors = new LinkedList(momentumAgent.getScape().findNeighbors());
//        neighbors.removeAll(getScape().findWithin((MomentumAgent) agent, false, 1.0));
//        for (int i = 0; i < neighbors.size(); i++) {
//            if (((MomentumAgent) neighbors.get(i)).getCoordinate() != null) {
//                int dx = (int) (((Coordinate2DContinuous) ((MomentumAgent) neighbors.get(i)).getCoordinate()).getXValue() - ((Coordinate2DContinuous) ((MomentumAgent) agent).getCoordinate()).getXValue());
//                int dy = (int) (((Coordinate2DContinuous) ((MomentumAgent) neighbors.get(i)).getCoordinate()).getYValue() - ((Coordinate2DContinuous) ((MomentumAgent) agent).getCoordinate()).getYValue());
//                //int x2 = ((Coordinate2DDiscrete) neighbors[i].getCoordinate()).getXValue();
//                //int y2 = ((Coordinate2DDiscrete) neighbors[i].getCoordinate()).getYValue();
//                g.drawLine(td / 2 - 1, td / 2 - 1, dx * td + td / 2 - 1, dy * td + td / 2 - 1);
//            }
//        }
//    }

    /**
 * Checks if is draw selected neighbors.
 * 
 * @return true, if is draw selected neighbors
 */
public boolean isDrawSelectedNeighbors() {
        return drawSelectedNeighbors;
    }

    /**
     * Sets the draw selected neighbors.
     * 
     * @param drawSelectedNeighbors
     *            the new draw selected neighbors
     */
    public void setDrawSelectedNeighbors(boolean drawSelectedNeighbors) {
        this.drawSelectedNeighbors = drawSelectedNeighbors;
    }
}
