package org.adempierelbr.nfe.v200.beans.infnfe.ide;

import org.adempierelbr.nfe.v200.beans.INFeBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("refNF")
public class NFeInfoNFRef implements INFeBean {

	private String cUF;
	private String AAMM;
	private String CNPJ;
	private String mod;
	private String serie;
	private String nNF;

	public String getcUF() {
		return cUF;
	}

	public void setcUF(String cUF) {
		this.cUF = cUF;
	}

	public String getAAMM() {
		return AAMM;
	}

	public void setAAMM(String aAMM) {
		AAMM = aAMM;
	}

	public String getCNPJ() {
		return CNPJ;
	}

	public void setCNPJ(String cNPJ) {
		CNPJ = cNPJ;
	}

	public String getMod() {
		return mod;
	}

	public void setMod(String mod) {
		this.mod = mod;
	}

	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	public String getnNF() {
		return nNF;
	}

	public void setnNF(String nNF) {
		this.nNF = nNF;
	}

}
