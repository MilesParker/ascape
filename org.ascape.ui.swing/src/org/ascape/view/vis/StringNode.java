/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */
package org.ascape.view.vis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * User: jmiller
 * Date: Jan 18, 2005
 * Time: 12:20:04 PM
 * To change this template use Options | File Templates.
 */
public class StringNode extends DefaultMutableTreeNode {

    /**
     * 
     */
    private static final long serialVersionUID = 996862836806940457L;

    String string;

    boolean booleanOp;

    public StringNode(String string) {
        this(string, false);
    }
    public StringNode(String string, boolean op) {
        super();
        this.string = string;
        this.booleanOp = op;
    }

    public boolean isLeaf() {
        boolean leaf = true;
        if (children == null || children.isEmpty()) { // children is null into a node is added
            System.out.println("Heads up -- this node has no children. String: " + string);
        } else {
            for (Iterator iterator = children.iterator(); iterator.hasNext();) {
                StringNode child = (StringNode) iterator.next();
                if (child.getString().split("\\s").length > 1) {
                    leaf = false;
                }
            }
        }

        return leaf;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public Object getUserObject() {
        return string;
    }

    public boolean isBooleanOp() {
        return booleanOp;
    }

    // utility method
    // returns enumeration of children sorted to have boolean operator children first
    public List sortChildren() {
        LinkedList list = new LinkedList();
        for (int i = 0; i < getChildCount(); i++) {
            StringNode child = (StringNode) getChildAt(i);
            if (child.isBooleanOp()) {
                list.addFirst(child);
            } else {
                list.addLast(child);
            }
        }
        return new ArrayList(list);
    }

    public String toString() {
        return getString();
    }

}