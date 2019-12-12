package se.bubbelbubbel.fakenews.model.star;

import java.util.List;

public class PrinterPostRequest {
	private String status = "";
	private String printerMAC ="";
	private String uniqueID = "";
	private String statusCode = "";
	private boolean printingInProgress;
	private ClientAction clientAction = null;
	private List<BarcodeReader> barcodeReaders = null;
	private List<Keyboard> keyboards = null;
	private List<Display> displays = null;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPrinterMAC() {
		return printerMAC;
	}
	public void setPrinterMAC(String printerMAC) {
		this.printerMAC = printerMAC;
	}
	public String getUniqueID() {
		return uniqueID;
	}
	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public boolean isPrintingInProgress() {
		return printingInProgress;
	}
	public void setPrintingInProgress(boolean printingInProgress) {
		this.printingInProgress = printingInProgress;
	}
	public ClientAction getClientAction() {
		return clientAction;
	}
	public void setClientAction(ClientAction clientAction) {
		this.clientAction = clientAction;
	}
	public List<BarcodeReader> getBarcodeReaders() {
		return barcodeReaders;
	}
	public void setBarcodeReaders(List<BarcodeReader> barcodeReaders) {
		this.barcodeReaders = barcodeReaders;
	}
	public List<Keyboard> getKeyboards() {
		return keyboards;
	}
	public void setKeyboards(List<Keyboard> keyboards) {
		this.keyboards = keyboards;
	}
	public List<Display> getDisplays() {
		return displays;
	}
	public void setDisplays(List<Display> displays) {
		this.displays = displays;
	}

}
