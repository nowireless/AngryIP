/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui.feeders;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.Platform;
import net.azib.ipscan.feeders.Feeder;
import net.azib.ipscan.feeders.FeederException;
import net.azib.ipscan.feeders.RangeFeeder;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.UnknownHostException;

import static net.azib.ipscan.config.Labels.getLabel;
import static net.azib.ipscan.util.InetAddressUtils.*;

/**
 * GUI for initialization of RangeFeeder.
 * 
 * @author Anton Keks
 */
public class RangeFeederGUI extends AbstractFeederGUI {
	public String startIPText;
	public String endIPText;
	public String hostnameText;
	//private Button ipUpButton;
	private String netmaskCombo;

	private boolean isEndIPUnedited = true;
	private boolean modifyListenersDisabled = false;

	public RangeFeederGUI() {
		//super(parent);
		feeder = new RangeFeeder();
	}

	public void initialize() {


		// do this stuff asynchronously (to show GUI faster)
		asyncFillLocalHostInfo(hostnameText, startIPText);
	}

	public Feeder createFeeder() {
		feeder = new RangeFeeder(startIPText, endIPText);
		return feeder;
	}
	
	public String[] serialize() {
		return new String[] {startIPText, endIPText};
	}

	public void unserialize(String[] parts) {
		// TODO: netmask support from the command-line
		startIPText = (parts[0]);
		endIPText = (parts[1]);
		// reset the netmask combo
		netmaskCombo = (getLabel("feeder.range.netmask"));
	}

	public String[] serializePartsLabels() {
		return new String[] {"feeder.range.startIP", "feeder.range.endIP"};
	}

	@Override
	protected void afterLocalHostInfoFilled(InterfaceAddress localInterface) {
		InetAddress address = localInterface.getAddress();
		if (!address.isLoopbackAddress()) {}
			//updateStartEndWithNetmask(address, "/" + localInterface.getNetworkPrefixLength());
	}
}
