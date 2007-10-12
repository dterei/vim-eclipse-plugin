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
package org.vimplugin.utils;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.IParameter;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.PlatformUI;

/**
 * Place all the global functions in this class
 * 
 */
public final class UtilFunctions {

	/**
	 * An instance of this class
	 */
	private static UtilFunctions functions;

	/**
	 * Returns the shared instance.
	 * 
	 * @return a static instance of this class
	 */
	public static UtilFunctions getDefault() {
		return functions;
	}

	/**
	 * The constructor.
	 */
	public UtilFunctions() {
		functions = this;
	}

	/**
	 * Remove the backslashes from the given string.
	 * 
	 * @param text
	 *            String to remove from
	 * @return The processed string
	 */
	public String removeBackSlashes(String text) {
		if (text.length() <= 2)
			return text;
		int offset = 0, length = text.length(), offset1 = 0;
		// System.out.println("Initial-->"+text);
		String newText = "";
		while (offset < length) {
			offset1 = text.indexOf('\\', offset);
			// System.out.println(newText+"--> "+offset+" ->"+offset1);
			if (offset1 < 0) {
				newText = newText + text.substring(offset);
				break;
			}
			newText = newText + text.substring(offset, offset1)
					+ text.substring(offset1 + 1, offset1 + 2);
			offset = offset1 + 2;
		}
		// System.out.println(newText+"-->Final");
		return newText;
	}

	/**
	 * Gives FileName by taking the path
	 * 
	 * @param Path
	 *            The path to a file
	 * @return The filename from a path
	 */
	public String fileName(String Path) {
		String file;
		file = Path.substring(Path.lastIndexOf(File.separator) + 1);
		return file;
	}

	public void convertToKeyStroke(String KeySeq) {
		// For available actions, see doc/availableActions file..
		System.out.println(KeySeq);

		if (KeySeq.equals("C-B")) {
			executeCommand("org.eclipse.ui.project.buildProject");
		} else if (KeySeq.equals("C-C")) {
			executeCommand("org.eclipse.ui.project.rebuildProject");
		} else if (KeySeq.equals("C-F11")) {
			executeCommand("org.eclipse.debug.ui.commands.RunLast");
		} else if (KeySeq.equals("F11")) {
			executeCommand("org.eclipse.debug.ui.commands.DebugLast");
		}

	}

	/**
	 * Helper method to execute a command with the given ID that has no
	 * paramaters that need to be set.
	 * 
	 * @param ID
	 *            The id of the command to execute
	 */
	private void executeCommand(String ID) {
		ICommandService com = (ICommandService) PlatformUI.getWorkbench()
				.getService(ICommandService.class);
		Command c = com.getCommand(ID);
		ExecutionEvent e = new ExecutionEvent(c, new HashMap<String, String>(),
				null, null);

		try {
			c.executeWithChecks(e);
		} catch (Exception err) {
			//TODO: better exception handling
			err.printStackTrace();
		}
	}

	/**
	 * Print Useful info about a command.
	 * 
	 * @param c
	 *            The command
	 */
	@SuppressWarnings("unused")
	private void printCommandInfo(Command c) {
		try {
			System.out.println("Defined: " + c.isDefined());
			System.out.println("Handled: " + c.isHandled());
			System.out.println("Enabled: " + c.isEnabled());

			IParameter[] params = c.getParameters();
			System.out.println("Params NULL: " + (params == null));

			if (params == null)
				return;

			System.out.println("# of Params: " + params.length);
			for (IParameter p : params) {
				System.out.println("ID: " + p.getId());
				System.out.println("NAME: " + p.getName());
				System.out.println("VALUES: " + p.getValues());
				System.out.println("OPTIONAL: " + p.isOptional());
			}
		} catch (Exception e) {
			//TODO: better exception handling
			e.printStackTrace();
		}
	}

	/**
	 * Print out all available commands in Eclipse.
	 */
	@SuppressWarnings( { "unused", "unchecked" })
	private void printAllCommands() {
		ICommandService com = (ICommandService) PlatformUI.getWorkbench()
				.getService(ICommandService.class);

		Collection ids = com.getDefinedCommandIds();
		for (Object s : ids) {
			System.out.println(s);
		}
	}

}
