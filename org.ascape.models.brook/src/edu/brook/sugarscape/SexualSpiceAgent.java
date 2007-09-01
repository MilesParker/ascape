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


public class SexualSpiceAgent extends NegotiatingAgent {

    private static int minReproductionBeginsAge = 12;

    private static int maxReproductionBeginsAge = 15;

    private static int minFemaleReproductionEndsAge = 40;

    private static int maxFemaleReproductionEndsAge = 50;

    private static int minMaleReproductionEndsAge = 50;

    private static int maxMaleReproductionEndsAge = 60;

    protected static int minInitialSugar = 25;

    protected static int maxInitialSugar = 50;

    protected static int minInitialSpice = 25;

    protected static int maxInitialSpice = 50;

    private boolean female;

    private float initialSugarEndowment;

    private float initialSpiceEndowment;

    private int beginReproductionAge;

    private int endReproductionAge;

    public void initialize() {
        super.initialize();
        initialSugarEndowment = getSugar().getStock();
        initialSpiceEndowment = getSpice().getStock();
        female = randomIs();
        beginReproductionAge = randomInRange(minReproductionBeginsAge, maxReproductionBeginsAge);
        if (female) {
            endReproductionAge = randomInRange(minFemaleReproductionEndsAge, maxFemaleReproductionEndsAge);
        } else {
            endReproductionAge = randomInRange(minMaleReproductionEndsAge, maxMaleReproductionEndsAge);
        }
    }

    public void setVision(int vision) {
        super.setVision(vision);
    }

    public void setSugarMetabolism(int metabolism) {
        super.setSugarMetabolism(metabolism);
    }

    public void setSpiceMetabolism(int metabolism) {
        super.setSpiceMetabolism(metabolism);
    }

    public float getSugarContributionToChild() {
        return initialSugarEndowment / 2;
    }

    public float getSpiceContributionToChild() {
        return initialSpiceEndowment / 2;
    }

    public boolean isFertile() {
//System.out.println(getSugar().getStock() + " >= " + initialSugarEndowment + "? "
//              + getSpice().getStock() + " >= " + initialSpiceEndowment + "? " + age
//              + " >= " + beginReproductionAge + "? " + age + " <= " + endReproductionAge
//              + "? " + ((getSugar().getStock() >= initialSugarEndowment) &&
//                       (getSpice().getStock() >= initialSpiceEndowment) &&
//                        (age >= beginReproductionAge) && (age <= endReproductionAge)));
        return ((getSugar().getStock() >= initialSugarEndowment) && (getSpice().getStock() >= initialSpiceEndowment) && (age >= beginReproductionAge) && (age <= endReproductionAge));
    }

    public boolean isMale() {
        return !female;
    }

    public boolean isFemale() {
        return female;
    }

    public void setFemale(boolean female) {
        this.female = female;
    }
}
