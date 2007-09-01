/*
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
 * _Heatbugs_
 * This is an ascape implementation of the canonical swarm model 'heatbugs'.
 * Hopefully, it will give Swarm users some insight into how ascape is organized.
 * I have tried to mirror swarm names and usage where consistent with ascape.
 * For the orignal Swarm implementation and more about swarm, see http://www.santafe.edu/projects/swarm
 *
 * _Ascape Contact_
 * Miles Parker
 * mparker@brook.edu
 * The Brookings Institution
 * Washington, D.C.
 */

package edu.brooksantafe.heatbugs;

import java.awt.Color;

import org.ascape.model.Cell;
import org.ascape.model.CellOccupant;
import org.ascape.model.HostCell;
import org.ascape.util.data.DataPoint;


/**
 * A heatbug, an agent that produces heat and moves to locations closest to an ideal temperature.
 **/
public class Heatbug extends CellOccupant {

    /**
     * The current unhappiness the bug is experiencing due to non-ideal heat conditions.
     */
    private double unhappiness;

    /**
     * The temperature the bug would like to live in
     */
    private int idealTemperature;

    /**
     * The amount of heat the bug generates to its environment
     */
    private int outputHeat;

    /**
     * At the start of each run, set ideal temperature and output heat to a uniformally distributed random value within
     * the model's min and max values.
     */
    public void initialize() {
        super.initialize();
        idealTemperature = randomInRange(((HeatbugModel) getRoot()).getMinIdealTemp(), ((HeatbugModel) getRoot()).getMaxIdealTemp());
        outputHeat = randomInRange(((HeatbugModel) getRoot()).getMinOutputHeat(), ((HeatbugModel) getRoot()).getMaxOutputHeat());
    }

    /**
     * Calculate the heatbug's unhappiness with its present location.
     */
    public void calculateHappiness() {
        float hostHeat = ((HeatCell) getHostCell()).getHeat();
        if (hostHeat < idealTemperature) {
            unhappiness = (double) (idealTemperature - hostHeat) / (double) HeatCell.MAX_HEAT;
        } else {
            unhappiness = (double) (hostHeat - idealTemperature) / (double) HeatCell.MAX_HEAT;
        }
    }

    /**
     * Classic Movement and Heat rule consistent with original Heatbugs behavior. If unhappiness is not zero, find the cell closest to the desired temperature
     * within the immediate vicinity. On model random move probability, take a random walk.
     */
    public void movementAndHeat() {
        //To mirror original heatbugs functionality, we have to put metabolism
        //(heat output) code in movement method.
        //The heat must be added after movement decision is made, but while old location is still known
        HeatCell oldHost = (HeatCell) getHostCell();
        if (unhappiness != 0.0) {
            Cell bestLocation;
            DataPoint maximizeFor = (((HeatCell) getHostCell()).getHeat() < idealTemperature)
                ? HeatCell.MAXIMUM_HEAT_POINT : HeatCell.MINIMUM_HEAT_POINT;
            bestLocation = (Cell) getHostCell().findMaximumWithin(maximizeFor, true, 1);
            if (!bestLocation.isAvailable() && bestLocation != oldHost) {
                randomWalkAvailable();
            } else if (bestLocation != oldHost) {
                moveTo((HostCell) bestLocation);
            }
            //This is slightly different then the swarm implementation..
            if (getRandom().nextFloat() < ((HeatbugModel) getRoot()).getRandomMoveProbability()) {
                randomWalk();
            }
        }
        oldHost.addHeat(outputHeat);
    }

    /**
     * Movement rule. If unhappiness is not zero, find the cell closest to the desired temperature
     * within the immediate vicinity. On model random move probability, take a random walk.
     */
    public void movement() {
        //To mirror original heatbugs functionality, we have to put metabolism
        //(heat output) code in movement method.
        //The heat must be added after movement decision is made, but while old location is still known
        //IGNORE OLD FUNCTIONALITY...
        HeatCell oldHost = (HeatCell) getHostCell();
        if (unhappiness != 0.0) {
            Cell bestLocation;
            DataPoint maximizeFor = (((HeatCell) getHostCell()).getHeat() < idealTemperature)
                ? HeatCell.MAXIMUM_HEAT_POINT : HeatCell.MINIMUM_HEAT_POINT;
            bestLocation = (Cell) getHostCell().findMaximumWithin(maximizeFor, true, 1);
            if (!bestLocation.isAvailable() && bestLocation != oldHost) {
                randomWalkAvailable();
            } else if (bestLocation != oldHost) {
                moveTo((HostCell) bestLocation);
            }
            //This is slightly different then the swarm implementation..
            if (getRandom().nextFloat() < ((HeatbugModel) getRoot()).getRandomMoveProbability()) {
                randomWalk();
            }
        }
    }

    /**
     * Movement rule. If unhappiness is not zero, find the cell closest to the desired temperature
     * within the immediate vicinity. On model random move probability, take a random walk.
     */
    public void generateHeat() {
        ((HeatCell) getHostCell()).addHeat(outputHeat);
    }

    /**
     * Returns a value representing the bugs unhappiness.
     */
    public double getUnhappiness() {
        return unhappiness;
    }

    /**
     * Sets a value representing the bugs unhappiness.
     */
    public void setUnhappiness(double unhappiness) {
        this.unhappiness = unhappiness;
    }

    /**
     * Returns a value representing the bugs unhappiness.
     */
    public int getOutputHeat() {
        return outputHeat;
    }

    /**
     * Sets a value representing the bugs unhappiness.
     */
    public void setOutputHeat(int outputHeat) {
        this.outputHeat = outputHeat;
    }

    /**
     * Returns a value representing the bugs unhappiness.
     */
    public double getIdealTemperature() {
        return idealTemperature;
    }

    /**
     * Sets a value representing the bugs unhappiness.
     */
    public void setIdealTemperature(int idealTemperature) {
        this.idealTemperature = idealTemperature;
    }

    /**
     * Returns green, the all-purpose bug color.
     */
    public Color getColor() {
        return Color.green;
    }

    public String getName() {
        return "Heat Cell " + coordinate;
    }
}
