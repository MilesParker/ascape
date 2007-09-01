/*
 * (c) 1998-2000 The Brookings Institution, All Rights Reserved
 *
 * Permission to use this software and its documentation for non-commercial
 * purposes and without fee is hereby granted, provided this copyright statement
 * is included. Please contact us for permission for redistribution and other uses.
 *
 * BROOKINGS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * OR NON-INFRINGEMENT. BROOKINGS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * _Emergence of classes_
 * See _The Emergence of Economic Classes in an SpecializedAgent-Based Bargaining Model_
 * Joshua Epstein
 * jepstein@brook.edu
 * Robert Axtell
 * raxtell@brook.edu
 * Peyton Young
 * pyoung@brook.edu
 *
 * Implementation:
 * Miles Parker
 * mparker@brook.edu
 * The Brookings Institution
 * Washington, D.C.
 */

package edu.brook.classes;


public class BargainingModelPeyton extends BargainingModelTwo {

    public void createScape() {
        super.createScape();
        agents.setPrototypeAgent(new PeytonBargainer());
        setLowPayoff(0.10f);
        setMediumPayoff(0.30f);
        setHighPayoff(0.50f);
    }
}
