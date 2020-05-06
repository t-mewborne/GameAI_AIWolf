package org.aiwolf.gtWolf;

import org.aiwolf.common.data.Species;

public class GTState {
	Species divination;	// what they divined someone to be (Human if divined is false)
	Boolean suspect; 	// True if susWolves is not empty (you have something to announce) (announce/vote)
	Boolean divined;	// True if you divined something
	Boolean cameOut;	// True if you came out
	Boolean earlyDays;

	public GTState(Species divination, Boolean suspect, Boolean divined, Boolean cameOut, Boolean earlyDays) {
		this.divination = divination;
		this.suspect = suspect;
		this.divined = divined;
		this.cameOut = cameOut;
		this.earlyDays = earlyDays;
	}
	
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        int divInt;
        int susInt;
        int divBoolInt;
        int cameInt;
        int dayInt;
        if (divination==Species.HUMAN) 
        	divInt = 1;
        else 
        	divInt = 0;
        if (suspect)
        	susInt = 1;
        else 
        	susInt = 0;
        if (divined)
        	divBoolInt = 1;
        else 
        	divBoolInt = 0;
        if (cameOut)
        	cameInt = 1;
        else 
        	cameInt = 0;
        if (earlyDays)
        	dayInt = 1;
        else 
        	dayInt = 0;
        result = prime * result + divInt;
        result = prime * result + susInt;
        result = prime * result + divBoolInt;
        result = prime * result + cameInt;
        result = prime * result + dayInt;
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof GTState)) {
            return false;
        }
        GTState other = (GTState) obj;
        if (divination != other.divination) {
            return false;
        }
        if (suspect != other.suspect) {
            return false;
        }
        if (divined != other.divined) {
            return false;
        }
        if (cameOut != other.cameOut) {
            return false;
        }
        if (earlyDays != other.earlyDays) {
        	return false;
        }
        return true;
    }
}
