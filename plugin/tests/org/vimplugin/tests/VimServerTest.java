package org.vimplugin.tests;

import junit.framework.TestCase;

import org.vimplugin.VimPlugin;
import org.vimplugin.VimServer;

public class VimServerTest extends TestCase {

	/**
	 * A Dummy test.
	 */
	public void testGVimStart() {
		VimPlugin vp = VimPlugin.getDefault();
		int serverID = vp.getDefaultVimServer();
		VimServer vs = vp.getVimserver(serverID);
		assertEquals(serverID, 0);
	}
	
}
