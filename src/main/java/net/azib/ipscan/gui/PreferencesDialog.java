/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui;

import net.azib.ipscan.config.Config;

import net.azib.ipscan.config.Labels;
import net.azib.ipscan.config.ScannerConfig;
import net.azib.ipscan.core.PortIterator;
import net.azib.ipscan.core.net.PingerRegistry;
import net.azib.ipscan.fetchers.FetcherException;


/**
 * Preferences Dialog
 *
 * @author Anton Keks
 */
public class PreferencesDialog {
	
	private PingerRegistry pingerRegistry;
	private Config globalConfig;
	private ScannerConfig scannerConfig;

	private String[] languages = { "system", "en", "hu", "lt", "es", "ku" };
	
	public PreferencesDialog(PingerRegistry pingerRegistry, Config globalConfig, ScannerConfig scannerConfig) {
		this.pingerRegistry = pingerRegistry;
		this.globalConfig = globalConfig;
		this.scannerConfig = scannerConfig;
	}
	
	
	/**
	 * Opens the specified tab of preferences dialog
	 * @param tabIndex
	 */
	public void openTab(int tabIndex) {
		// widgets are created on demand
		//createShell();
		loadPreferences();
		//tabFolder.setSelection(tabIndex);
		
		// select ports text by default if ports tab is opened
		// this is needed for PortsFetcher that uses this tab as its preferences
		//if (tabFolder.getItem(tabIndex) == portsTabItem) {
		//	portsText.forceFocus();
		//}
		
		//super.open();
	}

	/**
	 * This method initializes tabFolder	
	 */
	private void createTabFolder() {
	
//		createFetchersTab();
//		tabItem = new TabItem(tabFolder, SWT.NONE);
//		tabItem.setText(Labels.getLabel("title.preferences.fetchers"));
//		tabItem.setControl(fetchersTab);
	}

	/**
	 * This method initializes scanningTab	
	 */
	private void createScanningTab() {
		
	}

	/**
	 * This method initializes displayTab	
	 */
	private void createDisplayTab() {
	
	}
	
	/**
	 * This method initializes portsTab	
	 */
	private void createPortsTab() {
		}

	/**
	 * This method initializes fetchersTab	
	 */
//	private void createFetchersTab() {
//		GridLayout gridLayout = new GridLayout();
//		gridLayout.numColumns = 1;
//		fetchersTab = new Composite(tabFolder, SWT.NONE);
//		fetchersTab.setLayout(gridLayout);
//		Label label = new Label(fetchersTab, SWT.NONE);
//		label.setText(Labels.getLabel("preferences.fetchers.info"));
//	}


	private void loadPreferences() {
    pingerRegistry.checkSelectedPinger();
		
	}
	
	private void savePreferences() {
	}
}
