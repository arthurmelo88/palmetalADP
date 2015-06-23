package org.adempierelbr.nfe.v200.beans.infnfe.transporte;

import org.adempierelbr.nfe.v200.beans.INFeBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("reboque")
public class NFeReboqueBean implements INFeBean {

	private String placa;
	private String UF;
	private String RNTC;
	private String vagao;
	private String balsa;

	public String getVagao() {
		return vagao;
	}

	public void setVagao(String vagao) {
		this.vagao = vagao;
	}

	public String getBalsa() {
		return balsa;
	}

	public void setBalsa(String balsa) {
		this.balsa = balsa;
	}

	public String getPlaca() {
		return placa;
	}

	public void setPlaca(String placa) {
		this.placa = placa;
	}

	public String getUF() {
		return UF;
	}

	public void setUF(String uF) {
		UF = uF;
	}

	public String getRNTC() {
		return RNTC;
	}

	public void setRNTC(String rNTC) {
		RNTC = rNTC;
	}

}
