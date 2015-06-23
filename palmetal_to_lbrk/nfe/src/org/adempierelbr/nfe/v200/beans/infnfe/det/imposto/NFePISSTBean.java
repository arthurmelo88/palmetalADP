package org.adempierelbr.nfe.v200.beans.infnfe.det.imposto;

import org.adempierelbr.nfe.v200.beans.INFeBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("PISST")
public class NFePISSTBean implements INFeBean {
	private String vPIS;
	private String vBC;
	private String pPIS;
	private String qBCProd;
	private String vAliqProd;
	
	public String getVPIS() {
		return vPIS;
	}
	public void setVPIS(String vpis) {
		vPIS = vpis;
	}
	public String getVBC() {
		return vBC;
	}
	public void setVBC(String vbc) {
		vBC = vbc;
	}
	public String getPPIS() {
		return pPIS;
	}
	public void setPPIS(String ppis) {
		pPIS = ppis;
	}
	public String getQBCProd() {
		return qBCProd;
	}
	public void setQBCProd(String prod) {
		qBCProd = prod;
	}
	public String getVAliqProd() {
		return vAliqProd;
	}
	public void setVAliqProd(String aliqProd) {
		vAliqProd = aliqProd;
	}


}
