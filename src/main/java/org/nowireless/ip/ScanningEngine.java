package org.nowireless.ip;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableList;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.core.ScannerDispatcherThread;
import net.azib.ipscan.core.ScannerDispatcherThreadFactory;
import net.azib.ipscan.core.ScanningProgressCallback;
import net.azib.ipscan.core.ScanningResult;
import net.azib.ipscan.core.ScanningResultCallback;
import net.azib.ipscan.core.ScanningResultList;
import net.azib.ipscan.core.ScanningResult.ResultType;
import net.azib.ipscan.core.ScanningResultList.ScanInfo;
import net.azib.ipscan.core.state.ScanningState;
import net.azib.ipscan.core.state.StateMachine;
import net.azib.ipscan.core.state.StateMachine.Transition;
import net.azib.ipscan.gui.feeders.FeederGUIRegistry;
import net.azib.ipscan.core.state.StateTransitionListener;

public class ScanningEngine implements ScanningProgressCallback, StateTransitionListener, ScanningResultCallback {

	private final Logger log = LogManager.getLogger();
	
	private final ScannerDispatcherThreadFactory scannerDispatcherThreadFactory;
	private final StateMachine stateMachine;
	private final FeederGUIRegistry feederGUIRegistry;
	private final ScanningResultList list;
	
	private ScannerDispatcherThread scannerThread;
	
	private final Lock statusLock = new ReentrantLock();
	private String status;
	private final AtomicInteger runningThreads = new AtomicInteger();
	private final AtomicInteger progress = new AtomicInteger();
	
	public String getStatus() { 
		statusLock.lock();
		String ret = new String(this.status);
		statusLock.unlock();
		return ret; 
	}
	public AtomicInteger getRunningThreads() { return this.runningThreads; }
	public AtomicInteger getProgress() { return this.progress; }
	
	public String getCurrentStatus() {
		return this.getStatus() + " " + progress.get() + "% Threads " + runningThreads.get();
	}
	
	
	public ScanningResultList getScanningResultList() {
		return this.list;
	}
	
	public List<ScanningResult> getHosts(ResultType type) {
		List<ScanningResult> ret = new ArrayList<>();
		for(ScanningResult result : list) {
			if(result.getType().equals(type)) {
				ret.add(result);
			}
		}
		return ImmutableList.copyOf(ret);
	}
	
	public ScanningEngine(ScannerDispatcherThreadFactory scannerThreadFactory, StateMachine stateMachine, FeederGUIRegistry feederRegistry, ScanningResultList list) {
		log.trace("Scanning Engine");
		
		this.scannerDispatcherThreadFactory = scannerThreadFactory;
		this.stateMachine = stateMachine;
		this.feederGUIRegistry = feederRegistry;
		this.list = list;
		
		stateMachine.addTransitionListener(this);
	}
	
	protected void update(String status, int runningThreads, int progress) {
		statusLock.lock();
		this.status = status;
		statusLock.unlock();
		this.runningThreads.set(runningThreads);
		this.progress.set(progress);
	}
	
	private void setStatus(String status) {
		statusLock.lock();
		this.status = new String(status);
		statusLock.unlock();
	}
	
	@Override
	public void transitionTo(final ScanningState state, final Transition transition) {
		if(transition == Transition.INIT) {
			return;
		}
		
		switch (state) {
		case IDLE:
			break;
		case STARTING:
			try {
				scannerThread = scannerDispatcherThreadFactory.createScannerThread(feederGUIRegistry.createFeeder(), this, this);
				update("", 0, 0);
				stateMachine.startScanning();
			} catch (RuntimeException e) {
				stateMachine.reset();
				throw e;
			}
			break;
		case RESTARTING:
			stateMachine.reset();;
			break;
		case SCANNING:
			scannerThread.start();
			break;
		case STOPPING:
			this.setStatus(Labels.getLabel("state.waitForThreads"));
			break;
		case KILLING:
			this.setStatus(Labels.getLabel("state.killingThreads"));
			break;			
		default:
			break;
		}
		
		if(transition == Transition.COMPLETE) {
			ScanInfo info = list.getScanInfo();
			double scanTime = info.getScanTime() / 1000;
			int scanned = info.getHostCount();
			int alive = info.getAliveCount();
			log.info("Scan Time {}", scanTime);
			log.info("Scanned {}, Allive {}", scanned, alive);
		}
		
	}

	@Override
	public void updateProgress(InetAddress currentAddress, int runningThreads, int percentageComplete) {
		if(currentAddress != null) {
			setStatus(Labels.getLabel("state.scanning") + currentAddress.getHostAddress());
		}
		this.runningThreads.set(runningThreads);
		this.progress.set(percentageComplete);
		
		log.info(this.getCurrentStatus());
	}

	@Override
	public void prepareForResults(ScanningResult result) {
		addOrUpdateResult(result);
	}

	@Override
	public void consumeResults(ScanningResult result) {
		addOrUpdateResult(result);
	}
	
	protected void addOrUpdateResult(ScanningResult result) {
		if(list.isRegistered(result)) {
			list.update(result);
		} else {
			int index = list.size();
			list.registerAtIndex(index, result);
		}
	}

}
