package org.vim.vimplugin.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import org.vim.vimplugin.VimpluginPlugin;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = VimpluginPlugin.getDefault()
				.getPreferenceStore();
		
		if (System.getProperty("os.name").startsWith("Win")) {
			store.setDefault(PreferenceConstants.P_STRING, "C:/cygwin/bin/vim.exe");
		} else {
			store.setDefault(PreferenceConstants.P_STRING, "/usr/bin/vim.basic");
		}

	}

}
