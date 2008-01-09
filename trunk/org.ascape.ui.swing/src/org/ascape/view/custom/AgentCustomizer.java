/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.view.custom;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.swing.BorderFactory;
import javax.swing.JInternalFrame;
import javax.swing.border.BevelBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.ascape.model.Agent;
import org.ascape.model.CellOccupant;
import org.ascape.model.HostCell;
import org.ascape.model.LocatedAgent;
import org.ascape.model.event.ScapeEvent;
import org.ascape.view.vis.AgentView;

/**
 * A frame (modeless dialog) for interpreting the state of an agent and
 * optionally its hosted agents if any.
 * 
 * @author Miles Parker
 * @version 2.0
 * @history 2.0 renamed to AgentCustomizer from CellCustomizer
 * @history 2.0 1/2/02 significant renaming, refactoring and rationalization
 * @history 1.9.3 8/9/01 isolated view for subclasses
 * @history 1.9.2 2/1/01 fixed value parameter reversal
 * @history 1.9 4/24/00 created
 * @since 1.9
 */
public class AgentCustomizer extends BaseCustomizer implements Externalizable {

    /**
     * The Constant FOCUS_PRIMARY.
     */
    public final static int FOCUS_PRIMARY = -1;

    /**
     * The Constant FOCUS_HOSTED.
     */
    public final static int FOCUS_HOSTED = 1;

    /**
     * The focus.
     */
    private int focus = FOCUS_PRIMARY;

    /**
     * The view.
     */
    private AgentView view;

    /**
     * The current (and last) netscape Java release is flawed.
     */
//    private boolean netscapeFailure;
//
//    private static String netscapeMsg = "Sorry, this hosted inspector dialog does not work in your environment. " +
//        "The most likely reason for this is a flaw in Netscape's implementation " +
//        "of Java security and introspection. If you want to use this feature, try using " +
//        "the JRE plug-in at http://java.sun.com/products/jdk/1.2/jre, or another java-capable browser like Internet Explorer.";

    private AgentCustomizerPanel primaryCustomizer = new AgentCustomizerPanel();
    
    /**
     * The hosted customizer.
     */
    private AgentCustomizerPanel hostedCustomizer = new AgentCustomizerPanel();

    /**
     * Constructs the customizer.
     */
    public AgentCustomizer() {
        super();
        setName("Agent Inpector");
    }

    /**
     * Constructs the frame. Note that the view is purposely downclassed because
     * we have an AWT based view group and a Swing based view group, both of
     * which can use this class.
     * 
     * @param view
     *            the chart view being edited.
     */
    public AgentCustomizer(final org.ascape.view.vis.AgentView view) {
        this();
        this.view = view;
    }

    /**
     * Is this customizer supporting a hosted agent mode; i.e displaying a
     * 'hosted' hosted alongside the hosting agent? Overide to return false if
     * you do not want the hosted agent displayed. Otherwise, the primary is
     * dispalyed automatically.
     * 
     * @return true, if is hosted agent mode
     */
    public boolean isHostedAgentMode() {
        return ((view instanceof org.ascape.view.vis.HostedAgentView) && (view.getScape().getPrototypeAgent() instanceof HostCell));
    }

    /**
     * Is this customizer supporting a primary agent mode; i.e displaying the
     * primary agent. (That is, the "Cell".) True by default. Overide to return
     * false if you do not want the primary agent displayed.
     * 
     * @return true, if is primary agent mode
     */
    public boolean isPrimaryAgentMode() {
        return true;
    }

    /**
     * Create and place the customizer's components. Introspects the model to
     * find setting's primaryAccessors, and adds descriptions and text fields to
     * the customizer for them.
     */
    public void build() {
        super.build();
        if (getViewFrame() != null) {
            getViewFrame().setTitle("Agent Inspector");
            if (getViewFrame().getFrameImp() instanceof JInternalFrame) {
                ((JInternalFrame) getViewFrame().getFrameImp()).addInternalFrameListener(new InternalFrameAdapter() {
                    public void internalFrameClosing(InternalFrameEvent e) {
                        super.internalFrameClosing(e);
                        view.removeAgentCustomizer();
                    }
                });
            } else {
                ((Window) getViewFrame().getFrameImp()).addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        super.windowClosing(e);
                        view.removeAgentCustomizer();
                    }
                });
            }
        }

        contentPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        if (isPrimaryAgentMode() && isHostedAgentMode()) {
            contentPanel.setLayout(new GridLayout(1, 2));
            setPreferredSize(new Dimension(360, 180));
        } else if (isPrimaryAgentMode() || isHostedAgentMode()) {
            setPreferredSize(new Dimension(180, 180));
        } else {
            setPreferredSize(new Dimension(30, 180));
        }
        primaryCustomizer = view.createAgentCustomizerPanel();
        if (isPrimaryAgentMode()) {
            primaryCustomizer.build();
            primaryCustomizer.getMainPanel().setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
            contentPanel.add(primaryCustomizer.getMainPanel());
        }

        if (isHostedAgentMode()) {
            hostedCustomizer = view.createAgentCustomizerPanel();
            hostedCustomizer.build();
            hostedCustomizer.getMainPanel().setBorder(BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED), BorderFactory.createEmptyBorder(6, 6, 6, 6)));
            contentPanel.add(hostedCustomizer.getMainPanel());
        }
        determineAgent();
    }

    /**
     * Gets the focus.
     * 
     * @return the focus
     */
    public int getFocus() {
        return focus;
    }

    /**
     * Sets the focus.
     * 
     * @param focus
     *            the new focus
     */
    public void setFocus(int focus) {
        this.focus = focus;
    }

    /**
     * Determine agent.
     */
    private void determineAgent() {
        if (focus == FOCUS_PRIMARY) {
            if (primaryCustomizer.getAgent() != null) {
                if (primaryCustomizer.getAgent() instanceof HostCell) {
                    hostedCustomizer.setAgent((Agent) ((HostCell) primaryCustomizer.getAgent()).getOccupant());
                }
            }
        } else {
            //Focus == hostedCustomizer.getAgent()
            if (hostedCustomizer.getAgent() != null) {
                primaryCustomizer.setAgent(((CellOccupant) hostedCustomizer.getAgent()).getHostCell());
                if (primaryCustomizer.getAgent() == null) {
                    primaryCustomizer.setAgent(primaryCustomizer.getLastAgent());
                    hostedCustomizer.setAgent((Agent) ((HostCell) primaryCustomizer.getAgent()).getOccupant());
                }
                //The hostedCustomizer.getAgent() might be dead at this point or removed from the scape,
                //in which case primaryCustomizer.getAgent() will return null.
                //The primaryCustomizer.getAgent() == null code below will take care of this situation.
            } else {
                if (primaryCustomizer.getAgent() != null) {
                    if (primaryCustomizer.getAgent() instanceof HostCell) {
                        hostedCustomizer.setAgent((Agent) ((HostCell) primaryCustomizer.getAgent()).getOccupant());
                    }
                }
            }
            if (primaryCustomizer.getAgent() == null) {
                primaryCustomizer.setAgent(primaryCustomizer.getLastAgent());
                if (primaryCustomizer.getAgent() != null) {
                    if (primaryCustomizer.getAgent() instanceof HostCell) {
                        hostedCustomizer.setAgent((Agent) ((HostCell) primaryCustomizer.getAgent()).getOccupant());
                    }
                }
            }
        }
        if (isPrimaryAgentMode()) {
            if (primaryCustomizer.getLastAgent() != primaryCustomizer.getAgent()) {
                primaryCustomizer.onAgentChange();
            }
            primaryCustomizer.updateColor();
            getContentPanel().setBackground(primaryCustomizer.getColor());
            primaryCustomizer.setLastAgent(primaryCustomizer.getAgent());
        }
        if (hostedCustomizer.getLastAgent() != hostedCustomizer.getAgent()) {
            if (isHostedAgentMode()) {
                hostedCustomizer.onAgentChange();
            }
        }
        if (isHostedAgentMode()) {
            hostedCustomizer.updateColor();
        }
        hostedCustomizer.setLastAgent(hostedCustomizer.getAgent());
    }

    /**
     * Gets the agent.
     * 
     * @return the agent
     */
    public Agent getAgent() {
        //determineAgent();
        return primaryCustomizer.getAgent();
    }

    /**
     * Sets the agent.
     * 
     * @param agent
     *            the new agent
     */
    public void setAgent(Agent agent) {
        if (getAgent() != null) {
            ((LocatedAgent) getAgent()).requestUpdateNext();
        }
        if (primaryCustomizer.getAgent() == null) {
            setFocus(AgentCustomizer.FOCUS_PRIMARY);
        }
        this.primaryCustomizer.setAgent(agent);
        this.hostedCustomizer.setAgent(null);
        super.setObject(primaryCustomizer.getAgent());
        if (agent != null) {
            determineAgent();
        }
        if (getAgent() != null) {
            ((LocatedAgent) getAgent()).requestUpdateNext();
        }
    }

    /**
     * Sets the object this customizer is modifying.
     * 
     * @param target
     *            the object being customized
     */
    public void setObject(Object target) {
        setAgent((Agent) target);
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.PanelView#updateScapeGraphics()
     */
    public void updateScapeGraphics() {
        determineAgent();
        super.updateScapeGraphics();
    }

    /**
     * Method called once a model is deserialized.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void scapeDeserialized(ScapeEvent scapeEvent) {
        onChangeIterationsPerRedraw();
        getScape().getEnvironment().addView(this);
        if (primaryCustomizer.getAgent() == null) {
            getViewFrame().setVisible(false);
        }
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.PanelView#scapeNotification(org.ascape.model.event.ScapeEvent)
     */
    public void scapeNotification(ScapeEvent scapeEvent) {
        if (scapeEvent.getID() != ScapeEvent.TICK) {
            super.scapeNotification(scapeEvent);
        } else {
            //ignore tick count..causes bad flicker
            notifyScapeUpdated();
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
     * @exception IOException
     *                Includes any I/O exceptions that may occur
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        //System.out.println("writeExternal'ing AgentCustomizer/"+this+"...");
        super.writeExternal(out);
        out.writeInt(focus);
        out.writeObject(view);
        out.writeObject(primaryCustomizer.getAgent());
        out.writeObject(hostedCustomizer.getAgent());
        //System.out.println("Done writeExternal'ing AgentCustomizer/"+this);
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
        focus = in.readInt();
        view = (org.ascape.view.vis.AgentView) in.readObject();
        primaryCustomizer.setAgent((Agent) in.readObject());
        hostedCustomizer.setAgent((Agent) in.readObject());
    }
}
