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
 * Some text has been inserted, so modify document also.
 * @author menge
 *
 */
public class TextInsert extends VimListener {

	public TextInsert(VimConnection connection) {
		super(connection);
	}

	@Override
	public void handleEvent(VimEvent ve) {
			String event = ve.getEvent();
			if (event.equals("insert") == true) {
				int length = Integer.parseInt(ve.getArgument(0));
				String text = ve.getArgument(1);
				text = text.substring(1, text.length() - 1);
				for (AbstractVimEditor veditor : VimPlugin.getDefault()
						.getVimserver(connection.getVimID()).getEditors()) {
					if (veditor.getBufferID() == ve.getBufferID()) {
						veditor.insertDocumentText(text, length);
					}
				}
			}
	}

}
