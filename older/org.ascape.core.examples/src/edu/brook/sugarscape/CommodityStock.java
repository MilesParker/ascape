/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
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
