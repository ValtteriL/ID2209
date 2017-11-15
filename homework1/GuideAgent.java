import jade.core.Agent;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.FIPAException;
import jade.core.behaviours.*;
import jade.proto.SimpleAchieveREInitiator;
import jade.lang.acl.ACLMessage;
import jade.proto.states.MsgReceiver;

public class GuideAgent extends Agent {

    protected void setup() {

        // register to DF
        register();

        // create art tour - simpleachievereinitiator
        addBehaviour(new SimpleAchieveREInitiator(this, null) {
            @Override
            protected void handleInform(ACLMessage msg) {
                // TODO
            }
        });

        // the following tasks are executed in parallel
        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();

        // add closing time clock
        parallelBehaviour.addSubBehaviour(new WakerBehaviour(this, 60000) {
            @Override
            protected void handleElapsedTimeout() {
                // TODO
            }
        });

        // add message receiver - msgreceiver that calls simpleachievereinitiator when msg received
        parallelBehaviour.addSubBehaviour(new MsgReceiver(this, null, 0, null, null) {
            @Override
            protected void handleMessage(ACLMessage msg) {
                // TODO
            }
        });
    }

    // code for registering to DF
    void register() {
        ServiceDescription sd = new ServiceDescription();
        sd.setType("guide");
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