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
package org.vimplugin.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import org.vimplugin.VimPlugin;
import org.vimplugin.preferences.PreferenceConstants;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = VimPlugin.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.P_PORT, 3219);
		store.setDefault(PreferenceConstants.P_HOST, "localhost");
		store.setDefault(PreferenceConstants.P_PASS, "changeme");
		store.setDefault(PreferenceConstants.P_GVIM, "/usr/bin/gvim");
		store.setDefault(PreferenceConstants.P_EMBD, false);
	}

}
