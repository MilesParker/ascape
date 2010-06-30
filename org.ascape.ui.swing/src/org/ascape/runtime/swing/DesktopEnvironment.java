/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.runtime.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Random;
import java.util.TooManyListenersException;
import java.util.Vector;
import java.util.zip.GZIPOutputStream;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.event.ScapeListener;
import org.ascape.runtime.Runner;
import org.ascape.util.Utility;
import org.ascape.util.vis.ImageRegistry;
import org.ascape.view.custom.AutoCustomizer;
import org.ascape.view.custom.AutoCustomizerSwing;
import org.ascape.view.vis.ComponentView;
import org.ascape.view.vis.PersistentComponentView;
import org.ascape.view.vis.SimpleControlView;
import org.ascape.view.vis.control.ControlActionView;
import org.ascape.view.vis.control.ControlBarView;

/**
 * Supports all user environment aspects of a running ascape app in a vm,
 * including user frame, etc.
 * 
 * @author Miles Parker
 * @version 3.0
 * @history first in 6/21/02
 * @since 3.0
 */
public class DesktopEnvironment extends SwingEnvironment {

    /**
     * 
     */
    private static final long serialVersionUID = 6486119781336287766L;

    {ImageRegistry.register(new SwingImageProvider());}

    /**
     * Symbol indicating the manager is supporting an applet view.
     */
    public static final int APPLET_VIEW_MODE = -1;

    /**
     * Symbol indicating the manager is supporting a swing external frames view,
     * using javax.swing.JFrame;
     */
    public static final int CLASSIC_VIEW_MODE = -2;

    /**
     * Symbol indicating the manager is supporting a non-swing external frames
     * view, using java.awt.Frame;
     */
    public static final int NON_SWING_VIEW_MODE = -3;

    /**
     * Symbol indicating the manager is supporting a swing internal frames view,
     * using javax.swing.JIntenralFrame; [Internal frames are not quite ready
     * for prime time.]
     */
    public static final int MDI_VIEW_MODE = -4;

    /**
     * The mode the manager is currently supporting, standard by default.
     */
    private int viewMode = MDI_VIEW_MODE;

    /**
     * Symbol indicating that views added should be put into a tabbed layout.
     */
    public static final int TABBED_MULTIVIEW_MODE = -1;

    /**
     * Symbol indicating that views added should be put into a grid layout.
     */
    public static final int GRID_MULTIVIEW_MODE = 1;

    /**
     * Symbol indicating that views added should be put into a grid layout, with
     * a name appearing above each panel..
     */
    public static final int GRID_MULTIVIEW_LABEL_MODE = 2;

    /**
     * Multiview mode. By default GRID_MULTIVIEW_MODE.
     */
    private int multiViewMode = GRID_MULTIVIEW_MODE;


    /**
     * The extra space used in the screen.
     */
    //    Dimension screenBuffer = new Dimension(10, 60);
    private Dimension screenBuffer = new Dimension(60, 60);

    /**
     * The selection manager.
     */
    AgentSelectionManager selectionManager;

    //TODO..integrate multiwin w/ this schema?
    /**
     * Instantiates a new user environment.
     */
    public DesktopEnvironment() {
        ToolTipManager.sharedInstance().setInitialDelay(300);
        SwingEnvironment.DEFAULT_ENVIRONMENT = this;
        setControlBarView(new ControlBarView());
        addView(getControlBarView(), false);
        switch (viewMode) {
            //Comment out for web
            case MDI_VIEW_MODE:
                setCustomizer(new AutoCustomizerSwing());
                setFullScreen(false);
                copyrightNotice();
                //getUserFrame().show();
                getUserFrame().setTitle("Ascape");
            case CLASSIC_VIEW_MODE:
                if (SwingRunner.isDisplayGraphics()) {
                    showSplashScreenNotice();
                } else {
                    System.out.println("                 ***NOTICE***");
                    System.out.println();
                    System.out.println("Automatic splash screen display is disabled.");
                    System.out.println("For copyright reasons, the splash screen MUST be");
                    System.out.println("displayed whenever a graphical user interface is.");
                    System.out.println("See ViewFrameBridge.showSplashScreenNotice() for details.");
                    System.out.println();
                    System.out.println();
                }
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        setCustomizer(new AutoCustomizerSwing());
                    }
                });
                break;
                //End comment out for web
            case NON_SWING_VIEW_MODE:
                copyrightNotice();
                setCustomizer(new AutoCustomizer());
                addView(new SimpleControlView(true, true));
                break;
            default:
                throw new RuntimeException("Tried to setup a ViewFrameBridge with bad view mode specified:" + viewMode);
        }
    }

    private void copyrightNotice() {
        if (Runner.isDisplayGraphics()) {
            showSplashScreenNotice();
        } else {
            System.out.println("                 ***NOTICE***");
            System.out.println();
            System.out.println("Automatic splash screen display is disabled.");
            System.out.println("For copyright reasons, the splash screen MUST be");
            System.out.println("displayed whenever a graphical user interface is.");
            System.out.println("See ViewFrameBridge.showSplashScreenNotice() for details.");
            System.out.println();
            System.out.println();
        }
    }

    public void environmentQuiting(ScapeEvent scapeEvent) {
        super.environmentQuiting(scapeEvent);
        
        
        UserFrame frame = getUserFrame();
        
        // save frame positions
        frame.savePrefs();
        
        frame.dispose();
    }

	/**
	 * When a scape is added close any already open frames that aren't persistent and set frame title.
	 * 
	 * @param scapeEvent
	 *            the scape event
	 * @throws TooManyListenersException
	 *             the too many listeners exception
	 */
	public void scapeAdded(ScapeEvent scapeEvent)
			throws TooManyListenersException {
		super.scapeAdded(scapeEvent);
		closeOpenFrames(false);
		getUserFrame().setTitle("Ascape - " + getScape().getName());
	}

		
	/**
	 * Close any open frames.
	 * 
	 * @param closePersistent
	 *            close persistent frames? If {@code false} and a frame contains
	 *            a {@link PersistentComponentView}, it will not be closed.
	 */
	public void closeOpenFrames(boolean closePersistent) {

		// make a copy of frames because
		// during dispose the frames vector
		// will be concurrently modified
		// producing an error
		Vector<ViewFrameBridge> framesCopy = new Vector<ViewFrameBridge>();

		for (ViewFrameBridge vfb : frames) {
			framesCopy.add(vfb);
		}

		for (ViewFrameBridge vfb : framesCopy) {
			boolean persistent = false;
			for (ComponentView view : vfb.getViews()) {
				if (!closePersistent
						&& view instanceof PersistentComponentView) {
					persistent = true;
					break;
				}
			}

			if (!persistent) {
				// dispose the frame. this will remove it from
				// RuntimeEnvironment.environmentViews and from
				// SwingEnvironment.frames
				vfb.dispose();
			}
		}
	}
	
    /**
     * When a scape is removed reset frame title.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void scapeRemoved(ScapeEvent scapeEvent) {
        super.scapeRemoved(scapeEvent);
        if (getUserFrame() != null) {
            getUserFrame().setTitle("Ascape");
        }
    }

    /**
     * Register view frame.
     * 
     * @param v
     *            the v
     */
    protected void registerViewFrame(ViewFrameBridge v) {
        if (!(v.getViews()[0] instanceof ControlBarView)) {
            frames.addElement(v);
            //todo, this wasn't working well, so did a quick fix by just rebuilding frame list, prob. need a better long-term solution
            controlBarView.buildFrameList();
            //controlBarView.getFrameList().contentsChanged(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, frames.size() - 1, frames.size()));
        }
        v.setVisible(true);
    }

    /* (non-Javadoc)
     * @see org.ascape.explorer.UIEnvironment#createFrame(org.ascape.view.vis.ComponentView[])
     */
    public void createFrame(ComponentView[] views) {
        final ViewFrameBridge frame;
        if (views.length > 0) {
            frame = new ViewFrameBridge(views);
            if (frame.getFrameImp().getLocation().getX() + frame.getFrameImp().getLocation().getY() == 0) {
                placeRandomLocation(frame.getFrameImp());
            }
            registerViewFrame(frame);
        }
        if (getControlBarView() != null && getControlBarView().getViewFrame() != null) {
            getControlBarView().getViewFrame().toFront();
        }
    }

    /**
     * Places the window in a random location.
     * 
     * @param frameImp
     *            the frame imp
     */
    public void placeRandomLocation(Container frameImp) {
        if (frameImp != null) {
            if (viewMode == CLASSIC_VIEW_MODE) {
                frameImp.setLocation(Utility.randomInRange(new Random(), 0, Toolkit.getDefaultToolkit().getScreenSize().width - frameImp.getSize().width - screenBuffer.width),
                                     Utility.randomInRange(new Random(), 0, Toolkit.getDefaultToolkit().getScreenSize().height - frameImp.getSize().height - screenBuffer.height));
            } else if (viewMode == MDI_VIEW_MODE) {
                int maxX = Math.max((int) getUserFrame().getDeskScrollPane().getViewport().getSize().getWidth() - frameImp.getSize().width - screenBuffer.width, 1);
                int maxY = Math.max((int) getUserFrame().getDeskScrollPane().getViewport().getSize().getHeight() - frameImp.getSize().height - screenBuffer.height, 1);
                frameImp.setLocation(Utility.randomInRange(new Random(), 0, maxX), Utility.randomInRange(new Random(), 0, maxY));
            }
        }
    }


    /**
     * The Class ModelPickAction.
     */
    static class ModelPickAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = -5460430400163046083L;

        /**
         * The model name.
         */
        String modelName;

        /**
         * The model class name.
         */
        String modelClassName;

        /**
         * Instantiates a new model pick action.
         * 
         * @param modelName
         *            the model name
         * @param modelClassName
         *            the model class name
         */
        public ModelPickAction(String modelName, String modelClassName) {
            super(modelName, null);
            this.modelName = modelName;
            this.modelClassName = modelClassName;
        }

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            modelNameField.setText(modelClassName);
        }
    }

    /**
     * The Constant aboutIcon.
     */
    private final static Icon aboutIcon = new ImageIcon(getImage("AscapeLogo.gif"));

    /**
     * The about frame.
     */
    private static JInternalFrame aboutFrame = null;


    /**
     * Displays a standard about dialog.
     * 
     * @param scape
     *            the scape
     */
    public static void displayAboutDialog(Scape scape) {
        //Comment out for web
        if (aboutFrame == null || !aboutFrame.isVisible()) {
            String title = "About " + scape.getRoot().getName();
            String desc = "<html><head></head><title>" + title + "</title><body>";
            desc += scape.getRoot().getHTMLDescription();
            desc += "<BR><BR>";
            desc += Scape.copyrightAndCredits;
            desc += "\n</body>\n</html>";
            JEditorPane msgArea = new JEditorPane("text/html", desc);
            msgArea.setEditable(false);
            aboutFrame = new JInternalFrame(title);
            getDefaultDesktop().getUserFrame().getDesk()
            .add(aboutFrame, JLayeredPane.PALETTE_LAYER);
            aboutFrame.setClosable(true);
            aboutFrame.setIconifiable(true);
            aboutFrame.setMaximizable(true);
            aboutFrame.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
            aboutFrame.setBackground(Color.white);
            aboutFrame.setFrameIcon(DesktopEnvironment.getIcon("Inform"));
            JPanel p = new JPanel();
            GridBagLayout pgbl = new GridBagLayout();
            p.setLayout(pgbl);
            GridBagConstraints pgbc = pgbl.getConstraints(p);
            pgbc.gridx = 0;
            pgbc.gridy = 0;
            pgbc.anchor = GridBagConstraints.WEST;
            pgbc.gridwidth = 1;
            pgbc.weightx = 0.0;
            pgbc.weighty = 0.0;
            pgbc.fill = GridBagConstraints.NONE;
            p.setBackground(Color.white);
            p.add(new JLabel(aboutIcon), pgbc);
            pgbc.weightx = 1.0;
            pgbc.weighty = 1.0;
            pgbc.gridy++;
            pgbc.fill = GridBagConstraints.BOTH;
            JScrollPane sp = new JScrollPane(msgArea);
            Border b = new EmptyBorder(4, 4, 4, 4);
            sp.setBackground(Color.white);
            sp.setBorder(b);
            p.add(sp, pgbc);
            //todo; break out code to center into standard functionality
            aboutFrame.getContentPane().add(p);
            int aboutWidth = 600;
            int aboutHeight = 400;
            aboutFrame
            .setBounds((int) (getDefaultDesktop().getUserFrame().getDesk().getSize().getWidth() - aboutWidth) / 2,
                       (int) (getDefaultDesktop().getUserFrame().getDesk().getSize()
                               .getHeight() - aboutHeight) / 2, aboutWidth, aboutHeight);
            aboutFrame.show();
            //End comment out for web
        } else {
            try {
                aboutFrame.setIcon(false);
            } catch (PropertyVetoException e) {
            }
        }
    }

    public static DesktopEnvironment getDefaultDesktop() {
        return SwingEnvironment.DEFAULT_ENVIRONMENT instanceof DesktopEnvironment
        ? (DesktopEnvironment) SwingEnvironment.DEFAULT_ENVIRONMENT : null;
    }

    /**
     * Check for license agreement.
     */
    public static void checkForLicenseAgreement() {
        File licenseFile = new File("licenseAgreement.lic");
        if (licenseFile.exists()) {
            return;
        } else {
            final JPanel licenseAgreementPanel = new JPanel() {
                /**
                 * 
                 */
                private static final long serialVersionUID = 6149078982117986788L;

                public Dimension getPreferredSize() {
                    return new Dimension(700, 600);
                }
            };
            licenseAgreementPanel.setLayout(new BorderLayout());

            BufferedReader demoReader = new BufferedReader(new InputStreamReader(Scape.class.getResourceAsStream("LicenseAgreement.html")));
            String readString = "";
            try {
                String nextString = demoReader.readLine();
                while (nextString != null) {
                    //Strip off spaces
                    readString += nextString.trim() + "\n";
                    nextString = demoReader.readLine();
                }
                demoReader.close();
            } catch (IOException e) {
                throw new RuntimeException("IO Exception: " + e);
            }
            JEditorPane agreementTextPane = new JEditorPane("text/html", readString);
            agreementTextPane.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(agreementTextPane);
            Border b = new EmptyBorder(4, 4, 4, 4);
            scrollPane.setBackground(Color.white);
            scrollPane.setBorder(b);
            //todo; break out code to center into standard functionality
            licenseAgreementPanel.add(scrollPane, BorderLayout.CENTER);

            JPanel labelPanel = new JPanel(new BorderLayout());
            labelPanel.add(new Label("Do you agree with the above license?"), BorderLayout.CENTER);
            licenseAgreementPanel.add(labelPanel, BorderLayout.SOUTH);

            String[] options = new String[2];
            options[0] = "Do Not Agree";
            options[1] = "Agree";
            Container c = null;
            if (SwingEnvironment.DEFAULT_ENVIRONMENT instanceof DesktopEnvironment) {
                c = getDefaultDesktop().getUserFrame();
            }
            int opt = JOptionPane.showOptionDialog(c, licenseAgreementPanel, "License Agreement", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, "Open");
            if (opt == 1) {
                try {
                    saveLicenseAgreementFile(licenseFile);
                } catch (IOException e) {
                    throw new RuntimeException(e.toString());
                }
            } else {
                exit();
            }
        }
    }

    /**
     * Save license agreement file.
     * 
     * @param file
     *            the file
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private static void saveLicenseAgreementFile(File file) throws IOException {
        OutputStream os = new FileOutputStream(file);
        GZIPOutputStream gzos = new GZIPOutputStream(os);
        ObjectOutputStream oos = new ObjectOutputStream(gzos);
        String string = "agreed";
        oos.writeObject(string);
        oos.close();
    }

    /**
     * Are we in an applet vm context? returns true if this scape is viewed in
     * an applet, false otherwise.
     * 
     * @return true, if is in applet
     */
    public boolean isInApplet() {
        return false;
    }

    /**
     * Returns the view viewMode being used.
     * 
     * @return true, if supports swing
     */
    public boolean supportsSwing() {
        return viewMode == CLASSIC_VIEW_MODE || viewMode == MDI_VIEW_MODE;
    }

    /**
     * Returns the view viewMode being used.
     * 
     * @return the view mode
     */
    public int getViewMode() {
        return viewMode;
    }

    /**
     * Sets the viewMode to use.
     * 
     * @param _viewMode
     *            the _view mode
     */
    public void setViewMode(int _viewMode) {
        viewMode = _viewMode;
    }

    /**
     * Returns the multi view mode that will be used when adding any new
     * windows. If TABBED_MULTIVIEW_MODE, views that are added together will be
     * placed in seperate tabbed panels (viewable individually, taking up much
     * less space.) If GRID_MULTIVIEW_MODE, views that are added together will
     * be placed in a girdlayout (viewable all at once, taking up much more
     * space.) Default is grid view for compatibility, but this may be changed.
     * It is better to set the mode when you add multiple views.
     * 
     * @return the multi view mode
     */
    public int getMultiViewMode() {
        return multiViewMode;
    }

    /**
     * Sets the multi view to use when adding a new window. If
     * TABBED_MULTIVIEW_MODE, views that are added together will be placed in
     * seperate tabbed panels (viewable individually, taking up much less
     * space.) If GRID_MULTIVIEW_MODE, views that are added together will be
     * placed in a girdlayout (viewable all at once, taking up much more space.)
     * 
     * @param _multiViewMode
     *            the _multi view mode
     */
    public void setMultiViewMode(int _multiViewMode) {
        multiViewMode = _multiViewMode;
    }

    /**
     * Utility method to return an image resource as specified according to the
     * rules of Class.getResource.
     * 
     * @param string
     *            the resource reference of the image to load
     * @return the image
     */
    public static Image getImage(final String string) {
        URL url = DesktopEnvironment.class.getResource("images/" + string);
        if (url != null) {
            return Toolkit.getDefaultToolkit().getImage(url);
        } else {
            throw new RuntimeException("Image not found in  " + url + ": "+ string);
        }
    }

    /**
     * Utility method to return an image icon. The image icon must be in the
     * [lib]/org.ascape/view/images directory, and it must be a gif, ending
     * \".gif.\" Comment out for web.
     * 
     * @param imageName
     *            the name (without extension) specifiying the image icon to
     *            return
     * @return the icon
     */
    public static ImageIcon getIcon(String imageName) {
        return new ImageIcon(getImage(imageName + ".gif"));
    }


    /**
     * Sets the full screen.
     * 
     * @param fullScreen
     *            the new full screen
     */
    public void setFullScreen(boolean fullScreen) {
        UserFrame oldFrame = null;
        if (getUserFrame() == null) {
            setUserFrame(new UserFrame("", this));
        } else {
            oldFrame = getUserFrame();
            UserFrame newFrame = new UserFrame("", this, oldFrame);
            setUserFrame(newFrame);
        }
        if (fullScreen && (!isFullScreen() || !getUserFrame().isShowing())) {
            if (GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().isFullScreenSupported()) {
                getUserFrame().setUndecorated(true);
                GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(getUserFrame());
            } else {
                JOptionPane.showMessageDialog(getUserFrame(), "Your system does not support full screen windows.");
            }
        } else if (!fullScreen && (isFullScreen() || !getUserFrame().isShowing())) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            getUserFrame().setLocation(0, 0);
            getUserFrame().setSizeFromPrefs(screenSize.width, screenSize.height - 30);
            GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(null);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    getUserFrame().setVisible(true);
                }
            });
        }
        for (int i = 0; i < getEnvironmentViews().size(); i++) {
            ScapeListener scapeListener = (ScapeListener) getEnvironmentViews().get(i);
            if (scapeListener instanceof ControlActionView) {
                ((ControlActionView) scapeListener).changeInFullScreen();
            }
        }
        if (getScape() != null) {
            getUserFrame().setTitle("Ascape - " + getScape().getName());
        } else {
            getUserFrame().setTitle("Ascape");
        }
        if (oldFrame != null) {
            oldFrame.setVisible(false);
            //            for (Iterator iterator = environmentViews.iterator(); iterator.hasNext();) {
            //                ComponentView componentView = (ComponentView) iterator.next();
            //                componentView.forceScapeNotify();
            //            }
            //oldFrame.getgetUserFrame()l().getParent().removeAll();
        }
    }

    /**
     * Checks if is full screen.
     * 
     * @return true, if is full screen
     */
    public boolean isFullScreen() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getFullScreenWindow() == getUserFrame();
    }

    /**
     * Creates the toolbar.
     * 
     * @return the j tool bar
     */
    public static JToolBar createToolbar() {
        JToolBar toolBar = new JToolBar() {
            /**
             * 
             */
            private static final long serialVersionUID = -8540667211494555623L;

            protected JButton createActionComponent(Action a) {
                String text = (String) a.getValue(Action.NAME);
                Icon icon = (Icon) a.getValue(Action.SMALL_ICON);

                JButton b = new JButton(text, icon) {
                    /**
                     * 
                     */
                    private static final long serialVersionUID = 8206012016638039497L;

                    // I override JButton's createActionPropertyChangeListener
                    // to avoid the creation of a AbstractButton$ButtonActionPropertyChangeListener,
                    // which is not serializable!
                    protected PropertyChangeListener createActionPropertyChangeListener(Action a) {
                        return null;
                    }
                };
                if (icon != null) {
                    b.putClientProperty("hideActionText", Boolean.TRUE);
                }
                b.setHorizontalTextPosition(JButton.CENTER);
                b.setVerticalTextPosition(JButton.BOTTOM);
                b.setEnabled(a.isEnabled());
                b.setToolTipText((String) a.getValue(Action.SHORT_DESCRIPTION));
                return b;
            }
        };
        toolBar.setMargin(new Insets(0, 0, 0, 0));
        return toolBar;
    }

    /**
     * Gets the selection manager.
     * 
     * @return the selection manager
     */
    public AgentSelectionManager getSelectionManager() {
        return selectionManager;
    }

    /**
     * Gets the user frame.
     * 
     * @return the user frame
     */
    public UserFrame getUserFrame() {
        return (UserFrame) getRootPane();
    }

    /**
     * Sets the user frame.
     * 
     * @param userFrame
     *            the new user frame
     */
    public void setUserFrame(UserFrame userFrame) {
        setRootPane(userFrame);
    }

    //
    //    public void setShowToolbar(boolean show) {
    //        if (show)
    //    }
}

class ComboMenu extends JMenu {

    //ArrowIcon iconRenderer;

    /**
     * 
     */
    private static final long serialVersionUID = -5636927286300121887L;

    public ComboMenu(String label) {
        super(label);
        //iconRenderer = new ArrowIcon(SwingConstants.SOUTH, true);
        setBorder(new EtchedBorder());
        //setIcon(new BlankIcon(null, 11));
        setHorizontalTextPosition(JButton.LEFT);
        setFocusPainted(true);
    }

    /*public void paintComponent(Graphics g) {
    super.paintComponent(g);
    Dimension d = this.getPreferredSize();
    int x = Math.max(0, d.width - iconRenderer.getIconWidth() -3);
    int y = Math.max(0, (d.height - iconRenderer.getIconHeight())/2 -2);
    iconRenderer.paintIcon(this,g, x,y);
    }*/
}
