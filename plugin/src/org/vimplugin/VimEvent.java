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


/**
 * Resembles an event thrown by vim and caught by various listeners in vimplugin.
 * 
 */

// TODO: Better Exception Handling: Catching an Ex. and return "Error" is not
// the best way ... Perhaps introduce "VimException"?

public class VimEvent {

	/** The complete line vim threw.  */
	private String line;
	
	/** the connection this event came from */
	private VimConnection connection;

	/** Simply sets both private attributes. */
	public VimEvent(String _line,VimConnection _connection) {
		//TODO: pass at init? and store instead of at get methods.
		line = _line;
		connection = _connection;
	}

	/**
	 * The generic form of an event is: "bufID:name=123 arg1 arg2".
	 * 
	 * @return the original line vim threw.
	 */
	public String getLine() {
		return line;
	}

	/**
	 * The name of the event, as specified under :help netbeans.
	 * 
	 * @return the name of the event.
	 */
	public String getEvent() {
		int beginIndex = line.indexOf(':');
		int endIndex = line.indexOf('=');
		try {
			return line.substring(beginIndex + 1, endIndex);
		} catch (Exception e) {
			//TODO: Ouch, exception handling!
			return "Sorry";
		}
	}

	/**
	 * the argument at the specified position (starting with 0).
	 *  
	 * @param index
	 * @return the argument at the specified position.
	 */
	public String getArgument(int index) {
		int i = 0;
		int beginIndex = -1;
		while (i <= index) {
			beginIndex = line.indexOf(" ", beginIndex + 1);
			i++;
		}
		int endIndex = beginIndex;
		if (line.charAt(beginIndex + 1) == '"') {
			while (true) {
				endIndex = line.indexOf(" ", endIndex + 1);
				if (endIndex == -1
						|| (line.charAt(endIndex - 1) == '"' && beginIndex != endIndex - 2))
					break;
			}
		} else
			endIndex = line.indexOf(" ", beginIndex + 1);
		if (endIndex == -1)
			endIndex = line.length();
		try {
			return line.substring(beginIndex + 1, endIndex);
		} catch (Exception e) {
			return "Sorry";
		}

	}

	/**
	 * returns the bufferID. This is set by vimplugin. It is not the vim-buffer! budIDs
	 * start with one. Generic events have bufId of 0.
	 * 
	 * @return the bufferID of this event.
	 */
	public int getBufferID() {
		int beginIndex = line.indexOf(':');
		try {
			return Integer.parseInt(line.substring(0, beginIndex));
		} catch (Exception e) {
			return -1;
		}
	}

	/**
	 * Simple Getter.
	 * @return the connection this event came from.
	 */
	public VimConnection getConnection() {
		return connection;
	}

}
