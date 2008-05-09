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
 * We got a key command to process.
 * @author menge
 *
 */
public class KeyCommand extends VimListener {
	public KeyCommand(VimConnection connection) {
		super(connection);
	}

	public void handleEvent(VimEvent ve) {
		String event = ve.getEvent();
		if (event.equals("keyAtPos") == true) {
			String keySeq = ve.getArgument(0);
			keySeq = keySeq.substring(1, keySeq.length() - 1);
			String pos = ve.getArgument(1);
			for (AbstractVimEditor veditor : VimPlugin.getDefault()
					.getVimserver(connection.getVimID()).getEditors()) {
				if (veditor.getBufferID() == ve.getBufferID())
					veditor.fireKeyAction(keySeq, pos);
			}
	
		}
	}
}