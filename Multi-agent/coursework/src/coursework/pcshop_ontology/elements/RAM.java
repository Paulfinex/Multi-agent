package coursework.pcshop_ontology.elements;

import jade.content.onto.annotations.Slot;

public class RAM extends component {
	private int RAM_size;
	
	@Slot(mandatory = true)
	public int getSize() {
		return RAM_size;
	}
	
	public void setSize(int size) {
		this.RAM_size = size;
	}
}
