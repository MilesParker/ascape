/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.rule;

import org.ascape.model.Agent;
import org.ascape.model.Scape;
import org.ascape.model.space.Singleton;

/**
 * A rule that executes on the target scape, all children of the target scape,
 * and (if desired) all members of this or any children of the target scape. Any
 * scape with members active set to false will <i>not</i> be propagated to. If
 * isScapeOnly returns true, the rule will be propogated down to scapes, and
 * will not be executed on any non-scape member cells.
 * 
 * @author Miles Parker
 * @version 1.5
 * @history 1.5 moved isScapeOnly method from Rule
 * @history 1.0 first in
 * @since 1.0
 * @see org.ascape.model.Scape#isMembersActive
 */
public class Propogate extends Rule {

    /**
     * Constructs a Propogate rule.
     * 
     * @param name
     *            the name
     */
    /*public Propogate() {
        super("Unnamed");
    }*/

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a propogate rule with the provided name.
     * It is strongly encouraged to provide a name for all rules.
     * @param name the name of this object
     */
    public Propogate(String name) {
        super(name);
    }

    /**
     * Execute rule on child scapes and (optionally) agents.
     * 
     * @param agent
     *            the target agent
     */
    public void execute(Agent agent) {
        //If the agent is not a scape then obviously we stop propogating
        //(in this case, we would be at the leaf of a non-scape only propogation)
        //We also do not propogate if the agent is an AgentScape because the rule
        //has allready been executed on the agent.
        if ((agent instanceof Scape) && !(((Scape) agent).getSpace() instanceof Singleton)) {
            if (isScapeOnly()) {
                //We are propogating a rule designed for scapes so we don't propogate it down
                //if the scape is composed of non-scapes or inactive ones..
                if ((((Scape) agent).getPrototypeAgent() instanceof Scape) && (((Scape) ((Scape) agent).getPrototypeAgent()).isMembersActive())) {
                    ((Scape) agent).executeOnMembers(this);
                }
            }
            //Not scape only
            else {
                //Don't propgate if members are not active; that is, if the given (Scape) agent is
                //designed for structure, but not behavior.
                if (((Scape) agent).isMembersActive()) {
                    ((Scape) agent).executeOnMembers(this);
                }
            }
        }
    }

    /**
     * Is this rule intended only to be propogated to scapes?.
     * 
     * @return true, if is scape only
     */
    public boolean isScapeOnly() {
        return false;
    }

    /**
     * Returns false. Initialization should not need to be random, since it
     * should not depend on the state of any other agent.
     * 
     * @return true, if is random execution
     */
    public boolean isRandomExecution() {
        return false;
    }
}
