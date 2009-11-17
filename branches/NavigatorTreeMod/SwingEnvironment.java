/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */
package org.ascape.runtime.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.ascape.model.Scape;
import org.ascape.model.event.ScapeListener;
import org.ascape.runtime.AbstractUIEnvironment;
import org.ascape.runtime.swing.navigator.DefaultTreeBuilder;
import org.ascape.runtime.swing.navigator.TreeBuilder;
import org.ascape.view.vis.ComponentView;
import org.ascape.view.vis.control.ControlBarView;

/**
 * User: milesparker Date: May 26, 2006 Time: 7:29:23 PM To change this template use File | Settings | File Templates.
 */
public abstract class SwingEnvironment extends AbstractUIEnvironment {

    /**
     * 
     */
    private static final long serialVersionUID = -8950328979037923978L;

    /**
     * The Constant DEFAULT_BORDER.
     */
    private final static Border DEFAULT_BORDER = new EmptyBorder(4, 4, 3, 3);

    /**
     * The Constant ROLLOVER_BORDER.
     */
    private final static Border ROLLOVER_BORDER = new CompoundBorder(new LineBorder(Color.darkGray), new EmptyBorder(3,
                                                                                                                     3, 2, 2));

    /**
     * The Constant BUTTON_ROLLOVER_COLOR.
     */
    protected final static Color BUTTON_ROLLOVER_COLOR = new Color(180, 180, 200);

    /**
     * The Constant ACCENT_COLOR.
     */
    protected final static Color ACCENT_COLOR = new Color(125, 100, 200);
    /**
     * The control bar view.
     */
    protected transient ControlBarView controlBarView;

    /**
     * The root pane.
     */
    transient Container rootPane;

    /**
     * The model name field.
     */
    protected static JTextField modelNameField;

    /**
     * The model chooser menu.
     */
    private static JMenu modelChooserMenu;

    /**
     * The list of open frames.
     */
    transient Vector frames;

    /**
     * A sparse map for objects currently being edited by one or more editors.
     */
    private Map<Object, PropertyChangeSupport> propertySupportForObject;

    public static SwingEnvironment DEFAULT_ENVIRONMENT;

    private TreeBuilder navigatorTreeBuilder = new DefaultTreeBuilder(); 

    public TreeBuilder getNavigatorTreeBuilder() {
		return navigatorTreeBuilder;
	}

	public void setNavigatorTreeBuilder(TreeBuilder navigatorTreeBuilder) {
		this.navigatorTreeBuilder = navigatorTreeBuilder;
	}
    
    /**
     * Instantiates a new UI environment.
     */
    protected SwingEnvironment() {
        frames = new Vector();
        propertySupportForObject = new HashMap<Object, PropertyChangeSupport>();
    }

    /**
     * Gets the controlBarView for the ModelRoot object.
     * 
     * @return the controlBarView
     */
    public ControlBarView getControlBarView() {
        return controlBarView;
    }

    /**
     * Sets controlBarView for the ModelRoot object.
     * 
     * @param controlBarView
     *        the controlBarView
     */
    public void setControlBarView(ControlBarView controlBarView) {
        this.controlBarView = controlBarView;
    }

    /**
     * Gets the root pane.
     * 
     * @return the root pane
     */
    public Container getRootPane() {
        return rootPane;
    }

    /**
     * Sets the root pane.
     * 
     * @param rootPane
     *        the new root pane
     */
    public void setRootPane(Container rootPane) {
        this.rootPane = rootPane;
    }

    /**
     * Displays a standard splash screen message asserting copyright and other information. <strong>Warning: This splash
     * screen notice must be displayed anytime the Ascape uiEnvironment is initialized within a graphics uiEnvironment;
     * that is, anytime a user might see it. It is displayed anytime a model opens by default, but will not be displayed
     * if Scape.setDisplayGraphics() is set to false. This should normally only be done when the uiEnvironment is being
     * run in a headless mode, for example in a batch mode on a distributed system. In rare circumstances, such as when
     * opening multiple models or repeatedly instantiating new models, it might be desirable to disable the splash
     * screen at each model open. In this case, it is the model developer's responsiblity to call this method when first
     * instantiating the uiEnvironment. It is of course also a violation of copyright to attempt to circumvent the
     * methods that we have put in place to properly assert copyright. For example, this code should not be
     * modified.</strong>
     */
    public final static void showSplashScreenNotice() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new SplashScreen(4000L);
            }
        });
    }

    /**
     * Displays a standard splash screen message asserting copyright and other information. See warnings above.
     * 
     * @param scape
     *        the scape
     * @param e
     *        the e
     */
    public void showErrorDialog(Scape scape, RuntimeException e) {
        if (runtimeMode == RELEASE_RUNTIME_MODE) {
            String msg = "Sorry, an exception occured: " + e.toString();
            String title = "Runtime Exception";
            // Comment out for non-web
            // System.out.println(e);
            // Comment out for web
            if (scape != null) {
                String[] options = new String[3];
                options[0] = "Debug";
                options[1] = "Restart";
                options[2] = "Quit";
                int choice = JOptionPane.showOptionDialog(null, msg, title, JOptionPane.YES_NO_CANCEL_OPTION,
                                                          JOptionPane.ERROR_MESSAGE, null, options, "Quit");
                switch (choice) {
                    case 0:
                        throw e;
                    case 1:
                        String modelName = scape.getClass().getName();
                        scape.getRunner().close();
                        getScape().getRunner().openInstance(modelName);
                        break;
                    case 2:
                        scape.getRunner().quitFinally();
                        break;
                    default:
                        throw new RuntimeException("Bad return from JOptionPane: " + choice);
                }
            } else {
                String[] options = new String[2];
                options[0] = "Debug";
                options[1] = "Quit";
                int choice = JOptionPane.showOptionDialog(null, msg, title, JOptionPane.YES_NO_OPTION,
                                                          JOptionPane.ERROR_MESSAGE, null, options, "Quit");
                switch (choice) {
                    case 0:
                        throw e;
                    case 1:
                        System.exit(0);
                        break;
                    default:
                        throw new RuntimeException("Bad return from JOptionPane: " + choice);
                }
            }
            // End comment out for web
        } else {
            throw e;
        }
    }

    /**
     * Parses the file list.
     * 
     * @param t
     *        the t
     * @return the j menu
     */
    private static JMenu parseFileList(StringTokenizer t) {
        JMenu menu = ComboMenuBar.createMenu(t.nextToken());
        while (t.hasMoreElements()) {
            String next = t.nextToken();
            if (next.equals("{")) {
                JMenu nextLevelMenu = parseFileList(t);
                menu.add(nextLevelMenu);
            } else if (next.equals("}")) {
                return menu;
            } else if (next.equals("\n") || next.equals("\r")) {
                // ignore
            } else {
                String modelName = next;
                // Ignore "tabs"
                // modelName = (new StringBuffer(modelName)).
                if (t.nextToken().equals("|")) {
                    String modelClassName = t.nextToken();
                    JMenuItem item = new JMenuItem();
                    item.setAction(new DesktopEnvironment.ModelPickAction(modelName, modelClassName));
                    menu.add(item);
                } else {
                    throw new Error("Error in parsing demo model setup, \",\" expected.");
                }
            }
        }
        return menu;
    }

    /**
     * Validate model name.
     * 
     * @param modelName
     *        the model name
     * @return true, if successful
     */
    private static boolean validateModelName(String modelName) {
        if (!modelName.equals("")) {
            Class c = null;
            // Allow skipping of specification for nutech and brookings models
            try {
                c = Class.forName(modelName);
            } catch (ClassNotFoundException e) {
                try {
                    c = Class.forName("org." + modelName);
                    modelName = "org." + modelName;
                } catch (ClassNotFoundException e2) {
                    try {
                        c = Class.forName("edu.brook." + modelName);
                        modelName = "edu.brook." + modelName;
                    } catch (ClassNotFoundException e3) {
                        String msg = "The model \"" + modelName + "\"could not be found.\n";
                        msg += "-Check that you have completely specified the model name. \n";
                        msg += " (\"org.traffic.TrafficModel\" or \"traffic.TrafficModel\", but not \"Norms.\")\n";
                        msg += "-Check that your class path includes the specified model.";
                        JOptionPane.showMessageDialog(null, msg, "Error: Model Not Found", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            if (c != null) {
                if (Scape.class.isAssignableFrom(c)) {
                    return true;
                } else {
                    String msg = "The class \"" + modelName + "\" is not a model; it does not ";
                    msg += "subclass org.ascape.Scape.";
                    JOptionPane.showMessageDialog(null, msg, "Error: Not Model", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            // User entered blank..
            String msg = "You must enter a model name.";
            JOptionPane.showMessageDialog(null, msg, "Error: No Model Name", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    /**
     * Load chooser models menu.
     */
    private static void loadChooserModelsMenu() {
        BufferedReader demoReader = new BufferedReader(new InputStreamReader(Scape.class
                                                                             .getResourceAsStream("ModelChoices.txt")));
        // We build a string because Java's StremTokeniser sucks
        String readString = "";
        try {
            String nextString = demoReader.readLine();
            while (nextString != null) {
                // Strip off spaces
                readString += nextString.trim() + "\n";
                nextString = demoReader.readLine();
            }
            demoReader.close();
        } catch (IOException e) {
            throw new RuntimeException("IO Exception: " + e);
        }
        StringTokenizer st = new StringTokenizer(readString, ":{}|\r\n", true);
        modelChooserMenu = parseFileList(st);
    }

    /**
     * Gets the users models name from a dialog and validates that it is a legitimate class name. Returns the validated
     * model name, or null if the user cancelled. This feature only works with Swing..will generate exception if called
     * in other contexts.
     * 
     * @return the string
     */
    public String openDialog() {
        String modelName = "Temp";
        // While user doesn't press cancel
        while (!(modelName == null)) {

            final JPanel specifyModelPanel = new JPanel() {
                /**
                 * 
                 */
                private static final long serialVersionUID = 2882328601287498142L;

                public Dimension getPreferredSize() {
                    return new Dimension(240, super.getPreferredSize().height);
                }
            };

            specifyModelPanel.setLayout(new GridLayout(4, 1));

            specifyModelPanel.add(new JLabel("Enter Model Name:"));
            modelNameField = new JTextField();
            specifyModelPanel.add(modelNameField);

            specifyModelPanel.add(new JLabel("Or Choose Model:"));

            loadChooserModelsMenu();
            ComboMenuBar comboMenu = new ComboMenuBar(modelChooserMenu);

            specifyModelPanel.add(comboMenu);

            String[] options = new String[2];
            options[0] = "Cancel";
            options[1] = "Open";
            int opt = JOptionPane.showOptionDialog(rootPane, specifyModelPanel, "Open Model",
                                                   JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                                                   options, "Open");
            if (opt == 1) {
                modelName = modelNameField.getText();
                if (validateModelName(modelName)) {
                    return modelName;
                }
            } else {
                // We want to end at this point..Next
                modelName = null;
            }
        }
        return modelName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.explorer.RuntimeEnvironment#addViews(org.ascape.model.event.ScapeListener[], boolean)
     */
    public void addViews(ScapeListener[] views, boolean createFrame) {
        super.addViews(views, createFrame);
        ComponentView[] componentViews = new ComponentView[views.length];
        System.arraycopy(views, 0, componentViews, 0, componentViews.length);
        if (createFrame) {
            for (int i = 0; i < views.length; i++) {
                // Comment out for web
                if (!(views[i] instanceof ComponentView)) {
                    return;
                }
            }
            createFrame(componentViews);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.explorer.RuntimeEnvironment#addView(org.ascape.model.event.ScapeListener, boolean)
     */
    public void addView(ScapeListener view, boolean createFrame) {
        super.addView(view, createFrame);
        if (createFrame) {
            if (view instanceof ComponentView) {
                createFrame((ComponentView) view);
                ((ComponentView) view).build();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ascape.explorer.RuntimeEnvironment#removeView(org.ascape.model.event.ScapeListener)
     */
    public void removeView(ScapeListener view) {
        super.removeView(view);
        if (view.isLifeOfScape()) {
            if (view instanceof ComponentView) {
                if (frames.contains(((ComponentView) view).getViewFrame())) {
                    frames.removeElement(((ComponentView) view).getViewFrame());
                    controlBarView.buildFrameList();
                }
            }
        }
        if (view instanceof ComponentView && ((ComponentView) view).getViewFrame() != null
                && ((ComponentView) view).getViewFrame().isVisible()) {
            // Would be circular, except that view frame will be disposed..
            ((ComponentView) view).getViewFrame().dispose();
        }
    }

    /**
     * Gets the info area border.
     * 
     * @return the info area border
     */
    public static Border getInfoAreaBorder() {
        return BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1), BorderFactory
                                                  .createLineBorder(Color.GRAY));
    }

    /**
     * A helper method to remove a button from the provided toolbar for the specified action. Keep in mind that there
     * are a number of tool bars, so be sure to specify the correct one.
     * 
     * @param toolbar
     *        the toolbar
     * @param action
     *        the action
     */
    public static void removeToolBarButton(final JToolBar toolbar, final Action action) {
        JButton toRemove = null;
        for (int i = 0; i < toolbar.getComponents().length; i++) {
            Component component = toolbar.getComponents()[i];
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                if (button.getAction().equals(action)) {
                    toRemove = button;
                    break;
                }
            }
        }
        if (toRemove != null) {
            toolbar.remove(toRemove);
        }
    }

    /**
     * A helper method to add a button to the provided toolbar for the specified action.
     * 
     * @param toolBar
     *        the tool bar
     * @param action
     *        the action
     * @return the j button
     */
    public static JButton addToolBarButton(final JToolBar toolBar, final Action action) {
        final JButton actionButton = new JButton() {
            /**
             * 
             */
            private static final long serialVersionUID = 6986329468889324677L;

            public void setAction(Action action) {
                // We don't want to have names on toolbar buttons
                action.putValue(Action.NAME, "");
                super.setAction(action);
            }
        };
        actionButton.setBorder(DEFAULT_BORDER);
        actionButton.setRolloverEnabled(true);
        actionButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                actionButton.setBorder(ROLLOVER_BORDER);
                // todo get the real highlight color from user settings
                actionButton.setBackground(BUTTON_ROLLOVER_COLOR);
                super.mouseEntered(e);
            }

            public void mouseExited(MouseEvent e) {
                actionButton.setBorder(DEFAULT_BORDER);
                actionButton.setBackground(toolBar.getBackground());
                super.mouseExited(e);
            }
        });
        // actionButton.setModel
        if (action != null) {
            actionButton.setAction(action);
        }
        toolBar.add(actionButton);
        return actionButton;
    }

    /**
     * A helper method to add a button to the provided toolbar for the specified action.
     * 
     * @param toolBar
     *        the tool bar
     * @return the j button
     */
    public static JButton addToolBarButton(JToolBar toolBar) {
        return addToolBarButton(toolBar, null);
    }

    /**
     * Sets the up label.
     * 
     * @param label
     *        the new up label
     */
    protected static void setupLabel(JLabel label) {
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2),
                                                           getInfoAreaBorder()));
    }

    /**
     * Creates the label.
     * 
     * @return the j label
     */
    protected static JLabel createLabel() {
        JLabel label = new JLabel();
        setupLabel(label);
        return label;
    }

    /**
     * Creates the label.
     * 
     * @param width
     *        the width
     * @return the j label
     */
    public static JLabel createLabel(final int width) {
        JLabel label = new JLabel() {
            /**
             * 
             */
            private static final long serialVersionUID = -3556511509121905878L;

            public Dimension getPreferredSize() {
                return new Dimension(width, super.getPreferredSize().height);
            }
        };
        setupLabel(label);
        return label;
    }

    /**
     * Creates the frame.
     * 
     * @param view
     *        the view
     */
    public void createFrame(ComponentView view) {
        ComponentView[] views = new ComponentView[1];
        views[0] = view;
        createFrame(views);
    }

    /**
     * Returns PropertyChangeSupport for the object provided. This property change support should then be used to manage
     * all <i>UI edited</i> property changes. All customizers, inspectors and renderers should use this object to ensure
     * that they are properly updated when a change to an object occurs and that they properly notify property change
     * support when they cause a change to occur. Note that for efficiency reasons, computed (e.g. through a running
     * model) changes are _not_ reported using this mechanism, and setters should <i>not</i> fire their own property
     * change events. Renderers should instead register as ScapeListeners to ensure that they are properly notified of
     * changes in model state. Thus, the supporting map is assumed to be relativly sparse, and should only contain those
     * objects that have at one point been activly edited or rendered through an inspector, customizer or other UI tool.
     * 
     * @param object
     *        an object of relevance to user
     * @return a manger for proerty changes.
     */
    public PropertyChangeSupport getPropertySupportForObject(Object object) {
        PropertyChangeSupport pcs = propertySupportForObject.get(object);
        if (pcs == null) {
            pcs = new PropertyChangeSupport(object);
            propertySupportForObject.put(object, pcs);
        }
        return pcs;
    }

    /**
     * Creates the frame.
     * 
     * @param views
     *        the views
     */
    public abstract void createFrame(ComponentView[] views);

    /**
     * Returns the actual frame implementation. Use sparingly, and only if you know the view context you will be using!
     * 
     * @return the all frames
     */
    public Vector getAllFrames() {
        return frames;
    }
}
