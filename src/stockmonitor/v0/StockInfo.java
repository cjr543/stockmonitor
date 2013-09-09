package stockmonitor.v0;

import java.util.Date;

public class StockInfo {
	String code;
	String name;
	Double curPrice = 0.00;
	Date time;
	
	public StockInfo(String code, String name, Double curPrice, Date time){
		this.code = code;
		this.name = name;
		this.curPrice = curPrice;
		this.time = time;
	}
	
	public String getCode() { return code; }
	public String getName() { return name; }
	public Double getCurPrice() { return curPrice; }
	public Date getTime() { return time; }
	
	public String toString() {
		return code + ", " + name + ", " + curPrice.toString() + ", " + time.toString();
	}
}
