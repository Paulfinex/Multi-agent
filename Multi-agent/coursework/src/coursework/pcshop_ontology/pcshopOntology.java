package coursework.pcshop_ontology;

import jade.content.onto.BeanOntology;
import jade.content.onto.BeanOntologyException;
import jade.content.onto.Ontology;

public class pcshopOntology extends BeanOntology{
	
	private static Ontology theInstance = new pcshopOntology("my_ontology");
	
	public static Ontology getInstance(){
		return theInstance;
	}
	//singleton pattern
	private pcshopOntology(String name) {
		super(name);
		try {
			add("coursework.pcshop_ontology.elements");
		} catch (BeanOntologyException e) {
			e.printStackTrace();
		}
	}
}
