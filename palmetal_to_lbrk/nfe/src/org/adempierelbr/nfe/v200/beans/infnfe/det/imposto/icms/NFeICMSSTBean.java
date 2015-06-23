package org.adempierelbr.nfe.v200.beans.infnfe.det.imposto.icms;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("ICMSST")
public class NFeICMSSTBean implements INFeICMSBean {

	private String orig;
	private String CST;
	private String vBCSTRet;
	private String vICMSSTRet;
	private String vBCSTDest;
	private String vICMSSTDes;
	public String getOrig() {
		return orig;
	}
	public void setOrig(String orig) {
		this.orig = orig;
	}
	public String getCST() {
		return CST;
	}
	public void setCST(String cST) {
		CST = cST;
	}
	public String getvBCSTRet() {
		return vBCSTRet;
	}
	public void setvBCSTRet(String vBCSTRet) {
		this.vBCSTRet = vBCSTRet;
	}
	public String getvICMSSTRet() {
		return vICMSSTRet;
	}
	public void setvICMSSTRet(String vICMSSTRet) {
		this.vICMSSTRet = vICMSSTRet;
	}
	public String getvBCSTDest() {
		return vBCSTDest;
	}
	public void setvBCSTDest(String vBCSTDest) {
		this.vBCSTDest = vBCSTDest;
	}
	public String getvICMSSTDes() {
		return vICMSSTDes;
	}
	public void setvICMSSTDes(String vICMSSTDes) {
		this.vICMSSTDes = vICMSSTDes;
	}
	

}
