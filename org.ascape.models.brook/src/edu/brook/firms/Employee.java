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
 * mparker@brook.edu
 * (202) 797-6136
 *
 * Please contact us for permission to use, copy, modify, or distribute this software.
 */

package edu.brook.firms;

import java.awt.Color;
import java.util.List;

import org.ascape.model.Cell;
import org.ascape.model.Scape;


/**
 * An employee as member of a firm. Part of firms model
 *
 * @author Miles Parker
 * @version 1.0
 */
public class Employee extends Cell {

    protected final static int CURRENT_FIRM_OPTION = -2;

    protected final static int NEW_FIRM_OPTION = -1;

    private Employee[] friends;

    protected Firm firm;

    private static Scape firms;

    /**
     * Size of friend network.
     **/
    private static final int nFriends = 2;

    /**
     * Use friends or neighbors in comparison calculations
     **/
    protected static final boolean useFriends = false;

    protected UtilityFunction utility;

    /**
     * Effort level
     **/
    protected double cpEffort;

    /**
     * Effort level for last period
     **/
    private double lpEffort;

    protected static int proposedOption;

    public Employee() {
        super();
    }

    public void initialize() {
        if (useFriends) {
            findNewFriends();
        }
        utility = new UtilityFunction();
        utility.theta = Math.abs(getRandom().nextDouble());
        cpEffort = utility.theta;
        firm = Firm.form(firms, this);
        super.initialize();
        setName("Employee");
    }

    public Scape getFirms() {
        return firms;
    }

    public void setFirms(Scape firms) {
        this.firms = firms;
    }

    public void iterate() {
        maximizeUtilityForEffortAndOptions();
        if (proposedOption == CURRENT_FIRM_OPTION) {
            //Do nothing
        } else if (proposedOption == NEW_FIRM_OPTION) {
            formFirm();
        } else {
            //join(friends[proposedOption].getFirm());
            join((Firm) getComparisonFirms()[proposedOption]);
        }
    }

    public double effort() {
        return cpEffort;
    }

    public double output() {
        return cpEffort;
    }

    private void maximizeUtilityForEffortAndOptions() {
        lpEffort = cpEffort;
        double bestUtility = 0;
        double currentEffort = 0;
        double currentUtility = 0;
        //Since the employee will not count itself when calculating last periods effort
        utility.baseEffort = firm.effort() - this.effort();
        utility.baseSize = firm.getSize() - 1;
        currentEffort = maximizeUtilityForEffort();
        currentUtility = utility.solveFor(currentEffort);
        //First effort is always "best effort" at this point.
        bestUtility = currentUtility;
        cpEffort = currentEffort;
        proposedOption = CURRENT_FIRM_OPTION;

        utility.baseEffort = 0;
        utility.baseSize = 0;
        currentEffort = maximizeUtilityForEffort();
        currentUtility = utility.solveFor(currentEffort);
        if (currentUtility > bestUtility) {
            bestUtility = currentUtility;
            cpEffort = currentEffort;
            proposedOption = NEW_FIRM_OPTION;
            //System.out.println(proposedEffort + " " + utility.theta);
        }

        Cell[] comparisonFirms = getComparisonFirms();
        for (int i = 0; i < comparisonFirms.length; i++) {
            utility.baseEffort = ((Firm) comparisonFirms[i]).effort();
            utility.baseSize = ((Firm) comparisonFirms[i]).getSize();
            currentEffort = maximizeUtilityForEffort();
            currentUtility = utility.solveFor(currentEffort);
            if (currentUtility > bestUtility) {
                bestUtility = currentUtility;
                cpEffort = currentEffort;
                proposedOption = i;
            }
        }
    }

    public double maximizeUtilityForEffort() {
        return utility.maximize();
    }

    /**
     * Employee joins the specified firm.
     **/
    public void join(Firm firm) {
        if (this.firm != null) {
            leave(this.firm);
        }
        this.firm = firm;
        firm.hire(this);
    }

    /**
     * Employee forms a new firm.
     **/
    private void formFirm() {
        cpEffort = utility.theta;
        if (this.firm != null) {
            leave(this.firm);
        }
        firm = Firm.form(firms, this);
    }

    private void leave(Firm firm) {
        this.firm = null;
        firm.terminate(this);
    }

    public Firm getFirm() {
        return firm;
    }

    /**
     * Returns the firms output.
     * In this implementation: the sum of all employee's outputs.
     **/
    public double lastPeriodEffort() {
        return lpEffort;
    }

    public Cell[] getComparisonFirms() {
        if (useFriends) {
            Firm[] comparisonFirms = new Firm[friends.length];
            //Optimization..don't return copies of firms
            for (int i = 0; i < friends.length; i++) {
                comparisonFirms[i] = friends[i].getFirm();
            }
            return comparisonFirms;
        } else {
            // looking for other FIRMS, not employees
            List result = getFirm().getScape().findWithin(getFirm().getCoordinate(), null, false, nFriends/2);
            Cell[] comparisonFirms = new Firm[result.size()];
            result.toArray(comparisonFirms);
            return comparisonFirms;
        }
    }

    protected void findNewFriends() {
        friends = new Employee[nFriends];
        for (int i = 0; i < nFriends; i++) {
            friends[i] = null;
        }
        for (int i = 0; i < friends.length; i++) {
            Employee friendCandidate = null;
            boolean newFriend;
            do {
                //Find random candidate uniformally distributed across all employees
                int index = randomToLimit(scape.getSize());
                friendCandidate = (Employee) scape.get(index);
                newFriend = true;
                for (int j = 0; j < i; j++) {
                    if (friends[j] == friendCandidate) {
                        newFriend = false;
                        break;
                    }
                }
                //Keep looking if candidate is self or allready a friend...
            } while ((friendCandidate == this) || !newFriend);
            friends[i] = friendCandidate;
        }
    }

    public Color getColor() {
        //if (output() > firm.outputPerEmployee()) {
        //	return Color.green;
        //}
        //else {
        return Color.gray;
        //}
    }
}
