package coursework.pcshop_ontology.elements;

import jade.content.onto.annotations.Slot;

public class HD extends component {
	private int HD_size;
	
	@Slot(mandatory = true)
	public int getSize() {
		return HD_size;
	}
	
	public void setSize(int size) {
		this.HD_size = size;
	}
}
