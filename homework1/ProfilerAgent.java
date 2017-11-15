import jade.core.Agent;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.FIPAException;
import jade.core.behaviours.*;
import jade.proto.SimpleAchieveREInitiator;
import jade.lang.acl.ACLMessage;

public class ProfilerAgent extends Agent {

    protected void setup() {

        // create user profile
        String profile = "";

        // following behaviours are executed sequentially
        SequentialBehaviour sequentialBehaviour = new SequentialBehaviour();

        // request tour - simpleachievereinitiator
        sequentialBehaviour.addSubBehaviour(new SimpleAchieveREInitiator(this, null) {
            @Override
            protected void handleInform(ACLMessage msg) {
                // TODO
            }
        });

        // ask tour details - simpleachievereinitiator
        sequentialBehaviour.addSubBehaviour(new SimpleAchieveREInitiator(this, null) {
            @Override
            protected void handleInform(ACLMessage msg) {
                // TODO
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
}