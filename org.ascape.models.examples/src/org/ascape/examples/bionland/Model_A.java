/*
 * This software and documentation is subject to, and may be only used in accordance with, the terms and conditions of
 * the license agreement described or set forth in the file titled License.txt.  This software and documentation
 * contains information that is confidential and proprietary to NuTech Solutions, Inc.
 *
 *             Copyright (c) 2000-2004
 *             NuTech Solutions, Inc.
 *  
 * Portions of the software are licensed from The Brookings Institution
 *             Copyright (c) 1998-2000
 *            The Brookings Institution
 *          All rights reserved worldwide.
 * 
 * U.S. GOVERNMENT END USERS. The software is a "commercial item," as that term is defined in 48 C.F.R. 2.101,
 * consisting of "commercial computer software" and "commercial computer software documentation," as such terms are
 * used in 48 C.F.R. 12.212. Consistent with 48 C.F.R. 12.212 and 48 C.F.R. 227.7202-1 through 227.7202-4, and other
 * relevant sections of the Code of Federal Regulations, as applicable all U.S. Government End Users acquire the
 * software with only those rights set forth in the license agreement. This software includes trade secrets that
 * are exempt from disclosure under the Freedom of Information Act.  
 *
 */


package org.ascape.examples.bionland;

import org.ascape.model.CellOccupant;
import org.ascape.model.HostCell;
import org.ascape.model.Scape;
import org.ascape.model.space.Array2D;
import org.ascape.model.space.Array2DMoore;
import org.ascape.view.vis.Overhead2DView;

public class Model_A extends Scape {

    private Scape territory;

    private Scape bions;

    public void createScape() {
        super.createScape();
        territory = new Scape(new Array2DMoore());
        territory.setName("Bionland");
        territory.setPrototypeAgent(new HostCell());
        ((Array2D) territory.getSpace()).setExtent(25, 25);
        add(territory);
        bions = new Scape();
        bions.setExtent(100);
        bions.setName(name);
        bions.addInitialRule(MOVE_RANDOM_LOCATION_RULE);
        CellOccupant protoAgent = new CellOccupant();
        protoAgent.setHostScape(territory);
        bions.setPrototypeAgent(protoAgent);
        add(bions);
    }

    public void createViews() {
        super.createViews();
        Overhead2DView mapView = new Overhead2DView("Map");
        territory.addView(mapView);
    }
}
