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

public class VimEvent {

	private String line;

	public VimEvent(String _line) {
		//TODO: pass at init? and store instead of at get methods.
		line = _line;
	}

	public String getLine() {
		return line;
	}

	public String getEvent() {
		int beginIndex = line.indexOf(':');
		int endIndex = line.indexOf('=');
		try {
			return line.substring(beginIndex + 1, endIndex);
		} catch (Exception e) {
			return "Sorry";
		}
	}

	public String getArgument(int index) {
		int i = 0;
		int beginIndex = -1;
		while (i <= index) {
			beginIndex = line.indexOf(" ", beginIndex+1);
			i++;
		}
		int endIndex=beginIndex;
		if(line.charAt(beginIndex+1)=='"'){
			while(true){
				endIndex = line.indexOf(" ", endIndex+1);
				if(endIndex==-1 || (line.charAt(endIndex-1)=='"' && beginIndex!=endIndex-2))
					break;
			}
		}
		else
			endIndex = line.indexOf(" ", beginIndex+1);		
		if (endIndex == -1)
			endIndex = line.length();		
		try {
			return line.substring(beginIndex + 1, endIndex);
		} catch (Exception e) {
			return "Sorry";
		}

	}

	public int getBufferID() {
		int beginIndex = line.indexOf(':');
		try {
			return Integer.parseInt(line.substring(0, beginIndex));
		} catch (Exception e) {
			return -1;
		}
	}

}
