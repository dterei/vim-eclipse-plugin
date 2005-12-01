/*
 * 
 *   This file is part of vimplugin.
 *   
 *   Copyright (c) 2005 Sebastian Menge and others
 *   
 *   It is licensed under the CPL. See vimplugin/doc/COPYRIGHT for the complete license.
 *  
 */

package org.vim.vimplugin;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import de.mud.terminal.*;

public class VimJob extends Job {
      
	private Process p; 
	private vt320 e;
	
	public VimJob(Process _p, vt320 _e) {
         super("Vim Job");
         p = _p;
         e = _e;
      }
      
      public IStatus run(IProgressMonitor monitor) {
  		// Read vim output and interpret it
  		try {
  			byte []buf=new byte[1024];
  			while (true) {
  				int len=p.getInputStream().read(buf);
  				if (len == -1) {
  					return Status.OK_STATUS;
  				}

  				String str=new String(buf, 0, len);
  				e.putString(str);
  			}
  		} catch(IOException ioe) {
  			ioe.printStackTrace();
  			return Status.CANCEL_STATUS;
  		} catch(Exception e) {
  			e.printStackTrace();
  			return Status.CANCEL_STATUS;
  		}
      }
   }