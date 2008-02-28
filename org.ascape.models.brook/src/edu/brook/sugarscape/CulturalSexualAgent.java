/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.sugarscape;

import org.ascape.model.Agent;

public class CulturalSexualAgent extends SexualAgent {

    /**
     * 
     */
    private static final long serialVersionUID = 6104734513674513341L;

    private static int tagLength;

    private boolean[] tag;

    public void initialize() {
        super.initialize();
        tag = new boolean[tagLength];
        for (int i = 0; i < tag.length; i++) {
            //We do a draw for each random value instead of using values from large integers
            //to ensure that there are no artifacts.
            tag[i] = randomIs();
        }
    }

    public void play(Agent neighbor) {
        int position = randomToLimit(tag.length);
        ((CulturalSexualAgent) neighbor).setTagPosition(position, this.getTagPosition(position));
        //In case we changed majority...
        requestUpdate();
    }

    public static int getTagLength() {
        return tagLength;
    }

    public static void setTagLength(int _tagLength) {
        tagLength = _tagLength;
    }

    public boolean getTagPosition(int position) {
        return tag[position];
    }

    public void setTagPosition(int position, boolean value) {
        tag[position] = value;
    }

    public boolean[] getTag() {
        return tag;
    }

    public void setTag(boolean[] tag) {
        this.tag = tag;
    }

    public boolean getMajority() {
        int count0 = 0;
        int count1 = 0;
        int majoritySize = (int) Math.ceil(tag.length / 2);
        for (int i = 0; i < tag.length; i++) {
            if (!tag[i]) {
                count0++;
                if (count0 > majoritySize) {
                    return false;
                }
            } else {
                count1++;
                if (count1 > majoritySize) {
                    return true;
                }
            }
        }
        throw new RuntimeException("Internal Error in CulturalSexualAgent.getMajority");
    }
}
