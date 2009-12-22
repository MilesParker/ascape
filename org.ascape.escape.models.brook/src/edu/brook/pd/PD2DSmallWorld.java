/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.pd;

import org.ascape.model.HostCell;
import org.ascape.model.Scape;
import org.ascape.model.space.Array2DVonNeumann;
import org.ascape.model.space.Coordinate2DDiscrete;

public class PD2DSmallWorld extends PD2D {

    /**
     * 
     */
    private static final long serialVersionUID = 5158669672840347959L;

    public void createScape() {
        super.createScape();
        remove(lattice);
        remove(agents);
        // Create a 2D lattice (we'll be using von Neumann Geometry.)
        // lattice = new Scape(new Array2DSmallWorld());
        lattice = new Scape(new Array2DVonNeumann());
        // Use the generic host cell as the agent that will fill the lattice.
        lattice.setPrototypeAgent(new HostCell());
        // Important: Clear rules so the default iterate rule won't be used for
        // the host cell. The model will run faster, since iterate won't be
        // called
        // on each lattice cell.
        lattice.getRules().clear();
        // Set the size by setting the coordinate at the lattices largest
        // extent.
        lattice.setExtent(new Coordinate2DDiscrete(latticeWidth, latticeHeight));
        // Set the lattice to expect cells to request updates only when
        // neccesary.
        // This greatly enhances perfomance, but requires us to manually request
        // view
        // updates if a change occurs to a lattice cell. In this case, the only
        // change
        // that might occur to a cell is for an agent to move in or out of it,
        // because
        // an agents strategy does not change, and that is the only aspect of
        // agent
        // state that the view reflects. We do not need to request updates on
        // agent
        // movement, since that is handled automatically by the framework.
        // lattice.setCellsRequestUpdates(true);

        // Create an instance of Player that we can set state for...
        Player player = new edu.brook.pd.Player();
        // Set the host scape for the prototype agent,
        // So that all 'agents' members know where they live.
        player.setHostScape(lattice);
        // Create a scape for the agents (players) that will live on the
        // lattice.
        // Vectors are appropriate whenever the size of a population will
        // change.
        agents = new Scape();
        // Give the scape a short, descriptive name
        agents.setName("Players");
        // ...and make the player the prototype that will be used to fill the
        // initial scape.
        agents.setPrototypeAgent(player);
        // Tell the agents to execute in by rule order; each rule executes on
        // every agent
        // before the next rule is executed.
        agents.setExecutionOrder(Scape.RULE_ORDER);

        // Finally, we need to add our vectors to the root we created at the
        // beginning.
        // The root scape is the parent and controls its member scapes.
        add(lattice);
        add(agents);
    }
}
