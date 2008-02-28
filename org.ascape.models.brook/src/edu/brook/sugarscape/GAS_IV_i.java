/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.sugarscape;

import org.ascape.model.Scape;
import org.ascape.model.space.Array2DVonNeumann;
import org.ascape.model.space.Coordinate1DDiscrete;
import org.ascape.model.space.Coordinate2DDiscrete;

// like GAS_IV_d except with old age and reproduction

public class GAS_IV_i extends GAS_IV_e {

    /**
     * 
     */
    private static final long serialVersionUID = -848776017805061235L;

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
        agents.addRule(new TradeT4());
    }
}
