/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.runtime.swing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.ascape.model.Scape;
import org.ascape.model.event.DefaultScapeListener;
import org.ascape.model.event.ScapeEvent;
import org.ascape.runtime.Runner;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Manages model runs in a Swing UI environment.
 * 
 * @author Miles Parker
 * @since June 14, 2002
 * @version 3.0
 * @history June 14 first in
 */
public class SwingRunner extends BasicSwingRunner {

    /**
     * 
     */
    private static final long serialVersionUID = -2041589422471681672L;

    public SwingRunner() {
        super(new DesktopEnvironment());
    }

    /**
     * Creates, initializes and runs the model specified in the argument. To allow the running of a model directly from
     * the command line, you should subclass this method as shown below:
     * 
     * <pre><code><BR>
     *     public MyModel extends Model {
     *         public static void main(String[] args) {
     *             (open(&quot;mypath.MyModel&quot;)).start();
     *         }
     *     }
     * <BR>
     * </pre>
     * 
     * </code> Otherwise, assuming your classpath is set up correctly, to invoke a model from the command line type:
     * 
     * <pre><code><BR>
     *     java org.ascape.model.Scape mypath.myModel
     * </pre>
     * 
     * </code>
     * 
     * @param args
     *        at index 0; the name of the subclass of this class to run
     */
    public static void main(String[] args) {
        // Register environment
        Runner runner = new SwingRunner();
        try {
            runner.launch(args);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Exception attempting to load model.", "Error",
                                          JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void openImplementation(String[] args, boolean block) {
        Runner.assignEnvironmentParameters(args);
        if (isDisplayGraphics() && !(environment instanceof DesktopEnvironment)) {
            environment = new DesktopEnvironment();
        }
        super.openImplementation(args, block);
    }

    /**
     * Save the state of the scape to a file.
     */
    public void saveChoose() {
        JFileChooser chooser = null;
        boolean overwrite = false;
        File savedFile;
        while (!overwrite) {
            chooser = new JFileChooser();
            int option = chooser.showSaveDialog(null);
            if (option == JFileChooser.APPROVE_OPTION) {
                savedFile = chooser.getSelectedFile();
            } else {
                // user cancelled
                return;
            }
            if (savedFile.exists()) {
                // warn user about overwriting already existing file
                int n = JOptionPane.showConfirmDialog(chooser, "Warning - A file already exists by this name!\n"
                                                      + "Do you want to overwrite it?\n", "Save Confirmation", JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.YES_OPTION) {
                    overwrite = true;
                } else if (n == JOptionPane.CANCEL_OPTION) {
                    // need to break out of the loop and return to the model
                    chooser.cancelSelection();
                    getRootScape().getRunner().resume();
                }
            } else {
                overwrite = true;
            }
        }
        if (chooser.getSelectedFile() != null) {
            try {
                getRootScape().save(chooser.getSelectedFile());
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                                              "Sorry, couldn't save model because an input/output exception occured:\n"
                                              + e, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "You must enter a file name or cancel.", "Message",
                                          JOptionPane.INFORMATION_MESSAGE);
            // To Do, possible (though quite unlikely) stack exception, replace
            // w/ loop strucuture
            saveChoose();
        }
    }

    /**
     * Requests the scape to open a saved run, closing the existing one. Will not occur until the current iteration is
     * complete; use static forms to open concurrently. Always called on root.
     */
    public void closeAndOpenSavedFinally(Scape oldScape) {
        boolean exit = false;
        if (oldScape != null) {
            if (!oldScape.isPaused()) {
                oldScape.getRunner().pause();
            }
        }
        while (!exit) {
            JFileChooser chooser = new JFileChooser();
            int option = chooser.showOpenDialog(null);
            if (option == JFileChooser.APPROVE_OPTION) {
                if (chooser.getSelectedFile() != null) {
                    try {
                        final Scape newScape = openSavedRun(chooser.getSelectedFile());
                        if (newScape != null && oldScape != null) {
                            oldScape.addView(new DefaultScapeListener() {
                                /**
                                 * 
                                 */
                                private static final long serialVersionUID = 3300275064817945877L;

                                public void scapeClosing(ScapeEvent scapeEvent) {
                                    newScape.getRunner().setEnvironment(environment);
                                    newScape.getRunner().start();
                                    // open(modelName);
                                }
                            });
                            oldScape.getRunner().close();
                            exit = true;
                        }
                    } catch (FileNotFoundException e) {
                        JOptionPane.showMessageDialog(null, "Sorry, could not find the file you specified:\n"
                                                      + chooser.getSelectedFile(), "Error", JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException e) {
                        String msg = "Sorry, couldn't open model because a file exception occured:";
                        System.err.println(msg);
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, msg + "\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "You must enter a file name or cancel.", "Message",
                                                  JOptionPane.INFORMATION_MESSAGE);
                    closeAndOpenSavedFinally(oldScape);
                }
            } else {
                exit = true;
            }
        }
        // if (oldScape != null) {
        // oldScape.getModel().close();
        // }
        // if ((oldScape != null) && !oldWasPaused) {
        // oldScape.resume();
        // }
    }


    private IOException defaultWriteException;

    public void write(final java.io.ObjectOutputStream out) throws IOException {
        if (SwingUtilities.isEventDispatchThread()) {
            // This is all that is necessary if we are already in
            // the event dispatch thread, e.g. a user clicked a
            // button which caused the object to be written
            // System.err.println("Scape.writeObject - name: " + getName()+",
            // class: " + getClass());
            out.defaultWriteObject();
        } else {
            try {
                // we want to wait until the object has been written
                // before continuing. If we called this from the
                // event dispatch thread we would get an exception
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        try {
                            // easiest way to indicate to the enclosing class
                            // that an exception occurred is to have a member
                            // which keeps the IOException
                            defaultWriteException = null;
                            // we call the actual write object method
                            out.defaultWriteObject();
                        } catch (IOException ex) {
                            // oops, an exception occurred, remember the
                            // exception object
                            defaultWriteException = ex;
                        }
                    }
                });
                if (defaultWriteException != null) {
                    // an exception occurred in the code above, throw it!
                    throw defaultWriteException;
                }
            } catch (InterruptedException ex) {
                // I'm not quite sure what do here, perhaps:
                Thread.currentThread().interrupt();
                return;
            } catch (InvocationTargetException ex) {
                // This can actually only be a RuntimeException or an
                // Error - in either case we want to rethrow them
                Throwable target = ex.getTargetException();
                if (target instanceof RuntimeException) {
                    throw (RuntimeException) target;
                } else if (target instanceof Error) {
                    throw (Error) target;
                }
                ex.printStackTrace(); // this should not happen!
                throw new RuntimeException(ex.toString());
            }
        }
    }
}
