
package org.ascape.view.vis;

import java.util.TooManyListenersException;

import org.ascape.model.Agent;
import org.ascape.model.event.ScapeEvent;
import org.ascape.util.vis.ColorFeature;
import org.ascape.util.vis.ColorFeatureConcrete;
import org.eclipse.draw2d.AbstractLayout;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IViewPart;



public class GraphicsView extends EditPartScapeView {

    private static final long serialVersionUID = 1L;

    public GraphicsView(String name) {
        super("org.eclipse.amp.escape.view.GraphicsView", null, name);
    }

    /**
     * The generic color feature used to set the color for the primary agent type.
     */
    protected ColorFeature agentColorFeature;

    // public Transform createTR(Graphics graphics) {
    // Transform transform = new Transform(Device.getDevice());
    // graphics.setTransform(transform);
    // return transform;
    // }

    protected void updateScapeGraphics(Graphics graphics) {
    }

    class GraphicsEditPart extends AbstractGraphicalEditPart {

        protected IFigure createFigure() {
            imageFigure = new Figure() {
                public void paintFigure(Graphics graphics) {
                    synchronized (GraphicsView.this) {
                        if (image != null && !image.isDisposed()) {
                            graphics.drawImage(image, 0, 0);
                        }
                    }
                }
            };

            imageFigure.setLayoutManager(new AbstractLayout() {
                public Dimension getMinimumSize(IFigure container, int hintWidth, int hintHeight) {
                    if (getScape() != null) {
                        return getMinimumSizeWithin(new Dimension(hintWidth, hintHeight));
                    }
                    return new Dimension(500, 500);
                }

                protected Dimension calculatePreferredSize(IFigure container, int hintWidth, int hintHeight) {
                    if (getScape() != null) {
                        return getPreferredSizeWithin(new Dimension(hintWidth, hintHeight));
                    } else {
                        return new Dimension(500, 500);
                    }
                }

                public void layout(IFigure container) {
                    imageFigure.setLocation(new Point(0, 0));
                }
            });
            imageFigure.addFigureListener(new FigureListener() {
                public void figureMoved(IFigure source) {
                    refresh();
                }
            });
            return imageFigure;
        }

        protected void createEditPolicies() {
        }

        protected synchronized void createImage() {
            Dimension size = getPreferredSizeWithin(imageFigure.getSize());
            if (size.width <= 0 || size.height <= 0) {
                size = new Dimension(1, 1);
            }
            if (image == null || !imageFigure.getSize().equals(image.getBounds())) {
                if (image != null) {
                    // Need to release prior resources..
                    if (!image.isDisposed()) {
                        image.dispose();
                        imageGC.dispose();
                        imageGraphics.dispose();
                    }
                }
                // System.out.println("New: " + imageFigure.getSize());
                // System.out.println("OPld:" + image.getBounds());
                image = new Image(getViewer().getControl().getDisplay(), new org.eclipse.swt.graphics.Rectangle(0, 0,
                                                                                                                size.width, size.height));
                imageGC = new GC(image);
                imageGraphics = new SWTGraphics(imageGC);
            }
            updateScapeGraphics(imageGraphics);
            imageFigure.repaint();
        }

        public void refresh() {
            createImage();
            getFigure().repaint();
        }

        public void setModel(Object model) {
            super.setModel(model);
            createFeatures();
        }

        @Override
        public Object getAdapter(Class key) {
            if (key == IViewPart.class) {
                return getViewPart();
            }
            return super.getAdapter(key);
        }
    };

    public EditPart createEditPart(EditPart context, Object model) {
        return new GraphicsEditPart();
    }

    protected Image image;

    IFigure imageFigure;

    /**
     * Size of each individual cell, typically in pixels. 6 by default.
     */
    protected int agentSize = 20;

    protected GC imageGC;

    private Graphics imageGraphics;

    public void createFeatures() {
        if (agentColorFeature == null) {
            agentColorFeature = new ColorFeatureConcrete("Default Agent Color") {
                private static final long serialVersionUID = -3220486698290440285L;

                public Color getColor(Object object) {
                    return (Color) ((Agent) object).getPlatformColor();
                }
            };
        }
    }

    public void scapeAdded(ScapeEvent scapeEvent) throws TooManyListenersException {
        super.scapeAdded(scapeEvent);
        createFeatures();
    }

    public Dimension getSize() {
        return imageFigure.getSize();
    }

    public Dimension getPreferredSizeWithin(Dimension d) {
        return new Dimension(d.width < 1 ? 500 : d.width, d.height < 1 ? 500 : d.height);
    }

    public Dimension getMinimumSizeWithin(Dimension d) {
        return new Dimension(1, 1);
    }

    public Image getImage() {
        return image;
    }
}