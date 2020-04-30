package org.aiwolf.gt;

import org.aiwolf.sample.lib.AbstractRoleAssignPlayer;
import org.aiwolf.gt.GTBodyguard;
import org.aiwolf.gt.GTMedium;
import org.aiwolf.gt.GTPossessed;
import org.aiwolf.gt.GTSeer;
import org.aiwolf.gt.GTVillager;
import org.aiwolf.gt.GTWerewolf;

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
		return "GT-wolf";
	}

}
