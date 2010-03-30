/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.view.nonvis;

import java.util.Iterator;
import java.util.List;

import org.ascape.model.event.ScapeEvent;
import org.ascape.util.PropertyAccessor;
import org.ascape.util.Utility;

/**
 * A view providing basic system out reporting.
 * 
 * @author Miles Parker
 * @version 1.2 8/4/1999 first in
 * @since 1.2
 */
public class ConsoleOutView extends DataView {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Display results for every iteration?.
     */
    private boolean printResultsEachPeriod;

    /**
     * The longest name in the settings output.
     */
    private int longestNameLength;

    /**
     * The longest number in the settings output.
     */
    private int longestNumLength;

    /**
     * The time the current run was started. Used to track elapsed time for each
     * run.
     */
    private long startTime;

    /**
     * The character(s) to use for end-of-line.
     */
    public static String EOL = System.getProperty("line.separator");

    private transient IMessageStream msg;

    public ConsoleOutView() {
        setSystemStream();
    }

    /**
     * Prints the current model settings to standard out.
     * 
     * @return the string
     */
    public String settingsDescription() {
        StringBuffer desc = new StringBuffer();
        List accessors = scape.retrieveModelAccessorsOrdered();
        longestNameLength = 0;
        longestNumLength = 0;
        for (Iterator iterator = accessors.iterator(); iterator.hasNext();) {
            PropertyAccessor accessor = (PropertyAccessor) iterator.next();
            int nameLength = accessor.getLongName().length();
            if (nameLength > longestNameLength) {
                longestNameLength = nameLength;
            }
            if (accessor.getValue() instanceof Number) {
                if (((Number) accessor.getValue()).doubleValue() < 100000000) {
                    int numLength = Utility.formatToString(((Number) accessor.getValue()).doubleValue(), 4).length();
                    if (numLength > longestNumLength) {
                        longestNumLength = numLength;
                    }
                }
            } else if (accessor.getValue() instanceof Boolean) {
                // Not likely!
                if (4 > longestNumLength) {
                    longestNumLength = 4;
                }
            } else if (accessor.getValue() == null) {
                longestNumLength = 10;
            } else {
                int stringLength = ((String) accessor.getValue()).length();
                if (stringLength > 25) {
                    stringLength = 25;
                }
                if (stringLength > longestNumLength) {
                    longestNumLength = stringLength;
                }
            }
        }
        if (longestNumLength > 32) {
            longestNumLength = 32;
        }
        desc.append("_Settings_" + EOL);
        for (Iterator iterator = accessors.iterator(); iterator.hasNext();) {
            PropertyAccessor accessor = (PropertyAccessor) iterator.next();
            if (accessor.getValue() instanceof Number && ((Number) accessor.getValue()).doubleValue() > 100000000) {
                if (((Number) accessor.getValue()).intValue() != Integer.MAX_VALUE) {
                    desc.append(accessor.getLongName()
                                + ": "
                                + Utility.padStringLeft(accessor.getValue().toString(), longestNumLength
                                                        + longestNameLength + 1 - accessor.getLongName().length()) + EOL);
                } else {
                    desc.append(accessor.getLongName()
                                + ": "
                                + Utility.padStringLeft("inf/max", longestNumLength + longestNameLength + 1
                                                        - accessor.getLongName().length()) + EOL);
                }
            } else if (accessor.getValue() instanceof Number) {
                desc.append(Utility.padStringRight(accessor.getLongName() + ":", longestNameLength + 3)
                            + Utility.padStringLeft(
                                                    Utility.formatToString(((Number) accessor.getValue()).doubleValue(), 4),
                                                    longestNumLength) + EOL);
            } else if (accessor.getValue() instanceof Boolean) {
                if (((Boolean) accessor.getValue()).booleanValue() == false) {
                    desc.append(Utility.padStringRight(accessor.getLongName() + ":", longestNameLength + 3)
                                + Utility.padStringLeft("false", longestNumLength) + EOL);
                } else {
                    desc.append(Utility.padStringRight(accessor.getLongName() + ":", longestNameLength + 3)
                                + Utility.padStringLeft("true", longestNumLength) + EOL);
                }
            } else {
                desc.append(Utility.padStringRight(accessor.getLongName() + ":", longestNameLength + 3)
                            + (String) accessor.getValue() + EOL);
            }
        }
        return desc.toString();
    }

    /**
     * Prints the current model results to standard out.
     * 
     * @return the string
     */
    public String resultsDescription() {
        StringBuffer desc = new StringBuffer();
        String[] resultsNames = new String[dataSelection.getSelectionSize()];
        String[] resultsNums = new String[dataSelection.getSelectionSize()];
        for (int i = 0; i < dataSelection.getSelectionSize(); i++) {
            resultsNames[i] = dataSelection.getSelectedSeries(i).getName();
            if (resultsNames[i].length() > longestNameLength) {
                longestNameLength = resultsNames[i].length();
            }
            resultsNums[i] = Utility.formatToString(dataSelection.getSelectedSeries(i).getValue(), 4);
            if (resultsNums[i].length() > longestNumLength) {
                longestNumLength = resultsNums[i].length();
            }
        }
        for (int i = 0; i < dataSelection.getSelectionSize(); i++) {
            desc.append(Utility.padStringRight(resultsNames[i] + ":", longestNameLength + 3)
                        + Utility.padStringLeft(resultsNums[i], longestNumLength) + EOL);
        }
        return desc.toString();
    }

    /**
     * Are results being printed to standard out every period? False by default.
     * 
     * @return true, if is print results each period
     */
    public boolean isPrintResultsEachPeriod() {
        return printResultsEachPeriod;
    }

    /**
     * Should results be printed to standard out every period?.
     * 
     * @param printResultsEachPeriod
     *            true to print results each period, false to print results on
     *            stop only.
     */
    public void setPrintResultsEachPeriod(boolean printResultsEachPeriod) {
        this.printResultsEachPeriod = printResultsEachPeriod;
    }

    /**
     * On start, print the model settings to the console.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void scapeStarted(ScapeEvent scapeEvent) {
        startTime = System.currentTimeMillis();
        if (msg == null) {
            setSystemStream();
        }
        println();
        print(settingsDescription());
    }

    public void print(String string) {
        msg.print(string);
    }

    public void println() {
        msg.println();
    }

    public void println(String string) {
        msg.println(string);
    }

    /**
     * On update, if printing results on update, print the current results to
     * the screen.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void scapeIterated(ScapeEvent scapeEvent) {
        if (printResultsEachPeriod) {
            if (dataSelection.getSelectionSize() > 0) {
                println("_Results for " + scape.getPeriod() + "_");
            }
            println(resultsDescription());
        }
    }

    /**
     * On stop, print the time taken and final results to the screen.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void scapeStopped(ScapeEvent scapeEvent) {
        println(stopDescription());
    }

    /**
     * Stop description.
     * 
     * @return the string
     */
    public String stopDescription() {
        StringBuffer desc = new StringBuffer();
        desc.append("Stop Period: " + scape.getPeriod() + EOL);
        desc.append("Finished in: " + Utility.formatElapsedMillis(System.currentTimeMillis() - startTime) + EOL);
        if (dataSelection.getSelectionSize() > 0) {
            desc.append("_Final Results_" + EOL);
        }
        desc.append(resultsDescription());
        return desc.toString();
    }

    public void setSystemStream() {
        this.msg = new IMessageStream() {
            public void print(String message) {
                System.out.print(message);
            }

            public void println() {
                System.out.println();
            }

            public void println(String message) {
                System.out.println(message);
            }
        };
    }

    public void setIMessageStream(IMessageStream consoleStream) {
        this.msg = consoleStream;
    }
}
