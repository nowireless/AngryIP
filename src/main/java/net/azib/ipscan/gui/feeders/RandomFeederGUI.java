/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui.feeders;

import net.azib.ipscan.feeders.Feeder;
import net.azib.ipscan.feeders.RandomFeeder;

/**
 * GUI for initialization of RandomFeeder
 *
 * @author Anton Keks
 */
public class RandomFeederGUI extends AbstractFeederGUI {
	private String ipPrototypeText;
	private String ipMaskCombo;
	private String hostnameText;
	//private Button ipUpButton;
	private int countSpinner;

	public RandomFeederGUI() {
		//super(parent);
		feeder = new RandomFeeder();
	}

	public void initialize() {
		//setLayout(new GridLayout(5, false));

		//Label ipPrototypeLabel = new Label(this, SWT.NONE);
       // ipPrototypeText = new Text(this, SWT.BORDER);
		//Label ipMaskLabel = new Label(this, SWT.NONE);
       // ipMaskCombo = new Combo(this, SWT.NONE);
		//Label hostnameLabel = new Label(this, SWT.NONE);
		//hostnameText = new Text(this, SWT.BORDER);
		//ipUpButton = new Button(this, SWT.NONE);
		//Label countLabel = new Label(this, SWT.NONE);
      //  countSpinner = new Spinner(this, SWT.BORDER);
        
        // the longest possible IP
      //  ipPrototypeText.setText("255.255.255.255xx");
      //  int textWidth = ipPrototypeText.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
      //  ipPrototypeText.setText("");
		//ipPrototypeText.setLayoutData(new GridData(textWidth, -1));

        //ipPrototypeLabel.setText(getLabel("feeder.random.prototype") + ":");
		//ipPrototypeLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
        
       // ipMaskLabel.setText(getLabel("feeder.random.mask") + ":");

		//ipMaskCombo.setVisibleItemCount(10);
		// Warning: IPv4 specific netmasks
		//ipMaskCombo.add("255...128");
		//ipMaskCombo.add("255...0");
		//ipMaskCombo.add("255..0.0");
		//ipMaskCombo.add("255.0.0.0");
		//ipMaskCombo.add("0.0.0.0");
		//ipMaskCombo.add("255..0.255");
		//ipMaskCombo.add("255.0.0.255");
		//ipMaskCombo.select(3);
		//ipMaskCombo.setLayoutData(new GridData()); ((GridData)ipMaskCombo.getLayoutData()).horizontalSpan = 2;

      //  hostnameLabel.setText(getLabel("feeder.random.hostname") + ":");
		//ipMaskLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

		//FeederActions.HostnameButton hostnameSelectionListener = new FeederActions.HostnameButton(hostnameText, ipPrototypeText, ipMaskCombo);
		//hostnameText.addTraverseListener(hostnameSelectionListener);
		//hostnameText.setLayoutData(new GridData(textWidth, -1));

		//ipUpButton.setImage(new Image(getDisplay(), Labels.getInstance().getImageAsStream("button.ipUp.img")));
		//ipUpButton.setText(getLabel("button.ipUp"));
		//ipUpButton.addSelectionListener(hostnameSelectionListener);

		//countLabel.setText(getLabel("feeder.random.count"));

		//countSpinner.setSelection(100);
		//countSpinner.setMaximum(100000);
		//countSpinner.setMinimum(1);
		//countSpinner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//countSpinner.addTraverseListener(new TraverseListener() {
		//	public void keyTraversed(TraverseEvent e) {
		//		// this due to a bug either in SWT or GTK:
		//		// spinner getText() returns the new value only if
		//		// it has lost the focus first
		//		ipPrototypeText.forceFocus();
		//		countSpinner.forceFocus();
		//	}
		//});
		
		//pack();

		// do this stuff asynchronously (to show GUI faster)
		asyncFillLocalHostInfo(hostnameText, ipPrototypeText);
	}

	public Feeder createFeeder() {
		feeder = new RandomFeeder(ipPrototypeText, ipMaskCombo, countSpinner);
		return feeder;
	}
	
	public String[] serialize() {
		return new String[] {ipPrototypeText, ipMaskCombo, Integer.toString(countSpinner)};
	}

	public void unserialize(String[] parts) {
		ipPrototypeText = (parts[0]);
		ipMaskCombo = (parts[1]);
		countSpinner = (Integer.parseInt(parts[2]));
	}
	
	public String[] serializePartsLabels() {
		return new String[] {"feeder.random.prototype", "feeder.random.mask", "feeder.random.count"};
	}
}
