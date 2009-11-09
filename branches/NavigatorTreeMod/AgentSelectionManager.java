/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "ascape-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders.
 */
package org.ascape.runtime.swing;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ascape.model.Scape;

/*
 * Copyright 1998-2007 The Brookings Institution, NuTech Solutions,Inc., Metascape LLC, and contributors.
 * All rights reserved. This program and the accompanying materials are made available solely under the terms
 * the BSD license "ascape-license.txt" which must accompany any redistributions. Any referenced or included
 * libraries carry licenses of their respective copyright holders.
 * 
 * User: Miles Parker
 * Date: Apr 14, 2005
 * Time: 10:25:16 AM
 */

/**
 * The Class AgentSelectionManager.
 */
public class AgentSelectionManager {

    /**
     * The selections for scape.
     */
    private Map<Scape, List> selectionsForScape = new HashMap<Scape, List>();

    /**
     * Gets the selections for scape.
     * 
     * @param scape
     *            the scape
     * @return the selections for scape
     */
    public List getSelectionsForScape(Scape scape) {
        List selections = selectionsForScape.get(scape);
        if (selections == null) {
            selections = new ArrayList();
            selectionsForScape.put(scape, selections);
        }
        return selections;
    }
}
