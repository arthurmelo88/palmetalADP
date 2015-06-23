package org.adempierelbr.nfe.v200.beans.nfestatusservico;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("consStatServ")
public class ConsultaStatusServicoBean {

	@XStreamAsAttribute
	private String versao;
	
	@XStreamAsAttribute
	private String xmlns="http://www.portalfiscal.inf.br/nfe";
	
	private int tpAmb;
	private int cUF;
	private String xServ;

	public ConsultaStatusServicoBean() {
	}

	public void setTpAmb(int tpAmb) {
		this.tpAmb = tpAmb;
	}

	public int getTpAmb() {
		return tpAmb;
	}

	public void setCUF(int cUF) {
		this.cUF = cUF;
	}

	public int getCUF() {
		return cUF;
	}

	public String getVersao() {
		return versao;
	}

	public void setVersao(String versao) {
		this.versao = versao;
	}

	public String getXmlns() {
		return xmlns;
	}

	public void setXmlns(String xmlns) {
		this.xmlns = xmlns;
	}

	public String getXServ() {
		return xServ;
	}

	public void setXServ(String serv) {
		xServ = serv;
	}

}
