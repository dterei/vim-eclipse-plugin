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
package org.vimplugin.views;

import org.eclipse.swt.SWT; 
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.part.ViewPart;

import org.vimplugin.VimPlugin;

public class CommandView extends ViewPart {

	private Text input;

	public void createPartControl(Composite parent) {
		input = new Text(parent, SWT.MULTI);
		input.addListener(SWT.KeyDown, new Listener() {

			public void handleEvent(Event e) {
				System.out.println("Command View: " + e.character);
				if (e.character == 0x0D) {
					VimPlugin.getDefault().getVimserver(0).getVc().plain(
							input.getText());
					input.setText("");
				}

			}
		});
	}

	public void setFocus() {
		input.setFocus();
	}

}
