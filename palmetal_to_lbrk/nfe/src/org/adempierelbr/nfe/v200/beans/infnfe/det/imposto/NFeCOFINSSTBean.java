package org.adempierelbr.nfe.v200.beans.infnfe.det.imposto;

import org.adempierelbr.nfe.v200.beans.INFeBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("COFINSST")
public class NFeCOFINSSTBean implements INFeBean {

	private String CST;
	private String vBC;
	private String qBCProd;
	private String vAliqProd;
	private String pCOFINS;
	private String vCOFINS;
	
	public String getCST() {
		return CST;
	}
	
	public void setCST(String cst) {
		CST = cst;
	}
	
	public String getVBC() {
		return vBC;
	}
	
	public void setVBC(String vbc) {
		vBC = vbc;
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
	
	public String getPCOFINS() {
		return pCOFINS;
	}
	
	public void setPCOFINS(String pcofins) {
		pCOFINS = pcofins;
	}
	
	public String getVCOFINS() {
		return vCOFINS;
	}
	
	public void setVCOFINS(String vcofins) {
		vCOFINS = vcofins;
	}
	
	
}
