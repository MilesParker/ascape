/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.runtime.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.Customizer;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.io.Serializable;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.JWindow;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.ascape.model.Agent;
import org.ascape.model.Cell;
import org.ascape.view.custom.BaseCustomizer;
import org.ascape.view.vis.AgentSizedView;
import org.ascape.view.vis.AgentView;
import org.ascape.view.vis.ComponentView;


/**
 * A class allowing Frame, JFrame, and JInternalFrame to be used interchangeably,
 * and providing general support for frame management. This class also ends up
 * mediating and centralizing all swing/non-swing deployment issues. All Swing
 * related userEnvironment calls are sent to this class so that all swing
 * functionality can be removed in one place. This will change once the majority
 * of browsers support Swing.
 * 
 * @author Miles Parker
 * @version 3.0
 * @history 3.0 7/22/02 new internal frame changes
 * @history 2.9 5/9/02 updated for new movie refactorings
 * @history 2.9 3/1/02 many ongoing changes.
 * @history 1.2 8/2/99 first in, replaces ViewAbstractFrame, ViewFrame,
 *          ViewJFrame, ViewJInteranlFrameMan
 * @since 1.2
 */
public class ViewFrameBridge implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 3459580209958069427L;

    /**
     * The Constant POPUP_MAP_WIDTH.
     */
    private static final int POPUP_MAP_WIDTH = 500;

    /**
     * The Constant POPUP_MAP_HEIGHT.
     */
    private static final int POPUP_MAP_HEIGHT = 500;

    /**
     * Provides a basic (non-swing) Frame for an ascape view.
     */
    class ViewFrame extends Frame implements Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = 5646334137198779963L;

        /**
         * Returns the content pane. In this case, returns self; used for
         * compatability with Swing frames.
         * 
         * @return the content pane
         */
        public Container getContentPane() {
            return this;
        }

        /**
         * Set initial size upon notification of peer creation.
         */
        public void addNotify() {
            Dimension d = getSize();
            super.addNotify();
            setSize(getInsets().left + getInsets().right + d.width, getInsets().top + getInsets().bottom + d.height);
        }

        /* (non-Javadoc)
         * @see java.awt.Window#dispose()
         */
        public void dispose() {
            onFrameDispose();
            super.dispose();
        }
    }

    /**
     * Provides a JFrame for an ascape view. Comment out for web.
     */
    class ViewJFrame extends JFrame implements Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = -9145580460391689664L;

        /**
         * Set initial size upon notification of peer creation.
         */
        public void addNotify() {
            Dimension d = getSize();
            super.addNotify();
            setSize(getInsets().left + getInsets().right + d.width, getInsets().top + getInsets().bottom + d.height);
        }

        /* (non-Javadoc)
         * @see java.awt.Window#dispose()
         */
        public void dispose() {
            onFrameDispose();
            super.dispose();
        }
    }

    /**
     * A class providing an internal frame for an ascape view.
     */
    class ViewJInternalFrame extends JInternalFrame implements ViewPanZoomFrame, Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = 1677193503023573560L;

        /**
         * Set initial size upon notification of peer creation.
         */
        public void addNotify() {
            //Dimension d = getSize();
            super.addNotify();
            addInternalFrameListener(new InternalFrameAdapter() {
                public void internalFrameIconified(InternalFrameEvent e) {
                    for (int i = 0; i < views.length; i++) {
                        views[i].forceScapeNotify();
                    }
                }
            });
            addInternalFrameListener(new InternalFrameAdapter() {
                public void internalFrameDeactivated(InternalFrameEvent e) {
                    for (int i = 0; i < views.length; i++) {
                        views[i].forceScapeNotify();
                    }
                }
            });
            //setSize(getInsets().left + getInsets().right + d.width, getInsets().top + getInsets().bottom + d.height);
        }

        /* (non-Javadoc)
         * @see org.ascape.explorer.ViewPanZoomFrame#getPreferredSizeWithin(java.awt.Dimension)
         */
        public Dimension getPreferredSizeWithin(Dimension d) {
            if (isLockZoomToFrame()) {
                //We may need ot adjust the code below for more specific instances; for now it works fine for all
                //1d and 2d views...
                int frameWidth;
                int frameHeight;
                frameWidth = (int) getSize().getWidth() - scrollPanel.getViewport().getWidth();
                frameHeight = (int) getSize().getHeight() - scrollPanel.getViewport().getHeight();
                d.setSize((int) d.getWidth() - frameWidth, (int) d.getHeight() - frameHeight);
                if (views.length == 1) {
                    d = views[0].getPreferredSizeWithin(d);
                } else if (views.length > 1) {
                    if (DesktopEnvironment.getDefaultDesktop().getMultiViewMode() == DesktopEnvironment.GRID_MULTIVIEW_MODE
                            || DesktopEnvironment.getDefaultDesktop().getMultiViewMode() == DesktopEnvironment.GRID_MULTIVIEW_LABEL_MODE)
                    {
                        int xRows = ((GridLayout) viewPanel.getLayout()).getRows();
                        int xCols = ((GridLayout) viewPanel.getLayout()).getColumns();
                        //The following code is somewhat confusing, don't touch unless you understand logic
                        //The basic idea is that we need to translate size to individual cell size and back again
                        if (DesktopEnvironment.getDefaultDesktop().getMultiViewMode() == DesktopEnvironment.GRID_MULTIVIEW_MODE) {
                            d.setSize((int) ((d.getWidth() + gridBorderSize) / xCols) - gridBorderSize, (int) ((d.getHeight() + gridBorderSize) / xRows) - gridBorderSize);
                            d = views[0].getPreferredSizeWithin(d);
                            d.setSize((int) ((d.getWidth() + gridBorderSize) * xCols) - gridBorderSize, (int) ((d.getHeight() + gridBorderSize) * xRows) - gridBorderSize);
                        } else {
                            d.setSize((int) ((d.getWidth() + gridBorderSize) / xCols) - gridBorderSize, (int) ((d.getHeight() + gridBorderSize) / xRows) - gridBorderSize - labelSize);
                            d = views[0].getPreferredSizeWithin(d);
                            d.setSize((int) ((d.getWidth() + gridBorderSize) * xCols) - gridBorderSize, (int) ((d.getHeight() + gridBorderSize + labelSize) * xRows) - gridBorderSize);
                        }
                    } else if (DesktopEnvironment.getDefaultDesktop().getMultiViewMode() == DesktopEnvironment.TABBED_MULTIVIEW_MODE) {
                        //Just que off of first view size, though we may want to do max instead...
                        d = views[0].getPreferredSizeWithin(d);
                    }
                }
                d.setSize((int) d.getWidth() + frameWidth, (int) d.getHeight() + frameHeight);
                int newAgentSize = ((AgentSizedView) views[0]).calculateAgentSizeForViewSize(d);
                ((AgentSizedView) views[0]).setAgentSize(newAgentSize);
            }
            return d;
        }

        /* (non-Javadoc)
         * @see org.ascape.explorer.ViewPanZoomFrame#getSizeForAgentSize(int)
         */
        public Dimension getSizeForAgentSize(int size) {
            //We may need ot adjust the code below for more specific instances; for now it works fine for all
            //1d and 2d views...
            Dimension d = new Dimension();
            if (views.length == 1) {
                d = ((AgentSizedView) views[0]).calculateViewSizeForAgentSize(size);
            } else if (views.length > 1) {
                if (DesktopEnvironment.getDefaultDesktop().getMultiViewMode() == DesktopEnvironment.GRID_MULTIVIEW_MODE
                        || DesktopEnvironment.getDefaultDesktop().getMultiViewMode() == DesktopEnvironment.GRID_MULTIVIEW_LABEL_MODE)
                {
                    int xRows = ((GridLayout) viewPanel.getLayout()).getRows();
                    int xCols = ((GridLayout) viewPanel.getLayout()).getColumns();
                    //The following code is somewhat confusing, don't touch unless you understand logic
                    //The basic idea is that we need to translate size to individual cell size and back again
                    d = ((AgentSizedView) views[0]).calculateViewSizeForAgentSize(size);
                    if (DesktopEnvironment.getDefaultDesktop().getMultiViewMode() == DesktopEnvironment.GRID_MULTIVIEW_MODE) {
                        d.setSize((int) ((d.getWidth() + gridBorderSize) * xCols) - gridBorderSize, (int) ((d.getHeight() + gridBorderSize) * xRows) - gridBorderSize);
                    } else {
                        d.setSize((int) ((d.getWidth() + gridBorderSize) * xCols) - gridBorderSize, (int) ((d.getHeight() + gridBorderSize + labelSize) * xRows) - gridBorderSize);
                    }
                } else if (DesktopEnvironment.getDefaultDesktop().getMultiViewMode() == DesktopEnvironment.TABBED_MULTIVIEW_MODE) {
                    d = ((AgentSizedView) views[0]).calculateViewSizeForAgentSize(size);
                }
            }
            int frameWidth = (int) getSize().getWidth() - scrollPanel.getViewport().getWidth();
            int frameHeight = (int) getSize().getHeight() - scrollPanel.getViewport().getHeight();
            d.setSize(d.width + frameWidth, d.height + frameHeight);
            return d;
        }

        /* (non-Javadoc)
         * @see javax.swing.JInternalFrame#dispose()
         */
        public void dispose() {
            onFrameDispose();
            super.dispose();
        }

        /* (non-Javadoc)
         * @see org.ascape.explorer.ViewPanZoomFrame#getBridge()
         */
        public ViewFrameBridge getBridge() {
            return ViewFrameBridge.this;
        }
    }

    /**
     * A class providing a frame with no title bar, window management buttons,
     * etc., for an ascape view
     */
    class ViewJWindow extends JWindow implements ViewPanZoomFrame, Serializable {


        /**
         * 
         */
        private static final long serialVersionUID = 7421071765403310411L;

        /**
         * Instantiates a new view J window.
         */
        public ViewJWindow() {
            super(new JFrame());
            getOwner().setBounds(10,10,50,50);
            getOwner().setVisible(true);
        }

        /* (non-Javadoc)
         * @see org.ascape.explorer.ViewPanZoomFrame#getPreferredSizeWithin(java.awt.Dimension)
         */
        public Dimension getPreferredSizeWithin(Dimension d) {
            if (isLockZoomToFrame()) {
                //We may need ot adjust the code below for more specific instances; for now it works fine for all
                //1d and 2d views...
                int frameWidth;
                int frameHeight;
                frameWidth = (int) getSize().getWidth() - scrollPanel.getViewport().getWidth();
                frameHeight = (int) getSize().getHeight() - scrollPanel.getViewport().getHeight();
                d.setSize((int) d.getWidth() - frameWidth, (int) d.getHeight() - frameHeight);
                if (views.length == 1) {
                    d = views[0].getPreferredSizeWithin(d);
                } else if (views.length > 1) {
                    if (DesktopEnvironment.getDefaultDesktop().getMultiViewMode() == DesktopEnvironment.GRID_MULTIVIEW_MODE
                            || DesktopEnvironment.getDefaultDesktop().getMultiViewMode() == DesktopEnvironment.GRID_MULTIVIEW_LABEL_MODE)
                    {
                        int xRows = ((GridLayout) viewPanel.getLayout()).getRows();
                        int xCols = ((GridLayout) viewPanel.getLayout()).getColumns();
                        //The following code is somewhat confusing, don't touch unless you understand logic
                        //The basic idea is that we need to translate size to individual cell size and back again
                        if (DesktopEnvironment.getDefaultDesktop().getMultiViewMode() == DesktopEnvironment.GRID_MULTIVIEW_MODE) {
                            d.setSize((int) ((d.getWidth() + gridBorderSize) / xCols) - gridBorderSize, (int) ((d.getHeight() + gridBorderSize) / xRows) - gridBorderSize);
                            d = views[0].getPreferredSizeWithin(d);
                            d.setSize((int) ((d.getWidth() + gridBorderSize) * xCols) - gridBorderSize, (int) ((d.getHeight() + gridBorderSize) * xRows) - gridBorderSize);
                        } else {
                            d.setSize((int) ((d.getWidth() + gridBorderSize) / xCols) - gridBorderSize, (int) ((d.getHeight() + gridBorderSize) / xRows) - gridBorderSize - labelSize);
                            d = views[0].getPreferredSizeWithin(d);
                            d.setSize((int) ((d.getWidth() + gridBorderSize) * xCols) - gridBorderSize, (int) ((d.getHeight() + gridBorderSize + labelSize) * xRows) - gridBorderSize);
                        }
                    } else if (DesktopEnvironment.getDefaultDesktop().getMultiViewMode() == DesktopEnvironment.TABBED_MULTIVIEW_MODE) {
                        //Just que off of first view size, though we may want to do max instead...
                        d = views[0].getPreferredSizeWithin(d);
                    }
                }
                d.setSize((int) d.getWidth() + frameWidth, (int) d.getHeight() + frameHeight);

            }
            return d;
        }

        /* (non-Javadoc)
         * @see org.ascape.explorer.ViewPanZoomFrame#getSizeForAgentSize(int)
         */
        public Dimension getSizeForAgentSize(int size) {
            //We may need ot adjust the code below for more specific instances; for now it works fine for all
            //1d and 2d views...
            Dimension d = new Dimension();
            if (views.length == 1) {
                d = ((AgentSizedView) views[0]).calculateViewSizeForAgentSize(size);
            } else if (views.length > 1) {
                if (DesktopEnvironment.getDefaultDesktop().getMultiViewMode() == DesktopEnvironment.GRID_MULTIVIEW_MODE
                        || DesktopEnvironment.getDefaultDesktop().getMultiViewMode() == DesktopEnvironment.GRID_MULTIVIEW_LABEL_MODE)
                {
                    int xRows = ((GridLayout) viewPanel.getLayout()).getRows();
                    int xCols = ((GridLayout) viewPanel.getLayout()).getColumns();
                    //The following code is somewhat confusing, don't touch unless you understand logic
                    //The basic idea is that we need to translate size to individual cell size and back again
                    d = ((AgentSizedView) views[0]).calculateViewSizeForAgentSize(size);
                    if (DesktopEnvironment.getDefaultDesktop().getMultiViewMode() == DesktopEnvironment.GRID_MULTIVIEW_MODE) {
                        d.setSize((int) ((d.getWidth() + gridBorderSize) * xCols) - gridBorderSize, (int) ((d.getHeight() + gridBorderSize) * xRows) - gridBorderSize);
                    } else {
                        d.setSize((int) ((d.getWidth() + gridBorderSize) * xCols) - gridBorderSize, (int) ((d.getHeight() + gridBorderSize + labelSize) * xRows) - gridBorderSize);
                    }
                } else if (DesktopEnvironment.getDefaultDesktop().getMultiViewMode() == DesktopEnvironment.TABBED_MULTIVIEW_MODE) {
                    d = ((AgentSizedView) views[0]).calculateViewSizeForAgentSize(size);
                }
            }
            int frameWidth = (int) getSize().getWidth() - scrollPanel.getViewport().getWidth();
            int frameHeight = (int) getSize().getHeight() - scrollPanel.getViewport().getHeight();
            d.setSize(d.width + frameWidth, d.height + frameHeight);
            return d;
        }

        /* (non-Javadoc)
         * @see java.awt.Window#dispose()
         */
        public void dispose() {
            onFrameDispose();
            super.dispose();
        }

        /* (non-Javadoc)
         * @see org.ascape.explorer.ViewPanZoomFrame#getBridge()
         */
        public ViewFrameBridge getBridge() {
            return ViewFrameBridge.this;
        }
    }

    /**
     * The Class ViewPanel.
     */
    private class ViewPanel extends JPanel implements Scrollable {

        /**
         * 
         */
        private static final long serialVersionUID = 924757892310665777L;

        /* (non-Javadoc)
         * @see javax.swing.Scrollable#getPreferredScrollableViewportSize()
         */
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        /* (non-Javadoc)
         * @see javax.swing.Scrollable#getScrollableBlockIncrement(java.awt.Rectangle, int, int)
         */
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 20;
        }

        /* (non-Javadoc)
         * @see javax.swing.Scrollable#getScrollableTracksViewportHeight()
         */
        public boolean getScrollableTracksViewportHeight() {
            return isLockZoomToFrame();
        }

        /* (non-Javadoc)
         * @see javax.swing.Scrollable#getScrollableTracksViewportWidth()
         */
        public boolean getScrollableTracksViewportWidth() {
            return isLockZoomToFrame();
        }

        /* (non-Javadoc)
         * @see javax.swing.Scrollable#getScrollableUnitIncrement(java.awt.Rectangle, int, int)
         */
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 20;
        }
    }

    /**
     * The Class ZoomAction.
     */
    private abstract class ZoomAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = -3527370556448927637L;
        /**
         * The delta.
         */
        int delta;

        /**
         * Instantiates a new zoom action.
         * 
         * @param name
         *            the name
         * @param icon
         *            the icon
         * @param delta
         *            the delta
         */
        public ZoomAction(String name, Icon icon, int delta) {
            super(name, icon);
            this.delta = delta;
        }

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            AgentSizedView agentSizedView = (AgentSizedView) views[0];
            if (isLockZoomToFrame()) {
                getFrameImp().setSize(((ViewPanZoomFrame) getFrameImp()).getSizeForAgentSize(agentSizedView.getAgentSize() + delta));
                agentSizedView.setAgentSize(agentSizedView.getAgentSize() + delta);
            } else {
                for (int i = 0; i < views.length; i++) {
                    AgentSizedView view = (AgentSizedView) views[i];
                    view.setAgentSize(view.getAgentSize() + delta);
                }
            }
        }
    }

    /**
     * The Class ZoomInAction.
     */
    private class ZoomInAction extends ZoomAction {

        /**
         * 
         */
        private static final long serialVersionUID = -1718533012688935439L;

        /**
         * Instantiates a new zoom in action.
         */
        public ZoomInAction() {
            super("", DesktopEnvironment.getIcon("MagnifyPlus"), 1);
            putValue(Action.SHORT_DESCRIPTION, "Zoom in (increase cell size)");
        }
    }

    /**
     * The Class ZoomOutAction.
     */
    private class ZoomOutAction extends ZoomAction {

        /**
         * 
         */
        private static final long serialVersionUID = -8991802904647063089L;

        /**
         * Instantiates a new zoom out action.
         */
        public ZoomOutAction() {
            super("", DesktopEnvironment.getIcon("MagnifyMinus"), -1);
            putValue(Action.SHORT_DESCRIPTION, "Zoom out (decrease cell size)");
        }
    }

    /**
     * The Class ZoomLockAction.
     */
    private class ZoomLockAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = -5023319833662503132L;

        /**
         * Instantiates a new zoom lock action.
         */
        public ZoomLockAction() {
            super("", DesktopEnvironment.getIcon("UnLock"));
            putValue(Action.SHORT_DESCRIPTION, "Lock Frame Size to View Size");
        }

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            setLockZoomToFrame(true);
        }
    }

    /**
     * The Class ZoomUnLockAction.
     */
    private class ZoomUnLockAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = -4163993041024403592L;

        /**
         * Instantiates a new zoom un lock action.
         */
        public ZoomUnLockAction() {
            super("", DesktopEnvironment.getIcon("Lock"));
            putValue(Action.SHORT_DESCRIPTION, "Unlock Frame Size from View Size");
        }

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            setLockZoomToFrame(false);
        }
    }

    /**
     * The backing frame providing the frame implementation.
     */
    private transient Container frameImp;

    /**
     * The view toolbar.
     */
    private transient JToolBar viewToolbar;

    /**
     * The view panel.
     */
    private JPanel viewPanel;

    /**
     * The scroll panel.
     */
    private JScrollPane scrollPanel;

    /**
     * The status text.
     */
    private JLabel statusText;

    /**
     * The views that this frame is responsible for.
     */
    private ComponentView[] views;

    /**
     * The grid border size.
     */
    private int gridBorderSize = 3;

    /**
     * The label size.
     */
    private int labelSize = 8;

    /**
     * The bounds.
     */
    private Rectangle bounds;

    /**
     * The iconified.
     */
    private boolean iconified;

    /**
     * The lock zoom to frame.
     */
    private boolean lockZoomToFrame = true;

    /**
     * The lock unlock zoom button.
     */
    private transient JButton lockUnlockZoomButton;

    /**
     * The mouse down.
     */
    private boolean mouseDown;

    /**
     * The start mouse drag.
     */
    private Point startMouseDrag;

    /**
     * Determines whether the ViewFrameBridge's views will be removed from
     * the set of scape listeners. Default is true.
     */
    private boolean removeListenerOnDispose = true;
    
    public void setRemoveListenerOnDispose(boolean removeListenerOnDispose) {
		this.removeListenerOnDispose = removeListenerOnDispose;
	}
    
    /**
     * Construct a frame bridge.
     */
    public ViewFrameBridge() {
        super();
    }

    /**
     * Construct a frame bridge.
     * 
     * @param views
     *            the views
     */
    public ViewFrameBridge(ComponentView[] views) {
        this();
        setViews(views);
    }

    /**
     * Construct a frame bridge.
     * 
     * @param view
     *            the view
     */
    public ViewFrameBridge(ComponentView view) {
        this();
        setView(view);
    }

    /**
     * An internal method that picks the appropriate frame for the view mode.
     */
    private void selectFrameImp() {
        if (!SwingRunner.isMultiWinEnvironment()) {
            switch (DesktopEnvironment.getDefaultDesktop().getViewMode()) {
                //Comment out the following lines to support non-swing versions
                case DesktopEnvironment.CLASSIC_VIEW_MODE:
                    frameImp = new ViewJFrame();
                    //Test without, better for customizers
                    ((ViewJFrame) frameImp).setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    viewPanel.setLayout(new BorderLayout(0, 0));
                    ((ViewJFrame) frameImp).setIconImage(DesktopEnvironment.getImage("Ascape16.gif"));
                    frameImp.setVisible(false);
                    frameImp.setSize(0, 0);
                    ((ViewJFrame) frameImp).addWindowListener(new WindowAdapter() {
                        public void windowActivated(WindowEvent e) {
                            if (DesktopEnvironment.getDefaultDesktop().getControlBarView() != null) {
                                if (ViewFrameBridge.this != DesktopEnvironment.getDefaultDesktop().getControlBarView()
                                        .getViewFrame()) {
                                    DesktopEnvironment.getDefaultDesktop().getControlBarView()
                                    .setIgnoreFrameSelection(true);
                                    DesktopEnvironment.getDefaultDesktop().getControlBarView()
                                    .setFrameSelection(ViewFrameBridge.this);
                                    DesktopEnvironment.getDefaultDesktop().getControlBarView()
                                    .setIgnoreFrameSelection(false);
                                } else {
                                    DesktopEnvironment.getDefaultDesktop().getControlBarView()
                                    .setIgnoreFrameSelection(true);
                                    DesktopEnvironment.getDefaultDesktop().getControlBarView().setFrameSelection(null);
                                    DesktopEnvironment.getDefaultDesktop().getControlBarView()
                                    .setIgnoreFrameSelection(false);
                                }
                            }
                        }

                        public void windowDeiconified(WindowEvent e) {
                            if (DesktopEnvironment.getDefaultDesktop().getControlBarView() != null) {
                                if (ViewFrameBridge.this != DesktopEnvironment.getDefaultDesktop().getControlBarView()
                                        .getViewFrame()) {
                                    DesktopEnvironment.getDefaultDesktop().getControlBarView()
                                    .setIgnoreFrameSelection(true);
                                    DesktopEnvironment.getDefaultDesktop().getControlBarView()
                                    .setFrameSelection(ViewFrameBridge.this);
                                    DesktopEnvironment.getDefaultDesktop().getControlBarView()
                                    .setIgnoreFrameSelection(false);
                                } else {
                                    DesktopEnvironment.getDefaultDesktop().getControlBarView()
                                    .setIgnoreFrameSelection(true);
                                    DesktopEnvironment.getDefaultDesktop().getControlBarView().setFrameSelection(null);
                                    DesktopEnvironment.getDefaultDesktop().getControlBarView()
                                    .setIgnoreFrameSelection(false);
                                }
                            }
                        }

                        public void windowClosed(WindowEvent e) {
                            DesktopEnvironment.getDefaultDesktop().frames.removeElement(this);
                            if (DesktopEnvironment.getDefaultDesktop().getControlBarView() != null) {
                                DesktopEnvironment.getDefaultDesktop().getControlBarView().setFrameSelection(null);
                            }
                        }
                    });
                    break;
                case DesktopEnvironment.MDI_VIEW_MODE:
                    frameImp = new ViewJInternalFrame();
                    DesktopEnvironment.getDefaultDesktop().getUserFrame().getDesk().add(frameImp,
                                                                                        JLayeredPane.DEFAULT_LAYER);
                    ((ViewJInternalFrame) frameImp).setDefaultCloseOperation(ViewJInternalFrame.DISPOSE_ON_CLOSE);
                    frameImp.setVisible(false);
                    //				frameImp.setSize(0,0);
                    ((ViewJInternalFrame) frameImp).setClosable(true);
                    ((ViewJInternalFrame) frameImp).setDoubleBuffered(false);
                    ((ViewJInternalFrame) frameImp).setIconifiable(true);
                    ((ViewJInternalFrame) frameImp).setResizable(true);
                    ((ViewJInternalFrame) frameImp).addInternalFrameListener(new InternalFrameAdapter() {
                        public void internalFrameActivated(InternalFrameEvent e) {
                            if (DesktopEnvironment.getDefaultDesktop().getControlBarView() != null) {
                                if (ViewFrameBridge.this != DesktopEnvironment.getDefaultDesktop().getControlBarView()
                                        .getViewFrame()) {
                                    DesktopEnvironment.getDefaultDesktop().getControlBarView()
                                    .setIgnoreFrameSelection(true);
                                    DesktopEnvironment.getDefaultDesktop().getControlBarView()
                                    .setFrameSelection(ViewFrameBridge.this);
                                    DesktopEnvironment.getDefaultDesktop().getControlBarView()
                                    .setIgnoreFrameSelection(false);
                                } else {
                                    DesktopEnvironment.getDefaultDesktop().getControlBarView()
                                    .setIgnoreFrameSelection(true);
                                    DesktopEnvironment.getDefaultDesktop().getControlBarView().setFrameSelection(null);
                                    DesktopEnvironment.getDefaultDesktop().getControlBarView()
                                    .setIgnoreFrameSelection(false);
                                }
                            }
                        }

                        public void internalFrameClosed(InternalFrameEvent e) {
                            DesktopEnvironment.getDefaultDesktop().frames.removeElement(this);
                            if (DesktopEnvironment.getDefaultDesktop().getControlBarView() != null) {
                                DesktopEnvironment.getDefaultDesktop().getControlBarView().setFrameSelection(null);
                            }
                        }
                    });
                    break;
                    //End comment out here
                case DesktopEnvironment.NON_SWING_VIEW_MODE:
                    //The following code will only be called for applet mode in the case
                    //where a frame is being created from within an applet, i.e. a settings dialog.
                case DesktopEnvironment.APPLET_VIEW_MODE:
                    frameImp = new ViewFrame();
                    ((ViewFrame) frameImp).addWindowListener(new WindowAdapter() {
                        public void windowClosing(WindowEvent e) {
                            dispose();
                        }
                    });
                    break;
                default:
                    throw new RuntimeException("Called ViewFrameBridge:selectFrameImp with bad state.");
            }
        } else {
            if (isAgentView()) {
                if (isPopUpMap()) {
                    frameImp = new ViewJFrame();
                    frameImp.setBounds(0, 0, POPUP_MAP_WIDTH, POPUP_MAP_HEIGHT);
                    ((ViewJFrame) frameImp).setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

                    Timer timer = new Timer(5000, new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            //                            ((ViewJFrame) frameImp).getOwner().toBack();
                            ((ViewJFrame) frameImp).toBack();
                        }
                    });
                    timer.setRepeats(false);
                    timer.start();
                } else {
                    frameImp = new ViewJWindow();
                }
                /*
            // this is another way to get an undecorated window, but so far it doesn't seem to work under X-windows,
            // because the X windows manager (e.g. twm) still decorates it:
                frameImp = new ViewJFrame();
//                JFrame.setDefaultLookAndFeelDecorated(true);
                ((ViewJFrame) frameImp).setUndecorated(true);
                 */
            } else {
                frameImp = new ViewJFrame();

                ((ViewJFrame) frameImp).setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                // Set location so that Agent Inspector and Draw Features windows do not pop up in the very
                // corner at 0, 0, where they are hard to grab on the touch panel screens at VT.
                frameImp.setLocation(30, 30);
            }
            //            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            //            frameImp.setBounds(0, 0, screenSize.width, screenSize.height);
            GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(null);
            frameImp.validate();
            frameImp.setVisible(isAgentView() && !isPopUpMap());
            frameImp.setFocusable(!isAgentView() || isPopUpMap());
            /*
            // use the following if you want to send the agent views to the back
            if (isAgentView()) {
                (new Timer(1000, new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        ((ViewJWindow) frameImp).getOwner().toBack();
                        ((ViewJWindow) frameImp).toBack();
                    }
                })).start();
            }
             */
            //            frameImp.add();
        }
    }

    /**
     * Checks if is pop up map.
     * 
     * @return true, if is pop up map
     */
    private boolean isPopUpMap() {
        // todo: this is a hack
        return views[0].getName().equals("Search Target");
    }

    /**
     * Sets the default close operation.
     * 
     * @param closeOp
     *            the new default close operation
     */
    public void setDefaultCloseOperation(int closeOp) {
        if (SwingRunner.isMultiWinEnvironment()) {
            ((JFrame) frameImp).setDefaultCloseOperation(closeOp);
        } else {
            switch (DesktopEnvironment.getDefaultDesktop().getViewMode()) {
                //Comment out the following lines to support non-swing versions
                case DesktopEnvironment.CLASSIC_VIEW_MODE:
                    ((ViewJFrame) frameImp).setDefaultCloseOperation(closeOp);
                    break;
                case DesktopEnvironment.MDI_VIEW_MODE:
                    ((ViewJInternalFrame) frameImp).setDefaultCloseOperation(closeOp);
                    break;
                    //End comment out here
                case DesktopEnvironment.NON_SWING_VIEW_MODE:
                    //The following code will only be called for applet mode in the case
                    //where a frame is being created from within an applet, i.e. a settings dialog.
                case DesktopEnvironment.APPLET_VIEW_MODE:
                    break;
                default:
                    throw new RuntimeException("Called ViewFrameBridge:selectFrameImp with bad state.");
            }
        }
    }

    /**
     * Layout views.
     */
    private void layoutViews() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().removeAll();

        viewPanel = new ViewPanel();
        scrollPanel = new JScrollPane(viewPanel);
        getContentPane().add(scrollPanel, BorderLayout.CENTER);

        if (isAgentView()) {
            viewToolbar = DesktopEnvironment.createToolbar();
            JPanel statusPanel = new JPanel() {
                /**
                 * 
                 */
                private static final long serialVersionUID = 6659317059108565822L;

                public Dimension getPreferredSize() {
                    return new Dimension(120, super.getPreferredSize().height);
                }
            };
            statusText = new JLabel("    ");
            //        statusText.setFont(new Font(statusText.getFont().getName(), Font.PLAIN, 10));
            statusPanel.setLayout(new BorderLayout());
            statusPanel.add(statusText, BorderLayout.WEST);
            statusPanel.setBorder(DesktopEnvironment.getInfoAreaBorder());
            viewToolbar.add(statusPanel);
            lockUnlockZoomButton = DesktopEnvironment.addToolBarButton(viewToolbar);
            viewToolbar.add(lockUnlockZoomButton);
            setLockZoomToFrame(!SwingRunner.isMultiWinEnvironment());
            if (views.length > 0 && views[0] instanceof AgentSizedView) {
                DesktopEnvironment.addToolBarButton(viewToolbar, new ZoomInAction());
                DesktopEnvironment.addToolBarButton(viewToolbar, new ZoomOutAction());
            }
            //            if (!ModelRoot.isMultiWinEnvironment()) {
            getContentPane().add(viewToolbar, BorderLayout.SOUTH);
            //            }
        } else {
            lockZoomToFrame = true;
            if (getFrameImp() instanceof JInternalFrame) {
                ((JInternalFrame) getFrameImp()).setMaximizable(true);
            }
        }

        if (views.length > 1) {
            if (DesktopEnvironment.getDefaultDesktop().getMultiViewMode() == DesktopEnvironment.GRID_MULTIVIEW_MODE
                    || DesktopEnvironment.getDefaultDesktop().getMultiViewMode() == DesktopEnvironment.GRID_MULTIVIEW_LABEL_MODE)
            {
                //Pick smallest n x m grid that will contain all views, biasing toward wider vs. taller.
                int xRows = Math.max((int) Math.floor(Math.sqrt(views.length)), 1);
                int xCols = (int) Math.ceil((double) views.length / (double) xRows);
                viewPanel.setLayout(new GridLayout(xRows, xCols, gridBorderSize, gridBorderSize));
                if (DesktopEnvironment.getDefaultDesktop().getMultiViewMode() == DesktopEnvironment.GRID_MULTIVIEW_MODE) {
                    for (int i = 0; i < views.length; i++) {
                        viewPanel.add((Component) views[i]);
                    }
                } else {
                    for (int i = 0; i < views.length; i++) {
                        JPanel subPanel = new JPanel();
                        subPanel.setLayout(new BorderLayout());
                        JLabel label = new JLabel(views[i].getName()) {
                            /**
                             * 
                             */
                            private static final long serialVersionUID = -7034603443217265799L;

                            public Dimension getPreferredSize() {
                                return new Dimension(0, labelSize);
                            }
                        };
                        label.setFont(label.getFont().deriveFont(7.0f));
                        subPanel.add(label, BorderLayout.NORTH);
                        subPanel.add((Component) views[i], BorderLayout.CENTER);
                        viewPanel.add(subPanel);
                    }
                }
            } else if (DesktopEnvironment.getDefaultDesktop().getMultiViewMode() == DesktopEnvironment.TABBED_MULTIVIEW_MODE) {
                JTabbedPane tabPane = new JTabbedPane();
                viewPanel.setLayout(new BorderLayout());
                viewPanel.add(tabPane, BorderLayout.CENTER);
                for (int i = 0; i < views.length; i++) {
                    if (i < 9) {
                        //Users don't like 0-based numbering!
                        tabPane.addTab(views[i].getName(), DesktopEnvironment.getIcon("Circle_" + Integer.toString(i + 1)), (Component) views[i], "Select View " + Integer.toString(i + 1));
                    } else {
                        tabPane.addTab(views[i].getName(), DesktopEnvironment.getIcon("Circle"), (Component) views[i], "Select View " + Integer.toString(i + 1));
                    }
                }
            } else {
                throw new RuntimeException("Internal Error in ViewFrameBridge MultiView.");
            }
        } else {
            viewPanel.setLayout(new BorderLayout(0, 0));
            viewPanel.add((Component) views[0], BorderLayout.CENTER);
        }
    }

    /**
     * Sets the view to display in this window. Sets the size to the view
     * preferred size plus the window insets size. Sets the component's view
     * frame to this. Gives the window a random loation on the user's (primary?)
     * monitor.
     * 
     * @param views
     *            the views
     */
    public void setViews(ComponentView[] views) {
        this.views = views;
        if (views.length > 0) {
            selectFrameImp();
            layoutViews();
            for (int i = 0; i < views.length; i++) {
                final ComponentView view = views[i];
                view.setViewFrame(this);
                view.build();
                ((Component) view).setFocusable(true);
                ((Component) view).addMouseMotionListener(new MouseMotionAdapter() {
                    public void mouseMoved(MouseEvent e) {
                        try {
                            if (e.getComponent() instanceof AgentView) {
                                Agent agent = ((AgentView) e.getComponent()).getAgentAtPixel(e.getX(), e.getY());
                                if (agent != null) {
                                    String statusLine = agent.toString();
                                    if (agent instanceof Cell && ((Cell) agent).getOccupant() != null) {
                                        statusLine = ((Cell) agent).getOccupant() + "  [" + statusLine + "]";
                                    }
                                    statusText.setText(" " + statusLine);
                                } else {
                                    statusText.setText("    ");
                                }
                            }
                        } catch (ArrayIndexOutOfBoundsException e1) {
                            statusText.setText("    ");
                        }
                    }

                    public void mouseDragged(MouseEvent e) {
                        if (SwingUtilities.isLeftMouseButton(e) && !e.isAltDown() && !e.isShiftDown() &&
                                !e.isControlDown() && !isLockZoomToFrame()) {
                            final Point viewPosition = scrollPanel.getViewport().getViewPosition();
                            final Dimension viewPortSize = scrollPanel.getViewport().getSize();
                            final Dimension viewPanelSize = viewPanel.getSize();
                            final Point mousePoint = e.getPoint();
                            Point newViewPosition = new Point(
                                                              Math.min(Math.max(viewPosition.x + startMouseDrag.x - mousePoint.x, 0),
                                                                       viewPanelSize.width - viewPortSize.width),
                                                                       Math.min(Math.max(viewPosition.y + startMouseDrag.y - mousePoint.y, 0),
                                                                                viewPanelSize.height - viewPortSize.height));
                            scrollPanel.getViewport().setViewPosition(newViewPosition);
                        }
                    }
                });
                ((Component) view).addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            if (!e.isAltDown() && !e.isShiftDown() && !e.isControlDown() &&
                                    !isLockZoomToFrame()) {
                                mouseDown = true;
                                startMouseDrag = new Point(e.getPoint());
                            }
                            updateCursor(e);
                        }
                    }

                    public void mouseReleased(MouseEvent e) {
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            mouseDown = false;
                            updateCursor(e);
                        }
                    }

                    public void mouseEntered(MouseEvent e) {
                        //((Component) view).requestFocus();
                    }

                    public void mouseExited(MouseEvent e) {
                        if (statusText != null) {
                            statusText.setText("    ");
                        }
                    }
                });
                ((Component) view).addKeyListener(new KeyAdapter() {
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ALT) {
                            updateCursor(e);
                        }
                        super.keyPressed(e);
                    }

                    public void keyReleased(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ALT) {
                            updateCursor(e);
                        }
                        super.keyReleased(e);
                    }
                });
            }
            String frameName = "";
            for (int i = 0; i < views.length; i++) {
                frameName += views[i].getName();
                if (i < views.length - 1) {
                    frameName += "/";
                }
            }
            setTitle(frameName);

            if (DesktopEnvironment.getDefaultDesktop() == null
                    || DesktopEnvironment.getDefaultDesktop().getMultiViewMode() != DesktopEnvironment.APPLET_VIEW_MODE
                    || views[0] instanceof Customizer) {
                //End comment out for web
                if (frameImp.getSize().width * frameImp.getSize().height <= 0) {
                    pack();
                } else {
                    frameImp.validate();
                }
            }
        }
        iconUpdated();
    }

    /**
     * Update cursor.
     * 
     * @param e
     *            the e
     */
    private void updateCursor(InputEvent e) {
        if (e.isAltDown()) {
            viewPanel.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        } else {
            if (mouseDown) {
                viewPanel.setCursor(new Cursor(Cursor.MOVE_CURSOR));
            } else {
                viewPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        }
    }

    /**
     * This view has just been deserailized. Called by the framework.
     */
    public void viewDeserialized() {
        if (getFrameImp() == null) {
            setViews(views);
            DesktopEnvironment.getDefaultDesktop().registerViewFrame(this);
            try {
                ((JInternalFrame) this.getFrameImp()).setIcon(iconified);
            } catch (PropertyVetoException e) {
                //ignore...
            }
            this.getFrameImp().setBounds(bounds);
        }
    }

    /**
     * Should be called when the view has updated itself in a way that changes
     * icon.
     */
    public void iconUpdated() {
        if (frameImp instanceof ViewJInternalFrame) {
            //Just use icon for first view, assuming for now that most views in same frame will share same type
            if (views[0].getIcon() != null) {
                ((ViewJInternalFrame) frameImp).setFrameIcon(views[0].getIcon());
            } else {
                ((ViewJInternalFrame) frameImp).setFrameIcon(DesktopEnvironment.getIcon("Ascape16"));
            }
        }
    }

    /**
     * Brings the frame to the front.
     */
    public void toFront() {
        if (frameImp != null) {
            if (frameImp instanceof ViewFrame) {
                ((ViewFrame) frameImp).toFront();
            }
            //Comment out for web
            else if (frameImp instanceof ViewJFrame) {
                ((ViewJFrame) frameImp).toFront();
            } else if (frameImp instanceof ViewJInternalFrame) {
                ((ViewJInternalFrame) frameImp).toFront();
                ((ViewJInternalFrame) frameImp).grabFocus();
                if (DesktopEnvironment.getDefaultDesktop().getUserFrame() != null) {
                    //A little violation of proper encapsulation here...
                    DesktopEnvironment.getDefaultDesktop().getUserFrame().calculateVisibility();
                }
            } else if (frameImp instanceof ViewJWindow) {
                ((ViewJWindow) frameImp).toFront();
            }
            //Comment out for web
        }
    }

    /**
     * Called whenever the bridged frames are disposed.
     */
    protected void onFrameDispose() {
        if (views.length > 0 && views[0] instanceof BaseCustomizer && frameImp != null) {
            ((BaseCustomizer) views[0]).setLastBounds(frameImp.getBounds());
        }
        frameImp = null;
        for (int i = 0; i < views.length; i++) {
            if (removeListenerOnDispose && views[i].getScape() != null && 
            		views[i].getScape().getScapeListeners().contains(views[i])) {
                views[i].getScape().removeScapeListener(views[i]);
            }
            if (DesktopEnvironment.getDefaultDesktop() != null) {
                DesktopEnvironment.getDefaultDesktop().removeView(views[i]);
            }
        }
        if (frameImp instanceof ViewJFrame) {
            ((ViewJFrame) frameImp).setIconImage(null);
        } else if (frameImp instanceof ViewJInternalFrame) {
            ((ViewJInternalFrame) frameImp).setFrameIcon(null);
        }
    }

    /**
     * Packs the delegate frame.
     */
    public void pack() {
        if (frameImp != null) {
            if (frameImp instanceof ViewFrame) {
                ((ViewFrame) frameImp).pack();
            }
            //Comment out for web
            else if (frameImp instanceof ViewJFrame) {
                ((ViewJFrame) frameImp).pack();
            }
            else if (frameImp instanceof ViewJInternalFrame) {
                ((ViewJInternalFrame) frameImp).pack();
            } else if (frameImp instanceof ViewJWindow) {
                ((ViewJWindow) frameImp).pack();
            }
            //Comment out for web
        }
    }

    /**
     * Closes and frees the bridges frames, saving any relevant information.
     */
    public void dispose() {
        if (frameImp != null) {
            if (frameImp instanceof ViewFrame) {
                ((ViewFrame) frameImp).dispose();
            }
            //Comment out for web
            else if (frameImp instanceof ViewJWindow) {
                ((ViewJWindow) frameImp).dispose();
            } else if (frameImp instanceof ViewJFrame) {
                ((ViewJFrame) frameImp).dispose();
            } else if (frameImp instanceof ViewJInternalFrame) {
                ((ViewJInternalFrame) frameImp).dispose();
            } else {
                throw new RuntimeException("Unkown frame type.");
            }
            //Comment out for web
        }
    }

    /**
     * Returns a string represenetation of the view frame.
     * 
     * @return the string
     */
    public String toString() {
        return getTitle();
    }

    /**
     * Write object.
     * 
     * @param out
     *            the out
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        if (SwingUtilities.isEventDispatchThread()) {
            if (frameImp != null) {
                if (frameImp instanceof JFrame) {
                    ((ViewJFrame) frameImp).setIconImage(null);
                } else if (frameImp instanceof JInternalFrame) {
                    ((ViewJInternalFrame) frameImp).setFrameIcon(null);
                } else {
                    throw new RuntimeException("Internal Error: Unsupported frame type for externalizing: " + frameImp.getClass());
                }
                bounds = this.getFrameImp().getBounds();
                iconified = ((JInternalFrame) this.getFrameImp()).isIcon();
            }
            out.defaultWriteObject();
        } else {
            System.out.println(this + ": cannot write because not in Event Dispatch Thread!");
        }
    }

    /**
     * Gets the grid border size.
     * 
     * @return the grid border size
     */
    public int getGridBorderSize() {
        return gridBorderSize;
    }

    /**
     * Sets the grid border size.
     * 
     * @param gridBorderSize
     *            the new grid border size
     */
    public void setGridBorderSize(int gridBorderSize) {
        this.gridBorderSize = gridBorderSize;
        if (viewPanel.getLayout() instanceof GridLayout) {
            ((GridLayout) viewPanel.getLayout()).setHgap(gridBorderSize);
            ((GridLayout) viewPanel.getLayout()).setVgap(gridBorderSize);
            pack();
        }
    }

    /**
     * Sets the view to display in this window. Sets the size to the view
     * preferred size plus the window insets size. Sets the component's view
     * frame to this. Gives the window a random loation on the user's (primary?)
     * monitor.
     * 
     * @param view
     *            the view
     */
    public void setView(ComponentView view) {
        ComponentView[] views = new ComponentView[1];
        views[0] = view;
        setViews(views);
    }

    /**
     * Returns the views displayed in this window.
     * 
     * @return the views
     */
    public ComponentView[] getViews() {
        return views;
    }

    /**
     * Gets the view panel.
     * 
     * @return the view panel
     */
    public JPanel getViewPanel() {
        return viewPanel;
    }

    /**
     * Returns the actual frame implementation. Use sparingly, and only if you
     * know the view context you will be using!
     * 
     * @return the frame imp
     */
    public Container getFrameImp() {
        return frameImp;
    }

    /**
     * Returns the contentPane object for the frame. (In the case of Frame, will
     * return the Frame itself.)
     * 
     * @return the content pane
     */
    public Container getContentPane() {
        if (DesktopEnvironment.getDefaultDesktop() == null
                || DesktopEnvironment.getDefaultDesktop().getViewMode() != DesktopEnvironment.APPLET_VIEW_MODE
                || views[0] instanceof Customizer)
        {
            if (frameImp != null) {
                //todo--si this really neccessary!!
                if (frameImp instanceof ViewFrame) {
                    return ((ViewFrame) frameImp).getContentPane();
                }
                //Comment out for web
                else if (frameImp instanceof ViewJFrame) {
                    return ((ViewJFrame) frameImp).getContentPane();
                } else if (frameImp instanceof ViewJInternalFrame) {
                    return ((ViewJInternalFrame) frameImp).getContentPane();
                } else if (frameImp instanceof ViewJWindow) {
                    return ((ViewJWindow) frameImp).getContentPane();
                }
                //End comment out for web
                else {
                    throw new RuntimeException("Internal Error.");
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Gets the title of the frame. The title is displayed in the frame's
     * border.
     * 
     * @return the title
     */
    public String getTitle() {
        if (frameImp != null) {
            if (frameImp instanceof ViewFrame) {
                return ((ViewFrame) frameImp).getTitle();
            }
            else if (frameImp instanceof ViewJFrame) {
                return ((ViewJFrame) frameImp).getTitle();
            } else if (frameImp instanceof ViewJInternalFrame) {
                return ((ViewJInternalFrame) frameImp).getTitle();
            }
        }
        return "None";
    }


	/**
	 * Is this the currently selected internal frame, or the frame with the
	 * focus?
	 * 
	 * @return true if this a {@link JInternalFrame} that is selected,
	 * or a {@link Frame} or {@link JFrame} that has the focus.
	 */
	public boolean isSelected() {
		if (frameImp != null) {
			if (frameImp instanceof ViewFrame) {
				return ((ViewFrame) frameImp).isFocusOwner();
			} else if (frameImp instanceof ViewJFrame) {
				return ((ViewJFrame) frameImp).isFocusOwner();
			} else if (frameImp instanceof ViewJInternalFrame) {
				return ((ViewJInternalFrame) frameImp).isSelected();
			}
		}
		return false;
	}

    /**
     * Sets the title for this frame to the specified string.
     * 
     * @param title
     *            the title to be displayed in the frame's border
     */
    public void setTitle(String title) {
        if (frameImp != null) {
            if (frameImp instanceof ViewFrame) {
                ((ViewFrame) frameImp).setTitle(title);
            }
            //Comment out for web
            else if (frameImp instanceof ViewJFrame) {
                ((ViewJFrame) frameImp).setTitle(title);
            } else if (frameImp instanceof ViewJInternalFrame) {
                ((ViewJInternalFrame) frameImp).setTitle(title);
            }
            //Comment out for web
        }
    }

    /**
     * Makes the frame visible or invisible.
     * 
     * @param b
     *            true if frame should be made visible, false if frame should be
     *            hidden
     */
    public void setVisible(boolean b) {
        if (frameImp != null) {
            frameImp.setVisible(b);
        }
    }

    /**
     * Is the frame currently visible?.
     * 
     * @return true, if is visible
     */
    public boolean isVisible() {
        if (frameImp != null) {
            return frameImp.isVisible();
        } else {
            return false;
        }
    }

    /**
     * Checks if is agent view.
     * 
     * @return true, if is agent view
     */
    public boolean isAgentView() {
        return views.length > 0 && views[0] instanceof AgentView;
    }

    /**
     * Gets the view toolbar.
     * 
     * @return the view toolbar
     */
    public JToolBar getViewToolbar() {
        return viewToolbar;
    }

    /**
     * Checks if is lock zoom to frame.
     * 
     * @return true, if is lock zoom to frame
     */
    public boolean isLockZoomToFrame() {
        return lockZoomToFrame;
    }

    /**
     * Sets the lock zoom to frame.
     * 
     * @param lockZoomToFrame
     *            the new lock zoom to frame
     */
    public void setLockZoomToFrame(boolean lockZoomToFrame) {
        this.lockZoomToFrame = lockZoomToFrame;
        if (lockZoomToFrame) {
            if (viewPanel.getComponentCount() > 0) {
                Dimension frameSize = getFrameImp().getSize();
                frameSize = new Dimension(frameSize.width + (scrollPanel.getVerticalScrollBar().isVisible() ? scrollPanel.getVerticalScrollBar().getWidth() + 1 : 0), frameSize.height + (scrollPanel.getHorizontalScrollBar().isVisible() ? scrollPanel.getHorizontalScrollBar().getHeight() + 1 : 0));
                Dimension newSize = ((ViewPanZoomFrame) getFrameImp()).getPreferredSizeWithin(frameSize);
                newSize = new Dimension(newSize.width - (scrollPanel.getVerticalScrollBar().isVisible() ? scrollPanel.getVerticalScrollBar().getWidth() : 0), newSize.height - (scrollPanel.getHorizontalScrollBar().isVisible() ? scrollPanel.getHorizontalScrollBar().getHeight() : 0));
                getFrameImp().setSize(newSize);
            }
            lockUnlockZoomButton.setAction(new ZoomUnLockAction());
        } else {
            lockUnlockZoomButton.setAction(new ZoomLockAction());
        }
    }

    /**
     * Gets the zoom in action.
     * 
     * @return the zoom in action
     */
    public Action getZoomInAction() {
        return new ZoomInAction();
    }

    /**
     * Gets the zoom out action.
     * 
     * @return the zoom out action
     */
    public Action getZoomOutAction() {
        return new ZoomOutAction();
    }

    /**
     * Gets the scroll panel.
     * 
     * @return the scroll panel
     */
    public JScrollPane getScrollPanel() {
        return scrollPanel;
    }
}
