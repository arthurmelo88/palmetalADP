package org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.pis;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("PISNT")
public class NFePISNT implements INFePISBean {
	
	private String CST;

	public String getCST() {
		return CST;
	}

	public void setCST(String cst) {
		CST = cst;
	}

}
