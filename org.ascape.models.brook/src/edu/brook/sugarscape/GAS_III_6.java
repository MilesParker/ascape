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
