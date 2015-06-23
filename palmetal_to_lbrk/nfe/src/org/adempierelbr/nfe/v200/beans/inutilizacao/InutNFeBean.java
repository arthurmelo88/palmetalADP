package org.adempierelbr.nfe.v200.beans.inutilizacao;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("inutNFe")
public class InutNFeBean {

	@XStreamAsAttribute
	private String versao;
	
	@XStreamAsAttribute
	private String xmlns="http://www.portalfiscal.inf.br/nfe";
	
	private InfInutBean infInut;

	public InutNFeBean() {
	}

	public void setVersao(String versao) {
		this.versao = versao;
	}

	public String getVersao() {
		return versao;
	}

	public void setInfInut(InfInutBean infInut) {
		this.infInut = infInut;
	}

	public InfInutBean getInfInut() {
		return infInut;
	}
	
	public String getXmlns() {
		return xmlns;
	}

	public void setXmlns(String xmlns) {
		this.xmlns = xmlns;
	}
}
