/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */
package org.ascape.query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.ascape.model.Agent;


/**

 * User: jmiller
 * Date: Dec 6, 2004
 * Time: 4:18:13 PM
 * To change this template use Options | File Templates.
 */

/**
 * AND and OR accept a list of lists, and then applies the requested operation on the contents of the lists.
 */
public abstract class BooleanOperator {

    public static BooleanOperator AND = new BooleanOperator() {
        // walk through each list, and be sure that each item is on all the listsp
        public List operate(List initialResults) {
            ArrayList firstList = (ArrayList) initialResults.get(0);
            ArrayList forDeletion = new ArrayList();
            for (Iterator firstListIterator = firstList.iterator(); firstListIterator.hasNext();) {
                Agent agent = (Agent) firstListIterator.next();
                for (int i = 1; i < initialResults.size(); i++) { // skip 1st in list
                    ArrayList temp = (ArrayList) initialResults.get(i);
                    if (temp.contains(agent) == false) {
                        forDeletion.add(agent);
                    }
                }
            }

            // deletion sweep
            for (Iterator deletionIterator = forDeletion.iterator(); deletionIterator.hasNext();) {
                Agent agent = (Agent) deletionIterator.next();
                firstList.remove(agent);
            }

            return firstList;
        }
    };

    public static BooleanOperator OR = new BooleanOperator() {
        public List operate(List initialResults) {
            Set resultSet = new HashSet();
            for (Iterator initialResultsIterator = initialResults.iterator(); initialResultsIterator.hasNext();) {
                ArrayList arrayList = (ArrayList) initialResultsIterator.next();
                resultSet.addAll(arrayList);
            }
            return new ArrayList(resultSet);
        }
    };

    public abstract List operate(List initialResults);

}
