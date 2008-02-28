/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.runtime.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

/**
 * Creates and displays a basic splash screen.
 * 
 * @author Rod Nibbe (Modified by Miles Parker for use in Ascape)
 * @version 1.0
 * @since 1.0
 */
public class SplashScreen {

    /**
     * Instantiates a new splash screen.
     * 
     * @param duration
     *            the duration
     */
    public SplashScreen(final long duration) {
        Dimension center;
        final JWindow splash = new JWindow();
        JPanel jp = (JPanel) splash.getContentPane();

        Image logo = DesktopEnvironment.getImage("ascapesplash.png");

        ImageIcon image = new ImageIcon(logo);

        JLabel jl = new JLabel(image);
        int width = logo.getWidth(image.getImageObserver());
        int height = logo.getHeight(image.getImageObserver());
        center = Toolkit.getDefaultToolkit().getScreenSize();
        splash.setBounds((center.width - width) / 2, (center.height - height) / 2, width, height);
        jp.add(jl, BorderLayout.CENTER);
        splash.setVisible(true);


        final Thread nonblockingThread = new Thread() {
            public void run() {
                try {
                    sleep(duration);
                } catch (Exception e) {
                }
                splash.dispose();
            }
        };
        nonblockingThread.start();
    }

    /**
     * The main method.
     * 
     * @param args
     *            the arguments
     */
    public static void main(String[] args) {
        long duration = 5000L; // milliseconds

        if (args.length == 1) {
            duration = Long.parseLong(args[0]);
        }

        new SplashScreen(duration);
        System.exit(0);
    }

}
