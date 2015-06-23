package org.adempierelbr.nfe.v200.beans.prot;

import org.adempierelbr.nfe.v200.beans.INFeBean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Dados do Protocolo de Autorizacao
 * @author Fernando
 *
 */

@XStreamAlias("protNFe")
public class NFeProtBean implements INFeBean {
	
	@XStreamAsAttribute
	private String versao;
	
	private NFeInfProt infProt;

	public String getVersao() {
		return versao;
	}

	public void setVersao(String versao) {
		this.versao = versao;
	}

	public NFeInfProt getInfProt() {
		return infProt;
	}

	public void setInfProt(NFeInfProt infProt) {
		this.infProt = infProt;
	}




	
}
