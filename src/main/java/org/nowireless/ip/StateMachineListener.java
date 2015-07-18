package org.nowireless.ip;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.azib.ipscan.core.ScanningResult;
import net.azib.ipscan.core.ScanningResult.ResultType;
import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.core.state.ScanningState;
import net.azib.ipscan.core.state.StateMachine;
import net.azib.ipscan.core.state.StateMachine.Transition;
import net.azib.ipscan.core.state.StateTransitionListener;

public class StateMachineListener implements StateTransitionListener {

	private final Logger log = LogManager.getLogger();
	
	private ScanningResultList list;
	
	public StateMachineListener(ScanningResultList list, StateMachine stateMachine) {
		this.list = list;
		stateMachine.addTransitionListener(this);
	}
	
	@Override
	public void transitionTo(ScanningState state, Transition transition) {
		if(state == ScanningState.IDLE) {
			if(list.areResultsAvailable()) {
				List<ScanningResult> alive = new ArrayList<>();
				for(ScanningResult result : list) {
					if(ResultType.ALIVE.equals(result.getType())) {
						alive.add(result);
					}
				}
				
				log.info("Size {}", list.size());
				for(ScanningResult result : alive) {
					log.info(result);
				}
				
			}
		}
	}

}
