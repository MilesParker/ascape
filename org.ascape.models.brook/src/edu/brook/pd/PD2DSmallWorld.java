/*
 * This software and documentation is subject to, and may be only used in accordance with, the terms and conditions of
 * the license agreement described or set forth in the file titled License.txt.  This software and documentation
 * contains information that is confidential and proprietary to NuTech Solutions, Inc.
 *
 *             Copyright (c) 2000-2004
 *             NuTech Solutions, Inc.
 *
 * Portions of the software are licensed from The Brookings Institution
 *             Copyright (c) 1998-2000
 *            The Brookings Institution
 *           All rights reserved worldwide.
 *
 *
 * U.S. GOVERNMENT END USERS. The software is a "commercial item," as that term is defined in 48 C.F.R. 2.101,
 * consisting of "commercial computer software" and "commercial computer software documentation," as such terms are
 * used in 48 C.F.R. 12.212. Consistent with 48 C.F.R. 12.212 and 48 C.F.R. 227.7202-1 through 227.7202-4, and other
 * relevant sections of the Code of Federal Regulations, as applicable all U.S. Government End Users acquire the
 * software with only those rights set forth in the license agreement. This software includes trade secrets that
 * are exempt from disclosure under the Freedom of Information Act.
 *
 *
 * _Demographic Prisoners Dilemma_
 * See _Zones of Cooperation in Demographic Prisoner's Dilemma_
 * Santa Fe Institute Economics Research Program Working Paper
 * Joshua Epstein
 * jepstein@brook.edu
 *
 * Miles Parker
 * mparker@brook.edu
 * The Brookings Institution
 * Washington, D.C.
 */

package edu.brook.pd;

import org.ascape.model.HostCell;
import org.ascape.model.Scape;
import org.ascape.model.space.Array2DVonNeumann;
import org.ascape.model.space.Coordinate2DDiscrete;

public class PD2DSmallWorld extends PD2D {


    public void createScape() {
        super.createScape();
        remove(lattice);
        remove(agents);
        //Create a 2D lattice (we'll be using von Neumann Geometry.)
        //lattice = new Scape(new Array2DSmallWorld());
        lattice = new Scape(new Array2DVonNeumann());
        //Use the generic host cell as the agent that will fill the lattice.
        lattice.setPrototypeAgent(new HostCell());
        //Important: Clear rules so the default iterate rule won't be used for
        //the host cell. The model will run faster, since iterate won't be called
        //on each lattice cell.
        lattice.getRules().clear();
        //Set the size by setting the coordinate at the lattices largest extent.
        lattice.setExtent(new Coordinate2DDiscrete(latticeWidth, latticeHeight));
        //Set the lattice to expect cells to request updates only when neccesary.
        //This greatly enhances perfomance, but requires us to manually request view
        //updates if a change occurs to a lattice cell. In this case, the only change
        //that might occur to a cell is for an agent to move in or out of it, because
        //an agents strategy does not change, and that is the only aspect of agent
        //state that the view reflects. We do not need to request updates on agent
        //movement, since that is handled automatically by the framework.
        //lattice.setCellsRequestUpdates(true);

        //Create an instance of Player that we can set state for...
        Player player = new edu.brook.pd.Player();
        //Set the host scape for the prototype agent,
        //So that all 'agents' members know where they live.
        player.setHostScape(lattice);
        //Create a scape for the agents (players) that will live on the lattice.
        //Vectors are appropriate whenever the size of a population will change.
        agents = new Scape();
        //Give the scape a short, descriptive name
        agents.setName("Players");
        //...and make the player the prototype that will be used to fill the initial scape.
        agents.setPrototypeAgent(player);
        //Tell the agents to execute in by rule order; each rule executes on every agent
        //before the next rule is executed.
        agents.setExecutionOrder(Scape.RULE_ORDER);

        //Finally, we need to add our vectors to the root we created at the beginning.
        //The root scape is the parent and controls its member scapes.
        add(lattice);
        add(agents);
    }
}
