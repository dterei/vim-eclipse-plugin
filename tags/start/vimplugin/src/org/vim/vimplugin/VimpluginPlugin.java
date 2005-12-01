/*
 * 
 *   This file is part of vimplugin.
 *   
 *   Copyright (c) 2005 Sebastian Menge and others
 *   
 *   It is licensed under the CPL. See vimplugin/doc/COPYRIGHT for the complete license.
 *  
 */

package org.vim.vimplugin;

import org.eclipse.ui.plugin.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class VimpluginPlugin extends AbstractUIPlugin {

	//The shared instance. (singleton pattern)
	private static VimpluginPlugin plugin;
	
	/**
	 * The constructor.
	 */
	public VimpluginPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static VimpluginPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("vimplugin", path);
	}
}
