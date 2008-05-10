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

import org.vimplugin.VimEvent;
import org.vimplugin.VimListener;
import org.vimplugin.VimPlugin;
import org.vimplugin.editors.AbstractVimEditor;

/**
 * fire a keyAction when a special key is pressed inside vim.
 */
public class KeyCommand implements VimListener {

	/**
	 * reacts to "keyAtPos" by
	 * {@link org.vimplugin.editors.AbstractVimEditor#fireKeyAction(String,String)}
	 * 
	 * @see org.vimplugin.VimListener#handleEvent(org.vimplugin.VimEvent)
	 */
	// TODO: check whether "keyAtPos" and "keyCommand" are really the same...
	public void handleEvent(VimEvent ve) {
		String event = ve.getEvent();
		if (event.equals("keyAtPos") == true) {
			String keySeq = ve.getArgument(0);
			keySeq = keySeq.substring(1, keySeq.length() - 1);
			String pos = ve.getArgument(1);
			for (AbstractVimEditor veditor : VimPlugin.getDefault()
					.getVimserver(ve.getConnection().getVimID()).getEditors()) {
				if (veditor.getBufferID() == ve.getBufferID())
					veditor.fireKeyAction(keySeq, pos);
			}

		}
	}
}