import jade.core.Agent;

import java.util.List;

import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.FIPAException;
import jade.core.behaviours.*;
import jade.proto.SimpleAchieveREResponder;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.leap.Serializable;
import java.util.ArrayList;
import java.io.IOException;


public class CuratorAgent extends Agent {

    ArrayList<Artifact> collection;

    protected void setup() {

        // create art collection
        collection = new ArrayList<Artifact>();
        generateCollection();

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
                    try {
                        reply.setContentObject(collection);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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

    void generateCollection() {
        collection.add(new Artifact(1, 1000, "a", "A"));
        collection.add(new Artifact(2, 1100, "b", "B"));
        collection.add(new Artifact(3, 1200, "c", "C"));
        collection.add(new Artifact(4, 1300, "d", "D"));
        collection.add(new Artifact(5, 1400, "e", "E"));
        collection.add(new Artifact(6, 1500, "f", "F"));
        collection.add(new Artifact(7, 1600, "g", "G"));
        collection.add(new Artifact(8, 1700, "j", "J"));
        collection.add(new Artifact(9, 1800, "i", "I"));
        collection.add(new Artifact(10, 1900, "l", "L"));
    }
}

class Artifact implements Serializable {

    public int age;
    public int id;
    public String name;
    public String author;

    public Artifact(int artifactid, int artifactage, String artifactname, String artifactauthor) {
        this.id = artifactid;
        this.age = artifactage;
        this.name = artifactname;
        this.author = artifactauthor;
    }
}