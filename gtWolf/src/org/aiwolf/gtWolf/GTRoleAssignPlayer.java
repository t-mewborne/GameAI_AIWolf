package org.aiwolf.gtWolf;

import org.aiwolf.sample.lib.AbstractRoleAssignPlayer;

public class GTRoleAssignPlayer extends AbstractRoleAssignPlayer {

	
	public GTRoleAssignPlayer() {
		
		setVillagerPlayer(new GTVillager());
		setBodyguardPlayer(new GTBodyguard());
		setMediumPlayer(new GTMedium());
		setSeerPlayer(new GTSeer());
		setPossessedPlayer(new GTPossessed());
		setWerewolfPlayer(new GTWerewolf());
	}
	
	@Override
	public String getName() {
		return "GTWolf";
	}

}
