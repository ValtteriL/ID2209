import jade.core.Agent;
import jade.domain.DFService;
import jade.core.behaviours.*;

public class ProfilerAgent extends Agent {

    int highestbid; // highest bid this agent is willing to pay

    protected void setup() {
        // handle arguments, argument is highestbid
        Object[] args = getArguments();
        highestbid = Integer.parseInt(args[0].toString());

        // register
        register();

        // add behaviour to respond to bids
        addBehaviour(new CyclicBehaviour(this) {
            // do some auction here
        });
    }

    // code for registering to DF as profiler
    void register() {
        ServiceDescription sd = new ServiceDescription();
        sd.setType("profiler");
        sd.setName(getLocalName());

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }
}
