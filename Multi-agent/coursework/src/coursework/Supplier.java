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

public class Supplier extends Agent {
	// Change verbose value from 0 to 2 to change the amount of information output
	// in the console
	private int verbose = 0;
	private Codec codec = new SLCodec();
	private Ontology ontology = pcshopOntology.getInstance();
	private AID tickerAgent;
	private ArrayList<CustOrder> coList = new ArrayList<>();

	protected void setup() {
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		if (verbose > 0)
			System.out.println("supplier-agent " + getAID().getName() + " is ready.");
		// Register the supplier service in the yellow pages
		if (verbose == 2) {
			System.out.println(this.getAID().getName() + " Registering to the yellow pages");
		}
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("supplier1");
		sd.setName("JADE-shop");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		addBehaviour(new TickerWaiter(this));

	}

	public class TickerWaiter extends CyclicBehaviour {

		// Wait for a new day
		public TickerWaiter(Agent a) {
			super(a);
		}

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
					MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage msg = myAgent.receive(mt);

			if (msg != null) {
				switch (msg.getPerformative()) {
				case (ACLMessage.REQUEST):
					ContentElement ce = null;
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
						// Check if the message is an instance of Customer Order
						if (action instanceof CustOrder) {
							CustOrder co = (CustOrder) action;
							// Set delivery time to 1 day and add order to the list
							co.setDays(1);
							coList.add(co);
						}
					}

					break;

				case (ACLMessage.INFORM):

					if (msg.getContent().equals("new day")) {
						if (tickerAgent == null) {
							tickerAgent = msg.getSender();
						}
						OrderDayCounter();
						SequentialBehaviour dailyActivity = new SequentialBehaviour();
						dailyActivity.addSubBehaviour(new SendParts(myAgent));
						dailyActivity.addSubBehaviour(new EndDay(myAgent));
						myAgent.addBehaviour(dailyActivity);
					} else {
						// termination message to end simulation
						if (verbose > 0)
							System.out.println(myAgent.getAID().getName() + " terminating.");
						myAgent.doDelete();
					}

					break;

				}

			} else

			{
				block();
			}
		}

		// Send parts to the manufacturer
		public class SendParts extends OneShotBehaviour {

			public SendParts(Agent a) {
				super(a);
			}

			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			Action request = new Action();

			@Override
			public void action() {
				for (CustOrder c : coList) {
					if (c.getDays() == 0) {
						// Create a new supplier order and fills it with the pieces, setting up the
						// quantity and price
						SuppOrder so = new SuppOrder();
						so.setCpu(c.getPc().getCPU());
						so.setHd(c.getPc().getHD());
						so.setMb(c.getPc().getMB());
						so.setRam(c.getPc().getRAM());
						so.setQuantity(c.getQuantity());
						so.setPrice(CalcPrice(c.getPc(), c.getQuantity()));
						// Send a message to the manufacturer with the SuppOrder as content
						msg.addReceiver(c.getCustomer());
						msg.setLanguage(codec.getName());
						msg.setOntology(ontology.getName());
						request.setAction(so);
						request.setActor(c.getCustomer());
						try {
							getContentManager().fillContent(msg, request);
							send(msg);
						} catch (CodecException ce) {
							ce.printStackTrace();
						} catch (OntologyException oe) {
							oe.printStackTrace();
						}
					}
				}

			}

		}

		public class EndDay extends OneShotBehaviour {

			public EndDay(Agent a) {
				super(a);
			}

			@Override
			public void action() {
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.addReceiver(tickerAgent);
				msg.setContent("done");
				myAgent.send(msg);
			}
		}
	}

	// Updates the days in the order list
	private void OrderDayCounter() {
		for (CustOrder co : coList) {
			co.setDays(co.getDays() - 1);
		}
		for (CustOrder co : new ArrayList<CustOrder>(coList)) {
			if (co.getDays() < 0) {
				coList.remove(co);
			}
		}

	}

	// Calculate the price of an order
	private int CalcPrice(PC pc, int quantity) {
		int price = 0;
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
		return price * quantity;
	}
}
