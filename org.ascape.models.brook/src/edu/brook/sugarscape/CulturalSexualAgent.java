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
 */

package edu.brook.sugarscape;

import org.ascape.model.Agent;

public class CulturalSexualAgent extends SexualAgent {

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
