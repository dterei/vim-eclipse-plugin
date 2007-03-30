package org.vim.vimplugin.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.vim.vimplugin.VimpluginPlugin;

public class VimPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public VimPreferencePage() {
		super(GRID);
		setPreferenceStore(VimpluginPlugin.getDefault().getPreferenceStore());
		setDescription("Vimplugin Preferences");
	}
	
	public void createFieldEditors() {
		addField(new StringFieldEditor(
				PreferenceConstants.P_STRING, "Vim location", 
				getFieldEditorParent())
		);
	}

	public void init(IWorkbench workbench) {
	}
	
}