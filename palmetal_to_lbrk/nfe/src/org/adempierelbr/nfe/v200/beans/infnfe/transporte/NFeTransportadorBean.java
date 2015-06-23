package org.adempierelbr.nfe.v200.beans.infnfe.transporte;

import org.adempierelbr.nfe.v200.beans.INFeBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("transporta")
public class NFeTransportadorBean  implements INFeBean {

	private String CNPJ;
	private String CPF;
	private String xNome;
	private String IE;
	private String xEnder;
	private String xMun;
	private String UF;

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

	public String getIE() {
		return IE;
	}

	public void setIE(String iE) {
		IE = iE;
	}

	public String getxEnder() {
		return xEnder;
	}

	public void setxEnder(String xEnder) {
		this.xEnder = xEnder;
	}

	public String getxMun() {
		return xMun;
	}

	public void setxMun(String xMun) {
		this.xMun = xMun;
	}

	public String getUF() {
		return UF;
	}

	public void setUF(String uF) {
		UF = uF;
	}

}
