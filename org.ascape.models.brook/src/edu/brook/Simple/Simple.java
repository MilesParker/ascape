//Simple
//-------------------------------------------------------------------

package edu.brook.Simple;

import java.awt.Color;

import org.ascape.model.HostCell;
import org.ascape.model.Scape;
import org.ascape.model.event.ScapeEvent;
import org.ascape.model.space.Array2DVonNeumann;
import org.ascape.util.data.StatCollector;
import org.ascape.util.data.StatCollectorCSAMM;
import org.ascape.view.vis.ChartView;
import org.ascape.view.vis.Overhead2DView;

//-------------------------------------------------------------------

public class Simple extends Scape {

    public int nAgents = 2;
    private int latticeWidth = 30;
    private int latticeHeight = 30;

    Scape lattice;
    Scape agents;

    Overhead2DView overheadView;

    //----------------------------------------------------------------
    public void createScape() {
        super.createScape();
        lattice = new Scape(new Array2DVonNeumann());
        lattice.setPrototypeAgent(new HostCell());

        //Important: Clear rules so the default iterate rule won't be used
        //for the host cell. The model will run faster, since iterate won't
        //be called on each lattice cell.
        lattice.getRules().clear();

        //Set the size by setting the coordinate at the lattices largest
        //extent.
        lattice.setExtent(latticeWidth, latticeHeight);

        //Set the lattice to expect cells to request updates only when
        //neccesary. This greatly enhances perfomance, but requires us to
        //manually request view updates if a change occurs to a lattice cell.
        //In this case, the only change that might occur to a cell is for an
        //agent to move in or out of it, because an agents strategy does not
        //change, and that is the only aspect of agent state that the view
        //reflects. We do not need to request updates on agent movement, since
        //that is handled automatically by the framework.
        //lattice.setCellsRequestUpdates(true);

        //Create an instance of Agent that we can set state for...
        SimpleAgent simpleAgent = new edu.brook.Simple.SimpleAgent();

        //Set the host scape for the prototype agent,
        //So that all 'agents' members know where they live.
        simpleAgent.setHostScape(lattice);

        //Create a scape for the agents that will live on the lattice.
        //Vectors are appropriate whenever the size of a population will change.
        agents = new Scape();
        agents.setName("Agents");
        agents.addRule(UPDATE_RULE);

        //make the player the prototype that will be used to fill the initial scape.
        agents.setPrototypeAgent(simpleAgent);

        //Tell the agents to execute in by rule order; each rule executes on
        //every agent before the next rule is executed.
        agents.setExecutionOrder(Scape.RULE_ORDER);

        //Finally, we need to add our vectors to the root we created at the
        //beginning. The root scape is the parent and controls its member
        //scapes.
        add(lattice);
        add(agents);

    }

    //----------------------------------------------------------------
    public void createViews() {
        super.createViews();

        StatCollector happy = new StatCollectorCSAMM("Happiness", true) {
            public double getValue(Object object) {
                return ((SimpleAgent) object).getHappiness();
            }
        };

        //Add the new values stats to agents
        agents.addStatCollector(happy);


        //Create a new chart and add it
        ChartView chart = new ChartView();
        agents.addView(chart);

        //And add some of the stat series we've just created to it
        chart.addSeries("Maximum Happiness", Color.blue);

        //Set starting data point we're interested in
        chart.setDisplayPoints(100);

    }

    //----------------------------------------------------------------
    //Set the extent of the scape, which is simply the number of agents
    //we want.
    public void scapeSetup(ScapeEvent e) {
        agents.setSize(nAgents);
    }

    //----------------------------------------------------------------
    public int getNumberOfAgents() {
        return nAgents;
    }

    //----------------------------------------------------------------
    public void setNumberOfAgents(int _nAgents) {
        nAgents = _nAgents;
    }
}
