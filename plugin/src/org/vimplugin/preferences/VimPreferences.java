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
package org.vimplugin.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;

import org.vimplugin.VimPlugin;
import org.vimplugin.preferences.PreferenceConstants;

/**
 * Eeedit Vims Preference Page.
 * 
 * @author David Terei
 */
public class VimPreferences extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	/**
	 * Constructor
	 */
	public VimPreferences() {
		super(GRID);
		setPreferenceStore(VimPlugin.getDefault().getPreferenceStore());
		setDescription("General Settings");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	public void createFieldEditors() {
		addField(new BooleanFieldEditor(PreferenceConstants.P_EMBD,
				"Embed Vim: (Linux Only)", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_PORT, "Port:",
				getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_HOST, "Host:",
				getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_PASS, "Password:",
				getFieldEditorParent()));
		addField(new FileFieldEditor(PreferenceConstants.P_GVIM,
				"Path to gvim:", true, getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_KEY,
				"Hot Keys (not yet ready)", getFieldEditorParent()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

}
