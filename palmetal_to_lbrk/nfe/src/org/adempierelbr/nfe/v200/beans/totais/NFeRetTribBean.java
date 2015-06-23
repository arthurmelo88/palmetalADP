package org.adempierelbr.nfe.v200.beans.totais;

import org.adempierelbr.nfe.v200.beans.INFeBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("retTrib")
public class NFeRetTribBean implements INFeBean {

	private String vRetPIS;
	private String vRetCOFINS;
	private String vRetCSLL;
	private String vBCIRRF;
	private String vIRRF;
	private String vBCRetPrev;
	private String vRetPrev;

	public String getvRetPIS() {
		return vRetPIS;
	}

	public void setvRetPIS(String vRetPIS) {
		this.vRetPIS = vRetPIS;
	}

	public String getvRetCOFINS() {
		return vRetCOFINS;
	}

	public void setvRetCOFINS(String vRetCOFINS) {
		this.vRetCOFINS = vRetCOFINS;
	}

	public String getvRetCSLL() {
		return vRetCSLL;
	}

	public void setvRetCSLL(String vRetCSLL) {
		this.vRetCSLL = vRetCSLL;
	}

	public String getvBCIRRF() {
		return vBCIRRF;
	}

	public void setvBCIRRF(String vBCIRRF) {
		this.vBCIRRF = vBCIRRF;
	}

	public String getvIRRF() {
		return vIRRF;
	}

	public void setvIRRF(String vIRRF) {
		this.vIRRF = vIRRF;
	}

	public String getvBCRetPrev() {
		return vBCRetPrev;
	}

	public void setvBCRetPrev(String vBCRetPrev) {
		this.vBCRetPrev = vBCRetPrev;
	}

	public String getvRetPrev() {
		return vRetPrev;
	}

	public void setvRetPrev(String vRetPrev) {
		this.vRetPrev = vRetPrev;
	}

}
