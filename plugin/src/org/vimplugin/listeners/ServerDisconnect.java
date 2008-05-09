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
import org.vimplugin.VimPlugin;
import org.vimplugin.editors.AbstractVimEditor;

/**
 * Closes the editor window if the server was closed
 * @author menge
 *
 */
public class ServerDisconnect extends VimListener {

	public ServerDisconnect(VimConnection connection) {
		super(connection);
	}

	public void handleEvent(VimEvent ve) {
		String event = ve.getEvent();
		if (event.equals("disconnect") == true
				|| event.equals("killed") == true) {
			try {
				for (final AbstractVimEditor veditor : VimPlugin.getDefault()
						.getVimserver(connection.getVimID()).getEditors()) {
					if (veditor != null) {
						veditor.forceDispose();
					}
				}
			} catch (Exception e) {
				//TODO: better exception handling
				e.printStackTrace();
			}
			try {
				connection.close();
			} catch (Exception e) {
				//TODO: better exception handling
			}
		}
	}
}