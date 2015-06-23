package org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ICMS40")
public class NFeICMS40Bean implements INFeICMSBean {

	private String orig;
	private String CST;
	private String vICMS;
	private String motDesICMS;

	public String getOrig() {
		return orig;
	}

	public void setOrig(String orig) {
		this.orig = orig;
	}

	public String getCST() {
		return CST;
	}

	public void setCST(String cst) {
		CST = cst;
	}

	public String getVICMS() {
		return vICMS;
	}

	public void setVICMS(String vicms) {
		vICMS = vicms;
	}

	public String getMotDesICMS() {
		return motDesICMS;
	}

	public void setMotDesICMS(String motDesICMS) {
		this.motDesICMS = motDesICMS;
	}

	
}
