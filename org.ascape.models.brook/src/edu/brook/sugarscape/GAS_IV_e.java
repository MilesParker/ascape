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
 * _Sugarscape_
 * See _Growing Artificial Societies: Social Science from the Ground Up_
 * Brookings Institution Press, The MIT Press
 * Joshua Epstein
 * jepstein@brook.edu
 * Robert Axtell
 * raxtell@brook.edu
 *
 * Miles Parker
 * mparker@brook.edu

 * http://www.brook.edu/es/dynamics/models/ascape
 * The Brookings Institution
 * Washington, D.C.
 *
 * Alan Lockard
 * alockard@gmu.edu
 */

package edu.brook.sugarscape;

import org.ascape.model.Scape;
import org.ascape.model.space.Array2DVonNeumann;
import org.ascape.model.space.Coordinate1DDiscrete;
import org.ascape.model.space.Coordinate2DDiscrete;

// No trade, but old age and reproduction

public class GAS_IV_e extends GAS_SpiceBase {

    public void createScape() {
        setPrototypeAgent(new Scape());
        sugarscape = new Scape(new Array2DVonNeumann());
        sugarscape.setName("SpiceScape");
        sugarscape.setPrototypeAgent(new SpiceCell());
        sugarscape.setExtent(new Coordinate2DDiscrete(50, 50));
        SexualSpiceAgent agent = new SexualSpiceAgent();
        agent.setHostScape(sugarscape);
        agents = new Scape();
        agents.setPrototypeAgent(agent);
        agents.setExtent(new Coordinate1DDiscrete(400));
        sugarscape.setExecutionOrder(Scape.RULE_ORDER);
        sugarscape.setCellsRequestUpdates(true);
        add(sugarscape);
        add(agents);
        sugarscape.addRule(SpiceCell.SUGAR_SPICE_GROW_BACK_1_RULE);
        agents.addRule(MOVEMENT_RULE);
        agents.addRule(SugarAgent.HARVEST_RULE);
        agents.addRule(SpiceAgent.DEATH_STARVATION_OLD_AGE_SPICE_RULE);
        agents.addRule(METABOLISM_RULE);
        agents.addRule(new PromiscuousSpiceReprod());
    }
}
