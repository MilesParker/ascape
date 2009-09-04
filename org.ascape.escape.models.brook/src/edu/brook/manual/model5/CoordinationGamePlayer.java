/*
 * Copyright 1998-2007 The Brookings Institution, with revisions by Metascape LLC, and others. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */
package edu.brook.manual.model5;

import java.util.ArrayList;
import java.util.List;

import org.ascape.model.Agent;
import org.ascape.model.CellOccupant;
import org.ascape.model.rule.Rule;
import org.ascape.util.vis.ColorFeature;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.swt.graphics.Color;


public class CoordinationGamePlayer extends CellOccupant implements IColorProvider {

	/**
     * 
     */
	private static final long serialVersionUID = 6815417614812196706L;

	// cgplayer variables
	protected Color myColor;

	protected int totalScore;

	protected int[] recentPlays;

	protected int count = 0;

	protected int totalreds;

	protected int totalblues;

	protected List<CoordinationGamePlayer> friends;

	// agent initialization method
	public void initialize() {
		doRandom();
		recentPlays = new int[5];
		friends = new ArrayList<CoordinationGamePlayer>();

	}

	// add rules to the scape
	public void scapeCreated() {
		getScape().addInitialRule(MOVE_RANDOM_LOCATION_RULE);
		getScape().addInitialRule(new Rule("SetNewNetwork") {
			/**
             * 
             */
			private static final long serialVersionUID = -1127496411806208606L;

			public void execute(Agent agent) {
				((CoordinationGamePlayer) agent).setNewNetwork();
			}
		});
		getScape().addRule(RANDOM_WALK_RULE);
		getScape().addRule(UPDATE_RULE);
		getScape().addRule(PLAY_RANDOM_NEIGHBOR_RULE);
	}

	public void doRandom() {
		if (randomInRange(0, 1) > 0) {
			myColor = ColorFeature.RED;
		} else {
			myColor = ColorFeature.BLUE;
		}
	}

	// imitation rule
	public void update() {
		count++;
		if (count > recentPlays.length) {

			if (randomInRange(0, 100) >= ((CoordinationGame) getRoot()).error) {
				int currScore = 0;
				int bestScore = this.totalScore;
				// CellOccupant[] neighbors;
				// neighbors = getHostCell().getNeighboringOccupants();
				// New Social Network Code
				List neighbors = getNetwork();
				for (Object agent : neighbors) {
					currScore = ((CoordinationGamePlayer) agent).totalScore;
					if (currScore > bestScore) {
						bestScore = currScore;
						myColor = ((CoordinationGamePlayer) agent).getBackground(agent);
					}
				}
			} else {
				doRandom();
			}

		}
	}

	// play rule
	public void play(Agent partner) {
		int score;
		if ((((CoordinationGamePlayer) partner).getBackground(partner) == myColor)
				&& (myColor == ColorFeature.BLUE)) {
			score = ((CoordinationGame) getRoot()).getBlueScore();
		} else if ((((CoordinationGamePlayer) partner).getBackground(partner) == myColor)
				&& (myColor == ColorFeature.RED)) {
			score = ((CoordinationGame) getRoot()).getRedScore();
		} else {
			score = 0;
		}
		count = count + 1;
		updateRecentPlays(score);
	}

	// keep track of the recent scores
	public void updateRecentPlays(int score) {
		totalScore = 0;
		for (int i = 0; i < (recentPlays.length - 1); i++) {
			recentPlays[i] = recentPlays[i + 1];
			totalScore = totalScore + recentPlays[i];
		}
		recentPlays[recentPlays.length - 1] = score;
		totalScore = totalScore + recentPlays[recentPlays.length - 1];
	}

	// create friends networks at model setup
	public void setNewNetwork() {
		friends.clear();
		for (int i = 0; i < ((CoordinationGame) getRoot()).friends_size; i++) {
			friends.add((CoordinationGamePlayer) ((CoordinationGame) getRoot())
					.getPlayers().findRandom());
		}
		this.setNetwork(friends);
	}

	// cgplayer get() and set() methods
	public Color getBackground(Object object) {
		return myColor;
	}

	public Color getForeground(Object object) {
		return getBackground(object);
	}

	public void setColor(Color ncolor) {
		myColor = ncolor;
	}

	public int getRunningTotal() {
		return (totalScore * 10);
	}

}
