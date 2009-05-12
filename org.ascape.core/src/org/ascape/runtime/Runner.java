/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.runtime;

import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import org.ascape.model.Scape;
import org.ascape.model.event.ControlEvent;
import org.ascape.model.event.DefaultScapeListener;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.event.ScapeListener;
import org.ascape.model.rule.NotifyViews;
import org.ascape.model.space.SpatialTemporalException;
import org.ascape.util.PropertyAccessor;
import org.ascape.util.data.DataGroup;

/**
 * Manages model runs.
 * 
 * @author Miles Parker
 * @since June 14, 2002
 * @version 3.0
 * @history June 14 first in
 */
public abstract class Runner implements Serializable, Runnable {

    /**
     * 
     */
    private static final long serialVersionUID = 6924379091134591724L;

    private Scape scape;

    /**
     * Data group for all scapes. At some point this may be made non-static.
     */
    private DataGroup dataGroup;

    /**
     * Should any scapes opened be started automatically? Default true. true.
     */
    private static boolean startOnOpen = true;

    /**
     * Manages user space UI, settings etc.
     */
    protected transient RuntimeEnvironment environment;

    /**
     * The unit of time each iteration or period represents.
     */
    private String periodName = "Iteration";

    /**
     * A brief descripiton (including credits) of the scape or of the model, if
     * this is root scape. Plaintext.
     */
    private String description;

    /**
     * A brief descripiton (including credits) of the scape or of the model, if
     * this is root scape. Includes HTML style tags as appropriate.
     */
    private String HTMLDescription;

    /**
     * Iteration to start on when restarting, creating new model, etc...
     */
    private int startPeriod = 0;

    /**
     * Iteration to stop on.
     */
    private int stopPeriod = Integer.MAX_VALUE;

    /**
     * Period to pause on.
     */
    private int pausePeriod = Integer.MAX_VALUE;

    /**
     * The system path in which all files are by default stored to and retrieved
     * from. The value of the system variable ascape home.
     */
    private String home;

    /**
     * The earliest period this scape is expected to be run at.
     */
    private int earliestPeriod;

    /**
     * The latest period this scape is expected to be run at.
     */
    private int latestPeriod = Integer.MAX_VALUE;

    private List restartingViews = new Vector();

    /**
     * The number of iterations since the scape began iterating.
     */
    private int iteration;

    /**
     * The current period.
     */
    private int period;

    /**
     * Is the scape currently paused?
     */
    private boolean paused = false;

    /**
     * Is the scape currently running?
     */
    private boolean running = false;

    /**
     * Has a step been requested?
     */
    private boolean step = false;

    /**
     * Has a restart been requested after the current run stops?
     */
    private boolean closeAndOpenNewRequested = false;

    /**
     * Has loading of a saved run been requested after the current run stops?
     */
    private boolean closeAndOpenSavedRequested = false;

    /*
     * All of the following will be replaced by a diferred control mechanism.
     */

    /**
     * Has a restart been requested after the current run stops?
     */
    private boolean restartRequested = false;

    /**
     * Has a close been requested after the current run stops?
     */
    private boolean closeRequested = false;

    /**
     * Has a quit been requested after the current run stops?
     */
    private boolean quitRequested = false;

    /**
     * Has an open been requested when the current iteration completes?
     */
    private boolean openRequested = false;

    /**
     * Has a save been requested when the current iteration completes?
     */
    private boolean saveRequested = false;

    /**
     * Are we currently in the main control loop?
     */
    private boolean inMainLoop = false;

    private boolean beginningDeserializedRun = false;

    /**
     * Should the scape be restarted automatically after being stopped?
     */
    private boolean autoRestart = true;

    /**
     * Indicates that GUI should be displayed, if false, not GUI under any
     * circumstances.
     */
    private static boolean displayGraphics = true;

    /**
     * Indicates that we are forwarding graphics to a client scape.
     */
    private static boolean serveGraphics = false;

    /**
     * Indicates that we are in a multiwin environment and want simple winsow
     * strucutures.
     */
    private static boolean muiltWinEnvironment;

    private Thread modelThread;

    public Runner() {
        this(new RuntimeEnvironment());
    }

    public Runner(RuntimeEnvironment environment) {
        this.environment = environment;
    }

    protected void initialize() {
        setInternalRunning(false);
        getData().clear();
        scape.reseed();
        scape.execute(new NotifyViews(ScapeEvent.REQUEST_SETUP));
        waitForViewsUpdate();
        setIteration(0);
        setPeriod(getStartPeriod());
        scape.execute(Scape.INITIALIZE_RULE);
        scape.execute(new NotifyViews(ScapeEvent.REPORT_INITIALIZED));
        waitForViewsUpdate();
        scape.execute(Scape.INITIAL_RULES_RULE);
        setInternalRunning(true);
    }

    /**
     * The main run loop of a running simulation. Seperated from run so that it
     * can be executed in different runtime modes.
     */
    protected synchronized void runMainLoop() {
        inMainLoop = true;
        restartRequested = false;
        if (beginningDeserializedRun) { // we are re-starting the main loop
            // after reading in a serialized model
            beginningDeserializedRun = false;
            saveRequested = false;
            initialize();
            scape.executeOnRoot(new NotifyViews(ScapeEvent.REPORT_DESERIALIZED));
            waitForViewsUpdate();
            scape.reseed();
            getEnvironment().getConsole().println("\nNew Random Seed: " + scape.getRandomSeed() + "\n");
        } else { // !beginningDeserializedRun
            scape.executeOnRoot(Scape.CLEAR_STATS_RULE);
            initialize();
            scape.executeOnRoot(Scape.COLLECT_STATS_RULE);
            scape.executeOnRoot(new NotifyViews(ScapeEvent.REPORT_START));
            waitForViewsUpdate();
        }
        while (running) {
            if (scape.isListenersAndMembersCurrent() && (!paused || step)) {
                scape.executeOnRoot(Scape.CLEAR_STATS_RULE);
                iteration++;
                period++;
                // I've moved the pausePeriod code here because when it was
                // located in runMainLoop() the
                // views were not getting properly updated when the pause period
                // was reached. - Mario
                if (period == getPausePeriod() && !paused) {
                    pause();
                }
                scape.executeOnRoot(Scape.EXECUTE_RULES_RULE);
                scape.executeOnRoot(Scape.COLLECT_STATS_RULE);
                scape.executeOnRoot(new NotifyViews(ScapeEvent.REPORT_ITERATE));
                step = false;
            } else {
                if (paused) {
                    waitForViewsUpdate();
                    scape.executeOnRoot(new NotifyViews(ScapeEvent.TICK));
                    try {
                        // Wait for user to unpause model
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                } else {
                    try {
                        // Don't hog cycles while listeners are updating!
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }
                }
            }
            if (period >= getStopPeriod()) {
                waitForViewsUpdate();
                running = false;
                if (isAutoRestart()) {
                    restartRequested = true;
                }
            }
            if (closeAndOpenNewRequested) {
                new Thread(this, "Ascape Main Execution Loop") {
                    public void run() {
                        closeAndOpenNewFinally(scape);
                    }
                }.start();
                closeAndOpenNewRequested = false;
            }
            if (closeAndOpenSavedRequested) {
                new Thread(this, "Ascape Main Execution Loop") {
                    public void run() {
                        closeAndOpenSavedFinally(scape);
                    }
                }.start();
                closeAndOpenSavedRequested = false;
            }
            if (saveRequested) {
                waitForViewsUpdate();
                saveChoose();
                saveRequested = false;
            }
            if (openRequested) {
                waitForViewsUpdate();
                openChoose();
                openRequested = false;
            }
        }
        scape.executeOnRoot(new NotifyViews(ScapeEvent.REPORT_STOP));
        // Wait to ensure that all listeners are notified
        waitForViewsUpdate();
        if (restartRequested) {
            // Send an event to self for start
            scape.respondControl(new ControlEvent(this, ControlEvent.REQUEST_START));
        }
        if (closeRequested) {
            closeFinally();
            closeRequested = false;
        }
        if (quitRequested) {
            quitFinally();
        }
        inMainLoop = false;
    }

    /**
     * Responds to any control events fired at this scape. Currently reacts to
     * start, stop, pause, resume, step, quit, and restart events, as well as
     * listener update report events. All control events except listener updates
     * are passed up to the root. Any other events trigger an untrapped
     * exception.
     */
    public void respondControl(ControlEvent control) {
        switch (control.getID()) {
            case ControlEvent.REQUEST_CLOSE:
                close();
                break;
            case ControlEvent.REQUEST_OPEN:
                closeAndOpenNew();
                break;
            case ControlEvent.REQUEST_OPEN_SAVED:
                closeAndOpenSaved();
                break;
            case ControlEvent.REQUEST_SAVE:
                save();
                break;
            case ControlEvent.REQUEST_START:
                start();
                break;
            case ControlEvent.REQUEST_STOP:
                stop();
                break;
            case ControlEvent.REQUEST_STEP:
                setStep(true);
                break;
            case ControlEvent.REQUEST_RESTART:
                restart();
                break;
            case ControlEvent.REQUEST_PAUSE:
                pause();
                break;
            case ControlEvent.REQUEST_RESUME:
                resume();
                break;
            case ControlEvent.REQUEST_QUIT:
                quit();
                break;
            default:
                throw new RuntimeException("Unknown control event sent to Agent scape: " + control + " [" + control.getID()
                                           + "]");
        }
    }

    /**
     * Blocks until all views of this scape and this scape's members have been
     * updated.
     */
    public void waitForViewsUpdate() {
        while (!scape.isAllViewsUpdated() && inMainLoop) {
            try {
                // Don't hog cycles while listeners are updating!
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * Requests the scape to open another model, closing the existing one. Will
     * not occur until the current iteration is complete; use static forms to
     * open concurrently. Always called on root.
     */
    public void closeAndOpenNew() {
        if (running) {
            // Running, so we have to allow current iteration to finish
            closeAndOpenNewRequested = true;
        } else {
            closeAndOpenNewFinally(scape);
        }
    }

    /**
     * Requests the scape to open a saved run, closing the existing one. Will
     * not occur until the current iteration is complete; use static forms to
     * open concurrently. Always called on root.
     */
    public void closeAndOpenSaved() {
        if (isRunning()) {
            // Running, so we have to allow current iteration to finish
            setCloseAndOpenNewRequested(true);
        } else {
            // have to start a new thread or else the GUI locks up
            new Thread(this) {
                public void run() {
                    closeAndOpenSavedFinally(getRootScape());
                }
            }.start();
        }
    }

    /**
     * Requests the scape to open another model, closing the existing one.
     * Always called on root.
     */
    public void closeAndOpenNewFinally(final Scape oldScape) {
        boolean oldWasPaused = oldScape.isPaused();
        if (!oldWasPaused) {
            oldScape.getRunner().pause();
        }
        final String modelName = ((AbstractUIEnvironment) environment).openDialog();
        if (!oldWasPaused) {
            oldScape.getRunner().resume();
        }
        if (modelName != null) {
            // We want the old scape ot close first..
            oldScape.addView(new DefaultScapeListener() {
                /**
                 * 
                 */
                private static final long serialVersionUID = 1L;

                public void scapeClosing(ScapeEvent scapeEvent) {
                    openInstance(modelName);
                }
            });
            oldScape.getRunner().close();
        }
    }

    public static Scape openSavedRun(InputStream is) throws IOException {
        Scape newScape = null;
        GZIPInputStream gis = new GZIPInputStream(is);
        ObjectInputStream ois = new ObjectInputStream(gis);

        try {
            newScape = (Scape) ois.readObject();
            ois.close();
            // startPeriod is static so we have to set it here
            // we set it to scape.period + 1 so that there is not a blank first
            // point in the charts
            try {
                newScape.setStartPeriod(newScape.getPeriod() + 1);
            } catch (SpatialTemporalException e) {
                try {
                    newScape.setStartPeriod(newScape.getPeriod());
                } catch (SpatialTemporalException e1) {
                    try {
                        newScape.setStartPeriod(newScape.getPeriod());
                    } catch (SpatialTemporalException e2) {
                        throw new RuntimeException("Internal Error");
                    }
                }
            }
            newScape.getRunner().beginningDeserializedRun = true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return newScape;
    }

    public Scape openSavedRun(String fileName, String[] args) throws IOException {
        Scape newScape = openSavedRun(new File(fileName));

        if (newScape != null) {

            if (args.length > 0) {
                newScape.assignParameters(args, true);
            }
            newScape.getRunner().createEnvironment();

            if (newScape.isPaused() && newScape.isStartOnOpen()) {
                newScape.getRunner().resume();
            }

            newScape.getRunner().runMainLoop();
        }

        return newScape;
    }

    public Scape openSavedRun(File savedRunFile) throws IOException {
        Scape newScape = null;
        InputStream is = new FileInputStream(savedRunFile);
        newScape = openSavedRun(is);
        return newScape;
    }

    //    public void createNewModel() {
    //        createNewModel(null, new String[0]);
    //    }
    //
    //    public void createNewModel(Object applet, String[] args) {
    //        try {
    //            if (applet != null) {
    //                scape.getUIEnvironment().setApplet(applet, scape);
    //            }
    //            if (args != null) {
    //                // first pass of parseSettingArgs,
    //                // don't report any parameters not found
    //                scape.assignParameters(args, false);
    //            }
    //            scape.executeOnRoot(Scape.CREATE_RULE);
    //            if (args != null) {
    //                // second pass of parseSettingArgs,
    //                // this time report any parameters not found
    //                scape.assignParameters(args, true);
    //            }
    //            // mtp 12/7/2000
    //            scape.executeOnRoot(Scape.CREATE_VIEW_RULE);
    //            if (Runner.isStartOnOpen()) {
    //                start();
    //            }
    //        } catch (RuntimeException e) {
    //            if (scape.getUIEnvironment() != null) {
    //                scape.getUIEnvironment().showErrorDialog(scape, e);
    //            } else {
    //                throw (e);
    //            }
    //        }
    //    }

    /**
     * Open (create) and run the model, just as in the normal open, but block
     * execution. Should be used for testing model run behavior only.
     */
    public void testRun() {
        try {
            scape.executeOnRoot(Scape.CREATE_RULE);
            run();
        } catch (RuntimeException e) {
            if (scape.getUIEnvironment() != null) {
                scape.getUIEnvironment().showErrorDialog(scape, e);
            } else {
                throw e;
            }
        }
    }

    /**
     * Requests the scape to save itself, providing UI for this purpose. Will
     * not occur until the current iteration is complete. Always called on root.
     */
    public void save() {
        if (scape.isRoot()) {
            saveRequested = true;
        } else {
            save();
        }
    }

    /**
     * The basic execution cycle of a running scape. In normal usage this method
     * is not called directly; use start() instead. You might choose to call
     * this method directly if you want the calling code to block, for instance,
     * in order to test that some behavior occurs. In the current
     * implementation, only the root scape is a running thread; all child scapes
     * are iterated through the root thread. Synchronous, determined,
     * reproducible behavior is expected, let us know if you encounter anything
     * different! The cycle always begins by notifying any observers, giving
     * them a chance to observe initial state. Then, the scape waits for the
     * observers to update. When updated, the simulation iterates the root scape
     * and all child scapes with their rules. Again, the scape waits for the
     * observers to update, and the cycle of iteration and update continues
     * until it is paused or stopped. While paused, tick events will be sent to
     * observers, which typically chose to ignore them.
     */
    public synchronized void run() {
        run(false);
    }

    /**
     * Sets values for the models paramters based on supplied array of key value
     * pairs. Paramters and values are expected to be seperated with an "=", for
     * example: "MyParameter=12".
     * 
     * @param args
     *            an array of strings with paramter-value paris in the form
     *            "{paramter-name}={paramter-value}"
     * @param reportNotFound
     *            if paramters not found should result in a console notification
     *            and if errors in invocation should be reported, false
     *            otherwise
     */
    public static boolean assignEnvironmentParameters(String[] args) {
        boolean found = args.length == 0;
        for (String arg : args) {
            String paramName = PropertyAccessor.paramName(arg);
            found = found || assignEnvironmentParameter(arg, paramName);
        }
        return found;
    }

    public static boolean assignEnvironmentParameter(String arg, String paramName) {
        boolean found = false;
        if (paramName != null) {
            if (paramName.equalsIgnoreCase("DisplayGraphics")) {
                Runner.setDisplayGraphics(PropertyAccessor.paramValueBoolean(arg));
                found = true;
            } else if (paramName.equalsIgnoreCase("ServeGraphics")) {
                Runner.setServeGraphics(PropertyAccessor.paramValueBoolean(arg));
                found = true;
            } else if (paramName.equalsIgnoreCase("MultiWin")) {
                Runner.setMultiWinEnvironment(PropertyAccessor.paramValueBoolean(arg));
                found = true;
            } else if (paramName.equalsIgnoreCase("RedirectConsole")) {
                AbstractUIEnvironment.setRedirectConsole(PropertyAccessor.paramValueBoolean(arg));
                found = true;
            } else if (paramName.equalsIgnoreCase("ShowNavigator")) {
                AbstractUIEnvironment.setShowNavigator(PropertyAccessor.paramValueBoolean(arg));
                found = true;
            }
            //                if (!found && reportNotFound) {
            //                    getEnvironment().getConsole().println("***WARNING: Parameter not found: " + paramName);
            //                }
        }
        return found;
    }

    public void launch(String[] args) throws IOException {
        if (args.length > 0 && args[0].indexOf("=") == -1) {
            assignEnvironmentParameters(args);
            // the first arg is not a "key=value" pair, so it must be the
            // name of a java class implementing an Ascape model
            String[] argsRem = new String[args.length - 1];
            System.arraycopy(args, 1, argsRem, 0, argsRem.length);
            open(args[0], argsRem);
        } else {
            String fileName = null;
            List argsList = new LinkedList(Arrays.asList(args));

            for (ListIterator li = argsList.listIterator(); li.hasNext();) {
                String arg = (String) li.next();
                int equalAt = arg.lastIndexOf("=");
                if (equalAt < 1) {
                    getEnvironment().getConsole().println("Syntax error in command line: " + arg);
                } else {
                    String paramName = arg.substring(0, equalAt);
                    if (paramName.equalsIgnoreCase("SavedRun")) {
                        fileName = arg.substring(equalAt + 1);
                        li.remove();
                    }
                }
            }
            if (fileName != null) {
                scape = openSavedRun(fileName, (String[]) argsList.toArray(new String[0]));
            } else {
                // moved to top of method, so the license agreement has a frame
                // environment = new UIEnvironment();
                // OK to do this here, since we know the user has to be in a GUI
                // environment (don't have to wait to assign params...
                // environment = new UIEnvironment();
                // UIEnvironment.checkForLicenseAgreement();
                scape = openChoose(args);
                if (scape != null) {
                    scape.getRunner().createEnvironment();
                } else {
                    System.exit(0);
                }
            }
        }
    }

    /**
     * The basic execution cycle of a running scape. In normal usage this methos
     * is not called directly; use start() instead. You might choose to call
     * this method directly if you want the calling code to block, for instance,
     * in order to test that some behavior occurs. Also use this version with
     * argument "true" if you want to continue using the same thread for
     * restarts.
     * 
     * @param singlethread
     *            CURRENTLY IGNORED! should the run if restarted continue to use the same thread?
     */
    public void run(boolean singlethread) {
        if (scape.getUIEnvironment() != null
                && scape.getUIEnvironment().getRuntimeMode() == AbstractUIEnvironment.RELEASE_RUNTIME_MODE) {
            try {
                runMainLoop();
            } catch (RuntimeException e) {
                scape.getUIEnvironment().showErrorDialog(scape, e);
            }
        } else {
            runMainLoop();
        }
    }

    /**
     * Requests the scape to start. Note that the scape may not start
     * immeadiatly.
     * 
     * @see #setRunning
     */
    public void start() {
        if (!isRunning()) {
            modelThread = new Thread(this, "Ascape Main Execution Loop");
            modelThread.start();
        } else {
            System.out.println("Warning: Tried to start an already running scape.");
        }
    }

    public void notify(final ScapeEvent event, final ScapeListener listener) {
        listener.scapeNotification(event);
    }

    public void write(final java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    /**
     * Requests the scape to stop. Note that the scape will not actually stop
     * until the current iteration is complete.
     * 
     * @see #setRunning
     */
    public void stop() {
        setInternalRunning(false);
    }

    /**
     * Requests the scape to pause. (Convenience method).
     * 
     * @see #setPaused
     */
    public void pause() {
        setPaused(true);
    }

    /**
     * Requests the scape to resume. (Convenience method).
     * 
     * @see #setPaused
     */
    public void resume() {
        setPaused(false);
    }

    /**
     * Requests the scape to restart.
     */
    public void requestRestart() {
        restartRequested = true;
    }

    /**
     * Stops the scape and requests the scape to restart. (Convenience method).
     * 
     * @see #setRunning
     */
    public void restart() {
        if (running) {
            stop();
            restartRequested = true;
        } else {
            start();
        }
    }

    /**
     * Method neccessary because of amibiguous null values in simpler signature
     * methods.
     */
    public void openImplementation(String[] args, boolean block) {
        try {
            if (args != null) {
                // first pass of parseSettingArgs,
                // don't report any parameters not found
                scape.assignParameters(args, false);
            }
            scape.executeOnRoot(Scape.CREATE_RULE);
            if (args != null) {
                // second pass of parseSettingArgs,
                // this time report any parameters not found
                scape.assignParameters(args, true);
            }
            if (scape.getEnvironment() != null) {
                scape.addView(scape.getEnvironment());
            }

            scape.executeOnRoot(Scape.CREATE_VIEW_RULE);
            scape.createViews(args);

            if (Runner.isStartOnOpen()) {
                if (!block) {
                    start();
                } else {
                    run();
                }
            }
        } catch (RuntimeException e) {
            if (getEnvironment() instanceof AbstractUIEnvironment) {
                ((AbstractUIEnvironment) getEnvironment()).showErrorDialog(scape, e);
            } else {
                throw e;
            }
        }
    }

    /**
     * Creates and runs (if start on open is true) this model scape.
     * 
     * @param applet
     *            the applet if are we in an applet vm context
     * @param args
     *            paramter arguments for the scape
     * @param block
     *            should this call block or run in a new thread?
     */
    public void open(Object applet, String[] args, boolean block) {
        openImplementation(args, block);
    }

    /**
     * Creates and runs (if start on open is true) this model scape.
     * 
     * @param applet
     *            the applet if are we in an applet vm context
     * @param args
     *            paramter arguments for the scape
     */
    public void open(Object applet, String[] args) {
        openImplementation(args, false);
    }

    /**
     * Creates and runs (if start on open is true) this model scape.
     * 
     * @param args
     *            paramter arguments for the scape
     * @param block
     *            should this call block or run in a new thread?
     */
    public void open(String[] args, boolean block) {
        openImplementation(args, block);
    }

    /**
     * Creates and runs (if start on open is true) this model scape.
     * 
     * @param args
     *            paramter arguments for the scape
     */
    public void open(String[] args) {
        openImplementation(args, false);
    }

    /**
     * Creates and runs (if start on open is true) this model scape.
     */
    public void open() {
        openImplementation(null, false);
    }

    /**
     * Creates and runs (if start on open is true) this model scape.
     * 
     * @param block
     *            should this call block or run in a new thread?
     */
    public void open(boolean block) {
        openImplementation(null, block);
    }

    /**
     * Requests the scape to open a saved run.
     */
    public void openSavedChoose() {
        closeAndOpenSavedFinally(null);
    }

    public abstract void closeAndOpenSavedFinally(Scape oldScape);


    public Object instanceFromName(String modelName) {
        Object newObject;
        try {
            newObject = Thread.currentThread().getContextClassLoader().loadClass(modelName).newInstance();
        } catch (NullPointerException e) {
            throw new RuntimeException("An error ocurred while attempting to read " + modelName + ": " + e.getMessage(), e);
        } catch (InstantiationException e) {
            throw new RuntimeException("An error ocurred while attempting to read " + modelName + ": " + e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("An error ocurred while attempting to read " + modelName + ": " + e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            try {
                Class c = Class.forName(modelName);
                newObject = c.newInstance();
            } catch (Exception e2) {
                throw new RuntimeException("Couldn't find class: " + modelName);
            }
        }
        return newObject;
    }

    /**
     * Constructs, creates and runs (if start on open is true) the supplied model.
     * 
     * @deprecated Applets are no longer executed this way
     * @param modelName
     *        the fully qualified name of the Java class for the model's root scape
     * @param applet
     *        the applet if are we in an applet vm context
     * @param args
     *        paramter arguments for the scape
     * @param block
     *        should this call block or run in a new thread?
     */
    public Scape open(String modelName, Object applet, String[] args, boolean block) {
        return open(modelName, args, block);
    }

    /**
     * Constructs, creates and runs the supplied model.
     * 
     * @param modelName
     *        the fully qualified name of the Java class for the model's root scape
     * @param args
     *        paramter arguments for the scape
     */
    public Scape open(String modelName, String[] args) {
        return open(modelName, args, false);
    }

    /**
     * Constructs, creates and runs the supplied model.
     * 
     * @param modelName
     *            the fully qualified name of the Java class for the model's
     *            root scape
     * @param args
     *            paramter arguments for the scape
     * @param block
     *            should this call block or run in a new thread?
     */
    public Scape open(String modelName, String[] args, boolean block) {

        Scape newAgent = (Scape) instanceFromName(modelName);
        setRootScape(newAgent);
        open(args, block);
        return getRootScape();
    }

    /**
     * Constructs, creates and runs the supplied model.
     * 
     * @deprecated Applets are no longer executed this way
     * @param modelName
     *        the fully qualified name of the Java class for the model's root scape
     * @param applet
     *        the applet if are we in an applet vm context
     */
    public Scape open(String modelName, Object applet) {
        return open(modelName, null, null, false);
    }

    /**
     * Constructs, creates and runs the supplied model.
     * 
     * @param modelName
     *            the fully qualified name of the Java class for the model's
     *            root scape
     */
    public Scape open(String modelName, boolean block) {
        return open(modelName, null, null, block);
    }

    /**
     * Constructs, creates and runs the supplied model.
     * 
     * @param modelName
     *            the fully qualified name of the Java class for the model's
     *            root scape
     */
    public Scape openInstance(String modelName) {
        return open(modelName, null, new String[0], false);
    }

    /**
     * Requests the scape to open a model, providing UI for this purpose.
     */
    public Scape openChoose() {
        return openChoose(null);
    }

    /**
     * Requests the scape to open a model, providing UI for this purpose.
     */
    public Scape openChoose(String[] args) {
        String modelName = ((AbstractUIEnvironment) environment).openDialog();
        Scape scape = null;
        if (modelName != null) {
            scape = open(modelName, args);
        }
        return scape;
    }

    /*
     * To be done (perhaps) Opens the specified scape from a class file.
     */
    /*
     * public static void open(File file) { FileInputStream f = new
     * FileInputStream(file); ObjectInputStream s = new ObjectInputStream(f);
     * try { Scape newScape = (Scape) s.readObject(); } catch
     * (ClassNotFoundException e) { JOptionPane.showMessageDialog(null, "A class
     * couldn't be found (make sure you have all the appropriate model classes
     * installed): " + e, "Error", JOptionPane.INFORMATION_MESSAGE); } }
     */

    /**
     * Save the state of the scape to a file.
     */
    public abstract void saveChoose();

    public void close() {
        closeRequested = true;
        if (running) {
            // Running, so we have to allow current iteration to finish
            stop();
        } else {
            closeFinally();
        }
    }

    /**
     * Closes the application; allowing views to close themseleves gracefully.
     * Do not call this method directly unless you want to force close; call
     * <code>close()</code> instead, allowing a running scape to stop
     * gracefully. Override this method if you want to provide any scape related
     * pre-quit finalization or clean-up.
     * 
     * @see #quit
     */
    public void closeFinally() {
        dataGroup = null;
        // Send an event to self for quit
        scape.executeOnRoot(new NotifyViews(ScapeEvent.REQUEST_CLOSE));
        waitForViewsUpdate();
    }

    /**
     * Exits the application; calling stop if running and allowing views to
     * close themseleves gracefully. Override <code>quitFinally</code> if you
     * want to provide any pre-quit finalization or clean-up.
     * 
     * @see #quitFinally
     */
    public void quit() {
        if (inMainLoop) {
            quitRequested = true;
            // Running, so we have to allow current iteration to finish
            stop();
        }
        // Only one chance a quit request, otherwise we assume that the scape is
        // allready fulfilling it.
        else if (!quitRequested) {
            quitFinally();
        }
    }

    /**
     * Exits the application; allowing views to close themseleves gracefully. Do
     * not call this method directly unless you want to force quit; call
     * <code>quit()</code> instead, allowing a running scape to stop
     * gracefully. Override this method if you want to provide any scape related
     * pre-quit finalization or clean-up.
     * 
     * @see #quit
     */
    public void quitFinally() {
        closeFinally();
        scape.executeOnRoot(new NotifyViews(ScapeEvent.REQUEST_QUIT));
        waitForViewsUpdate();
        exit();
    }

    /**
     * Final kill. Calls System exit, which appears neccessary for vm even when
     * code has finished.
     */
    public static void exit() {
        try {
            System.exit(0);
        } catch (SecurityException e) {
            System.out
            .println("Can't quit in this security context. (Scape is probably running in browser or viewer; quit or change that.)");
        }
    }

    public void createEnvironment() {
        if (environment == null) {
            environment = new RuntimeEnvironment();
        }
    }

    public static boolean isDisplayGraphics() {
        try {
            return displayGraphics && !GraphicsEnvironment.isHeadless();
        } catch (HeadlessException e) {
            return false;
        }
    }

    public static void setDisplayGraphics(boolean displayGraphics) {
        Runner.displayGraphics = displayGraphics;
    }

    public static boolean isServeGraphics() {
        return serveGraphics;
    }

    public static void setServeGraphics(boolean serveGraphics) {
        Runner.serveGraphics = serveGraphics;
    }

    public static boolean isMultiWinEnvironment() {
        return muiltWinEnvironment;
    }

    public static void setMultiWinEnvironment(boolean muiltWinEnvironment) {
        Runner.muiltWinEnvironment = muiltWinEnvironment;
    }

    /**
     * Sets the period name for the delegate
     * 
     * @return the periodName
     */
    public String getPeriodName() {
        return periodName;
    }

    /**
     * Sets periodName for the ModelRoot object.
     * 
     * @param periodName
     *            the periodName
     */
    public void setPeriodName(String periodName) {
        this.periodName = periodName;
    }

    /**
     * Gets the description for the ModelRoot object.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description for the ModelRoot object.
     * 
     * @param description
     *            the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns a brief descripiton (including credits) of the scape or of the
     * model, if this is root scape. Plaintext.
     * 
     * @return the description
     */
    public String getHTMLDescription() {
        return HTMLDescription;
    }

    /**
     * Sets an html-formatted description to be used for the model as a whole.
     * 
     * @param HTMLdescription
     *            the description
     */
    public void setHTMLDescription(String HTMLdescription) {
        this.HTMLDescription = HTMLdescription;
    }

    /**
     * Returns the period that this model started.
     * 
     * @return the startPeriod
     */
    public int getStartPeriod() {
        return startPeriod;
    }

    /**
     * Gets the startOnOpen for the Runner object. Start on Open is static
     * because we may not have a scape context.
     * 
     * @return the startOnOpen
     */
    public static boolean isStartOnOpen() {
        return startOnOpen;
    }

    /**
     * Sets startOnOpen for the ModelRoot object.
     * 
     * @param startOnOpen
     *            the startOnOpen
     */
    public static void setStartOnOpen(boolean _startOnOpen) {
        startOnOpen = _startOnOpen;
    }

    /**
     * Sets the start period for this scape. The start period is the period this
     * scape is given when a model run is started.
     * 
     * @param startPeriod
     *            the period to begin runs at
     * @throws org.ascape.model.space.SpatialTemporalException
     *             exception
     */
    public void setStartPeriod(int startPeriod) throws SpatialTemporalException {
        if (startPeriod >= earliestPeriod) {
            this.startPeriod = startPeriod;
        } else {
            throw new SpatialTemporalException("Tried to set start period before earliest period");
        }
    }

    /**
     * Returns the period this scape stops running at. By default, the lesser of
     * latest period and integer maximum value (effectively unlimited.)
     * 
     * @return the stopPeriod
     */
    public int getStopPeriod() {
        return stopPeriod;
    }

    /**
     * Sets the stop period for this scape. The stop period is the period that
     * the scape is automatically stopped at. The scape may be automatically set
     * to start agina at start value is the scape is set to restart.
     * 
     * @param stopPeriod
     *            the period the scape will stop at upon reaching
     * @see #setAutoRestart
     * @throws org.ascape.model.space.SpatialTemporalException
     *             exception
     */
    public void setStopPeriod(int stopPeriod) throws SpatialTemporalException {
        if (stopPeriod <= latestPeriod) {
            this.stopPeriod = stopPeriod;
        } else {
            throw new SpatialTemporalException("Tried to set stop period after latest period");
        }
    }

    /**
     * Gets the pausePeriod for the ModelRoot object.
     * 
     * @return the pausePeriod
     */
    public int getPausePeriod() {
        return pausePeriod;
    }

    /**
     * Sets pausePeriod for the ModelRoot object.
     * 
     * @param pausePeriod
     *            the pausePeriod
     */
    public void setPausePeriod(int pausePeriod) {
        this.pausePeriod = pausePeriod;
    }

    /**
     * Gets the earliestPeriod for the ModelRoot object.
     * 
     * @return the earliestPeriod
     */
    public int getEarliestPeriod() {
        return earliestPeriod;
    }

    /**
     * Sets the earliest period this scape is expected to be run at. 0 by
     * default.
     * 
     * @param earliestPeriod
     *            the lowest period value this scape can have
     */
    public void setEarliestPeriod(int earliestPeriod) {
        this.earliestPeriod = earliestPeriod;
        if (startPeriod < earliestPeriod) {
            try {
                setStartPeriod(earliestPeriod);
            }
            // "Can't" happen
            catch (SpatialTemporalException e) {
                throw new RuntimeException("Internal Logic Error");
            }
        }
    }

    /**
     * Gets the latestPeriod for the ModelRoot object.
     * 
     * @return the latestPeriod
     */
    public int getLatestPeriod() {
        return latestPeriod;
    }

    /**
     * Sets the latest period this scape is expected to be run at. Max of
     * integer (effectively unlimited) by default.
     * 
     * @param latestPeriod
     *            the highest period value this scape can have
     */
    public void setLatestPeriod(int latestPeriod) {
        this.latestPeriod = latestPeriod;
        if (stopPeriod > latestPeriod) {
            try {
                setStopPeriod(latestPeriod);
            }
            // "Can't" happen
            catch (SpatialTemporalException e) {
                throw new RuntimeException("Internal Logic Error");
            }
        }
    }

    /**
     * Gets the restartingViews for the ModelRoot object.
     * 
     * @return the restartingViews
     */
    public List getRestartingViews() {
        return restartingViews;
    }

    /**
     * Sets restartingViews for the ModelRoot object.
     * 
     * @param restartingViews
     *            the restartingViews
     */
    public void setRestartingViews(List restartingViews) {
        this.restartingViews = restartingViews;
    }

    /**
     * Gets the AutoRestart for the ModelRoot object.
     * 
     * @return the Restart state
     */
    public boolean isAutoRestart() {
        return autoRestart;
    }

    /**
     * Sets Restart for the ModelRoot object.
     * 
     * @param autoRestart
     *            should the model restart when it ends?
     */
    public void setAutoRestart(boolean autoRestart) {
        this.autoRestart = autoRestart;
    }

    /**
     * Is the supplied period a valid period for this scape?
     * 
     * @param period
     *            the period to test
     * @return true if within earliest and latest periods, false otherwise
     */
    public boolean isValidPeriod(int period) {
        return period >= earliestPeriod && period <= latestPeriod;
    }

    /**
     * Returns the path in which all files should by default be stored to and
     * retrieved from. Nonstatic, so that parameter can automatically be set
     * from command line, but backing variable is static. Default is "./", can
     * be modified by calling setHome or providing an ascape.home java property.
     * (This may change now since it is no longer neccesary.)
     * 
     * @return the home
     */
    public String getHome() {
        if (home == null) {
            home = "./";
            try {
                home = System.getProperty("ascape.home", home);
            }
            // Ignore security exception; we may be running in an environment
            // such as an applet where we can't get ascape home
            // no probel,m we just need to make sure that lib, etc. are in the
            // right place.
            catch (SecurityException e) {
            }
        }
        return home;
    }

    /**
     * Sets the path in which to store all scape related files. Nonstatic, so
     * that parameter can automatically be set from command line, but backing
     * variable is static.
     * 
     * @param home
     *            the home
     */
    public void setHome(String home) {
        this.home = home;
    }

    public RuntimeEnvironment getEnvironment() {
        return environment;
    }

    public boolean isBeginningDeserializedRun() {
        return beginningDeserializedRun;
    }

    public void setBeginningDeserializedRun(boolean beginningDeserializedRun) {
        this.beginningDeserializedRun = beginningDeserializedRun;
    }

    public boolean isCloseAndOpenNewRequested() {
        return closeAndOpenNewRequested;
    }

    public void setCloseAndOpenNewRequested(boolean closeAndOpenNewRequested) {
        this.closeAndOpenNewRequested = closeAndOpenNewRequested;
    }

    public boolean isCloseAndOpenSavedRequested() {
        return closeAndOpenSavedRequested;
    }

    public void setCloseAndOpenSavedRequested(boolean closeAndOpenSavedRequested) {
        this.closeAndOpenSavedRequested = closeAndOpenSavedRequested;
    }

    public boolean isCloseRequested() {
        return closeRequested;
    }

    public void setCloseRequested(boolean closeRequested) {
        this.closeRequested = closeRequested;
    }

    public boolean isInMainLoop() {
        return inMainLoop;
    }

    public void setInMainLoop(boolean inMainLoop) {
        this.inMainLoop = inMainLoop;
    }

    public int getIteration() {
        return iteration;
    }

    public void setIteration(int iteration) {
        this.iteration = iteration;
    }

    public boolean isOpenRequested() {
        return openRequested;
    }

    public void setOpenRequested(boolean openRequested) {
        this.openRequested = openRequested;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public boolean isQuitRequested() {
        return quitRequested;
    }

    public void setQuitRequested(boolean quitRequested) {
        this.quitRequested = quitRequested;
    }

    public boolean isRestartRequested() {
        return restartRequested;
    }

    public void setRestartRequested(boolean restartRequested) {
        this.restartRequested = restartRequested;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        if (running) {
            start();
        } else {
            stop();
        }
    }

    public void setInternalRunning(boolean running) {
        this.running = running;
    }

    public boolean isSaveRequested() {
        return saveRequested;
    }

    public void setSaveRequested(boolean saveRequested) {
        this.saveRequested = saveRequested;
    }

    public boolean isStep() {
        return step;
    }

    public void setStep(boolean step) {
        this.step = step;
    }

    public DataGroup getData() {
        return dataGroup;
    }

    public void setRootScape(Scape scape) {
        this.scape = scape;
        scape.setRunner(this);
        dataGroup = new DataGroup();
        dataGroup.setScape(scape);
    }

    public Scape getRootScape() {
        return scape;
    }

    public void setEnvironment(RuntimeEnvironment environment) {
        this.environment = environment;
    }

    public Thread getModelThread() {
        return modelThread;
    }
}
