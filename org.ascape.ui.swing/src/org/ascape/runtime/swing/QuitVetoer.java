package org.ascape.runtime.swing;

/**
 * Provides method for checking whether we can quit Ascape or not.
 * 
 * @author Oliver Mannion
 * @version $Revision$
 */
public interface QuitVetoer {

	/**
     * Test whether we can quit Ascape or not.
	 * 
	 * @return {@code true} if we can quit.
	 */
	boolean canQuit();

}
