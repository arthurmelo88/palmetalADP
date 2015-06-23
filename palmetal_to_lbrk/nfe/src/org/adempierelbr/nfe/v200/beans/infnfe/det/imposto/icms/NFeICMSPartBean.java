package org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ICMSPart")
public class NFeICMSPartBean implements INFeICMSBean {

	private String orig;
	private String CST;
	private String modBC;
	private String vBC;
	private String pRedBC;
	private String pICMS;
	private String vICMS;
	private String modBCST;
	private String pMVAST;
	private String pRedBCST;
	private String vBCST;
	private String pICMSST;
	private String vICMSST;
	private String pBCOp;
	private String UFST;

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

	public String getVBC() {
		return vBC;
	}

	public void setVBC(String vbc) {
		vBC = vbc;
	}

	public String getPRedBC() {
		return pRedBC;
	}

	public void setPRedBC(String redBC) {
		pRedBC = redBC;
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

	public String getpBCOp() {
		return pBCOp;
	}

	public void setpBCOp(String pBCOp) {
		this.pBCOp = pBCOp;
	}

	public String getUFST() {
		return UFST;
	}

	public void setUFST(String uFST) {
		UFST = uFST;
	}
}
