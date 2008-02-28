/*
 * Copyright 2000 Miles T. Parker. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package name.milesparker.spatialipd;


public class PD2DGA_CLOSEST extends PD2DGA {

    /**
     * 
     */
    private static final long serialVersionUID = 5082904971311823486L;

    public void createScape() {
        super.createScape();
        getAgents().getRules().setSelected(PLAY_RANDOM_NEIGHBOR_RULE, false);
        getAgents().getRules().setSelected("Play Closest", true);
    }
}
