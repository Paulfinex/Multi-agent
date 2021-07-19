package coursework.pcshop_ontology.elements;

import jade.content.AgentAction;

import jade.core.AID;

public class CustOrder implements AgentAction  {
	private AID customer;
	private PC pc;
	private int price;
	private int quantity;
	private int days;

	public AID getCustomer() {
		return customer;
	}

	public void setCustomer(AID customer) {
		this.customer = customer;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public PC getPc() {
		return pc;
	}

	public void setPc(PC pc) {
		this.pc = pc;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	@Override
	public String toString() {
		return "CustOrder [customer=" + customer + ", pc=" + pc + ", price=" + price + ", quantity=" + quantity
				+ ", days=" + days + "]";
	}

	

}
