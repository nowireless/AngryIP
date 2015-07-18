/**
 * This file is a part of Angry IP Scanner source code,
 * see http://www.angryip.org/ for more information.
 * Licensed under GPLv2.
 */
package net.azib.ipscan.gui.fetchers;

import net.azib.ipscan.fetchers.Fetcher;
import net.azib.ipscan.fetchers.FetcherPrefs;
import net.azib.ipscan.fetchers.PortTextFetcher;

/**
 * PortTextFetcherPrefs
 *
 * @author Anton Keks
 */
public class PortTextFetcherPrefs implements FetcherPrefs {
	
	private PortTextFetcher fetcher;
	
	public PortTextFetcherPrefs() {
	}
	
	public void openFor(Fetcher fetcher) {
		this.fetcher = (PortTextFetcher) fetcher;
	}

	private String stringToText(String s) {
		StringBuilder t = new StringBuilder();
		for (char c : s.toCharArray()) {
			if (c == '\n') 
				t.append("\\n");
			else if (c == '\r')
				t.append("\\r");
			else if (c == '\t')
				t.append("\\t");
			else
				t.append(c);
		}
		return t.toString();
	}
}
