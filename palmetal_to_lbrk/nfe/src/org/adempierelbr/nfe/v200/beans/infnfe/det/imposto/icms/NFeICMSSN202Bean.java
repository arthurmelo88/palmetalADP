package org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ICMSSN202")
public class NFeICMSSN202Bean implements INFeICMSBean {

	private String orig;
	private String CSOSN;
	private String modBCST;
	private String pMVAST;
	private String pRedBCST;
	private String vBCST;
	private String pICMSST;
	private String vICMSST;

	
	
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
	public String getModBCST() {
		return modBCST;
	}
	public void setModBCST(String modBCST) {
		this.modBCST = modBCST;
	}
	public String getpMVAST() {
		return pMVAST;
	}
	public void setpMVAST(String pMVAST) {
		this.pMVAST = pMVAST;
	}
	public String getpRedBCST() {
		return pRedBCST;
	}
	public void setpRedBCST(String pRedBCST) {
		this.pRedBCST = pRedBCST;
	}
	public String getvBCST() {
		return vBCST;
	}
	public void setvBCST(String vBCST) {
		this.vBCST = vBCST;
	}
	public String getpICMSST() {
		return pICMSST;
	}
	public void setpICMSST(String pICMSST) {
		this.pICMSST = pICMSST;
	}
	public String getvICMSST() {
		return vICMSST;
	}
	public void setvICMSST(String vICMSST) {
		this.vICMSST = vICMSST;
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
