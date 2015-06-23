package org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ICMSSN500")
public class NFeICMSSN500Bean implements INFeICMSBean {

	private String orig;
	private String CSOSN;
	private String vBCSTRet;
	private String vICMSSTRet;
	
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
	
	public String getvBCSTRet() {
		return vBCSTRet;
	}
	public void setvBCSTRet(String vBCSTRet) {
		this.vBCSTRet = vBCSTRet;
	}
	public String getvICMSSTRet() {
		return vICMSSTRet;
	}
	public void setvICMSSTRet(String vICMSSTRet) {
		this.vICMSSTRet = vICMSSTRet;
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
