package org.vimplugin.listeners;

import org.vimplugin.VimConnection;
import org.vimplugin.VimEvent;
import org.vimplugin.VimListener;

/**
 * DEBUG: write all Events to console
 * @author menge
 *
 */

public class Logger extends VimListener {

	public Logger(VimConnection vc) {
		super(vc);
	}
	
	@Override
	public void handleEvent(VimEvent ve) {
		System.out.println(ve.getLine());
	}
}
