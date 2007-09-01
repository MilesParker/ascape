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
 * _Sugarscape_
 * See _Growing Artificial Societies: Social Science from the Ground Up_
 * Brookings Institution Press, The MIT Press
 * Joshua Epstein
 * jepstein@brook.edu
 * Robert Axtell
 * raxtell@brook.edu
 *
 * Miles Parker
 * mparker@brook.edu

 * http://www.brook.edu/es/dynamics/models/ascape
 * The Brookings Institution
 * Washington, D.C.
 * Alan Lockard
 * alockard@gmu.edu
 */

package edu.brook.sugarscape;

import org.ascape.model.Cell;

/**
 * Source of a commodity, such as sugar or spice,
 * in a sugarscape
 */
public class CommoditySource {

//    public static float moundness = .004f; // was .004

    private Cell owner; // Who (what cell) owns this commodity source

    private float capacity;

    private String name = "Generic commodity";

    private float quantity;  // was sugar

//    public double getValue(Object object) {
//        return (1.0 - (double) ((SugarCell) object).getSugar() / (double) SugarCell.MAX_SUGAR);
//    }

    public void growBackEpsilon(float epsilon) {
        //We test (instead of just incrementing) so we know if a view update
        //is required or not
        if (quantity < capacity) {
            quantity += epsilon;
            if (quantity > capacity) {
                quantity = capacity;
            }
            owner.requestUpdate();
        }
    }

    public void growBack1() {
        //We test (instead of just incrementing) so we know if a view update
        //is required or not
        if (quantity < capacity) {
            quantity++;
            if (quantity > capacity) {
                quantity = capacity;
            }
            owner.requestUpdate();
        }
    }

    public void growBackInf() {
        if (quantity < capacity) {
            quantity = capacity;
            owner.requestUpdate();
        }
    }

    public float takeQuantity() {
        float tempQuantity = quantity;
        quantity = 0;
        return tempQuantity;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public float getCapacity() {
        return capacity;
    }

    public void setCapacity(float capacity) {
        this.capacity = capacity;
    }

    public int getDefaultValue() {
        return (int) quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOwner(Cell _cell) {
        owner = _cell;
    }

    public Cell getOwner() {
        return owner;
    }
}
