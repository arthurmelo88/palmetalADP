package org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.cofins;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("COFINSNT")
public class NFeCOFINSNTBean implements INFeCOFINSBean {
	
	private String CST;

	public String getCST() {
		return CST;
	}

	public void setCST(String cst) {
		CST = cst;
	}

}
