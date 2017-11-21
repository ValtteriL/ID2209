import jade.core.Agent;
import jade.core.behaviours.*;

public class CuratorAgent extends Agent {

    int starterbid; // auction starts at this price
    int lowestbid; // auction stops if no-one bids with this price

    protected void setup() {
        // handle arguments, arguments are starterbid and lowestbid
        Object[] args = getArguments();
        starterbid = Integer.parseInt(args[0].toString());
        lowestbid = Integer.parseInt(args[1].toString());
        

        // after profilers have registered themselves, start the auction
        addBehaviour(new WakerBehaviour(this, 2000) {
            // find all the profilers first
            // then do some auction
        });
    }
}
