package org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.cofins;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("COFINSQtde")
public class NFeCOFINSQtdeBean implements INFeCOFINSBean {
	
	private String CST;
	private String qBCProd;
	private String vAliqProd;
	private String vCOFINS;

	public String getCST() {
		return CST;
	}

	public void setCST(String cst) {
		CST = cst;
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

	public String getVCOFINS() {
		return vCOFINS;
	}

	public void setVCOFINS(String vcofins) {
		vCOFINS = vcofins;
	}

}
