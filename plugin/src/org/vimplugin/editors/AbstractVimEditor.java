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
package org.vimplugin.editors;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jdt.core.CompletionRequestor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.vimplugin.VimPlugin;
import org.vimplugin.preferences.PreferenceConstants;
import org.vimplugin.utils.UtilFunctions;
import org.vimplugin.utils.WidHandler;

/**
 * Provides an Editor to Eclipse which is backed by a Vim instance. This class
 * must be initialised through one of the two subclasses {@link VimEditor} or
 * {@link VimEditorNewWindow}.
 * 
 * @author Nageshwar M, David Terei
 */
public class AbstractVimEditor extends TextEditor {

	// ID of the VimServer.
	protected int serverID;

	// Buffer ID in Vim instance.
	public int bufferID;

	protected Canvas editorGUI;

	// Document Instances..
	protected IDocument document;

	protected VimDocumentProvider documentProvider;

	protected boolean dirty;

	protected boolean alreadyClosed = false;

	// Code suggesting Engine
	public CompletionRequestor requestor;

	public IJavaProject iJavaProject;

	// Relative path to this file in the project
	public IPath pathToTheFile;

	/**
	 * The constructor.
	 */
	public AbstractVimEditor() {
		super();
		bufferID = -1; // not really necessary but set it to an invalid buffer
		setDocumentProvider(documentProvider = new VimDocumentProvider());
		requestor = new CompletionRequestor() {
			public void accept(CompletionProposal test) {
				System.out.println(test.getCompletion());
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		if (!gvimAvailable()) {
			//TODO: handle nicer.
			close(false);
			return;
		}
		
		//TODO: If external nicer display in Eclipse.
		editorGUI = new Canvas(parent, SWT.EMBEDDED);
		Color color = new Color(parent.getDisplay(), new RGB(0x10, 0x10, 0x10));
		editorGUI.setBackground(color);

		alreadyClosed = false;
		dirty = false;

		createVim(editorGUI);

		IFileEditorInput iei = (IFileEditorInput) getEditorInput();
		IFile selectedFile = iei.getFile();
		pathToTheFile = selectedFile.getProjectRelativePath();
		iJavaProject = JavaCore.create(selectedFile.getProject());
		String absolutpath = selectedFile.getRawLocation().toPortableString();
		bufferID = VimPlugin.getDefault().numberOfBuffers++;
		VimPlugin.getDefault().getVimserver(serverID).getVc().command(bufferID,
				"editFile", "\"" + absolutpath + "\"");
	}

	/**
	 * Create a vim instance figuring out if it should be external or embedded.
	 * 
	 * @param parent
	 */
	private void createVim(Composite parent) {
		boolean embd = VimPlugin.getDefault().getPreferenceStore().getBoolean(
				PreferenceConstants.P_EMBD);
		if (embd) {
			createEmbeddedVim(parent);
		} else {
			createExternalVim(parent);
		}
	}

	/**
	 * Create an external Vim instance.
	 * 
	 * @param parent
	 */
	private void createExternalVim(Composite parent) {
		VimPlugin.getDefault().getVimserver(serverID).start(this);
	}

	/**
	 * Create an embedded Vim instance.
	 * 
	 * @param parent
	 */
	private void createEmbeddedVim(Composite parent) {
		int wid = WidHandler.getWID(parent);
		if (wid == WidHandler.WID_ERROR) {
			//TODO: handle error.
		}
		int h = parent.getClientArea().height;
		int w = parent.getClientArea().width;
		VimPlugin.getDefault().getVimserver(serverID).start(this, wid);
		VimPlugin.getDefault().getVimserver(serverID).getVc().command(bufferID,
				"setLocAndSize", h + " " + w);
	}

	/**
	 * @return If gvim exists and is executable.
	 */
	protected boolean gvimAvailable() {
		String gvim = VimPlugin.getDefault().getPreferenceStore().getString(
				PreferenceConstants.P_GVIM);
		File file = new File(gvim);
		if (file.exists())
			return true;
		return false;
	}

	/**
	 * This function will be called by vimserver when it gets an event
	 * disconnect or killed It doesn't ask to save modifications since vim takes
	 * care of that.
	 */
	public void forceDispose() {
		final AbstractVimEditor vime = this;
		Display display= getSite().getShell().getDisplay();
		display.asyncExec(new Runnable() {
			public void run() {
				if (vime != null && !vime.alreadyClosed) {
					vime.setDirty(false);
					vime.showBusy(true);
					vime.close(false);
					getSite().getPage().closeEditor(vime, false);
					//vime.alreadyClosed = true;
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.editors.text.TextEditor#dispose()
	 */
	public void dispose() {
		System.out.println("dispose()");
		// TODO: calling close ourselves here doesn't seem right.
		close(true);

		if (editorGUI != null) {
			editorGUI.dispose();
			editorGUI = null;
		}

		document = null;
		requestor = null;
		iJavaProject = null;
		pathToTheFile = null;

		super.dispose();
	}

	/**
	 * This function will be called when we close the window If the
	 * <code>save</code> is true then we call command save If the current
	 * buffer is the last one the vim will be closed, else only the buffer will
	 * be closed.
	 */
	public void close(boolean save) {
		System.out.println("close( " + save + " );");
		if (this.alreadyClosed) {
			super.close(false);
			return;
		}

		alreadyClosed = true;
		VimPlugin.getDefault().getVimserver(serverID).editors.remove(this);

		try {
			if (save && dirty) {
				VimPlugin.getDefault().getVimserver(serverID).getVc().command(
						bufferID, "save", "");
				dirty = false;
				firePropertyChange(PROP_DIRTY);
			}

			if (VimPlugin.getDefault().getVimserver(serverID).editors.size() > 0) {
				VimPlugin.getDefault().getVimserver(serverID).getVc().command(
						bufferID, "close", "");
			} else {
				VimPlugin.getDefault().getVimserver(serverID).getVc().function(
						bufferID, "saveAndExit", "");
				VimPlugin.getDefault().stopVimServer(serverID);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.close(false);
	}

	/*
	 * public void setHighlightRange(int offset,int length,boolean moveCursor){
	 * System.out.println("--Highlighted-"+offset+length+"-- OK!");
	 * if(moveCursor){ VimPlugin.getDefault().getVimserver().getVc().command(
	 * bufferID, "setDot", "2/1"); } }
	 */

	/**
	 * We can't modify the file in the eclipse source viewer.. We use that only
	 * for showing error messages...
	 */
	public boolean isEditable() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {
		VimPlugin.getDefault().getVimserver(serverID).getVc().command(bufferID,
				"save", "");
		dirty = false;
		firePropertyChange(PROP_DIRTY);
	}

	/**
	 * Since we have a copy of edited text in document, we can perform saveAs
	 * operation
	 */
	public void doSaveAs() {
		performSaveAs(null);
	}

	/**
	 * Initialisation goes here..
	 */
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		try {
			document = documentProvider.createDocument(input);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns <code>true</code> if the file was modified, else return
	 * <code>false</code>
	 */
	public boolean isDirty() {
		if (alreadyClosed)
			getSite().getPage().closeEditor(this, false);
		return dirty;
	}

	/**
	 * Returns <code>true</code> if save as is allowed, else return
	 * <code>false</code>
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}

	/**
	 * Makes the present editor dirty.. means the IDE knows that file was
	 * modified
	 * 
	 * @param result
	 */
	public void setDirty(boolean result) {
		dirty = result;
		firePropertyChange(PROP_DIRTY);
	}

	/**
	 * According to the given <code>KeySeq</code> executes the actions..
	 * 
	 * @param keySeq The key sequence pressed. 
	 * @param pos The position of the cursor in the text file.
	 */
	public void fireKeyAction(String keySeq, String pos) {
		int position = Integer.parseInt(pos);
		if (keySeq.equals("F11"))
			possibleCompletions(position + 2);
		else
			UtilFunctions.getDefault().convertToKeyStroke(keySeq);
	}

	/**
	 * Sets focus (brings to top in Vim) to the buffer.. this function will be
	 * called when user activates this editor window
	 */
	public void setFocus() {
		if (alreadyClosed) {
			getSite().getPage().closeEditor(this, false);
			return;
		}
		// Brings the corresponding buffer to top
		VimPlugin.getDefault().getVimserver(serverID).getVc().command(bufferID,
				"setDot", "off");
		// Brings the vim editor window to top
		VimPlugin.getDefault().getVimserver(serverID).getVc().command(bufferID,
				"raise", "");
	}

	/**
	 * Sets the editor window title to path.. need to change path to file name..
	 * 
	 * @param path
	 */
	public void setTitleTo(String path) {
		setPartName(UtilFunctions.getDefault().fileName(path));
		setContentDescription(path);
		firePropertyChange(PROP_TITLE);
	}

	// /////// Handling Document content.. ///////////

	/**
	 * Returns the document provider
	 */
	public IDocumentProvider getDocumentProvider() {
		return documentProvider;
	}

	/**
	 * Sets the document content to given text
	 * 
	 * @param text The text for the editor.
	 */
	public void setDocumentText(String text) {
		document.set(text);
		setDirty(true);
	}

	/**
	 * Inserts text into document 
	 * FIXME Not working properly.. both
	 * insertDocument and removeDocument have some implementation problems..
	 * 
	 * @param text The text to insert.
	 * @param offset The offset to insert it at.
	 */
	public void insertDocumentText(String text, int offset) {
		text = UtilFunctions.getDefault().removeBackSlashes(text);
		System.out.println(text + " INSERT " + offset);
		try {
			String first = document.get(0, offset);
			String last = document.get(offset, document.getLength() - offset);
			if (text.equals(new String("\\n"))) {
				System.out.println("Insert new Line");
				first = first + System.getProperty("line.separator") + last;
			} else
				first = first + text + last;
			document.set(first);
			setDirty(true);
			System.out.println(first);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Removes text in the document
	 * 
	 * @param offset The offset of the cursor in the text.
	 * @param length The amount of text to remove.
	 */
	public void removeDocumentText(int offset, int length) {
		System.out.println(offset + " REMOVE " + length);
		try {
			String first = document.get(0, offset);
			String last = document.get(offset + length, document.getLength()
					- offset - length);
			first = first + last;
			System.out.println(first);
			document.set(first);
			setDirty(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.editors.text.TextEditor#createActions()
	 */
	protected void createActions() {
		super.createActions();
	}

	// ///// Code Completion and error reporting Engine Implementation ////////
	/**
	 * Gives the possible code suggestions to the requester class.. This strings
	 * will be send back to vim to display.
	 * 
	 * @param position Position in the buffer
	 */
	public void possibleCompletions(int position) {
		try {
			IJavaElement javaElement = iJavaProject.findElement(pathToTheFile);
			if (javaElement.getElementType() == IJavaElement.COMPILATION_UNIT) {
				ICompilationUnit test = (ICompilationUnit) javaElement;
				test.codeComplete(position, requestor);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
