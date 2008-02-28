/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.firms;

import org.ascape.model.Agent;
import org.ascape.model.Cell;
import org.ascape.model.rule.Rule;

class MaximizeOptionsAndEffort extends Rule {

    /**
     * 
     */
    private static final long serialVersionUID = -8742177948793238525L;

    public MaximizeOptionsAndEffort() {
        super("Maximize Options and Effort");
    }

    public void execute(Agent agent) {
        Worker worker = (Worker) agent;
        worker.maximizeCompensation();
        if (Employee.proposedOption == Worker.CURRENT_FIRM_OPTION) {
            //Do nothing
        } else if (Employee.proposedOption == Employee.NEW_FIRM_OPTION) {
            ProfitFirm.formProfitFirm(scape.getScape(), worker);
            ;
        } else {
            //join(friends[proposedOption].getFirm());
            worker.join((Firm) worker.getComparisonFirms()[Employee.proposedOption]);
        }
        worker.cpEffort = worker.maximizeUtilityForEffort();
        worker.cpEffort = worker.cpEffort * ((ProfitFirm) worker.firm).currentCompensation();
    }
}

/**
 * The Class Worker.
 */
public class Worker extends Employee {

    /**
     * 
     */
    private static final long serialVersionUID = 246173895124782312L;
    /**
     * The Constant costOfMoving.
     */
    private final static double costOfMoving = 0.0;

    /**
     * Instantiates a new worker.
     */
    public Worker() {
        super();
    }

    /* (non-Javadoc)
     * @see org.ascape.model.Agent#scapeCreated()
     */
    public void scapeCreated() {
        scape.addRule(new MaximizeOptionsAndEffort());
    }

    /* (non-Javadoc)
     * @see edu.brook.firms.Employee#initialize()
     */
    public void initialize() {
        if (useFriends) {
            findNewFriends();
        }
        utility = new UtilityFunction();
        utility.theta = Math.abs(getRandom().nextDouble());
        cpEffort = utility.theta;
        //firm = ProfitFirm.formProfitFirm(((ScapeList) getHostScape()), this);
    }

    /* (non-Javadoc)
     * @see edu.brook.firms.Employee#getFirm()
     */
    public Firm getFirm() {
        return firm;
    }

    /* (non-Javadoc)
     * @see edu.brook.firms.Employee#effort()
     */
    public double effort() {
        return 1;
    }

    /* (non-Javadoc)
     * @see edu.brook.firms.Employee#output()
     */
    public double output() {
        return 1;
    }

    /**
     * Maximize compensation.
     */
    public void maximizeCompensation() {
        proposedOption = CURRENT_FIRM_OPTION;
        double proposedCompensation = ((ProfitFirm) firm).currentCompensation() * (1 + costOfMoving);
        //System.out.print(proposedCompensation);
        //worker will assume he or she can make as much as the market price for one unit of output
        if (proposedCompensation < 1) {
            proposedOption = NEW_FIRM_OPTION;
            //what worker can earn for one unit of output
            proposedCompensation = ((ProfitFirm) firm).currentCompensation() * (1 + costOfMoving);
            //System.out.print("\t" + proposedCompensation);
        }
        Cell[] comparisonFirms = getComparisonFirms();
        for (int i = 0; i < comparisonFirms.length; i++) {
            if (proposedCompensation < ((ProfitFirm) comparisonFirms[i]).currentCompensation()) {
                proposedOption = i;
                proposedCompensation = ((ProfitFirm) comparisonFirms[i]).currentCompensation() * (1 + costOfMoving);
                //System.out.print("\t" + proposedCompensation);
            }
        }
        //System.out.println();
    }
}
