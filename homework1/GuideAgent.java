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
import jade.lang.acl.UnreadableException;
import java.io.Serializable;
import java.util.ArrayList;
import java.io.IOException;


public class GuideAgent extends Agent {

    ArrayList<Artifact> collection;
    UserProfile user;
    ArrayList guidescollection;

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
                try {
                    user = (UserProfile) tourRequest.getContentObject();
                    System.out.println("Guide: the user has minage = " + user.minAge + " and maxage " + user.maxAge);
                } 
                catch (UnreadableException e) {
                    e.printStackTrace();
                }

                // ask curator for art
                System.out.println("Guide: Creating art tour...");
                ACLMessage msg = new ACLMessage(ACLMessage.CFP);
                msg.addReceiver(findService("curator"));
                send(msg);

                // receive message from curator with collection
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
                ACLMessage reply = blockingReceive(mt);
                if (reply != null) {
                    try {
                        guidescollection = (ArrayList) reply.getContentObject();
                        System.out.println("Guide: got art collection from curator! (" + guidescollection.size() + ") items");
                    } 
                    catch (UnreadableException e) {
                        e.printStackTrace();
                    }
                }

                // create custom collection for the user
                ArrayList<Artifact> customized = customizeCollection(user, guidescollection);

                // reply for profiler
                ACLMessage tourReply = tourRequest.createReply();
                tourReply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                try {
                    tourReply.setContentObject(customized);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    // Code for finding service by service type
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

    // Code for choosing right pieces of art for user
    private ArrayList<Artifact> customizeCollection(UserProfile profile, ArrayList<Artifact> fullList) {
        ArrayList<Artifact> customizedlist = new ArrayList<Artifact>();
        for (int i=0; i<fullList.size(); i++) {
            Artifact temp = fullList.get(i);
            if (temp.age <= profile.maxAge && temp.age >= profile.minAge) {
                // suitable piece of art!
                customizedlist.add(temp);
            }      
        }
        return customizedlist;
    }
}
