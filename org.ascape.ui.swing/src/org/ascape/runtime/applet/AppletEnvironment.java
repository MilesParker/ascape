package org.ascape.runtime.applet;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.ascape.runtime.swing.DesktopEnvironment;
import org.ascape.runtime.swing.SwingEnvironment;
import org.ascape.view.custom.AutoCustomizerSwing;
import org.ascape.view.vis.AgentSizedView;
import org.ascape.view.vis.ComponentView;
import org.ascape.view.vis.control.ControlBarView;

public class AppletEnvironment extends SwingEnvironment {

    private static final long serialVersionUID = 8255010443821405398L;

    private static final int SETTINGS_WDITH = 250;

    /**
     * The applet the scape is running within, if any. Should be ok to have this static, since a vm should always either
     * be running as an applet or an application.
     */
    private SwingApplet applet;

    private JPanel viewPanel;

    private JPanel customizerPanel;

    private ControlBarView controlBar;

    private JSplitPane mainSlider;

    public AppletEnvironment() {
        SwingEnvironment.DEFAULT_ENVIRONMENT = this;
        setShowNavigator(false);
        AutoCustomizerSwing customizer = new AutoCustomizerSwing();
        setCustomizer(customizer);
    }

    public void initialize() {
        scape.addView(controlBar, false);

        AbstractAction settingsAction = new AbstractAction() {
            /**
             * 
             */
            private static final long serialVersionUID = -2962923969270143073L;

            public void actionPerformed(ActionEvent e) {
                if (mainSlider.getDividerLocation() > applet.getContentPane().getWidth() - SETTINGS_WDITH) {
                    mainSlider.setDividerLocation(applet.getContentPane().getWidth() - SETTINGS_WDITH);
                } else {
                    mainSlider.setDividerLocation(applet.getContentPane().getWidth());
                }
            }
        };
        settingsAction.putValue(Action.NAME, "Settings");
        settingsAction.putValue(Action.SHORT_DESCRIPTION, "Display Model Settings");
        settingsAction.putValue(Action.SMALL_ICON, DesktopEnvironment.getIcon("World"));
        DesktopEnvironment.addToolBarButton(controlBar.getRunControlBar(), settingsAction);

        scape.addView(getCustomizer());
        ((AutoCustomizerSwing) getCustomizer()).getDelegate().setNotifyScapeAutomatically(true);
    }

    public void createFrame(ComponentView[] views) {
        for (ComponentView componentView : views) {
            if (componentView instanceof AutoCustomizerSwing) {
                final AutoCustomizerSwing customizer = (AutoCustomizerSwing) componentView;
                applet.getContentPane().remove(viewPanel);
                mainSlider = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, viewPanel, customizer);
                applet.getContentPane().add("Center", mainSlider);
                mainSlider.setOneTouchExpandable(true);
                mainSlider.setResizeWeight(1.0);
                mainSlider.setLastDividerLocation(applet.getContentPane().getWidth() - SETTINGS_WDITH);
                mainSlider.setDividerLocation(applet.getContentPane().getWidth());
                customizer.forceScapeNotify();
            } else if (componentView instanceof javax.swing.JPanel) {
                final JPanel componentPanel = (JPanel) componentView;
                componentPanel.addComponentListener(new ComponentListener() {

                    public void componentShown(ComponentEvent e) {
                    }

                    public void componentResized(ComponentEvent e) {
                        if (componentPanel instanceof AgentSizedView) {
                            int newAgentSize = ((AgentSizedView) componentPanel).calculateAgentSizeForViewSize(e
                                                                                                               .getComponent().getSize());
                            ((AgentSizedView) componentPanel).setAgentSize(newAgentSize);
                        }
                    }

                    public void componentMoved(ComponentEvent e) {
                    }

                    public void componentHidden(ComponentEvent e) {
                    }
                });
                if (viewPanel.getComponents().length > 0) {
                    Component subComponent = viewPanel.getComponents()[0];
                    viewPanel.remove(subComponent);
                    JSplitPane subSlider = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, subComponent,
                                                          componentPanel);
                    applet.getContentPane().add("Center", subSlider);
                    subSlider.setDividerLocation(subComponent.getPreferredSize().width);
                    viewPanel.add(subSlider);
                } else {
                    viewPanel.add(componentPanel);
                }
            }
        }
    }

    public boolean isInApplet() {
        return false;
    }

    /**
     * If model is running in an applet vm context, returns the applet.
     * 
     * @return the applet
     */
    public SwingApplet getApplet() {
        return applet;
    }

    /**
     * Sets the applet scape views are to be displayed within, indicating that we are in an applet vm context.
     * 
     * @param _applet
     *        the _applet
     */
    public void setApplet(Object _applet) {
        applet = (SwingApplet) _applet;
        applet.setLayout(new BorderLayout());

        controlBar = new ControlBarView("Applet Control", true);
        JPanel controlArea = new JPanel();
        controlArea.setLayout(new BorderLayout());
        controlArea.add("Center", controlBar);
        applet.getContentPane().add("South", controlArea);

        // JPanel customizerArea = new JPanel();
        // customizerArea.setLayout(new BorderLayout());
        // customizerArea.add("Center", (AutoCustomizerSwing) getCustomizer());
        // applet.getContentPane().add("East", customizerArea);

        viewPanel = new JPanel();
        applet.getContentPane().add("Center", viewPanel);
        viewPanel.setLayout(new javax.swing.BoxLayout(viewPanel, javax.swing.BoxLayout.X_AXIS));
    }
}
