package org.vimplugin.handlers;

import java.util.HashMap;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

public class HandleEclipseCommand implements CommandHandler {

	private final String id;

	public HandleEclipseCommand(String id) {
		this.id = id;
	}

	public void handle(Object... params) {
		System.out.println("trying to exec eclipse command "+id);
		ICommandService com = (ICommandService) PlatformUI.getWorkbench()
				.getService(ICommandService.class);
		Command c = com.getCommand(id);
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
