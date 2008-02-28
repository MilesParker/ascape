/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */
package org.ascape.query;

import java.util.List;

import org.ascape.model.Agent;
import org.ascape.model.Scape;
import org.ascape.model.event.DefaultScapeListener;
import org.ascape.model.event.ScapeEvent;
import org.ascape.query.parser.BoolExprTree;
import org.ascape.query.parser.ParseException;
import org.ascape.query.parser.QTBooleanPhrase;
import org.ascape.query.parser.QTInput;
import org.ascape.util.Conditional;

/**

 * User: jmiller
 * Date: Dec 6, 2004
 * Time: 1:24:06 PM
 * To change this template use Options | File Templates.
 */

/**
 * Only the form-based searches have the option of being dynamic. By default, they are static searches. The dynamicSearch
 * flag will change that. Searches by user-input cannot be dynamic, due to the current implementation.
 */

public class Query extends DefaultScapeListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    // Can search on only 1 collection per query
    private Scape collection;

    private List results;

    // by default, all searches are dynamic.
    private boolean dynamicSearch = true;

    private String queryString;
    private QTInput query;

    private int searchSize;
    private int searchPosition;
    private int searchFound;

    private boolean evaluating;

    Conditional condition = new Conditional() {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public boolean meetsCondition(Object object) {
            searchPosition++;
            Agent agent = (Agent) object;
            boolean res = ((QTBooleanPhrase) query.jjtGetChild(0)).evaluate(agent);
            if (res) {
                searchFound++;
            }
            return res;
////            List descriptorsList = getDescriptors(agent);
////            for (Iterator accessorIterator = descriptorsList.iterator(); accessorIterator.hasNext();) {
////                PropertyDescriptor descriptor = (PropertyDescriptor) accessorIterator.next();
////                if (descriptor.getName().equalsIgnoreCase(propertyDescriptor.getName())) {
////                    System.out.println("descriptor name: " + descriptor.getName()  + ", getDescriptorValue) = " + getDescriptorValue(agent, descriptor)+", subvalue: " + subvalue);
//            if (getDescriptorValue(agent, propertyDescriptor) != null && subvalue.equalsIgnoreCase(SIMPLE_SEARCH)) {
////                        System.out.println("1.. simple search");
////                    if (getDescriptorValue(agent, descriptor) != null && subvalue.equalsIgnoreCase(ASTERISK)) {
//                res = compareObject(PropertyAccessor.getValue(object, propertyDescriptor));
////                        break;
//            } else if (getDescriptorValue(agent, propertyDescriptor) != null && subvalue.equalsIgnoreCase(ASTERISK)) {
////                        System.out.println("2. simple internal search");
////                    } else if (getDescriptorValue(agent, descriptor) != null && subvalue.equalsIgnoreCase(SIMPLE_SEARCH)) {
////                         //find in the list
//                PropertyAccessor pa = new PropertyAccessor(agent, propertyDescriptor);
//                List value = (List) pa.getValue();
//                for (Iterator iterator = value.iterator(); iterator.hasNext();) {
//                    Object o = iterator.next();
//                    if (compareComplex(o)) {
//                        res = true;
//                        break;
//                    }
//                }
////                        System.out.println("Returning FALSE");
////                        break;
//            } else if (getDescriptorValue(agent, propertyDescriptor) != null) {
////                        System.out.println("3.. complex internal search");
//                // complex search case
////                        System.out.println("Conditional.. 2");
//                PropertyAccessor pa = new PropertyAccessor(agent, propertyDescriptor);
//                List descriptors = getComplexSearchDescriptors(pa.getValue());
////                        System.out.println("Descriptors");
//                for (Iterator complexDescripIt = descriptors.iterator(); complexDescripIt.hasNext();) {
//                    PropertyDescriptor pd = (PropertyDescriptor) complexDescripIt.next();
//                    if (pd.getName().equalsIgnoreCase(subvalue)) {
//                        PropertyAccessor pa2 = new PropertyAccessor(pa.getValue(), pd);
////                                return complexCompare(pa2.getValue());
//                        if (compareComplex(pa2.getValue())) {
//                            res = true;
//                            break;
//                        }
//                    }
//                }
////                        System.out.println("Returning FALSE");
////                        break;
//            } else {
//                throw new IllegalStateException("PropertyDescriptor " + propertyDescriptor.getName() + "'s value is null.");
//            }
////                }
////            }
        }
    };

    /**
     * TODO: All of this. Mostly, make searching the same, regardless of simple or complex quotes, especially re: the
     * spacing between parameters.
     * String based. See comments below for more details, but essentially, a search with a boolean operator (complex) must
     * be enclosed by parens. Searches without one (simple) can be, but do not have to be.
     *
     * Complex Query:
     * A series of strings using parens and boolean operators.
     * For now, unlike the simple queries, there are no spaces allowed in the simple query portions. For example,
     * ((age>15) AND (color=blue)).
     * Complex queries do not yet allow for parameters enclosed in quotes.
     *
     * Simple Query:
     * assume each token is a param_operator_value broken up by BooleanOperators
     * example 1: age>15 example 2: color=blue.. Zero or One space is allowed.
     * New Note: When a search parameter is enclosed in quotes ("Holland Queen"), you must leave a space between the parameters:
     * value2 = "Holland Queen".
     * take each token and parse it into a search string
     * @param collection the collection to be searched
     * @param queryString
     */
    public Query(Scape collection, String queryString) throws ParseException {
        this(collection, queryString, true);
    }

    public Query(Scape collection, String queryString, boolean dynamic) throws ParseException {
        super.scape = collection; // for DefaultScapeListener
        dynamicSearch = dynamic;
        collection.addScapeListenerFirst(this);
        this.collection = collection;

        this.queryString = queryString;
        this.query = BoolExprTree.parse(queryString);
        query.validate(collection.getPrototypeAgent());
    }

    public void execute() {
        searchSize = collection.size();
        evaluating = true;
        results = collection.find(condition);
        evaluating = false;
    }

    public void scapeIterated(ScapeEvent scapeEvent) {
        super.scapeIterated(scapeEvent);
        if (dynamicSearch) {
            execute();
        }
        // todo: update results field in GUI
    }

    public List getResults() {
        return results;
    }

    public String getName() {
        return "Query";
    }

    public boolean isDynamicSearch() {
        return dynamicSearch;
    }

    public void setDynamicSearch(boolean dynamicSearch) {
        this.dynamicSearch = dynamicSearch;
    }

    public int getSearchSize() {
        return searchSize;
    }

    public int getSearchPosition() {
        return searchPosition;
    }

    public int getSearchFound() {
        return searchFound;
    }

    public boolean isEvaluating() {
        return evaluating;
    }

    public String getQueryString() {
        return queryString;
    }

    public QTInput getQuery() {
        return query;
    }
}

