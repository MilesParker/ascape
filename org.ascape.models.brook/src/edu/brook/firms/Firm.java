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
 * Firms Model
 * Robert Axtell
 * The Brookings Institution
 * Washington, D.C.
 * raxtell@brook.edu
 *
 * Miles Parker
 * The Brookings Institution
 * Washington, D.C.
 * mparker@brook.edu
 * (202) 797-6136
 *
 * Please contact us for permission to use, copy, modify, or distribute this software.
 */

package edu.brook.firms;

import java.awt.Color;
import java.util.Iterator;

import org.ascape.model.Scape;




public class Firm extends Scape {

    private int age = 0;

    protected double lpEffort;

    @SuppressWarnings("unused")
    private double lpOutput;

    protected int lpSize;

    private Color color;

    public Firm() {
        super();
        //We use Math.random() so as not to interfere w/ model random draws.
    }

    public void initialize() {
        color = new Color((float) Math.random(), (float) Math.random(), (float) Math.random());
        setAutoCreate(false);
        setName("Firm");
        super.initialize();
    }

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
     **/
    public void iterate() {
        age++;
        lpEffort = effort();
        lpOutput = output();
        lpSize = getSize();
    }

    /**
     * Disband the firm, removing it from the scape of scape.
     **/
    private void disolve() {
        scape.remove(this);
    }

    /**
     * Makes the supplied employee a member of this firm.
     * Does not make the employee's parent scape the firm.
     * @param employee the employee to hire
     **/
    public void hire(Employee employee) {
        add(employee, false);
    }

    /**
     * Terminates the supplied employee from this firm.
     * @param employee the employee to terminate
     * @exception RuntimeException if the employee is not a member of this firm
     **/
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
     * Returns the scape effort.
     * In this implementation: the sum of all employee's outputs.
     **/
    public double effort() {
        double result = 0;
        Iterator i = iterator();
        while (i.hasNext()) {
            result += ((Employee) i.next()).effort();
        }
        return result;
    }

    /**
     * Return the average per agent effort.
     */
    public double getAvgEffort() {
        return effort() / getSize();
    }

    public double recOutput;
    public double recSize;
    public double recIter;

    /**
     * Return the average per agent effort.
     */
    public double getAvgOutput() {
        return output() / getSize();
    }

    /**
     * Returns the scape output
     **/
    protected double output() {
        return UtilityFunction.output(effort());
    }

    /**
     * Returns the firm's effort in the last period.
     * In this implementation: the sum of all employee's efforts.
     **/
    public double lastPeriodEffort() {
        return lpEffort;
    }

    /**
     * Returns the firm's size in the last period.
     **/
    public double lastPeriodSize() {
        return lpSize;
    }

    /**
     * In this implementation, color is assigned randomly for each employee.
     **/
    public Color getColor() {
        return color;
    }
}
