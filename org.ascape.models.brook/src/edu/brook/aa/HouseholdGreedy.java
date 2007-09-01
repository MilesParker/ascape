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
 * Artificial Anasazi - Long House Valley
 * see _Understanding Anasazi Culture Change Through Agent-Based Modeling_
 * Jeffrey S. Dean, George J. Gumerman, Joshua M. Epstein, Robert Axtell,
 * Alan C. Swedlund, Miles Parker, and Steven McCarroll
 *
 * Java Implementation:
 * Miles Parker
 * mparker@brook.edu
 * Original C++ Implementation:
 * Steven McCarroll
 * The Brookings Institution
 * Washington, D.C.
 */

package edu.brook.aa;



public class HouseholdGreedy extends HouseholdAggregate {

    public Farm[] bufferFarms = new Farm[0];

    public void fission() {
        HouseholdAggregate child = new HouseholdGreedy();//(Household) this.clone();
        scape.add(child);
        child.initialize();
        child.setAge(0);
        giveMaizeGift(child);
        child.move();
        //if ((child.farm.getLocation() != null) && (child.settlement != null)) {
        //For now, record fissions regardless of successful move to match C++ code
        scape.getData().getStatCollector("Fissions").addValue(0.0);
        //}
        //System.out.println(child.age);
    }

    public void leave() {
        super.leave();
        for (int i = 0; i < bufferFarms.length; i++) {
            bufferFarms[i].leave();
        }
    }

    public void move() {
        super.move();
        /*if ((farm.getLocation() != null) && (settlement != null)) {
        	Vector locations = farm.getLocation().getNeighbors(new Conditional() {
			    public boolean meetsCondition(Object object) {
			    	return ((((Location) object).getFarm() == null) && (((Location) object).getSettlement() == null));
			    }
        	});
        	bufferFarms = new Farm[locations.size()];
        	bufferFarms = new Farm[locations.size()];
        	for (int i = 0; i < bufferFarms.length; i++) {
        		bufferFarms[i] = new Farm();
                bufferFarms[i].occupy(((Location) locations.elementAt(i)));
        	}
        }*/
    }
}
