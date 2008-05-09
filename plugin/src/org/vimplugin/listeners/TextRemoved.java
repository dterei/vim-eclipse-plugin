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
 * Some text has been removed.. so remove that text in document also.
 * @author menge
 *
 */
public class TextRemoved extends VimListener {
	public TextRemoved(VimConnection connection) {
		super(connection);
	}

	public void handleEvent(VimEvent ve) {
		String event = ve.getEvent();
		if (event.equals("remove") == true) {
			int offset = Integer.parseInt(ve.getArgument(0));
			int length = Integer.parseInt(ve.getArgument(1));
			for (AbstractVimEditor veditor : VimPlugin.getDefault()
					.getVimserver(connection.getVimID()).getEditors()) {
				if (veditor.getBufferID() == ve.getBufferID()) {
					veditor.removeDocumentText(offset, length);
				}
			}
		}
	}
}