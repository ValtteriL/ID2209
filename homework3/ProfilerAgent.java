import jade.core.Agent;
import jade.domain.DFService;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

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

public class ProfilerAgent extends Agent {

    int highestbid; // highest bid this agent is willing to pay

    protected void setup() {

        // handle arguments, argument is highestbid
        Object[] args = getArguments();
        highestbid = Integer.parseInt(args[0].toString());

        System.out.print("I GOT ARGUMENT " + highestbid);
        // register TODO - is this visible accross all containers?
        register();

        // // add behaviour to respond to bids
        // addBehaviour(new CyclicBehaviour(this) {
        //     @Override
        //     public void action() {
        //         ACLMessage msg = receive();
        //         if (msg != null) {
        //             switch (msg.getPerformative()) {
        //                 case ACLMessage.CFP: {
        //                     // curator wants some bids
                            
        //                     // get current bid  
        //                     int currentbid = Integer.parseInt(msg.getContent());
        //                     if (currentbid <= highestbid) {
        //                         // suitable price, propose to buy it
        //                         ACLMessage reply = msg.createReply();
        //                         reply.setPerformative(ACLMessage.PROPOSE);
        //                         reply.setContent(Integer.toString(currentbid));
        //                         send(reply);
        //                     }

        //                     // otherwise ignore
        //                     break;
        //                 }
        //                 case ACLMessage.ACCEPT_PROPOSAL: {
        //                     // curator accepted our bid
        //                     System.out.println(myAgent.getLocalName() + ": I bought the item for " + msg.getContent() + "!");
        //                     break;
        //                 }
        //                 case ACLMessage.REJECT_PROPOSAL: {
        //                     // curator rejected our bid
        //                     break;
        //                 }
        //                 case ACLMessage.INFORM: {
        //                     switch (msg.getOntology()) {
        //                         case "start": {
        //                             // the auction starts!
        //                             System.out.println(myAgent.getLocalName() + ": the auction starts NOW!");
        //                             break;
        //                         }
        //                         case "sold": {
        //                             // the item hasbeen sold
        //                             System.out.println(myAgent.getLocalName() + ": the item has been sold.. exiting");
        //                             myAgent.doDelete();
        //                             break;
        //                         }
        //                         case "nobids": {
        //                             // the auctioneer has stopped selling the item because no bids
        //                             System.out.println(myAgent.getLocalName() + ": auction ended without bids.. exiting");
        //                             myAgent.doDelete();
        //                             break;
        //                         }
        //                         default: {
        //                             break;
        //                         }
        //                     }
        //                     break;
        //                 }
        //                 default: {
        //                     break;
        //                 }
        //             }
        //         }
        //         block(); // <- schedule execution until next message received
        //     }
        // });
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
