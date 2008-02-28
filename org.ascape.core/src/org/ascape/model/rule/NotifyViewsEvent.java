/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */
package org.ascape.model.rule;

import org.ascape.model.Agent;
import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;

/*
 * User: Miles Parker
 * Date: Oct 7, 2005
 * Time: 5:44:13 PM
 */

/**
 * The Class NotifyViewsEvent.
 */
public class NotifyViewsEvent extends PropogateScapeOnly {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * The event.
     */
    private ScapeEvent event;

    /**
     * Instantiates a new notify views event.
     * 
     * @param event
     *            the event
     */
    public NotifyViewsEvent(ScapeEvent event) {
        super("Notify Views id: " + event);
        this.event = event;
    }

    /**
     * Notify all views of state update.
     * 
     * @param agent
     *            the agent (scape) to notify veiws
     * @see org.ascape.model.Scape#notifyViews
     */
    public void execute(Agent agent) {
        //if (!((((Scape) agent).getPrototypeAgent()) instanceof AgentScape)) {
        ((Scape) agent).notifyViews(event);
        super.execute(agent);
        //}
    }
}
