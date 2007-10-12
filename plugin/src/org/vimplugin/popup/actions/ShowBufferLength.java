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
package org.vimplugin.popup.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.*;

import org.vimplugin.*;

public class ShowBufferLength implements IObjectActionDelegate {

	private int seq;

	/**
	 * Constructor for Action1.
	 */
	public ShowBufferLength() {
		super();
		seq = 0;
		//VimPlugin.getDefault().getVimserver(0).getVc().addListener(this);
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		seq++;
		VimPlugin.getDefault().getVimserver(0).getVc().plain(
				"1:getLength/" + seq);
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {}

	public void handleEvent(VimEvent ve) {
		if (ve.getLine().startsWith(Integer.toString(seq))) {
			System.out.println(ve.getLine());
		}
	}

}
