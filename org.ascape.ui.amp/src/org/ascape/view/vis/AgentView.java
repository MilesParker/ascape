/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.view.vis;

import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import org.ascape.model.Agent;
import org.ascape.model.LocatedAgent;
import org.ascape.util.VectorSelection;
import org.ascape.util.vis.ColorFeature;
import org.ascape.util.vis.ColorFeatureConcrete;
import org.ascape.util.vis.DrawFeature;
import org.ascape.util.vis.DrawFeatureSelection;
import org.ascape.util.vis.IDrawSelection;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Color;



/**
 * A generic base class for views that draw some kind of spatial view of a group of cells. Cell views have a default
 * draw feature that draws a background for the cell, using the cell color feature.
 * 
 * @author Miles Parker, Josh Miller
 * @version 3.0d
 * @history 3.0 First in
 * @since 1.0
 */
public abstract class AgentView extends GraphicsView implements IDrawSelection, Observer {

    private static final long serialVersionUID = 2123024822872459897L;

    /**
     * Any draw features specific to this view.
     */
    private Vector<DrawFeature> viewDrawFeatures;

    /**
     * This view's draw features combined with the Scape's features.
     */
    private Vector<DrawFeature> allDrawFeatures;

    /**
     * The draw features that have been selected to draw.
     */
    protected VectorSelection drawSelection;

    /**
     * A delegate keeping track of observers of draw features.
     */
    private DrawFeatureObservable drawFeatureObservable = new DrawFeatureObservable();

    /**
     * The generic color feature used to set the color for the primary agent type.
     */
    protected ColorFeature agentColorFeature;

    /**
     * Instantiates a new agent view.
     */
    public AgentView() {
        this("Base View");
    }

    /**
     * Instantiates a new agent view.
     * 
     * @param name
     *        the name
     */
    public AgentView(String name) {
        super(name);
        viewDrawFeatures = new Vector<DrawFeature>();
    }

    /**
     * Returns an observable delegate that notifies users of draw features that a change has occurred. If you need to
     * know when a change in draw features occur, implement observer in the appropriate class and add it to the
     * Observerable this method returns.
     * 
     * @return the draw features observable
     */
    protected Observable getDrawFeaturesObservable() {
        return drawFeatureObservable;
    }

    public void createFeatures() {
        if (agentColorFeature == null) {
            agentColorFeature = new ColorFeatureConcrete("Default Agent Color") {
                private static final long serialVersionUID = -3220486698290440285L;

                public Color getColor(Object object) {
                    return (Color) ((Agent) object).getPlatformColor();
                }
            };
        }
    }

    /**
     * Adds the provided draw feature to this scape.
     * 
     * @param feature
     *        the feature
     * @see org.ascape.util.vis.DrawFeature
     */
    public void addDrawFeature(DrawFeature feature) {
        // Simple linear search...
        // todo, replace with hashmap mechanism
        for (DrawFeature drawFeature : viewDrawFeatures) {
            if (drawFeature.getName().equals(feature.getName())) {
                // ignore, don't add feature with same name twice.
                return;
            }
        }
        viewDrawFeatures.addElement(feature);
        updateDrawFeatures();
        drawFeatureObservable.setChanged();
        drawFeatureObservable.notifyObservers();
    }

    /**
     * Removes the provided draw feature.
     * 
     * @param feature
     *        the draw feature to be removed
     * @return returns true if successful. False, otherwise.
     */
    public boolean removeDrawFeature(DrawFeature feature) {
        DrawFeature found = null;
        // todo, replace with hashmap mechanism
        for (DrawFeature drawFeature : viewDrawFeatures) {
            if (drawFeature.getName().equals(feature.getName())) {
                found = feature;
            }
        }
        if (found != null) {
            viewDrawFeatures.removeElement(feature);
            updateDrawFeatures();
            drawFeatureObservable.setChanged();
            drawFeatureObservable.notifyObservers();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns, as a vector, the draw features available for interpretation of members of this scape.
     * 
     * @return the draw features
     * @see org.ascape.util.vis.DrawFeature
     */
    public Vector<DrawFeature> getDrawFeatures() {
        return allDrawFeatures;
    }

    /**
     * Update draw features.
     */
    private void updateDrawFeatures() {
        allDrawFeatures = new Vector<DrawFeature>(getScape().getDrawFeatures());
        allDrawFeatures.addAll(viewDrawFeatures);
        getDrawSelection().setVector(allDrawFeatures);
        getScape().requestUpdate();
    }

    /**
     * Returns the selection of draw features for this view.
     * 
     * @return the draw selection
     */
    public VectorSelection getDrawSelection() {
        if (drawSelection == null) {
            drawSelection = new DrawFeatureSelection(getScape());
            getScape().getDrawFeaturesObservable().addObserver(drawSelection);
            getScape().getDrawFeaturesObservable().addObserver(this);
            updateDrawFeatures();
            drawFeatureObservable.setChanged();
            drawFeatureObservable.notifyObservers();
        }
        return drawSelection;
    }

    /**
     * Builds the view. Sets the color feature defaults, adds the default oval and fill draw features, and selects the
     * draw agents as ovals feature.
     */
    // public abstract void build();
    /**
     * Returns the color feature that will be used for determining agent color. The default color feature is simply the
     * getForegroundColor() method of the agent.
     * 
     * @return the agent color feature
     */
    public ColorFeature getAgentColorFeature() {
        return agentColorFeature;
    }

    /**
     * Set the color feature that will be used for determining agent color. The default color feature is simply the
     * getForegroundColor() method of the agent.
     * 
     * @param agentColorFeature
     *        the color feature, whose object is assumed to be an agent populating this lattice
     */
    public void setAgentColorFeature(ColorFeature agentColorFeature) {
        this.agentColorFeature = agentColorFeature;
    }

    // public void setVisible(boolean visible) {
    // super.setVisible(visible);
    // requestUpdateAll();
    // }

    /**
     * Returns the cell at the given pixel in this view.
     * 
     * @param x
     *        the horizontal pixel location
     * @param y
     *        the vertical pixel location
     * @return the agent at pixel
     */
    public Agent getAgentAtPixel(int x, int y) {
        return null;
    }

    /**
     * Draws a marker for the provided selected agent.
     * 
     * @param g
     *        the graphics context to draw to
     * @param a
     *        the agent to draw
     */
    protected void drawSelectedAgent(Graphics graphics, LocatedAgent a) {
    }

    public Dimension getPreferredSizeWithin(Dimension d) {
        int tempCellSize = calculateAgentSizeForViewSize(d);
        setAgentSize(tempCellSize);
        return calculateViewSizeForAgentSize(d, tempCellSize);
    }

    public Dimension getMiniumSizeWithin(Dimension d) {
        return calculateViewSizeForAgentSize(d, 1);
    }

    /**
     * Calculate view size for agent size.
     * 
     * @param availableSize
     *        TODO
     * @param agentSize
     *        the agent size
     * 
     * @return the dimension
     */
    public Dimension calculateViewSizeForAgentSize(Dimension availableSize, int agentSize) {
        return new Dimension(500, 500);
    }

    /**
     * Calculate agent size for view size.
     * 
     * @param d
     *        the d
     * @return the int
     */
    public int calculateAgentSizeForViewSize(Dimension d) {
        return agentSize;
    }

    /**
     * Returns a one-dimension size of pixels used to represent each cell.
     * 
     * @return the agent size
     */
    public int getAgentSize() {
        return agentSize;
    }

    /**
     * Sets the number of pixels used to represent each cell.
     * 
     * @param cellSize
     *        number of pixels per edge
     */
    public void setAgentSize(int cellSize) {
        this.agentSize = cellSize;
        // if (getScape() != null) {
        // Dimension calculateViewSizeForAgentSize = calculateViewSizeForAgentSize(null, cellSize);
        // // getPanel().setBounds(new Rectangle(0, 0, calculateViewSizeForAgentSize.x,
        // // calculateViewSizeForAgentSize.y));
        // }
    }

    /**
     * Draws a marker for the agent, if any, currently being viewed by the agent customizer.
     * 
     * @param g
     *        the graphics context to draw to
     */
    // protected void drawSelectedAgent(Graphics g) {
    // if ((agentCustomizer != null) && (agentCustomizer.getAgent() != null)) {
    // LocatedAgent a = (LocatedAgent) agentCustomizer.getAgent();
    // if (a != null) {
    // drawSelectedAgent(g, a);
    // } else {
    // //System.out.println("calling repaint");
    // //repaint();
    // }
    // }
    // }
    /*
     * (non-Javadoc)
     * 
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
        updateDrawFeatures();
    }
}

/**
 * Just a class for a delegated proxy for draw features.
 */
class DrawFeatureObservable extends Observable implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4241617900817151329L;

    /**
     * Have to provide this silly method because set changed is protected for some reason.
     */
    public void setChanged() {
        super.setChanged();
    }
}
