package org.adempierelbr.nfe.v200.beans.infnfe.emit;

import org.adempierelbr.nfe.v200.beans.INFeBean;

public class NFeEmitBean implements INFeBean {

	private String CNPJ;
	private String CPF;
	private String xNome;
	private String xFant;
	
	private NFeEndEmitBean enderEmit;

	private String IE;
	private String IEST;
	private String IM;
	private String CNAE;
	private String CRT;
	
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
	public String getxFant() {
		return xFant;
	}
	public void setxFant(String xFant) {
		this.xFant = xFant;
	}
	public NFeEndEmitBean getEnderEmit() {
		return enderEmit;
	}
	public void setEnderEmit(NFeEndEmitBean enderEmit) {
		this.enderEmit = enderEmit;
	}
	public String getIE() {
		return IE;
	}
	public void setIE(String iE) {
		IE = iE;
	}
	public String getIEST() {
		return IEST;
	}
	public void setIEST(String iEST) {
		IEST = iEST;
	}
	public String getIM() {
		return IM;
	}
	public void setIM(String iM) {
		IM = iM;
	}
	public String getCNAE() {
		return CNAE;
	}
	public void setCNAE(String cNAE) {
		CNAE = cNAE;
	}
	public String getCRT() {
		return CRT;
	}
	public void setCRT(String cRT) {
		CRT = cRT;
	}
	
	
	
}
