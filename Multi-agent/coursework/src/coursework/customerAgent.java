package coursework;

import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
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

public class customerAgent extends Agent {
	// Change verbose value from 0 to 2 to change the amount of information output
	// in the console
	private int verbose = 0;
	private Codec codec = new SLCodec();
	private Ontology ontology = pcshopOntology.getInstance();
	private AID shopAgent = new AID("shop", AID.ISLOCALNAME);
	private AID tickerAgent;
	private CustOrder custOrder;

	protected void setup() {
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		if (verbose > 0)
			System.out.println("customer-agent " + getAID().getName() + " is ready.");
		// Register the customer in the yellow pages
		if (verbose == 2) {
			System.out.println(this.getAID().getName() + " Registering to the yellow pages");
		}
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("customer");
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
			MessageTemplate mt = MessageTemplate.or(MessageTemplate.MatchContent("new day"),
					MessageTemplate.MatchContent("terminate"));
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				if (tickerAgent == null) {
					tickerAgent = msg.getSender();
				}
				if (msg.getContent().equals("new day")) {
					// spawn new sequential behaviour for day ’s activities
					SequentialBehaviour dailyActivity = new SequentialBehaviour();
					// sub - behaviours will execute in the order they are added
					dailyActivity.addSubBehaviour(new MakeOrder(myAgent));
					dailyActivity.addSubBehaviour(new EndDay(myAgent));

					myAgent.addBehaviour(dailyActivity);
				} else {
					// termination message to end simulation
					if (verbose > 0)
						System.out.println(myAgent.getAID().getName() + " terminating.");
					myAgent.doDelete();
				}
			} else {
				block();
			}
		}

		// Create a new order
		public class MakeOrder extends OneShotBehaviour {

			public MakeOrder(Agent a) {
				super(a);
				myAgent = a;
			}

			@Override
			public void action() {

				motherboard mb = new motherboard();
				CPU cpu = new CPU();
				RAM ram = new RAM();
				HD hd = new HD();
				// Randomize PC parts
				if (Math.random() < 0.5) {
					cpu.setModel("Mintel");
					mb.setModel("Mintel");
				} else {
					cpu.setModel("IMD");
					mb.setModel("IMD");
				}

				if (Math.random() < 0.5) {
					ram.setSize(4);
				} else {
					ram.setSize(16);
				}

				if (Math.random() < 0.5) {
					hd.setSize(1);
				} else {
					hd.setSize(2);
				}
				// Create the PC for the order
				PC pc = new PC();
				pc.setCPU(cpu);
				pc.setHD(hd);
				pc.setMB(mb);
				pc.setRAM(ram);

				// Randomize attributes for the order
				int quantity = (int) Math.floor(1 + (50 * Math.random()));
				int price = quantity * (int) Math.floor(500 + (200 * Math.random()));
				int days = (int) Math.floor(1 + (10 * Math.random()));

				// Create the Customer order
				CustOrder co = new CustOrder();
				co.setCustomer(myAgent.getAID());
				co.setDays(days);
				co.setPc(pc);
				co.setPrice(price);
				co.setQuantity(quantity);
				if (verbose == 2) {
					System.out.println(myAgent.getAID().getName() + " created the following order: " + co.toString());
				}
				;

				// Send the order to the shop
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
				msg.addReceiver(shopAgent);
				msg.setLanguage(codec.getName());
				msg.setOntology(ontology.getName());
				Action request = new Action();
				request.setAction(co);
				request.setActor(shopAgent);
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
}
