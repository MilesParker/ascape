/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.view.vis.control;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.ascape.runtime.swing.DesktopEnvironment;

/**
 * A class providing a menu bar for controlling a model. Provides start, stop,
 * restart, pause, resume, quit, info, status, and new charts. Control views can
 * be attached to any scape, and controls will typically affect the model
 * (entire collection of scapes) as a whole.s Use SimpleControlView if Swing is
 * not available or if simple buttons are preferred to image buttons with
 * tool-tips. Requires Swing.
 * 
 * @author Miles Parker
 * @version 3.0
 * @since 3.0
 */
public class MenuBarView extends ControlActionView {

    /**
     * The menu bar.
     */
    private JMenuBar menuBar;

    /**
     * The full screen item.
     */
    private JCheckBoxMenuItem fullScreenItem;

    /**
     * The toolbar item.
     */
    private JCheckBoxMenuItem toolbarItem;

    /**
     * The show tool bar action.
     */
    private Action showToolBarAction;

    /**
     * The hide tool bar action.
     */
    private Action hideToolBarAction;

    /**
     * The file menu.
     */
    protected JMenu fileMenu;

    /**
     * The control menu.
     */
    protected JMenu controlMenu;

    /**
     * Constructs the control view, creating and laying out its components.
     */
    public MenuBarView() {
        this("Menu Bar View");
    }

    /**
     * Constructs the control view, creating and laying out its components.
     * 
     * @param name
     *            the name
     */
    public MenuBarView(String name) {
        super(name);

        fullWindowAction = new AbstractAction() {
            /**
             * 
             */
            private static final long serialVersionUID = 1556930508272488966L;

            public void actionPerformed(ActionEvent e) {
                DesktopEnvironment.getDefaultDesktop().setFullScreen(true);
            }
        };
        fullWindowAction.putValue(Action.NAME, "Full Screen");
        fullWindowAction.putValue(Action.SHORT_DESCRIPTION, "Display Ascape in Full Screen");

        normalWindowAction = new AbstractAction() {
            /**
             * 
             */
            private static final long serialVersionUID = 609757419862169763L;

            public void actionPerformed(ActionEvent e) {
                DesktopEnvironment.getDefaultDesktop().setFullScreen(false);
            }
        };
        normalWindowAction.putValue(Action.NAME, "Full Screen");
        normalWindowAction.putValue(Action.SHORT_DESCRIPTION, "Display Ascape in Normal Application Window");

        showToolBarAction = new AbstractAction() {
            /**
             * 
             */
            private static final long serialVersionUID = -6431361141666126911L;

            public void actionPerformed(ActionEvent e) {
                DesktopEnvironment.getDefaultDesktop().getUserFrame().setShowToolBar(true);
                toolbarItem.setAction(hideToolBarAction);
                toolbarItem.setState(true);
            }
        };
        showToolBarAction.putValue(Action.NAME, "Toolbar");
        showToolBarAction.putValue(Action.SHORT_DESCRIPTION, "Show Toolbar");
        hideToolBarAction = new AbstractAction() {
            /**
             * 
             */
            private static final long serialVersionUID = -4739257836212809903L;

            public void actionPerformed(ActionEvent e) {
                DesktopEnvironment.getDefaultDesktop().getUserFrame().setShowToolBar(false);
                toolbarItem.setAction(showToolBarAction);
                toolbarItem.setState(false);
            }
        };
        hideToolBarAction.putValue(Action.NAME, "Toolbar");
        hideToolBarAction.putValue(Action.SHORT_DESCRIPTION, "Hide Toolbar");

        //        showNavigatorAction = new AbstractAction() {
        //            public void actionPerformed(ActionEvent e) {
        //                UserEnvironment.getDefaultEnvironment().getUserFrame().setShowNavigator(true);
        //                navigatorItem.setAction(hideNavigatorAction);
        //                navigatorItem.setState(true);
        //            }
        //        };
        //        showNavigatorAction.putValue(Action.NAME, "Navigator");
        //        showNavigatorAction.putValue(Action.SHORT_DESCRIPTION, "Show Navigator");
        //        hideNavigatorAction = new AbstractAction() {
        //            public void actionPerformed(ActionEvent e) {
        //                UserEnvironment.getDefaultEnvironment().getUserFrame().setShowNavigator(false);
        //                navigatorItem.setAction(showNavigatorAction);
        //                navigatorItem.setState(false);
        //            }
        //        };
        //        hideNavigatorAction.putValue(Action.NAME, "Navigator");
        //        hideNavigatorAction.putValue(Action.SHORT_DESCRIPTION, "Hide Navigator");
        //
        //        showConsoleAction = new AbstractAction() {
        //            public void actionPerformed(ActionEvent e) {
        //                UserEnvironment.getDefaultEnvironment().getUserFrame().setRedirectConsole(true);
        //                consoleItem.setAction(hideConsoleAction);
        //                consoleItem.setState(true);
        //            }
        //        };
        //        showConsoleAction.putValue(Action.NAME, "Console");
        //        showConsoleAction.putValue(Action.SHORT_DESCRIPTION, "Show Console");
        //
        //        hideConsoleAction = new AbstractAction() {
        //            public void actionPerformed(ActionEvent e) {
        //                UserEnvironment.getDefaultEnvironment().getUserFrame().setRedirectConsole(false);
        //                consoleItem.setAction(showConsoleAction);
        //                consoleItem.setState(false);
        //            }
        //        };
        //        hideConsoleAction.putValue(Action.NAME, "Console");
        //        hideConsoleAction.putValue(Action.SHORT_DESCRIPTION, "Hide Console");

        setLayout(new FlowLayout());

        menuBar = new JMenuBar();
        menuBar.setFont(new Font(menuBar.getFont().getFontName(), menuBar.getFont().getStyle(), 12));

        fileMenu = new JMenu("File");
        addMenuBarAction(fileMenu, getReopenAction());
        addMenuBarAction(fileMenu, getOpenAction());
        addMenuBarAction(fileMenu, getOpenSavedAction());
        addMenuBarAction(fileMenu, getSaveAction());
        //        addMenuBarAction(fileMenu, getCloseAction());
        fileMenu.addSeparator();
        addMenuBarAction(fileMenu, getQuitAction());
        menuBar.add(fileMenu);

        JMenu viewMenu = new JMenu("View");
        fullScreenItem = new JCheckBoxMenuItem();
        viewMenu.add(fullScreenItem);
        toolbarItem = new JCheckBoxMenuItem();
        viewMenu.add(toolbarItem);
        //        navigatorItem = new JCheckBoxMenuItem();
        //        viewMenu.add(navigatorItem);
        //        consoleItem = new JCheckBoxMenuItem();
        //        viewMenu.add(consoleItem);
        viewMenu.addSeparator();
        addMenuBarAction(viewMenu, getAddTSAction());
        addMenuBarAction(viewMenu, getAddHistAction());
        addMenuBarAction(viewMenu, getAddPieAction());
        menuBar.add(viewMenu);
        toolbarItem.setAction(hideToolBarAction);
        toolbarItem.setState(true);
        //        navigatorItem.setAction(hideNavigatorAction);
        //        navigatorItem.setState(true);
        //        consoleItem.setAction(hideConsoleAction);
        //        consoleItem.setState(true);
        changeInFullScreen();

        controlMenu = new JMenu("Control");
        addMenuBarAction(controlMenu, getStartRestartAction());
        //        addMenuBarAction(controlMenu, getRestartAction());
        //        addMenuBarAction(controlMenu, getStopAction());
        controlMenu.addSeparator();
        addMenuBarAction(controlMenu, getPauseResumeAction());
        //        addMenuBarAction(controlMenu, getPauseAction());
        //        addMenuBarAction(controlMenu, getResumeAction());
        addMenuBarAction(controlMenu, getStepAction());
        menuBar.add(controlMenu);

        JMenu optionsMenu = new JMenu("Options");
        addMenuBarAction(optionsMenu, getSettingsAction());
        addMenuBarAction(optionsMenu, getSearchAction());
        addMenuBarAction(optionsMenu, getInfoAction());
        //        optionsMenu.addSeparator();
        //        addMenuBarAction(optionsMenu, getRecordStartAction());
        //        addMenuBarAction(optionsMenu, getRecordStopAction());
        menuBar.add(optionsMenu);
        //Force since we won't be adding anywhere.
        environmentNowNoScape();
    }

    /**
     * Gets the menu bar.
     * 
     * @return the menu bar
     */
    public JMenuBar getMenuBar() {
        return menuBar;
    }

    /* (non-Javadoc)
     * @see org.ascape.explorer.ControlActionView#changeInFullScreen()
     */
    public void changeInFullScreen() {
        super.changeInFullScreen();
        if (DesktopEnvironment.getDefaultDesktop().isFullScreen()) {
            fullScreenItem.setAction(getNormalWindowAction());
            fullScreenItem.setIcon(null);
            fullScreenItem.setState(true);
        } else {
            fullScreenItem.setAction(getFullWindowAction());
            fullScreenItem.setIcon(null);
            fullScreenItem.setState(false);
        }
    }

    /**
     * The Constant EMPTY_7_ICON.
     */
    public final static Icon EMPTY_7_ICON = new Icon() {
        public int getIconHeight() {
            return 7;
        }

        public int getIconWidth() {
            return 7;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
        }
    };

    /**
     * A helper method to add a button to the provided toolbar for the specified
     * action.
     * 
     * @param menu
     *            the menu
     * @param action
     *            the action
     */
    protected static void addMenuBarAction(JMenu menu, Action action) {
        JMenuItem item = new JMenuItem(action);
        //Icons in menus are annoying...
        //We set a blank one so that checkboxes won't look weird.
        item.setIcon(EMPTY_7_ICON);
        menu.add(item);
    }

    /**
     * Gets the control menu.
     * 
     * @return the control menu
     */
    public JMenu getControlMenu() {
        return controlMenu;
    }

    /**
     * Gets the file menu.
     * 
     * @return the file menu
     */
    public JMenu getFileMenu() {
        return fileMenu;
    }

}
