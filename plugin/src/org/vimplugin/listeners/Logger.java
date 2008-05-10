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

/**
 * Writes all events to standard out.
 */
public class Logger implements VimListener {

	/**
	 * System.out.println(ve.getLine());
	 * 
	 * @see org.vimplugin.VimListener#handleEvent(org.vimplugin.VimEvent)
	 */
	public void handleEvent(VimEvent ve) {
		System.out.println(ve.getLine());
	}
}
