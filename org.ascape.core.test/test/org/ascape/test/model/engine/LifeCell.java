/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */
package org.ascape.test.model.engine;

import java.awt.Color;

import org.ascape.model.Cell;
import org.ascape.util.Conditional;


/*
 * This software is confidential and proprietary to
 * NuTech Solutions, Inc.  No portion of this software may
 * be reproduced, published, used, or disclosed
 * to others without the WRITTEN authorization
 * of NuTech Solutions.
 *             Copyright (c) 2004
 *                NuTech Solutions,Inc.
 *
 * NUTECH SOLUTIONS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * OR NON-INFRINGEMENT. NUTECH SOLUTIONS SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * 
 *
 * User: Miles Parker
 * Date: Feb 24, 2005
 * Time: 10:55:14 AM
 */

/**
 * This is the class that defines the state of a cell within the automata.
 */
public class LifeCell extends Cell {

    /**
     * 
     */
    private static final long serialVersionUID = -5249250491439819467L;

    /**
     * A conditional is a class that is used to determine whether some object passed
     * to it meets a set condition. In this case, it simply determines wheteher a life
     * cell is alive or not. In Java, classes like this serve a similar role as function
     * pointers in C++; they allow us to use a common method (in this case, countNeighbors)
     * on any kind of object.
     */
    public final static Conditional IS_ALIVE = new Conditional() {
        /**
         * 
         */
        private static final long serialVersionUID = 522038176098992877L;

        public boolean meetsCondition(Object object) {
            return ((LifeCell) object).alive;
        }
    };

    //Is the cell currently alive?
    public boolean alive;
    //Stores the cells 'next' alive state; that is, the state that the cell should be in
    //when all cells have finsihed calculating their next state.
    public boolean nextAlive;

    public LifeCell() {
    }

    public void initialize() {
        super.initialize();
        if (getRandom().nextFloat() < ConwayLife.getInitialAliveDensity()) {
            alive = true;
        } else {
            alive = false;
        }
    }

    public boolean calculateNextAlive() {
        int neighborsAlive = countNeighbors(IS_ALIVE);
        if (alive) {
            if ((neighborsAlive < 2) || (neighborsAlive > 3)) {
                return false;
            }
        }
//Dead
        else {
            if (neighborsAlive == 3) {
                return true;
            }
        }
        return alive;
    }

    public Color getColor() {
        if (!alive) {
            return Color.black;
        } else {
            return Color.white;
        }
    }
}
