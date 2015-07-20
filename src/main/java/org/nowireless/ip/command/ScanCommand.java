package org.nowireless.ip.command;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nowireless.console.CommandProcessor.Command;

import jline.console.ConsoleReader;
import net.azib.ipscan.core.net.PingerRegistry;
import net.azib.ipscan.core.state.ScanningState;
import net.azib.ipscan.core.state.StateMachine;
import net.azib.ipscan.gui.feeders.AbstractFeederGUI;
import net.azib.ipscan.gui.feeders.FeederGUIRegistry;
import net.azib.ipscan.gui.feeders.RangeFeederGUI;

public class ScanCommand implements Command {

	private final StateMachine stateMachine;
	private final FeederGUIRegistry feederGUIRegistry;
	private final PingerRegistry pingerRegistry;
	private final Logger log = LogManager.getLogger();
	
	public ScanCommand(StateMachine stateMachine, FeederGUIRegistry registry, PingerRegistry pingerRegistry) {
		this.stateMachine = stateMachine;
		this.feederGUIRegistry = registry;
		this.pingerRegistry = pingerRegistry;
	}
	
	@Override
	public void exec(List<String> args, ConsoleReader reader) throws Exception {
		if(args.size() != 2) {
			log.warn("Ex usage: scan 10.0.0.0 10.0.0.255");
			return;
		}
		
		if(stateMachine.inState(ScanningState.IDLE)) {
			AbstractFeederGUI feeder = feederGUIRegistry.current();
			if(feeder instanceof RangeFeederGUI) {
				RangeFeederGUI range = (RangeFeederGUI) feeder;
				range.startIPText = args.get(0);
				range.endIPText = args.get(1);
				
				pingerRegistry.checkSelectedPinger();
				
				stateMachine.transitionToNext();
			} else {
				log.error("Cannot use feeder {}", feeder);
			}
		} else {
			log.warn("The StateMachine is currently in {}", stateMachine.getState());
		}
		
		
	}

	@Override
	public String getDescription() {
		return "TODO";
	}
	
}