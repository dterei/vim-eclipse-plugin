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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

import org.vimplugin.listeners.FileOpened;
import org.vimplugin.listeners.FileUnmodified;
import org.vimplugin.listeners.KeyCommand;
import org.vimplugin.listeners.Logger;
import org.vimplugin.listeners.ServerDisconnect;
import org.vimplugin.listeners.ServerStarted;
import org.vimplugin.listeners.TextInsert;
import org.vimplugin.listeners.TextRemoved;
import org.vimplugin.preferences.PreferenceConstants;

/**
 * Manage the communication channel with Vim.
 * 
 */
public class VimConnection implements Runnable {

	private boolean serverRunning = false;

	private boolean startupDone = false;

	private int vimID;

	private BufferedReader in;

	// Observer Pattern
	private HashSet<VimListener> listeners = new HashSet<VimListener>();

	private PrintWriter out;

	private int port;

	private ServerSocket socket;

	private Socket vimSocket;

	public VimConnection(int instanceID) {
		port = VimPlugin.getDefault().getPreferenceStore().getInt(
				PreferenceConstants.P_PORT);
		vimID = instanceID;
	}

	public void addListener(VimListener vl) {
		listeners.add(vl);
	}

	public boolean close() throws IOException {
		vimSocket.close();
		socket.close();
		serverRunning = false;
		return true;
	}

	public void command(int bufID, String name, String param) {
		int seqno = VimPlugin.getDefault().nextSeqNo();
		String tmp = bufID + ":" + name + "!" + seqno + " " + param;
		out.println(tmp);
	}

	public String function(int bufID, String name, String param)
			throws IOException {
		int seqno = VimPlugin.getDefault().nextSeqNo();
		String tmp = bufID + ":" + name + "/" + seqno + " " + param;
		out.println(tmp);
		try {
			tmp = in.readLine();
		} catch (Exception e) {
			//TODO: better exception handling
		}// the function might be saveAndExit.. in such case we wont get any
		// response
		return tmp;
	}

	public boolean isServerRunning() {
		return serverRunning;
	}

	public void plain(String s) {
		out.println(s);
	}

	public void run() {
		try {
			// start server
			socket = new ServerSocket(port + vimID);
			System.out.println("Server started and listening");
			// accept client
			vimSocket = socket.accept();
			out = new PrintWriter(vimSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(vimSocket
					.getInputStream()));
			System.out.println("Connection established");

			// Add Listeners 
			if (VimPlugin.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.P_DEBUG)) {
				listeners.add(new Logger());
			}

			listeners.add(new ServerStarted());
			listeners.add(new ServerDisconnect());
			listeners.add(new TextInsert());
			listeners.add(new TextRemoved());
			listeners.add(new FileOpened());
			listeners.add(new FileUnmodified());
			listeners.add(new KeyCommand());
			
			// handle Events
			String line;
			while (!startupDone && (line = in.readLine()) != null) {
				VimEvent ve = new VimEvent(line,this);
				for (VimListener listener : listeners) {
					listener.handleEvent(ve);
				}
			}
			addSpecialKeys();
			while ((serverRunning && (line = in.readLine()) != null)) {
				VimEvent ve = new VimEvent(line,this);
				for (VimListener listener : listeners) {
					listener.handleEvent(ve);
				}
			}
		} catch (IOException e) {
			//TODO: better exception handling
			e.printStackTrace();
		}
	}

	public void addSpecialKeys() {
		// Build Project --Press Ctrl+B
		command(vimID, "specialKeys", "\"C-B\"");
		// ReBuild Project --Press Ctrl+C
		command(vimID, "specialKeys", "\"C-C\"");
		// Run Last launched the project
		command(vimID, "specialKeys", "\"C-F11\"");
		// Debug Last launched
		command(vimID, "specialKeys", "\"F11\"");
		// Code Completion suggestions --not implemented
		command(vimID, "specialKeys", "\"F2\"");
	}

	public int getVimID() {
		return vimID;
	}

	public boolean isStartupDone() {
		return startupDone;
	}

	public void setServerRunning(boolean serverRunning) {
		this.serverRunning = serverRunning;
	}

	public void setStartupDone(boolean startupDone) {
		this.startupDone = startupDone;
	}

}
