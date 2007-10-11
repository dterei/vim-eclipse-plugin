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
package org.vimplugin.editors;

import org.vimplugin.VimPlugin;

/**
 * A VimEditor class intended to open a new Vim instance each time its
 * called.
 * 
 * @author Nageshwar M, David Terei
 */
public class VimEditorNewWindow extends AbstractVimEditor {
	
	public VimEditorNewWindow() {
		super();
		serverID = VimPlugin.getDefault().createVimServer();
	}

}
