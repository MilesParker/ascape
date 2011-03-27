/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.runtime.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import org.ascape.runtime.swing.navigator.Navigator;
import org.ascape.runtime.swing.navigator.RightClickPopupDesktopMenu;
import org.ascape.view.vis.Overhead2DView;
import org.ascape.view.vis.control.MenuBarView;

/**
 * A desktop (MDI) style environment for model exploration.
 */
public class UserFrame extends JFrame {

	private static final int TREE_DIVIDER_LOCATION_DEFAULT = 180;

	private static final int CONSOLE_DIVIDER_LOCATION_DEFAULT = 740;

	/**
     * 
     */
	private static final long serialVersionUID = 5621172820611162226L;

	/**
	 * Preferences that save GUI components positions.
	 */
	private Preferences prefs =
		Preferences.userNodeForPackage(this.getClass());
	
	/**
	 * The desk.
	 */
	private JDesktopPane desk;

	/**
	 * The desk scroll.
	 */
	private JScrollPane deskScroll;

	/**
	 * The tabbed console pane at the bottom of the screen.
	 */
	private JTabbedPane consolePane;

	/**
	 * The text area the is the console log.
	 */
	private ConsoleOutput console;

	/**
	 * The split pane between the Navigator tree and the desktop.
	 */
	private JSplitPane deskAndTree;
	
	/**
	 * The split pane between the console section and the rest of the screen.
	 */
	private JSplitPane deskTreeConsole;
	
	/**
	 * The navigator.
	 */
	private Navigator navigator;

	/**
	 * The environment.
	 */
	private DesktopEnvironment environment;

	/**
	 * The root panel.
	 */
	private JPanel rootPanel;

	/**
	 * The menu view.
	 */
	private MenuBarView menuView;

	/**
	 * The toolbar panel.
	 */
	private JPanel toolbarPanel;

	/**
	 * The inner panel.
	 */
	private JPanel innerPanel;

	/**
	 * The place icons on left side.
	 */
	private boolean placeIconsOnLeftSide = true;

	/**
	 * The minimized.
	 */
	private boolean minimized;

	/**
	 * The show tool bar.
	 */
	private boolean showToolBar = true;

	/**
	 * The update non visible views.
	 */
	private boolean updateNonVisibleViews = false;

	/**
	 * The Class ThreadCheckingRepaintManager.
	 */
	public class ThreadCheckingRepaintManager extends RepaintManager {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.RepaintManager#addInvalidComponent(javax.swing.JComponent
		 * )
		 */
		public synchronized void addInvalidComponent(JComponent jComponent) {
			checkThread();
			super.addInvalidComponent(jComponent);
		}

		/**
		 * Check thread.
		 */
		private void checkThread() {
			if (!SwingUtilities.isEventDispatchThread()) {
				// throw new RuntimeException("Wrong Thread!!");
				if (menuView != null && menuView.getScape() != null
						&& menuView.getScape().isRunning()) {
					System.err.println("Wrong Thread");
					Thread.dumpStack();
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.RepaintManager#addDirtyRegion(javax.swing.JComponent,
		 * int, int, int, int)
		 */
		public synchronized void addDirtyRegion(JComponent jComponent, int i,
				int i1, int i2, int i3) {
			checkThread();
			super.addDirtyRegion(jComponent, i, i1, i2, i3);
		}
	}

	/**
	 * Instantiates a new user frame.
	 * 
	 * @param title
	 *            the title
	 * @param environment
	 *            the environment
	 * @param oldFrame
	 *            the old frame
	 */
	public UserFrame(String title, final DesktopEnvironment environment,
			UserFrame oldFrame) {
		super(title);
		this.environment = environment;
		if (oldFrame != null) {
			// essentially a shallow clone based on old frame
			this.rootPanel = oldFrame.rootPanel;
			this.menuView = oldFrame.menuView;
			this.desk = oldFrame.desk;
			this.deskScroll = oldFrame.deskScroll;
			this.deskTreeConsole = oldFrame.deskTreeConsole;
			this.deskAndTree = oldFrame.deskAndTree;
			this.consolePane = oldFrame.consolePane;
			this.showToolBar = oldFrame.showToolBar;
			this.toolbarPanel = oldFrame.toolbarPanel;
			this.innerPanel = oldFrame.innerPanel;
		}
		setIconImage(DesktopEnvironment.getImage("Ascape16.gif"));
		getContentPane().setLayout(new BorderLayout());

		if (oldFrame == null) {
			buildDesk();
			buildMenuView();
		}
		setJMenuBar(this.menuView.getMenuBar());

		if (oldFrame == null) {
			buildRootPanel(environment);
		}
		getContentPane().add(this.rootPanel, BorderLayout.CENTER);

		addWindowListener(new WindowAdapter() {
			public void windowIconified(WindowEvent e) {
				super.windowIconified(e);
				minimized = true;
				calculateVisibility();
			}

			public void windowDeiconified(WindowEvent e) {
				super.windowDeiconified(e);
				minimized = false;
				calculateVisibility();
			}

			public void windowClosing(WindowEvent e) {
				// tell window not to close
				// (environment.quit() will do this for us if we can quit)
				setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

				// execute quit on new thread instead of AWT
				new Thread("Quit") {
					public void run() {
						environment.quit();
					}
				}.start();
			}
		});
		
		loadPrefs();
	}

	private static final String TREE_DIVIDER_LOCATION = "tree divider location"; 
	private static final String TREE_LAST_DIVIDER_LOCATION = "tree last divider location"; 
	private static final String CONSOLE_DIVIDER_LOCATION = "console divider location"; 
	private static final String CONSOLE_LAST_DIVIDER_LOCATION = "console last divider location"; 
	private static final String USERFRAME_WIDTH = "application width"; 
	private static final String USERFRAME_HEIGHT = "application height"; 

	/**
	 * Set the size of the {@link UserFrame} to the smaller of the screen dimensions
	 * and the dimensions saved in the preferences between runs. 
	 * 
	 * @param screenWidth screen width
	 * @param screenHeight screen height
	 */
	public void setSizeFromPrefs(int screenWidth, int screenHeight) {
		int width = Integer.parseInt(prefs.get(USERFRAME_WIDTH, String.valueOf(screenWidth)));
		int height = Integer.parseInt(prefs.get(USERFRAME_HEIGHT, String.valueOf(screenHeight)));

		setSize(Math.min(width, screenWidth) , Math.min(height, screenHeight));
	}

	/**
	 * Load preferences including: console divider location, tree divider location.
	 */
	public void loadPrefs() {
		int consoleDividerLocation = Integer.parseInt(prefs.get(CONSOLE_DIVIDER_LOCATION, String.valueOf(CONSOLE_DIVIDER_LOCATION_DEFAULT)));
		int consoleDividerLastLocation = Integer.parseInt(prefs.get(CONSOLE_LAST_DIVIDER_LOCATION, "-1"));
		
		deskTreeConsole.setDividerLocation(consoleDividerLocation);
		deskTreeConsole.setLastDividerLocation(consoleDividerLastLocation);
		
		int treeDividerLocation = Integer.parseInt(prefs.get(TREE_DIVIDER_LOCATION, String.valueOf(TREE_DIVIDER_LOCATION_DEFAULT)));
		int treeDividerLastLocation = Integer.parseInt(prefs.get(TREE_LAST_DIVIDER_LOCATION, "-1"));

		deskAndTree.setDividerLocation(treeDividerLocation);
		deskAndTree.setLastDividerLocation(treeDividerLastLocation);
		
	}

	/**
	 * Save preferences including: console divider location, tree divider location, userframe width and height.
	 */
	public void savePrefs() {
		prefs.put(CONSOLE_DIVIDER_LOCATION, String.valueOf(deskTreeConsole.getDividerLocation()));
		prefs.put(CONSOLE_LAST_DIVIDER_LOCATION, "" + String.valueOf(deskTreeConsole.getLastDividerLocation()));

		prefs.put(TREE_DIVIDER_LOCATION, String.valueOf(deskAndTree.getDividerLocation()));
		prefs.put(TREE_LAST_DIVIDER_LOCATION, "" + String.valueOf(deskAndTree.getLastDividerLocation()));
		
		prefs.put(USERFRAME_WIDTH, String.valueOf(getWidth()));
		prefs.put(USERFRAME_HEIGHT, String.valueOf(getHeight()));
	}
	
	/**
	 * Instantiates a new user frame.
	 * 
	 * @param title
	 *            the title
	 * @param environment
	 *            the environment
	 */
	public UserFrame(String title, final DesktopEnvironment environment) {
		this(title, environment, null);
	}

	/**
	 * Builds the desk.
	 */
	private void buildDesk() {
		desk = new JDesktopPane();
		desk.setBackground(Color.LIGHT_GRAY);
		desk.setDesktopManager(new UserDesktopManager());
		desk.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				calculateVisibility();
			}
		});
		deskScroll = new JScrollPane(desk);
		deskScroll.getViewport().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				calculateVisibility();
			}
		});
		deskScroll.setViewportView(desk);

		deskScroll.addMouseListener(new RightClickPopupDesktopMenu(
				environment));
	}

	/**
	 * Builds the menu view.
	 */
	private void buildMenuView() {
		menuView = new MenuBarView();
		SwingEnvironment.DEFAULT_ENVIRONMENT.addView(menuView, false);
	}

	/**
	 * Builds the root panel.
	 * 
	 * @param environment
	 *            the environment
	 */
	private void buildRootPanel(final DesktopEnvironment environment) {

		rootPanel = new JPanel();
		rootPanel.setLayout(new BorderLayout());
		innerPanel = new JPanel();
		innerPanel.setLayout(new BorderLayout());
		rootPanel.add(innerPanel, BorderLayout.CENTER);

		toolbarPanel = new JPanel();
		toolbarPanel.setLayout(new BorderLayout());
		environment.getControlBarView().build();
		innerPanel.add(toolbarPanel, BorderLayout.NORTH);
		handleControlBarShow();

		JComponent mainDesk;

		if (DesktopEnvironment.isShowNavigator()) {
			navigator = new Navigator();
			JPanel navTitle = new JPanel() {
				/**
                 * 
                 */
				private static final long serialVersionUID =
						6048424073628257167L;

				public void paint(Graphics g) {
					super.paint(g);
					g.setColor(Color.white);
					g.drawString("Navigator", 10, 10);
				}
			};
			navTitle.setBackground(DesktopEnvironment.ACCENT_COLOR);
			navTitle.setBorder(BorderFactory.createRaisedBevelBorder());
			SwingEnvironment.DEFAULT_ENVIRONMENT.addView(navigator, false);

			JScrollPane navScroll = new JScrollPane(navigator);

			JPanel navPanel = new JPanel(new BorderLayout());
			navPanel.add(navScroll, BorderLayout.CENTER);
			navPanel.add(navTitle, BorderLayout.NORTH);

			deskAndTree =
					new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
							navPanel, deskScroll);
			deskAndTree.setDividerSize(8);
			deskAndTree.setUI(new BasicSplitPaneUI());
			deskAndTree.setOneTouchExpandable(true);
			deskAndTree.setResizeWeight(1.0);
			deskAndTree.setDividerLocation(TREE_DIVIDER_LOCATION_DEFAULT);

			mainDesk = deskAndTree;
		} else {
			mainDesk = deskScroll;
		}

		if (DesktopEnvironment.isRedirectConsole()) {
			final JScrollPane logScroll = new JScrollPane() {
				/**
                 * 
                 */
				private static final long serialVersionUID =
						5505727044708554260L;

				public Dimension getPreferredSize() {
					return new Dimension(super.getPreferredSize().width, 120);
				}
			};

			console = new ConsoleOutput() {
				/**
                 * 
                 */
				private static final long serialVersionUID =
						-2375015593304873880L;

				public void append(String text) {
					super.append(text);
					JScrollBar b = logScroll.getVerticalScrollBar();
					b.setValue(b.getMaximum());
				}
			};

			logScroll.setViewportView(console);
			console.setMinimumSize(new Dimension(0, 0));
			JPanel logPanel = new JPanel(new BorderLayout());
			logPanel.add(logScroll, BorderLayout.CENTER);

			// Create a tabbed pane to hold the logPanel (which holds the
			// logScroll which holds the console) on a tab called "Log".
			consolePane = new JTabbedPane();
			consolePane.addTab("Log", logPanel);

			// Create split between console and rest of desktop
			deskTreeConsole =
					new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, mainDesk,
							consolePane);
			deskTreeConsole.setDividerSize(8);
			deskTreeConsole.setUI(new BasicSplitPaneUI());
			deskTreeConsole.setOneTouchExpandable(true);
			deskTreeConsole.setResizeWeight(1.0);
			deskTreeConsole.setDividerLocation(CONSOLE_DIVIDER_LOCATION_DEFAULT);
			innerPanel.add(deskTreeConsole, BorderLayout.CENTER);
			
		} else {
			innerPanel.add(mainDesk, BorderLayout.CENTER);
		}
	}

	public void flushConsoleLog() {
		console.flush();
	}

	/**
	 * Handle control bar show.
	 */
	private void handleControlBarShow() {
		if (showToolBar && toolbarPanel.getComponentCount() == 0) {
			toolbarPanel.add(environment.getControlBarView(),
					BorderLayout.WEST);
		} else {
			toolbarPanel.removeAll();
		}
		environment.getControlBarView().setVisible(true);
		innerPanel.validate();
	}

	// private void buildRootPanel(final UserEnvironment environment) {
	//
	// rootPanel = new JPanel();
	// rootPanel.setLayout(new BorderLayout());
	// innerPanel = new JPanel();
	// innerPanel.setLayout(new BorderLayout());
	// rootPanel.add(innerPanel, BorderLayout.CENTER);
	//
	// toolbarPanel = new JPanel();
	// toolbarPanel.setLayout(new BorderLayout());
	// innerPanel.add(toolbarPanel, BorderLayout.NORTH);
	// handleControlBarShow();
	//
	// handleShowNavigatorConsole();
	// }

	// private void handleShowNavigatorConsole() {
	// if (mainDesk != null) {
	// innerPanel.remove(mainDesk);
	// }
	// if (isShowNavigator()) {
	// if (navigator == null) {
	// navigator = new Navigator();
	// JPanel navTitle = new JPanel() {
	// public void paint(Graphics g) {
	// super.paint(g);
	// g.setColor(Color.white);
	// g.drawString("Navigator", 10, 10);
	// }
	// };
	// navTitle.setBackground(UserEnvironment.ACCENT_COLOR);
	// navTitle.setBorder(BorderFactory.createRaisedBevelBorder());
	// UserEnvironment.getDefaultEnvironment().addView(navigator, false);
	//
	// JScrollPane navScroll = new JScrollPane(navigator);
	//
	// JPanel navPanel = new JPanel(new BorderLayout());
	// navPanel.add(navScroll, BorderLayout.CENTER);
	// navPanel.add(navTitle, BorderLayout.NORTH);
	//
	// JSplitPane deskAndTree = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
	// true, navPanel, deskScroll);
	// deskAndTree.setDividerSize(8);
	// deskAndTree.setUI(new BasicSplitPaneUI());
	// deskAndTree.setOneTouchExpandable(true);
	// deskAndTree.setResizeWeight(1.0);
	// deskAndTree.setDividerLocation(180);
	//
	// mainDesk = deskAndTree;
	// }
	// } else {
	// if (navigator != null) {
	// environment.removeView(navigator);
	// navigator = null;
	// }
	// mainDesk = deskScroll;
	// }
	// if (isRedirectConsole()) {
	// if (deskTreeConsole == null) {
	// final JScrollPane consoleScroll = new JScrollPane() {
	// public Dimension getPreferredSize() {
	// return new Dimension(super.getPreferredSize().width, 120);
	// }
	// };
	// if (console == null) {
	// console = new ConsoleOutput() {
	// public void append(String text) {
	// super.append(text);
	// JScrollBar b = consoleScroll.getVerticalScrollBar();
	// b.setValue(b.getMaximum());
	// }
	// };
	// }
	// consoleScroll.setViewportView(console);
	// console.setMinimumSize(new Dimension(0, 0));
	// JPanel consolePanel = new JPanel(new BorderLayout());
	// consolePanel.add(consoleScroll, BorderLayout.CENTER);
	// JPanel consoleTitle = new JPanel() {
	// public void paint(Graphics g) {
	// super.paint(g);
	// g.setColor(Color.white);
	// g.drawString("Console", 10, 10);
	// }
	// };
	// consoleTitle.setBackground(UserEnvironment.ACCENT_COLOR);
	// consoleTitle.setBorder(BorderFactory.createRaisedBevelBorder());
	// consolePanel.add(consoleTitle, BorderLayout.NORTH);
	//
	// deskTreeConsole = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true,
	// mainDesk, consolePanel);
	// deskTreeConsole.setDividerSize(8);
	// deskTreeConsole.setUI(new BasicSplitPaneUI());
	// deskTreeConsole.setOneTouchExpandable(true);
	// deskTreeConsole.setResizeWeight(1.0);
	// deskTreeConsole.setDividerLocation(800);
	// }
	// mainDesk = deskTreeConsole;
	// } else {
	// if (deskTreeConsole != null) {
	// environment.removeView(navigator);
	// deskTreeConsole = null;
	// }
	// }
	// innerPanel.add(mainDesk, BorderLayout.CENTER);
	// }

	/**
	 * Checks if is show tool bar.
	 * 
	 * @return true, if is show tool bar
	 */
	public boolean isShowToolBar() {
		return showToolBar;
	}

	/**
	 * Sets the show tool bar.
	 * 
	 * @param showToolBar
	 *            the new show tool bar
	 */
	public void setShowToolBar(boolean showToolBar) {
		this.showToolBar = showToolBar;
		handleControlBarShow();
	}

	// public boolean isShowNavigator() {
	// return showNavigator;
	// }
	//
	// public void setShowNavigator(boolean showNavigator) {
	// this.showNavigator = showNavigator;
	// handleShowNavigatorConsole();
	// }
	//
	// public boolean isRedirectConsole() {
	// return redirectConsole;
	// }
	//
	// public void setRedirectConsole(boolean redirectConsole) {
	// this.redirectConsole = redirectConsole;
	// handleShowNavigatorConsole();
	// }

	// private void handleControlBarShow() {
	// if (isShowToolBar()) {
	// if (environment.getControlBarView() == null) {
	// environment.setControlBarView(new ControlBarView());
	// environment.getControlBarView().build();
	// environment.addView(environment.getControlBarView(), false);
	// toolbarPanel.add(environment.getControlBarView(), BorderLayout.WEST);
	// environment.getControlBarView().setVisible(true);
	// innerPanel.validate();
	// }
	// } else {
	// if (environment.getControlBarView() != null) {
	// toolbarPanel.remove(environment.getControlBarView());
	// environment.removeView(environment.getControlBarView());
	// environment.setControlBarView(null);
	// toolbarPanel.invalidate();
	// }
	// }
	// }

	/**
	 * Component views in.
	 * 
	 * @param comp
	 *            the comp
	 * @param currentList
	 *            the current list
	 */
	private void componentViewsIn(Component comp, List<Component> currentList) {
		if (comp instanceof org.ascape.view.vis.ComponentView) {
			currentList.add(comp);
		} else if (comp instanceof Container) {
			Component[] comps = ((Container) comp).getComponents();
			for (int i = 0; i < comps.length; i++) {
				componentViewsIn(comps[i], currentList);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Frame#addNotify()
	 */
	public void addNotify() {
		super.addNotify();
		calculateVisibility();
		// uncomment the following line to check for bad swing threads
		// RepaintManager.setCurrentManager(new ThreadCheckingRepaintManager());
	}

	/**
	 * Move component.
	 * 
	 * @param frame
	 *            the frame
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	public void moveComponent(JInternalFrame frame, int x, int y) {
		DesktopEnvironment.getDefaultDesktop().getUserFrame().getDesk()
				.getDesktopManager().beginDraggingFrame(frame);
		DesktopEnvironment.getDefaultDesktop().getUserFrame().getDesk()
				.getDesktopManager().dragFrame(frame, x, y);
		DesktopEnvironment.getDefaultDesktop().getUserFrame().getDesk()
				.getDesktopManager().endDraggingFrame(frame);
	}

	/**
	 * Calculate visibility.
	 */
	protected void calculateVisibility() {
		// SwingUtilities.invokeLater(new Runnable() {
		// public void run() {
		if (!minimized) {
			if (environment.getControlBarView() != null) {
				environment.getControlBarView().setVisible(true);
			}
			JInternalFrame[] frames = desk.getAllFrames();

			Rectangle fullBounds = new Rectangle(0, 0, 0, 0);// this.getBounds());
			for (int i = 0; i < frames.length; i++) {
				fullBounds = fullBounds.union(frames[i].getBounds());
			}
			desk.setPreferredSize(fullBounds.getSize());

			List<Component> allComponentViews = new LinkedList<Component>();
			componentViewsIn(desk, allComponentViews);
			Rectangle viewBounds = deskScroll.getViewport().getViewRect();
			try {
				Point viewLoc = deskScroll.getLocationOnScreen();
				Rectangle viewScreenBounds =
						new Rectangle(viewLoc.x, viewLoc.y, viewBounds.width,
								viewBounds.height);
				for (Iterator iterator = allComponentViews.iterator(); iterator
						.hasNext();) {
					Component component = (Component) iterator.next();
					if (!component.isVisible()) {
						component.setVisible(true);
					}
					Point compLoc = component.getLocationOnScreen();
					Rectangle boundsOnScreen =
							new Rectangle(compLoc.x, compLoc.y, component
									.getBounds().width,
									component.getBounds().height);
					if (boundsOnScreen.intersects(viewScreenBounds)) {
						component.setVisible(true);
					} else {
						component.setVisible(updateNonVisibleViews);
						((org.ascape.view.vis.ComponentView) component)
								.forceScapeNotify();
					}
				}
			} catch (IllegalComponentStateException e) {
				for (Iterator iterator = allComponentViews.iterator(); iterator
						.hasNext();) {
					Component component = (Component) iterator.next();
					component.setVisible(updateNonVisibleViews);
					((org.ascape.view.vis.ComponentView) component)
							.forceScapeNotify();
				}
			}

			for (int i = 0; i < frames.length; i++) {
				JInternalFrame frame1 = frames[i];
				if (!frame1.isIcon()) {
					for (int j = 0; j < frames.length; j++) {
						JInternalFrame frame2 = frames[j];
						if (!frame2.isIcon()) {
							// Is frame 1 in front of frame 2?
							if (desk.getIndexOf(frame1) < desk
									.getIndexOf(frame2)) {
								List<Component> frameComponentViews =
										new LinkedList<Component>();
								componentViewsIn(frame2, frameComponentViews);

								for (Iterator iterator =
										frameComponentViews.iterator(); iterator
										.hasNext();) {
									Component component =
											(Component) iterator.next();
									if (frame1
											.getBounds()
											.contains(
													calculateBoundsInFrame(
															component)
															.intersection(
																	viewBounds))) {
										component
												.setVisible(updateNonVisibleViews);
										((org.ascape.view.vis.ComponentView) component)
												.forceScapeNotify();
									}
								}
							}
						} else {
							List<Component> frameComponentViews =
									new LinkedList<Component>();
							componentViewsIn(frame2, frameComponentViews);

							for (Iterator iterator =
									frameComponentViews.iterator(); iterator
									.hasNext();) {
								((Component) iterator.next())
										.setVisible(updateNonVisibleViews);
							}
						}
					}
				}
			}
		} else {
			List<Component> allComponentViews = new LinkedList<Component>();
			componentViewsIn(desk, allComponentViews);
			for (Iterator iterator = allComponentViews.iterator(); iterator
					.hasNext();) {
				Component component = (Component) iterator.next();
				component.setVisible(updateNonVisibleViews);
				((org.ascape.view.vis.ComponentView) component)
						.forceScapeNotify();
			}
			environment.getControlBarView().setVisible(updateNonVisibleViews);
			environment.getControlBarView().forceScapeNotify();
		}
		// }
		// });
	}

	/**
	 * Calculate bounds in frame.
	 * 
	 * @param component
	 *            the component
	 * @return the rectangle
	 */
	private synchronized Rectangle calculateBoundsInFrame(Component component) {
		Point locationInDesk = new Point(0, 0);
		Component nextComponent = component;
		while (!(nextComponent instanceof JDesktopPane)
				&& nextComponent.getParent() != null) {
			locationInDesk =
					new Point((int) (locationInDesk.getX() + nextComponent
							.getLocation().getX()), (int) (locationInDesk
							.getY() + nextComponent.getLocation().getY()));
			nextComponent = nextComponent.getParent();
		}
		Rectangle componentBounds = component.getBounds();
		componentBounds =
				new Rectangle((int) locationInDesk.getX(),
						(int) locationInDesk.getY(), componentBounds.width,
						componentBounds.height);
		return componentBounds;
	}

	/**
	 * Gets the desk.
	 * 
	 * @return the desk
	 */
	public JDesktopPane getDesk() {
		return desk;
	}

	/**
	 * Gets the desk scroll pane.
	 * 
	 * @return the desk scroll pane
	 */
	public JScrollPane getDeskScrollPane() {
		return deskScroll;
	}

	/**
	 * Gets the tabbed console pane.
	 * 
	 * @return the console JTabbedPane
	 */
	public JTabbedPane getConsolePane() {
		return consolePane;
	}

	/**
	 * Gets the split plane between the console and the desktop.
	 * 
	 * @return the console JSplitPane
	 */
	public JSplitPane getConsoleSplit() {
		return deskTreeConsole;
	}

	/**
	 * Get the navigator.
	 * 
	 * @return navigator
	 */
	public Navigator getNavigator() {
		return navigator;
	}

	/**
	 * Checks if is show toolbar.
	 * 
	 * @return true, if is show toolbar
	 */
	public boolean isShowToolbar() {
		return showToolBar;
	}

	/**
	 * Sets the show toolbar.
	 * 
	 * @param showToolbar
	 *            the new show toolbar
	 */
	public void setShowToolbar(boolean showToolbar) {
		this.showToolBar = showToolbar;
		handleControlBarShow();
	}

	/**
	 * Checks if is place icons on left side.
	 * 
	 * @return true, if is place icons on left side
	 */
	public boolean isPlaceIconsOnLeftSide() {
		return placeIconsOnLeftSide;
	}

	/**
	 * Sets the place icons on left side.
	 * 
	 * @param placeIconsOnLeftSide
	 *            the new place icons on left side
	 */
	public void setPlaceIconsOnLeftSide(boolean placeIconsOnLeftSide) {
		this.placeIconsOnLeftSide = placeIconsOnLeftSide;
	}

	/**
	 * The Class UsertDesktopManager.
	 */
	private class UserDesktopManager extends DefaultDesktopManager {

		/**
         * 
         */
		private static final long serialVersionUID = 6102706823874157676L;
		/**
		 * The last bounds.
		 */
		Rectangle lastBounds;

		/*
		 * (non-Javadoc)
		 * 
		 * @seejavax.swing.DefaultDesktopManager#activateFrame(javax.swing.
		 * JInternalFrame)
		 */
		public void activateFrame(JInternalFrame f) {
			super.activateFrame(f);
			calculateVisibility();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.DefaultDesktopManager#closeFrame(javax.swing.JInternalFrame
		 * )
		 */
		public void closeFrame(JInternalFrame f) {
			super.closeFrame(f);
			calculateVisibility();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @seejavax.swing.DefaultDesktopManager#deactivateFrame(javax.swing.
		 * JInternalFrame)
		 */
		public void deactivateFrame(JInternalFrame f) {
			super.deactivateFrame(f);
			calculateVisibility();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @seejavax.swing.DefaultDesktopManager#deiconifyFrame(javax.swing.
		 * JInternalFrame)
		 */
		public void deiconifyFrame(JInternalFrame f) {
			super.deiconifyFrame(f);
			calculateVisibility();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.DefaultDesktopManager#iconifyFrame(javax.swing.JInternalFrame
		 * )
		 */
		public void iconifyFrame(JInternalFrame f) {
			super.iconifyFrame(f);
			calculateVisibility();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @seejavax.swing.DefaultDesktopManager#maximizeFrame(javax.swing.
		 * JInternalFrame)
		 */
		public void maximizeFrame(JInternalFrame f) {
			super.maximizeFrame(f);
			calculateVisibility();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @seejavax.swing.DefaultDesktopManager#minimizeFrame(javax.swing.
		 * JInternalFrame)
		 */
		public void minimizeFrame(JInternalFrame f) {
			super.minimizeFrame(f);
			calculateVisibility();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.DefaultDesktopManager#openFrame(javax.swing.JInternalFrame
		 * )
		 */
		public void openFrame(JInternalFrame f) {
			super.openFrame(f);
			calculateVisibility();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.DefaultDesktopManager#beginResizingFrame(javax.swing.
		 * JComponent, int)
		 */
		public void beginResizingFrame(JComponent f, int direction) {
			lastBounds = f.getBounds();
			super.beginResizingFrame(f, direction);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.DefaultDesktopManager#beginDraggingFrame(javax.swing.
		 * JComponent)
		 */
		public void beginDraggingFrame(JComponent f) {
			lastBounds = f.getBounds();
			super.beginDraggingFrame(f);
			calculateVisibility();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.DefaultDesktopManager#dragFrame(javax.swing.JComponent,
		 * int, int)
		 */
		public void dragFrame(JComponent f, int newX, int newY) {
			// prevent dragging to the left of scroll area to avoid
			// complications and keep interface simple..
			super.dragFrame(f, Math.max(newX, 0), Math.max(newY, 0));
			if (newX > lastBounds.getX()) {
				desk.setSize(desk.getPreferredSize());
				calculateVisibility();
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.DefaultDesktopManager#endDraggingFrame(javax.swing.JComponent
		 * )
		 */
		public void endDraggingFrame(JComponent f) {
			super.endDraggingFrame(f);
			desk.setSize(desk.getPreferredSize());
			calculateVisibility();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @seejavax.swing.DefaultDesktopManager#setBoundsForFrame(javax.swing.
		 * JComponent, int, int, int, int)
		 */
		public void setBoundsForFrame(JComponent f, int newX, int newY,
				int newWidth, int newHeight) {
			newX = Math.max(newX, 0);
			newY = Math.max(newY, 0);
			if (f instanceof ViewPanZoomFrame
					&& !((JInternalFrame) f).isIcon()
					&& ((ViewPanZoomFrame) f).getBridge().isLockZoomToFrame()
					&& ((ViewPanZoomFrame) f).getBridge().isAgentView()) {
				Dimension newWithin;
				if (((ViewPanZoomFrame) f).getBridge().getViews()[0] instanceof Overhead2DView) {
					if (newWidth > lastBounds.getWidth()
							&& newHeight == lastBounds.getHeight()) {
						// Allow vertical to grow to any size limited onl by
						// width
						newHeight = Integer.MAX_VALUE;
					} else if (newHeight > lastBounds.getHeight()
							&& newWidth == lastBounds.getWidth()) {
						// Allow horizontal to grow to any size limited onl by
						// height
						newWidth = Integer.MAX_VALUE;
					}
				}
				newWithin =
						((ViewPanZoomFrame) f)
								.getPreferredSizeWithin(new Dimension(
										newWidth, newHeight));
				// Dragging from top end
				if (newX != lastBounds.getBounds().getX()) {
					newX += newWidth - newWithin.getWidth();
				}
				// Dragging from left end
				if (newY != lastBounds.getBounds().getY()) {
					newY += newHeight - newWithin.getHeight();
				}
				super.setBoundsForFrame(f, newX, newY, (int) newWithin
						.getWidth(), (int) newWithin.getHeight());
			} else {
				super.setBoundsForFrame(f, newX, newY, newWidth, newHeight);
			}
			calculateVisibility();
		}

		// replace standard implementation to place icons on top...
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.DefaultDesktopManager#getBoundsForIconOf(javax.swing.
		 * JInternalFrame)
		 */
		protected Rectangle getBoundsForIconOf(JInternalFrame f) {
			if (placeIconsOnLeftSide) {
				JInternalFrame.JDesktopIcon icon = f.getDesktopIcon();
				Dimension prefSize = icon.getPreferredSize();
				Container c = f.getParent();
				if (c == null) {
					return new Rectangle(0, 0, prefSize.width,
							prefSize.height);
				}
				Rectangle parentBounds = c.getBounds();
				Component[] components = c.getComponents();
				Rectangle availableRectangle = null;
				JInternalFrame.JDesktopIcon currentIcon = null;
				int x = 0;
				int y = 0;// modification here
				int w = prefSize.width;
				int h = prefSize.height;
				boolean found = false;
				while (!found) {
					availableRectangle = new Rectangle(x, y, w, h);
					found = true;
					for (int i = 0; i < components.length; i++) {
						if (components[i] instanceof JInternalFrame) {
							currentIcon =
									((JInternalFrame) components[i])
											.getDesktopIcon();
						} else if (components[i] instanceof JInternalFrame.JDesktopIcon) {
							currentIcon =
									(JInternalFrame.JDesktopIcon) components[i];
						} else {
							continue;
						}
						if (!currentIcon.equals(icon)) {
							if (availableRectangle.intersects(currentIcon
									.getBounds())) {
								found = false;
								break;
							}
						}
					}
					if (currentIcon == null) {
						return availableRectangle;
					}
					y += currentIcon.getBounds().height;
					if (y + h > parentBounds.height) {
						y = 0;
						x += w; // modification here
					}
				}
				return availableRectangle;
			} else {
				return super.getBoundsForIconOf(f);
			}
		}
	}

	/**
	 * Checks if is update non visible views.
	 * 
	 * @return true, if is update non visible views
	 */
	public boolean isUpdateNonVisibleViews() {
		return updateNonVisibleViews;
	}

	/**
	 * Set true to have all views update, even if they are not currently
	 * visible. Useful for models that compute for a long time each iteration
	 * and typically have some views that are off-screen at any given time.
	 * Warning: can slow down models that are graphics-bound rather than
	 * compute-bound.
	 * 
	 * @param updateNonVisibleViews
	 *            the update non visible views
	 */
	public void setUpdateNonVisibleViews(boolean updateNonVisibleViews) {
		this.updateNonVisibleViews = updateNonVisibleViews;
	}

	/**
	 * Gets the menu view.
	 * 
	 * @return the menu view
	 */
	public MenuBarView getMenuView() {
		return menuView;
	}

}

/**
 * The console log text area. Reassigns System.out to itself. Creates a new
 * thread called "Ascape Consoler Updater".
 */
class ConsoleOutput extends JTextArea {

	/**
     * 
     */
	private static final long serialVersionUID = -2358292646446121662L;

	private boolean runThread = true;

	private boolean includeErr = false;

	private Thread readOut;

	private BufferedReader drOut;

	private PipedInputStream pinOut;

	private Thread readErr;

	private BufferedReader drErr;

	private PipedInputStream pinErr;

	class Appender implements Runnable {

		String appendString;

		public Appender(StringBuffer buffer) {
			appendString = buffer.toString();
		}

		public void run() {
			append(appendString);
		}
	}

	public void flush() {
		// wake up (interrupt) console updater thread
		// then yield the currently executing thread
		// so it can do its work
		readOut.interrupt();
		Thread.yield();
	}

	public ConsoleOutput() {
		setFont(new Font("Monospaced", Font.PLAIN, 12));
		final PipedOutputStream pout = new PipedOutputStream();
		final PrintStream sout = new PrintStream(pout, true);
		System.setOut(sout);

		readOut = new Thread("Ascape Console Updater StdOut") {
			public void start() {
				super.start();
				pinOut = new PipedInputStream();
				drOut = new BufferedReader(new InputStreamReader(pinOut));
				try {
					pout.connect(pinOut);
				} catch (IOException e) {
					throw new RuntimeException("Internal Exception: " + e);
				}
			}

			public void run() {
				while (runThread) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
					try {
						StringBuffer currentLines = new StringBuffer();
						String nextLine = drOut.readLine();
						while (nextLine != null) {
							currentLines.append(nextLine + "\n");
							if (drOut.ready()) {
								nextLine = drOut.readLine();
							} else {
								nextLine = null;
							}
						}
						SwingUtilities
								.invokeLater(new Appender(currentLines));
					} catch (IOException e) {
						// Ignore, pipe end dead(fix later...)
					}
				}
			}
		};

		// Terminate automatically when all other threead finish
		readOut.setDaemon(true);
		readOut.start();

		if (includeErr) {
			final PipedOutputStream perr = new PipedOutputStream();
			final PrintStream serr = new PrintStream(perr, true);
			System.setErr(serr);

			readErr = new Thread("Ascape Console Updater StdErr") {
				public void start() {
					super.start();
					pinErr = new PipedInputStream();
					drErr = new BufferedReader(new InputStreamReader(pinErr));
					try {
						perr.connect(pinErr);
					} catch (IOException e) {
						throw new RuntimeException("Internal Exception: " + e);
					}
				}

				public void run() {
					while (runThread) {
						try {
							sleep(50);
						} catch (InterruptedException e) {
						}
						try {
							final StringBuffer currentLines =
									new StringBuffer();
							String nextLine = drErr.readLine();
							// We want to stop placing stuff into the console if
							// the thread has been stopped, even if
							// there is still stuff in the queue
							int j = 0;
							// We don't want to go anymore than 10 so as not to
							// wait endlessly
							while (nextLine != null && runThread && j < 10) {
								j++;
								currentLines.append(nextLine + "\n");
								nextLine = drErr.readLine();
							}
							Runnable appendText = new Runnable() {
								public void run() {
									append(currentLines.toString());
								}
							};
							SwingUtilities.invokeLater(appendText);
						} catch (IOException e) {
							// Ignore, pipe end dead(fix later...)
						}
					}
				}
			};
			// Terminate automatically when all other threead finish
			readErr.setDaemon(true);
			readErr.start();
		}
	}
}
