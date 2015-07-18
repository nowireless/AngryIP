package org.nowireless.ip;

import net.azib.ipscan.core.state.StateMachine;

public class DummyStateMachine extends StateMachine {

	
	@Override
	protected void notifyAboutTransition(Transition transition) {
		//System.out.println("State Transition " + transition.name());
		super.notifyAboutTransition(transition);
	}
}
