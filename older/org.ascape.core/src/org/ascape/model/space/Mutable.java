/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.model.space;


/**
 * A one-dimensional, fixed-size, collection of agents providing services
 * described for space.
 * 
 * @author Miles Parker
 * @version 2.0
 * @history 2.0 11/10/01 added new agent random method
 * @history 2.0 11/5/01 added new agent method
 * @history 1.5 11/30/99 first in
 * @since 1.5
 */
public interface Mutable {

    /**
     * Is a delete sweep needed for this space?.
     * 
     * @return true, if is delete sweep needed
     */
    boolean isDeleteSweepNeeded();

    /**
     * Walks through each agent, deleting it if it has been marked for deletion.
     */
    void deleteSweep();

    /**
     * Removes the supplied object (agent) from this list. The agent is an
     * object for consistency with the java collections api.
     * 
     * @param o
     *            the agent to be removed
     * @return true if the agent was deleted, false otherwise
     */
    boolean remove(Object o);

    /**
     * Creates a new agent in the space by cloning the prototype agent, adding
     * it to an arbitrary place (usually the 'end'), and initializing it.
     * 
     * @return the location
     */
    Location newLocation();

    /**
     * Creates a new agent in this list by cloning the prototype agent, adding
     * it to a random or arbitrary (usually the 'end') place in the list, and
     * initializing it.
     * 
     * @param randomLocation
     *            should the agent be placed in a random location, or in an
     *            arbitrary location?
     * @return the location
     */
    Location newLocation(boolean randomLocation);

    /**
     * Removes all agents from the space.
     */
    void clear();
}
