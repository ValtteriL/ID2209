import jade.core.Agent;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.FIPAException;
import jade.core.behaviours.*;
import jade.proto.SimpleAchieveREResponder;
import jade.lang.acl.ACLMessage;

public class CuratorAgent extends Agent {

    String[] collection;

    protected void setup() {

        // register to DF
        register();

        // create art collection - oneshot
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                // TODO
            }
        });

        // respond to tour guide - simpleachieveresponder
        addBehaviour(new SimpleAchieveREResponder(this, null) {
            @Override
            protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
                // TODO
                return null;
            }
        });

        // respond to profileragent - cyclicbehaviour
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null)
                    System.out.println(" - " + myAgent.getLocalName() + " <- " + msg.getContent());
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.INFORM);
                reply.setContent("Pong");
                send(reply);
                block(); // <- schedule execution until next message received
            }
        });
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