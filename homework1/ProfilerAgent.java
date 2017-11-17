import jade.core.Agent;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.FIPAException;
import jade.core.behaviours.*;
import jade.proto.SimpleAchieveREInitiator;
import jade.lang.acl.ACLMessage;
import java.io.Serializable;
import jade.lang.acl.UnreadableException;
import jade.domain.FIPANames;
import java.io.IOException;
import java.util.ArrayList;
import jade.proto.SubscriptionInitiator;
import jade.lang.acl.MessageTemplate;
import jade.core.behaviours.TickerBehaviour;

import javax.swing.BoxLayout;

import java.awt.Frame;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ProfilerAgent extends Agent {

    UserProfile profile;
    String tour;
    String artdata;
    List list;
    ArrayList<AID> guides;

    protected void setup() {

        // create user profile
        profile = new UserProfile();

        createGuideSelectionGUI();

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
                    }
                } catch (FIPAException fe) {
                    fe.printStackTrace();
                }
            }
        };
        addBehaviour(b);

        // after 5 seconds, get tour from first guide
        addBehaviour(new WakerBehaviour(this, 5000) {

            @Override
            public void onWake() {
                ACLMessage msg = new ACLMessage(ACLMessage.CFP);
                msg.addReceiver(guides.get(0));
                msg.setContent("profile"); // TODO send guide our profile
                send(msg);

                // Receive message with the tour
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
                ACLMessage reply = blockingReceive(mt);
                if (reply != null) {
                    System.out.println("Profiler got tour! - " + reply.getContent());
                    tour = reply.getContent();
                }

                // start asking curator - every second
                addBehaviour(new TickerBehaviour(myAgent, 1000) {
                    @Override
                    public void onTick() {
                        // ask
                        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                        msg.addReceiver(findService("curator"));
                        send(msg);

                        // receive reply
                        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
                        ACLMessage reply = blockingReceive(mt);
                        if (reply != null) {
                            System.out.println("Profiler got information from curator! - " + reply.getContent());
                        }

                        //this.stop(); // stop asking!
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
    * Creates a simple GUI to select a guide for a tour.
    * */
    private void createGuideSelectionGUI() {
        final Frame frame = new Frame();
        Panel panel = new Panel();

        BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.PAGE_AXIS);
        panel.setLayout(boxLayout);

        Label label = new Label("Guides available");

        list = new List();
        list.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent event) {
                // getTour(list.getSelectedIndex()); 
            }
        });

        if (guides != null) {
            for (AID guide : guides) {
                list.add(guide.getLocalName());
            }
        }

        panel.add(label);
        panel.add(list);

        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                doDelete();
                frame.dispose();
            }
        });
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

    private int interestCentury;

    public UserProfile() {
        interestCentury = (int) (Math.random() * 500 + 1500);
    }
}