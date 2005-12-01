/*
 * 
 *   This file is part of vimplugin.
 *   
 *   Copyright (c) 2005 Sebastian Menge and others
 *   
 *   It is licensed under the CPL. See vimplugin/doc/COPYRIGHT for the complete license.
 *  
 */


package org.vim.vimplugin.editors;

import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.vim.vimplugin.VimJob;

import de.mud.terminal.SwingTerminal;
import de.mud.terminal.vt320;

public class VimEditor extends EditorPart {

	/* the terminal emulation */
  private vt320 emulation;

  /* vim */
  private Process vim = null;
	private VimJob vimjob = null; 
	
  /* std In/Out/Err */
  private InputStream vimIs=null;
	private OutputStream vimOs=null;
	private InputStream vimErr=null;

  /* widgtes */
  private SwingTerminal st=null;
	private Frame awtFrame = null;
  private Composite awtComposite;
	
  
  /* constants */
  private final String VIMCMD = "vim.org";

  // using linux, "vim" flickers enormously, probably a 
  // problem with doublebuffering in SwingTerminal and SWT_AWT
  //private final String VIMCMD = "vim";
  
	/**
	 * The constructor.
	 */
	public VimEditor() {
	}

	public void createPartControl(Composite parent) {
		// setup vim
    try {
			vim = Runtime.getRuntime().exec(VIMCMD);
			vimIs=vim.getInputStream();
			vimOs=vim.getOutputStream();
			vimErr=vim.getErrorStream();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}

		
    // setup the terminal emulation
    emulation = new vt320() {
			public void write(byte[] b) {
				try {
					vimOs.write(b);
					vimOs.flush();
				} catch(IOException ioe) {
					ioe.printStackTrace();
				}
			}
			public void beep() {
				System.err.println("beep()");
			}
			public void sendTelnetCommand(byte cmd) {
				System.err.println("sendTelnetCommand(byte cmd)");
			}
		};


    // setup the gui
    st=new SwingTerminal(emulation, new Font("Monospaced", Font.PLAIN, 12));
		st.setResizeStrategy(SwingTerminal.RESIZE_SCREEN);

		awtComposite = new Composite(parent, SWT.EMBEDDED);

    //     the SWT_AWT Bridge is ugly and not free (as in freedom),
    //     but the guys from JTA are working on a "SWTTerminal"
    awtFrame = SWT_AWT.new_Frame(awtComposite);
		
		awtFrame.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent ce) {
				int r=st.getVDUBuffer().getRows();
				int c=st.getVDUBuffer().getColumns();
				sendCmd(":set lines="+r+" columns="+c+"\n");
			}
		});
		
		awtFrame.add(st);

    // get the file to edit
    IFileEditorInput iei = (IFileEditorInput)getEditorInput();
		IFile f = iei.getFile();
		IPath path = f.getRawLocation();
		sendCmd(":hide edit "+path.toString()+"\n");
		setPartName(iei.getName());
    
    // start vim in a seperate Job.
    vimjob = new VimJob(vim,emulation);
		vimjob.schedule();
  }

	public void sendCmd(String vimCmd) {
		try {
			// System.err.println("CMD to vim: "+vimCmd);
			byte[] b=vimCmd.getBytes();
			vimOs.write(new byte [] {'', ''});
			vimOs.write(b);
			vimOs.flush();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void setTerm(String vimCmd) {
		// System.err.println("Set terminal: "+vimCmd);
		if (vimCmd.equals("FONT")) {
			st.setResizeStrategy(SwingTerminal.RESIZE_FONT);
		} else {
			st.setResizeStrategy(SwingTerminal.RESIZE_SCREEN);
		}
	}
	
  public void init(IEditorSite site, IEditorInput input) throws PartInitException {
    setSite(site);
    setInput(input);
  }
  
  public void dispose() {
    super.dispose();
    
    boolean cancelled = vimjob.cancel();
    System.err.println("vimjob cancelled: "+cancelled);
  }
  

////////////////   TODO   /////////////////
  
  
  public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		
	}

	public void doSaveAs() {
		// TODO Auto-generated method stub
		
	}

	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setFocus() {
		// TODO Auto-generated method stub
	
	}
}
