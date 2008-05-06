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
package org.vimplugin.utils;

import java.lang.reflect.Field;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Composite;

/**
 * Simple class to provide an abstracted interface for getting
 * a SWT Widgets HWND/Wid/Handle.
 * 
 */
public class WidHandler {
	
	/**
	 * Indicates an error occurred finding the WID.
	 */
	public static final int WID_ERROR = -2;

	/**
	 * The field to grab for Linux/GTK2.
	 */
	private static final String linuxWID = "embeddedHandle";
	
	/**
	 * The field to grab for Windows/Win32.
	 */
	private static final String win32WID = "handle";
	
	/**
     * Block instantiation.
     */
	private WidHandler() {
		// do nothing
	}

	/**
	 * Get the Window ID/Handle of the SWT Widget given. Uses reflection since
	 * the code is platform specific and this allows us to distribute just one
	 * plugin for all platforms.
	 * 
	 * @param parent The SWT Widget.
	 * @return The handle of the SWT Widget.
	 */
	public static long getWID(Composite parent) {
		Class<?> c = parent.getClass();
		Field f = null;
		
		try {
			if( Platform.getOS().equals(Platform.OS_LINUX) ) {			
				//f = c.getField(linuxWID);
				// the class reflection doesn't work on linux
				// return the embeddedHandle 
				return parent.embeddedHandle;
			}
			else if( Platform.getOS().equals(Platform.OS_WIN32) )
				f = c.getField(win32WID);			
			else {
				f = c.getField(win32WID);
			}
			
			return f.getInt(parent);
		} catch (SecurityException e) {
			//TODO: better exception handling
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			//TODO: better exception handling
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			//TODO: better exception handling
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			//TODO: better exception handling
			e.printStackTrace();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		return WID_ERROR;
	}
}
