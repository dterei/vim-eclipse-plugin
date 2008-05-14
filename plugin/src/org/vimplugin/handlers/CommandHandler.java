package org.vimplugin.handlers;

public interface CommandHandler {

	/**
	 * Commands call this method on their handler-member.
	 * 
	 * @param params a list of 0 to n parameters
	 * @see http://java.sun.com/j2se/1.5.0/docs/guide/language/varargs.html
	 */
	public void handle(Object... params);
	
}
