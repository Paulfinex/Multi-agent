package coursework.pcshop_ontology.elements;

import jade.content.onto.annotations.Slot;

public class CPU extends component {

	private String cpu_model;
	
	@Slot(mandatory = true)
	public String getModel() {
		return cpu_model;
	}
	
	public void setModel(String model) {
		this.cpu_model = model;
	}

}
