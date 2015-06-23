package org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ICMS30")
public class NFeICMS30Bean implements INFeICMSBean {
	private String orig;
	private String CST;
	private String modBCST;
	private String pMVAST;
	private String pRedBCST;
	private String vBCST;
	private String pICMSST;
	private String vICMSST;

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

	public String getModBCST() {
		return modBCST;
	}

	public void setModBCST(String modBCST) {
		this.modBCST = modBCST;
	}

	public String getPMVAST() {
		return pMVAST;
	}

	public void setPMVAST(String pmvast) {
		pMVAST = pmvast;
	}

	public String getPRedBCST() {
		return pRedBCST;
	}

	public void setPRedBCST(String redBCST) {
		pRedBCST = redBCST;
	}

	public String getVBCST() {
		return vBCST;
	}

	public void setVBCST(String vbcst) {
		vBCST = vbcst;
	}

	public String getPICMSST() {
		return pICMSST;
	}

	public void setPICMSST(String picmsst) {
		pICMSST = picmsst;
	}

	public String getVICMSST() {
		return vICMSST;
	}

	public void setVICMSST(String vicmsst) {
		vICMSST = vicmsst;
	}

}
