/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui.feeders;

import net.azib.ipscan.config.LoggerFactory;
import net.azib.ipscan.feeders.Feeder;
import net.azib.ipscan.feeders.FeederCreator;
import net.azib.ipscan.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

/**
 * Base class of feeder GUI classes.
 * 
 * @author Anton Keks
 */
public abstract class AbstractFeederGUI implements FeederCreator {
	static final Logger LOG = LoggerFactory.getLogger();
	
	protected Feeder feeder;

	public AbstractFeederGUI() {
		initialize();
	}

	public abstract void initialize();
		
	/**
	 * @return the feeder id
	 */
	public String getFeederId() {
		return feeder.getId();
	}

	/**
	 * @return the feeder name
	 */
	public String getFeederName() {
		return feeder.getName();
	}
	
	/**
	 * @return the feeder's name and the information about its current settings
	 */
	public String getInfo() {
		return getFeederName() + ": " + createFeeder().getInfo();
	}
	
	private static final Object localResolveLock = new Object();
	/** Cached name of local host **/
	private static String localName;
	/** Cached address of local host **/
	private static InterfaceAddress localInterface;
	
	/**
	 * Asynchronously resolves localhost's name and address and then populates the specified fields.
	 * The idea is to show GUI faster.
	 */
	protected void asyncFillLocalHostInfo(final String hostnameText, final String ipText) {
		new Thread() {
			public void run() {
				// this method is called for multiple Feeders simultaneously
				synchronized (localResolveLock) {
					if (localInterface == null) {
						localInterface = InetAddressUtils.getLocalInterface();
						try {
							localName = InetAddress.getLocalHost().getHostName();
						}
						catch (UnknownHostException e) {
							localName = localInterface.getAddress().getHostName();
						}
					}
					
					//LOG.info("Local Host Name {}", localName);
					
					/*Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							// fill the IP and hostname fields with local hostname and IP addresses
							if ("".equals(hostnameText.getText()))
								hostnameText.setText(localName);
							if ("".equals(ipText.getText())) {
								ipText.setText(localInterface.getAddress().getHostAddress());
								afterLocalHostInfoFilled(localInterface);
							}
						}
					});*/
				}
			}
		}.start();		
	}

	protected void afterLocalHostInfoFilled(InterfaceAddress localInterface) {
	}
}
