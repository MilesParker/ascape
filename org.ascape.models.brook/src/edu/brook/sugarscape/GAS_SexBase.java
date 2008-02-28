/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.sugarscape;


public class GAS_SexBase extends GAS_Base {

    /**
     * 
     */
    private static final long serialVersionUID = -7323806302172641112L;

    public void createScape() {
        super.createScape();
        SexualAgent agent = new SexualAgent();
        agent.setHostScape(sugarscape);
        agents.setPrototypeAgent(agent);
        agents.setExtent(new org.ascape.model.space.Coordinate1DDiscrete(300));
        agents.getRules().clear();
        agents.addRule(MOVEMENT_RULE);
        agents.addRule(SugarAgent.HARVEST_RULE);
        agents.addRule(METABOLISM_RULE);
        agents.addRule(new PromiscuousReprod());
        agents.addRule(SugarAgent.DEATH_STARVATION_OLD_AGE_RULE);
        sugarscape.getRules().clear();
        sugarscape.addRule(SugarCell.SUGAR_GROW_BACK_1_RULE);
    }
}
