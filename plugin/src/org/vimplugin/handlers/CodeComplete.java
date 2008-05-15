package org.vimplugin.handlers;

import org.eclipse.jdt.core.ICompilationUnit;
import org.vimplugin.VimEvent;
import org.vimplugin.VimException;
import org.vimplugin.editors.AbstractVimEditor;
import org.vimplugin.editors.VimCompletion;

/**
 * Performs a CodeCompletion.
 */
public class CodeComplete implements IHandler {

	/**
	 * gets the current compilation unit and calls codecomplete at the current
	 * position. Does only work for java files!
	 * 
	 * TODO: This can probably also be done via the {@link EclipseCommand}
	 * "org.eclipse.ui.edit.text.contentAssist.proposals" or similar.
	 * 
	 * TODO: Actually change the editors contents!
	 * 
	 * @see org.vimplugin.handlers.IHandler#handle(java.lang.Object[])
	 * @param params We expect onlyan vimevent at params[0]
	 * @throws VimException wrapped Excption.
	 */
	public void handle(Object... params){

		try {
			// get the vimevent
			VimEvent ve = (VimEvent) params[0];

			// derive the things we wanna work with
			AbstractVimEditor veditor = ve.getEditor();
			ICompilationUnit cu = (ICompilationUnit) veditor.getIJavaElement();
			int position = new Integer(ve.getArgument(1));

			// do the codecomplete (for now just sysout it.
			cu.codeComplete(position + 2, new VimCompletion());
		} catch (Exception e) {
			//TODO: Exception handling
			e.printStackTrace();
		}
	}

}
