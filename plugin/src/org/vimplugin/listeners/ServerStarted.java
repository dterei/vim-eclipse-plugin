/*
 * Eeedit
 *
 * Copyright (c) 2007 by The Eeedit Project.
 *
 * Released under the GNU General Public License
 * with ABSOLUTELY NO WARRANTY.
 *
 * See the file COPYING for more information.
 */
package org.vimplugin.listeners;

import org.vimplugin.VimConnection;
import org.vimplugin.VimEvent;
import org.vimplugin.VimListener;

/**
 * Fires server started event.
 * @author menge
 *
 */
public class ServerStarted extends VimListener {
	public ServerStarted(VimConnection parent) {
		super(parent);
	}

	public void handleEvent(VimEvent ve) {
		String event = ve.getEvent();
		if (event.equals("startupDone") == true) {
			connection.setStartupDone(true);
			connection.setServerRunning(true);
		}
	}
}