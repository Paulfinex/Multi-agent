package coursework;

import jade.core.*;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class main {

	public static void main(String[] args) {
		Profile myProfile = new ProfileImpl();
		Runtime myRuntime = Runtime.instance();
		ContainerController myContainer = myRuntime.createMainContainer(myProfile);
		try {
			// Starting all the agents
			AgentController rma = myContainer.createNewAgent("rma", "jade.tools.rma.rma", null);
			rma.start();
			AgentController clockAgent = myContainer.createNewAgent("clock", ClockAgent.class.getCanonicalName(), null);
			clockAgent.start();
			AgentController shopAgent = myContainer.createNewAgent("shop", shopAgent.class.getCanonicalName(), null);
			shopAgent.start();
			AgentController supplierAgent1 = myContainer.createNewAgent("supplier1", Supplier.class.getCanonicalName(),
					null);
			supplierAgent1.start();
			AgentController supplierAgent2 = myContainer.createNewAgent("supplier2", Supplier2.class.getCanonicalName(),
					null);
			supplierAgent2.start();
			AgentController customerAgent1 = myContainer.createNewAgent("customer1",
					customerAgent.class.getCanonicalName(), null);
			customerAgent1.start();
			AgentController customerAgent2 = myContainer.createNewAgent("customer2",
					customerAgent.class.getCanonicalName(), null);
			customerAgent2.start();
			AgentController customerAgent3 = myContainer.createNewAgent("customer3",
					customerAgent.class.getCanonicalName(), null);
			customerAgent3.start();
			AgentController customerAgent4 = myContainer.createNewAgent("customer4",
					customerAgent.class.getCanonicalName(), null);
			customerAgent4.start();

		} catch (Exception e) {
			System.out.println("Exception starting agent: " + e.toString());
		}

	}

}
