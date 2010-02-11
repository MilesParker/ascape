package org.ascape.model.rule;

import org.ascape.model.Agent;
import org.ascape.model.Scape;

/**
 * Check a scape's listeners to see if we can quit.
 * 
 * @author Oliver Mannion
 * @version $Revision$ 
 */
public class ListenerQuitCheck extends PropogateScapeOnly {


    /**
	 * 
	 */
	private static final long serialVersionUID = -2675948214688308560L;

	private boolean blnCanQuit = true;
	
	/**
	 * Constructor.
	 */
    public ListenerQuitCheck() {
        super(ListenerQuitCheck.class.getSimpleName());
    }

    /**
     * Check all the scape's listeners, then children, to see if we can quit.
     * 
     * @param agent
     *            the agent (scape) to check
     */
    public void execute(Agent agent) {
        blnCanQuit = ((Scape) agent).scapeCanQuit();
        
        // check children
        if (blnCanQuit) {
        	super.execute(agent);
        }
    }

	public boolean canQuit() {
		return blnCanQuit;
	}
    
}
