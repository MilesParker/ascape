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
 * Miles Parker
 * The Brookings Institution
 * Washington, D.C.
 * mparker@brook.edu
 * (202) 797-6136
 *
 * Please contact us for permission to use, copy, modify, or distribute this software.
 */

package edu.brook.firms;

import org.ascape.model.Agent;
import org.ascape.model.Cell;
import org.ascape.model.rule.Rule;

class MaximizeOptionsAndEffort extends Rule {

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

public class Worker extends Employee {

    private final static double costOfMoving = 0.0;

    public Worker() {
        super();
    }

    public void scapeCreated() {
        scape.addRule(new MaximizeOptionsAndEffort());
    }

    public void initialize() {
        if (useFriends) {
            findNewFriends();
        }
        utility = new UtilityFunction();
        utility.theta = Math.abs(getRandom().nextDouble());
        cpEffort = utility.theta;
        //firm = ProfitFirm.formProfitFirm(((ScapeList) getHostScape()), this);
    }

    public Firm getFirm() {
        return firm;
    }

    public double effort() {
        return 1;
    }

    public double output() {
        return 1;
    }

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
