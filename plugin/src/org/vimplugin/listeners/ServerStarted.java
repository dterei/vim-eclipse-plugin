/*
 * Vimplugin
 *
 * Copyright (c) 2008 by The Vimplugin Project.
 *
 * Released under the GNU General Public License
 * with ABSOLUTELY NO WARRANTY.
 *
 * See the file COPYING for more information.
 */
package org.vimplugin.listeners;

import org.vimplugin.VimConnection;
import org.vimplugin.VimEvent;
import org.vimplugin.VimException;
import org.vimplugin.VimListener;

/**
 * Fires server started event.
 */
public class ServerStarted implements VimListener {

	/**
	 * initializes the {@link VimConnection VimConnection} on "startupDone".
	 * @see org.vimplugin.VimListener#handleEvent(org.vimplugin.VimEvent)
	 */
	public void handleEvent(VimEvent ve) throws VimException {
		String event = ve.getEvent();
		if (event.equals("startupDone") == true) {
			ve.getConnection().setStartupDone(true);
			ve.getConnection().setServerRunning(true);
		}
	}
}