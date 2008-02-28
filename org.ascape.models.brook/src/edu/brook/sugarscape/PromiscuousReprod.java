/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.sugarscape;

import java.util.List;

import org.ascape.model.Agent;
import org.ascape.model.CellOccupant;
import org.ascape.model.rule.Rule;
import org.ascape.util.Utility;

/**
 * A rule causing the agent to reproduce with as many neighbors
 * as possible in random order.
 *
 * @author Miles Parker
 * @version 1.0
 **/
public class PromiscuousReprod extends Rule {

    /**
     * 
     */
    private static final long serialVersionUID = 9144293905149346916L;

    public PromiscuousReprod() {
        super("Promiscuous Reproduction");
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
                    SexualAgent child = (SexualAgent) agent.clone();
                    agent.getScape().add(child);
                    child.initialize();
                    //Leave initially assigned cell
                    child.leave();
                    child.setSugar(((SexualAgent) agent).takeSugar(((SexualAgent) agent).getContributionToChild()));
                    child.setSugar(mate.takeSugar(mate.getContributionToChild()));
                    child.setAge(0);

                    if (randomIs()) {
                        child.setVision(((SexualAgent) agent).getVision());
                    } else {
                        child.setVision(mate.getVision());
                    }

                    if (randomIs()) {
                        child.setSugarMetabolism(((SexualAgent) agent).getSugarMetabolism());
                    } else {
                        child.setSugarMetabolism(mate.getSugarMetabolism());
                    }

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
