/*
 * Vimplugin
 *
 * Copyright (c) 2008 by The Vimplugin Project.
 *
 * Released under the GNU General Public License
 * with ABSOLUTELY NO WARRANTY.
 *
 * See the file COPYING for more information.
 */
package org.vimplugin.listeners;

import java.util.HashMap;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.vimplugin.VimEvent;
import org.vimplugin.VimException;
import org.vimplugin.VimPlugin;
import org.vimplugin.editors.AbstractVimEditor;
import org.vimplugin.editors.VimCompletion;

/**
 * fire a keyAction when a special key is pressed inside vim.
 */
public class KeyCommand implements VimListener {

	/**
	 * reacts to "keyAtPos" by
	 * {@link org.vimplugin.editors.AbstractVimEditor#fireKeyAction(String,String)}
	 * 
	 * @see org.vimplugin.listeners.VimListener#handleEvent(org.vimplugin.VimEvent)
	 */
	// TODO: check whether "keyAtPos" and "keyCommand" are really the same...
	public void handleEvent(VimEvent ve) throws VimException {
		String event = ve.getEvent();
		if (event.equals("keyAtPos") == true) {
			String keySeq = ve.getArgument(0);
			keySeq = keySeq.substring(1, keySeq.length() - 1);
			String pos = ve.getArgument(1);
			for (AbstractVimEditor veditor : VimPlugin.getDefault()
					.getVimserver(ve.getConnection().getVimID()).getEditors()) {
				if (veditor.getBufferID() == ve.getBufferID()) {
					//veditor.fireKeyAction(keySeq, pos);
					System.out.println("Fire KeyAction: "+keySeq);
					int position = Integer.parseInt(pos);
						// For available actions, see doc/availableActions file..

						String ID="";
						
						if (keySeq.equals("F2")) {
							ICompilationUnit unit = (ICompilationUnit) veditor.getIJavaElement();
							try {
								unit.codeComplete(position+2, new VimCompletion());
							} catch (JavaModelException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						} else if (keySeq.equals("C-B")) {
							ID = "org.eclipse.ui.project.buildProject";
						} else if (keySeq.equals("C-C")) {
							ID ="org.eclipse.ui.project.rebuildProject";
						} else if (keySeq.equals("C-F11")) {
							ID = "org.eclipse.debug.ui.commands.RunLast";
						} else if (keySeq.equals("F11")) {
							ID= "org.eclipse.debug.ui.commands.DebugLast";
						}

						ICommandService com = (ICommandService) PlatformUI.getWorkbench()
								.getService(ICommandService.class);
						Command c = com.getCommand(ID);
						ExecutionEvent e = new ExecutionEvent(c, new HashMap<String, String>(),
								null, null);

						try {
							c.executeWithChecks(e);
						} catch (ExecutionException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (NotDefinedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (NotEnabledException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (NotHandledException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
				}
			}

		}
	}
}