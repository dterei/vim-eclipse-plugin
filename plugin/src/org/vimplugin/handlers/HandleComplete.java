package org.vimplugin.handlers;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.vimplugin.editors.VimCompletion;

public class HandleComplete implements CommandHandler {

	private final ICompilationUnit cu;
	private final int position;
	
	public HandleComplete(ICompilationUnit cu, int position){
		this.cu = cu;
		this.position = position;
	}
	
	public void handle() {
		try {
			cu.codeComplete(position+2, new VimCompletion());
		} catch (JavaModelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
