package coursework.pcshop_ontology.elements;

import jade.content.Concept;
import jade.content.onto.annotations.Slot;

public class PC implements Concept {

	private int ID;
	private motherboard MB;
	private HD HD;
	private CPU CPU;
	private RAM RAM;
	
	@Slot(mandatory = true)
	public int getID() {
		return ID;
	}
	
	public void setID(int ID) {
		this.ID = ID;
	}
	
	@Slot(mandatory = true)
	public HD getHD() {
		return HD;
	}
	
	public void setHD(HD HD) {
		this.HD = HD;
	}
	
	@Slot(mandatory = true)
	public CPU getCPU() {
		return CPU;
	}
	
	public void setCPU(CPU CPU) {
		this.CPU = CPU;
	}
	
	@Slot(mandatory = true)
	public RAM getRAM() {
		return RAM;
	}
	
	public void setRAM(RAM RAM) {
		this.RAM = RAM;
	}
	
	@Slot(mandatory = true)
	public motherboard getMB() {
		return MB;
	}
	
	public void setMB(motherboard MB) {
		this.MB = MB;
	}
}
