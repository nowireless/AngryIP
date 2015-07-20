package org.nowireless.ip.command;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nowireless.console.CommandProcessor.Command;
import org.nowireless.ip.ScanningEngine;

import jline.console.ConsoleReader;
import jline.internal.Log;
import net.azib.ipscan.core.ScanningResult;
import net.azib.ipscan.core.ScanningResult.ResultType;

public class ShowCommand implements Command {

	private final Logger log = LogManager.getLogger();
	private final ScanningEngine engine;
	
	public ShowCommand(ScanningEngine engine) {
		this.engine = engine;
	}
	
	@Override
	public void exec(List<String> args, ConsoleReader reader) throws Exception {
		if(args.size() == 1) {
			String arg = args.get(0);
			if(arg.equalsIgnoreCase("help")) {
				dead();
			} else if(arg.equalsIgnoreCase("alive")) {
				alive();
			} else if(arg.equalsIgnoreCase("dead")) {
				dead();
			} else {
				help();
			}
		} else if(args.size() == 2) {
			if(args.get(0).equalsIgnoreCase("ip")) {
				ip(args.get(1));
			}
		} else {
			help();
		}
	}
	
	private void help() {
		log.info("show alive");
		log.info("show dead");
		log.info("show help");
		log.info("show ip 10.0.0.0");
	}
	
	private void alive() {
		List<ScanningResult> alive = engine.getHosts(ResultType.ALIVE);
		for(ScanningResult result : alive) {
			Log.info("{}", result.getAddress());
		}
	}
	
	private void dead() {
		List<ScanningResult> alive = engine.getHosts(ResultType.DEAD);
		for(ScanningResult result : alive) {
			Log.info("{}", result.getAddress());
		}
	}

	private void ip(String ip) {
		ScanningResult ipResult = null;
		for(ScanningResult result : engine.getScanningResultList()) {
			if(result.getAddress().getHostAddress().equals(ip)) {
				ipResult = result;
			}
		}
		if(ipResult == null) {
			log.warn("Given invalid ip");
			return;
		}
		
		log.info(ipResult);
	}
	
	@Override
	public String getDescription() {
		return "Todo";
	}

}
