/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package edu.brook.firms;

import java.awt.Color;
import java.util.Iterator;

import org.ascape.model.Scape;




/**
 * The Class Firm.
 */
public class Firm extends Scape {

    /**
     * 
     */
    private static final long serialVersionUID = 6654613651194368202L;

    /**
     * The age.
     */
    private int age = 0;

    /**
     * The lp effort.
     */
    protected double lpEffort;

    /**
     * The lp output.
     */
    @SuppressWarnings("unused")
    private double lpOutput;

    /**
     * The lp size.
     */
    protected int lpSize;

    /**
     * The color.
     */
    private Color color;

    /**
     * Instantiates a new firm.
     */
    public Firm() {
        super();
        //We use Math.random() so as not to interfere w/ model random draws.
    }

    /* (non-Javadoc)
     * @see org.ascape.model.Scape#initialize()
     */
    public void initialize() {
        color = new Color((float) Math.random(), (float) Math.random(), (float) Math.random());
        setAutoCreate(false);
        setName("Firm");
        super.initialize();
    }

    /**
     * Form.
     * 
     * @param firms
     *            the firms
     * @param employee
     *            the employee
     * @return the firm
     */
    public static Firm form(Scape firms, Employee employee) {
        Firm firm = (Firm) firms.newAgent();
        firm.hire(employee);
        firm.lpSize = 1;
        firm.lpEffort = employee.effort();
        return firm;
    }

    /**
     * For scape iteration is passive (it does not, and should not, affect the
     * model as a whole. The firm ages itself, save data for previous years, and
     * saves data that viewers may be interested in as attributes.
     */
    public void iterate() {
        age++;
        lpEffort = effort();
        lpOutput = output();
        lpSize = getSize();
    }

    /**
     * Disband the firm, removing it from the scape of scape.
     */
    private void disolve() {
        scape.remove(this);
    }

    /**
     * Makes the supplied employee a member of this firm. Does not make the
     * employee's parent scape the firm.
     * 
     * @param employee
     *            the employee to hire
     */
    public void hire(Employee employee) {
        add(employee, false);
    }

    /**
     * Terminates the supplied employee from this firm.
     * 
     * @param employee
     *            the employee to terminate
     * @exception RuntimeException
     *                if the employee is not a member of this firm
     */
    public void terminate(Employee employee) {
        if (remove(employee)) {
            //Removed succesfully.
        } else {
            //Leave called in error.
            throw new RuntimeException("Employee not member of firm attempting to leave.");
        }
        if (getSize() == 0) {
            disolve();
        }
    }

    /**
     * Returns the scape effort. In this implementation: the sum of all
     * employee's outputs.
     * 
     * @return the double
     */
    public synchronized double effort() {
        double result = 0;
        Iterator i = iterator();
        while (i.hasNext()) {
            result += ((Employee) i.next()).effort();
        }
        return result;
    }

    /**
     * Return the average per agent effort.
     * 
     * @return the avg effort
     */
    public double getAvgEffort() {
        if (getSize() > 0) {
            return effort() / getSize();
        }
        return 0.0;
    }

    /**
     * The rec output.
     */
    public double recOutput;

    /**
     * The rec size.
     */
    public double recSize;

    /**
     * The rec iter.
     */
    public double recIter;

    /**
     * Return the average per agent effort.
     * 
     * @return the avg output
     */
    public double getAvgOutput() {
        if (getSize() > 0) {
            return output() / getSize();
        }
        return 0.0;
    }

    /**
     * Returns the scape output.
     * 
     * @return the double
     */
    protected double output() {
        return UtilityFunction.output(effort());
    }

    /**
     * Returns the firm's effort in the last period. In this implementation: the
     * sum of all employee's efforts.
     * 
     * @return the double
     */
    public double lastPeriodEffort() {
        return lpEffort;
    }

    /**
     * Returns the firm's size in the last period.
     * 
     * @return the double
     */
    public double lastPeriodSize() {
        return lpSize;
    }

    /**
     * In this implementation, color is assigned randomly for each employee.
     * 
     * @return the color
     */
    public Color getColor() {
        return color;
    }
}
