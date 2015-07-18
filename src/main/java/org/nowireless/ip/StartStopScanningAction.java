/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package org.nowireless.ip;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.core.ScannerDispatcherThread;
import net.azib.ipscan.core.ScannerDispatcherThreadFactory;
import net.azib.ipscan.core.ScanningProgressCallback;
import net.azib.ipscan.core.ScanningResult;
import net.azib.ipscan.core.ScanningResultCallback;
import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.core.net.PingerRegistry;
import net.azib.ipscan.core.state.ScanningState;
import net.azib.ipscan.core.state.StateMachine;
import net.azib.ipscan.core.state.StateMachine.Transition;
import net.azib.ipscan.core.state.StateTransitionListener;
import net.azib.ipscan.gui.feeders.FeederGUIRegistry;
import java.net.InetAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Start/Stop button action class.
 * It listens to presses on the buttons as well as updates gui statuses
 * 
 * @author Anton Keks
 */
public class StartStopScanningAction implements ScanningProgressCallback, StateTransitionListener {
	
	private static Logger log = LogManager.getLogger("Status");
	
	public class StatusBar {

		private String status;
		private int runningThreads;
		private int progress;
		
		public void setStatusText(String string) {
			this.status = string;
		}

		public void setRunningThreads(int runningThreads) {
			this.runningThreads = runningThreads;
		}

		public void setProgress(int percentageComplete) {
			this.progress = percentageComplete;
		}
		
		private void update() {
			log.info("{} {}% Threads {}", status, progress, runningThreads);
		}
	}
	
	private ScanningResultList list;
	
	public void addOrUpdateResult(ScanningResult result) {
		if(list.isRegistered(result)) {
			list.update(result);
		} else {
			int index = list.size();
			list.registerAtIndex(index, result);
		}
	}
	
	private ScannerDispatcherThreadFactory scannerThreadFactory;
	private ScannerDispatcherThread scannerThread;
	//private GUIConfig guiConfig;
	private PingerRegistry pingerRegistry;

	private String mainWindowTitle;
	private StatusBar statusBar = new StatusBar();
	//private ResultTable resultTable;
	private FeederGUIRegistry feederRegistry;
	//private Button button;
	
	//Image[] buttonImages = new Image[ScanningState.values().length];
	String[] buttonTexts = new String[ScanningState.values().length];
	
	//private Display display;
	private StateMachine stateMachine;
	
	/**
	 * Creates internal stuff independent from all other external dependencies
	 */
	StartStopScanningAction(ScanningResultList list) {
		//this.display = display;
		this.list = list;
		
		// preload button images
		//buttonImages[ScanningState.IDLE.ordinal()] = new Image(display, Labels.getInstance().getImageAsStream("button.start.img"));
		//buttonImages[ScanningState.SCANNING.ordinal()] = new Image(display, Labels.getInstance().getImageAsStream("button.stop.img"));
		////buttonImages[ScanningState.STARTING.ordinal()] = buttonImages[ScanningState.SCANNING.ordinal()]; 
		//buttonImages[ScanningState.RESTARTING.ordinal()] = buttonImages[ScanningState.SCANNING.ordinal()];
		//buttonImages[ScanningState.STOPPING.ordinal()] = new Image(display, Labels.getInstance().getImageAsStream("button.kill.img"));
		//buttonImages[ScanningState.KILLING.ordinal()] = buttonImages[ScanningState.STOPPING.ordinal()];
		
		// preload button texts
		buttonTexts[ScanningState.IDLE.ordinal()] = Labels.getLabel("button.start");
		buttonTexts[ScanningState.SCANNING.ordinal()] = Labels.getLabel("button.stop");
		buttonTexts[ScanningState.STARTING.ordinal()] = buttonTexts[ScanningState.SCANNING.ordinal()]; 
		buttonTexts[ScanningState.RESTARTING.ordinal()] = buttonTexts[ScanningState.SCANNING.ordinal()];
		buttonTexts[ScanningState.STOPPING.ordinal()] = Labels.getLabel("button.kill");
		buttonTexts[ScanningState.KILLING.ordinal()] = Labels.getLabel("button.kill");
	}
	
	public StartStopScanningAction(ScannerDispatcherThreadFactory scannerThreadFactory, StateMachine stateMachine, FeederGUIRegistry feederRegistry, PingerRegistry pingerRegistry, ScanningResultList list) {
		this(list);

		this.scannerThreadFactory = scannerThreadFactory;
		//this.resultTable = resultTable;
		//this.statusBar = statusBar;
		this.feederRegistry = feederRegistry;
		this.pingerRegistry = pingerRegistry;
		//this.button = startStopButton;
		this.stateMachine = stateMachine;
		//this.guiConfig = guiConfig;
		
		// add listeners to all state changes
		stateMachine.addTransitionListener(this);
		
		// set the default image
		ScanningState state = stateMachine.getState();
		//button.setImage(buttonImages[state.ordinal()]);
		//button.setText(buttonTexts[state.ordinal()]);
	}

	/**
	 * Called when scanning button is clicked
	 */
	public void start() {
		// ask for confirmation before erasing scanning results
		if (stateMachine.inState(ScanningState.IDLE)) {
			if (!preScanChecks())
				return;
		}
		//ScanMenuActions.isLoadedFromFile = false;
		stateMachine.transitionToNext();
	}

	private final boolean preScanChecks() {
		// autodetect usable pingers and silently ignore any changes - 
		// user should see any errors only if they have explicitly selected a pinger
		pingerRegistry.checkSelectedPinger();
		
		// ask user for confirmation if needed
		//if (guiConfig.askScanConfirmation && resultTable.getItemCount() > 0) {
		//	MessageBox box = new MessageBox(resultTable.getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.SHEET);
		//	box.setText(Labels.getLabel("text.scan.new"));
		//	box.setMessage(Labels.getLabel("text.scan.confirmation"));
		//	if (box.open() != SWT.YES) {
		//		return false;
		//	}
		//}
		return true;
	} 
	
	public void transitionTo(final ScanningState state, final Transition transition) {
		if (transition == Transition.INIT)
			return;
		
		// TODO: separate GUI and non-GUI stuff
		switch (state) {
			case IDLE:
				// reset state text
				//button.setEnabled(true);
				updateProgress(null, 0, 0);
				statusBar.setStatusText(null);
				break;
			case STARTING:
				// start the scan from scratch!
				//resultTable.removeAll();
				try {
					scannerThread = scannerThreadFactory.createScannerThread(feederRegistry.createFeeder(), StartStopScanningAction.this, createResultsCallback(state));
					stateMachine.startScanning();
					//mainWindowTitle = statusBar.getShell().getText();
				}
				catch (RuntimeException e) {
					stateMachine.reset();
					throw e;
				}
				break;
			case RESTARTING:
				// restart the scanning - rescan
				//resultTable.resetSelection();
				//try {
				//	scannerThread = scannerThreadFactory.createScannerThread(feederRegistry.createRescanFeeder(resultTable.getSelection()), StartStopScanningAction.this, createResultsCallback(state));
				//	stateMachine.startScanning();
					//mainWindowTitle = statusBar.getShell().getText();
				//}
				//catch (RuntimeException e) {
				stateMachine.reset();
					//throw e;
				//}
				break;
			case SCANNING:
				scannerThread.start();
				break;
			case STOPPING:
				statusBar.setStatusText(Labels.getLabel("state.waitForThreads"));
				break;
			case KILLING:
				//button.setEnabled(false);
				statusBar.setStatusText(Labels.getLabel("state.killingThreads"));
				break;
		}
		// change button image
		//button.setImage(buttonImages[state.ordinal()]);
		//button.setText(buttonTexts[state.ordinal()]);
	}
	
	/**
	 * @return the appropriate ResultsCallback instance, depending on the configured display method.
	 */
	private final ScanningResultCallback createResultsCallback(ScanningState state) {
		// rescanning must follow the same strategy of displaying all hosts (even the dead ones), because the results are already in the list
		//if (guiConfig.displayMethod == DisplayMethod.ALL || state == ScanningState.RESTARTING) {
		return new ScanningResultCallback() {
			public void prepareForResults(ScanningResult result) {
				addOrUpdateResult(result);
				//log.info("Result {}", result.toString());
			}
			public void consumeResults(ScanningResult result) {
				addOrUpdateResult(result);
				//log.info("Result {}", result.toString());
			}
		};
		//}
		/*if (guiConfig.displayMethod == DisplayMethod.ALIVE) {
			return new ScanningResultCallback() {
				public void prepareForResults(ScanningResult result) {
				}
				public void consumeResults(ScanningResult result) {
					if (result.getType().ordinal() >= ResultType.ALIVE.ordinal())
						resultTable.addOrUpdateResultRow(result);
				}
			};
		}
		if (guiConfig.displayMethod == DisplayMethod.PORTS) {
			return new ScanningResultCallback() {
				public void prepareForResults(ScanningResult result) {
				}
				public void consumeResults(ScanningResult result) {
					if (result.getType() == ResultType.WITH_PORTS)
						resultTable.addOrUpdateResultRow(result);
				}
			};
		}*/
		//throw new UnsupportedOperationException(guiConfig.displayMethod.toString());
	}

	public void updateProgress(final InetAddress currentAddress, final int runningThreads, final int percentageComplete) {
		// update status bar
		
		
		if (currentAddress != null) {
			statusBar.setStatusText(Labels.getLabel("state.scanning") + currentAddress.getHostAddress());
		}					
		statusBar.setRunningThreads(runningThreads);
		statusBar.setProgress(percentageComplete);
		statusBar.update();
	
		// show percentage in main window title
		//if (!stateMachine.inState(ScanningState.IDLE)) {
		//	statusBar.getShell().setText(percentageComplete + "% - " + mainWindowTitle);
		//} else {
		//	statusBar.getShell().setText(mainWindowTitle);
		//}
		// change button image according to the current state
		//button.setImage(buttonImages[stateMachine.getState().ordinal()]);
		
	}

}
