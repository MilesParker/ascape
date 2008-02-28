/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */


package org.ascape.gis.view;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import javax.swing.SwingUtilities;

import org.ascape.gis.model.MapAgent;
import org.ascape.gis.model.MapCoordinate;
import org.ascape.model.Agent;
import org.ascape.model.LocatedAgent;
import org.ascape.model.event.ScapeEvent;
import org.ascape.util.vis.ColorFeature;
import org.ascape.util.vis.ColorFeatureConcrete;
import org.ascape.util.vis.DrawFeature;
import org.ascape.view.custom.AgentCustomizer;
import org.ascape.view.vis.AgentView;
import org.ascape.view.vis.ComponentViewDelegate;

import com.bbn.openmap.BufferedMapBean;
import com.bbn.openmap.LatLonPoint;
import com.bbn.openmap.Layer;
import com.bbn.openmap.MapBean;
import com.bbn.openmap.event.LayerStatusEvent;
import com.bbn.openmap.event.LayerStatusListener;
import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.gui.OMToolSet;
import com.bbn.openmap.layer.daynight.DayNightLayer;
import com.bbn.openmap.layer.location.Location;
import com.bbn.openmap.layer.location.LocationHandler;
import com.bbn.openmap.layer.location.LocationLayer;
import com.bbn.openmap.layer.shape.ShapeLayer;
import com.bbn.openmap.omGraphics.DrawingAttributes;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;

/**
 * An overhead view of the map
 *
 * @author    Miles Parker, Josh Miller, and others
 * @created   September-November, 2001
 */
public class MapView extends AgentView implements Serializable, LayerStatusListener {

    /**
     * An instance of ViewMapBean
     */
    private transient ViewMapBean mapBean;

    /**
     * A flag to keep track of if the view is waiting for the mapbean to paint
     */
    private boolean awaitingMapBeanPaint;

    /**
     * A flag to keep track of if the view is waiting for the mapbean to finish working
     */
    private boolean awaitingMapBeanWorking;

    /**
     * an instance of AgentCustomizer
     */
//    protected AgentCustomizer agentCustomizer;

    /**
     * The agent being customized
     */
    protected Agent customizeAgent;

    private boolean showMapBorders = true;

    /**
     * An instance of ColorFeature
     */
//    protected ColorFeature agentColorFeature;

    /**
     * An inner class, it keeps track of the Locations on the Map
     *
     * @author    minchios
     * @created   November 9, 2001
     */
    class MapLocationLayer extends LocationLayer {

        /**
         * 
         */
        private static final long serialVersionUID = 9174527535905429663L;

        /**
         * Gets the locationHandlers attribute of the MapLocationLayer object
         *
         * @return   The locationHandlers value
         */
        public LocationHandler[] getLocationHandlers() {
            return dataHandlers;
        }
    }

    class MapRouteLayer extends Layer {
        /**
         * 
         */
        private static final long serialVersionUID = 1473830944658611412L;
        private OMGraphicList omgraphics;
        public MapRouteLayer() {
            setName("MapRouteLayer");
            omgraphics = new OMGraphicList();
        }
        public void addOMGraphic(OMGraphic graphic) {
            omgraphics.addOMGraphic(graphic);
        }
        public void removeOMGraphic(OMGraphic graphic) {
            omgraphics.remove(graphic);
        }
        public boolean containsOMGraphic(OMGraphic graphic) {
            return omgraphics.contains(graphic);
        }
        public void projectionChanged(ProjectionEvent event) {
            setProjection(event.getProjection().makeClone());
            omgraphics.generate(getProjection());
            repaint();
        }
        public void paint(Graphics g) {
            super.paint(g);
            omgraphics.render(g);
            fireStatusUpdate(LayerStatusEvent.FINISH_WORKING);
        }
        public OMGraphicList getOmgraphics() {
            return omgraphics;
        }
        public void clearRouteLayer() {
            if (omgraphics != null) {
                omgraphics.clear();
            }
        }
    }

    /**
     * ViewMapBean Keeps track of the OpenMap parameters
     *
     * @author    minchios
     * @created   November 9, 2001
     */
    public class ViewMapBean extends BufferedMapBean {

        /**
         * 
         */
        private static final long serialVersionUID = -6197742887793359498L;

        /**
         * A layer that displays graphics supplied by LocationHandlers
         */
        LocationLayer locLayer;

        /**
         * A Layer to display hypothetical transportation routes.
         */
        MapRouteLayer routeLayer;

        MapRouteLayer imageRouteLayer;

        /**
         * An OpenMap Layer that displays shape files.
         */
        ShapeLayer shapeLayer;

        /**
         * A layer that draws the day/Night terminator on the map.
         * (Not currently implemented).
         */
        DayNightLayer dayNightLayer;


        /**
         * Constructs an instance of ViewMapBean.
         */
        public ViewMapBean() {

            // make the map bean
            super();

            // NOT AT ALL SURE IF THIS IS NECESSARY.
            // IGNORING IT FOR NOW, UNTIL SOMEONE HAS TIME TO LOOK INTO IT.
            if (agentColorFeature == null) {
                agentColorFeature =
                    new ColorFeatureConcrete("Default Agent Color") {
                        /**
                         * 
                         */
                        private static final long serialVersionUID = 3692823319544557523L;

                        public Color getColor(Object object) {
                            return ((Agent) object).getColor();
                        }
                    };
            }
        }

        /**
         * Adds a feature to the Notify attribute of the ViewMapBean object
         */
        public void addNotify() {

            // Leaving this out causes trouble!
            super.addNotify();

            getDelegate().setNotifyScapeAutomatically(false);

            // Build and add a layer that displays the map.
            shapeLayer = new ShapeLayer();
            Properties shapeLayerProps = new Properties();
            shapeLayerProps.put("political.prettyName", "Political Solid");
            shapeLayerProps.put("political.lineColor", "112211");
            shapeLayerProps.put("political.fillColor", "229933");
            shapeLayerProps.put("political.shapeFile", "../../Ascape/lib/org/ascapex/gis/view/dat/dcwpo-browse.shp");
            shapeLayerProps.put("political.spatialIndex", "../../Ascape/lib/org/ascapex/gis/view/dat/dcwpo-browse.ssx");
            shapeLayer.setProperties("political", shapeLayerProps);
            Properties drawProps = new Properties();
            drawProps.put("fillColor", "33CC45");
            drawProps.put("lineColor", "330011");
            drawProps.put("testColor", "001133");
            DrawingAttributes drawAttr = new DrawingAttributes(drawProps);
            shapeLayer.setDrawingAttributes(drawAttr);
            shapeLayer.doPrepare();
            shapeLayer.setVisible(true);

            // Build and add a layer that shows the time of day... based on position of the sun
            // and draws corresponding shadows
//            dayNightLayer = new DayNightLayer();
//            Properties dayNightLayerProps = new Properties();
//            dayNightLayerProps.put("daynight.prettyName", "Day / Night");
//            dayNightLayer.setProperties("daynight", dayNightLayerProps);
//            add(dayNightLayer);

            // Build and add a layer that shows DCs, trucks, et al
            // use this to place fixed position enemy locations, supply ships, etc
            locLayer = new MapLocationLayer();
            Properties locLayerProps = new Properties();
            locLayerProps.put("locationLayer.useDeclutter", "true");
            locLayerProps.put("locationlayer.declutterMatrix", "com.bbn.openmap.layer.DeclutterMatrix");
            locLayerProps.put("locationlayer.allowPartials", "true");
            locLayerProps.put("locationlayer.locationHandlers", "ascape.gis");
            locLayerProps.put("ascape.gis.class", "org.ascapex.gis.model.MapLocationHandler");
            locLayerProps.put("ascape.gis.prettyName", "gis");
            locLayerProps.put("ascape.gis.locationColor", "FF0000");
            locLayerProps.put("ascape.gis.nameColor", "008C54");
            locLayerProps.put("ascape.gis.showNames", "true");
            locLayerProps.put("ascape.gis.showLocations", "true");
            locLayer.setProperties("locationlayer", locLayerProps);
            locLayer.doPrepare();
            locLayer.setVisible(true);

            routeLayer = new MapRouteLayer();
            imageRouteLayer = new MapRouteLayer();
//            routeLayer.setVisible(true);

            add(routeLayer, 0);
            add(imageRouteLayer, 1);

            add(locLayer, 2);   // <-------------------xxx
            if (showMapBorders) {
                add(shapeLayer, 3);
            }
            awaitingMapBeanPaint = false;

            shapeLayer.addLayerStatusListener(MapView.this);
//            routeLayer.addLayerStatusListener(MapView.this);
            locLayer.addLayerStatusListener(MapView.this);
//            shapeLayer.addLayerStatusListener(MapView.this);

            // Attach a Mouse Delegator to the Map Bean
            /*
			 *  MouseDelegator md = new MouseDelegator(this);
			 *  MapMouseMode[] modes = new MapMouseMode[2];
			 *  modes[0] = new SelectMouseMode();
			 *  modes[1] = new NavMouseMode();
			 *  /md.setMouseModes(modes, 1);
			 *  md.setMouseModes(modes, 0);
			 */
            ViewMapBean.this.addMouseListener(
                new MouseListener() {
                    public void mouseClicked(MouseEvent e) {
                        //Remove for applet only
                        if (e.isAltDown()) {
                            Agent candidateAgent = getAgentForEvent(e);
                            //toggle on if not selected, off if allready selected
                            if (getCustomizeAgent() != candidateAgent) {
                                displayAgentCustomizer(candidateAgent);
//                                agentCustomizer.setFocus(AgentCustomizer.FOCUS_PRIMARY);
                                if (!e.isShiftDown()) {
                                    agentCustomizer.setFocus(AgentCustomizer.FOCUS_PRIMARY);
                                } else {
                                    agentCustomizer.setFocus(AgentCustomizer.FOCUS_HOSTED);
                                }

                            } else {
                                removeAgentCustomizer();
                                setCustomizeAgent(null);
                            }
                            notifyScapeUpdated();
                            repaint();
                        } else if (e.getClickCount() == 2){
                            displayCustomizer();
                        }
                    }

                    /**
                     * a no op
                     *
                     * @param e  a MouseEvent
                     */
                    public void mouseEntered(MouseEvent e) {
                    }


                    /**
                     * a no op
                     *
                     * @param e a MouseEvent
                     */
                    public void mouseExited(MouseEvent e) {
                    }


                    /**
                     * a no op
                     *
                     * @param e a MouseEvent
                     */
                    public void mousePressed(MouseEvent e) {
                    }

                    /**
                     * a no op
                     * @param e a MouseEvent
                     */
                    public void mouseReleased(MouseEvent e) {
                    }
                });
            this.addMouseMotionListener(
                new MouseMotionListener() {
                    public void mouseDragged(MouseEvent e) {
                        if (e.isAltDown()) {
                            Agent candidateAgent = getAgentForEvent(e);
                            if (e.getX() >= 0 && e.getX() < MapView.this.getSize().width && e.getY() >= 0 && e.getY() < MapView.this.getSize().height) {
                                displayAgentCustomizer(candidateAgent);
                            }
//                            if (!e.isShiftDown()) {
//                                agentCustomizer.setFocus(AgentCustomizer.FOCUS_PRIMARY);
//                            }
//                            else {
//                                agentCustomizer.setFocus(AgentCustomizer.FOCUS_HOSTED);
//                            }
                        }
                    }


                    public void mouseMoved(MouseEvent e) {
                    }
                });

        }
        public void addOMGraphic(OMGraphic graphic) {
            routeLayer.addOMGraphic(graphic);
        }
        public void addImageGraphic(OMGraphic graphic) {
            imageRouteLayer.addOMGraphic(graphic);
        }
        public void removeOMGraphic(OMGraphic graphic) {
            routeLayer.removeOMGraphic(graphic);
        }
        public void clearOMGraphics() {
            routeLayer.clearRouteLayer();
        }

        /**
         * Paints the panel. If view is awaiting update, paint calls updated so
         * that the scape will be notified when we are done painting. This
         * method should rarely need to be overridden; use scapeNotification to
         * update component state.
         *
         * @param g  the graphics context.
         */
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            //awaitingMapBeanUpdate = false;
            if (awaitingMapBeanPaint) {
                awaitingMapBeanPaint = false;
                MapView.this.notifyScapeUpdated();
            }
        }


        /**
         * Gets the locLayer attribute of the ViewMapBean object
         *
         * @return   The locLayer value
         */
        public LocationLayer getLocLayer() {
            return locLayer;
        }
        public MapRouteLayer getRouteLayer() {
            return routeLayer;
        }
        public MapRouteLayer getImageRouteLayer() {
            return imageRouteLayer;
        }
        public boolean containsOMGraphic(OMGraphic graphic) {
            return routeLayer.containsOMGraphic(graphic);
        }
    }


    /**
     * Constructs an instance of MapView.
     */
    public MapView() {
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        mapBean = new ViewMapBean();
        setName("Map View");

        setDelegate(new ComponentViewDelegate(this) {
            /**
             * 
             */
            private static final long serialVersionUID = -6173856857264450020L;

            public void viewPainted() {
                if (!awaitingMapBeanPaint && !awaitingMapBeanWorking) {
                    super.viewPainted();
                }
            }
        });
    }

    /**
     * Override addNotify to build buffer.
     */
    public void addNotify() {
        super.addNotify();

        setLayout(new BorderLayout());
        //setEnabled(true);

        // Create the tabbed panel
        //tabbedPanel = new JTabbedPane();
        //add(tabbedPanel, BorderLayout.CENTER);

        // Map Panel
        //JPanel mapPanel = new JPanel();
        //mapPanel.setLayout(new BorderLayout());
        //mapBean = new ViewMapBean();

        //mapPanel.add(mapBean, BorderLayout.CENTER);

        OMToolSet omts = new OMToolSet();

        // Associate the tool with the map
        omts.setupListeners(mapBean);

        // Create an OpenMap toolbar
//        ToolPanel toolBar = new ToolPanel();
//
//        // Add the tool to the toolbar
//        toolBar.add(omts);
//        //JInternalFrame toolFrame = new JInternalFrame();
//        //toolFrame.getContentPane().add(toolBar);
//        PanelView toolView = new PanelView();
//        toolView.add(toolBar);
//        UserEnvironment.userEnvironment.createFrame(toolView);
        //UserEnvironment.userEnvironment.getUserFrame().getDesk().add(toolFrame, JLayeredPane.DRAG_LAYER);
        //toolFrame.setVisible(true);

        // Add the tool bar to the frame
        //mapPanel.add(toolBar, BorderLayout.NORTH);
        add(mapBean, BorderLayout.CENTER);

        //tabbedPanel.add(mapPanel, "Map");
        getLocationHandler().setView(this);
    }


    /**
     * Returns true if the location is inside the specified region
     *
     * @param l      the Location
     * @param nwLat  nw lat corner
     * @param nwLon  nw lon corner
     * @param seLat  se lat corner
     * @param seLon  se lon corner
     * @return       True if the Location is inside the specified region
     */
    public boolean inside(Location l, double nwLat, double nwLon, double seLat, double seLon) {
        //WARNING: bad, quick code!! We need to fix this for wrap around issues, for the moment we are assuming that we are not near 0 degrees...
        return ((l.lat <= nwLat) && (l.lat >= seLat) && (l.lon >= nwLon) && (l.lon <= seLon));
    }

    /**
     * On update, set the awaitingMapBeanWorking flag to true, and if he mapBean's location layer
     * is not null, then call doPreparer().
     */
    public void updateScapeGraphics() {
//        Object[] drawFeatures = getDrawSelection().getSelection();
//        for (Iterator agents = getScape().iterator(); agents.hasNext();) {
//            for (int i = 0; i < drawFeatures.length; i++) {
//                ((DrawFeature) drawFeatures[i]).draw(bufferedGraphics, agents.next(), 0, 0);
//            }
//        }
        awaitingMapBeanWorking = true;
        awaitingMapBeanPaint = false;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (isShowMapBorders()) {
                    mapBean.shapeLayer.doPrepare();
                }
                mapBean.locLayer.doPrepare();
            }
        });
    }

    /**
     * Notifies this view that its scape has been updated. View sets awaiting
     * update state to true, and requests a repaint. Subclasses should update
     * their component states at this point.
     *
     * @param scapeEvent  a scape event update, usually unspecified
     */
    public void scapeNotification(ScapeEvent scapeEvent) {
        Object[] drawFeatures = getDrawSelection().getSelection();
        for (Iterator agents = getScape().iterator(); agents.hasNext();) {
            for (int i = 0; i < drawFeatures.length; i++) {
                try {
                    ((DrawFeature) drawFeatures[i]).draw(bufferedGraphics, agents.next(), 0, 0);
                } catch (NoSuchElementException e) {
                }
            }
        }
        super.scapeNotification(scapeEvent);
        if ((getViewFrame() != null) && isShowing()) {
            awaitingMapBeanWorking = true;
        }
    }

    Set updated = new HashSet();

    /**
     * Update the layer status
     *
     * @param evt  a LayerStatusEvent
     */
    public synchronized void updateLayerStatus(LayerStatusEvent evt) {
        if ((evt.getStatus() == LayerStatusEvent.FINISH_WORKING) && awaitingMapBeanWorking) {
            //awaitingMapBeanUpdate = false;

            updated.add(evt.getLayer());
            if (updated.size() == 2) {
                awaitingMapBeanWorking = false;
                awaitingMapBeanPaint = true;
                updated = new HashSet();
                mapBean.repaint();
//                scape.respondControl(new ControlEvent(this, ControlEvent.REPORT_LISTENER_UPDATED));
            }
        }
    }

    /**
     * Displays a customizer for altering the settings for this view.
     * May be a no op if (in the case of no swing support) a customizer isn't available for the environment.
     */
//      public void displayAgentCustomizer(Agent agent) {
//          if ((agentCustomizer == null) || (agentCustomizer.getAgent() != agent)) {
//              if (agentCustomizer == null) {
//                  agentCustomizer = new AgentCustomizer(this);
//              }
//              if (agentCustomizer.getViewFrame() == null) {
//                  getScape().addView(agentCustomizer);
//              }
//              setCustomizeAgent(agent);
//              agentCustomizer.getViewFrame().toFront();
//              updateScapeGraphics();
//              repaint();
//          }
//      }

    /**
     * Hides window for altering the setting for this view.
     */
//      public void removeAgentCustomizer() {
//          if ((agentCustomizer != null) && (agentCustomizer.getViewFrame() != null)) {
//              agentCustomizer.getViewFrame().dispose();
//          }
//      }

    /**
     * Returns the preferred size of this view, which is the size of the lattice
     * times this views cellSize.
     *
     * @return   the preferredSize
     */
    public Dimension getPreferredSize() {
        return new Dimension(800, 600);
    }

    public void addGraphic(OMGraphic graphic) {
        mapBean.addOMGraphic(graphic);
    }
    public void addImage(OMGraphic graphic) {
        mapBean.addImageGraphic(graphic);
    }
    public boolean containsGraphic(OMGraphic graphic) {
        return mapBean.containsOMGraphic(graphic);
    }

    public void removeGraphic(OMGraphic graphic) {
        mapBean.removeOMGraphic(graphic);
    }

    public void clearGraphics() {
        mapBean.clearOMGraphics();
    }


    /**
     * Returns a list of graphics that are within a specified region
     *
     * @param nwLat        nw lat corner
     * @param nwLon        nw lon corner
     * @param seLat        se lat corner
     * @param seLon        se lon corner
     * @param graphicList  the graphicList
     * @return             the graphicList
     */
    public Vector get(double nwLat, double nwLon, double seLat, double seLon, Vector graphicList) {
        ArrayList locations = new ArrayList();
        Iterator mapItems = getScape().iterator();
        while (mapItems.hasNext()) {
            MapAgent item = (MapAgent) mapItems.next();
            Location candidateLocation = item.getLocation();
            if ((candidateLocation != null) && (inside(candidateLocation, nwLat, nwLon, seLat, seLon))) {
                //if (candidateLocation.
                locations.add(candidateLocation);
            }
        }
        if (((ViewMapBean) getMapBean()).getRouteLayer() != null) {
            MapRouteLayer mrl = ((ViewMapBean) getMapBean()).getRouteLayer();
            MapRouteLayer mil = ((ViewMapBean) getMapBean()).getImageRouteLayer();
            try {
                for (Iterator iterator = mrl.getOmgraphics().iterator(); iterator.hasNext();) {
                    OMGraphic graphic = (OMGraphic) iterator.next();
                    // it appears that graphics are layered (top - down) in the order they're in the list. So
                    // those added first show up over those added later.
                    locations.add(graphic);
                }
                for (Iterator iterator = mil.getOmgraphics().iterator(); iterator.hasNext();) {
                    OMGraphic graphic = (OMGraphic) iterator.next();
                    locations.add(graphic);
                }
            } catch (ConcurrentModificationException e) {}
        }
        graphicList.clear();
        graphicList.addAll(locations);
        return graphicList;
    }


    /**
     * Gets the agentForEvent for the MapView object.
     *
     * @param e  parameter
     * @return   the agentForEvent
     */
    private LocatedAgent getAgentForEvent(MouseEvent e) {
        //return (LocatedAgent) ((BaseModel) getScape().getRoot()).getCH53s().get(0);
        if (getScape().getSize() > 0) {
            LatLonPoint p = mapBean.getCoordinates(e);
            return (getScape()).findNearest(new MapCoordinate(p.getLatitude(), p.getLongitude()), null, true, Double.MAX_VALUE);
        } else {
            return null;
        }
    }


    /**
     * Gets the bean for the MapView object.
     *
     * @return   the bean
     */
    public MapBean getBean() {
        return mapBean;
    }


    /*
	 *  public boolean isComponentsnotifyScape () {
	 *  /return !awaitingMapBeanUpdate;
	 *  if (!awaitingMapBeanUpdate) {
	 *  return true;
	 *  }
	 *  if (((MapLocationLayer) mapBean.getLocLayer()).getCurrentWorker() == null) {
	 *  awaitingMapBeanUpdate = false;
	 *  return true;
	 *  }
	 *  return false;
	 *  /return (!awaitingMapBeanUpdate) && (((MapLocationLayer) mapBean.getLocLayer()).getCurrentWorker() == null);
	 *  }
	 */
    /**
     * Gets the locationHandler for the MapView object.
     *
     * @return   the locationHandler
     */
    public MapLocationHandler getLocationHandler() {
        return (MapLocationHandler) ((MapLocationLayer) mapBean.getLocLayer()).getLocationHandlers()[0];
    }


    /**
     * Gets the customizeAgent for the MapView object.
     *
     * @return   the customizeAgent
     */
    public Agent getCustomizeAgent() {
        if (agentCustomizer != null) {
            return agentCustomizer.getAgent();
        } else {
            return null;
        }
    }

    /**
     * Gets the agentCustomizer for the MapView object.
     *
     * @return   the agentCustomizer
     */
//      public AgentCustomizer getAgentCustomizer() {
//          return agentCustomizer;
//      }


    /**
     * Sets customizeAgent for the MapView object.
     *
     * @param Agent  the customizeAgent
     */
//      public void setCustomizeAgent(Agent Agent) {
//          if (agentCustomizer != null) {
//              /*
//  			 *  if (agentCustomizer.getAgent() != null) {
//  			 *  agentCustomizer.getAgent().requestUpdateNext();
//  			 *  }
//  			 */
//              agentCustomizer.setAgent(Agent);
//              /*
//  			 *  if (agentCustomizer.getAgent() != null) {
//  			 *  agentCustomizer.getAgent().requestUpdateNext();
//  			 *  }
//  			 */
//          }
//      }


    /**
     * Sets the agentCustomizer.
     *
     * @param agentCustomizer  the agentCustomizer
     */
    public void setAgentCustomizer(AgentCustomizer agentCustomizer) {
        this.agentCustomizer = agentCustomizer;
    }


    /**
     * Returns the color feature that will be used for determining agent color.
     * The default color feature is simply the getColor() method of the cell.
     *
     * @return   The primaryAgentColorFeature value
     */
    public ColorFeature getPrimaryAgentColorFeature() {
        return agentColorFeature;
    }


    /**
     * Set the color feature that will be used for determining agent color. The
     * default color feature is simply the getColor() method of the cell.
     *
     * @param agentColorFeature the color feature, whose object is assumed to
     *      be a cell populating this lattice
     */
    public void setPrimaryAgentColorFeature(ColorFeature agentColorFeature) {
        this.agentColorFeature = agentColorFeature;
    }

    public MapBean getMapBean() {
        return mapBean;
    }

    public boolean isShowMapBorders() {
        return showMapBorders;
    }

    public void setShowMapBorders(boolean showMapBorders) {
        this.showMapBorders = showMapBorders;
    }
}
