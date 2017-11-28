import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.core.Runtime;

public class Controller {

	public static void main(String[] args) {
		try {
			Runtime rt =  Runtime.instance();
			rt.setCloseVM(true);

			AgentContainer agentContainer = rt.createMainContainer(new ProfileImpl("localhost", 8080, null));
			AgentController rma = agentContainer.createNewAgent("rma", jade.tools.rma.rma.class.getName(), new Object[0]);
			rma.start();
			
			AgentContainer auctioneerContainer = rt.createAgentContainer(new ProfileImpl("localhost", 8080, "auctioneers"));
			AgentController hmAgentController = auctioneerContainer.createNewAgent("Curator", CuratorAgent.class.getName(), new String[] {"150","10","80"});
			hmAgentController.start();
			
			AgentContainer participantContainer = rt.createAgentContainer(new ProfileImpl("localhost", 8080, "participants"));
			AgentController galileoAgentController = participantContainer.createNewAgent("Profiler1", ProfilerAgent.class.getName(), new String[] {"100"});
			galileoAgentController.start();
			AgentController galileoAgentController2 = participantContainer.createNewAgent("Profiler2", ProfilerAgent.class.getName(), new String[] {"100"});
			galileoAgentController2.start();
			
			AgentContainer auction1Container = rt.createAgentContainer(new ProfileImpl("localhost", 8080, "auction1"));
			AgentContainer auction2Container = rt.createAgentContainer(new ProfileImpl("localhost", 8080, "auction2"));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
