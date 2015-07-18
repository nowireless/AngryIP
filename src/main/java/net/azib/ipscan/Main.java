/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan;

import net.azib.ipscan.config.*;
import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.core.UserErrorException;
import net.azib.ipscan.gui.feeders.AbstractFeederGUI;
import net.azib.ipscan.gui.feeders.FeederGUIRegistry;
import net.azib.ipscan.gui.feeders.RangeFeederGUI;

import javax.swing.*;

import org.apache.logging.log4j.LogManager;
import org.nowireless.ip.StartStopScanningAction;
import org.nowireless.ip.StateMachineListener;

import java.security.Security;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main executable class.
 * It initializes all the needed stuff and launches the user interface.
 * <p/>
 * All Exceptions, which are thrown out of the program, are caught and logged
 * using the java.util.logging facilities.
 * 
 * @see #main(String...)
 * @author Anton Keks
 */
public class Main {
	static final Logger LOG = LoggerFactory.getLogger();

	/**
	 * The launching point
	 * <p/>
	 * In development, pass the following on the JVM command line:
	 * <tt>-Djava.util.logging.config.file=config/logging.properties</tt>
	 * <p/>
	 * On Mac, add the following (otherwise SWT won't work):
	 * <tt>-XstartOnFirstThread</tt>
	 */
	public static void main(String... args) {
		//System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
		org.apache.logging.log4j.Logger log = LogManager.getLogger();
		log.trace(System.getProperty("java.util.logging.manager"));
		log.trace("Yes");
		try {
			long startTime = System.currentTimeMillis();
			initSystemProperties();

			LOG.finer("SWT initialized after " + (System.currentTimeMillis() - startTime));

			Locale locale = Config.getConfig().getLocale();
			Labels.initialize(locale);

			LOG.finer("Labels and Config initialized after " + (System.currentTimeMillis() - startTime));

			ComponentRegistry componentRegistry = new ComponentRegistry();
			LOG.finer("ComponentRegistry initialized after " + (System.currentTimeMillis() - startTime));

			processCommandLine(args, componentRegistry);

			// create the main window using dependency injection
			LOG.fine("Startup time: " + (System.currentTimeMillis() - startTime));
			
			componentRegistry.getContainer().getComponentInstance(StateMachineListener.class);
			
			FeederGUIRegistry reg = (FeederGUIRegistry) componentRegistry.getContainer().getComponentInstance(FeederGUIRegistry.class);
			
			AbstractFeederGUI feeder = reg.current();
			if(feeder instanceof RangeFeederGUI) {
				RangeFeederGUI range = (RangeFeederGUI) feeder;
				range.startIPText = "10.0.0.0";
				range.endIPText = "10.0.0.255";
			}
			
			StartStopScanningAction action = componentRegistry.getStartAction();
			
			action.start();
			
			while (true) {
				try {
					//System.out.println("!");
					
					Thread.sleep(50);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		catch (UnsatisfiedLinkError e) {
			JOptionPane.showMessageDialog(null, "Failed to load native code. Probably you are using a binary built for wrong OS or CPU. If 64-bit binary doesn't work for you, try 32-bit version, or vice versa.");
			e.printStackTrace();
		}
		catch (Throwable e) {
			JOptionPane.showMessageDialog(null, e + "\nPlease submit a bug report mentioning your OS and what were you doing.");
			e.printStackTrace();
		}
	}


	private static void initSystemProperties() {
		// currently we support IPv4 only
		System.setProperty("java.net.preferIPv4Stack", "true");
		// disable DNS caches
		Security.setProperty("networkaddress.cache.ttl", "0");
		Security.setProperty("networkaddress.cache.negative.ttl", "0");
	}

	private static void processCommandLine(String[] args, ComponentRegistry componentRegistry) {
		if (args.length != 0) {
			CommandLineProcessor cli = componentRegistry.getCommandLineProcessor();
			try {
				cli.parse(args);
			}
			catch (Exception e) {
				showMessageToConsole(e.getMessage() + "\n\n" + cli);
				System.exit(1);
			}
		}
	}

	private static void showMessageToConsole(String usageText) {
		System.out.println(usageText);
	}

	/**
	 * Returns a nice localized message for the passed exception
	 * in case it is possible, or toString() otherwise.
	 */
	static String getLocalizedMessage(Throwable e) {
		String localizedMessage;
		try {
			// try to load localized message
			if (e instanceof UserErrorException) {
				localizedMessage = e.getMessage();
			}
			else {
				String exceptionClassName = e.getClass().getSimpleName();
				String originalMessage = e.getMessage();
				localizedMessage = Labels.getLabel("exception." + exceptionClassName + (originalMessage != null ? "." + originalMessage : ""));
			}
			// add cause summary, if it exists
			if (e.getCause() != null) {
				localizedMessage += "\n\n" + e.getCause().toString();
			}
			LOG.log(Level.FINE, "error", e);
		}
		catch (Exception e2) {
			// fallback to default text
			localizedMessage = e.toString();
			// output stack trace to the console
			LOG.log(Level.SEVERE, "unexpected error", e);
		}
		return localizedMessage;
	}	
}
