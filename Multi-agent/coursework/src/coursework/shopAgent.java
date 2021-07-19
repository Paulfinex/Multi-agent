package coursework;

import java.util.ArrayList;
import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import coursework.pcshop_ontology.pcshopOntology;
import coursework.pcshop_ontology.elements.*;

public class shopAgent extends Agent {
	// Change verbose value from 0 to 2 to change the amount of information output
	// in the console
	private int verbose = 0;
	private Codec codec = new SLCodec();
	private Ontology ontology = pcshopOntology.getInstance();
	private AID tickerAgent;
	private ArrayList<CustOrder> orders = new ArrayList<CustOrder>();
	private Warehouse wh = new Warehouse();
	private AID supplier1;
	private AID supplier2;
	private int totalProfit = 0;
	private int prevProfit= 0;
	protected void setup() {
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		if (verbose > 0)
			System.out.println("shop-agent " + getAID().getName() + " is ready.");
		// Register the shop service in the yellow pages
		if (verbose == 2) {
			System.out.println(this.getAID().getName() + " Registering to the yellow pages");
		}
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("shop");
		sd.setName("JADE-shop");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		addBehaviour(new OneShotBehaviour() {
			public void action() {
				DFAgentDescription template1 = new DFAgentDescription();
				ServiceDescription sd1 = new ServiceDescription();
				sd1.setType("supplier1");
				template1.addServices(sd1);
				DFAgentDescription template2 = new DFAgentDescription();
				ServiceDescription sd2 = new ServiceDescription();
				sd2.setType("supplier2");
				template2.addServices(sd2);
				try {
					DFAgentDescription[] agentsType1 = DFService.search(myAgent, template1);
					supplier1 = agentsType1[0].getName();
					DFAgentDescription[] agentsType2 = DFService.search(myAgent, template2);
					supplier2 = agentsType2[0].getName();
				} catch (FIPAException fe) {
					fe.printStackTrace();
				}

			}

		});
		addBehaviour(new TickerWaiter(this));

	}

	@Override
	protected void takeDown() {
		// Deregister from the yellow pages
		try {
			DFService.deregister(this);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}

	public class TickerWaiter extends CyclicBehaviour {

		// behaviour to wait for a new day
		public TickerWaiter(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage msg = myAgent.receive(mt);
			ContentElement ce = null;
			if (msg != null) {
				switch (msg.getPerformative()) {
				case (ACLMessage.REQUEST):

					try {
						ce = getContentManager().extractContent(msg);
					} catch (UngroundedException e) {
						e.printStackTrace();
					} catch (CodecException e) {
						e.printStackTrace();
					} catch (OntologyException e) {
						e.printStackTrace();
					}
					if (ce instanceof Action) {
						Concept action = ((Action) ce).getAction();
						if (action instanceof CustOrder) {
							CustOrder co = (CustOrder) action;

							EvaluateOrder(co);

						}
						if (action instanceof SuppOrder) {
							SuppOrder so = (SuppOrder) action;
							AddToWarehouse(so);
							totalProfit -= so.getPrice();
						}
					}

					break;

				case (ACLMessage.INFORM):

					if (msg.getContent().equals("new day")) {
						if (tickerAgent == null) {
							tickerAgent = msg.getSender();
						}
						prevProfit=totalProfit;
						if (verbose == 2) {
							System.out.println("Current profit: " + totalProfit);
						}
		
						SequentialBehaviour dailyActivity = new SequentialBehaviour();

						dailyActivity.addSubBehaviour(new Daily(myAgent));
						dailyActivity.addSubBehaviour(new EndDay(myAgent));

						myAgent.addBehaviour(dailyActivity);
					} else {
						// termination message to end simulation					
						if (verbose > 0)
						System.out.println(myAgent.getAID().getName() + " terminating.");
						System.out.println("Simulation ended. Total Profit: " + totalProfit);
						myAgent.doDelete();
					}

					break;

				}

			} else

			{
				block();
			}
		}

		// Daily function routine
		public class Daily extends OneShotBehaviour {

			public Daily(Agent a) {
				super(a);
			}

			@Override
			public void action() {
				OrderDayCounter();
				totalProfit -= wh.Penality();
				Manufacture();
			}
		}

		public class EndDay extends OneShotBehaviour {

			public EndDay(Agent a) {
				super(a);
			}

			@Override
			public void action() {
				if(verbose >0)
				System.out.println("Daily profit: " + (totalProfit-prevProfit));
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.addReceiver(tickerAgent);
				msg.setContent("done");
				myAgent.send(msg);
			}
		}
	}

	// Evaluates if an order is profitable or not (Earning at least 40% of the gross
	// profit)
	private boolean EvaluateOrder(CustOrder co) {

		int profit = 0;
		int supp = 0;
		if (co.getDays() < 4) {
			profit = co.getPrice() - CalculateOrderPrice(co.getPc(), co.getQuantity(), 2);
			supp = 2;
		} else {
			profit = co.getPrice() - CalculateOrderPrice(co.getPc(), co.getQuantity(), 1);
			supp = 1;
		}
		if(orders.size()<=80 ||(profit >20000)) {
		if (profit >10000)

		{
			orders.add(co);
			OrderComponents(co, supp);
			return true;
		}
		}
		return false;
	}

	// Orders components from the supplier
	private void OrderComponents(CustOrder co, int supplier) {
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		Action request = new Action();
		co.setCustomer(this.getAID());
		if (verbose == 2) {
			System.out.println("Sending the follow order to supplier" + supplier + ": " + co.toString());
		}
		switch (supplier) {
		case 1:
			msg.addReceiver(supplier1);
			msg.setLanguage(codec.getName());
			msg.setOntology(ontology.getName());
			request.setAction(co);
			request.setActor(supplier1);
			try {
				getContentManager().fillContent(msg, request);
				send(msg);
			} catch (CodecException ce) {
				ce.printStackTrace();
			} catch (OntologyException oe) {
				oe.printStackTrace();
			}
			break;
		case 2:
			msg.addReceiver(supplier2);
			msg.setLanguage(codec.getName());
			msg.setOntology(ontology.getName());
			request.setAction(co);
			request.setActor(supplier2);
			try {
				getContentManager().fillContent(msg, request);
				send(msg);
			} catch (CodecException ce) {
				ce.printStackTrace();
			} catch (OntologyException oe) {
				oe.printStackTrace();
			}

			break;
		}
	}

	// Local price table from both of the suppliers
	private int CalculateOrderPrice(PC pc, int quantity, int supplier) {
		if (supplier != 1 && supplier != 2) {
			return 0;
		}
		int price = 0;

		switch (supplier) {
		case 1:
			if (pc.getCPU().getModel() == "Mintel") {
				price += 200;
			} else {
				price += 150;
			}
			if (pc.getMB().getModel() == "Mintel") {
				price += 125;
			} else {
				price += 75;
			}
			if (pc.getRAM().getSize() == 16) {
				price += 90;
			} else {
				price += 50;
			}
			if (pc.getHD().getSize() == 2) {
				price += 75;
			} else {
				price += 50;
			}
			break;
		case 2:
			if (pc.getCPU().getModel() == "Mintel") {
				price += 175;
			} else {
				price += 130;
			}
			if (pc.getMB().getModel() == "Mintel") {
				price += 115;
			} else {
				price += 65;
			}
			if (pc.getRAM().getSize() == 16) {
				price += 80;
			} else {
				price += 40;
			}
			if (pc.getHD().getSize() == 2) {
				price += 65;
			} else {
				price += 45;
			}
			break;

		}

		return price * quantity;

	}

	// Add items to the warehouse storage
	private void AddToWarehouse(SuppOrder so) {
		int quantity = so.getQuantity();
		wh.addCpu(so.getCpu(), quantity);
		wh.addHd(so.getHd(), quantity);
		wh.addMb(so.getMb(), quantity);
		wh.addRam(so.getRam(), quantity);
	}

	// Remove items from the warehouse storage
	private void RemoveFromWarehouse(CustOrder co) {
		int quantity = 0 - co.getQuantity();
		wh.addCpu(co.getPc().getCPU(), quantity);
		wh.addHd(co.getPc().getHD(), quantity);
		wh.addMb(co.getPc().getMB(), quantity);
		wh.addRam(co.getPc().getRAM(), quantity);
	}

	// Updates the day count on orders
	private void OrderDayCounter() {
		for (CustOrder co : orders) {
			co.setDays(co.getDays() - 1);
		}
		for (CustOrder co : new ArrayList<CustOrder>(orders)) {
			if (co.getDays() < 0) {
				totalProfit -= 50;
			}
		}
	}

	// Find if an order is possible, then fulfils it
	private void Manufacture() {
		int maxPC = 100;
		//Loop all orders
		for (CustOrder c : new ArrayList<CustOrder>(orders)) {
			//If all the parts are available
			if (wh.canManufacture(c)) {
				//If the number of pcs don't exceed the maximum number allowed
				if (c.getQuantity() <= maxPC)
				{
					// Remove parts to be used, add profit, remove order from list
					RemoveFromWarehouse(c);
					totalProfit += c.getPrice();
					maxPC -= c.getQuantity();
					orders.remove(c);
				}
			}
		}

	}

}
