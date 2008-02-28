/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */


package org.ascape.examples.boids.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import org.ascape.examples.boids.BaseModel;
import org.ascape.examples.boids.Boid;
import org.ascape.examples.boids.Obstacle;
import org.ascape.util.vis.DrawFeature;
import org.ascape.view.vis.Overhead2DContinuousView;


public class BoidsView extends Overhead2DContinuousView {

    public final DrawFeature boid_polygon_draw_feature = new DrawFeature("Boid Polygon") {
        /**
         * 
         */
        private static final long serialVersionUID = 7912571631811473142L;

        public void draw(Graphics g, Object object, int x, int y) {
            if (object instanceof Obstacle) {
                g.setColor(Color.black);
                g.drawLine(-x / 2, y / 2, x / 2, -y / 2);
                g.drawLine(-x / 2, -y / 2, x / 2, y / 2);
            } else if (object instanceof Boid) {
                Boid agent = (Boid) object;
                g.setColor(agent.getColor());
                Polygon poly = new Polygon();
                poly.addPoint(-x / 2, y / 2);
                poly.addPoint(x / 2, 0);
                poly.addPoint(-x / 2, -y / 2);
                poly.addPoint(0, 0);
                // rotate graphic
                ((Graphics2D) g).rotate(agent.getHeading());
                g.fillPolygon(poly);
                // de-rotate graphic
                ((Graphics2D) g).rotate(-agent.getHeading());
            } else {
                throw new RuntimeException("Invalid Object: " + object);
            }
        }
    };

    public BoidsView() {
        super();
        addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                if (e.isShiftDown() && (!e.isAltDown())) {
                    placeObstacle(e.getPoint());
                    notifyScapeUpdated();
                    repaint();
                }
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }
        });
    }

    public void build() {
        super.build();
        getDrawSelection().clear();
        boid_polygon_draw_feature.setName(getName() + " Agents");
        getDrawSelection().addElement(boid_polygon_draw_feature, true);
    }

    /**
     * On notification of a scape update, draws the actual overhead view.
     */
    public synchronized void updateScapeGraphics() {
        super.updateScapeGraphics();
    }

    /**
     * Place an obstacle at the specified pixel point.
     * Synchronized against updateScape graphics to avoid comdification problems.
     * @param point the _pixel_ point to place the obstacle at
     */
    protected synchronized void placeObstacle(Point point) {
        Obstacle obstacle = new Obstacle();
        obstacle.moveTo(getCoordinateAtPixel((int) point.getX(), (int) point.getY()));
        ((BaseModel) getScape().getRoot()).getObstacles().add(obstacle);
        obstacle.initialize();
    }

}
