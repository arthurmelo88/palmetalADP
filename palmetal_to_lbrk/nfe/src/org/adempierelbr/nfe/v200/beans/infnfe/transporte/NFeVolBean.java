package org.adempierelbr.nfe.v200.beans.infnfe.transporte;

import org.adempierelbr.nfe.v200.beans.INFeBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("vol")
public class NFeVolBean implements INFeBean {

	private String qVol;
	private String esp;
	private String marca;
	private String nVol;
	private String pesoL;
	private String pesoB;

	public String getqVol() {
		return qVol;
	}

	public void setqVol(String qVol) {
		this.qVol = qVol;
	}

	public String getEsp() {
		return esp;
	}

	public void setEsp(String esp) {
		this.esp = esp;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public String getnVol() {
		return nVol;
	}

	public void setnVol(String nVol) {
		this.nVol = nVol;
	}

	public String getPesoL() {
		return pesoL;
	}

	public void setPesoL(String pesoL) {
		this.pesoL = pesoL;
	}

	public String getPesoB() {
		return pesoB;
	}

	public void setPesoB(String pesoB) {
		this.pesoB = pesoB;
	}

}
