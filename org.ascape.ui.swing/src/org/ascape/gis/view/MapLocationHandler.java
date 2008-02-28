/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */


package org.ascape.gis.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.MenuElement;

import com.bbn.openmap.layer.location.AbstractLocationHandler;
import com.bbn.openmap.layer.location.Location;
import com.bbn.openmap.layer.location.LocationPopupMenu;

/**
 * This is a handler that open map uses to determine objects to be painted. Most
 * of its activities are delegated to the MapView. (We can't simply define this
 * as an inner class of MapView, because OpenMap wants to create it directly.)
 *
 * @author    Miles Parker, Josh Miller, and others
 * @created   September-November, 2001
 */
public class MapLocationHandler extends AbstractLocationHandler
    implements MouseListener, ActionListener, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 7729758986778278346L;
    /**
     * An instance of MapView
     */
    private MapView view;

    /**
     * A no op.
     *
     * @param e  a MouseEvent
     */
    public void mouseClicked(MouseEvent e) {
    }


    /**
     * A no op.
     *
     * @param e  a MouseEvent
     */
    public void mouseDragged(MouseEvent e) {
    }


    /**
     * A no op.
     *
     * @param e  a MouseEvent
     */
    public void mouseEntered(MouseEvent e) {
    }


    /**
     * A no op.
     *
     * @param e  a MouseEvent
     */
    public void mouseExited(MouseEvent e) {
    }


    /**
     * Method.
     *
     * @param e  parameter
     */
    public void mousePressed(MouseEvent e) {
    }


    /**
     * A no op.
     *
     * @param e  a MouseEvent
     */
    public void mouseReleased(MouseEvent e) {
    }


    /**
     * A no op.
     *
     * @param e  a MouseEvent
     */
    public void actionPerformed(ActionEvent e) {
    }


    /**
     * A no op.
     */
    public void reloadData() {
    }


    /**
     * Fills the LocationPopupMenu with MenuItems
     *
     * @param locMenu  a LocationPopupMenu
     */
    public void fillLocationPopUpMenu(LocationPopupMenu locMenu) {
        try {
            MenuElement[] items = locMenu.getSubElements();
            for (int i = 0; i < items.length; i++) {
                locMenu.remove(i);
            }
            final Location l = locMenu.getLoc();
            locMenu.add(new JMenuItem(l.getName()));
        } catch (Exception e) {
            System.err.println("POPUP ERROR: " + e.toString());
        }
    }


    /**
     * Returns a list of graphics that are within a specified region
     * Part of Abstract Location Handler contract.
     *
     * @param nwLat        nw lat corner
     * @param nwLon        nw lon corner
     * @param seLat        se lat corner
     * @param seLon        se lon corner
     * @param graphicList  the graphicList
     * @return             the graphicList
     */
    public Vector get(float nwLat, float nwLon, float seLat, float seLon, Vector graphicList) {
        if (view != null) {
            return view.get(nwLat, nwLon, seLat, seLon, graphicList);
        } else {
            return graphicList;
        }
    }


    /**
     * Sets view for the MapLocationHandler object.
     *
     * @param view  the view
     */
    public void setView(MapView view) {
        this.view = view;
    }
}
