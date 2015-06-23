package org.adempierelbr.nfe.v200.beans.infnfe.cobranca;

import org.adempierelbr.nfe.v200.beans.INFeBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("dup")
public class NFeDupBean implements INFeBean {

	private String nDup;
	private String dVenc;
	private String vDup;

	public String getnDup() {
		return nDup;
	}

	public void setnDup(String nDup) {
		this.nDup = nDup;
	}

	public String getdVenc() {
		return dVenc;
	}

	public void setdVenc(String dVenc) {
		this.dVenc = dVenc;
	}

	public String getvDup() {
		return vDup;
	}

	public void setvDup(String vDup) {
		this.vDup = vDup;
	}

}
