/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */

package org.ascape.runtime;

import java.util.ArrayList;
import java.util.TooManyListenersException;

import org.ascape.model.Scape;
import org.ascape.model.event.DefaultScapeListener;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.event.ScapeListener;
import org.ascape.view.nonvis.ConsoleOutView;

/**
 * Supports all non-ui shared aspects of Ascape runtime environment, including
 * console view, managing environment views, etc..
 * 
 * @version 3.0
 * @history 3.0 8/1/02 first in
 * @since 3.0
 */
public class RuntimeEnvironment extends DefaultScapeListener {

    /**
     * 
     */
    private static final long serialVersionUID = -4376665907245552553L;

    /**
     * Symbol for indicating that the scape should be stopped automatically
     * (default) A view that can report results to the console. Every model has
     * one by default.
     */
    private ConsoleOutView consoleOutView;

    /**
     * The environment views.
     */
    transient ArrayList environmentViews = new ArrayList();

    /**
     * Instantiates a new runtime environment.
     */
    public RuntimeEnvironment() {
        setConsole(new ConsoleOutView());
        showConsoleNotice();
    }

    /**
     * Adds the view.
     * 
     * @param view
     *            the view
     * @param createFrame
     *            the create frame
     */
    public void addView(ScapeListener view, boolean createFrame) {
        if (view.getScape() == null) {
            if (!view.isLifeOfScape()) {
                // Add to exisitng scape, if any
                if (getScape() != null) {
                    getScape().addView(view, false);
                }
                // else no problem, any scape that gets added will automatically
                // have this view added to it
            } else {
                // throw new Error("Tried to add a scape specific view directly
                // to the user environment. Use scape.addView() instead.");
            }
        }
        if (!view.isLifeOfScape()) {
            environmentViews.add(view);
        }
    }

    /**
     * Adds the view.
     * 
     * @param view
     *            the view
     */
    public void addView(ScapeListener view) {
        addView(view, true);
    }

    /**
     * Adds the views.
     * 
     * @param views
     *            the views
     */
    public void addViews(ScapeListener[] views) {
        addViews(views, true);
    }

    /**
     * Adds the views.
     * 
     * @param views
     *            the views
     * @param createFrame
     *            the create frame
     */
    public void addViews(ScapeListener[] views, boolean createFrame) {
        // Code assumes that if one view hasn't been added, none of them have
        ScapeListener[] componentViews = new ScapeListener[views.length];
        System.arraycopy(views, 0, componentViews, 0, componentViews.length);
        for (int i = 0; i < componentViews.length; i++) {
            ScapeListener componentView = componentViews[i];
            if (!componentView.isLifeOfScape()) {
                environmentViews.add(componentView);
            }
            if (componentView.getScape() == null) {
                if (!componentView.isLifeOfScape()) {
                    // Add to exisitng scape, if any
                    if (getScape() != null) {
                        getScape().addView(componentView, false);
                    }
                    // else no problem, any scape that gets added will
                    // automatically have this view added to it
                } else {
                    throw new Error("Tried to add a scape specific view directly to the user environment. Use scape.addView() instead.");
                }
            }
        }
    }

    /**
     * Removes the view (if it is an environment view).
     * 
     * @param view
     *            the view
     */
    public void removeView(ScapeListener view) {
        if (!view.isLifeOfScape()) {
            environmentViews.remove(view);
        }
    }

    /**
     * Removes the views.
     * 
     * @param views
     *            the views
     */
    public void removeViews(ScapeListener[] views) {
        for (int i = 0; i < views.length; i++) {
            ScapeListener view = views[i];
            removeView(view);
        }
    }

    /**
     * Setup up a basic interactive controlBar time userEnvironment for a model
     * application.
     * 
     * @param scapeEvent
     *            the scape event
     */
    public void environmentQuiting(ScapeEvent scapeEvent) {
        scape = null;
    }

	/**
	 * Returns {@code true} if the environment will allow us to quit in response
	 * to user initiated quit.
	 * 
	 * @return {@code true} if we can quit.
	 */
	public boolean canQuit() {
		return true;
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
    public void quit() {
        if (getScape() != null) {
            getScape().getRunner().quit();
        } else {
            for (int i = 0; i < environmentViews.size(); i++) {
                ScapeListener scapeListener = (ScapeListener) environmentViews.get(i);
                scapeListener.environmentQuiting(new ScapeEvent(this, ScapeEvent.REQUEST_QUIT));
            }
            RuntimeEnvironment.exit();
        }
    }

    /**
     * Final kill. Calls System exit, which appears neccessary for vm even when
     * code has finished.
     */
    public static void exit() {
        try {
            System.exit(0);
        } catch (SecurityException e) {
            System.out.println("Can't quit in this security context. (Scape is probably running in browser or viewer; quit or change that.)");
        }
    }

    /**
     * When a scape is added add any persiten views to it and set frame title.
     * 
     * @param scapeEvent
     *            the scape event
     * @throws TooManyListenersException
     *             the too many listeners exception
     */
    public void scapeAdded(ScapeEvent scapeEvent) throws TooManyListenersException {
        scape = (Scape) scapeEvent.getSource();
        for (int i = 0; i < environmentViews.size(); i++) {
            ScapeListener scapeListener = (ScapeListener) environmentViews.get(i);
            ((Scape) scapeEvent.getSource()).addView(scapeListener, false);
        }
        getConsole().setName(scape + " Standard (Text) Output");
        ((Scape) scapeEvent.getSource()).addView(getConsole());
    }

    /**
     * Gets the consoleOutView for the ModelRoot object.
     * 
     * @return the consoleOutView
     */
    public ConsoleOutView getConsole() {
        return consoleOutView;
    }

    /**
     * Sets consoleOutView for the ModelRoot object.
     * 
     * @param consoleOutView
     *            the consoleOutView
     */
    public void setConsole(ConsoleOutView consoleOutView) {
        this.consoleOutView = consoleOutView;
    }

    /**
     * Gets the environment views.
     * 
     * @return the environment views
     */
    public ArrayList getEnvironmentViews() {
        return environmentViews;
    }

    /**
     * Displays a standard splash screen message asserting copyright and other
     * information.
     */
    private void showConsoleNotice() {
        // Print standard copyright disclaimer..
        getConsole().println();
        getConsole().println("Ascape");
        getConsole().println("An agent-based modeling framework and runtime environment.");
        getConsole().println("Design and development: Miles T. Parker");
        getConsole().println("Additional development: Mario Inchiosa, Josh Miller and others");
        getConsole().println();
        getConsole().println("Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.");
        getConsole().println("All rights reserved.");
        getConsole().println("This program and the accompanying materials are made available solely under");
        getConsole().println("the incliude BSD license 'ascape-license.txt'.");
        getConsole().println("Any referenced or included libraries carry licenses of their respective copyright holders.");
        getConsole().println();
        getConsole().println("THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS");
        getConsole().println("'AS IS' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT");
        getConsole().println("LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR");
        getConsole().println("A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR");
        getConsole().println("CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,");
        getConsole().println("EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,");
        getConsole().println("PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR");
        getConsole().println("PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF");
        getConsole().println("LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING");
        getConsole().println("NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS");
        getConsole().println("SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. ");
        getConsole().println();
    }
}
