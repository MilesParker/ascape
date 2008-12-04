/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.classes;

import org.ascape.model.Agent;
import org.ascape.model.Scape;
import org.ascape.model.rule.Rule;
import org.ascape.model.space.Array1D;
import org.ascape.model.space.Coordinate1DDiscrete;
import org.ascape.util.vis.SimplexFeature;
import org.ascape.view.vis.SimplexView;

public class BargainingModelBase extends Scape {

	/**
     * 
     */
	private static final long serialVersionUID = 603123771815863548L;

	private int population = 100;

	protected int minMemorySize = 20;

	protected int maxMemorySize = 100;

	protected float randomStrategyProbability = .1f;

	protected Scape agents;

	public void createScape() {
		super.createScape();
		agents = new Scape(new Array1D());
		agents.setExtent(new Coordinate1DDiscrete(population));
		agents.setPrototypeAgent(new Bargainer());
		agents.addRule(PLAY_OTHER);
		agents.setExecutionStyle(REPEATED_DRAW);
		agents.setAgentsPerIteration(population / 2);
		add(agents);
	}

	@SuppressWarnings("unused")
	private boolean allMedium;

	Rule checkAllMedium = new Rule("Check All Medium") {
		/**
         * 
         */
		private static final long serialVersionUID = 7273741592235908013L;

		public void execute(Agent a) {
			if (((Bargainer) a).lastStrategy != Strategy.MEDIUM_STRATEGY) {
				allMedium = false;
			}
		}

		@SuppressWarnings("unused")
		public boolean isRandom() {
			return false;
		}
	};

	SimplexView intraMemoryView;

	private static float lowPayoff = 0.30f;

	private static float mediumPayoff = 0.50f;

	private static float highPayoff = 1.0f - lowPayoff;

	public void createViews() {
		/*
		 * agents.addView(new ConsoleOutView() { public void
		 * updateScapeGraphics() { int iter = scape.getIteration(); if (((float)
		 * ( iter) / 1000) == (iter / 1000)) { System.out.println(iter); }
		 * allMedium = true; agents.executeOnMembers(checkAllMedium); if
		 * (allMedium) { System.out.println("Medium at iteration "+iter);
		 * stop(); } } });
		 */
		super.createViews();
		intraMemoryView = new SimplexView("Intragroup Memory",
				Strategy.LOW_STRATEGY.getDemand());
		intraMemoryView.setSimplexFeature(new SimplexFeature("Memory") {
			public float getAxis1Value(Object object) {
				return (float) ((Bargainer) object).countIntraHigh()
						/ (float) ((Bargainer) object).countIntra();
			}

			public String getAxis1Name() {
				return "high";
			}

			public float getAxis2Value(Object object) {
				return (float) ((Bargainer) object).countIntraMedium()
						/ (float) ((Bargainer) object).countIntra();
			}

			public String getAxis2Name() {
				return "medium";
			}

			public float getAxis3Value(Object object) {
				return (float) ((Bargainer) object).countIntraLow()
						/ (float) ((Bargainer) object).countIntra();
			}

			public String getAxis3Name() {
				return "low";
			}
		});

		/*
		 * final StatCollector[] stats = { new StatCollector("All"), new
		 * StatCollectorCond("Low") { public boolean meetsCondition(Object
		 * object) { return (((Bargainer) object).getLastStrategy() ==
		 * Strategy.LOW_STRATEGY); } }, new StatCollectorCond("Medium") { public
		 * boolean meetsCondition(Object object) { return (((Bargainer)
		 * object).getLastStrategy() == Strategy.MEDIUM_STRATEGY); } }, new
		 * StatCollectorCond("Hi") { public boolean meetsCondition(Object
		 * object) { return (((Bargainer) object).getLastStrategy() ==
		 * Strategy.HIGH_STRATEGY); } } }; agents.addStatCollectors(stats);
		 * 
		 * //Create a new chart ChartView chart = new ChartView(); //Add the
		 * chart view agents.addView(chart); chart.addSeries("Count Low",
		 * Color.blue); chart.addSeries("Count Medium", Color.green);
		 * chart.addSeries("Count Hi", Color.red);
		 */
	}

	public float getRandomStrategyProbability() {
		return randomStrategyProbability;
	}

	public void setRandomStrategyProbability(float randomStrategyProbability) {
		this.randomStrategyProbability = randomStrategyProbability;
	}

	public int getMinimumMemorySize() {
		return minMemorySize;
	}

	public void setMinimumMemorySize(int minMemorySize) {
		this.minMemorySize = minMemorySize;
	}

	public int getMaximumMemorySize() {
		return maxMemorySize;
	}

	public void setMaximumMemorySize(int maxMemorySize) {
		this.maxMemorySize = maxMemorySize;
	}

	public static float getLowPayoffStatic() {
		return lowPayoff;
	}

	public float getLowPayoff() {
		return lowPayoff;
	}

	public void setLowPayoff(float lowPayoff) {
		BargainingModelBase.lowPayoff = lowPayoff;
	}

	public static float getMediumPayoffStatic() {
		return mediumPayoff;
	}

	public float getMediumPayoff() {
		return mediumPayoff;
	}

	public void setMediumPayoff(float mediumPayoff) {
		BargainingModelBase.mediumPayoff = mediumPayoff;
	}

	public static float getHighPayoffStatic() {
		return highPayoff;
	}

	public float getHighPayoff() {
		return highPayoff;
	}

	public void setHighPayoff(float highPayoff) {
		BargainingModelBase.highPayoff = highPayoff;
	}
}
