package org.aiwolf.gt;

import org.aiwolf.common.data.Species;

public class GTState {
	Species divination;	// what they divined someone to be
	Boolean suspect; 	// True if susWolves is not empty (you have something to announce) (announce/vote)

	public GTState(Species divination, Boolean suspect) {
		this.divination = divination;
		this.suspect = suspect;
	}
}
