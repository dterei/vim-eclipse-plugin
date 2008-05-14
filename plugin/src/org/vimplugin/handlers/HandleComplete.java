package org.vimplugin.handlers;

import org.eclipse.jdt.core.ICompilationUnit;
import org.vimplugin.VimEvent;
import org.vimplugin.editors.AbstractVimEditor;
import org.vimplugin.editors.VimCompletion;

public class HandleComplete implements CommandHandler {

	public void handle(Object... params) {

		try {
			//get the vimevent
			VimEvent ve = (VimEvent)params[0];
			
			//derive the things we wanna work with 
			AbstractVimEditor veditor = ve.getEditor(); 
			ICompilationUnit cu = (ICompilationUnit) veditor.getIJavaElement();
			int position = new Integer(ve.getArgument(1));
			
			cu.codeComplete(position+2, new VimCompletion());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
