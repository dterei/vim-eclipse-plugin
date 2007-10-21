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

import org.vimplugin.editors.AbstractVimEditor;
import org.vimplugin.preferences.PreferenceConstants;

/**
 * Manage the communication channel with Vim.
 * 
 */
public class VimConnection implements Runnable {

	private boolean _isServerRunning = false;

	private boolean _isStartupDone = false;

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
		_isServerRunning = false;
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
		return _isServerRunning;
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

			addListeners();
			// handle Events
			String line;
			while (!_isStartupDone && (line = in.readLine()) != null) {
				VimEvent ve = new VimEvent(line);
				for (VimListener listener : listeners) {
					listener.handleEvent(ve);
				}
			}
			addSpecialKeys();
			while ((_isServerRunning && (line = in.readLine()) != null)) {
				VimEvent ve = new VimEvent(line);
				for (VimListener listener : listeners) {
					listener.handleEvent(ve);
				}
			}
		} catch (IOException e) {
			//TODO: better exception handling
			e.printStackTrace();
		}
	}

	/**
	 * Events
	 */
	//TODO simplify this long method?
	private void addListeners() {
		// DEBUG: write all Events to console
		if (VimPlugin.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.P_DEBUG)) {
			listeners.add(new VimListener(this) {
				public void handleEvent(VimEvent ve) {
					System.out.println(ve.getLine());
				}
			});
		}

		// Fires server started event
		listeners.add(new VimListener(this) {
			public void handleEvent(VimEvent ve) {
				String event = ve.getEvent();
				if (event.equals("startupDone") == true) {
					vc._isStartupDone = true;
					vc._isServerRunning = true;
				}
			}
		});

		// Closes the editor window if the server was closed
		listeners.add(new VimListener(this) {
			public void handleEvent(VimEvent ve) {
				String event = ve.getEvent();
				if (event.equals("disconnect") == true
						|| event.equals("killed") == true) {
					try {
						for (final AbstractVimEditor veditor : VimPlugin.getDefault()
								.getVimserver(vimID).getEditors()) {
							if (veditor != null) {
								veditor.forceDispose();
							}
						}
					} catch (Exception e) {
						//TODO: better exception handling
						e.printStackTrace();
					}
					try {
						vc.close();
					} catch (Exception e) {
						//TODO: better exception handling
					}
				}
			}
		});

		// Some text has been inserted, so modify document also
		listeners.add(new VimListener(this) {
			public void handleEvent(VimEvent ve) {
				String event = ve.getEvent();
				if (event.equals("insert") == true) {
					int length = Integer.parseInt(ve.getArgument(0));
					String text = ve.getArgument(1);
					text = text.substring(1, text.length() - 1);
					for (AbstractVimEditor veditor : VimPlugin.getDefault()
							.getVimserver(vimID).getEditors()) {
						if (veditor.getBufferID() == ve.getBufferID()) {
							veditor.insertDocumentText(text, length);
						}
					}
				}
			}
		});

		// Some text has been removed.. so remove that text in document also
		listeners.add(new VimListener(this) {
			public void handleEvent(VimEvent ve) {
				String event = ve.getEvent();
				if (event.equals("remove") == true) {
					int offset = Integer.parseInt(ve.getArgument(0));
					int length = Integer.parseInt(ve.getArgument(1));
					for (AbstractVimEditor veditor : VimPlugin.getDefault()
							.getVimserver(vimID).getEditors()) {
						if (veditor.getBufferID() == ve.getBufferID()) {
							veditor.removeDocumentText(offset, length);
						}
					}
				}
			}
		});

		// The File was opened, set Titles..
		listeners.add(new VimListener(this) {
			public void handleEvent(VimEvent ve) {
				String event = ve.getEvent();
				if (event.equals("fileOpened") == true) {
					String filePath = ve.getArgument(0);
					filePath = filePath.substring(1, filePath.length() - 1);
					int ID = VimPlugin.getDefault().getNumberOfBuffers() - 1;
					for (AbstractVimEditor veditor : VimPlugin.getDefault()
							.getVimserver(vimID).getEditors()) {
						if (veditor.getBufferID() == ID) {
							veditor.setTitleTo(filePath);
						}
					}
				}
			}
		});

		// The File became unmodified
		listeners.add(new VimListener(this) {
			public void handleEvent(VimEvent ve) {
				String event = ve.getEvent();
				if (event.equals("save") == true
						|| event.equals("unmodified") == true) {
					for (AbstractVimEditor veditor : VimPlugin.getDefault()
							.getVimserver(vimID).getEditors()) {
						if (veditor.getBufferID() == ve.getBufferID())
							veditor.setDirty(false);
					}

				}
			}
		});
		
		// We got a key command to process
		listeners.add(new VimListener(this) {
			public void handleEvent(VimEvent ve) {
				String event = ve.getEvent();
				if (event.equals("keyAtPos") == true) {
					String keySeq = ve.getArgument(0);
					keySeq = keySeq.substring(1, keySeq.length() - 1);
					String pos = ve.getArgument(1);
					for (AbstractVimEditor veditor : VimPlugin.getDefault()
							.getVimserver(vimID).getEditors()) {
						if (veditor.getBufferID() == ve.getBufferID())
							veditor.fireKeyAction(keySeq, pos);
					}

				}
			}
		});
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

}
