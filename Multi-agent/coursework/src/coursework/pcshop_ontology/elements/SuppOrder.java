package coursework.pcshop_ontology.elements;

import jade.content.AgentAction;


public class SuppOrder implements AgentAction  {

	motherboard mb;
	CPU cpu;
	RAM ram;
	HD hd;
	int quantity;
	int price;
	


	public motherboard getMb() {
		return mb;
	}

	public void setMb(motherboard mb) {
		this.mb = mb;
	}

	public CPU getCpu() {
		return cpu;
	}

	public void setCpu(CPU cpu) {
		this.cpu = cpu;
	}

	public RAM getRam() {
		return ram;
	}

	public void setRam(RAM ram) {
		this.ram = ram;
	}

	public HD getHd() {
		return hd;
	}

	public void setHd(HD hd) {
		this.hd = hd;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}
	
	
	
}
