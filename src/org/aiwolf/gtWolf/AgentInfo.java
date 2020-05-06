package org.aiwolf.gtWolf;

import org.aiwolf.common.data.Role;

public class AgentInfo {
	boolean alive;
	int index;
	Role role;
	Role COrole = null;
	int state;
	int voteFor;
	int NvotedBy;
	int wincnt;
	AgentInfo() {
		state = -1;
		alive = true;
		wincnt = 0;
	}
}
