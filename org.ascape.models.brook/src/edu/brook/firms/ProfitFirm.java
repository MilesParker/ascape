/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.firms;

import java.awt.Color;

import org.ascape.model.Scape;




/**
 * The Class ProfitFirm.
 */
public class ProfitFirm extends Firm {

    /**
     * 
     */
    private static final long serialVersionUID = 8732514745576095705L;

    /**
     * The current compensation.
     */
    private double currentCompensation = 1 + Math.random() * .2;

    /**
     * The current price.
     */
    private double currentPrice = 1;

    /**
     * The cp profit.
     */
    private double cpProfit = 0.0;

    /**
     * The lp profit.
     */
    private double lpProfit = 0.0;

    /**
     * The switched last.
     */
    protected boolean switchedLast = false;

    /**
     * The wealth.
     */
    private double wealth = 100;

    /**
     * The compensation velocity.
     */
    private double compensationVelocity = Math.random() * .04;

    /**
     * Form profit firm.
     * 
     * @param firms
     *            the firms
     * @param employee
     *            the employee
     * @return the profit firm
     */
    public static ProfitFirm formProfitFirm(Scape firms, Employee employee) {
        ProfitFirm firm = new ProfitFirm();
        firm.hire(employee);
        firm.lpSize = 1;
        firm.lpEffort = employee.effort();
        firms.add(firm);
        return firm;
    }

    /**
     * Current price.
     * 
     * @return the double
     */
    public double currentPrice() {
        return currentPrice;
    }

    /**
     * Current compensation.
     * 
     * @return the double
     */
    public double currentCompensation() {
        return currentCompensation;
    }

    /**
     * Makes the supplied employee a member from this firm.
     * 
     * @param employee
     *            the employee to hire
     */
    public void hire(Employee employee) {
        super.hire(employee);
        calculateCompensation();
    }

    /* (non-Javadoc)
     * @see edu.brook.firms.Firm#getColor()
     */
    public Color getColor() {
        float green = Math.min(1, Math.max(0, (float) (cpProfit / 10)));
        float red = Math.min(1, Math.max(0, (float) (-cpProfit / 10)));
        return new Color(red, green, 0);
    }

    /**
     * Calculate compensation.
     */
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

    /* (non-Javadoc)
     * @see edu.brook.firms.Firm#iterate()
     */
    public void iterate() {
        calculateCompensation();
        wealth = wealth + cpProfit;
        super.iterate();
    }
}
