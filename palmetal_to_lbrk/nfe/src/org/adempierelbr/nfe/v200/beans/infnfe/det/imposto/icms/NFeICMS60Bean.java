package org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ICMS60")
public class NFeICMS60Bean implements INFeICMSBean {

	private String orig;
	private String CST;
	private String vBCSTRet;
	private String vICMSSTRet;

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

	public String getVBCSTRet() {
		return vBCSTRet;
	}

	public void setVBCSTRet(String ret) {
		vBCSTRet = ret;
	}

	public String getVICMSSTRet() {
		return vICMSSTRet;
	}

	public void setVICMSSTRet(String ret) {
		vICMSSTRet = ret;
	}



}
