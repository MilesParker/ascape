/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.examples.boids.view;

import java.util.ArrayList;
import java.util.List;

/*
 * This software is confidential and proprietary to
 * NuTech Solutions, Inc.  No portion of this software may
 * be reproduced, published, used, or disclosed
 * to others without the WRITTEN authorization
 * of NuTech Solutions.
 *             Copyright (c) 2002-2004
 *                NuTech Solutions,Inc.
 *
 * NUTECH SOLUTIONS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * OR NON-INFRINGEMENT. NUTECH SOLUTIONS SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * User: mparker
 * Date: Mar 27, 2003
 * Time: 12:15:19 PM
 * To change this template use Options | File Templates.
 */

public class Test {

    public static final void main(String[] args) {
        int shortest = Integer.MAX_VALUE;
        int cycles = 0;
        while (shortest > 17) {
            cycles++;
            List near = new ArrayList();
            List far = new ArrayList();
            List order = new ArrayList();
            near.add(new Integer(10));
            near.add(new Integer(5));
            near.add(new Integer(2));
            near.add(new Integer(1));
            int tripLength = 0;
            for (int i = 0; i < 3; i++) {
                Integer first = (Integer) near.remove((int) (Math.random() * near.size()));
                Integer second = (Integer) near.remove((int) (Math.random() * near.size()));
                tripLength += Math.max(first.intValue(), second.intValue());
                far.add(first);
                far.add(second);
                order.add(first);
                order.add(second);
                if (i < 2) {
                    Integer back = (Integer) far.remove((int) (Math.random() * far.size()));
                    tripLength += back.intValue();
                    far.remove(back);
                    near.add(back);
                    order.add(back);
                }
            }
            if (tripLength < shortest) {
                shortest = tripLength;
                System.out.println("Cycle: " + cycles);
                System.out.println(tripLength);
                System.out.println(order);
            }
        }
    }
}
