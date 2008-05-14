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
package org.vimplugin.editors;

import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jdt.core.CompletionRequestor;

public class VimCompletion extends CompletionRequestor {
	
	/**
	 * TODO: javadoc
	 * @see org.eclipse.jdt.core.CompletionRequestor#accept(org.eclipse.jdt.core.CompletionProposal)
	 */
	@Override
	public void accept(CompletionProposal test) {
		//do something meaningful with this completion.
		System.out.println(test.getName());
	}
}