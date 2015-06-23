package org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.ipi;

import org.adempierelbr.nfe.v200.beans.INFeBean;

public class NFeIPITribBean implements INFeBean {
	
	private String CST;
	private String vBC;
	private String pIPI;
	private String qUnid;
	private String vUnid;
	private String vIPI;

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

	public String getPIPI() {
		return pIPI;
	}

	public void setPIPI(String pipi) {
		pIPI = pipi;
	}

	public String getQUnid() {
		return qUnid;
	}

	public void setQUnid(String unid) {
		qUnid = unid;
	}

	public String getVUnid() {
		return vUnid;
	}

	public void setVUnid(String unid) {
		vUnid = unid;
	}

	public String getVIPI() {
		return vIPI;
	}

	public void setVIPI(String vipi) {
		vIPI = vipi;
	}

}
