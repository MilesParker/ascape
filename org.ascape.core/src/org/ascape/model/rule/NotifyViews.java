/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.rule;

import org.ascape.model.Agent;
import org.ascape.model.Scape;

/**
 * A rule causing the target to notify its views that an update has occured.
 * 
 * @author Miles Parker
 * @version 1.0.1
 * @history 1.0.0 ~9/~/98
 * @history 1.0.1 3/19/99 renamed from NotifyListener
 * @since 1.0
 */
public class NotifyViews extends PropogateScapeOnly {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * The id.
     */
    private int id;

    /**
     * Instantiates a new notify views.
     * 
     * @param id
     *            the id
     */
    public NotifyViews(int id) {
        super("Notify Views id: " + id);
        this.id = id;
    }

    /**
     * Notify all views of state update.
     * 
     * @param agent
     *            the agent (scape) to notify veiws
     * @see Scape#notifyViews
     */
    public void execute(Agent agent) {
        //if (!((((Scape) agent).getPrototypeAgent()) instanceof AgentScape)) {
        ((Scape) agent).notifyViews(id);
        super.execute(agent);
        //}
    }

    /**
     * Only scapes can have views.
     */
    //public boolean isScapeOnly() {
    //    return true;
    //}
}
