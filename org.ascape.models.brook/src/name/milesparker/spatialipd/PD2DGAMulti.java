/*
 * Copyright 2000 Miles T. Parker. 
 * All rights reserved.
 * This program and the accompanying materials are made available solely under of the BSD license "brookings-models-license.txt".
 * Any referenced or included libraries carry licenses of their respective copyright holders. 
 */
package name.milesparker.spatialipd;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ascape.model.Agent;
import org.ascape.model.rule.Rule;
import org.ascape.util.data.StatCollectorCSAMM;
import org.ascape.view.nonvis.DataScape;

/*
 * This software is confidential and proprietary to
 * NuTech Solutions, Inc.  No portion of this software may
 * be reproduced, published, used, or disclosed
 * to others without the WRITTEN authorization
 * of NuTech Solutions.
 *             Copyright (c) 2004
 *                NuTech Solutions,Inc.
 *
 * NUTECH SOLUTIONS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * OR NON-INFRINGEMENT. NUTECH SOLUTIONS SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * 
 *
 * User: Miles Parker
 * Date: Feb 9, 2005
 * Time: 11:55:02 AM
 */

public class PD2DGAMulti extends PD2DGA implements DataScape {

    /**
     * 
     */
    private static final long serialVersionUID = 2714970231178689621L;
    int rankSize = 8;

    public void writePeriodHeader(DataOutputStream os) throws IOException {
    }

    public void writePeriodData(DataOutputStream os) throws IOException {
    }

    class WealthCollector extends StatCollectorCSAMM {
        /**
         * 
         */
        private static final long serialVersionUID = -6751951496344134801L;

        public double getValue(Object object) {
            return ((PlayerGA) object).getWealth();
        }
    }

    public void writeRunHeader(DataOutputStream os) throws IOException {
        for (int i = 0; i < rankSize; i++) {
            os.writeBytes("\t" + "Genome " + i);
            os.writeBytes("\t" + "Count " + i);
//            os.writeBytes("\t" + "Wealth " + i);
        }
    }

    public void writeRunData(DataOutputStream os) throws IOException {
        final Map genomeForStats = new HashMap();
        getAgents().executeOnMembers(new Rule("Genome Stats") {
            /**
             * 
             */
            private static final long serialVersionUID = -8053932730990368821L;

            public void execute(Agent agent) {
                PlayerGA player = (PlayerGA) agent;
                StatCollectorCSAMM stat = (StatCollectorCSAMM) genomeForStats.get(player.getChromosomeStrategyAsString());
                if (stat == null) {
                    stat = new StatCollectorCSAMM(player.getChromosomeStrategyAsString());
                    genomeForStats.put(player.getChromosomeStrategyAsString(), stat);
                }
                stat.addValueFor(agent);
            }
        });
        List rankedGenomes = new ArrayList(genomeForStats.values());
        Collections.sort(rankedGenomes, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((StatCollectorCSAMM) o2).getCount() - ((StatCollectorCSAMM) o1).getCount();
            }
        });
        for (int i = 0; (i < rankSize) && (i < rankedGenomes.size()); i++) {
            StatCollectorCSAMM stat = (StatCollectorCSAMM) rankedGenomes.get(i);
            os.writeBytes("\t" + stat.getName());
            os.writeBytes("\t" + stat.getCount());
//            os.writeBytes("\t" + stat.getTotal());
        }
        os.writeBytes("\t" + "Test run data");
    }
}
