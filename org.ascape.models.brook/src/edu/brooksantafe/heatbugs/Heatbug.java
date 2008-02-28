/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
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
     * 
     */
    private static final long serialVersionUID = -3644065018295193171L;

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
