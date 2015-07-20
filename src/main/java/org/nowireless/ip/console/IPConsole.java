package org.nowireless.ip.console;

import java.util.List;

import org.nowireless.console.CommandProcessor;
import org.nowireless.console.CommandProcessor.Command;
import org.nowireless.ip.command.ScanCommand;
import org.nowireless.ip.command.ShowCommand;
import org.nowireless.console.Console;

import jline.console.ConsoleReader;
import net.azib.ipscan.Main;

public class IPConsole {

	private class ExitCommand implements Command {
		@Override public void exec(List<String> args, ConsoleReader reader) throws Exception { Main.setRunning(false); }
		@Override public String getDescription() { return "Exits the program"; }
	}
	
	private final Console console = new Console();
	
	public IPConsole(ScanCommand scanCommand, ShowCommand showCommand) {
		console.getProcessor().registerCommand("scan", scanCommand);
		console.getProcessor().registerCommand("show", showCommand);
		console.getProcessor().registerCommand("clear", new CommandProcessor.ClearScreenCommand());
		console.getProcessor().registerCommand("exit", new ExitCommand());
	}
	
	public void start() {
		console.start();
	}
	
	
	
}
