package org.adempierelbr.nfe.v200.beans.nfeConsultaNF.retorno;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("retCancNFe")
public class RetCancNFeBean {

	private String versao;

	private InfCancBean infCanc;

	public String getVersao() {
		return versao;
	}

	public void setVersao(String versao) {
		this.versao = versao;
	}

	public InfCancBean getInfCanc() {
		return infCanc;
	}

	public void setInfCanc(InfCancBean infCanc) {
		this.infCanc = infCanc;
	}
}
