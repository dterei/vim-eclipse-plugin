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
package org.vimplugin;

public abstract class VimListener {

	VimConnection vc;

	public VimListener() {

	}

	public VimListener(VimConnection parent) {
		vc = parent;
	}

	public abstract void handleEvent(VimEvent ve);

}
