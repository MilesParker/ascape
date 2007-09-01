/*
 * This software and documentation is subject to, and may be only used in accordance with, the terms and conditions of
 * the license agreement described or set forth in the file titled License.txt.  This software and documentation
 * contains information that is confidential and proprietary to NuTech Solutions, Inc.
 *
 *             Copyright (c) 2000-2004
 *             NuTech Solutions, Inc.
 *  
 * Portions of the software are licensed from The Brookings Institution
 *             Copyright (c) 1998-2000
 *            The Brookings Institution
 *          All rights reserved worldwide.
 * 
 * U.S. GOVERNMENT END USERS. The software is a "commercial item," as that term is defined in 48 C.F.R. 2.101,
 * consisting of "commercial computer software" and "commercial computer software documentation," as such terms are
 * used in 48 C.F.R. 12.212. Consistent with 48 C.F.R. 12.212 and 48 C.F.R. 227.7202-1 through 227.7202-4, and other
 * relevant sections of the Code of Federal Regulations, as applicable all U.S. Government End Users acquire the
 * software with only those rights set forth in the license agreement. This software includes trade secrets that
 * are exempt from disclosure under the Freedom of Information Act.  
 *
 *
 * Boids Model
 *
 * June - July 2002
 *
 * An implementation of Craig Reynold's Boids model in Ascape.
 *             Copyright (c) 2002-2004-2004
 *                NuTech Solutions,Inc.
 *
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
        public void draw(Graphics g, Object object, int x, int y) {
            if (object instanceof Obstacle) {
                Obstacle o = (Obstacle) object;
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
