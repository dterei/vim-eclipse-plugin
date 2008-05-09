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
package org.vimplugin;

public abstract class VimListener {

	protected VimConnection connection;

	public VimListener() {

	}

	public VimListener(VimConnection parent) {
		connection = parent;
	}

	public abstract void handleEvent(VimEvent ve);

}
