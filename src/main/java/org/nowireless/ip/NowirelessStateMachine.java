package org.nowireless.ip;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.azib.ipscan.core.state.StateMachine;

public class NowirelessStateMachine extends StateMachine {
	
	Logger log = LogManager.getLogger();
	
	@Override
	protected void notifyAboutTransition(Transition transition) {
		log.info("State Transition: {}", transition);
		super.notifyAboutTransition(transition);
	}
}
