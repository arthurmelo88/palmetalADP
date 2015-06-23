package org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ICMSSN101")
public class NFeICMSSN101Bean implements INFeICMSBean {

	private String orig;
	private String CSOSN;
	private String pCredSN;
	private String vCredICMSSN;
	public String getOrig() {
		return orig;
	}
	public void setOrig(String orig) {
		this.orig = orig;
	}
	public String getCSOSN() {
		return CSOSN;
	}
	public void setCSOSN(String cSOSN) {
		CSOSN = cSOSN;
	}
	public String getpCredSN() {
		return pCredSN;
	}
	public void setpCredSN(String pCredSN) {
		this.pCredSN = pCredSN;
	}
	public String getvCredICMSSN() {
		return vCredICMSSN;
	}
	public void setvCredICMSSN(String vCredICMSSN) {
		this.vCredICMSSN = vCredICMSSN;
	}
	
	@Override
	public String getCST() {
		 return getCSOSN();
	}
	@Override
	public void setCST(String CST) {
		setCSOSN(CST);
		
	}


	
}
