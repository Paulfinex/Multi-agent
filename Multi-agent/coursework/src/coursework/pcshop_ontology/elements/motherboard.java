package coursework.pcshop_ontology.elements;

import jade.content.onto.annotations.Slot;

public class motherboard extends component {
	private String mb_model;
	
	@Slot(mandatory = true)
	public String getModel() {
		return mb_model;
	}
	
	public void setModel(String model) {
		this.mb_model = model;
	}

}
