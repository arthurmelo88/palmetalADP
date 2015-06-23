package org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ICMSSN102")
public class NFeICMSSN102Bean implements INFeICMSBean {

	private String orig;
	private String CSOSN;
	
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
	
	@Override
	public String getCST() {
		 return getCSOSN();
	}
	@Override
	public void setCST(String CST) {
		setCSOSN(CST);
		
	}


	
}
