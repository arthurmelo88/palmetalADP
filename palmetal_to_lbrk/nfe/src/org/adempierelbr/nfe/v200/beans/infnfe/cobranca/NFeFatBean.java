package org.adempierelbr.nfe.v200.beans.infnfe.cobranca;

import org.adempierelbr.nfe.v200.beans.INFeBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("fat")
public class NFeFatBean implements INFeBean {

	private String nFat;
	private String vOrig;
	private String vDesc;
	private String vLiq;

	public String getnFat() {
		return nFat;
	}

	public void setnFat(String nFat) {
		this.nFat = nFat;
	}

	public String getvOrig() {
		return vOrig;
	}

	public void setvOrig(String vOrig) {
		this.vOrig = vOrig;
	}

	public String getvDesc() {
		return vDesc;
	}

	public void setvDesc(String vDesc) {
		this.vDesc = vDesc;
	}

	public String getvLiq() {
		return vLiq;
	}

	public void setvLiq(String vLiq) {
		this.vLiq = vLiq;
	}

}
