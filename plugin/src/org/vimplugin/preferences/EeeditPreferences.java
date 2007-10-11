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

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Provides an empty preference page. Used since we have sub-pages.
 * 
 * @author David Terei
 */
public class EeeditPreferences extends PreferencePage implements
		IWorkbenchPreferencePage {

	/**
	 * Message to display on the empty page.
	 */
	private static final String message = "Expand the tree to edit preferences for a specific content type.";

	/**
	 * Initialise a given composite with a grid layout.
	 * 
	 * @param parent The composite.
	 * @param numColumns Number of columns for the grid.
	 * @return The initialised composite.
	 */
	private Composite createComposite(Composite parent, int numColumns) {
		noDefaultAndApplyButton();
		Composite composite = new Composite(parent, 0);
		GridLayout layout = new GridLayout();
		layout.numColumns = numColumns;
		composite.setLayout(layout);
		GridData data = new GridData(4);
		data.horizontalIndent = 0;
		data.verticalAlignment = 4;
		data.horizontalAlignment = 4;
		composite.setLayoutData(data);
		return composite;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
		Composite composite = createComposite(parent, 1);
		String description = message;
		Text text = new Text(composite, 8);
		text.setBackground(composite.getBackground());
		text.setText(description);
		return composite;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench iworkbench) {
	}

}
