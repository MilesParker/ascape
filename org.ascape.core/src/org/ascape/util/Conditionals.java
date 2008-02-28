/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.util;


/**
 * The Class Conditionals.
 */
public class Conditionals {

    /**
     * And.
     * 
     * @param op1
     *            the op1
     * @param op2
     *            the op2
     * @return the conditional
     */
    public final static Conditional and(final Conditional op1, final Conditional op2) {
        if ((op1 != null) && (op2 != null)) {
            return new Conditional() {
                /**
                 * 
                 */
                private static final long serialVersionUID = 1L;

                public boolean meetsCondition(Object object) {
                    return op1.meetsCondition(object) && op2.meetsCondition(object);
                }
            };
        } else {
            if (op1 != null) {
                return op1;
            } else {
                //op2 may be null or not null, in either case the correct rsult will return.
                return op2;
            }
        }
    }

    /**
     * Or.
     * 
     * @param op1
     *            the op1
     * @param op2
     *            the op2
     * @return the conditional
     */
    public final static Conditional or(final Conditional op1, final Conditional op2) {
        return new Conditional() {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            public boolean meetsCondition(Object object) {
                return op1.meetsCondition(object) || op2.meetsCondition(object);
            }
        };
    }

    /**
     * Not equal.
     * 
     * @param compare1
     *            the compare1
     * @return the conditional
     */
    public static final Conditional notEqual(final Object compare1) {
        return new Conditional() {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            public boolean meetsCondition(Object compare2) {
                return !compare1.equals(compare2);
            }
        };
    }

    /**
     * Equal.
     * 
     * @param compare1
     *            the compare1
     * @return the conditional
     */
    public static final Conditional equal(final Object compare1) {
        return new Conditional() {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            public boolean meetsCondition(Object compare2) {
                return compare1.equals(compare2);
            }
        };
    }
}
