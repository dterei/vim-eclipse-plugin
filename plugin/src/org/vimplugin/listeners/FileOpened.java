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
 * The File was opened, set Titles..
 * @author menge
 *
 */
public class FileOpened extends VimListener {

	public FileOpened(VimConnection connection) {
		super(connection);
	}

	public void handleEvent(VimEvent ve) {
		String event = ve.getEvent();
		if (event.equals("fileOpened") == true) {
			String filePath = ve.getArgument(0);
			filePath = filePath.substring(1, filePath.length() - 1);
			int ID = VimPlugin.getDefault().getNumberOfBuffers() - 1;
			for (AbstractVimEditor veditor : VimPlugin.getDefault()
					.getVimserver(this.connection.getVimID()).getEditors()) {
				if (veditor.getBufferID() == ID) {
					veditor.setTitleTo(filePath);
				}
			}
		}
	}
}