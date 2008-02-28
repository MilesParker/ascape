/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.sugarscape;

import java.awt.Color;
import java.util.List;

import org.ascape.model.Agent;
import org.ascape.model.CellOccupant;
import org.ascape.model.rule.Rule;
import org.ascape.model.space.Coordinate1DDiscrete;
import org.ascape.util.Utility;
import org.ascape.util.vis.ColorFeatureConcrete;

public class GAS_III_6 extends GAS_II_2 {

    /**
     * 
     */
    private static final long serialVersionUID = 7697945539722553586L;

    public void createScape() {
        super.createScape();
        setSugarMoundness(.005f);
        CulturalSexualAgent.setTagLength(11);
        CulturalSexualAgent agent = new CulturalSexualAgent();
        agent.setHostScape(sugarscape);
        agents.setPrototypeAgent(agent);
        agents.setExtent(new Coordinate1DDiscrete(300));
        agents.addRule(new CulturalReproduction());
        agents.addRule(PLAY_NEIGHBORS_RULE);
        agents.addRule(SugarAgent.DEATH_STARVATION_RULE);
    }

    public void createViews() {
        super.createViews();
        sugarView.setHostedAgentColorFeature(new ColorFeatureConcrete("Culture") {
            /**
             * 
             */
            private static final long serialVersionUID = 5780368552228821750L;

            public Color getColor(Object object) {
                if (((CulturalSexualAgent) object).getMajority()) {
                    return Color.blue;
                } else {
                    return Color.red;
                }
            }
        });
    }
}

class CulturalReproduction extends Rule {

    /**
     * 
     */
    private static final long serialVersionUID = -4430571309485409403L;

    public CulturalReproduction() {
        super("Cultural Reproduction");
    }

    /**
     * Reproduce with as many agents as possible.
     * @param agent the reproducing agent
     */
    public void execute(Agent agent) {
        List neighbors = ((CellOccupant) agent).findNeighborsOnHost();
        //pick a random series from the set of series that match the number of neighbors
        //(Assumes von Neumann neighborhood, otherwise creates error)
        int[] series = Utility.uniqueSeries[neighbors.size()][randomToLimit(Utility.uniqueSeries[neighbors.size()].length)];
        for (int i = 0; i < series.length; i++) {
            if (!((SexualAgent) agent).isFertile()) {
                break;
            }
            SexualAgent mate = (SexualAgent) neighbors.get(series[i] - 1);
            if ((((SexualAgent) agent).isFemale() != mate.isFemale()) && (mate.isFertile())) {
                if ((((CellOccupant) agent).getHostCell().isNeighborAvailable()) || (mate.getHostCell().isNeighborAvailable())) {
                    CulturalSexualAgent child = (CulturalSexualAgent) agent.clone();
                    agent.getScape().add(child);
                    child.initialize();
                    //Leave initially assigned cell
                    child.leave();
                    child.setSugar(((SexualAgent) agent).takeSugar(((SexualAgent) agent).getContributionToChild()));
                    child.setSugar(mate.takeSugar(mate.getContributionToChild()));
                    child.setAge(0);

                    if (((CulturalSexualAgent) agent).getMajority()) {
                        if (randomIs()) {
                            child.setVision(((SexualAgent) agent).getVision());
                        } else {
                            child.setVision(mate.getVision());
                        }
                    } else {
                        if (randomIs()) {
                            child.setSugarMetabolism(((SexualAgent) agent).getSugarMetabolism());
                        } else {
                            child.setSugarMetabolism(mate.getSugarMetabolism());
                        }
                    }
                    for (int j = 0; j < CulturalSexualAgent.getTagLength(); j++) {
                        if (((CulturalSexualAgent) agent).getTagPosition(j) == ((CulturalSexualAgent) mate).getTagPosition(j)) {
                            child.setTagPosition(j, ((CulturalSexualAgent) agent).getTagPosition(j));
                        } else {
                            child.setTagPosition(j, randomIs());
                        }
                    }

                    /*if (randomIs()) {
                        child.setVision(((SexualAgent) agent).getVision());
                    }
                    else {
                        child.setVision(mate.getVision());
                    }
                    if (randomIs()) {
                        child.setSugarMetabolism(((SexualAgent) agent).getSugarMetabolism());
                    }
                    else {
                        child.setSugarMetabolism(mate.getSugarMetabolism());
                    }*/

                    if ((((CellOccupant) agent).getHostCell().isNeighborAvailable())) {
                        child.moveTo(((CellOccupant) agent).getHostCell().findRandomAvailableNeighbor());
                    } else {
                        child.moveTo(mate.getHostCell().findRandomAvailableNeighbor());
                    }
                }
            }
        }
    }

    public boolean isRandomExecution() {
        return false;
    }

    public boolean isCauseRemoval() {
        return false;
    }
}
