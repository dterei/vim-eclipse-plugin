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

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.vimplugin.VimConnection;
import org.vimplugin.VimPlugin;

/**
 * Vimplugin Preference Page. The fields are explained in
 * {@link org.vimplugin.preferences.PreferenceConstants PreferenceConstants}. The
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
	@Override
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
		addField(new BooleanFieldEditor(PreferenceConstants.P_DEBUG,
				"Debug to stdout:", getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_KEY1,
				"Hot Key 1:", getFieldEditorParent()));
		
		String[][] commands = {
				{"build","org.eclipse.ui.project.buildProject"},
				{"rebuild","org.eclipse.ui.project.rebuildProject"},
				{"runlast","org.eclipse.debug.ui.commands.RunLast"}
		};
		addField(new ComboFieldEditor(PreferenceConstants.P_COMMAND1,
				"Command 1:", commands,getFieldEditorParent() ));
	}

	/**
	 * does nothing.
	 * 
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
	@Override
	public boolean performOk() {
		int vimid = VimPlugin.getDefault().getDefaultVimServer();
		VimConnection vc = VimPlugin.getDefault().getVimserver(vimid).getVc(); 
		
		String key1 = VimPlugin.getDefault().getPreferenceStore().getString(PreferenceConstants.P_KEY1);
		String command1 = VimPlugin.getDefault().getPreferenceStore().getString(PreferenceConstants.P_COMMAND1);

		vc.setEclipseCommandHandler(key1, command1);
		
		return super.performOk();
	}

}
