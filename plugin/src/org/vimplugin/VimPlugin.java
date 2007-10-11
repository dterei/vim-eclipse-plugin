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

import java.util.HashMap;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.vimplugin.utils.UtilFunctions;

/**
 * The main plugin class to be used in the desktop.
 */
public class VimPlugin extends AbstractUIPlugin {

	/**
	 * The shared instance.
	 */
	private static VimPlugin plugin;

	/**
	 * ID of the default Vim instance.
	 */
	public static final int DEFAULT_VIMSERVER_ID = 0;

	/**
	 * Returns the shared instance.
	 * 
	 * @return the default plugin instance
	 */
	public static VimPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path.
	 * 
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("VimNB", path);
	}

	/**
	 * Counts number of instances of vimServerNewWindow
	 */
	public int nextServerID;

	/**
	 * Counts number of total buffers opened so far. If we close one buffer this
	 * value doesn't change.
	 */
	public int numberOfBuffers;

	/**
	 * Counts number of commands executed so far. Will be useful for checking
	 * functions and replies
	 */
	public int SeqNo;

	/**
	 * Store all the vim instances using their id as the key.
	 */
	private HashMap<Integer, VimServer> vimServers = new HashMap<Integer, VimServer>();

	/**
	 * The constructor.
	 */
	public VimPlugin() {
		plugin = this;
		new UtilFunctions();
	}

	/**
	 * Creates a {@link VimServer} for each open action
	 * 
	 * @return the server instance
	 */
	public int getDefaultVimServer() {
		return createVimServer(DEFAULT_VIMSERVER_ID);
	}

	/**
	 * Creates a VimServer.
	 * 
	 * @return The VimServer ID.
	 */
	public int createVimServer() {
		return createVimServer(nextServerID++);
	}

	/**
	 * Create a new VimServer with the ID Specified. If a VimServer with the ID
	 * specified already exists, then don't do anything.
	 * 
	 * @param id ID to use for the new VimServer.
	 * @return ID of the new VimServer.
	 */
	private int createVimServer(int id) {
		if (!vimServers.containsKey(id)) {
			VimServer vimserver = new VimServer(id);
			vimServers.put(id, vimserver);
		}
		return id;
	}

	/**
	 * Stops the VimServer specified.
	 * 
	 * @param id The ID of the VimServer to stop.
	 * @return Success.
	 */
	public boolean stopVimServer(int id) {
		boolean b = getVimserver(id).stop();
		vimServers.remove(id);
		return b;
	}

	/**
	 * Returns VimServer with the id specified.
	 * 
	 * @param id The ID of the VimServer.
	 * @return The VimServer with the ID specified.
	 */
	public VimServer getVimserver(int id) {
		return vimServers.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		nextServerID = 1; // 0 is for the DEFAULT VimServer
		numberOfBuffers = 1; // Vim starts buffer count from 1
		SeqNo = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}
}
