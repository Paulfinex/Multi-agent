package coursework.pcshop_ontology.elements;

import jade.content.Concept;
import jade.content.onto.annotations.Slot;

public class component implements Concept {

	private int ID;
	
	@Slot(mandatory = false)
	public int getID() {
		return ID;
	}
	
	public void setID(int ID) {
		this.ID = ID;
	}
}
