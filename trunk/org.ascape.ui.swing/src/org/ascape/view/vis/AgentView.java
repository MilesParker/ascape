/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.view.vis;

import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.TooManyListenersException;
import java.util.Vector;

import javax.swing.SwingUtilities;

import org.ascape.model.Agent;
import org.ascape.model.LocatedAgent;
import org.ascape.model.event.ScapeEvent;
import org.ascape.util.VectorSelection;
import org.ascape.util.vis.ColorFeature;
import org.ascape.util.vis.DrawFeature;
import org.ascape.util.vis.DrawFeatureSelection;
import org.ascape.view.custom.AgentCustomizer;
import org.ascape.view.custom.AgentCustomizerPanel;
import org.ascape.view.custom.ViewCustomizer;

/**
 * A generic base class for views that draw some kind of spatial view of a group
 * of cells. Cell views have a default draw feature that draws a background for
 * the cell, using the cell color feature.
 * 
 * @author Miles Parker, Josh Miller
 * @version 3.0d
 * @history 3.0 First in
 * @since 1.0
 */
public abstract class AgentView extends BufferView implements Observer {

    /**
     * Has an update all been requested?.
     */
    protected boolean updateAllRequested;

    /**
     * The panel responsible for customizing this view.
     */
    private ViewCustomizer viewCustomizer;

    /**
     * The panel responsible for customizing a selected cell.
     */
    protected AgentCustomizer agentCustomizer;

    /**
     * The color feature used to set the cell draw color.
     */
    protected ColorFeature agentColorFeature;

    /**
     * Any draw features specific to this view.
     */
    private Vector viewDrawFeatures;

    /**
     * This view's draw features combined with the Scape's features.
     */
    private Vector allDrawFeatures;

    /**
     * The draw features that have been selected to draw.
     */
    protected VectorSelection drawSelection;

    /**
     * A delegate keeping track of observers of draw features.
     */
    private DrawFeatureObservable drawFeatureObservable = new DrawFeatureObservable();

    /**
     * The clear background automatically.
     */
    protected boolean clearBackgroundAutomatically = true;

    /**
     * The clear background next.
     */
    private boolean clearBackgroundNext = false;

    /**
     * The mouse dragged.
     */
    private boolean mouseDragged;

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
     *            the name
     */
    public AgentView(String name) {
        super(name);
        viewDrawFeatures = new Vector();
        addMouseListener(new MouseAdapter() {
            private static final long DWELL_THRESHOLD = 300;
            private long mousePressedWhen;

			public void mousePressed(MouseEvent e) {
                //Remove for applet only
                if (SwingUtilities.isLeftMouseButton(e)) {
                    mousePressedWhen = e.getWhen();
                    mouseDragged = false;

                    if (e.isAltDown() && (!e.isControlDown())) {
                        Agent candidateAgent = getAgentAtPixel(e.getX(), e.getY());
                        //toggle on if not selected, off if allready selected
                        if ((candidateAgent != null) && ((agentCustomizer == null) || (agentCustomizer.getAgent() != candidateAgent))) {
                            displayAgentCustomizer(candidateAgent);
                            if (!e.isShiftDown()) {
                                agentCustomizer.setFocus(AgentCustomizer.FOCUS_PRIMARY);
                            } else {
                                agentCustomizer.setFocus(AgentCustomizer.FOCUS_HOSTED);
                            }
                        } else {
                            removeAgentCustomizer();
                        }
                        notifyScapeUpdated();
                        repaint();
                    } else if (!e.isShiftDown()) {
                        if (e.isControlDown()) {
                            displayCustomizer();
                        } else if (e.getClickCount() == 2) {
                            displayCustomizer();
                        }
                    }
                }
            }

			public void mouseReleased(MouseEvent e) {
                if (!mouseDragged &&
                        SwingUtilities.isLeftMouseButton(e) &&
                        !e.isShiftDown() && !e.isAltDown() && !e.isControlDown() && (e.getClickCount() == 1)) {
                    long mouseReleasedWhen = e.getWhen();
    				long whenDiff = mouseReleasedWhen - mousePressedWhen;

                    // Use slow and fast mouse click (or touch-panel tap) to bring up the Agent Inspector
                    // (AgentCustomizer) and Draw Features (ViewCustomizer), respectively. Useful for interaction
                    // via the touch-panel screens of the VT tiled display cluster.
                    if (whenDiff > DWELL_THRESHOLD) { // slow tap
                        Agent candidateAgent = getAgentAtPixel(e.getX(), e.getY());
                        //toggle on if not selected, off if already selected
                        if ((candidateAgent != null) && ((agentCustomizer == null) || (agentCustomizer.getAgent() != candidateAgent))) {
                            displayAgentCustomizer(candidateAgent);
                            agentCustomizer.setFocus(AgentCustomizer.FOCUS_PRIMARY);
                        } else {
                            removeAgentCustomizer();
                        }
                        notifyScapeUpdated();
                        repaint();
                    } else { // quick tap
                        displayCustomizer();
                    }
                }
			}
        });
        addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {
                mouseDragged = true;
                if (e.isAltDown()) {
                    if (e.getX() >= 0 && e.getX() < AgentView.this.getSize().width && e.getY() >= 0 && e.getY() < AgentView.this.getSize().height) {
                        displayAgentCustomizer(getAgentAtPixel(e.getX(), e.getY()));
                    }
                    if (!e.isShiftDown()) {
                        agentCustomizer.setFocus(AgentCustomizer.FOCUS_PRIMARY);
                    } else {
                        agentCustomizer.setFocus(AgentCustomizer.FOCUS_HOSTED);
                    }
                }
            }

            public void mouseMoved(MouseEvent e) {
            }
        });
    }

    /**
     * Called to create and layout the components of the component view, once
     * the view's scape has been created.
     */
    public void build() {
        drawSelection = new DrawFeatureSelection(scape);
        scape.getDrawFeaturesObservable().addObserver(drawSelection);
        getScape().getDrawFeaturesObservable().addObserver(this);
        updateDrawFeatures();
        drawFeatureObservable.setChanged();
        drawFeatureObservable.notifyObservers();
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.PanelView#updateScapeGraphics()
     */
    public void updateScapeGraphics() {
        super.updateScapeGraphics();
        if (isClearBackground() || clearBackgroundNext) {
            // paint background
            clearBackground();
            clearBackgroundNext = false;
        }
    }

    /**
     * Clear background.
     */
    private void clearBackground() {
        if (bufferedGraphics != null) {
            bufferedGraphics.setColor(this.getBackground());
            bufferedGraphics.fillRect(0, 0, getSize().width, getSize().height);
        }
    }

    /**
     * Called immediatly after the scape is started.
     * 
     * @param scapeEvent
     *            the scape event
     * @throws TooManyListenersException
     *             the too many listeners exception
     */
    public void scapeAdded(ScapeEvent scapeEvent) throws TooManyListenersException {
        super.scapeAdded(scapeEvent);
        requestUpdateAll();
    }

    /**
     * Called immediatly after the scape is started.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void scapeStarted(ScapeEvent scapeEvent) {
        super.scapeStarted(scapeEvent);
        requestUpdateAll();
    }

    /**
     * Returns an observable delegate that notifies users of draw features that
     * a change has occurred. If you need to know when a change in draw features
     * occur, implement observer in the appropriate class and add it to the
     * Observerable this method returns.
     * 
     * @return the draw features observable
     */
    protected Observable getDrawFeaturesObservable() {
        return drawFeatureObservable;
    }

    /**
     * Adds the provided draw feature to this scape.
     * 
     * @param feature
     *            the feature
     * @see org.ascape.util.vis.DrawFeature
     */
    public void addDrawFeature(DrawFeature feature) {
        //Simple linear search...
        //todo, replace with hashmap mechanism
        for (Iterator iterator = viewDrawFeatures.iterator(); iterator.hasNext();) {
            DrawFeature drawFeature = (DrawFeature) iterator.next();
            if (drawFeature.getName().equals(feature.getName())) {
                //ignore, don't add feature with same name twice.
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
     *            the draw feature to be removed
     * @return returns true if successful. False, otherwise.
     */
    public boolean removeDrawFeature(DrawFeature feature) {
        DrawFeature found = null;
        //todo, replace with hashmap mechanism
        for (Iterator iterator = viewDrawFeatures.iterator(); iterator.hasNext();) {
            DrawFeature drawFeature = (DrawFeature) iterator.next();
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
     * Returns, as a vector, the draw features available for interpretation of
     * members of this scape.
     * 
     * @return the draw features
     * @see org.ascape.util.vis.DrawFeature
     */
    public Vector getDrawFeatures() {
        return allDrawFeatures;
    }

    /**
     * Update draw features.
     */
    private void updateDrawFeatures() {
        allDrawFeatures = new Vector(scape.getDrawFeatures());
        allDrawFeatures.addAll(viewDrawFeatures);
        drawSelection.setVector(allDrawFeatures);
        requestUpdateAll();
        getScape().requestUpdate();
    }


    /**
     * Sets the customize agent.
     * 
     * @param agent
     *            the new customize agent
     */
    public void setCustomizeAgent(Agent agent) {
        if (agentCustomizer != null) {
            agentCustomizer.setAgent(agent);
        }
    }

    /**
     * Gets the agent customizer.
     * 
     * @return the agent customizer
     */
    public AgentCustomizer getAgentCustomizer() {
        return agentCustomizer;
    }

    /**
     * Sets the agent customizer.
     * 
     * @param customizer
     *            the new agent customizer
     */
    public void setAgentCustomizer(AgentCustomizer customizer) {
        this.agentCustomizer = customizer;
    }

    /**
     * Returns the selection of draw features for this view.
     * 
     * @return the draw selection
     */
    public VectorSelection getDrawSelection() {
        if (drawSelection == null) {
            throw new RuntimeException("Error calling getDrawSelection(). You must add the view to a scape before accessing its draw selection.");
        }
        return drawSelection;
    }

    /**
     * Builds the view. Sets the color feature defaults, adds the default oval
     * and fill draw features, and selects the draw agents as ovals feature.
     */
    //public abstract void build();

    /**
     * Displays a customizer for altering the settings for this view.
     * May be a no op if (in the case of no swing support) a customizer isn't available for the environment.
     */
    public void displayCustomizer() {
        if (viewCustomizer == null) {
            viewCustomizer = new ViewCustomizer(this);
            getScape().addView(viewCustomizer);
            getViewFrame().setTitle(this.getName() + " Select Features");
        } else {
            viewCustomizer.selected();
        }
    }

    /**
     * Removes the customizer for this view.
     */
    public void removeCustomizer() {
        if (viewCustomizer != null) {
            viewCustomizer.getViewFrame().dispose();
            viewCustomizer = null;
        }
    }

    /**
     * Creates the agent customizer.
     * 
     * @return the agent customizer
     */
    protected AgentCustomizer createAgentCustomizer() {
        return new AgentCustomizer(this);
    }

    /**
     * Constructs an agent panel to use to represent Agent data in the
     * customizer. Override this method to create custom implementations of
     * AgentCustomizerPanel.
     * 
     * @return a new instance of AgentCusotmizerPanel.
     */
    public AgentCustomizerPanel createAgentCustomizerPanel() {
        return new AgentCustomizerPanel();
    }

    /**
     * Displays a window for altering the setting for this view. May be a no op
     * if (in the case of no swing support) a viewCustomizer isn't available for
     * the environment.
     * 
     * @param agent
     *            the agent
     */
    public void displayAgentCustomizer(Agent agent) {
        if (agent != null) {
            if ((agentCustomizer == null) || (agentCustomizer.getAgent() != agent)) {
                if (agentCustomizer == null) {
                    agentCustomizer = createAgentCustomizer();
                }
                if (agentCustomizer.getAgent() == null) {
                    getScape().addView(agentCustomizer);
                }
                agentCustomizer.setAgent(agent);
                agentCustomizer.getViewFrame().toFront();
                updateScapeGraphics();
                repaint();
            }
        } else {
            removeAgentCustomizer();
        }
    }

    /**
     * Removes the agent inspector from this view.
     */
    public void removeAgentCustomizer() {
        if (agentCustomizer != null) {
            if (agentCustomizer.getViewFrame() != null) {
                agentCustomizer.getViewFrame().dispose();
            }
            agentCustomizer.setAgent(null);
            repaint();
        }
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
     *            the color feature, whose object is assumed to be an agent
     *            populating this lattice
     */
    public void setAgentColorFeature(ColorFeature agentColorFeature) {
        this.agentColorFeature = agentColorFeature;
    }

    /**
     * Request clear background.
     */
    private void requestClearBackground() {
        clearBackgroundNext = true;
    }

    /**
     * Requests that all cells be updated, irregardless of wether they have
     * requested it or not. Should be called whenever a change that can affect
     * the view as a whole, such as a change in draw selection, is made. is
     * made.
     */
    public void requestUpdateAll() {
        updateAllRequested = true;
        requestClearBackground();
    }

    /**
     * Checks if is clear background.
     * 
     * @return true, if is clear background
     */
    public boolean isClearBackground() {
        return clearBackgroundAutomatically;
    }

    /**
     * Sets the clear background.
     * 
     * @param clearBackground
     *            the new clear background
     */
    public void setClearBackground(boolean clearBackground) {
        this.clearBackgroundAutomatically = clearBackground;
    }

//    public void setVisible(boolean visible) {
//        super.setVisible(visible);
//        requestUpdateAll();
//    }

    /**
 * Returns the cell at the given pixel in this view.
 * 
 * @param x
 *            the horizontal pixel location
 * @param y
 *            the vertical pixel location
 * @return the agent at pixel
 */
    public Agent getAgentAtPixel(int x, int y) {
        return null;
    }

    /**
     * Draws a marker for the provided selected agent.
     * 
     * @param g
     *            the graphics context to draw to
     * @param a
     *            the agent to draw
     */
    protected void drawSelectedAgent(Graphics g, LocatedAgent a) {
    }

    /**
     * Draws a marker for the agent, if any, currently being viewed by the agent
     * customizer.
     * 
     * @param g
     *            the graphics context to draw to
     */
    protected void drawSelectedAgent(Graphics g) {
        if ((agentCustomizer != null) && (agentCustomizer.getAgent() != null)) {
            LocatedAgent a = (LocatedAgent) agentCustomizer.getAgent();
            if (a != null) {
                drawSelectedAgent(g, a);
            } else {
//System.out.println("calling repaint");
//repaint();
            }
        }
    }

    /* (non-Javadoc)
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
        updateDrawFeatures();
    }

    /**
     * Paints the canvas. Ordinarily, you should not do painting here, but into
     * the buffer in the scape updated method. Paint draws the buffer into the
     * canvas. Here, we are just painting the selected cell, if any, directly
     * into the graphics and calling the super method.
     * 
     * @param g
     *            the g
     */
    public void paintComponent(Graphics g) {
        //todo for some reason we will occasionally see a selected agent painted one step back; forcing a repaint puts the selected cell in the right palce
        if (bufferedImage != null) {
            if (g.drawImage(bufferedImage, 0, 0, this)) {
                drawSelectedAgent(g);
                //This always returns in any environment we've tested, but needs a fix if not..
                getDelegate().viewPainted();
            } else {
                System.out.println("Warning: Unable to complete view draw in AgentView.paintComponent()");
            }
        }
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
        out.writeObject(viewCustomizer);
        out.writeObject(agentCustomizer);
        out.writeObject(agentColorFeature);
        out.writeObject(drawSelection);
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
        viewCustomizer = (ViewCustomizer) in.readObject();
        agentCustomizer = (AgentCustomizer) in.readObject();
        agentColorFeature = (ColorFeature) in.readObject();
        drawSelection = (VectorSelection) in.readObject();
    }
}

/**
 * Just a class for a delegated proxy for draw features.
 */
class DrawFeatureObservable extends Observable implements Serializable {

    /**
     * Have to provide this silly method because set changed is protected for some reason.
     */
    public void setChanged() {
        super.setChanged();
    }
}
