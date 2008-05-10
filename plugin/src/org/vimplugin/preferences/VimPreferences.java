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

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;

import org.vimplugin.VimPlugin;
import org.vimplugin.preferences.PreferenceConstants;

/**
 * Vimplugin Preference Page. The fields are explained in
 * {@link org.vimplugin.PreferenceConstants PreferenceConstants}. The
 * preferecences have to be adjusted to the settings vim was started with (e.g.
 * 
 * <pre>
 * vim -nb:{hostname}:{addr}:{password}
 * </pre> ).
 */

// TODO: Move all strings to a properties file.
public class VimPreferences extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	/**
	 * Initializes the preference store and sets a description for the dialog. 
	 */
	public VimPreferences() {
		super(FieldEditorPreferencePage.GRID);
		setPreferenceStore(VimPlugin.getDefault().getPreferenceStore());
		setDescription("General Settings");
	}

	/**
	 * Adds the fields.
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	public void createFieldEditors() {
		addField(new BooleanFieldEditor(PreferenceConstants.P_EMBD,
				"Embed Vim: (Vim 7.1 on Linux and Windows only)",
				getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_PORT, "Port:",
				getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_HOST, "Host:",
				getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_PASS, "Password:",
				getFieldEditorParent()));
		addField(new FileFieldEditor(PreferenceConstants.P_GVIM,
				"Path to gvim:", true, getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_OPTS,
				"additional Parameters:", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_KEY,
				"Hot Keys (not yet ready):", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceConstants.P_DEBUG,
				"Debug to stdout:", getFieldEditorParent()));
	}

	/**
	 * does nothing.
	 * 
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

}
