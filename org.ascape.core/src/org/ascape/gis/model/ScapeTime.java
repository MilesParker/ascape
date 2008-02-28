/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */


package org.ascape.gis.model;

import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;

/**
 * A scape that is aware of time. Intended to be the root scape (model scape) of a model that is concerned with time.
 * This may be delegated to a seperate class.
 *
 * @author    Miles Parker, Josh Miller, Mario Inchiosa
 * @created   September-November, 2001
 */
public class ScapeTime extends Scape {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Number of seconds per timestep
     */
    private int secondsPerIteration = 60;

    /**
     * Number of seconds since model started
     */
    private int secondsSinceStart;

    /**
     * Return the parameter rate per day as rate per iteration.
     *
     * @param ratePerDay  parameter
     * @return
     */
    public double ratePerDayAsRatePerIteration(double ratePerDay) {
        return ratePerDay / 24.0 / 3600.0 * secondsPerIteration;
    }

    /**
     * Return the parameter rate per hour as rate per iteration.
     *
     * @param ratePerHour  parameter
     * @return
     */
    public double ratePerHourAsRatePerIteration(double ratePerHour) {
        return ratePerHour / 3600.0 * secondsPerIteration;
    }

    /**
     * Return the parameter rate per minute as rate per iteration.
     *
     * @param ratePerMinute  parameter
     * @return
     */
    public double ratePerMinuteAsRatePerIteration(double ratePerMinute) {
        return ratePerMinute / 60.0 * secondsPerIteration;
    }

    /**
     * Return the parameter rate per second as rate per iteration.
     *
     * @param ratePerSecond  parameter
     * @return
     */
    public double ratePerSecondAsRatePerIteration(double ratePerSecond) {
        return ratePerSecond * secondsPerIteration;
    }

    /**
     * Converts probability per minute to probability per iteration
     *
     * @param probPerMinute  parameter
     * @return
     */
    public double convertProbPerMinuteToProbPerIteration(double probPerMinute) {
        // -41.58883083359672 = -Log[2.0]*60.0
        double halfLifeInSeconds = -41.58883083359672 / Math.log(1.0 - probPerMinute);
        return 1.0 - Math.pow(2.0, -secondsPerIteration / halfLifeInSeconds);
    }

    public double convertProbPerHourToProbPerIteration(double probPerHour) {
        // -2495.32985001580311 = -Log[2.0]*3600.0
        double halfLifeInSeconds = -2495.32985001580311 / Math.log(1.0 - probPerHour);
        return 1.0 - Math.pow(2.0, -secondsPerIteration / halfLifeInSeconds);
    }

    public double convertProbPerDayToProbPerIteration(double probPerDay) {
        // -59887.916400379274733 = -Log[2.0]*3600.0 * 24
        double halfLifeInSeconds = -59887.916400379274733 / Math.log(1.0 - probPerDay);
        return 1.0 - Math.pow(2.0, -secondsPerIteration / halfLifeInSeconds);
    }

    /**
     * Gets the number of seconds per iteration
     *
     * @return   the secondsPerIteration
     */
    public int getSecondsPerIteration() {
        return secondsPerIteration;
    }

    /**
     * Initialize the model, removing any agents that might have been added in a prewvious run,
     * and checking that settings are set correctly.
     */
    public void initialize() {
        super.initialize();
        secondsSinceStart = 0;
    }

    /**
     * Sets the number of seconds per iteration
     *
     * @param secondsPerIteration  the secondsPerIteration
     */
    public void setSecondsPerIteration(int secondsPerIteration) {
        this.secondsPerIteration = secondsPerIteration;
    }

    /**
     * Each timestep, increment secondsSinceStart, and tell the CommunicationsNetwork and the
     * CommandCenter to check their queues.
     */
    public void scapeIterated(ScapeEvent event) {
        super.scapeIterated(event);
        secondsSinceStart += secondsPerIteration;
    }

    /**
     * Gets the number of seconds since the simulation started
     *
     * @return   the secondsSinceStart
     */
    public int getSecondsSinceStart() {
        return secondsSinceStart;
    }

    /**
     * Returns a string description of the elapsed simulated time since the
     * simulation began, in the form "d, hh:mm:ss"
     *
     * @return   the periodDescription
     */
    public String getPeriodDescription() {
        int days = secondsSinceStart / (60 * 60 * 24);
        int hours = secondsSinceStart % (60 * 60 * 24) / (60 * 60);
        int minutes = secondsSinceStart % (60 * 60) / 60;
        int seconds = secondsSinceStart % 60;

        return Integer.toString(days) + ", " +
            (hours < 10 ? "0" : "") +
            Integer.toString(hours) + ":" +
            (minutes < 10 ? "0" : "") +
            Integer.toString(minutes) + ":" +
            (seconds < 10 ? "0" : "") +
            Integer.toString(seconds) + "";
    }
}
