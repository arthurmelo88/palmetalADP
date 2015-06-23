package org.adempierelbr.nfe.v200.beans.nfestatusservico.retorno;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("retConsStatServ")
public class RetConsultaStatusServicoBean {

	private int tpAmb;
	private String verAplic;
	private int cStat;
	private String xMotivo;
	private int cUF;
	private String dhRecbto;
	private int tMed;
	private String dhRetorno;
	private String xObs;

	public RetConsultaStatusServicoBean() {
	}

	public void setTpAmb(int tpAmb) {
		this.tpAmb = tpAmb;
	}

	public int getTpAmb() {
		return tpAmb;
	}

	public void setVerAplic(String verAplic) {
		this.verAplic = verAplic;
	}

	public String getVerAplic() {
		return verAplic;
	}

	public void setCStat(int cStat) {
		this.cStat = cStat;
	}

	public int getCStat() {
		return cStat;
	}

	public void setXMotivo(String xMotivo) {
		this.xMotivo = xMotivo;
	}

	public String getXMotivo() {
		return xMotivo;
	}

	public void setCUF(int cUF) {
		this.cUF = cUF;
	}

	public int getCUF() {
		return cUF;
	}

	public void setDhRecbto(String dhRecbto) {
		this.dhRecbto = dhRecbto;
	}

	public String getDhRecbto() {
		return dhRecbto;
	}

	public void setTMed(int tMed) {
		this.tMed = tMed;
	}

	public int getTMed() {
		return tMed;
	}

	public void setDhRetorno(String dhRetorno) {
		this.dhRetorno = dhRetorno;
	}

	public String getDhRetorno() {
		return dhRetorno;
	}

	public void setXObs(String xObs) {
		this.xObs = xObs;
	}

	public String getXObs() {
		return xObs;
	}

	public String toString(){
		return getXMotivo()+ " - Tempo Médio de Resposta do Serviço (em minutos): " + getTMed();
	}
}
