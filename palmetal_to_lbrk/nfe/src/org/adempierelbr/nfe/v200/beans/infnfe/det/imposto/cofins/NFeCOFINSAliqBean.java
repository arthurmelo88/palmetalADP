package org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.cofins;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("COFINSAliq")
public class NFeCOFINSAliqBean implements INFeCOFINSBean {
	
	private String CST;
	private String vBC;
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
