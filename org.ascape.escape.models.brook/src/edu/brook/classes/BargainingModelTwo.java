/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */

package edu.brook.classes;

import org.ascape.view.vis.SimplexView;

public class BargainingModelTwo extends BargainingModelBase {

	/**
     * 
     */
	private static final long serialVersionUID = -4439696528099816913L;

	public void createScape() {
		super.createScape();
		agents.setPrototypeAgent(new BargainerTagged());
	}

	private SimplexView interMemoryView;

	public void createGraphicViews() {
		super.createGraphicViews();
		interMemoryView = new SimplexView();
		agents.addView(interMemoryView);
	interMemoryView.setCentroidTradeoff(Strategy.LOW_STRATEGY.getDemand());
		interMemoryView.setSimplexFeature(new InterSimplexFeature("Memory"));		}

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
}
