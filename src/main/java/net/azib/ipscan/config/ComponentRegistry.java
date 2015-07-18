/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.config;

import net.azib.ipscan.core.PluginLoader;
import net.azib.ipscan.core.Scanner;
import net.azib.ipscan.core.ScannerDispatcherThreadFactory;
import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.core.net.PingerRegistry;
import net.azib.ipscan.exporters.CSVExporter;
import net.azib.ipscan.exporters.ExporterRegistry;
import net.azib.ipscan.exporters.IPListExporter;
import net.azib.ipscan.exporters.TXTExporter;
import net.azib.ipscan.exporters.XMLExporter;
import net.azib.ipscan.fetchers.CommentFetcher;
import net.azib.ipscan.fetchers.FetcherRegistry;
import net.azib.ipscan.fetchers.FilteredPortsFetcher;
import net.azib.ipscan.fetchers.HTTPSenderFetcher;
import net.azib.ipscan.fetchers.HostnameFetcher;
import net.azib.ipscan.fetchers.IPFetcher;
import net.azib.ipscan.fetchers.MACVendorFetcher;
import net.azib.ipscan.fetchers.NetBIOSInfoFetcher;
import net.azib.ipscan.fetchers.PingFetcher;
import net.azib.ipscan.fetchers.PingTTLFetcher;
import net.azib.ipscan.fetchers.PortsFetcher;
import net.azib.ipscan.fetchers.UnixMACFetcher;
import net.azib.ipscan.fetchers.WebDetectFetcher;
import net.azib.ipscan.fetchers.WinMACFetcher;
import net.azib.ipscan.gui.feeders.FeederGUIRegistry;
import net.azib.ipscan.gui.feeders.RandomFeederGUI;
import net.azib.ipscan.gui.feeders.RangeFeederGUI;

import org.nowireless.ip.DummyStateMachine;
import org.nowireless.ip.StartStopScanningAction;
import org.nowireless.ip.StateMachineListener;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * This class is the dependency injection configuration using the Pico Container.
 * 
 * @author Anton Keks
 */
public class ComponentRegistry {

	private PicoContainer container;

	private boolean containerStarted;

	public ComponentRegistry() {
		MutablePicoContainer container = new DefaultPicoContainer();
		this.container = container;

		//ComponentParameter anyComponentParameter = new ComponentParameter();

		// non-GUI
		Config globalConfig = Config.getConfig();
		container.registerComponentInstance(globalConfig);
		container.registerComponentInstance(globalConfig.getPreferences());
		container.registerComponentInstance(globalConfig.forScanner());
		container.registerComponentInstance(globalConfig.forOpeners());
		container.registerComponentInstance(globalConfig.forFavorites());
		container.registerComponentInstance(Labels.getInstance());
		container.registerComponentImplementation(CommentsConfig.class);
		container.registerComponentImplementation(ConfigDetector.class);

		container.registerComponentImplementation(ExporterRegistry.class);
		container.registerComponentImplementation(TXTExporter.class);
		container.registerComponentImplementation(CSVExporter.class);
		container.registerComponentImplementation(XMLExporter.class);
		container.registerComponentImplementation(IPListExporter.class);

		container.registerComponentImplementation(FetcherRegistry.class, FetcherRegistry.class);
		container.registerComponentImplementation(IPFetcher.class);
		container.registerComponentImplementation(PingFetcher.class);
		container.registerComponentImplementation(PingTTLFetcher.class);
		container.registerComponentImplementation(HostnameFetcher.class);
		container.registerComponentImplementation(PortsFetcher.class);
		container.registerComponentImplementation(FilteredPortsFetcher.class);
		container.registerComponentImplementation(WebDetectFetcher.class);
		container.registerComponentImplementation(HTTPSenderFetcher.class);
		container.registerComponentImplementation(CommentFetcher.class);
		container.registerComponentImplementation(NetBIOSInfoFetcher.class);
		if (Platform.WINDOWS) {
			container.registerComponentImplementation(WinMACFetcher.class);
		} else { 
			container.registerComponentImplementation(UnixMACFetcher.class);
		}
		container.registerComponentImplementation(MACVendorFetcher.class);

		container.registerComponentImplementation(DummyStateMachine.class);
		
		container.registerComponentImplementation(PingerRegistry.class, PingerRegistry.class);
		container.registerComponentImplementation(ScanningResultList.class);
		container.registerComponentImplementation(Scanner.class);
		container.registerComponentImplementation(ScannerDispatcherThreadFactory.class);
		container.registerComponentImplementation(CommandLineProcessor.class);

		container.registerComponentImplementation(FeederGUIRegistry.class);
		container.registerComponentImplementation(RangeFeederGUI.class);
		
		container.registerComponentImplementation(StartStopScanningAction.class);
		
		container.registerComponentImplementation(RandomFeederGUI.class);
		//container.registerComponentImplementation(FileFeederGUI.class);
		
		//container.registerComponentImplementation(TheStartable.class);
		
		container.registerComponentImplementation(StateMachineListener.class);
		
        new PluginLoader().addTo(container);
	}

	private void start() {
		if (!containerStarted) {
			containerStarted = true;
			container.start();
		}
	}

	//public MainWindow getMainWindow() {
	//	// initialize all startable components
	//	start();
	//	// initialize and return the main window
	//	return (MainWindow) container.getComponentInstance(MainWindow.class);
	//}

	public StartStopScanningAction getStartAction() {
		start();
		return (StartStopScanningAction) container.getComponentInstance(StartStopScanningAction.class);
	}
	
	public CommandLineProcessor getCommandLineProcessor() {
		start();
		return (CommandLineProcessor) container.getComponentInstance(CommandLineProcessor.class);
	}
	
	public PicoContainer getContainer() {
		return this.container;
	}
}
