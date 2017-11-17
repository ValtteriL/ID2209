import jade.core.Agent;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.FIPAException;
import jade.core.behaviours.*;
import jade.proto.SimpleAchieveREResponder;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class CuratorAgent extends Agent {

    String[] collection;

    protected void setup() {

        // register to DF
        register();

        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();

        // respond to Guideagent
        parallelBehaviour.addSubBehaviour(new OneShotBehaviour(this) {
            @Override
            public void action() {
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
                ACLMessage msg = blockingReceive(mt);
                if (msg != null) {
                    System.out.println("Curator: got msg from Guide!");
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                    reply.setContent("Pong");
                    send(reply);
                }
                block(); // <- schedule execution until next message received
            }
        });

        // respond to profileagent
        parallelBehaviour.addSubBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
                ACLMessage msg = receive(mt);
                if (msg != null) {
                    System.out.println("Curator: Got msg from Profiler");
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent("Pong");
                    send(reply);
                }
                block(); // <- schedule execution until next message received
            }
        });

        addBehaviour(parallelBehaviour);
    }

    // code for registering to DF
    void register() {
        ServiceDescription sd = new ServiceDescription();
        sd.setType("curator");
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