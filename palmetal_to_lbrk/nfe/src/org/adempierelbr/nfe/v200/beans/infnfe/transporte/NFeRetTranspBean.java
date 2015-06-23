package org.adempierelbr.nfe.v200.beans.infnfe.transporte;

import org.adempierelbr.nfe.v200.beans.INFeBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("retTransp")
public class NFeRetTranspBean implements INFeBean {

	private String vServ;
	private String vBCRet;
	private String pICMSRet;
	private String vICMSRet;
	private String CFOP;
	private String cMunFG;

	public String getvServ() {
		return vServ;
	}

	public void setvServ(String vServ) {
		this.vServ = vServ;
	}

	public String getvBCRet() {
		return vBCRet;
	}

	public void setvBCRet(String vBCRet) {
		this.vBCRet = vBCRet;
	}

	public String getpICMSRet() {
		return pICMSRet;
	}

	public void setpICMSRet(String pICMSRet) {
		this.pICMSRet = pICMSRet;
	}

	public String getvICMSRet() {
		return vICMSRet;
	}

	public void setvICMSRet(String vICMSRet) {
		this.vICMSRet = vICMSRet;
	}

	public String getCFOP() {
		return CFOP;
	}

	public void setCFOP(String cFOP) {
		CFOP = cFOP;
	}

	public String getcMunFG() {
		return cMunFG;
	}

	public void setcMunFG(String cMunFG) {
		this.cMunFG = cMunFG;
	}

}
