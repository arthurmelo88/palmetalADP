package org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ICMS20")
public class NFeICMS20Bean implements INFeICMSBean {
	private String orig;
	private String CST;
	private String modBC;
	private String pRedBC;
	private String vBC;
	private String pICMS;
	private String vICMS;

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

	public String getModBC() {
		return modBC;
	}

	public void setModBC(String modBC) {
		this.modBC = modBC;
	}

	public String getPRedBC() {
		return pRedBC;
	}

	public void setPRedBC(String redBC) {
		pRedBC = redBC;
	}

	public String getVBC() {
		return vBC;
	}

	public void setVBC(String vbc) {
		vBC = vbc;
	}

	public String getPICMS() {
		return pICMS;
	}

	public void setPICMS(String picms) {
		pICMS = picms;
	}

	public String getVICMS() {
		return vICMS;
	}

	public void setVICMS(String vicms) {
		vICMS = vicms;
	}

}
