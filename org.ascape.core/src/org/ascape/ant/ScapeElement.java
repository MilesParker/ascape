/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package org.ascape.ant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.ascape.model.Scape;
import org.ascape.model.event.DefaultScapeListener;
import org.ascape.model.event.ScapeEvent;
import org.ascape.runtime.NonGraphicRunner;
import org.ascape.runtime.Runner;
import org.ascape.util.Conditional;
import org.ascape.view.nonvis.ScapeFromXMLView;

class FindByName implements Conditional {

	/**
     * 
     */
    private static final long serialVersionUID = 4842463839365436663L;
    String name;

	public FindByName(String name) {
		this.name = name;
	}

	public boolean meetsCondition(Object object) {
		return ((Scape) object).getName().equals(name);
	}
}

/**
 * The Class ScapeElement.
 */
public class ScapeElement implements Serializable {

	/**
     * 
     */
    private static final long serialVersionUID = -9010195028864059228L;

    /**
	 * The scape.
	 */
	private Scape scape;

	/**
	 * The parent.
	 */
	ScapeElement parent;

	/**
	 * The member.
	 */
	MembersElement member;

	/**
	 * The member name.
	 */
	private String memberName;

	/**
	 * The input file name.
	 */
	private String inputFileName;

	/**
	 * The model parameters.
	 */
	private ParameterSet modelParameters;

	/**
	 * The model rules.
	 */
	private SelectionSet modelRules;

	/**
	 * The views.
	 */
	private List<ViewElement> views;

	/**
	 * The extent.
	 */
	CoordinateElement extent;

	/**
	 * The stop view.
	 */
	private CheckStopListener stopView;

	/**
	 * Instantiates a new scape element.
	 */
	public ScapeElement() {
		views = new ArrayList<ViewElement>();
	}

	/**
	 * Adds the members.
	 * 
	 * @param member
	 *            the member
	 */
	public void addMembers(MembersElement member) {
		if (this.member == null) {
			this.member = member;
			member.parent = this;
		} else {
			throw new BuildException(
					"Tried to add more than one member element: " + member);
		}
	}

	/**
	 * Adds the property set.
	 * 
	 * @param set
	 *            the set
	 * @throws BuildException
	 *             the build exception
	 */
	public void addPropertySet(ParameterSet set) throws BuildException {
		modelParameters = set;
	}

	/**
	 * Adds the rule set.
	 * 
	 * @param set
	 *            the set
	 * @throws BuildException
	 *             the build exception
	 */
	public void addRuleSet(SelectionSet set) throws BuildException {
		modelRules = set;
	}

	/**
	 * Adds the sweep view.
	 * 
	 * @param viewElement
	 *            the view element
	 * @throws BuildException
	 *             the build exception
	 */
	public void addSweepView(SweepViewElement viewElement)
			throws BuildException {
		views.add(viewElement);
	}

	/**
	 * Adds the data output view.
	 * 
	 * @param view
	 *            the view
	 */
	public void addDataOutputView(DataOutputViewElement view) {
		views.add(view);
	}

	/**
	 * Adds the all output view.
	 * 
	 * @param view
	 *            the view
	 */
	public void addAllOutputView(AllOutputViewElement view) {
		views.add(view);
	}

	/**
	 * Adds the scape output view.
	 * 
	 * @param view
	 *            the view
	 */
	public void addScapeOutputView(ScapeOutputViewElement view) {
		views.add(view);
	}

	/**
	 * Adds the extent.
	 * 
	 * @param coordinate
	 *            the coordinate
	 */
	public void addExtent(CoordinateElement coordinate) {
		if (this.extent != null) {
			throw new BuildException(
					"Tried to specify more than one extenet value.");
		}
		this.extent = coordinate;
	}

	/**
	 * Sets the model class.
	 * 
	 * @param modelName
	 *            the new model class
	 */
	public void setModelClass(String modelName) {
		try {
			Class c = Class.forName(modelName);
			try {
				scape = (Scape) c.newInstance();
			} catch (InstantiationException e) {
				throw new RuntimeException("The class \"" + modelName
						+ " could not be instantiated", e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException("The class \"" + modelName
						+ " could not be accessed", e);
			}
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("The class \"" + modelName
					+ " could not be found.");
		}
	}

	/**
	 * Sets the member name.
	 * 
	 * @param memberName
	 *            the new member name
	 */
	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	/**
	 * Sets the input file name.
	 * 
	 * @param inputFileName
	 *            the new input file name
	 */
	public void setInputFileName(String inputFileName) {
		this.inputFileName = inputFileName;
	}

	/**
	 * Creates the views.
	 */
	private void createViews() {
		for (Iterator iterator = views.iterator(); iterator.hasNext();) {
			((ViewElement) iterator.next()).addToScape(scape);
		}
	}

	/**
	 * Gets the scape.
	 * 
	 * @return the scape
	 */
	public Scape getScape() {
		return scape;
	}

	/**
	 * Load.
	 */
	public void load() {
		if (scape == null) {
			if (parent != null) {
				Iterator iterator = parent.getScape().getSpace()
						.conditionalIterator(new FindByName(memberName));
				if (iterator.hasNext()) {
					scape = (Scape) iterator.next();
				} else {
					throw new BuildException(
							"Couldn't find scapeElement element for " + parent
									+ ": " + memberName);
				}
			} else {
				throw new InternalError("Unexpected State");
			}
		}
//		if (Runner.isDisplayGraphics()) {
//	        if (isDisplayGraphics()) {
//	            runner.new DesktopEnvironment();
//	        }
//		} else {
//		    
//		}
		if (extent != null) {
			scape.setExtent(extent.determineCoordinate());
			scape.createScape();
		}
		if (modelRules != null) {
			modelRules.apply(scape.getRules());
		}
		if (modelParameters != null) {
			scape.assignParameters(modelParameters.asArgs());
		}

		createViews();
		// if the run.xml file specifies an input file for this scape, then read
		// from the file and use it to
		// populate the scape.
		if (inputFileName != null) { // if a file is specified
			ScapeFromXMLView scapeFromXMLView = new ScapeFromXMLView();
			scapeFromXMLView.parseFile(inputFileName);
			getScape().addView(scapeFromXMLView);

		}

		if (member != null) {
			for (Iterator iterator = member.getSubScapes().iterator(); iterator
					.hasNext();) {
				ScapeElement scapeElement = (ScapeElement) iterator.next();
				scapeElement.load();
			}
		}
	}

	/**
	 * Run.
	 */
	public void run() {
		open();
		// should always be the last view so that restarts have already been
		// requested from control views
		stopView = new CheckStopListener();
		scape.addView(stopView);
		scape.getRunner().start();
		while (!stopView.stopped) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace(); // To change body of catch statement use
				// Options | File Templates.
			}
		}
	}

	/**
	 * Open.
	 */
	public void open() {
        if (scape.getRunner() == null) {
            (new NonGraphicRunner()).setRootScape(scape);
        }
		Runner.setStartOnOpen(false);
		if (modelParameters != null) {
			scape.getRunner().open(modelParameters.asArgs(), true);
		} else {
			scape.getRunner().open(true);
		}
		load();
	}

	/**
	 * Gets the views.
	 * 
	 * @return the views
	 */
	public List getViews() {
		return views;
	}

	/**
	 * The listener interface for receiving checkStop events. The class that is
	 * interested in processing a checkStop event implements this interface, and
	 * the object created with that class is registered with a component using
	 * the component's <code>addCheckStopListener<code> method. When
	 * the checkStop event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @see CheckStopEvent
	 */
	private static class CheckStopListener extends DefaultScapeListener {

		/**
         * 
         */
        private static final long serialVersionUID = 1L;
        /**
		 * The stopped.
		 */
		boolean stopped;

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.ascape.model.event.DefaultScapeListener#scapeClosing(org.ascape.model.event.ScapeEvent)
		 */
		public void scapeClosing(ScapeEvent scapeEvent) {
			if (!((Scape) scapeEvent.getSource()).getRunner()
					.isRestartRequested()) {
				stopped = true;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.ascape.model.event.DefaultScapeListener#scapeStopped(org.ascape.model.event.ScapeEvent)
		 */
		public void scapeStopped(ScapeEvent scapeEvent) {
			if (!Runner.isServeGraphics()
					&& !((Scape) scapeEvent.getSource()).getRunner()
							.isRestartRequested()) {
				stopped = true;
			}
		}
	}

	/**
	 * Gets the model parameters.
	 * 
	 * @return the model parameters
	 */
	public ParameterSet getModelParameters() {
		return modelParameters;
	}

    protected void setViews(List views) {
        this.views = views;
    }
}
