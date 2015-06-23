package org.adempierelbr.nfe.v200.beans.nfeConsultaNF;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("consSitNFe")
public class ConsultaNFBean {

	@XStreamAsAttribute
	private String versao;
	
	@XStreamAsAttribute
	private String xmlns="http://www.portalfiscal.inf.br/nfe";
	
	private int tpAmb;
	private String xServ;
	private String chNFe;


	public ConsultaNFBean() {
	}

	public void setVersao(String versao) {
		this.versao = versao;
	}

	public String getVersao() {
		return versao;
	}
	
	public String getXmlns() {
		return xmlns;
	}

	public void setXmlns(String xmlns) {
		this.xmlns = xmlns;
	}

	public void setTpAmb(int tpAmb) {
		this.tpAmb = tpAmb;
	}

	public int getTpAmb() {
		return tpAmb;
	}

	public void setXServ(String xServ) {
		this.xServ = xServ;
	}

	public String getXServ() {
		return xServ;
	}

	public void setChNFe(String chNFe) {
		this.chNFe = chNFe;
	}

	public String getChNFe() {
		return chNFe;
	}

}
