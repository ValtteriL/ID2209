import jade.core.Agent;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.FIPAException;
import jade.core.behaviours.*;
import jade.proto.SimpleAchieveREInitiator;
import jade.lang.acl.ACLMessage;
import jade.proto.states.MsgReceiver;
import jade.lang.acl.MessageTemplate;

public class GuideAgent extends Agent {

    String tour;

    protected void setup() {

        // register to DF
        register();

        // wait for profiler to ask about the tour
        MessageTemplate trmt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
        addBehaviour(new MsgReceiver(this, trmt, Long.MAX_VALUE, null, null) {
            @Override
            protected void handleMessage(ACLMessage tourRequest) {
                super.handleMessage(tourRequest);
                System.out.println("Guide: Someone is asking for tour...");
                // int age = (int) tourRequest.getContent(); // TODO: should we use the age somehow?

                // ask curator for art
                System.out.println("Guide: Creating art tour...");
                ACLMessage msg = new ACLMessage(ACLMessage.CFP);
                msg.addReceiver(findService("curator"));
                send(msg);

                // receive message with art
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
                ACLMessage reply = blockingReceive(mt);
                if (reply != null) {
                    System.out.println("Guide got reply! - " + reply.getContent());
                    tour = reply.getContent();
                }

                // TODO HERE: modify received art to correspond to profilers interests

                // reply for profiler
                ACLMessage tourReply = tourRequest.createReply();
                tourReply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                tourReply.setContent(tour);
                send(tourReply);
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

    // Find service by service type
    private AID findService(String servicetype) {
        DFAgentDescription description = new DFAgentDescription();
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(servicetype);
        description.addServices(serviceDescription);
        try {
            DFAgentDescription[] resultAgentDescriptions = DFService.search(this, description);
            if (resultAgentDescriptions.length > 0) {
                return resultAgentDescriptions[0].getName();
            }
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        return null;
    }
}