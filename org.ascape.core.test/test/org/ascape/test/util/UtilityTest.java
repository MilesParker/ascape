/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.test.util;

import java.util.Locale;

import junit.framework.TestCase;

import org.ascape.util.Utility;
import org.ascape.util.data.DataPointConcrete;

public class UtilityTest extends TestCase {

    public UtilityTest(String name) {
        super(name);
    }

    public void testFormatToString() {
        if (Locale.getDefault().equals(Locale.US)) {
            assertTrue(Utility.formatToString(0.00123, 2).equals("0.00"));
            assertTrue(Utility.formatToString(0.0123, 2).equals("0.01"));
            assertTrue(Utility.formatToString(123.12, 0).equals("123"));
            assertTrue(Utility.formatToString(123.12, 2).equals("123.12"));
            assertTrue(Utility.formatToString(123.12, 4).equals("123.1200"));
            assertTrue(Utility.formatToString(333222111.1234567, 2).equals("333222111.12"));
            assertTrue(Utility.formatToString(1.1E9, 0).equals("1E9"));
            assertTrue(Utility.formatToString(1.1E9, 2).equals("1.10E9"));

            assertTrue(Utility.formatToString(-0.00123, 2).equals("-0.00"));
            assertTrue(Utility.formatToString(-0.0123, 2).equals("-0.01"));
            assertTrue(Utility.formatToString(-123.12, 0).equals("-123"));
            assertTrue(Utility.formatToString(-123.12, 2).equals("-123.12"));
            assertTrue(Utility.formatToString(-123.12, 4).equals("-123.1200"));
            assertTrue(Utility.formatToString(-333222111.1234567, 2).equals("-333222111.12"));
            assertTrue(Utility.formatToString(-1.1E9, 0).equals("-1E9"));
            assertTrue(Utility.formatToString(-1.1E9, 2).equals("-1.10E9"));
        } else {
            System.out.println("Cannot test formatToString() method because default locale is not US.");
        }
    }

    public void testRemoveQualifiers() {
        assertTrue(Utility.removeQualifiers("Min Blah").equals("Blah"));
    }

    public void testTanh() {
/*
x=100.0 t1=1.0 t=1.0 t-t1/=0.0 t2-t/=0.0 -t2--t/=0.0
x=10.0 t1=0.9999999958776926 t=0.9999999958776926 t-t1/=0.0 t2-t/=0.0 -t2--t/=0.0
x=1.0 t1=0.7615941559557649 t=0.7615941559557649 t-t1/=0.0 t2-t/=0.0 -t2--t/=0.0
x=0.1 t1=0.0996679946249559 t=0.0996679946249559 t-t1/=0.0 t2-t/=0.0 -t2--t/=0.0
x=0.01 t1=0.00999966667999938 t=0.00999966667999938 t-t1/=0.0 t2-t/=0.0 -t2--t/=0.0
x=0.0010 t1=9.999996666668063E-4 t=9.999996666668063E-4 t-t1/=0.0 t2-t/=0.0 -t2--t/=0.0
x=1.0E-4 t1=9.999999966668898E-5 t=9.999999966668898E-5 t-t1/=0.0 t2-t/=0.0 -t2--t/=0.0
x=1.0E-5 t1=9.999999999621023E-6 t=9.999999999621023E-6 t-t1/=0.0 t2-t/=0.0 -t2--t/=0.0
x=1.0000000000000002E-6 t1=9.999999999727446E-7 t=9.999999999727446E-7 t-t1/=0.0 t2-t/=0.0 -t2--t/=0.0
x=1.0000000000000002E-7 t1=1.0000000000000002E-7 t=9.999999994736393E-8 t-t1/=-5.263609641515011E-10 t2-t/=0.0 -t2--t/=0.0
x=1.0000000000000002E-8 t1=1.0000000000000002E-8 t=9.99999993922529E-9 t-t1/=-6.07747115.3.0832E-9 t2-t/=0.0 -t2--t/=0.0
x=1.0000000000000003E-9 t1=1.0000000000000003E-9 t=1.0000000272292198E-9 t-t1/=2.7229219498755795E-8 t2-t/=0.0 -t2--t/=0.0
x=1.0000000000000003E-10 t1=1.0000000000000003E-10 t=1.000000082740371E-10 t-t1/=8.274037070416421E-8 t2-t/=0.0 -t2--t/=0.0
x=1.0000000000000003E-11 t1=1.0000000000000003E-11 t=1.000000082740371E-11 t-t1/=8.274037073647596E-8 t2-t/=0.0 -t2--t/=0.0
x=1.0000000000000002E-12 t1=1.0000000000000002E-12 t=1.0000333894311098E-12 t-t1/=3.3389431109571946E-5 t2-t/=0.0 -t2--t/=0.0
x=1.0000000000000002E-13 t1=1.0000000000000002E-13 t=9.997558336749535E-14 t-t1/=-2.441663250466919E-4 t2-t/=0.0 -t2--t/=0.0
x=1.0000000000000002E-14 t1=1.0000000000000002E-14 t=9.992007221626409E-15 t-t1/=-7.9927783735.3.01E-4 t2-t/=0.0 -t2--t/=0.0
x=1.0E-15 t1=1.0E-15 t=1.0547118733938987E-15 t-t1/=0.05471187339389863 t2-t/=0.0 -t2--t/=0.0
x=1.0000000000000001E-16 t1=1.0000000000000001E-16 t=5.551115123125783E-17 t-t1/=-0.4448884876874218 t2-t/=0.0 -t2--t/=0.0
x=1.0E-17 t1=1.0E-17 t=0.0 t-t1/=-1.0 t2-t/=0.0 -t2--t/=0.0
x=1.0E-18 t1=1.0E-18 t=0.0 t-t1/=-1.0 t2-t/=0.0 -t2--t/=0.0
x=1.0000000000000001E-19 t1=1.0000000000000001E-19 t=0.0 t-t1/=-1.0 t2-t/=0.0 -t2--t/=0.0
x=1.0000000000000001E-20 t1=1.0000000000000001E-20 t=0.0 t-t1/=-1.0 t2-t/=0.0 -t2--t/=0.0
x=1.0000000000000001E-21 t1=1.0000000000000001E-21 t=0.0 t-t1/=-1.0 t2-t/=0.0 -t2--t/=0.0
x=1.0E-22 t1=1.0E-22 t=0.0 t-t1/=-1.0 t2-t/=0.0 -t2--t/=0.0
x=1.0000000000000001E-23 t1=1.0000000000000001E-23 t=0.0 t-t1/=-1.0 t2-t/=0.0 -t2--t/=0.0
x=1.0000000000000001E-24 t1=1.0000000000000001E-24 t=0.0 t-t1/=-1.0 t2-t/=0.0 -t2--t/=0.0
x=1.0000000000000002E-25 t1=1.0000000000000002E-25 t=0.0 t-t1/=-1.0 t2-t/=0.0 -t2--t/=0.0
x=1.0000000000000002E-26 t1=1.0000000000000002E-26 t=0.0 t-t1/=-1.0 t2-t/=0.0 -t2--t/=0.0
*/
/*
        double x = 100.;
        for(int i=1; i<30; i++) {
            System.out.println("x="+x+" t1="+Utility.tanh1(x)+" t="+Utility.tanh(x)+" t-t1/="+
                    (Utility.tanh(x)-Utility.tanh1(x))/Utility.tanh1(x)+" t2-t/="+
                    (Utility.tanh2(x)-Utility.tanh(x))/Utility.tanh1(x)+" -t2--t/="+
                    (Utility.tanh2(-x)-Utility.tanh(-x))/Utility.tanh1(x));
            x = x / 10.;
        }
*/

        assertTrue(DataPointConcrete.equals(Utility.tanh(Double.POSITIVE_INFINITY), 1.0));
        assertTrue(DataPointConcrete.equals(Utility.tanh(23.0), 0.99999999999999999998));
        assertTrue(DataPointConcrete.equals(Utility.tanh(2.3), 0.98009639626619135685));
        assertTrue(DataPointConcrete.equals(Utility.tanh(0.023), 0.022995945191328686897));
        assertTrue(DataPointConcrete.equals(Utility.tanh(1.0e-5), 9.99999999966666666668e-6));
        assertTrue(DataPointConcrete.equals(Utility.tanh(2.3e-8), 2.2999999999999995944e-8));

        assertTrue(Math.abs((Utility.tanh(23.0) - 0.99999999999999999998) / 0.99999999999999999998) < 1.0e-13);
        assertTrue(Math.abs((Utility.tanh(21.0) - 0.99999999999999999885) / 0.99999999999999999885) < 1.0e-13);
        assertTrue(Math.abs((Utility.tanh(2.3) - 0.98009639626619135685) / 0.98009639626619135685) < 1.0e-13);
        assertTrue(Math.abs((Utility.tanh(0.023) - 0.022995945191328686897) / 0.022995945191328686897) < 1.0e-13);
        assertTrue(Math.abs((Utility.tanh(1.0e-4) - 0.000099999999666666668000) / 0.000099999999666666668000) < 1.0e-12);
        assertTrue(Math.abs((Utility.tanh(1.0e-5) - 9.99999999966666666668e-6) / 9.99999999966666666668e-6) < 1.0e-11);
        //               Utility.tanh(1.0e-5) = 9.999999999621023E-6, which is correct to 11 significant digits
        assertTrue(Math.abs((Utility.tanh(2.0e-6) - 1.9999999999973333333333376e-6) / 1.9999999999973333333333376e-6) < 1.0e-12);
        assertTrue(Math.abs((Utility.tanh(1.0e-6) - 9.99999999999666666666667e-7) / 9.99999999999666666666667e-7) < 1.0e-12);
        assertTrue(Math.abs((Utility.tanh(0.5e-6) - 4.999999999999583333333333e-7) / 4.999999999999583333333333e-7) < 1.0e-13);
        assertTrue(Math.abs((Utility.tanh(2.3e-8) - 2.2999999999999995944e-8) / 2.2999999999999995944e-8) < 1.0e-13);

        assertTrue(DataPointConcrete.equals(Utility.tanh(Double.NEGATIVE_INFINITY), -1.0));
        assertTrue(DataPointConcrete.equals(Utility.tanh(-23.0), -0.99999999999999999998));
        assertTrue(DataPointConcrete.equals(Utility.tanh(-2.3), -0.98009639626619135685));
        assertTrue(DataPointConcrete.equals(Utility.tanh(-0.023), -0.022995945191328686897));
        assertTrue(DataPointConcrete.equals(Utility.tanh(-1.0e-5), -9.99999999966666666668e-6));
        assertTrue(DataPointConcrete.equals(Utility.tanh(-2.3e-8), -2.2999999999999995944e-8));

        assertTrue(Math.abs((Utility.tanh(-23.0) - -0.99999999999999999998) / 0.99999999999999999998) < 1.0e-13);
        assertTrue(Math.abs((Utility.tanh(-21.0) - -0.99999999999999999885) / 0.99999999999999999885) < 1.0e-13);
        assertTrue(Math.abs((Utility.tanh(-2.3) - -0.98009639626619135685) / 0.98009639626619135685) < 1.0e-13);
        assertTrue(Math.abs((Utility.tanh(-0.023) - -0.022995945191328686897) / 0.022995945191328686897) < 1.0e-13);
        assertTrue(Math.abs((Utility.tanh(-1.0e-4) - -0.000099999999666666668000) / 0.000099999999666666668000) < 1.0e-12);
        assertTrue(Math.abs((Utility.tanh(-1.0e-5) - -9.99999999966666666668e-6) / 9.99999999966666666668e-6) < 1.0e-11);
        assertTrue(Math.abs((Utility.tanh(-2.0e-6) - -1.9999999999973333333333376e-6) / 1.9999999999973333333333376e-6) < 1.0e-12);
        assertTrue(Math.abs((Utility.tanh(-1.0e-6) - -9.99999999999666666666667e-7) / 9.99999999999666666666667e-7) < 1.0e-12);
        assertTrue(Math.abs((Utility.tanh(-0.5e-6) - -4.999999999999583333333333e-7) / 4.999999999999583333333333e-7) < 1.0e-13);
        assertTrue(Math.abs((Utility.tanh(-2.3e-8) - -2.2999999999999995944e-8) / 2.2999999999999995944e-8) < 1.0e-13);

        assertTrue(DataPointConcrete.equals(Utility.tanh(0.0), 0.0));
        assertTrue(Double.isNaN(Utility.tanh(Double.NaN)));
    }
}
