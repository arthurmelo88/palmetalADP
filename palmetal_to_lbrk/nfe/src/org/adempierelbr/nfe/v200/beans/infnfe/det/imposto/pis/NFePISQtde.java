package org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.pis;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("PISQtde")
public class NFePISQtde implements INFePISBean {

	private String CST;
	private String qBCProd;
	private String vAliqProd;
	private String vPIS;

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

	public String getVPIS() {
		return vPIS;
	}

	public void setVPIS(String vpis) {
		vPIS = vpis;
	}

}
