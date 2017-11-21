import jade.core.Agent;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.FIPAException;
import jade.core.behaviours.*;
import jade.proto.SimpleAchieveREInitiator;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.domain.FIPANames;
import java.io.IOException;
import java.util.ArrayList;
import jade.proto.SubscriptionInitiator;
import jade.lang.acl.MessageTemplate;
import jade.core.behaviours.TickerBehaviour;

import java.awt.List;
import java.io.Serializable;

public class ProfilerAgent extends Agent {

    UserProfile profile;
    List list;
    ArrayList<AID> guides;
    ArrayList<Artifact> profilescollection;
    int currentArt;

    protected void setup() {
        currentArt = 0;
        list = new List();

        // create user profile
        profile = new UserProfile(1500, 1800);

        // subscribe to new guides on DF
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType("guide");
        Behaviour b = new SubscriptionInitiator(this,
                DFService.createSubscriptionMessage(this, getDefaultDF(), template, null)) {
            protected void handleInform(ACLMessage inform) {
                try {
                    DFAgentDescription[] dfds = DFService.decodeNotification(inform.getContent());
                    if (dfds.length > 0) {

                        System.out.println("Profiler: Got subscription message from DF");
                        //Refresh the guides and update the list
                        guides = findGuides();
                        updateList();
                        for (int i=0; i<list.countItems(); i++)
                            System.out.println(list.getItem(0)); // print out the available guides
                    }
                } catch (FIPAException fe) {
                    fe.printStackTrace();
                }
            }
        };
        addBehaviour(b);

        // after 2 seconds, get tour from first guide
        addBehaviour(new WakerBehaviour(this, 2000) {

            @Override
            public void onWake() {
                ACLMessage msg = new ACLMessage(ACLMessage.CFP);
                msg.addReceiver(guides.get(0));
                try {
                    msg.setContentObject(profile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                send(msg);

                // Receive message with the tour
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
                ACLMessage reply = blockingReceive(mt);
                if (reply != null) {
                    try {
                        profilescollection = (ArrayList) reply.getContentObject();
                        System.out
                                .println("Profiler: got custom collection! (" + profilescollection.size() + ") items");
                    } catch (UnreadableException e) {
                        e.printStackTrace();
                    }
                }

                // start asking curator - every second
                addBehaviour(new TickerBehaviour(myAgent, 1000) {
                    @Override
                    public void onTick() {
                        if (currentArt < profilescollection.size()) {
                            // if we havent asked about every art yet

                            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                            msg.addReceiver(findService("curator"));
                            send(msg);

                            // receive reply
                            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
                            ACLMessage reply = blockingReceive(mt);
                            if (reply != null) {
                                System.out.println("Profiler got information from curator about "
                                        + profilescollection.get(currentArt).name + "! - " + reply.getContent());
                            }

                            currentArt++; // increment the counter

                        } else {
                            this.stop(); // stop asking!
                        }
                    }
                });
            }
        });

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


    /**
     * Updates the list of guides in the GUI.
     * */
    private void updateList() {
        list.removeAll();

        if (guides != null) {
            for (AID guide : guides) {
                list.add(guide.getLocalName());
            }
        }
    }

    /**
    * Find all registered guides.
    * */
    private ArrayList<AID> findGuides() {
        DFAgentDescription description = new DFAgentDescription();
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType("guide");
        description.addServices(serviceDescription);
        try {
            DFAgentDescription[] resultAgentDescriptions = DFService.search(this, description);
            if (resultAgentDescriptions.length > 0) {
                ArrayList<AID> guides = new ArrayList<AID>();
                for (int i = 0; i < resultAgentDescriptions.length; i++) {
                    guides.add(resultAgentDescriptions[i].getName());
                }
                return guides;
            }
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        return null;
    }

}

class UserProfile implements Serializable {
    public int minAge;
    public int maxAge;

    public UserProfile(int min, int max) {
        this.minAge = min;
        this.maxAge = max;
    }
}