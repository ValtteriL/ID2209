import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.ArrayList;

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


public class CuratorAgent extends Agent {

    int starterbid; // auction starts at this price
    int lowestbid; // auction stops if no-one bids with this price
    int reduceamount; // price will be reduced this amount between cfp:s
    ArrayList<AID> profilers; // arraylist of all profilers found
    boolean isSold;

    protected void setup() {
        isSold = false; // item not sold yet!

        // handle arguments, arguments are starterbid, reduceamount, lowestbid
        Object[] args = getArguments();
        this.starterbid = Integer.parseInt(args[0].toString());
        this.reduceamount = Integer.parseInt(args[1].toString());
        this.lowestbid = Integer.parseInt(args[2].toString());

        // after profilers have registered themselves, start the auction
        addBehaviour(new WakerBehaviour(this, 2000) {

            @Override
            public void onWake() {
                // find all the profilers first
                profilers = findProfilers();

                // tell everyone the auction has started
                inform("start");

                // then do some auction, lower the price every 3 seconds
                addBehaviour(new TickerBehaviour(myAgent, 3000) {
                    @Override
                    public void onTick() {
                        if (!isSold) {
                            // not sold, sell it

                            // check that we've not lowered the price too much!
                            if (starterbid < lowestbid) {
                                System.out.println(myAgent.getLocalName() + ": Price fell below lowest acceptable, closing auction");
                                inform("nobids"); // inform that closing auction without bids
                                myAgent.doDelete();
                            }

                            System.out.println(myAgent.getLocalName()+ ": Asking " + starterbid + " for the item...");
                            cfp(); // send cfp to each profiler

                            starterbid = starterbid - reduceamount; // update price
                        } else {
                            // item has been already sold
                            System.out.println(myAgent.getLocalName()+ ": Item has been sold. Closing the auction");
                            inform("sold"); // tell everyone the item was sold
                            myAgent.doDelete();
                        }
                    }
                });
            }
        });

        // add behaviour to get responses to bids
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
                ACLMessage msg = receive(mt);
                if (msg != null) {
                            // received proposal!
                            ACLMessage reply = msg.createReply();
                            if (!isSold && Integer.parseInt(msg.getContent()) >= starterbid) {
                                // if proposal good enough, accept it
                                System.out.println(myAgent.getLocalName() + ": Selling item for "+ msg.getContent());
                                reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                                isSold = true; // mark item as sold
                            } else {
                                // the item's been sold or the proposal is too low, reject!
                                System.out.println(myAgent.getLocalName() + ": Got offer when the item has already been sold or the offer is already too low! Rejecting");
                                reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                            }
                            reply.setContent(msg.getContent());
                            send(reply);
                        }
                    block(); // <- schedule execution until next message received
                }
        });
    }

    // inform participants about start, sold or no bids
    private void inform(String reason) {
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);

        for (AID profiler : profilers) {
			message.addReceiver(profiler);
        }

        message.setOntology(reason);
        send(message);
    }

    // call for proposals using the current price
    private void cfp() {
        ACLMessage message = new ACLMessage(ACLMessage.CFP);

        for (AID profiler : profilers) {
			message.addReceiver(profiler);
        }

        message.setContent(Integer.toString(this.starterbid));
        send(message);
    }

    // Find all registered profilers.
    private ArrayList<AID> findProfilers() {
        DFAgentDescription description = new DFAgentDescription();
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType("profiler");
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
