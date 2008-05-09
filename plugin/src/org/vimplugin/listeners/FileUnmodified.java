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
 * The File became unmodified.
 * @author menge
 */

public class FileUnmodified extends VimListener {

	public FileUnmodified(VimConnection connection) {
		super(connection);
	}

	public void handleEvent(VimEvent ve) {
		String event = ve.getEvent();
		if (event.equals("save") == true
				|| event.equals("unmodified") == true) {
			for (AbstractVimEditor veditor : VimPlugin.getDefault()
					.getVimserver(connection.getVimID()).getEditors()) {
				if (veditor.getBufferID() == ve.getBufferID())
					veditor.setDirty(false);
			}
	
		}
	}
}