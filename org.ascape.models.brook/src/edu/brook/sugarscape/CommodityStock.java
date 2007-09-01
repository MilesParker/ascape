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
 *
 * Alan Lockard
 * alockard@gmu.edu
 */

package edu.brook.sugarscape;

import org.ascape.model.Agent;

/**
 * A stock of a commodity, like sugar or spice.
 * Apt to be associated with a sugar or spice, or other kind of agent.
 *
 * @author Alan Lockard
 * @version 1.0
 */
public class CommodityStock {

    private float stock;
    private int metabolism;
    private String name = "Generic commodity stock";
    private Agent owner; // Who (what cell) owns this commodity source

    public void putStock(float more) {
        this.stock += more;
    }

    public void reduceStock(float less) {
        this.stock -= less;
    }

    public float getStock() {
        return stock;
    }

    public void setStock(float stock) {
//if (Float.isNaN (stock)) System.out.println(this.stock + " becomes " + stock);
        this.stock = stock;
    }

    public float takeStock() {
        float tempStock = stock;
        stock = 0;
        return tempStock;
    }

    public float takeStock(float amount) {
        if (amount > stock) {
            throw new RuntimeException("Tried to take more stock from an agent than agent had. Add a check for this.");
        }
        stock -= amount;
        return amount;
    }

    public void addStock(float stock) {
        setStock(this.stock + stock);
    }

    public int getMetabolism() {
        return metabolism;
    }

    public void setMetabolism(int metabolism) {
        this.metabolism = metabolism;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOwner(Agent _agent) {
        owner = _agent;
    }

    public Agent getOwner() {
        return owner;
    }
}
