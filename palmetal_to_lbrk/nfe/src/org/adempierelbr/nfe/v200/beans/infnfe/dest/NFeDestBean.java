package org.adempierelbr.nfe.v200.beans.infnfe.dest;

import org.adempierelbr.nfe.v200.beans.INFeBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("dest")
public class NFeDestBean implements INFeBean {

	private String CNPJ;
	private String CPF;
	private String xNome;
	private NFeDestEndBean enderDest;
	private String IE;
	private String ISUF;
	private String email;

	public String getCNPJ() {
		return CNPJ;
	}

	public void setCNPJ(String cNPJ) {
		CNPJ = cNPJ;
	}

	public String getCPF() {
		return CPF;
	}

	public void setCPF(String cPF) {
		CPF = cPF;
	}

	public String getxNome() {
		return xNome;
	}

	public void setxNome(String xNome) {
		this.xNome = xNome;
	}

	public NFeDestEndBean getEnderDest() {
		return enderDest;
	}

	public void setEnderDest(NFeDestEndBean enderDest) {
		this.enderDest = enderDest;
	}

	public String getIE() {
		return IE;
	}

	public void setIE(String iE) {
		IE = iE;
	}

	public String getISUF() {
		return ISUF;
	}

	public void setISUF(String iSUF) {
		ISUF = iSUF;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
