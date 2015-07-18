/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui.feeders;

import net.azib.ipscan.feeders.Feeder;
import net.azib.ipscan.feeders.FileFeeder;


import static net.azib.ipscan.config.Labels.getLabel;

/**
 * GUI for initialization of FileFeeder.
 *
 * @author Anton Keks
 */
public class FileFeederGUI extends AbstractFeederGUI {
	private String fileNameText;

	public FileFeederGUI() {
		//super(parent);
		feeder = new FileFeeder();
	}

	public void initialize() {
		//setLayout(new GridLayout(3, false));
		//Label fileNameLabel = new Label(this, SWT.NONE);
       // fileNameText = new Text(this, SWT.BORDER);
		//Button browseButton = new Button(this, SWT.NONE);
        
        //fileNameLabel.setText(getLabel("feeder.file.name") + ":");

		//fileNameText.setLayoutData(new GridData(160, -1));

        //browseButton.setText(getLabel("feeder.file.browse"));
       // browseButton.addSelectionListener(new SelectionAdapter() {
		//	public void widgetSelected(SelectionEvent e) {
		//		FileDialog dialog = new FileDialog(getShell());
		//		dialog.setText(getLabel("feeder.file.browse"));
		//		String fileName = dialog.open();
		///		if (fileName != null) {
		//			fileNameText.setText(fileName);
		//			fileNameText.setSelection(fileName.length());
		//		}
		//	}
		//});
       //                 
		//pack();
	}

	public Feeder createFeeder() {
		feeder = new FileFeeder(fileNameText);
		return feeder;
	}
	
	public String[] serialize() {
		return new String[] {fileNameText};
	}

	public void unserialize(String[] parts) {
		//fileNameText.setText(parts[0]);
	}

	public String[] serializePartsLabels() {
		return new String[] {"feeder.file.name"};
	}
}
