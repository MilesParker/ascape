/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
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

    /**
     * 
     */
    private static final long serialVersionUID = 5544200456273860689L;

    /**
     * The Constant CURRENT_FIRM_OPTION.
     */
    protected final static int CURRENT_FIRM_OPTION = -2;

    /**
     * The Constant NEW_FIRM_OPTION.
     */
    protected final static int NEW_FIRM_OPTION = -1;

    /**
     * The friends.
     */
    private Employee[] friends;

    /**
     * The firm.
     */
    protected Firm firm;

    /**
     * The firms.
     */
    private static Scape firms;

    /**
     * Size of friend network.
     */
    private static final int nFriends = 2;

    /**
     * Use friends or neighbors in comparison calculations.
     */
    protected static final boolean useFriends = false;

    /**
     * The utility.
     */
    protected UtilityFunction utility;

    /**
     * Effort level.
     */
    protected double cpEffort;

    /**
     * Effort level for last period.
     */
    private double lpEffort;

    /**
     * The proposed option.
     */
    protected static int proposedOption;

    /**
     * Instantiates a new employee.
     */
    public Employee() {
        super();
    }

    /* (non-Javadoc)
     * @see org.ascape.model.Cell#initialize()
     */
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

    /**
     * Gets the firms.
     * 
     * @return the firms
     */
    public Scape getFirms() {
        return firms;
    }

    /**
     * Sets the firms.
     * 
     * @param firms
     *            the new firms
     */
    public void setFirms(Scape firms) {
        Employee.firms = firms;
    }

    /* (non-Javadoc)
     * @see org.ascape.model.Agent#iterate()
     */
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

    /**
     * Effort.
     * 
     * @return the double
     */
    public double effort() {
        return cpEffort;
    }

    /**
     * Output.
     * 
     * @return the double
     */
    public double output() {
        return cpEffort;
    }

    /**
     * Maximize utility for effort and options.
     */
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

    /**
     * Maximize utility for effort.
     * 
     * @return the double
     */
    public double maximizeUtilityForEffort() {
        return utility.maximize();
    }

    /**
     * Employee joins the specified firm.
     * 
     * @param firm
     *            the firm
     */
    public void join(Firm firm) {
        if (this.firm != null) {
            leave(this.firm);
        }
        this.firm = firm;
        firm.hire(this);
    }

    /**
     * Employee forms a new firm.
     */
    private void formFirm() {
        cpEffort = utility.theta;
        if (this.firm != null) {
            leave(this.firm);
        }
        firm = Firm.form(firms, this);
    }

    /**
     * Leave.
     * 
     * @param firm
     *            the firm
     */
    private void leave(Firm firm) {
        this.firm = null;
        firm.terminate(this);
    }

    /**
     * Gets the firm.
     * 
     * @return the firm
     */
    public Firm getFirm() {
        return firm;
    }

    /**
     * Returns the firms output. In this implementation: the sum of all
     * employee's outputs.
     * 
     * @return the double
     */
    public double lastPeriodEffort() {
        return lpEffort;
    }

    /**
     * Gets the comparison firms.
     * 
     * @return the comparison firms
     */
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

    /**
     * Find new friends.
     */
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

    /* (non-Javadoc)
     * @see org.ascape.model.Agent#getColor()
     */
    public Color getColor() {
        //if (output() > firm.outputPerEmployee()) {
        //	return Color.green;
        //}
        //else {
        return Color.gray;
        //}
    }
}
