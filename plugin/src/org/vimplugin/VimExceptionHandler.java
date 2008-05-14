package org.vimplugin;

import org.vimplugin.utils.UtilFunctions;

public class VimExceptionHandler implements Thread.UncaughtExceptionHandler {

	/**
	 * Handle Exceptions in a Thread like {@link VimConnection} right.
	 */
	public void uncaughtException(Thread t, Throwable e) {
		System.err.println("VimConnection: "+UtilFunctions.getDefault().stackTraceToString(e));
    }


}
