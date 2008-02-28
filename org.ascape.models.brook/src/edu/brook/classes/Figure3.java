/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.classes;


public class Figure3 extends Figure2 {

    /**
     * 
     */
    private static final long serialVersionUID = 3635094581503240509L;

    public void createScape() {
        super.createScape();
        agents.setPrototypeAgent(new Bargainer() {
            /**
             * 
             */
            private static final long serialVersionUID = -3989370190199939534L;

            public void initialize() {
                super.initialize();
                initializeMemoryHiLow();
            }
        });
    }
}
