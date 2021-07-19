package coursework;

import coursework.pcshop_ontology.elements.*;

public class Warehouse {

	int CPUMintel = 0;
	int CPUIMD = 0;
	int MBMintel = 0;
	int MBIMD = 0;
	int RAM4 = 0;
	int RAM16 = 0;
	int HD1 = 0;
	int HD2 = 0;

	// Calculate the daily cost of storage
	public int Penality() {
		return (CPUMintel + CPUIMD + MBMintel + MBIMD + RAM4 + RAM16 + HD1 + HD2);

	}

	// Checks if an order is possible with the stored parts
	public boolean canManufacture(CustOrder co) {

		boolean cpu = false;
		boolean mb = false;
		boolean hd = false;
		boolean ram = false;

		switch (co.getPc().getCPU().getModel()) {
		case "Mintel":
			if (co.getQuantity() <= CPUMintel) {
				cpu = true;
			}
			break;
		case "IMD":
			if (co.getQuantity() <= CPUIMD) {
				cpu = true;
			}
			break;
		}

		switch (co.getPc().getMB().getModel()) {
		case "Mintel":
			if (co.getQuantity() <= MBMintel) {
				mb = true;
			}
			break;
		case "IMD":
			if (co.getQuantity() <= MBIMD) {
				mb = true;
			}
			break;
		}

		switch (co.getPc().getHD().getSize()) {
		case 1:
			if (co.getQuantity() <= HD1) {
				hd = true;
			}
			break;
		case 2:
			if (co.getQuantity() <= HD2) {
				hd = true;
			}
			break;
		}

		switch (co.getPc().getRAM().getSize()) {
		case 4:
			if (co.getQuantity() <= RAM4) {
				ram = true;
			}
			break;
		case 16:
			if (co.getQuantity() <= RAM16) {
				ram = true;
			}
			break;
		}

		if (cpu && mb && hd && ram) {
			return true;
		}
		return false;
	}

	// Getters and setters

	public int getCPUMintel() {
		return CPUMintel;
	}

	public void setCPUMintel(int cPUMintel) {
		CPUMintel = cPUMintel;
	}

	public int getCPUIMD() {
		return CPUIMD;
	}

	public void setCPUIMD(int cPUIMD) {
		CPUIMD = cPUIMD;
	}

	public int getMBMintel() {
		return MBMintel;
	}

	public void setMBMintel(int mBMintel) {
		MBMintel = mBMintel;
	}

	public int getMBIMD() {
		return MBIMD;
	}

	public void setMBIMD(int mBIMD) {
		MBIMD = mBIMD;
	}

	public int getRAM4() {
		return RAM4;
	}

	public void setRAM4(int rAM4) {
		RAM4 = rAM4;
	}

	public int getRAM16() {
		return RAM16;
	}

	public void setRAM16(int rAM16) {
		RAM16 = rAM16;
	}

	public int getHD1() {
		return HD1;
	}

	public void setHD1(int hD1) {
		HD1 = hD1;
	}

	public int getHD2() {
		return HD2;
	}

	public void setHD2(int hD2) {
		HD2 = hD2;
	}

	public void addRam(RAM ram, int quantity) {
		switch (ram.getSize()) {
		case 4:
			RAM4 += quantity;
			break;
		case 16:
			RAM16 += quantity;
			break;
		}
	}

	public void addCpu(CPU cpu, int quantity) {
		switch (cpu.getModel()) {
		case "Mintel":
			CPUMintel += quantity;
			break;
		case "IMD":
			CPUIMD += quantity;
			break;
		}
	}

	public void addHd(HD hd, int quantity) {
		switch (hd.getSize()) {
		case 1:
			HD1 += quantity;
			break;
		case 2:
			HD2 += quantity;
			break;
		}

	}

	public void addMb(motherboard mb, int quantity) {
		switch (mb.getModel()) {
		case "Mintel":
			MBMintel += quantity;
			break;
		case "IMD":
			MBIMD += quantity;
			break;
		}
	}

	@Override
	public String toString() {
		return "Warehouse [CPUMintel=" + CPUMintel + ", CPUIMD=" + CPUIMD + ", MBMintel=" + MBMintel + ", MBIMD="
				+ MBIMD + ", RAM4=" + RAM4 + ", RAM16=" + RAM16 + ", HD1=" + HD1 + ", HD2=" + HD2 + "]";
	}

}
