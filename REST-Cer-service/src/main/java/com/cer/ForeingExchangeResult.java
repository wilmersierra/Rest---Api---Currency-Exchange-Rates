package com.cer;

public class ForeingExchangeResult extends CommonResult {

	String currencySource;
	String currencyTarget;
	String rate;
	
	public String getCurrencySource() {
		return currencySource;
	}
	public void setCurrencySource(String currencySource) {
		this.currencySource = currencySource;
	}
	public String getCurrencyTarget() {
		return currencyTarget;
	}
	public void setCurrencyTarget(String currencyTarget) {
		this.currencyTarget = currencyTarget;
	}
	public String getRate() {
		return rate;
	}
	public void setRate(String rate) {
		this.rate = rate;
	}	
	
}
