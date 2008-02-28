/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */
package org.ascape.model.space;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.ascape.util.Conditional;


/**
 * User: milesparker Date: Aug 11, 2005 Time: 4:21:00 PM To change this template
 * use File | Settings | File Templates.
 */
public class BFSWithinIterator2DSmallWorld extends BFSWithinIterator {

    /**
     * The origins.
     */
    List origins;

    /**
     * Instantiates a new BFS within iterator2 D small world.
     * 
     * @param space
     *            the space
     * @param origin
     *            the origin
     * @param condition
     *            the condition
     * @param includeSelf
     *            the include self
     * @param distance
     *            the distance
     */
    public BFSWithinIterator2DSmallWorld(Discrete space, Location origin, Conditional condition, boolean includeSelf, double distance) {
        super(space, origin, condition, includeSelf, distance);
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.BFSWithinIterator#initialize()
     */
    protected void initialize() {
        origins = new ArrayList();
        List originDepthList = new ArrayList();
        originDepthList.add(getOrigin());
        origins.add(originDepthList);
        if (includeSelf) {
            setDepth(0);
            setLastDepth(0);            
        }else {
        setDepth(1);
        setLastDepth(1);
        }
        setSearchIterator(nextDepth());
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.BFSWithinIterator#visit(org.ascape.model.space.Node)
     */
    public void visit(Node candidate) {
    }

    /**
     * The contains SW.
     */
    Conditional containsSW = new Conditional() {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public boolean meetsCondition(Object object) {
            return ((Array2DSmallWorld) getSpace()).getSmallWorldCells().contains(object);  //To change body of implemented methods use File | Settings | File Templates.
        }
    };

    /**
     * The Class InList.
     */
    class InList implements Conditional {
        
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        /**
         * The list.
         */
        Collection list;
        
        /**
         * Instantiates a new in list.
         * 
         * @param list
         *            the list
         */
        public InList(Collection list) {
            this.list = list;
        }
        
        /* (non-Javadoc)
         * @see org.ascape.util.Conditional#meetsCondition(java.lang.Object)
         */
        public boolean meetsCondition(Object object) {
            return list.contains(object);  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    /* (non-Javadoc)
     * @see org.ascape.model.space.BFSWithinIterator#nextDepth()
     */
    public Iterator nextDepth() {
        nextSearch = new ArrayList();
        for (int originDepth = 0; originDepth <= getInternalDepth() + 2; originDepth++) {
            if (origins.size() <= originDepth) {
                List originDepthList = new ArrayList();
                origins.add(originDepth, originDepthList);
            }
        }
        for (int originDepth = 0; originDepth <= getInternalDepth() && visited.size() <= getSpace().getSize(); originDepth++) {
            List originDepthList = (List) origins.get(originDepth);
            for (Iterator iterator1 = originDepthList.iterator(); iterator1.hasNext();) {
                Node node = (Node) iterator1.next();
                boolean includeOrigin = (originDepth > 0 || includeSelf);
                List neighborsMoore = new ArrayList((((Array2DSmallWorld) getSpace()).findWithinMoore(node.getCoordinate(), includeOrigin, getInternalDepth() - originDepth)));
                neighborsMoore.removeAll(getVisited());
                List neighborsSW = CollectionSpace.filter(neighborsMoore, containsSW);
                for (Iterator iterator2 = neighborsSW.iterator(); iterator2.hasNext();) {
                    Node swNode = (Node) iterator2.next();
                    //find out-going edge
                    for (Iterator iterator3 = swNode.findNeighbors().iterator(); iterator3.hasNext();) {
                        Node neighborNode = (Node) iterator3.next();
                        if ((((Array2D) getSpace()).calculateDistanceMoore(swNode.getCoordinate(), neighborNode.getCoordinate())) > 1) {
                            ((List) origins.get(originDepth + 2)).add(neighborNode);
                            break;
                        }
                    }
                }
                getVisited().addAll(neighborsMoore);
                nextSearch.addAll(neighborsMoore);
            }
        }
//        Array2DSmallWorldTest.printSmallWorld(getSpace(), new InList(nextSearch));
        return nextSearch.iterator();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
