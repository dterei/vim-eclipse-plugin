package org.vimplugin.handlers;


public class HandleUndefined implements CommandHandler {

	String message;
	
	public HandleUndefined() {
		message = "Undefined!";
	}
	
	public HandleUndefined(String m) {
		message = m;
	}
	
	public void handle(Object... params) {
		System.out.println(message);
	}

}
