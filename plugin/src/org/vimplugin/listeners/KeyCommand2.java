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
import org.vimplugin.VimException;
import org.vimplugin.VimPlugin;
import org.vimplugin.editors.AbstractVimEditor;
import org.vimplugin.handlers.CommandHandler;

/**
 * Executes a CommandHandler, when a special key is pressed inside eclipse.
 * 
 * TODO: check CommandPattern
 * TODO: ComanndPattern Parameters?
 * TODO: reuse Eclipse-Commands facilities
 */
public class KeyCommand2 implements VimListener {

	private final String key;
	private CommandHandler handler;
	
	public KeyCommand2(String key) {
		this.key = key;
	}
	
	/**
	 * reacts to "keyAtPos" by calling {@link #handler}.
	 * 
	 * @see org.vimplugin.listeners.VimListener#handleEvent(org.vimplugin.VimEvent)
	 */
	// TODO: check whether "keyAtPos" and "keyCommand" are really the same...
	public void handleEvent(VimEvent ve) throws VimException {
		String event = ve.getEvent();
		if (event.equals("keyAtPos") == true) {
			
			String keySeq = ve.getArgument(0);
			keySeq = keySeq.substring(1, keySeq.length() - 1);
			
			for (AbstractVimEditor veditor : VimPlugin.getDefault()
					.getVimserver(ve.getConnection().getVimID()).getEditors()) {
				if (veditor.getBufferID() == ve.getBufferID()) {
					if (keySeq.equals(key)) handler.handle(ve);				
				}
			}

		}
	}

	public CommandHandler getHandler() {
		return handler;
	}

	public void setHandler(CommandHandler handler) {
		this.handler = handler;
	}

	public String getKey() {
		return key;
	}
}