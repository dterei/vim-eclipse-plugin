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

import java.io.IOException;
import java.util.HashSet;

import org.eclipse.core.runtime.Platform;
import org.vimplugin.editors.AbstractVimEditor;
import org.vimplugin.preferences.PreferenceConstants;

/**
 * Abstract class that implements as much as of the Vim Server functions
 * as possible so that VimServer and VimServerNewWindow can hopefully be
 * combined eventually to one class or at least reduced to very tiny class
 * which just extend this class in a trivial manner.
 * 
 */
public class VimServer {

	private int ID;

	/**
	 * The editors associated with the vim instance. For same window opening.
	 */
	public HashSet<AbstractVimEditor> editors = new HashSet<AbstractVimEditor>();

	/**
	 * Initialise the class.
	 * 
	 * @param instanceID The ID for this VimServer.
	 */
	public VimServer(int instanceID) {
		ID = instanceID;
	}

	public int getInstanceID() {
		return ID;
	}
	
	/**
	 * The Vim process.
	 */
	protected Process p;

	/**
	 * The thread used to communicate with vim (runs {@link #vc}).
	 */
	protected Thread t;

	/**
	 * Used to communicate with vim.
	 */
	protected VimConnection vc = null;

	/**
	 * @return The {@link VimConnection} Used to communicate with this Vim
	 *         instance
	 */
	public VimConnection getVc() {
		return vc;
	}

	/**
	 * Gives the vim argument with the port depending on the portID
	 * @param portID 
	 * @return The argument for vim for starting the Netbeans interface.
	 */
	protected String getNetbeansString(int portID) {
		
		int port = VimPlugin.getDefault().getPreferenceStore().getInt(
				PreferenceConstants.P_PORT)+portID;
		String host = VimPlugin.getDefault().getPreferenceStore().getString(
				PreferenceConstants.P_HOST);
		String pass = VimPlugin.getDefault().getPreferenceStore().getString(
				PreferenceConstants.P_PASS);

		return "-nb:" + host + ":" + port + ":" + pass;
	}
	
	/**
	 * Start vim.
	 * 
	 * @param editor
	 *            The editor to associate it with
	 */
	public void start(AbstractVimEditor editor) {
		String gvim = VimPlugin.getDefault().getPreferenceStore().getString(
				PreferenceConstants.P_GVIM);
		String arg0 = getNetbeansString(ID);

		start(editor, gvim, arg0);
	}

	/**
	 * Start vim and embed it in the Window with the <code>wid</code> given.
	 * 
	 * @param editor
	 *            The editor to associate vim with
	 * @param wid
	 *            The id of the window to embed vim into
	 */
	public void start(AbstractVimEditor editor, int wid) {
		String gvim = VimPlugin.getDefault().getPreferenceStore().getString(
				PreferenceConstants.P_GVIM);
		String arg0 = getNetbeansString(ID);

		String arg1 = "-f"; // foreground -- dont fork
		String arg2 = "";
		String arg3 = String.valueOf(wid);
		if( Platform.getOS().equals(Platform.OS_LINUX) )
			arg2 = "--socketid";
		else if( Platform.getOS().equals(Platform.OS_WIN32) )
			arg2 = "--socketid";
		else
			arg3 = "";
		
		start(editor, gvim, arg0, arg1, arg2, arg3);
	}

	public void start(AbstractVimEditor editor, String... args) {
		editors.add(editor);
		if (vc != null && vc.isServerRunning())
			return;

		//setup VimConnection and start server thread
		vc = new VimConnection(ID);
		t = new Thread(vc);
		t.setDaemon(true);
		t.start();

		// starting gvim with Netbeans interface
		try {
			System.out.println("Trying to start vim");
			p = new ProcessBuilder(args).start();
			System.out.println("Started vim");
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Waits until server starts.. vim should return startupDone
		while (!vc.isServerRunning())
			;
	}

	/**
	 * Stops the server.. closes the vimconnection
	 * 
	 * @return Success
	 */
	public boolean stop() {
		boolean result = false; // If error raised

		try {
			result = vc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		vc = null;
		t.interrupt();
		p.destroy();
		return result;
	}

}
