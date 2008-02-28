/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.ant.swing;

import org.apache.tools.ant.BuildException;
import org.ascape.ant.AntViewElement;
import org.ascape.runtime.Runner;
import org.ascape.runtime.swing.SwingRunner;

/**
 * The Class ScapeElement.
 */
public class ScapeSwingElement extends org.ascape.ant.ScapeElement {

	/**
     * 
     */
    private static final long serialVersionUID = 2506265203872809183L;

    @Override
    public void open() {
        if (getScape().getRunner() == null) {
            if (Runner.isDisplayGraphics()) {
                (new SwingRunner()).setRootScape(getScape());
            }
        }
        super.open();
    }

    /**
	 * Adds the chart view.
	 * 
	 * @param view
	 *            the view
	 * @throws BuildException
	 *             the build exception
	 */
	public void addChartView(AntChartViewElement view) throws BuildException {
		getViews().add(view);
	}

	/**
	 * Adds the view.
	 * 
	 * @param viewElement
	 *            the view element
	 * @throws BuildException
	 *             the build exception
	 */
	public void addView(AntViewElement viewElement) throws BuildException {
	    getViews().add(viewElement);
	}

	/**
	 * Adds the agent view.
	 * 
	 * @param viewElement
	 *            the view element
	 * @throws BuildException
	 *             the build exception
	 */
	public void addAgentView(AntAgentViewElement viewElement)
			throws BuildException {
	    getViews().add(viewElement);
	}
}
