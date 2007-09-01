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

import java.awt.Color;

import org.ascape.model.Scape;




public class ProfitFirm extends Firm {

    private double currentCompensation = 1 + Math.random() * .2;

    private double currentPrice = 1;

    private double cpProfit = 0.0;

    private double lpProfit = 0.0;

    protected boolean switchedLast = false;

    private double wealth = 100;

    private double compensationVelocity = Math.random() * .04;

    public static ProfitFirm formProfitFirm(Scape firms, Employee employee) {
        ProfitFirm firm = new ProfitFirm();
        firm.hire(employee);
        firm.lpSize = 1;
        firm.lpEffort = employee.effort();
        firms.add(firm);
        return firm;
    }

    public double currentPrice() {
        return currentPrice;
    }

    public double currentCompensation() {
        return currentCompensation;
    }

    /**
     * Makes the supplied employee a member from this firm.
     * @param employee the employee to hire
     **/
    public void hire(Employee employee) {
        super.hire(employee);
        calculateCompensation();
    }

    public Color getColor() {
        float green = Math.min(1, Math.max(0, (float) (cpProfit / 10)));
        float red = Math.min(1, Math.max(0, (float) (-cpProfit / 10)));
        return new Color(red, green, 0);
    }

    private void calculateCompensation() {
        cpProfit = output() - (currentCompensation() * getSize());
        if (getSize() > 1) {
            if (cpProfit < lpProfit) {
                compensationVelocity = -compensationVelocity;
                /*if (switchedLast) {
                    compensationVelocity = compensationVelocity / 2;
                }
                else {
                }
                switchedLast = true;*/
                //We switched last time so, lets try to get closer
            }
        } else {
            compensationVelocity = Math.abs(compensationVelocity);
        }
        if (cpProfit == lpProfit) {
            compensationVelocity = Math.random() * 0.04;
        }
        //else {
        //    compensationVelocity = compensationVelocity * 2;
        //}
        //compensationVelocity = (Math.random() * .1);
        //if (Math.random() > .8) {
        currentCompensation += compensationVelocity;
        //}
        if (currentCompensation < 0) {
            currentCompensation = 0;
        }
        //if ((currentCompensation() * getSize()) > wealth) {
        //   currentCompensation = wealth / getSize();
        //compensationVelocity = Math.random() * .02;
        //}
        lpProfit = cpProfit;
        //System.out.println("Firm: " + (cpProfit / getSize()));
    }

    public void iterate() {
        calculateCompensation();
        wealth = wealth + cpProfit;
        super.iterate();
    }
}
