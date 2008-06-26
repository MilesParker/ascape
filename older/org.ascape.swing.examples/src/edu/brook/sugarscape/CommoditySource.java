/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
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
