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

import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.space.Array2DVonNeumann;
import org.ascape.model.space.Coordinate1DDiscrete;
import org.ascape.model.space.Coordinate2DDiscrete;
import org.ascape.util.data.UnitIntervalDataPoint;
import org.ascape.util.vis.ColorFeatureFixed;
import org.ascape.util.vis.ColorFeatureGradiated;
import org.ascape.view.vis.Overhead2DView;

public class GAS_Base extends Scape {

    //Parameters
    private int minVision = 1;
    private int maxVision = 6;
    private int minSugarMetabolism = 1;
    private int maxSugarMetabolism = 4;
    private int minInitialSugar = 0;
    private int maxInitialSugar = 30;
    private int minDeathAge = 60;
    private int maxDeathAge = 100;
    private float sugarMoundness = .004f;

    protected Scape sugarscape;

    protected Scape agents;

    protected Overhead2DView sugarView;

    /**
     * Returns the topology parameter for sugar peaks
     */
    public float getSugarMoundness() {
        return sugarMoundness;
    }

    /**
     * Sets the topology parameter for sugar peaks
     */
    public void setSugarMoundness(float moundness) {
        sugarMoundness = moundness;
    }

    /**
     * Returns the minimum vision that an agent may be given on initialization.
     * Model parameter.
     */
    public int getMinVision() {
        return minVision;
    }

    /**
     * Sets the minimum vision that an agent may be given on initialization.
     * Model parameter.
     */
    public void setMinVision(int _minVision) {
        minVision = _minVision;
    }

    /**
     * Returns the maximum vision that an agent may be given on initialization.
     * Model parameter.
     */
    public int getMaxVision() {
        return maxVision;
    }

    /**
     * Sets the maximum vision that an agent may be given on initialization.
     * Model parameter.
     */
    public void setMaxVision(int _maxVision) {
        maxVision = _maxVision;
    }

    /**
     * Returns the minimum sugar metabolism that an agent may be given on initialization.
     * Model parameter.
     */
    public int getMinSugarMetabolism() {
        return minSugarMetabolism;
    }

    /**
     * Sets the minimum sugar metabolism that an agent may be given on initialization.
     * Model parameter.
     */
    public void setMinSugarMetabolism(int _minMetabolism) {
        minSugarMetabolism = _minMetabolism;
    }

    /**
     * Returns the maximum sugar metabolism that an agent may be given on initialization.
     * Model parameter.
     */
    public int getMaxSugarMetabolism() {
        return maxSugarMetabolism;
    }

    /**
     * Sets the maximum Sugar metabolism that an agent may be given on initialization.
     * Model parameter.
     */
    public void setMaxSugarMetabolism(int _maxMetabolism) {
        maxSugarMetabolism = _maxMetabolism;
    }

    /**
     * Returns the minimum age that an agent may be given on initialization.
     * Model parameter.
     */
    public int getMinDeathAge() {
        return minDeathAge;
    }

    /**
     * Sets the minimum age that an agent may be given on initialization.
     * Model parameter.
     */
    public void setMinDeathAge(int _minDeathAge) {
        minDeathAge = _minDeathAge;
    }

    /**
     * Returns the maximum age that an agent may be given on initialization.
     * Model parameter.
     */
    public int getMaxDeathAge() {
        return maxDeathAge;
    }

    /**
     * Sets the maximum age that an agent may be given on initialization.
     * Model parameter.
     */
    public void setMaxDeathAge(int _maxDeathAge) {
        maxDeathAge = _maxDeathAge;
    }

    /**
     * Returns the minimum sugar that an agent may be given on initialization.
     * Model parameter.
     */
    public int getMinInitialSugar() {
        return minInitialSugar;
    }

    /**
     * Sets the minimum sugar that an agent may be given on initialization.
     * Model parameter.
     */
    public void setMinInitialSugar(int _minInitialSugar) {
        minInitialSugar = _minInitialSugar;
    }

    /**
     * Returns the maximum sugar that an agent may be given on initialization.
     * Model parameter.
     */
    public int getMaxInitialSugar() {
        return maxInitialSugar;
    }

    /**
     * Sets the maximum sugar that an agent may be given on initialization.
     * Model parameter.
     */
    public void setMaxInitialSugar(int _maxInitialSugar) {
        maxInitialSugar = _maxInitialSugar;
    }

    public void createScape() {
        setName("Sugarscape Model");
        setPrototypeAgent(new Scape());
        sugarscape = new Scape(new Array2DVonNeumann());
        sugarscape.setName("Sugar Cells");
        sugarscape.setPrototypeAgent(new SugarCell());
        sugarscape.setExtent(new Coordinate2DDiscrete(50, 50));
        SugarAgent agent = new SugarAgent();
        agent.setHostScape(sugarscape);
        agents = new Scape();
        agents.setName("Sugar Agents");
        agents.setPrototypeAgent(agent);
        sugarscape.setExecutionOrder(Scape.RULE_ORDER);
        sugarscape.setCellsRequestUpdates(true);
        add(sugarscape);
        add(agents);
    }

    public void scapeSetup(ScapeEvent scapeEvent) {
        agents.setExtent(new Coordinate1DDiscrete(400));
    }

    public void createViews() {
        super.createViews();
        sugarView = new Overhead2DView();
        final ColorFeatureGradiated colorCellForSugar = new ColorFeatureGradiated("Sugar");
        colorCellForSugar.setDataPoint(new UnitIntervalDataPoint() {
            public double getValue(Object object) {
                return ((double) ((SugarCell) object).getSugar().getQuantity() / (double) SugarCell.MAX_SUGAR);
            }
        });
        colorCellForSugar.setMaximumColor(Color.yellow);
        sugarView.setCellColorFeature(colorCellForSugar);
        sugarView.setHostedAgentColorFeature(ColorFeatureFixed.red);
        sugarView.setCellSize(8);
        sugarscape.addView(sugarView);
        if (getUserEnvironment().isInApplet()) {
            getUserEnvironment().getAppletModelPanel().add("Center", sugarView);
        }
    }
}
