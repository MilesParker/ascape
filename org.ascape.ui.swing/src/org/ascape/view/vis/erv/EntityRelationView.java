/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */
package org.ascape.view.vis.erv;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.ascape.model.Agent;
import org.ascape.util.vis.ColorFeature;
import org.ascape.util.vis.DrawFeature;
import org.ascape.util.vis.ERVDerivedFontMap;
import org.ascape.util.vis.ERVGlyphFactory;
import org.ascape.util.vis.ERVUtil;
import org.ascape.util.vis.EntityFeature;
import org.ascape.util.vis.RelationFeature;
import org.ascape.util.vis.SelectionGroup;
import org.ascape.view.vis.AgentView;

/**
 * A class for a panel that acts as an observer of scapes as a graph of entities
 * joined by relations. Incorporates Carl's original EntityGraph class, uses the
 * classes named ERV* for support, requires the EntityFeature and
 * RelationFeature interfaces to extract the necessary info from the scape to
 * decorate the graph.
 * 
 * @author Carl Tollander and Roger Critchlow
 * @version 2.9
 * @history 2.9 Moved into main Ascape.
 * @history 1.0 (Class version) 06/05/01 initial definition
 * @since 1.0
 */
public class EntityRelationView extends AgentView {

    /**
     * Clamp a double to an interval.
     * 
     * @param x
     *            a <code>double</code> value
     * @param min
     *            a <code>double</code> value
     * @param max
     *            a <code>double</code> value
     * @return a <code>double</code> value
     */
    private static double clamp(double x, double min, double max) {
        return x < min ? min : x > max ? max : x;
    }

    /**
     * The <code>viewCustomizer</code> is a panel which allows the rendered
     * draw features to be selected.
     */
    private ERVViewCustomizer viewCustomizer = null;

    /**
     * The <code>agentCustomizer</code> is a panel which allows the contents
     * of each rendered node to be inspected.
     */
//    AgentCustomizer agentCustomizer = null;

    /**
     * The <code>drawSelection</code> manages the selected draw features.
     */
//    VectorSelection drawSelection = null;

    /**
     * Describe variable <code>entityFeature</code> here.
     */
    private EntityFeature entityFeature = null;

    /**
     * Describe variable <code>entityDrawFeature</code> here.
     */
    private EntityDrawFeature entityDrawFeature = null;

    /**
     * Describe variable <code>selectedAgent</code> here.
     */
    private Agent selectedAgent = null;

    /**
     * Describe variable <code>entityFeatureGroup</code> here.
     */
    private SelectionGroup entityFeatureGroup = null;

    /**
     * The class <code>RelationDrawFeature</code> encapsulates the drawing
     * mechanisms previously distributed over methods in <code>ERVEdge</code>
     * and <code>ERVChannel</code>.
     * 
     * @author Miles Parker, Matthew Hendrey, and others
     */
    public class RelationDrawFeature extends DrawFeature {

        /**
         * 
         */
        private static final long serialVersionUID = 2342598931329577651L;

        /**
         * The <code>RelationFeature</code> used in this
         * <code>RelationDrawFeature</code> .
         */
        RelationFeature f;

        /*
	 * A bunch of objects previously created once for each relation drawn.
	 */
        /**
         * The channel line.
         */
        private transient Line2D channelLine = null;
        
        /**
         * The p1.
         */
        private transient Point2D p1 = null;
        
        /**
         * The p2.
         */
        private transient Point2D p2 = null;
        
        /**
         * The apt.
         */
        private transient Point2D apt = null;
        
        /**
         * The bpt.
         */
        private transient Point2D bpt = null;
        
        /**
         * The v.
         */
        private transient Point2D v = null;
        
        /**
         * The slider glyph.
         */
        private transient GeneralPath sliderGlyph = null;
        
        /**
         * The back to zero.
         */
        private transient AffineTransform backToZero = null;
        
        /**
         * The trans.
         */
        private transient AffineTransform trans = null;

        /**
         * Constructs the feature with a name and a relation feature.
         * 
         * @param relationFeature
         *            the draw feature used by this feature
         * @param name
         *            parameter
         */
        public RelationDrawFeature(String name, RelationFeature relationFeature) {
            super(name);
            initializeTransientFields();
            this.f = relationFeature;
        }

        /**
         * Initialize transient fields.
         */
        private void initializeTransientFields() {
            channelLine = new Line2D.Float(0, 0, 0, 0);
            p1 = new Point2D.Double();
            p2 = new Point2D.Double();
            apt = new Point2D.Double();
            bpt = new Point2D.Double();
            v = new Point2D.Double();
            sliderGlyph = ERVGlyphFactory.circle(6);
            backToZero = new AffineTransform();
            trans = new AffineTransform();
        }

        /**
         * Read object.
         * 
         * @param stream
         *            the stream
         * @throws IOException
         *             Signals that an I/O exception has occurred.
         * @throws ClassNotFoundException
         *             the class not found exception
         */
        private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
            stream.defaultReadObject();
            initializeTransientFields();
        }

        /**
         * Draws a graphic interpretation of the object into the supplied
         * graphics port, (typically) within the supplied dimensions. Views
         * which use this class are responsible for translating the graphics so
         * that the object is drawn at the approriate location. Please let us
         * know if you think you need a directly addressed alternative.
         * 
         * @param g
         *            the Graphics context to draw into
         * @param object
         *            the object to interpret for drawing
         * @param width
         *            the width of the space that should be drawn into
         * @param height
         *            the height of the space that should be drawn into
         */
        public void draw(Graphics g, Object object, int width, int height) {
            System.err.println("Wrong draw called in EntityRelationView.RelationDrawFeature");
        }

        /**
         * Describe <code>draw</code> method here.
         * 
         * @param g
         *            a <code>Graphics</code> value
         * @param orig
         *            an <code>Object</code> value
         * @param dest
         *            an <code>Object</code> value
         * @param width
         *            an <code>int</code> value
         * @param height
         *            an <code>int</code> value
         * @param offset
         *            parameter
         * @param maxLineWidth
         *            parameter
         */
        public void draw(Graphics g, Object orig, Object dest, int width, int height, double offset, double maxLineWidth) {
            if (!f.includesRelation(orig, dest)) {
                return;
            }

            Graphics2D g2 = (Graphics2D) g;

            // find the colors
            Color lineColor = f.getLineColor(orig, dest);
            Color glyphColor = f.getGlyphColor(orig, dest);

            // find the line width
            double a = f.getLineWidth(orig, dest);
            if (a == Double.NaN) {
                System.out.println("NaN from RelationFeature.getLineWidth()");
                return;
            }
            double amount = clamp(a, 0.0, 1.0);

            // find the glyph position
            a = f.getGlyphPosition(orig, dest);
            if (a == Double.NaN) {
                System.out.println("NaN from RelationFeature.getGlyphPosition()");
                return;
            }
            double sliderPos = clamp(a, -1.0, 1.0);

            // find the shapes from the nodes
            RectangularShape origShape = entityDrawFeature.getCachedShape(orig);
            RectangularShape destShape = entityDrawFeature.getCachedShape(dest);

            // get the origin and destination node locations
            // these are true pixel locations, not normed locations
            // also these are to the shape centers, not corners
            p1.setLocation(origShape.getCenterX(), origShape.getCenterY());
            p2.setLocation(destShape.getCenterX(), destShape.getCenterY());

            // calculate the vector orthogonal to line p1 p2
            //    make a vector between p1 and p2
            //    orthogonalize it
            //    normalize it
            v.setLocation(p2.getX() - p1.getX(), p2.getY() - p1.getY());
            ERVUtil.normalize(v);
            ERVUtil.orthogonalize(v);

            // scale the orthogonal vector by the offset
            ERVUtil.scaleBy(v, offset);

            // binary search for the location of apt that is
            // close to origShape but not buried in it
            p1.setLocation(origShape.getCenterX() + v.getX(), origShape.getCenterY() + v.getY());
            p2.setLocation(destShape.getCenterX() + v.getX(), destShape.getCenterY() + v.getY());
            apt.setLocation((p1.getX() + p2.getX()) * 0.5, (p1.getY() + p2.getY()) * 0.5);
            for (int i = 0; i < 20; i += 1) {
                if (origShape.contains(apt)) {
                    p1.setLocation(apt);
                } else {
                    p2.setLocation(apt);
                }
                apt.setLocation((p1.getX() + p2.getX()) * 0.5, (p1.getY() + p2.getY()) * 0.5);
            }

            // binary search for the location of bpt that is
            // close to destShape but not buried in it
            p1.setLocation(origShape.getCenterX() + v.getX(), origShape.getCenterY() + v.getY());
            p2.setLocation(destShape.getCenterX() + v.getX(), destShape.getCenterY() + v.getY());
            bpt.setLocation((p1.getX() + p2.getX()) * 0.5, (p1.getY() + p2.getY()) * 0.5);
            for (int i = 0; i < 20; i += 1) {
                if (destShape.contains(bpt)) {
                    p2.setLocation(bpt);
                } else {
                    p1.setLocation(bpt);
                }
                bpt.setLocation((p1.getX() + p2.getX()) * 0.5, (p1.getY() + p2.getY()) * 0.5);
            }

            // set the line end points
            channelLine.setLine(apt, bpt);

            // calculate the stroke width for the amount.
            // if it is different from the current linestroke width, or if there
            // is no linestroke and the new stroke width is > 1, make a new linestroke.
            Stroke lineStroke;
            int lineStrokeWidth = (int) java.lang.Math.round(amount * maxLineWidth);

            if (lineStrokeWidth > 0) {
                lineStroke = new BasicStroke(lineStrokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);

                // set colors and strokes.
                g2.setPaint(lineColor);
                Stroke oldStroke = g2.getStroke();
                g2.setStroke(lineStroke);
                g2.draw(channelLine);
                g2.setStroke(oldStroke);

                // positionSliderGlyph();
                Rectangle bounds = sliderGlyph.getBounds();
                backToZero.setToIdentity();
                backToZero.translate(-bounds.getX(), -bounds.getY());
                sliderGlyph.transform(backToZero);

                // Point2D q1 = channelLine.getP1();
                // Point2D bpt = channelLine.getP2();
                double len = apt.distance(bpt);
                Point2D vec = new Point2D.Double(bpt.getX() - apt.getX(), bpt.getY() - apt.getY());
                ERVUtil.normalize(vec);
                ERVUtil.scaleBy(vec, len * (sliderPos + 1.0) / 2.0);

                trans.setToIdentity();
                trans.translate(apt.getX() + vec.getX() - bounds.getWidth() / 2, apt.getY() + vec.getY() - bounds.getHeight() / 2);
                sliderGlyph.transform(trans);

                g2.setPaint(glyphColor);
                g2.fill(new Area(sliderGlyph));
                g2.setPaint(Color.black);
                g2.draw(sliderGlyph);
            }
        }
    }

    /**
     * The class <code>EntityDrawFeature</code> encapsulates the drawing
     * mechanism previously distributed over methods of <code>ERVNode</code>.
     * 
     * @author Miles Parker, Matthew Hendrey, and others
     */
    class EntityDrawFeature extends DrawFeature {

        /**
         * 
         */
        private static final long serialVersionUID = -5816035986967047522L;

        /**
         * The <code>EntityFeature</code> used in this entity draw feature.
         */
        EntityFeature f;

        /**
         * The <code>shapeMap</code> is used to cache the last node shape
         * computed for an Entity.
         */
        transient Map shapeMap = null;

        /**
         * A class which caches the last shape drawn for an entity and keeps the
         * locally overridden coordinates for the shape.
         * 
         * @author <a href="mailto:rcritch@nutech.com"></a>
         */
        class CachedShape {

            /**
             * The shape class.
             */
            Class shapeClass;
            
            /**
             * The shape.
             */
            RectangularShape shape;
            
            /**
             * The width.
             */
            int width;
            
            /**
             * The height.
             */
            int height;

            /**
             * Constructs an instance of CachedShape.
             * 
             * @param shapeClass
             *            parameter
             * @param shape
             *            parameter
             * @param width
             *            parameter
             * @param height
             *            parameter
             */
            CachedShape(Class shapeClass, RectangularShape shape, int width, int height) {
                this.shapeClass = shapeClass;
                this.shape = shape;
                this.width = width;
                this.height = height;
            }

            /**
             * Shape class.
             * 
             * @return the class
             */
            Class shapeClass() {
                return shapeClass;
            }

            /**
             * Shape.
             * 
             * @return the rectangular shape
             */
            RectangularShape shape() {
                return shape;
            }

            /**
             * Width.
             * 
             * @return the int
             */
            int width() {
                return width;
            }

            /**
             * Height.
             * 
             * @return the int
             */
            int height() {
                return height;
            }

            /**
             * Sets the width.
             * 
             * @param width
             *            the new width
             */
            void setWidth(int width) {
                this.width = width;
            }

            /**
             * Sets the height.
             * 
             * @param height
             *            the new height
             */
            void setHeight(int height) {
                this.height = height;
            }
        }

        /**
         * Constructs the feature with a name and entity feature.
         * 
         * @param entityFeature
         *            the EntityFeature to use in this EntityDrawFeature.
         * @param name
         *            parameter
         */
        public EntityDrawFeature(String name, EntityFeature entityFeature) {
            super(name);
            initializeTransientFields();
            this.f = entityFeature;
        }

        /**
         * Initialize transient fields.
         */
        private void initializeTransientFields() {
            shapeMap = new HashMap();
        }

        /**
         * Read object.
         * 
         * @param stream
         *            the stream
         * @throws IOException
         *             Signals that an I/O exception has occurred.
         * @throws ClassNotFoundException
         *             the class not found exception
         */
        private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
            stream.defaultReadObject();
            initializeTransientFields();
        }

        /**
         * Find if a cached entity shape exists.
         * 
         * @param o
         *            an <code>Object</code> value
         * @return a <code>boolean</code> value
         */
        public boolean haveCachedShape(Object o) {
            if (!shapeMap.containsKey(o)) {
                return false;
            }
            CachedShape shapeCache = (CachedShape) shapeMap.get(o);
            return shapeCache.shapeClass() == f.getShapeClass(o);
        }

        /**
         * Draws a graphic interpretation of the object into the supplied
         * graphics port, (typically) within the supplied dimensions. Views
         * which use this class are responsible for translating the graphics so
         * that the object is drawn at the approriate location. Please let us
         * know if you think you need a directly addressed alternative.
         * 
         * @param g
         *            the Graphics context to draw into
         * @param width
         *            the width of the space that should be drawn into
         * @param height
         *            the height of the space that should be drawn into
         * @param o
         *            parameter
         */
        public void draw(Graphics g, Object o, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            // find the shape
            RectangularShape shape = getShape(o, width, height);
            if (shape == null) {
                return;
            }

            Rectangle2D brect = shape.getBounds();
            if (brect.getWidth() == 0.0 || brect.getHeight() == 0.0) {
                return;
            }

            // draw the shape
            g2.setColor(f.getColor(o));
            g2.fill(new Area(shape));
            g2.setPaint(f.getBorderColor(o));
            g2.draw(shape);
            g2.setPaint(Color.black);

            // put the label in the entity shape
            // default transformation has 1 pt == 1 pixel (about).
            // so a 24 point font is about 24 pixels?
            String text = f.getText(o);
            Rectangle2D srect = shape.getBounds();
            ERVDerivedFontMap derivedFontMap = f.getDerivedFontMap();
            g2.setFont(derivedFontMap.getFont((float) (srect.getHeight() / 3.5)));
            FontRenderContext frc = g2.getFontRenderContext();
            Rectangle2D fbounds = g2.getFont().getStringBounds(text, frc);
            float fwidth = (float) fbounds.getWidth();
            float cx = (float) (srect.getX() + (srect.getWidth() / 2));
            float cy = (float) (srect.getY() + (srect.getHeight() / 2));
            g2.drawString(text, cx - fwidth / 2, cy);
        }

        /**
         * Describe <code>getEntityFeature</code> method here.
         * 
         * @return an <code>EntityFeature</code> value
         */
        public EntityFeature getEntityFeature() {
            return f;
        }

        /**
         * Find a cached entity shape.
         * 
         * @param o
         *            an <code>Object</code> value
         * @return a <code>RectangularShape</code> value
         */
        public RectangularShape getCachedShape(Object o) {
            return haveCachedShape(o) ? ((CachedShape) shapeMap.get(o)).shape() : null;
        }

        /**
         * Find the minimum diameter of a cached entity shape.
         * 
         * @param o
         *            an <code>Object</code> value
         * @return a <code>double</code> value
         */
        public double getMinDim(Object o) {
            RectangularShape shape = getCachedShape(o);
            if (o != null) {
                return java.lang.Math.min(shape.getWidth(), shape.getHeight());
            }
            return 1;
        }

        /**
         * Get the possibly cached shape for this entity and scale to the
         * appropriate size for the associated value and the window size, use
         * the provided coordinates or maintain the user preferred coordinates.
         * 
         * @param o
         *            an <code>Object</code> value
         * @param width
         *            an <code>int</code> value
         * @param height
         *            an <code>int</code> value
         * @return a <code>RectangularShape</code> value
         */
        public RectangularShape getShape(Object o, int width, int height) {
            RectangularShape shape;
            CachedShape cachedShape = null;
            if (haveCachedShape(o)) {
                cachedShape = (CachedShape) shapeMap.get(o);
                shape = cachedShape.shape();
            } else {
                shape = f.getShape(o);
            }

            // Find the specified position of this entity
            // which may have been overridden by user manipulation

            Point2D.Double pt = f.getPosition(o);
            double nx = pt.getX();
            double ny = pt.getY();

            // Find the specified diameter of this entity
            double normedHeightMin = f.getMinNormedHeight(o);
            double normedHeightMax = f.getMaxNormedHeight(o);
            double a = f.getHeight(o);
            if (a == Double.NaN) {
                System.out.println("NaN from EntityFeature.getHeight()");
                return null;
            }
            double nh = normedHeightMin + ((normedHeightMax - normedHeightMin) * clamp(a, 0.0, 1.0));

            // Scale specified position and diameter by window size
            double nuh = (nh * height);
            double nuy = (ny * height) - (nuh / 2);
            double nux = (nx * width) - (nuh / 2);

            // Test for invalid values
            if (nuh == Double.NaN || nuy == Double.NaN || nux == Double.NaN) {
                System.out.println("NaN positioning cell for EntityRelationView");
                return null;
            }
            if (nuh == 0) {
                return null;
            }

            // Either create a cache for this shape
            // or update the existing cache
            if (cachedShape == null) {
                shapeMap.put(o, new CachedShape(f.getShapeClass(o), shape, width, height));
            } else {
                nux = shape.getX();
                nuy = shape.getY();
                if (width != cachedShape.width() || height != cachedShape.height()) {
                    nux = nux * width / cachedShape.width();
                    nuy = nuy * height / cachedShape.height();
                    cachedShape.setWidth(width);
                    cachedShape.setHeight(height);
                }
            }
            shape.setFrame(nux, nuy, nuh, nuh);
            return shape;
        }

        /**
         * <code>setLocation</code> updates the cached location for an
         * entities shape. Called from mouse motion listener.
         * 
         * @param o
         *            an <code>Object</code> value
         * @param bounds
         *            a <code>Rectangle2D</code> value
         * @param x
         *            a <code>double</code> value
         * @param y
         *            a <code>double</code> value
         */
        public void setLocation(Object o, Rectangle2D bounds, double x, double y) {
            RectangularShape shape = getCachedShape(o);
            if (shape != null) {
                shape.setFrameFromCenter(x, y, x + shape.getWidth() / 2, y + shape.getHeight() / 2);
            }
        }
    }

    /**
     * Constructs an entity relation view.
     */
    public EntityRelationView() {
        this("Entity Relation View");
    }

    /**
     * Constructs an abstract entity relation view.
     * 
     * @param name
     *            a user relevant name for this view
     */
    public EntityRelationView(String name) {
        super(name);
        setPreferredSize(new Dimension(300, 300));

        this.addMouseListener(
            new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    if (!e.isAltDown()) {
                        Agent candidateAgent = getAgentAtPixel(e.getX(), e.getY());
                        selectedAgent = candidateAgent;
                    }
                }
            });

        this.addMouseMotionListener(
            new MouseMotionAdapter() {
                public void mouseDragged(MouseEvent e) {
                    if (!e.isAltDown() && selectedAgent != null && entityDrawFeature != null) {
                        entityDrawFeature.setLocation(selectedAgent, getBounds(), e.getPoint().getX(), e.getPoint().getY());
                        updateScapeGraphics();
                        repaint();
                    }
                }
            });
    }

    /**
     * <code>addDrawFeature</code> adds a new draw feature to the view and
     * determines if it should be initially selected for viewing.
     * 
     * @param feature
     *            an <code>DrawFeature</code> value
     * @param selected
     *            the DrawFeature object to be added
     */
    public void addDrawFeature(DrawFeature feature, boolean selected) {
        addDrawFeature(feature);
        getDrawSelection().setSelected(feature, selected);
    }

    /**
     * add an EntityFeature manager for this view and specify if it should be
     * initially selected.
     * 
     * @param entityFeature
     *            an <code>EntityFeature</code> value
     * @param selected
     *            a <code>boolean</code> value
     */
    public void addEntityFeature(EntityFeature entityFeature, boolean selected) {
        if (entityFeatureGroup == null) {
            entityFeatureGroup = new SelectionGroup(getDrawSelection());
        }
        DrawFeature feature = new EntityDrawFeature(entityFeature.getName(), entityFeature);
        entityFeatureGroup.add(feature);
        addDrawFeature(feature, selected);
    }

    /**
     * add an EntityFeature manager for this view.
     * 
     * @param entityFeature
     *            the entityFeature
     */
    public void addEntityFeature(EntityFeature entityFeature) {
        addEntityFeature(entityFeature, true);
    }

    /**
     * add a RelationFeature manager for this view and specify if the feature
     * should be initially selected.
     * 
     * @param relationFeature
     *            a <code>RelationFeature</code> value
     * @param selected
     *            a <code>boolean</code> value
     */
    public void addRelationFeature(RelationFeature relationFeature, boolean selected) {
        addDrawFeature(new RelationDrawFeature(relationFeature.getName(), relationFeature), selected);
    }

    /**
     * add a RelationFeature manager for this view.
     * 
     * @param relationFeature
     *            the RelationFeature object to be added
     */
    public void addRelationFeature(RelationFeature relationFeature) {
        addRelationFeature(relationFeature, true);
    }

    /**
     * Method.
     */
    public void removeAllRelationFeatures() {
        // FIX.ME
    }

    /* (non-Javadoc)
     * @see org.ascape.view.vis.AgentView#updateScapeGraphics()
     */
    public synchronized void updateScapeGraphics() {
        super.updateScapeGraphics();
        if (scape == null || !scape.isInitialized() || !built) {
            return;
        }

        //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Object selection[] = getDrawSelection().getSelection();
        // find the entityFeature
        Rectangle bounds = getBounds();
        entityDrawFeature = null;
        entityFeature = null;
        for (int i = 0; i < selection.length; i += 1) {
            if (selection[i] instanceof EntityDrawFeature) {
                entityDrawFeature = (EntityDrawFeature) selection[i];
                entityFeature = entityDrawFeature.getEntityFeature();
                for (Iterator j = entityFeature.iterator(); j.hasNext();) {
                    Object o = j.next();
                    if (entityFeature.includesEntity(o)) {
                        entityDrawFeature.draw(bufferedGraphics, o, bounds.width, bounds.height);
                    }
                }
            }
        }
        if (entityFeature == null) {
            return;
        }
        // draw the relation features
        for (Iterator i = entityFeature.iterator(); i.hasNext();) {
            Object orig = i.next();
            if (!entityFeature.includesEntity(orig)) {
                continue;
            }
            for (Iterator j = entityFeature.iterator(); j.hasNext();) {
                Object dest = j.next();
                if (!entityFeature.includesEntity(dest)) {
                    continue;
                }
                // compute displacement of relation channels
                // take the minimum dimension of the two node shapes
                double d = java.lang.Math.min(entityDrawFeature.getMinDim(orig), entityDrawFeature.getMinDim(dest));
                // divide by the number of relations and by 2,
                // because the relations need a channel in both directions
                d = d / (selection.length - 1) / 2;
                // start the first pair of channels at d/2 on either
                // side of the center line between the nodes
                double s = d / 2;
                for (int k = 0; k < selection.length; k += 1) {
                    if (selection[k] instanceof RelationDrawFeature) {
                        RelationDrawFeature relationDrawFeature = (RelationDrawFeature) selection[k];
                        relationDrawFeature.draw(bufferedGraphics, orig, dest, bounds.width, bounds.height, s, d * 0.5);
                        s += d;
                    }
                }
            }
        }
    }

    /**
     * The built.
     */
    private boolean built = false;

    /* (non-Javadoc)
     * @see org.ascape.view.vis.AgentView#build()
     */
    public void build() {
        super.build();
        if (selected != null) {
            for (int i = 0; i < selected.length; i++) {
                getDrawSelection().setSelected(((DrawFeature) selected[i]).getName(), true);
            }
        }
        built = true;
    }

    /**
     * Gets the featureSelected for the EntityRelationView object.
     * 
     * @param featureName
     *            parameter
     * @return the featureSelected
     */
    public boolean isFeatureSelected(String featureName) {
        Object selection[] = getDrawSelection().getSelection();
        for (int i = 0; i < selection.length; i += 1) {
            DrawFeature feature = (DrawFeature) selection[i];
            if (featureName.equals(feature.getName()) && getDrawSelection().isSelected(i)) {
                // System.err.println("isFeatureSelected("+featureName+") is true");
                return true;
            }
        }
        // System.err.println("isFeatureSelected("+featureName+") is false");
        return false;
    }

    /**
     * <code>getColorFeature</code> returns the color feature used to
     * determine node/cell colors.
     * 
     * @return a <code>ColorFeature</code> value
     */
    public ColorFeature getPrimaryAgentColorFeature() {
        return entityFeature;
    }

    /**
     * <code>getColorFeature</code> returns the color feature used to
     * determine node/cell colors.
     * 
     * @return a <code>ColorFeature</code> value
     */
    public ColorFeature getAgentColorFeature() {
        return entityFeature;
    }

    /**
     * Method to find the cell under the specified point.
     * 
     * @param x
     *            the x coordinate of the point.
     * @param y
     *            the y coordinate of the point.
     * @return the cellAtPixel
     */
    public Agent getAgentAtPixel(int x, int y) {
        Point2D point = new Point2D.Float(x, y);
        if (entityFeature != null && entityDrawFeature != null) {
            for (Iterator i = entityFeature.iterator(); i.hasNext();) {
                Object o = i.next();
                RectangularShape shape = entityDrawFeature.getCachedShape(o);
                if (shape != null && shape.contains(point)) {
                    return (Agent) o;
                }
            }
        }
        return null;
    }

    /**
     * The selected.
     */
    private Object[] selected;

    /**
     * The object implements the writeExternal method to save its contents by
     * calling the methods of DataOutput for its primitive values or calling the
     * writeObject method of ObjectOutput for objects, strings, and arrays.
     * 
     * @param out
     *            the stream to write the object to
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @serialData Overriding methods should use this tag to describe the data
     *             layout of this Externalizable object. List the sequence of
     *             element types and, if possible, relate the element to a
     *             public/protected field and/or method of this Externalizable
     *             class.
     * @exception java.io.IOException
     *                Includes any I/O exceptions that may occur
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(entityDrawFeature);
        out.writeObject(entityFeature);
        out.writeObject(entityFeatureGroup);
        out.writeObject(selectedAgent);
        out.writeObject(viewCustomizer);
        selected = new Object[getDrawSelection().getSelectionSize()];
        for (int i = 0; i < selected.length; i++) {
            selected[i] = getDrawSelection().getSelectedElement(i);
        }
        out.writeObject(selected);
    }

    /**
     * The object implements the readExternal method to restore its contents by
     * calling the methods of DataInput for primitive types and readObject for
     * objects, strings and arrays. The readExternal method must read the values
     * in the same sequence and with the same types as were written by
     * writeExternal.
     * 
     * @param in
     *            the stream to read data from in order to restore the object
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ClassNotFoundException
     *             the class not found exception
     * @exception java.io.IOException
     *                if I/O errors occur
     * @exception java.lang.ClassNotFoundException
     *                If the class for an object being restored cannot be found.
     */
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        entityDrawFeature = (EntityDrawFeature) in.readObject();
        entityFeature = (EntityFeature) in.readObject();
        entityFeatureGroup = (SelectionGroup) in.readObject();
        selectedAgent = (Agent) in.readObject();
        viewCustomizer = (ERVViewCustomizer) in.readObject();
        selected = (Object[]) in.readObject();
    }
}
