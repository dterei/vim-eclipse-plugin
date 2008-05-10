/*
 * Vimplugin
 *
 * Copyright (c) 2007 by The Vimplugin Project.
 *
 * Released under the GNU General Public License
 * with ABSOLUTELY NO WARRANTY.
 *
 * See the file COPYING for more information.
 */
package org.vimplugin.listeners;

import org.vimplugin.VimEvent;
import org.vimplugin.VimListener;
import org.vimplugin.VimPlugin;
import org.vimplugin.editors.AbstractVimEditor;

/**
 * Closes the editor window if the server was closed.
 */
public class ServerDisconnect implements VimListener {

	/**
	 * Disposes the {@link org.vimplugin.editors.AbstractVimEditor ViMEditor} on
	 * "disconnect" or killed.
	 */
	public void handleEvent(VimEvent ve) {
		String event = ve.getEvent();

		if (event.equals("disconnect") == true
				|| event.equals("killed") == true) {
			try {
				for (final AbstractVimEditor veditor : VimPlugin.getDefault()
						.getVimserver(ve.getConnection().getVimID())
						.getEditors()) {
					if (veditor != null) {
						veditor.forceDispose();
					}
				}
			} catch (Exception e) {
				// TODO: better exception handling
				e.printStackTrace();
			}
			try {
				ve.getConnection().close();
			} catch (Exception e) {
				// TODO: better exception handling
			}
		}
	}
}