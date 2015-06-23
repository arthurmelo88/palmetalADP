package org.adempierelbr.nfe.v200.beans.nfeConsultaNF.retorno;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("protNFe")
public class ProtNFeBean {

	@XStreamAsAttribute
	private String versao;
	
	private InfProtBean infProt;

	public InfProtBean getInfProt() {
		return infProt;
	}

	public void setInfProt(InfProtBean infProt) {
		this.infProt = infProt;
	}

	public String getVersao() {
		return versao;
	}

	public void setVersao(String versao) {
		this.versao = versao;
	}

}
