import jade.core.Agent;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.FIPAException;

public class GuideAgent extends Agent {

    protected void setup() {
        // register to DF
        register();

        // create art tour - simpleachievereinitiator
        addBehaviour(new SimpleAchieveREInitiator(this) {
            @Override
            public void action() {
                // TODO
            }
        });

        // the following tasks are executed in parallel
        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();

        // add closing time clock
        parallelBehaviour.addSubBehaviour(new WakerBehaviour(this, 60000) {
            @Override
            public void action() {
                // TODO
            }
        });

        // add message receiver - msgreceiver that calls simpleachievereinitiator when msg received
        parallelBehaviour.addSubBehaviour(new MsgReceiver(this) {
            @Override
            public void action() {
                // TODO
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
}